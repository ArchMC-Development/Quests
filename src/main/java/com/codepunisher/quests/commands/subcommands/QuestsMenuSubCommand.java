package com.codepunisher.quests.commands.subcommands;

import com.codepunisher.quests.cache.QuestCache;
import com.codepunisher.quests.cache.QuestPlayerCache;
import com.codepunisher.quests.commands.QuestsSubCommand;
import com.codepunisher.quests.commands.lib.CommandCall;
import com.codepunisher.quests.config.QuestsConfig;
import com.codepunisher.quests.menu.ActiveQuestsMenu;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

@AllArgsConstructor
public class QuestsMenuSubCommand implements QuestsSubCommand {
  private final QuestsConfig questsConfig;
  private final QuestCache questCache;
  private final QuestPlayerCache playerCache;

  @Override
  public Consumer<CommandCall> getCommandCallConsumer() {
    return call -> {
      Player player = call.asPlayer();
      new ActiveQuestsMenu(player, questsConfig, questCache, playerCache).open(player);
    };
  }
}
