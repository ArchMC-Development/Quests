package com.codepunisher.quests.models;

import com.codepunisher.quests.util.UtilChat;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class GuiItem {
  private final ItemStack itemStack;
  private final Set<GuiType> guiTypes;
  private final List<Integer> slots;
  private final ButtonType buttonType;
  private final boolean closeOnClick;

  @Nullable private Sound clickSound;

  public GuiItem(
      Material material,
      String name,
      List<String> lore,
      List<String> menuTypes,
      List<Integer> slots,
      String buttonType,
      boolean closeOnClick) {
    this.itemStack = convertItemStack(material, name, lore);
    this.guiTypes =
        menuTypes.stream()
            .map(type -> GuiType.valueOf(type.toUpperCase()))
            .collect(Collectors.toSet());
    this.slots = slots;
    this.buttonType = ButtonType.valueOf(buttonType.toUpperCase());
    this.closeOnClick = closeOnClick;
  }

  public GuiItem(
      Material material,
      String name,
      List<String> lore,
      List<String> menuTypes,
      List<Integer> slots,
      String buttonType,
      boolean closeOnClick,
      String clickSound) {
    this(material, name, lore, menuTypes, slots, buttonType, closeOnClick);
    this.clickSound = Sound.valueOf(clickSound.toUpperCase());
  }

  private ItemStack convertItemStack(Material material, String name, List<String> lore) {
    ItemStack item = new ItemStack(material);
    ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      meta.setDisplayName(UtilChat.colorize(name));
      meta.setLore(lore.stream().map(UtilChat::colorize).collect(Collectors.toList()));
      meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
      item.setItemMeta(meta);
    }
    return item;
  }
}
