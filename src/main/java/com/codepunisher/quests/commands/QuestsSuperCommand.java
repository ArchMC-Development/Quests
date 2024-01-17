package com.codepunisher.quests.commands;

import com.google.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

@AllArgsConstructor(onConstructor = @__({ @Inject }))
public class QuestsSuperCommand implements CommandExecutor {
    private final QuestsSubCommandCache subCommandCache;

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command cmd, @NonNull String label, @NonNull String[] args) {
        boolean hasNoArgument = args.length == 0;
        if (hasNoArgument) {
            // Display all commands here
        }
        return false;
    }
}
