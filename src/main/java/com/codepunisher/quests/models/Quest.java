package com.codepunisher.quests.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

@Getter
@AllArgsConstructor
public class Quest {
    private final String id;
    private final String displayName;
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

        if (associatedObject instanceof EntityType entityType) {
            return Material.valueOf(getMobName(entityType) + "_SPAWN_EGG");
        }

        return questType.getDefaultDisplay();
    }

    private String getMobName(EntityType entityType) {
        if (entityType == EntityType.VILLAGER) {
            return "VILLAGER";
        }

        return entityType.name().replace(" ", "_").toUpperCase();
    }
}
