package com.codepunisher.quests.commands.subcommands;

import com.codepunisher.quests.cache.QuestCache;
import com.codepunisher.quests.cache.QuestPlayerCache;
import com.codepunisher.quests.commands.QuestsSubCommand;
import com.codepunisher.quests.commands.lib.CommandCall;
import com.codepunisher.quests.config.QuestsConfig;
import com.codepunisher.quests.database.QuestDatabase;
import com.codepunisher.quests.models.ActiveQuestPlayerData;
import com.codepunisher.quests.models.Quest;
import com.codepunisher.quests.util.UtilChat;
import com.google.gson.Gson;
import gg.scala.aware.Aware;
import gg.scala.aware.message.AwareMessage;
import gg.scala.aware.thread.AwareThreadContext;
import kotlin.Pair;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Optional;
import java.util.function.Consumer;

@AllArgsConstructor
public class QuestEditSubCommand implements QuestsSubCommand {
    private final QuestsConfig questsConfig;
    private final QuestCache questCache;
    private final QuestDatabase questDatabase;
    private final Aware<AwareMessage> aware;
    private final QuestPlayerCache playerCache;
    private final Gson gson;

    @Override
    public Consumer<CommandCall> getCommandCallConsumer() {
        return call -> {
            CommandSender sender = call.getSender();
            try {
                String mode = call.getArg(1);
                String id = call.getArg(2).toLowerCase();
                Optional<Quest> quest = questCache.getQuest(id);
                if (quest.isEmpty()) {
                    sender.sendMessage(UtilChat.colorize("&cA quest with the id %s does not exist!".formatted(id)));
                    return;
                }
                String value = call.getStringFromArgumentsAtIndex(3);
                if (value == null) throw new RuntimeException();

                questDatabase.remove(quest.get());
                wipeFromCaches(quest.get().getId());
                Quest newQuest;
                if (mode.equalsIgnoreCase("displayname")) {
                    newQuest = new Quest(
                            quest.get().getId(),
                            value,
                            quest.get().getQuestType(),
                            quest.get().getAssociatedObject(),
                            quest.get().getMin(),
                            quest.get().getMax(),
                            quest.get().getPermission(),
                            quest.get().getRewards()
                    );
                } else if (mode.equalsIgnoreCase("rewards")) {
                    newQuest = new Quest(
                            quest.get().getId(),
                            quest.get().getDisplayName(),
                            quest.get().getQuestType(),
                            quest.get().getAssociatedObject(),
                            quest.get().getMin(),
                            quest.get().getMax(),
                            quest.get().getPermission(),
                            value.split(",")
                    );
                } else {
                    throw new RuntimeException();
                }

                questCache.add(newQuest);
                questDatabase.insert(newQuest);
                AwareMessage.of(
                        "quest-edit",
                        aware,
                        new Pair<>("quest", gson.toJson(newQuest))
                ).publish(AwareThreadContext.ASYNC, "quest-plugin");
                sender.sendMessage(
                        UtilChat.colorize(
                                questsConfig.getLang(sender).getQuestEditSuccess().replaceAll("%1%", id.replaceAll("_", " "))));
            } catch (RuntimeException e) {
                sender.sendMessage(
                        UtilChat.colorize(questsConfig.getLang(sender).getInvalidQuestEditUsage())
                                .replaceAll(
                                        "%1%",
                                        "/" + call.getName() + " edit <displayname|rewards> <id> <value>"));
            }
        };
    }

    public void edit(String questJson) {
        Quest quest = gson.fromJson(questJson, Quest.class);
        wipeFromCaches(quest.getId());
        questCache.add(quest);
    }

    private void wipeFromCaches(String id) {
        questCache.remove(id);
        questCache.removeActiveQuest(id);
        playerCache
                .getActiveQuestEntrySet()
                .forEach(
                        entry -> {
                            ActiveQuestPlayerData playerData = entry.getValue();
                            if (playerData.getCurrentQuestId().equalsIgnoreCase(id)) {
                                playerData.optOutOfCurrentQuestId();
                                Optional.ofNullable(Bukkit.getPlayer(entry.getKey()))
                                        .ifPresent(
                                                player -> player.sendMessage(
                                                        UtilChat.colorize(
                                                                questsConfig.getLang(player).getQuestDeletedByAdmin())));
                            }
                        });
    }
}
