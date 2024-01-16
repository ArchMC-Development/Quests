package com.codepunisher.quests.commands;

import org.bukkit.command.CommandSender;

public interface QuestsSubCommand {
    String getCommand();

    String usage();

    String permission();

    String requirePlayerArgument();

    boolean requirePlayerSender();

    boolean run(CommandSender sender, String[] args);
}
