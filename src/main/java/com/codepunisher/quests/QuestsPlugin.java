package com.codepunisher.quests;

import com.codepunisher.quests.cache.QuestCache;
import com.codepunisher.quests.cache.QuestPlayerCache;
import com.codepunisher.quests.cache.QuestSubCommandCache;
import com.codepunisher.quests.commands.QuestSubCommandRegistrar;
import com.codepunisher.quests.commands.QuestsCommand;
import com.codepunisher.quests.commands.lib.CommandRegistrar;
import com.codepunisher.quests.config.QuestsConfig;
import com.codepunisher.quests.database.QuestDatabase;
import com.codepunisher.quests.database.impl.QuestDatabaseImpl;
import com.zaxxer.hikari.HikariDataSource;
import fr.mrmicky.fastinv.FastInvManager;
import me.drepic.proton.common.ProtonManager;
import me.drepic.proton.common.ProtonProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class QuestsPlugin extends JavaPlugin {
  private HikariDataSource hikariDataSource;

  @Override
  public void onEnable() {
    QuestsConfig questsConfig = new QuestsConfig();
    questsConfig.reload(this);

    hikariDataSource = new HikariDataSource(questsConfig.getHikariConfig());
    QuestDatabase questDatabase = new QuestDatabaseImpl(this, hikariDataSource);
    questDatabase.createQuestTable();

    ProtonManager proton = ProtonProvider.get();

    QuestSubCommandCache subCommandCache = new QuestSubCommandCache();
    QuestCache questCache = new QuestCache();
    QuestPlayerCache playerCache = new QuestPlayerCache();
    QuestSubCommandRegistrar subCommandRegistrar =
        new QuestSubCommandRegistrar(this, subCommandCache, questCache, playerCache, questDatabase, proton);
    subCommandRegistrar.register();

    QuestsCommand questsCommand = new QuestsCommand(questsConfig, subCommandCache);
    CommandRegistrar commandRegistrar = new CommandRegistrar(this, questsConfig);
    questsConfig
        .getLanguageCommandMap()
        .forEach(
            (key, langCmd) -> {
              commandRegistrar.registerCommands(
                  questsCommand, langCmd.getPrimaryCommands().toArray(new String[0]));
            });

    FastInvManager.register(this);
  }

  @Override
  public void onDisable() {
    hikariDataSource.close();
  }
}
