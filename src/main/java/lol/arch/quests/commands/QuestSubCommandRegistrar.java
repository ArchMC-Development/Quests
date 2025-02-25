package lol.arch.quests.commands;

import gg.scala.aware.Aware;
import gg.scala.aware.message.AwareMessage;
import lol.arch.quests.cache.QuestSubCommandCache;
import lol.arch.quests.commands.subcommands.*;
import lol.arch.quests.config.QuestsConfig;
import lol.arch.quests.models.CmdType;
import lombok.AllArgsConstructor;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.annotation.Annotation;

@AllArgsConstructor
public class QuestSubCommandRegistrar {
    private final JavaPlugin plugin;
    private final QuestsConfig questsConfig;
    private final QuestSubCommandCache questSubCommandCache;
    private final Aware<AwareMessage> aware;

    public void register() {
        QuestResetSubCommand questsResetSubCommand =
                new QuestResetSubCommand(
                        plugin,
                        questsConfig,
                        aware);

        aware.listen("quest-reset", new Annotation[0], (message) -> {
            questsResetSubCommand.reset();
            return null;
        });

        QuestsAddSubCommand questsAddSubCommand =
                new QuestsAddSubCommand(plugin, questsConfig);

        QuestDeleteSubCommand questDeleteSubCommand =
                new QuestDeleteSubCommand(
                        plugin,
                        questsConfig);

        QuestEditSubCommand questEditSubCommand =
                new QuestEditSubCommand(
                        questsConfig);

        questSubCommandCache.add(CmdType.ADD, questsAddSubCommand);
        questSubCommandCache.add(CmdType.DELETE, questDeleteSubCommand);
        questSubCommandCache.add(CmdType.EDIT, questEditSubCommand);
        questSubCommandCache.add(CmdType.RESET, questsResetSubCommand);
        questSubCommandCache.add(
                CmdType.MENU, new QuestsMenuSubCommand(questsConfig));
        questSubCommandCache.add(
                CmdType.LANGUAGE,
                new QuestLanguageSubCommand(plugin, questsConfig));
        questSubCommandCache.add(
                CmdType.RELOAD,
                new QuestsReloadSubCommand(plugin, questsConfig)
        );
    }
}
