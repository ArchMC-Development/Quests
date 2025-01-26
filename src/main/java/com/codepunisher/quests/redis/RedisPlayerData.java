package com.codepunisher.quests.redis;

import com.codepunisher.quests.models.ActiveQuestPlayerData;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface RedisPlayerData {
    /**
     * Clears everything regarding player data in redis completely
     */
    void clear();

    /**
     * Clear specific player from redis
     */
    void clear(UUID uuid);

    /**
     * Loads player data from redis into the in memory local cache
     */
    CompletableFuture<Void> loadRedisDataIntoLocalCache(Player player);

    /**
     * Pulls most recent data from cache and updates the player data in redis
     */
    void updateRedisFromLocalCache(UUID uuid, ActiveQuestPlayerData activeQuestPlayerData);
}
