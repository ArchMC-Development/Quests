package com.codepunisher.quests.commands;

import com.codepunisher.quests.commands.lib.CommandCall;

import java.util.function.Consumer;

public interface QuestsSubCommand {
   Consumer<CommandCall> getCommandCallConsumer();
}
