package lol.arch.quests.menu;

import fr.mrmicky.fastinv.FastInv;
import lol.arch.quests.config.QuestsConfig;
import lol.arch.quests.models.ButtonType;
import lol.arch.quests.models.GuiItem;
import lol.arch.quests.models.gui.GuiInventory;
import lol.arch.quests.util.UtilChat;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class AbstractMenu extends FastInv {
    protected final GuiInventory guiInventory;
    private final Map<ButtonType, Consumer<InventoryClickEvent>> clickHandlers = new HashMap<>();
    private final boolean playOpenSound;

    public AbstractMenu(Player player, QuestsConfig config, GuiInventory guiInventory, boolean... optionalPlayOpenSound) {
        super(guiInventory.getSize(), UtilChat.colorize(guiInventory.getTitle()));
        this.guiInventory = guiInventory;
        this.playOpenSound = optionalPlayOpenSound == null || optionalPlayOpenSound.length == 0;

        // Filling background (if the item matches the inventory type)
        config.getLang(player).getBackGroundItems().stream()
                .filter(guiItem -> guiItem.getGuiTypes().contains(guiInventory.getGuiType()))
                .forEach(
                        guiItem -> {
                            guiItem
                                    .getSlots()
                                    .forEach(
                                            slot -> {
                                                setItem(
                                                        slot,
                                                        guiItem.getItemStack(),
                                                        (event) -> {
                                                            handleGenericGuiItemDefaults(player, guiItem);
                                                        });
                                            });
                        });

        // Filling items from the gui inventory
        if (guiInventory.getGuiItems() == null) return;
        for (GuiItem guiItem : guiInventory.getGuiItems()) {
            for (int slot : guiItem.getSlots()) {
                setItem(
                        slot,
                        guiItem.getItemStack(),
                        (event) -> {
                            handleGenericGuiItemDefaults(player, guiItem);

                            // Custom click handler (if present)
                            Optional.ofNullable(clickHandlers.get(guiItem.getButtonType()))
                                    .ifPresent(
                                            eventConsumer -> {
                                                eventConsumer.accept(event);
                                            });
                        });
            }
        }
    }

    @Override
    public void open(Player player) {
        super.open(player);

        // Gui open sound
        if (guiInventory.getOpenSound() != null && playOpenSound) {
            player.playSound(player.getLocation(), guiInventory.getOpenSound(), 0.35f, 1.25f);
        }
    }

    protected void addClickHandler(
            ButtonType buttonType, Consumer<InventoryClickEvent> clickHandler) {
        this.clickHandlers.put(buttonType, clickHandler);
    }

    private void handleGenericGuiItemDefaults(Player player, GuiItem guiItem) {
        if (guiItem.getClickSound() != null) {
            player.playSound(player.getLocation(), guiItem.getClickSound(), 0.35f, 1.25f);
        }

        if (guiItem.isCloseOnClick()) {
            player.closeInventory();
        }
    }
}
