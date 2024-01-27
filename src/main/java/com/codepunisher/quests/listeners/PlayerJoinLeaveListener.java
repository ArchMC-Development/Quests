package com.codepunisher.quests.listeners;

import com.codepunisher.quests.cache.QuestCache;
import com.codepunisher.quests.cache.QuestPlayerCache;
import com.codepunisher.quests.redis.RedisPlayerData;
import com.codepunisher.quests.util.UtilChat;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

@AllArgsConstructor
public class PlayerJoinLeaveListener implements Listener {
  private final QuestCache questCache;
  private final QuestPlayerCache playerCache;
  private final RedisPlayerData redisPlayerData;

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    removeQuestFromPlayerIfNoLongerExists(player);
    redisPlayerData.loadRedisDataIntoLocalCache(player);
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    UUID uuid = event.getPlayer().getUniqueId();
    playerCache
        .get(uuid)
        .ifPresent(
            questPlayerData -> {
              redisPlayerData.updateRedisFromLocalCache(uuid, questPlayerData);
              playerCache.remove(uuid);
            });
  }

  private void removeQuestFromPlayerIfNoLongerExists(Player player) {
    playerCache
        .get(player.getUniqueId())
        .ifPresent(
            playerData -> {
              boolean questWasRemoved =
                  questCache.getQuest(playerData.getCurrentQuestId()).isEmpty()
                      && !playerData.getCurrentQuestId().isEmpty();
              if (questWasRemoved) {
                playerData.optOutOfCurrentQuestId();
                player.sendMessage(
                    UtilChat.colorize("&cThe quest you were in has been deleted by an admin!"));
              }
            });
  }
}
