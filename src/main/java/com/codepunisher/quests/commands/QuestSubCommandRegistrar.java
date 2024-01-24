package com.codepunisher.quests.commands;

import com.codepunisher.quests.cache.QuestCache;
import com.codepunisher.quests.cache.QuestPlayerCache;
import com.codepunisher.quests.cache.QuestSubCommandCache;
import com.codepunisher.quests.commands.subcommands.*;
import com.codepunisher.quests.config.QuestsConfig;
import com.codepunisher.quests.database.QuestDatabase;
import com.codepunisher.quests.models.CmdType;
import lombok.AllArgsConstructor;
import me.drepic.proton.common.ProtonManager;
import me.drepic.proton.common.ProtonProvider;
import org.bukkit.plugin.java.JavaPlugin;

@AllArgsConstructor
public class QuestSubCommandRegistrar {
  private final JavaPlugin plugin;
  private final QuestSubCommandCache questSubCommandCache;
  private final QuestCache questCache;
  private final QuestPlayerCache playerCache;
  private final QuestDatabase questDatabase;
  private final ProtonManager proton;

  public void register() {
    questSubCommandCache.add(CmdType.RELOAD, new QuestsReloadCommand());

    QuestResetDailySubCommand questsResetSubCommand = new QuestResetDailySubCommand(plugin, questCache, playerCache, proton);
    proton.registerMessageHandlers(questsResetSubCommand);

    QuestsAddSubCommand questsAddSubCommand = new QuestsAddSubCommand(questCache, questDatabase, proton);
    proton.registerMessageHandlers(questsAddSubCommand);

    questSubCommandCache.add(CmdType.ADD, questsAddSubCommand);
    questSubCommandCache.add(CmdType.JOIN, new QuestsJoinSubCommand());
    questSubCommandCache.add(CmdType.LEAVE, new QuestsLeaveSubCommand());
    questSubCommandCache.add(CmdType.STATUS, new QuestsStatusSubCommand());
    questSubCommandCache.add(CmdType.MENU, new QuestsMenuSubCommand(questCache));
    questSubCommandCache.add(CmdType.LANGUAGE, new QuestLanguageSubCommand());
  }
}
