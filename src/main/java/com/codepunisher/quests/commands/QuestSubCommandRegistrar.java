package com.codepunisher.quests.commands;

import com.codepunisher.quests.commands.subcommands.*;
import com.codepunisher.quests.config.QuestsConfig;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class QuestSubCommandRegistrar {
  private final QuestsSubCommandCache subCommandCache;
  private final QuestsConfig questsConfig;

  public void register() {
    subCommandCache.register(
        new QuestsReloadCommand(
            questsConfig.getQuestReloadSubCommand(),
            questsConfig.getQuestReloadSubCommandUsage(),
            questsConfig.getQuestReloadSubCommandPermission()));

    subCommandCache.register(
        new QuestsConfigureSubCommand(
            questsConfig.getQuestConfigureSubCommand(),
            questsConfig.getQuestConfigureSubCommandUsage(),
            questsConfig.getQuestConfigureSubCommandPermission(),
            questsConfig));

    subCommandCache.register(
        new QuestsJoinSubCommand(
            questsConfig.getQuestJoinSubCommand(),
            questsConfig.getQuestJoinSubCommandUsage(),
            questsConfig.getQuestJoinSubCommandPermission()));

    subCommandCache.register(
        new QuestsLeaveSubCommand(
            questsConfig.getQuestLeaveSubCommand(),
            questsConfig.getQuestLeaveSubCommandUsage(),
            questsConfig.getQuestLeaveSubCommandPermission()));

    subCommandCache.register(
        new QuestsStatusSubCommand(
            questsConfig.getQuestStatusSubCommand(),
            questsConfig.getQuestStatusSubCommandUsage(),
            questsConfig.getQuestStatusSubCommandPermission()));

    subCommandCache.register(
        new QuestsMenuSubCommand(
            questsConfig.getQuestMenuSubCommand(),
            questsConfig.getQuestMenuSubCommandUsage(),
            questsConfig.getQuestMenuSubCommandPermission()));
  }
}
