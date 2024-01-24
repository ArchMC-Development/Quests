package com.codepunisher.quests.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;


@Getter
@AllArgsConstructor
public enum QuestType {
  BLOCK_BREAK(Material.COBBLESTONE) {
    @Override
    public Class<?> getAssociationFromInput(String input) {
      return Material.valueOf(input.toUpperCase()).getClass();
    }
  };

  private final Material defaultDisplay;

  public abstract Class<?> getAssociationFromInput(String input);
}
