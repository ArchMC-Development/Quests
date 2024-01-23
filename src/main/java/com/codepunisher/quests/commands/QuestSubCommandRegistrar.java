package com.codepunisher.quests.commands;

import com.codepunisher.quests.cache.QuestCache;
import com.codepunisher.quests.cache.QuestSubCommandCache;
import com.codepunisher.quests.commands.subcommands.*;
import com.codepunisher.quests.config.QuestsConfig;
import com.codepunisher.quests.models.CmdType;
import lombok.AllArgsConstructor;
import org.bukkit.plugin.java.JavaPlugin;

@AllArgsConstructor
public class QuestSubCommandRegistrar {
  private final JavaPlugin plugin;
  private final QuestsConfig questsConfig;
  private final QuestCache questCache;
  private final QuestSubCommandCache questSubCommandCache;

  public void register() {
    questSubCommandCache.add(CmdType.RELOAD, new QuestsReloadCommand());
    questSubCommandCache.add(CmdType.ADD, new QuestsAddSubCommand(questsConfig, questCache));
    questSubCommandCache.add(CmdType.JOIN, new QuestsJoinSubCommand());
    questSubCommandCache.add(CmdType.LEAVE, new QuestsLeaveSubCommand());
    questSubCommandCache.add(CmdType.STATUS, new QuestsStatusSubCommand());
    questSubCommandCache.add(CmdType.MENU, new QuestsMenuSubCommand());
    questSubCommandCache.add(CmdType.LANGUAGE, new QuestLanguageSubCommand());
  }
}
