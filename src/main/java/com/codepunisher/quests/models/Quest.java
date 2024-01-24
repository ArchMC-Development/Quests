package com.codepunisher.quests.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

@Getter
@AllArgsConstructor
public class Quest {
  private final String id;
  private final QuestType questType;
  private final Object associatedObject;
  private final int min;
  private final int max;
  private final String permission;
  private final String[] rewards;

  public Material getDisplay() {
    if (associatedObject instanceof Material material) {
      return material;
    }

    return questType.getDefaultDisplay();
  }
}
