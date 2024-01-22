package com.codepunisher.quests.commands.subcommands;

import com.codepunisher.quests.commands.QuestsSubCommand;
import com.codepunisher.quests.commands.lib.CommandCall;
import com.codepunisher.quests.config.QuestsConfig;
import com.codepunisher.quests.menu.ConfigurationMainMenu;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;

// TODO: Create/Delete/Edit
// TODO: Configurable category
// TODO: Configurable difficulty
// TODO: Quest (long form) -> name, task types list, amount per task, category, dependencies (other
// quests), rewards
// TODO: Quest (daily) -> name, task types list, min-max randomized per task, rewards
@AllArgsConstructor
public class QuestsConfigureSubCommand implements QuestsSubCommand {
  @Override
  public Consumer<CommandCall> getCommandCallConsumer(JavaPlugin plugin, QuestsConfig questsConfig) {
    return call -> {
      Player player = call.asPlayer();
      new ConfigurationMainMenu(player, questsConfig).open(player);
    };
  }
}
