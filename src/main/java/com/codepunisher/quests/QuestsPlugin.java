package com.codepunisher.quests;

import com.codepunisher.quests.adapters.QuestAdapter;
import com.codepunisher.quests.cache.QuestCache;
import com.codepunisher.quests.cache.QuestPlayerCache;
import com.codepunisher.quests.cache.QuestSubCommandCache;
import com.codepunisher.quests.commands.QuestSubCommandRegistrar;
import com.codepunisher.quests.commands.QuestsCommand;
import com.codepunisher.quests.commands.lib.CommandRegistrar;
import com.codepunisher.quests.config.QuestsConfig;
import com.codepunisher.quests.database.QuestDatabase;
import com.codepunisher.quests.database.impl.QuestDatabaseImpl;
import com.codepunisher.quests.listeners.PlayerJoinLeaveListener;
import com.codepunisher.quests.listeners.QuestTrackingListener;
import com.codepunisher.quests.models.Quest;
import com.codepunisher.quests.redis.RedisActiveQuests;
import com.codepunisher.quests.redis.RedisPlayerData;
import com.codepunisher.quests.redis.impl.RedisActiveQuestsImpl;
import com.codepunisher.quests.redis.impl.RedisPlayerDataImpl;
import com.codepunisher.quests.tasks.RedisPlayerDataSaveTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zaxxer.hikari.HikariDataSource;
import fr.mrmicky.fastinv.FastInvManager;
import me.drepic.proton.common.ProtonManager;
import me.drepic.proton.common.ProtonProvider;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.JedisPool;

// TODO: placeholderapi/api
// TODO: double check requirements
// TODO: reward for completing all?
// TODO: signshit
// TODO: configurations (async)
// TODO: clean code where can (clean main class?)
// TODO: tests
// TODO: make look pretty
// TODO: remove unnecessary shades/dependencies
// TODO: put on github (pretty read me) and a jar file
public class QuestsPlugin extends JavaPlugin {
  private HikariDataSource hikariDataSource;
  private QuestPlayerCache playerCache;
  private RedisPlayerData redisPlayerData;

  @Override
  public void onEnable() {
    // ----- ( CONFIGURATION ) -----
    QuestsConfig questsConfig = new QuestsConfig();
    questsConfig.reload(this);

    // ----- ( CACHES ) -----
    QuestCache questCache = new QuestCache();
    playerCache = new QuestPlayerCache();

    // ----- ( GSON TYPE ADAPTER ) -----
    Gson gson = new GsonBuilder().registerTypeAdapter(Quest.class, new QuestAdapter()).create();

    // ----- ( DATABASE ) -----
    hikariDataSource = new HikariDataSource(questsConfig.getHikariConfig());
    QuestDatabase questDatabase = new QuestDatabaseImpl(this, hikariDataSource);
    questDatabase.createQuestTable();
    questDatabase
        .getAllQuests()
        .thenAccept(
            quests -> {
              quests.forEach(questCache::add);
            });

    // ----- ( REDIS ) -----
    JedisPool jedisPool = new JedisPool("localhost", 6379);
    RedisActiveQuests redisActiveQuests = new RedisActiveQuestsImpl(this, questCache, jedisPool);
    redisPlayerData = new RedisPlayerDataImpl(this, playerCache, jedisPool, gson);
    Bukkit.getOnlinePlayers().forEach(redisPlayerData::loadRedisDataIntoLocalCache);
    getServer()
        .getScheduler()
        .runTaskTimerAsynchronously(
            this, new RedisPlayerDataSaveTask(playerCache, redisPlayerData), 1200L, 1200L);
    redisActiveQuests
        .getDailyQuests()
        .thenAccept(
            quests -> {
              quests.forEach(questCache::addActiveQuest);
            });
    ProtonManager proton = ProtonProvider.get();

    // ----- ( SUB COMMANDS ) -----
    QuestSubCommandCache subCommandCache = new QuestSubCommandCache();
    QuestSubCommandRegistrar subCommandRegistrar =
        new QuestSubCommandRegistrar(
            subCommandCache,
            questCache,
            playerCache,
            questDatabase,
            redisActiveQuests,
            redisPlayerData,
            proton,
            gson);
    subCommandRegistrar.register();

    // ----- ( MAIN QUEST COMMAND ) -----
    QuestsCommand questsCommand = new QuestsCommand(questsConfig, subCommandCache);
    CommandRegistrar commandRegistrar = new CommandRegistrar(this, questsConfig);
    questsConfig
        .getLanguageCommandMap()
        .forEach(
            (key, langCmd) -> {
              commandRegistrar.registerCommands(
                  questsCommand, langCmd.getPrimaryCommands().toArray(new String[0]));
            });

    // ----- ( LISTENER ) -----
    PluginManager pluginManager = getServer().getPluginManager();
    pluginManager.registerEvents(new PlayerJoinLeaveListener(questCache, playerCache, redisPlayerData), this);
    pluginManager.registerEvents(new QuestTrackingListener(playerCache, questCache), this);

    // ----- ( FASTINV REGISTRY ) -----
    FastInvManager.register(this);
  }

  @Override
  public void onDisable() {
    hikariDataSource.close();

    // This allows the plugin to be plugman reloadable
    playerCache.getEntrySet().forEach(entry -> {
      redisPlayerData.updateRedisFromLocalCache(entry.getKey(), entry.getValue());
    });
  }
}
