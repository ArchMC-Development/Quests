package com.codepunisher.quests.listeners;

import com.codepunisher.quests.cache.QuestPlayerCache;
import com.codepunisher.quests.redis.RedisPlayerData;
import lombok.AllArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

@AllArgsConstructor
public class PlayerJoinLeaveListener implements Listener {
    private final QuestPlayerCache playerCache;
    private final RedisPlayerData redisPlayerData;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        redisPlayerData.loadRedisDataIntoLocalCache(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        playerCache.get(uuid).ifPresent(questPlayerData -> {
            redisPlayerData.updateRedisFromLocalCache(uuid, questPlayerData);
            playerCache.remove(uuid);
        });
    }
}
