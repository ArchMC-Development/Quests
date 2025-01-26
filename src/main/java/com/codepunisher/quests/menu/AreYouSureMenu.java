package com.codepunisher.quests.menu;

import com.codepunisher.quests.config.QuestsConfig;
import com.codepunisher.quests.models.ButtonType;
import com.codepunisher.quests.models.gui.GuiInventory;
import org.bukkit.entity.Player;

public class AreYouSureMenu extends AbstractMenu {
    public AreYouSureMenu(
            Player player,
            QuestsConfig config,
            GuiInventory areYouSureInventory,
            Runnable yes,
            Runnable no) {
        super(player, config, areYouSureInventory);

        addClickHandler(
                ButtonType.ARE_YOU_SURE_YES,
                (event) -> {
                    yes.run();
                });

        addClickHandler(
                ButtonType.ARE_YOU_SURE_NO,
                (event) -> {
                    no.run();
                });
    }
}
