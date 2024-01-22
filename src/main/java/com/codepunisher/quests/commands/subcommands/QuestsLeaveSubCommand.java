package com.codepunisher.quests.commands.subcommands;

import com.codepunisher.quests.commands.QuestsSubCommand;
import com.codepunisher.quests.commands.lib.CommandCall;
import com.codepunisher.quests.config.QuestsConfig;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;

public class QuestsLeaveSubCommand implements QuestsSubCommand {
    @Override
    public Consumer<CommandCall> getCommandCallConsumer(JavaPlugin plugin, QuestsConfig questsConfig) {
        return call -> {
            call.asPlayer().sendMessage("leave");
        };
    }
}