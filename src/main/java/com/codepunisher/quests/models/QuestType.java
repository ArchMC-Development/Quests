package com.codepunisher.quests.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;

@Getter
@AllArgsConstructor
public enum QuestType {
    BLOCK_BREAK(Material.COBBLESTONE) {
        @Override
        @SuppressWarnings("unchecked")
        public <T> T getAssociationFromInput(String input) {
            return (T) Material.valueOf(input.toUpperCase());
        }

        @Override
        public <T> String getInputFromAssociatedObject(T object) {
            if (object instanceof Material material) {
                return material.name();
            }
            return null;
        }
    },
    CRAFTING(Material.CRAFTING_TABLE) {
        @Override
        @SuppressWarnings("unchecked")
        public <T> T getAssociationFromInput(String input) {
            return (T) Material.valueOf(input.toUpperCase());
        }

        @Override
        public <T> String getInputFromAssociatedObject(T object) {
            if (object instanceof Material material) {
                return material.name();
            }
            return null;
        }
    },
    ENTITY_KILLER(Material.DIAMOND_SWORD) {
        @Override
        @SuppressWarnings("unchecked")
        public <T> T getAssociationFromInput(String input) {
            return (T) EntityType.valueOf(input.toUpperCase());
        }

        @Override
        public <T> String getInputFromAssociatedObject(T object) {
            if (object instanceof EntityType entityType) {
                return entityType.name();
            }
            return null;
        }
    },
    BLOCKS_PLACED(Material.OAK_PLANKS) {
        @Override
        @SuppressWarnings("unchecked")
        public <T> T getAssociationFromInput(String input) {
            return (T) Material.valueOf(input.toUpperCase());
        }

        @Override
        public <T> String getInputFromAssociatedObject(T object) {
            if (object instanceof Material material) {
                return material.name();
            }
            return null;
        }
    },
    KILL_PLAYER(Material.STONE_SWORD) {
        @Override
        @SuppressWarnings("unchecked")
        public <T> T getAssociationFromInput(String input) {
            return (T) Bukkit.getOfflinePlayer(input);
        }

        @Override
        public <T> String getInputFromAssociatedObject(T object) {
            if (object instanceof OfflinePlayer player) {
                return player.getName();
            }
            return null;
        }
    },
    BLOCKS_TRAVELLED(Material.DIAMOND_BOOTS) {
        @Override
        @SuppressWarnings("unchecked")
        public <T> T getAssociationFromInput(String input) {
            return (T) Dimension.valueOf(input.toUpperCase());
        }

        @Override
        public <T> String getInputFromAssociatedObject(T object) {
            if (object instanceof Dimension dimension) {
                return dimension.name();
            }
            return null;
        }
    };

    private final Material defaultDisplay;

    public abstract <T> T getAssociationFromInput(String input);

    public abstract <T> String getInputFromAssociatedObject(T object);
}