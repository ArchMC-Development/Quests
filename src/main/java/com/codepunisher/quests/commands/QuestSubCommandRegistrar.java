package com.codepunisher.quests.commands;

import com.codepunisher.quests.cache.QuestCache;
import com.codepunisher.quests.cache.QuestPlayerCache;
import com.codepunisher.quests.cache.QuestSubCommandCache;
import com.codepunisher.quests.commands.subcommands.*;
import com.codepunisher.quests.config.QuestsConfig;
import com.codepunisher.quests.database.QuestDatabase;
import com.codepunisher.quests.database.QuestPlayerStorageDatabase;
import com.codepunisher.quests.models.CmdType;
import com.codepunisher.quests.redis.RedisActiveQuests;
import com.codepunisher.quests.redis.RedisPlayerData;
import com.google.gson.Gson;
import gg.scala.aware.Aware;
import gg.scala.aware.message.AwareMessage;
import lombok.AllArgsConstructor;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.annotation.Annotation;

@AllArgsConstructor
public class QuestSubCommandRegistrar {
    private final JavaPlugin plugin;
    private final QuestsConfig questsConfig;
    private final QuestSubCommandCache questSubCommandCache;
    private final QuestCache questCache;
    private final QuestPlayerCache playerCache;
    private final QuestDatabase questDatabase;
    private final QuestPlayerStorageDatabase storageDatabase;
    private final RedisActiveQuests redisActiveQuests;
    private final RedisPlayerData redisPlayerData;
    private final Aware<AwareMessage> aware;
    private final Gson gson;

    public void register() {
        QuestResetSubCommand questsResetSubCommand =
                new QuestResetSubCommand(
                        plugin,
                        questsConfig,
                        redisActiveQuests,
                        redisPlayerData,
                        questCache,
                        playerCache,
                        aware);

        aware.listen("quest-reset", new Annotation[0], (message) -> {
            questsResetSubCommand.reset();
            return null;
        });

        QuestsAddSubCommand questsAddSubCommand =
                new QuestsAddSubCommand(plugin, questsConfig, questCache, questDatabase, aware, gson);

        aware.listen("quest-add", new Annotation[0], (message) -> {
            questsAddSubCommand.add(message.getContent().get("quest").toString());
            return null;
        });

        QuestDeleteSubCommand questDeleteSubCommand =
                new QuestDeleteSubCommand(
                        plugin,
                        questsConfig,
                        questDatabase,
                        redisActiveQuests,
                        questCache,
                        playerCache,
                        aware);
        aware.listen("quest-delete", new Annotation[0], (message) -> {
            questDeleteSubCommand.delete(message.getContent().get("quest").toString());
            return null;
        });

        questSubCommandCache.add(CmdType.ADD, questsAddSubCommand);
        questSubCommandCache.add(CmdType.DELETE, questDeleteSubCommand);
        questSubCommandCache.add(CmdType.RESET, questsResetSubCommand);
        questSubCommandCache.add(
                CmdType.MENU, new QuestsMenuSubCommand(questsConfig, questCache, playerCache));
        questSubCommandCache.add(
                CmdType.LANGUAGE,
                new QuestLanguageSubCommand(plugin, questsConfig, playerCache, storageDatabase));
        questSubCommandCache.add(
                CmdType.RELOAD,
                new QuestsReloadSubCommand(plugin, questsConfig)
        );
    }
}
