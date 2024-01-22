package com.codepunisher.quests;

import com.codepunisher.quests.commands.QuestsCommand;
import com.codepunisher.quests.commands.lib.CommandRegistrar;
import com.codepunisher.quests.config.QuestsConfig;
import fr.mrmicky.fastinv.FastInvManager;
import org.bukkit.plugin.java.JavaPlugin;

public class QuestsPlugin extends JavaPlugin {
  @Override
  public void onEnable() {
    QuestsConfig questsConfig = new QuestsConfig();
    questsConfig.reload(this);

    QuestsCommand questsCommand = new QuestsCommand(this, questsConfig);
    CommandRegistrar commandRegistrar = new CommandRegistrar(this, questsConfig);
    questsConfig.getLanguageCommandMap().forEach((key, langCmd) -> {
      commandRegistrar.registerCommands(
              questsCommand, langCmd.getPrimaryCommands().toArray(new String[0]));
    });

    FastInvManager.register(this);
  }
}
