package com.codepunisher.quests.models.gui;

import com.codepunisher.quests.models.GuiItem;
import com.codepunisher.quests.models.GuiType;
import lombok.Getter;
import org.bukkit.Sound;

import javax.annotation.Nullable;
import java.util.List;

@Getter
public class GuiInventory {
  private final int size;
  private final String title;
  private final GuiType guiType;

  @Nullable private List<GuiItem> guiItems;

  @Nullable private Sound openSound;

  public GuiInventory(@Nullable List<GuiItem> guiItems, int size, String title, String guiType) {
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

  public GuiInventory(int size, String title, GuiType guiType, String openSound) {
    this.size = size;
    this.title = title;
    this.guiType = guiType;
    this.openSound = Sound.valueOf(openSound.toUpperCase());
  }
}
