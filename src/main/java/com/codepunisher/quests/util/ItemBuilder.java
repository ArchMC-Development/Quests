package com.codepunisher.quests.util;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class ItemBuilder {
    private final ItemStack itemStack;
    private final ItemMeta meta;

    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.meta = itemStack.getItemMeta();
    }

    public ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
        this.meta = itemStack.getItemMeta();
    }

    public ItemBuilder name(String name) {
        meta.setDisplayName(UtilChat.colorize(name));
        return this;
    }

    public ItemBuilder amount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    // Adds to existing lore without over writing
    public ItemBuilder addLore(String... addedLore) {
        List<String> newLore = meta.getLore();
        if (newLore == null) {
            lore(addedLore);
            return this;
        }

        newLore.addAll(Arrays.asList(addedLore));

        meta.setLore(newLore.stream()
                .map(UtilChat::colorize)
                .collect(Collectors.toList()));
        return this;
    }

    public ItemBuilder addLore(List<String> addedLore) {
        List<String> newLore = meta.getLore();
        if (newLore == null) {
            lore(addedLore);
            return this;
        }

        newLore.addAll(addedLore);

        meta.setLore(newLore.stream()
                .map(UtilChat::colorize)
                .collect(Collectors.toList()));
        return this;
    }


    public ItemBuilder lore(String... lore) {
        meta.setLore(Arrays.stream(lore)
                .map(UtilChat::colorize)
                .collect(Collectors.toList()));

        return this;
    }

    public ItemBuilder lore(List<String> lore) {
        meta.setLore(lore.stream()
                .map(UtilChat::colorize)
                .collect(Collectors.toList()));

        return this;
    }

    public ItemBuilder appendLore(String... lore) {
        if (meta == null || meta.getLore() == null) {
            lore(lore);
            return this;
        }

        List<String> updatedLore = new ArrayList<>(meta.getLore());
        for (String s : lore) {
            updatedLore.add(UtilChat.colorize(s));
        }

        meta.setLore(updatedLore);
        return this;
    }

    public ItemBuilder appendLoreAtIndex(int index, List<String> lore) {
        if (meta == null) {
            return this;
        }

        List<String> updatedLore = meta.getLore() != null ?
                new ArrayList<>(meta.getLore()) : new ArrayList<>();

        int i = 0;
        for (String line : lore) {
            updatedLore.add(index + i, UtilChat.colorize(line));
            i++;
        }

        meta.setLore(updatedLore);
        return this;
    }

    public ItemBuilder removeLoreLinesFromTop(int lines) {
        if (meta == null || meta.getLore() == null || meta.getLore().isEmpty()) {
            return this;
        }

        List<String> currentLore = new ArrayList<>(meta.getLore());
        if (lines >= currentLore.size()) {
            meta.setLore(new ArrayList<>());
        } else {
            List<String> updatedLore = currentLore.subList(lines, currentLore.size());
            meta.setLore(updatedLore);
        }

        return this;
    }

    public ItemBuilder model(int model) {
        meta.setCustomModelData(model);
        return this;
    }

    public ItemBuilder type(Material type) {
        itemStack.setType(type);
        return this;
    }

    public ItemBuilder glow() {
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    public ItemBuilder glowIf(Predicate<ItemMeta> predicate) {
        if (predicate.test(meta)) {
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        return this;
    }

    // Only works on leather armor
    public ItemBuilder dye(Color color) {
        if (meta instanceof LeatherArmorMeta leatherArmorMeta) {
            leatherArmorMeta.setColor(color);
        }
        return this;
    }

    public ItemBuilder hideAttributes() {
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        return this;
    }

    public ItemBuilder hideEnchants() {
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    public ItemBuilder hideAllFlags() {
        for (ItemFlag flag : ItemFlag.values()) {
            meta.addItemFlags(flag);
        }
        return this;
    }

    public ItemBuilder removeAllAttributes() {
        for (Attribute attribute : Attribute.values()) {
            meta.removeAttributeModifier(attribute);
        }

        return this;
    }

    public ItemBuilder enchant(Enchantment enchantment, int level) {
        meta.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemBuilder meta(Consumer<ItemMeta> metaConsumer) {
        metaConsumer.accept(meta);
        return this;
    }

    public ItemStack build() {
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemBuilder of(ItemStack itemStack) {
        return new ItemBuilder(itemStack);
    }

    public static ItemBuilder of(Material material) {
        return new ItemBuilder(material);
    }
}
