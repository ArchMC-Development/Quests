package com.codepunisher.quests.menu;

import com.codepunisher.quests.util.ItemBuilder;
import fr.mrmicky.fastinv.FastInv;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class AbstractAreYouSureMenu extends FastInv {
    @Nullable
    private FastInv fastInv;

    public AbstractAreYouSureMenu(Consumer<Player> playerConsumer, String... warningDescription) {
        super(45, "Are you sure?");
        init(playerConsumer, warningDescription);
    }

    public AbstractAreYouSureMenu(Consumer<Player> playerConsumer, @Nullable FastInv backMenu, String... warningDescription) {
        super(45, "Are you sure?");
        this.fastInv = backMenu;
        init(playerConsumer, warningDescription);
        setBackButton(this, backMenu);
    }

    private void init(Consumer<Player> playerConsumer, String... warningDescription) {
        setItems(getBorders(), new ItemStack(Material.BLACK_STAINED_GLASS_PANE));

        // Warning
        setItem(4, ItemBuilder.of(Material.OAK_SIGN).name("&c&lWarning").lore(warningDescription).build());

        // Confirmation
        setItem(21, ItemBuilder.of(Material.EMERALD_BLOCK).name("&a&lYes").lore("&8➜ Click to confirm").build(), (event -> {
            Player player = (Player) event.getWhoClicked();
            player.closeInventory();
            playerConsumer.accept(player);
        }));

        // Deny
        setItem(23, ItemBuilder.of(Material.REDSTONE_BLOCK).name("&c&lNo").lore("&8➜ Click to deny").build(), (event) -> {
            Player player = (Player) event.getWhoClicked();

            if (fastInv != null) {
                fastInv.open(player);
                return;
            }

            // Only closing if not re-opening another menu
            player.closeInventory();
        });

        setCloseButton(this);
    }
  private void setCloseButton(FastInv fastInv) {
    fastInv.setItem(
        getBottomMiddleSlot(fastInv),
        ItemBuilder.of(Material.BARRIER).name("&c&lClose").lore("&8➜ Click to close").build(),
        (event -> {
          Player player = (Player) event.getWhoClicked();
          player.closeInventory();
        }));
  }

  private void setBackButton(FastInv current, FastInv back) {
    int size = current.getInventory().getSize();
    int bottomBackLeftPosition = size - 9;
    current.setItem(
        bottomBackLeftPosition,
        ItemBuilder.of(Material.OAK_FENCE_GATE)
            .name("&f&lBack")
            .lore("&8➜ Click to go back")
            .build(),
        (event -> {
          Player player = (Player) event.getWhoClicked();
          back.open(player);
        }));
  }

  private int getBottomMiddleSlot(FastInv fastInv) {
    return fastInv.getInventory().getSize() - 5;
  }
}
