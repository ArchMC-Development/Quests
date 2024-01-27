package com.codepunisher.quests.database;

import org.bukkit.Location;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface QuestSignDatabase {
    void createSignTable();

    void insert(Location location);

    void delete(Location location);

    CompletableFuture<List<Location>> getSignLocations();
}
