package com.codepunisher.quests.commands.subcommands;

import com.codepunisher.quests.commands.QuestsSubCommand;
import com.codepunisher.quests.commands.lib.CommandCall;
import com.codepunisher.quests.config.QuestsConfig;
import com.codepunisher.quests.menu.ConfigurationMainMenu;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

//TODO: Create/Delete/Edit
//TODO: Configurable category
//TODO: Configurable difficulty
//TODO: Quest (long form) -> name, task types list, amount per task, category, dependencies (other quests), rewards
//TODO: Quest (daily) -> name, task types list, min-max randomized per task, rewards
public class QuestsConfigureSubCommand extends QuestsSubCommand {
    private final QuestsConfig config;

    public QuestsConfigureSubCommand(String command, String usage, String permission, QuestsConfig config) {
        super(command, usage, permission);
        this.config = config;
    }

    @Override
    protected Consumer<CommandCall> getCommandCallConsumer() {
        return call -> {
            Player player = call.asPlayer();
            new ConfigurationMainMenu(player, config).open(player);
        };
    }
}
