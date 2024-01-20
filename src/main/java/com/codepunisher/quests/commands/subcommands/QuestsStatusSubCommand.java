package com.codepunisher.quests.commands.subcommands;

import com.codepunisher.quests.commands.QuestsSubCommand;
import com.codepunisher.quests.commands.lib.CommandCall;

import java.util.function.Consumer;

public class QuestsStatusSubCommand extends QuestsSubCommand {
    public QuestsStatusSubCommand(String command, String usage, String permission) {
        super(command, usage, permission);
    }

    @Override
    protected Consumer<CommandCall> getCommandCallConsumer() {
        return call -> {
            call.asPlayer().sendMessage("status");
        };
    }
}
