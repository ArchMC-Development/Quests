package com.codepunisher.quests;

import com.codepunisher.quests.adapters.QuestAdapter;
import com.codepunisher.quests.api.QuestsAPI;
import com.codepunisher.quests.api.QuestsApiImpl;
import com.codepunisher.quests.cache.QuestCache;
import com.codepunisher.quests.cache.QuestPlayerCache;
import com.codepunisher.quests.cache.QuestSignCache;
import com.codepunisher.quests.cache.QuestSubCommandCache;
import com.codepunisher.quests.commands.QuestSubCommandRegistrar;
import com.codepunisher.quests.commands.QuestsCommand;
import com.codepunisher.quests.commands.lib.CommandRegistrar;
import com.codepunisher.quests.config.QuestsConfig;
import com.codepunisher.quests.database.QuestDatabase;
import com.codepunisher.quests.database.QuestPlayerStorageDatabase;
import com.codepunisher.quests.database.QuestSignDatabase;
import com.codepunisher.quests.database.impl.QuestDatabaseImpl;
import com.codepunisher.quests.database.impl.QuestPlayerStorageDataImpl;
import com.codepunisher.quests.database.impl.QuestSignDatabaseImpl;
import com.codepunisher.quests.expansions.QuestsExpansion;
import com.codepunisher.quests.listeners.PlayerJoinLeaveListener;
import com.codepunisher.quests.listeners.QuestTrackingListener;
import com.codepunisher.quests.listeners.SignChangeListener;
import com.codepunisher.quests.models.Quest;
import com.codepunisher.quests.redis.RedisActiveQuests;
import com.codepunisher.quests.redis.RedisPlayerData;
import com.codepunisher.quests.redis.impl.RedisActiveQuestsImpl;
import com.codepunisher.quests.redis.impl.RedisPlayerDataImpl;
import com.codepunisher.quests.tasks.RedisPlayerDataSaveTask;
import com.codepunisher.quests.tasks.SignDeleteTaskTimer;
import com.codepunisher.quests.tasks.SignUpdateTaskTimer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zaxxer.hikari.HikariDataSource;
import fr.mrmicky.fastinv.FastInvManager;
import gg.scala.aware.Aware;
import gg.scala.aware.AwareBuilder;
import gg.scala.aware.codec.codecs.interpretation.AwareMessageCodec;
import gg.scala.aware.message.AwareMessage;
import kotlin.jvm.JvmClassMappingKt;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.JedisPool;

public class QuestsPlugin extends JavaPlugin {
    @Getter private static QuestsAPI questsAPI;
    private HikariDataSource hikariDataSource;
    private QuestPlayerCache playerCache;
    private RedisPlayerData redisPlayerData;

