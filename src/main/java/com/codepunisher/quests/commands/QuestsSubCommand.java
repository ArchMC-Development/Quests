package com.codepunisher.quests.commands;

import com.codepunisher.quests.commands.lib.CommandCall;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Consumer;

@Getter
@AllArgsConstructor
public abstract class QuestsSubCommand {
    private final String command;
    private final String usage;
    private final String permission;

    protected abstract Consumer<CommandCall> getCommandCallConsumer();
}
