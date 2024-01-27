package com.codepunisher.quests.menu;

import com.codepunisher.quests.cache.QuestCache;
import com.codepunisher.quests.cache.QuestPlayerCache;
import com.codepunisher.quests.models.QuestPlayerData;
import com.codepunisher.quests.util.ItemBuilder;
import com.codepunisher.quests.util.UtilChat;
import fr.mrmicky.fastinv.FastInv;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class QuestsDailyCycleMenu extends FastInv {
  private final QuestPlayerCache playerCache;

  public QuestsDailyCycleMenu(Player player, QuestCache questCache, QuestPlayerCache playerCache) {
    super(27, "Quests");
    this.playerCache = playerCache;

    questCache
        .getActiveQuestsEntrySet()
        .forEach(
            entry -> {
              questCache
                  .getQuest(entry.getKey())
                  .ifPresent(
                      quest -> {
                        if (!player.hasPermission(quest.getPermission())) {
                          return;
                        }

                        QuestPlayerData playerData = getPlayerData(player);
                        boolean hasJoined = playerData.getCurrentQuestId().equals(quest.getId());
                        boolean isCompleted =
                            playerData.getCompletedDailyQuests().contains(quest.getId());
                        String color = isCompleted ? "&c" : hasJoined ? "&6" : "&a";

                        Material type =
                            isCompleted ? Material.RED_STAINED_GLASS_PANE : quest.getDisplay();

                        List<String> listLore = new ArrayList<>();
                        listLore.add(
                            isCompleted ? "&c&lCOMPLETED" : "&7Requirement: " + entry.getValue());
                        listLore.add("");

                        if (!hasJoined && !isCompleted) {
                          listLore.add("&7Click to join");
                        }

                        if (hasJoined && !isCompleted) {
                          listLore.add("&7Right-Click to opt-out");
                        }

                        ItemStack item =
                            ItemBuilder.of(type)
                                .name(color + quest.getQuestType().name())
                                .lore(listLore)
                                .glowIf(meta -> hasJoined && !isCompleted)
                                .build();

                        addItem(
                            item,
                            (event) -> {
                              if (isCompleted) {
                                return;
                              }

                              if (hasJoined && event.isRightClick()) {
                                new AbstractAreYouSureMenu(
                                        p -> {
                                          playerData.optOutOfCurrentQuestId();
                                          player.sendMessage(
                                              UtilChat.colorize("&cYou have left the quest"));
                                          new QuestsDailyCycleMenu(player, questCache, playerCache)
                                              .open(player);
                                        },
                                        this,
                                        "&7This will remove you",
                                        "&7from the quest and delete",
                                        "&7all progress!")
                                    .open(player);
                                return;
                              }

                              if (hasJoined) {
                                player.sendMessage(
                                    UtilChat.colorize("&cYou have already joined this quest!"));
                                return;
                              }

                              // If player is already in a quest (are you sure menu)
                              if (!playerData.getCurrentQuestId().isEmpty()) {
                                new AbstractAreYouSureMenu(
                                        p -> {
                                          playerData.setCurrentQuestId(quest.getId());
                                          playerCache.add(player.getUniqueId(), playerData);
                                          player.sendMessage(
                                              UtilChat.colorize("&aYou have joined the quest!"));
                                          new QuestsDailyCycleMenu(player, questCache, playerCache)
                                              .open(player);
                                        },
                                        this,
                                        "&7Are you sure you want to ",
                                        "&7switch quests? This will remove",
                                        "&7progress on your current quest!")
                                    .open(player);
                                return;
                              }

                              playerData.setCurrentQuestId(quest.getId());
                              playerCache.add(player.getUniqueId(), playerData);
                              player.sendMessage(UtilChat.colorize("&aYou have joined the quest!"));
                              new QuestsDailyCycleMenu(player, questCache, playerCache)
                                  .open(player);
                            });
                      });
            });
  }

  private QuestPlayerData getPlayerData(Player player) {
    Optional<QuestPlayerData> optionalPlayerData = playerCache.get(player.getUniqueId());
    return optionalPlayerData.orElseGet(QuestPlayerData::new);
  }
}
