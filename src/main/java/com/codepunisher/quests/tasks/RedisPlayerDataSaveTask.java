package com.codepunisher.quests.tasks;

import com.codepunisher.quests.cache.QuestPlayerCache;
import com.codepunisher.quests.redis.RedisPlayerData;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RedisPlayerDataSaveTask implements Runnable {
    private final QuestPlayerCache playerCache;
    private final RedisPlayerData redisPlayerData;

    @Override
    public void run() {
        playerCache.getEntrySet().forEach(entry -> {
            redisPlayerData.updateRedisFromLocalCache(entry.getKey(), entry.getValue());
        });
    }
}
