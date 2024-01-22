package com.codepunisher.quests.models;

import com.codepunisher.quests.commands.QuestsSubCommand;
import com.codepunisher.quests.commands.subcommands.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CmdType {
  RELOAD("ReloadSubCommand", new QuestsReloadCommand()),
  CONFIGURE("ConfigureSubCommand", new QuestsConfigureSubCommand()),
  JOIN("JoinSubCommand", new QuestsJoinSubCommand()),
  LEAVE("LeaveSubCommand", new QuestsLeaveSubCommand()),
  STATUS("StatusSubCommand", new QuestsStatusSubCommand()),
  MENU("MenuSubCommand", new QuestsMenuSubCommand()),
  LANGUAGE("LanguageSubCommand", new QuestLanguageSubCommand());

  private final String configPath;
  private final QuestsSubCommand questsSubCommand;
}
