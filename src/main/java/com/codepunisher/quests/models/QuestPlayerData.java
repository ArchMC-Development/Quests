package com.codepunisher.quests.models;

import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

public class QuestPlayerData {
    @Setter
    private String currentQuestId;
    @Getter
    private int currentQuestProgress;

    public Optional<String> getCurrentQuestId() {
        return Optional.ofNullable(currentQuestId);
    }

    public void incrementQuestProgress(int amount) {
        this.currentQuestProgress += amount;
    }
}
