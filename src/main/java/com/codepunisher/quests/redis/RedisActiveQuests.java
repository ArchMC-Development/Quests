package com.codepunisher.quests.redis;

import com.codepunisher.quests.models.Quest;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface RedisActiveQuests {
    void clear();

    void removeQuest(Quest quest);

    void addLocalCacheToRedis();

    CompletableFuture<Map<String, Integer>> getDailyQuests();
}
