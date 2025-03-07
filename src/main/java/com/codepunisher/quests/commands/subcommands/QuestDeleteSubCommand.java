package com.codepunisher.quests.commands.subcommands;

import com.codepunisher.quests.cache.QuestCache;
import com.codepunisher.quests.cache.QuestPlayerCache;
import com.codepunisher.quests.commands.QuestsSubCommand;
import com.codepunisher.quests.commands.lib.CommandCall;
import com.codepunisher.quests.config.QuestsConfig;
import com.codepunisher.quests.database.QuestDatabase;
import com.codepunisher.quests.menu.AreYouSureMenu;
import com.codepunisher.quests.models.ActiveQuestPlayerData;
import com.codepunisher.quests.models.Quest;
import com.codepunisher.quests.redis.RedisActiveQuests;
import com.codepunisher.quests.util.UtilChat;
import gg.scala.aware.Aware;
import gg.scala.aware.message.AwareMessage;
import gg.scala.aware.thread.AwareThreadContext;
import kotlin.Pair;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;
import java.util.function.Consumer;

@AllArgsConstructor
public class QuestDeleteSubCommand implements QuestsSubCommand {
    private final JavaPlugin plugin;
    private final QuestsConfig questsConfig;
    private final QuestDatabase questDatabase;
    private final RedisActiveQuests redisActiveQuests;
    private final QuestCache questCache;
    private final QuestPlayerCache playerCache;
    private final Aware<AwareMessage> aware;

    @Override
    public Consumer<CommandCall> getCommandCallConsumer() {
        return call -> {
            Player player = call.asPlayer();
            try {
                Optional<Quest> optionalQuest = questCache.getQuest(call.getArg(1));
                if (optionalQuest.isEmpty()) {
                    throw new NullPointerException();
                }

                new AreYouSureMenu(
                        player,
                        questsConfig,
                        questsConfig.getLang(player).getAreYouSureDeleteInventory(),
                        () -> {
                            Quest quest = optionalQuest.get();
                            questDatabase.remove(quest);
                            redisActiveQuests.removeQuest(quest);
                            wipeFromCaches(quest.getId());
                            AwareMessage.of(
                                    "quest-add",
                                    aware,
                                    new Pair<>("quest", quest.getId())
                            ).publish(AwareThreadContext.ASYNC, "quest-plugin");
                            plugin
                                    .getLogger()
                                    .info(
                                            String.format(
                                                    "%s just deleted the quest %s", player.getName(), quest.getId()));
                            player.sendMessage(
                                    UtilChat.colorize(
                                            questsConfig
                                                    .getLang(player)
                                                    .getQuestDeleted()
                                                    .replaceAll("%1%", quest.getId())));
                            player.closeInventory();
                        },
                        player::closeInventory)
                        .open(player);
            } catch (Exception e) {
                player.sendMessage(
                        UtilChat.colorize(
                                questsConfig
                                        .getLang(player)
                                        .getQuestDoesNotExist()
                                        .replaceAll(
                                                "%1%",
                                                questCache.getQuests().stream().map(Quest::getId).toList().toString())));
            }
        };
    }

    public void delete(String id) {
        wipeFromCaches(id);
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
                                                player -> {
                                                    player.sendMessage(
                                                            UtilChat.colorize(
                                                                    questsConfig.getLang(player).getQuestDeletedByAdmin()));
                                                });
                            }
                        });
    }
}
