package com.codepunisher.quests.database;

import com.codepunisher.quests.models.Quest;

public interface QuestDatabase {
    void createQuestTable();

    void insert(Quest quest);
}
