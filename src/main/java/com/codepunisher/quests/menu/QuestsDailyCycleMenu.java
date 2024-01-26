package com.codepunisher.quests.menu;

import com.codepunisher.quests.cache.QuestCache;
import com.codepunisher.quests.cache.QuestPlayerCache;
import com.codepunisher.quests.models.Quest;
import com.codepunisher.quests.models.QuestPlayerData;
import com.codepunisher.quests.util.ItemBuilder;
import com.codepunisher.quests.util.UtilChat;
import fr.mrmicky.fastinv.FastInv;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class QuestsDailyCycleMenu extends FastInv {
  private final QuestPlayerCache playerCache;

  public QuestsDailyCycleMenu(Player player, QuestCache questCache, QuestPlayerCache playerCache) {
    super(27, "Test");
    this.playerCache = playerCache;

    questCache
        .getActiveQuestsEntrySet()
        .forEach(
            entry -> {
              questCache
                  .getQuest(entry.getKey())
                  .ifPresent(
                      quest -> {
                        QuestPlayerData playerData = getPlayerData(player);
                        boolean hasJoined = playerData.getCurrentQuestId().equals(quest.getId());
                        String color = hasJoined ? "&6" : "&a";

                        ItemStack item =
                            ItemBuilder.of(quest.getDisplay())
                                .name(color + quest.getQuestType().name())
                                .lore("&7Requirement: " + entry.getValue())
                                .glowIf(meta -> hasJoined)
                                .build();

                        addItem(
                            item,
                            (event) -> {
                              if (hasJoined) {
                                player.sendMessage(
                                    UtilChat.colorize("&cYou have already joined this quest!"));
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
