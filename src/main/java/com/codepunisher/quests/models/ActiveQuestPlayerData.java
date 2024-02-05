package com.codepunisher.quests.models;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class ActiveQuestPlayerData {
  private final Set<String> completedDailyQuests = new HashSet<>();

  @Setter private String currentQuestId = "";
  private int currentQuestProgress;

  public void incrementQuestProgress(int amount) {
    this.currentQuestProgress += amount;
  }

  public void setCurrentQuestIdAsCompleted() {
    completedDailyQuests.add(String.copyValueOf(currentQuestId.toCharArray()));
    optOutOfCurrentQuestId();
  }

  public void optOutOfCurrentQuestId() {
    currentQuestId = "";
    currentQuestProgress = 0;
  }

  public int getCurrentQuestProgress(Quest quest) {
    if (quest.getId().equalsIgnoreCase(currentQuestId)) {
      return currentQuestProgress;
    }

    return 0;
  }
}
