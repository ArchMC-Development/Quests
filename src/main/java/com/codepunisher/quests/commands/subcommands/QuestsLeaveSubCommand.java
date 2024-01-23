package com.codepunisher.quests.commands.subcommands;

import com.codepunisher.quests.commands.QuestsSubCommand;
import com.codepunisher.quests.commands.lib.CommandCall;

import java.util.function.Consumer;

public class QuestsLeaveSubCommand implements QuestsSubCommand {
    @Override
    public Consumer<CommandCall> getCommandCallConsumer() {
        return call -> {
            call.asPlayer().sendMessage("leave");
        };
    }
}