    @Override
    public void onEnable() {
        //
        // ----- ( CACHES ) -----
        //
        QuestCache questCache = new QuestCache();
        QuestSignCache signCache = new QuestSignCache();
        playerCache = new QuestPlayerCache();
        getLogger().info("Quests caches loaded...");

        //
        // ----- ( API ) -----
        //
        questsAPI = new QuestsApiImpl(playerCache);

        //
        // ----- ( CONFIGURATION ) -----
        //
        QuestsConfig questsConfig = new QuestsConfig(playerCache);
        questsConfig.reload(this);
        getLogger().info("Quests config loaded...");

        //
        // ----- ( GSON TYPE ADAPTER ) -----
        //
        Gson gson = new GsonBuilder().registerTypeAdapter(Quest.class, new QuestAdapter()).create();
        getLogger().info("Quests gson loaded...");

        //
        // ----- ( DATABASE ) -----
        //
        hikariDataSource = new HikariDataSource(questsConfig.getHikariConfig());
        QuestDatabase questDatabase = new QuestDatabaseImpl(this, hikariDataSource);
        questDatabase.createQuestTable();
        questDatabase
                .getAllQuests()
                .thenAccept(
                        quests -> {
                            quests.forEach(questCache::add);
                        });

        QuestSignDatabase signDatabase =
                new QuestSignDatabaseImpl(this, questsConfig, hikariDataSource);
        signDatabase.createSignTable();
        signDatabase
                .getSignLocations()
                .thenAccept(
                        locations -> {
                            locations.forEach(signCache::add);
                        });

        QuestPlayerStorageDatabase storageDatabase =
                new QuestPlayerStorageDataImpl(this, hikariDataSource);
        storageDatabase.createTable();

        getLogger().info("Quests databases loaded...");

        //
        // ----- ( REDIS ) -----
        //
        JedisPool jedisPool = questsConfig.getJedisPool();
        RedisActiveQuests redisActiveQuests = new RedisActiveQuestsImpl(this, questCache, jedisPool);
        redisPlayerData = new RedisPlayerDataImpl(this, playerCache, jedisPool, gson);
        Bukkit.getOnlinePlayers().forEach(redisPlayerData::loadRedisDataIntoLocalCache);
        redisActiveQuests
                .getDailyQuests()
                .thenAccept(
                        quests -> {
                            quests.forEach(questCache::addActiveQuest);
                        });
        Aware<AwareMessage> aware = AwareBuilder.of("rootkit:polls:messages", JvmClassMappingKt.getKotlinClass(AwareMessage.class))
                .logger(getLogger())
                .codec(AwareMessageCodec.INSTANCE)
                .build();
        getLogger().info("Quests redis loaded...");

        //
        // ----- ( TASK TIMERS ) -----
        //
        getServer()
                .getScheduler()
                .runTaskTimerAsynchronously(
                        this, new RedisPlayerDataSaveTask(playerCache, redisPlayerData), 1200L, 1200L);

        getServer()
                .getScheduler()
                .runTaskTimer(this, new SignDeleteTaskTimer(signDatabase, signCache), 10L, 10L);

        getServer()
                .getScheduler()
                .runTaskTimer(
                        this,
                        new SignUpdateTaskTimer(questsConfig, questCache, playerCache, signCache),
                        20L,
                        20L);

        getLogger().info("Quests task timers loaded...");

        //
        // ----- ( SUB COMMANDS ) -----
        //
        QuestSubCommandCache subCommandCache = new QuestSubCommandCache();
        QuestSubCommandRegistrar subCommandRegistrar =
                new QuestSubCommandRegistrar(
                        this,
                        questsConfig,
                        subCommandCache,
                        questCache,
                        playerCache,
                        questDatabase,
                        storageDatabase,
                        redisActiveQuests,
                        redisPlayerData,
                        aware,
                        gson);
        subCommandRegistrar.register();
        getLogger().info("Quests sub commands loaded...");

        //
        // ----- ( MAIN QUEST COMMAND ) -----
        //
        QuestsCommand questsCommand = new QuestsCommand(questsConfig, subCommandCache);
        CommandRegistrar commandRegistrar = new CommandRegistrar(this, questsConfig);
        questsConfig
                .getLanguageCommandMap()
                .forEach(
                        (key, langCmd) -> {
                            commandRegistrar.registerCommands(
                                    questsCommand, langCmd.getLangCmd().getPrimaryCommands().toArray(new String[0]));
                        });

        getLogger().info("Quests primary command loaded...");

        //
        // ----- ( LISTENER ) -----
        //
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(
                new PlayerJoinLeaveListener(
                        this, questsConfig, questCache, playerCache, redisPlayerData, storageDatabase),
                this);
        pluginManager.registerEvents(
                new QuestTrackingListener(this, questsConfig, playerCache, questCache, storageDatabase),
                this);
        pluginManager.registerEvents(
                new SignChangeListener(questsConfig, signDatabase, signCache), this);
        getLogger().info("Quests listeners loaded...");

        //
        // ----- ( FASTINV REGISTRY ) -----
        //
        FastInvManager.register(this);
        getLogger().info("Quests fastinv loaded...");

        //
        // ----- ( PlaceholderAPI ) -----
        //
        if (pluginManager.isPluginEnabled("PlaceholderAPI")) {
            new QuestsExpansion(questCache, playerCache).register();
            getLogger().info("PlaceholderAPI expansion successfully registered!");
        }
    }

    @Override
    public void onDisable() {
        hikariDataSource.close();
        getLogger().info("Quests database closed...");

        // This allows the plugin to be plugman reloadable
        playerCache.getActiveQuestEntrySet().forEach(entry -> {
            redisPlayerData.updateRedisFromLocalCache(entry.getKey(), entry.getValue());
        });

        getLogger().info("Quests player cached dumped to redis...");
    }
}
