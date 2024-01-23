package com.codepunisher.quests;

import com.codepunisher.quests.cache.QuestCache;
import com.codepunisher.quests.cache.QuestSubCommandCache;
import com.codepunisher.quests.commands.QuestSubCommandRegistrar;
import com.codepunisher.quests.commands.QuestsCommand;
import com.codepunisher.quests.commands.lib.CommandRegistrar;
import com.codepunisher.quests.config.QuestsConfig;
import com.codepunisher.quests.database.impl.QuestDatabaseImpl;
import com.zaxxer.hikari.HikariDataSource;
import fr.mrmicky.fastinv.FastInvManager;
import org.bukkit.plugin.java.JavaPlugin;

public class QuestsPlugin extends JavaPlugin {
  private HikariDataSource hikariDataSource;

  @Override
  public void onEnable() {
    QuestsConfig questsConfig = new QuestsConfig();
    questsConfig.reload(this);

    QuestSubCommandCache subCommandCache = new QuestSubCommandCache();
    QuestCache questCache = new QuestCache();
    QuestSubCommandRegistrar subCommandRegistrar = new QuestSubCommandRegistrar(this, questsConfig, questCache, subCommandCache);
    subCommandRegistrar.register();

    QuestsCommand questsCommand = new QuestsCommand(this, questsConfig, subCommandCache);
    CommandRegistrar commandRegistrar = new CommandRegistrar(this, questsConfig);
    questsConfig.getLanguageCommandMap().forEach((key, langCmd) -> {
      commandRegistrar.registerCommands(
              questsCommand, langCmd.getPrimaryCommands().toArray(new String[0]));
    });

    hikariDataSource = new HikariDataSource(questsConfig.getHikariConfig());
    QuestDatabaseImpl questDatabase = new QuestDatabaseImpl(this, hikariDataSource);
    questDatabase.createQuestTable();

    FastInvManager.register(this);
  }

  @Override
  public void onDisable() {
    hikariDataSource.close();
  }
}
