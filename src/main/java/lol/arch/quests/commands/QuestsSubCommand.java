package lol.arch.quests.commands;

import lol.arch.quests.commands.lib.CommandCall;

import java.util.function.Consumer;

public interface QuestsSubCommand {
    Consumer<CommandCall> getCommandCallConsumer();
}
