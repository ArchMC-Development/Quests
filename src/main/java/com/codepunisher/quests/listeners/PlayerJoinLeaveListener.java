package com.codepunisher.quests.listeners;

import com.codepunisher.quests.cache.QuestCache;
import com.codepunisher.quests.cache.QuestPlayerCache;
import com.codepunisher.quests.models.ActiveQuestPlayerData;
import com.codepunisher.quests.redis.RedisPlayerData;
import com.codepunisher.quests.util.UtilChat;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

@AllArgsConstructor
public class PlayerJoinLeaveListener implements Listener {
  private final JavaPlugin plugin;
  private final QuestCache questCache;
  private final QuestPlayerCache playerCache;
  private final RedisPlayerData redisPlayerData;

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    redisPlayerData
        .loadRedisDataIntoLocalCache(player)
        .thenRun(
            () -> {
              plugin
                  .getServer()
                  .getScheduler()
                  .runTask(
                      plugin,
                      () -> {
                        playerCache
                            .getActiveQuestPlayerData(player.getUniqueId())
                            .ifPresent(
                                playerData -> {
                                  removeQuestFromPlayerIfNoLongerExists(player, playerData);
                                  sendCurrentQuestData(player, playerData);
                                });
                      });
            });
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    UUID uuid = event.getPlayer().getUniqueId();
    playerCache
        .getActiveQuestPlayerData(uuid)
        .ifPresent(
            questPlayerData -> {
              redisPlayerData.updateRedisFromLocalCache(uuid, questPlayerData);
              playerCache.removeActiveQuestUser(uuid);
            });
  }

  private void removeQuestFromPlayerIfNoLongerExists(Player player, ActiveQuestPlayerData playerData) {
    boolean questWasRemoved =
        questCache.getQuest(playerData.getCurrentQuestId()).isEmpty()
            && !playerData.getCurrentQuestId().isEmpty();
    if (questWasRemoved) {
      playerData.optOutOfCurrentQuestId();
      player.sendMessage(
          UtilChat.colorize("&cThe quest you were in has been deleted by an admin!"));
    }
  }

  private void sendCurrentQuestData(Player player, ActiveQuestPlayerData playerData) {
    questCache
        .getQuest(playerData.getCurrentQuestId())
        .ifPresent(
            quest -> {
              player.sendMessage(UtilChat.colorize("&aYour current quest: " + quest.getId()));
            });
  }
}
