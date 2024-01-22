package com.codepunisher.quests.models;

import lombok.Getter;
import org.bukkit.Sound;

import javax.annotation.Nullable;
import java.util.List;

@Getter
public class GuiInventory {
  private final List<GuiItem> guiItems;
  private final int size;
  private final String title;
  private final GuiType guiType;

  @Nullable private Sound openSound;

  public GuiInventory(List<GuiItem> guiItems, int size, String title, String guiType) {
    this.guiItems = guiItems;
    this.size = size;
    this.title = title;
    this.guiType = GuiType.valueOf(guiType.toUpperCase());
  }

  public GuiInventory(
      List<GuiItem> guiItems, int size, String title, String guiType, String openSound) {
    this(guiItems, size, title, guiType);
    this.openSound = Sound.valueOf(openSound.toUpperCase());
  }
}
