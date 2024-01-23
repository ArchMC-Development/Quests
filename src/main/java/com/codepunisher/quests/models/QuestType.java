package com.codepunisher.quests.models;

import org.bukkit.Material;


public enum QuestType {
  BLOCK_BREAK {
    @Override
    public Class<?> getAssociationFromInput(String input) {
      return Material.valueOf(input.toUpperCase()).getClass();
    }
  };

  public abstract Class<?> getAssociationFromInput(String input);
}
