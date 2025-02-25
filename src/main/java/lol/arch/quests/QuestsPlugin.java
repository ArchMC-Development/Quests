package lol.arch.quests;

import fr.mrmicky.fastinv.FastInvManager;
import gg.scala.aware.Aware;
import gg.scala.aware.AwareBuilder;
import gg.scala.aware.codec.codecs.interpretation.AwareMessageCodec;
import gg.scala.aware.message.AwareMessage;
import kotlin.jvm.JvmClassMappingKt;
import lol.arch.quests.adapters.QuestAdapter;
import lol.arch.quests.api.QuestsAPI;
import lol.arch.quests.api.QuestsApiImpl;
import lol.arch.quests.cache.QuestSubCommandCache;
import lol.arch.quests.commands.QuestSubCommandRegistrar;
import lol.arch.quests.commands.QuestsCommand;
import lol.arch.quests.commands.lib.CommandRegistrar;
import lol.arch.quests.config.QuestsConfig;
import lol.arch.quests.data.QuestDataService;
import lol.arch.quests.expansions.QuestsExpansion;
import lol.arch.quests.feature.CloudSyncFeature;
import lol.arch.quests.listeners.QuestTrackingListener;
import lol.arch.quests.models.Quest;
import lol.arch.quests.profile.QuestPlayerDataService;
import lol.arch.quests.profile.ShortTermDataService;
import lol.arch.quests.sync.ActiveQuestsDataSync;
import lombok.Getter;
import net.evilblock.cubed.serializers.Serializers;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class QuestsPlugin extends JavaPlugin {
    @Getter private static QuestsAPI questsAPI;

    @Override
    public void onLoad() {
        Serializers.INSTANCE.builder().registerTypeAdapter(Quest.class, new QuestAdapter());
    }

    @Override
    public void onEnable() {
        QuestDataService.INSTANCE.configure();
        QuestPlayerDataService.INSTANCE.configure();
        ShortTermDataService.INSTANCE.configure();
        ActiveQuestsDataSync.INSTANCE.load();
        CloudSyncFeature.INSTANCE.configure();

        //
        // ----- ( API ) -----
        //
        questsAPI = new QuestsApiImpl();

        //
        // ----- ( CONFIGURATION ) -----
        //
        QuestsConfig questsConfig = new QuestsConfig();
        questsConfig.reload(this);

        //
        // ----- ( REDIS ) -----
        //
        Aware<AwareMessage> aware = AwareBuilder.of("quests:updates", JvmClassMappingKt.getKotlinClass(AwareMessage.class))
                .logger(getLogger())
                .codec(AwareMessageCodec.INSTANCE)
                .build();

        //
        // ----- ( SUB COMMANDS ) -----
        //
        QuestSubCommandCache subCommandCache = new QuestSubCommandCache();
        QuestSubCommandRegistrar subCommandRegistrar =
                new QuestSubCommandRegistrar(
                        this,
                        questsConfig,
                        subCommandCache,
                        aware);
        subCommandRegistrar.register();

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


        //
        // ----- ( LISTENER ) -----
        //
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(
                new QuestTrackingListener(this, questsConfig),
                this);

        //
        // ----- ( FASTINV REGISTRY ) -----
        //
        FastInvManager.register(this);

        //
        // ----- ( PlaceholderAPI ) -----
        //
        if (pluginManager.isPluginEnabled("PlaceholderAPI")) {
            new QuestsExpansion().register();
        }
    }
}
