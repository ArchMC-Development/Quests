package com.codepunisher.quests.commands.subcommands;

import com.codepunisher.quests.commands.QuestsSubCommand;
import com.codepunisher.quests.commands.lib.CommandCall;

import java.util.function.Consumer;

public class QuestsJoinSubCommand extends QuestsSubCommand {
  public QuestsJoinSubCommand(String command, String usage, String permission) {
    super(command, usage, permission);
  }

  @Override
  public Consumer<CommandCall> getCommandCallConsumer() {
    return call -> {
      call.asPlayer().sendMessage("join");
    };
  }
}
