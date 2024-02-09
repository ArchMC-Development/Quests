package com.codepunisher.quests.listeners;

import com.codepunisher.quests.cache.QuestCache;
import com.codepunisher.quests.cache.QuestPlayerCache;
import com.codepunisher.quests.config.QuestsConfig;
import com.codepunisher.quests.database.QuestPlayerStorageDatabase;
import com.codepunisher.quests.models.ActiveQuestPlayerData;
import com.codepunisher.quests.models.PlayerStorageData;
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
  private final QuestsConfig questsConfig;
  private final QuestCache questCache;
  private final QuestPlayerCache playerCache;
  private final RedisPlayerData redisPlayerData;
  private final QuestPlayerStorageDatabase playerStorageDatabase;

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    UUID uuid = player.getUniqueId();
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
                            .getActiveQuestPlayerData(uuid)
                            .ifPresent(
                                playerData -> {
                                  removeQuestFromPlayerIfNoLongerExists(player, playerData);
                                });
                      });
            });

    playerStorageDatabase
        .read(uuid)
        .thenAccept(
            playerStorageData -> {
              playerStorageData.ifPresentOrElse(
                  storageData -> {
                    playerCache.addPlayerStorage(uuid, storageData);
                  },
                  () -> {
                    playerCache.addPlayerStorage(uuid, new PlayerStorageData());
                  });
            });
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    UUID uuid = event.getPlayer().getUniqueId();
    playerCache.removePlayerStorage(uuid);
    playerCache
        .getActiveQuestPlayerData(uuid)
        .ifPresent(
            questPlayerData -> {
              redisPlayerData.updateRedisFromLocalCache(uuid, questPlayerData);
              playerCache.removeActiveQuestUser(uuid);
            });
  }

  private void removeQuestFromPlayerIfNoLongerExists(
      Player player, ActiveQuestPlayerData playerData) {
    boolean questWasRemoved =
        questCache.getQuest(playerData.getCurrentQuestId()).isEmpty()
            && !playerData.getCurrentQuestId().isEmpty();
    if (questWasRemoved) {
      playerData.optOutOfCurrentQuestId();
      player.sendMessage(UtilChat.colorize(questsConfig.getLang(player).getQuestDeletedByAdmin()));
    }
  }
}
