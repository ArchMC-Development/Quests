package com.codepunisher.quests.commands.lib;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

@Getter
@AllArgsConstructor
public class CommandCall {
  private final CommandSender sender;
  private final String[] args;
  private final String name;

  public Player asPlayer() {
    return (Player) sender;
  }

  public String getArg(int index) {
    return args[index];
  }

  public Player parsePlayer(int index) {
    return Bukkit.getPlayerExact(args[index]);
  }

  public boolean isSenderPlayer() {
    return sender instanceof Player;
  }

  public boolean isSenderPlayer(Player target) {
    if (!(sender instanceof Player player)) {
      return false;
    }

    return player.getUniqueId().equals(target.getUniqueId());
  }

  public boolean isSenderPlayer(String targetName) {
    if (!(sender instanceof Player player)) {
      return false;
    }

    return player.getName().equals(targetName);
  }

  public boolean isValidArg(int index) {
    // First check if 'args' is null or if 'index' is out of bounds
    if (args == null || index < 0 || index >= args.length) {
      return false;
    }

    // Then check if the targeted element is null
    return args[index] != null;
  }

  public boolean hasNoArgs() {
    return args.length == 0;
  }

  // Useful for commands like /broadcast <message>
  @Nullable
  public String getStringFromArguments() {
    return getStringFromArgumentsAtIndex(0);
  }

  @Nullable
  public String getStringFromArgumentsAtIndex(int index) {
    StringBuilder builder = new StringBuilder();
    for (int i = index; i < args.length; i++) {
      // No space at end
      if (i == args.length - 1) {
        builder.append(args[i]);
        continue;
      }

      // With space
      builder.append(args[i]).append(" ");
    }

    return builder.toString().isEmpty() ? null : builder.toString();
  }
}
