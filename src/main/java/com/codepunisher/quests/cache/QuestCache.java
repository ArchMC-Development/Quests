package com.codepunisher.quests.cache;

import com.codepunisher.quests.models.Quest;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class QuestCache {
    private final Map<String, Quest> allQuestMap = new ConcurrentHashMap<>();
    private final Map<String, Integer> activeQuestsMap = new ConcurrentHashMap<>();

    public void add(Quest quest) {
        allQuestMap.put(quest.getId(), quest);
    }

    public void addActiveQuest(String id, int requirement) {
        activeQuestsMap.put(id, requirement);
    }

    public Optional<Quest> getQuest(String id) {
        return Optional.ofNullable(allQuestMap.get(id));
    }

    public Optional<Integer> getRequirement(String id) {
        return Optional.ofNullable(activeQuestsMap.get(id));
    }

    public Collection<Quest> getQuests() {
        return allQuestMap.values();
    }

    public Set<Map.Entry<String, Integer>> getActiveQuestsEntrySet() {
        return activeQuestsMap.entrySet();
    }

    public void remove(String id) {
        allQuestMap.remove(id);
    }

    public void removeActiveQuest(String id) {
        activeQuestsMap.remove(id);
    }

    public void removeAllActiveQuests() {
        activeQuestsMap.clear();
    }
}
