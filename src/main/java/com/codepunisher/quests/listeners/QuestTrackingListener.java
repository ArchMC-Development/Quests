package com.codepunisher.quests.listeners;

import com.codepunisher.quests.cache.QuestCache;
import com.codepunisher.quests.cache.QuestPlayerCache;
import com.codepunisher.quests.models.Quest;
import com.codepunisher.quests.models.QuestPlayerData;
import com.codepunisher.quests.models.QuestType;
import com.codepunisher.quests.redis.RedisPlayerData;
import com.codepunisher.quests.util.UtilChat;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.CraftItemEvent;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
public class QuestTrackingListener implements Listener {
  private final RedisPlayerData redisPlayerData;
  private final QuestPlayerCache playerCache;
  private final QuestCache questCache;

  // TODO: (stop players from re-joining)
  @EventHandler
  public void onBreak(BlockBreakEvent event) {
    handleQuestProgressIncrease(event.getPlayer(), QuestType.BLOCK_BREAK, event.getBlock().getType());
  }

  @EventHandler
  public void onCraft(CraftItemEvent event) {
    Player player = (Player) event.getWhoClicked();
    handleQuestProgressIncrease(player, QuestType.CRAFTING, event.getRecipe().getResult().getType());
  }

  private <T> void handleQuestProgressIncrease(
      Player player, QuestType questType, T associatedObject) {
    UUID uuid = player.getUniqueId();
    Optional<QuestPlayerData> playerDataOptional = playerCache.get(uuid);
    if (playerDataOptional.isEmpty()) {
      return;
    }

    QuestPlayerData playerData = playerDataOptional.get();
    String questId = playerData.getCurrentQuestId();
    Optional<Quest> questOptional = questCache.getQuest(questId);
    if (questOptional.isEmpty()) {
      return;
    }

    Quest quest = questOptional.get();
    if (quest.getQuestType() != questType) {
      return;
    }

    // Checking if the associated type (material, etc.) matches
    if (!questType
        .getInputFromAssociatedObject(associatedObject)
        .equalsIgnoreCase(questType.getInputFromAssociatedObject(quest.getAssociatedObject()))) {
      return;
    }

    Optional<Integer> requirementOptional = questCache.getRequirement(questId);
    if (requirementOptional.isEmpty()) {
      return;
    }

    int requirement = requirementOptional.get();
    playerData.incrementQuestProgress(1);

    // Quest completion
    int progress = playerData.getCurrentQuestProgress();
    if (progress >= requirement) {
      player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.75f, 1.75f);
      player.sendTitle(UtilChat.colorize("&aQuest Complete"), UtilChat.colorize("good job!!!"));

      redisPlayerData.clear(uuid);
      playerCache.remove(uuid);

      Arrays.stream(quest.getRewards())
          .forEach(
              reward -> {
                Bukkit.dispatchCommand(
                    Bukkit.getConsoleSender(), reward.replaceAll("%player%", player.getName()));
              });
    }

    player.sendActionBar(
        UtilChat.colorize(
            String.format("&aTotal broken for quest: &8(&f%s&7/&f%s&8)", progress, requirement)));
  }
}
