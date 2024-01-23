package com.codepunisher.quests.cache;

import com.codepunisher.quests.models.Quest;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class QuestCache {
    private final Map<String, Quest> questMap = new HashMap<>();

    public void add(Quest quest) {
        questMap.put(quest.getId(), quest);
    }

    public Optional<Quest> getQuest(String id) {
        return Optional.ofNullable(questMap.get(id));
    }

    public void remove(String id) {
        questMap.remove(id );
    }
}
