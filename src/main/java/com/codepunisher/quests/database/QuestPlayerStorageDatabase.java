package com.codepunisher.quests.database;

import com.codepunisher.quests.models.PlayerStorageData;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface QuestPlayerStorageDatabase {
    void createTable();

    void insert(UUID uuid, PlayerStorageData storageData);

    CompletableFuture<Optional<PlayerStorageData>> read(UUID uuid);
}
