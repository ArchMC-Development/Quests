package com.codepunisher.quests.commands;

import com.codepunisher.quests.commands.lib.CommandCall;
import com.codepunisher.quests.config.QuestsConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;

public interface QuestsSubCommand {
   Consumer<CommandCall> getCommandCallConsumer(JavaPlugin plugin, QuestsConfig questsConfig);
}
