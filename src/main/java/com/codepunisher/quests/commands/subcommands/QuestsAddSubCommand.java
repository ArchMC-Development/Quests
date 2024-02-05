package com.codepunisher.quests.commands.subcommands;

import com.codepunisher.quests.cache.QuestCache;
import com.codepunisher.quests.commands.QuestsSubCommand;
import com.codepunisher.quests.commands.lib.CommandCall;
import com.codepunisher.quests.config.QuestsConfig;
import com.codepunisher.quests.database.QuestDatabase;
import com.codepunisher.quests.models.Quest;
import com.codepunisher.quests.models.QuestType;
import com.codepunisher.quests.util.UtilChat;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import me.drepic.proton.common.ProtonManager;
import me.drepic.proton.common.message.MessageHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

@AllArgsConstructor
public class QuestsAddSubCommand implements QuestsSubCommand {
  private final JavaPlugin plugin;
  private final QuestsConfig questsConfig;
  private final QuestCache questCache;
  private final QuestDatabase questDatabase;
  private final ProtonManager proton;
  private final Gson gson;

  @Override
  public Consumer<CommandCall> getCommandCallConsumer() {
    return call -> {
      CommandSender sender = call.getSender();
      try {
        String id = call.getArg(1).toLowerCase();
        if (questCache.getQuest(id).isPresent()) {
          sender.sendMessage(UtilChat.colorize("&cThat quest id already exists!"));
          return;
        }

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
        questDatabase.insert(quest);
        proton.broadcast("quest-plugin", "quest-add", gson.toJson(quest));
        plugin
            .getLogger()
            .info(String.format("%s has been added by %s", quest.getId(), sender.getName()));
        sender.sendMessage(
            UtilChat.colorize(
                questsConfig.getLang(sender).getQuestAddSuccess().replaceAll("%1%", id.replaceAll("_", " "))));
      } catch (RuntimeException e) {
        String options = Arrays.toString(QuestType.values());
        sender.sendMessage(
            UtilChat.colorize(questsConfig.getLang(sender).getInvalidQuestAddUsage())
                .replaceAll(
                    "%1%",
                    "/"
                        + call.getName()
                        + " add <id> "
                        + options
                        + " <association> <min> <max> <permission> <console-command-rewards>"));
      }
    };
  }

  @MessageHandler(namespace = "quest-plugin", subject = "quest-add")
  public void onCacheUpdateReceive(String jsonQuest) {
    questCache.add(gson.fromJson(jsonQuest, Quest.class));
  }
}
