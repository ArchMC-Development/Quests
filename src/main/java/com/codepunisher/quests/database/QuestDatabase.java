package com.codepunisher.quests.database;

import com.codepunisher.quests.models.Quest;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface QuestDatabase {
    void createQuestTable();

    void insert(Quest quest);

    CompletableFuture<List<Quest>> getAllQuests();
}
