package com.codepunisher.quests.commands.subcommands;

import com.codepunisher.quests.cache.QuestCache;
import com.codepunisher.quests.commands.QuestsSubCommand;
import com.codepunisher.quests.commands.lib.CommandCall;
import com.codepunisher.quests.menu.QuestsDailyCycleMenu;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

@AllArgsConstructor
public class QuestsMenuSubCommand implements QuestsSubCommand {
  private final QuestCache questCache;

  @Override
  public Consumer<CommandCall> getCommandCallConsumer() {
    return call -> {
      Player player = call.asPlayer();
      new QuestsDailyCycleMenu(questCache).open(player);
    };
  }
}
