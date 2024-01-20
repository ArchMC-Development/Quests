package com.codepunisher.quests;

import com.codepunisher.quests.commands.QuestsCommand;
import com.codepunisher.quests.commands.QuestSubCommandRegistrar;
import com.codepunisher.quests.commands.QuestsSubCommandCache;
import com.codepunisher.quests.commands.lib.CommandRegistrar;
import com.codepunisher.quests.config.QuestsConfig;
import org.bukkit.plugin.java.JavaPlugin;

public class QuestsPlugin extends JavaPlugin {
  @Override
  public void onEnable() {
    QuestsConfig questsConfig = new QuestsConfig();
    questsConfig.reload(this);

    QuestsSubCommandCache questsSubCommandCache = new QuestsSubCommandCache();
    QuestSubCommandRegistrar questSubCommandRegistrar = new QuestSubCommandRegistrar(questsSubCommandCache, questsConfig);
    questSubCommandRegistrar.register();

    QuestsCommand questsCommand = new QuestsCommand(questsSubCommandCache, questsConfig);

    CommandRegistrar commandRegistrar = new CommandRegistrar(this, questsConfig);
    commandRegistrar.registerCommands(questsCommand, questsConfig.getQuestCommands().toArray(new String[0]));
  }
}
