package com.codepunisher.quests.models;

import lombok.Getter;
import lombok.Setter;

@Getter
public class QuestPlayerData {
    @Setter
    private String currentQuestId = "";
    private int currentQuestProgress;

    public void incrementQuestProgress(int amount) {
        this.currentQuestProgress += amount;
    }
}
