package com.codepunisher.quests.cache;

import com.codepunisher.quests.models.QuestPlayerData;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class QuestPlayerCache {
  private final Map<UUID, QuestPlayerData> playerDataMap = new ConcurrentHashMap<>();

    public void add(UUID uuid, QuestPlayerData playerData) {
        playerDataMap.put(uuid, playerData);
    }

    public void remove(UUID uuid) {
        playerDataMap.remove(uuid);
    }

    public void removeAll() {
        playerDataMap.clear();
    }

    public Optional<QuestPlayerData> get(UUID uuid) {
        return Optional.ofNullable(playerDataMap.get(uuid));
    }

    public Set<Map.Entry<UUID, QuestPlayerData>> getEntrySet() {
        return playerDataMap.entrySet();
    }
}
