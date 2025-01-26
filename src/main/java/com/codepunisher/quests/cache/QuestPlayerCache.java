package com.codepunisher.quests.cache;

import com.codepunisher.quests.models.ActiveQuestPlayerData;
import com.codepunisher.quests.models.PlayerStorageData;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class QuestPlayerCache {
    private final Map<UUID, ActiveQuestPlayerData> activeQuestsPlayerDataMap = new ConcurrentHashMap<>();
    private final Map<UUID, PlayerStorageData> playerStorageDataMap = new ConcurrentHashMap<>();

    public void addActiveQuestUser(UUID uuid, ActiveQuestPlayerData playerData) {
        activeQuestsPlayerDataMap.put(uuid, playerData);
    }

    public void addPlayerStorage(UUID uuid, PlayerStorageData storageData) {
        playerStorageDataMap.put(uuid, storageData);
    }

    public void removeActiveQuestUser(UUID uuid) {
        activeQuestsPlayerDataMap.remove(uuid);
    }

    public void removePlayerStorage(UUID uuid) {
        playerStorageDataMap.remove(uuid);
    }

    public void removeAllActiveQuestUsers() {
        activeQuestsPlayerDataMap.clear();
    }

    public Optional<ActiveQuestPlayerData> getActiveQuestPlayerData(UUID uuid) {
        return Optional.ofNullable(activeQuestsPlayerDataMap.get(uuid));
    }

    public PlayerStorageData getPlayerStorageData(UUID uuid) {
        return playerStorageDataMap.getOrDefault(uuid, new PlayerStorageData());
    }

    public Set<Map.Entry<UUID, ActiveQuestPlayerData>> getActiveQuestEntrySet() {
        return activeQuestsPlayerDataMap.entrySet();
    }
}
