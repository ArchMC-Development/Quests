package com.codepunisher.quests.redis;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface RedisActiveQuests {
    void clear();

    void addLocalCacheToRedis();

    CompletableFuture<Map<String, Integer>> getDailyQuests();
}
