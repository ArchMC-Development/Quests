package lol.arch.quests.menu;

import lol.arch.quests.config.QuestsConfig;
import lol.arch.quests.models.ButtonType;
import lol.arch.quests.models.gui.GuiInventory;
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
