package com.codepunisher.quests.commands.subcommands;

import com.codepunisher.quests.cache.QuestCache;
import com.codepunisher.quests.commands.QuestsSubCommand;
import com.codepunisher.quests.commands.lib.CommandCall;
import com.codepunisher.quests.config.QuestsConfig;
import com.codepunisher.quests.models.Quest;
import com.codepunisher.quests.models.QuestType;
import com.codepunisher.quests.util.UtilChat;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.function.Consumer;

// TODO: GUI or command -> /quest add id=%s type=%s association=%s min=%s max=%s permission=%s
//TODO: DATABASE AND REDIS
// rewards=%s
@AllArgsConstructor
public class QuestsAddSubCommand implements QuestsSubCommand {
  private final QuestsConfig questsConfig;
  private final QuestCache questCache;

  @Override
  public Consumer<CommandCall> getCommandCallConsumer() {
    return call -> {
      Player player = call.asPlayer();
      try {
        String id = call.getArg(1).toLowerCase();
        QuestType questType = QuestType.valueOf(call.getArg(2).toUpperCase());
        Object associatedObject = questType.getAssociationFromInput(call.getArg(3));
        int min = Integer.parseInt(call.getArg(4));
        int max = Integer.parseInt(call.getArg(5));
        String permission = call.getArg(6);
        String[] consoleCommandRewards =
            Objects.requireNonNull(call.getStringFromArgumentsAtIndex(7)).split(",");

        Quest quest =
            new Quest(id, questType, associatedObject, min, max, permission, consoleCommandRewards);
        questCache.add(quest);
        player.sendMessage(UtilChat.colorize("&aYour mom this totally works get fucking good kid: &f" + id));
      } catch (RuntimeException ignored) {
        player.sendMessage(
            UtilChat.colorize(
                "&cInvalid usage! Example -> /quest add <id> <type> <association> <min> <max> <permission> <console-command-rewards>"));
      }
    };
  }
}
