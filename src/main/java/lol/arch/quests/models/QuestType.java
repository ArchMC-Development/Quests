package lol.arch.quests.models;

import io.papermc.paper.entity.Shearable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Tameable;
import org.bukkit.potion.PotionType;

import java.util.Objects;

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
    MOB_KILLER(Material.DIAMOND_SWORD) {
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
    DAMAGE_BLOCK(Material.IRON_PICKAXE) {
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
    },
    SHEAR_ENTITY(Material.SHEARS) {
        @Override
        @SuppressWarnings("unchecked")
        public <T> T getAssociationFromInput(String input) {
            EntityType entityType = EntityType.valueOf(input.toUpperCase());
            if (!entityType.getEntityClass().isInstance(Shearable.class)) return null;
            return (T) entityType;
        }

        @Override
        public <T> String getInputFromAssociatedObject(T object) {
            if (object instanceof EntityType entityType) {
                assert entityType.getEntityClass() != null;
                if (!entityType.getEntityClass().isInstance(Shearable.class)) return null;
                return entityType.name();
            }
            return null;
        }
    },
    TAME_MOB(Material.WHEAT) {
        @Override
        @SuppressWarnings("unchecked")
        public <T> T getAssociationFromInput(String input) {
            EntityType entityType = EntityType.valueOf(input.toUpperCase());
            if (!entityType.getEntityClass().isInstance(Tameable.class)) return null;
            return (T) entityType;
        }

        @Override
        public <T> String getInputFromAssociatedObject(T object) {
            if (object instanceof EntityType entityType) {
                assert entityType.getEntityClass() != null;
                if (!entityType.getEntityClass().isInstance(Tameable.class)) return null;
                return entityType.name();
            }
            return null;
        }
    },
    FILL_BUCKET(Material.MILK_BUCKET) {
        @Override
        @SuppressWarnings("unchecked")
        public <T> T getAssociationFromInput(String input) {
            Material material = Material.valueOf(input.toUpperCase());
            if (!material.name().endsWith("_BUCKET")) return null;
            return (T) material;
        }

        @Override
        public <T> String getInputFromAssociatedObject(T object) {
            if (object instanceof Material material) {
                if (!material.name().endsWith("_BUCKET")) return null;
                return material.name();
            }
            return null;
        }
    },
    CONSUME_ITEM(Material.APPLE) {
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
    BREW_POTION(Material.POTION) {
        @Override
        @SuppressWarnings("unchecked")
        public <T> T getAssociationFromInput(String input) {
            return (T) PotionType.valueOf(input.toUpperCase());
        }

        @Override
        public <T> String getInputFromAssociatedObject(T object) {
            if (object instanceof PotionType potion) {
                return potion.name();
            }
            return null;
        }
    },
    ENCHANT_ITEM(Material.ENCHANTING_TABLE) {
        @Override
        @SuppressWarnings("unchecked")
        public <T> T getAssociationFromInput(String input) {
            return (T) Registry.ENCHANTMENT.get(Objects.requireNonNull(NamespacedKey.fromString(input)));
        }

        @Override
        public <T> String getInputFromAssociatedObject(T object) {
            if (object instanceof Enchantment enchantment) {
                return enchantment.getKey().asString();
            }
            return null;
        }
    },
    SMELT_ITEM(Material.FURNACE) {
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
    CATCH_FISH(Material.FISHING_ROD) {
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
    }
    ;

    private final Material defaultDisplay;

    public abstract <T> T getAssociationFromInput(String input);

    public abstract <T> String getInputFromAssociatedObject(T object);
}