package com.codepunisher.quests.menu;

import com.codepunisher.quests.config.QuestsConfig;
import com.codepunisher.quests.models.ButtonType;
import org.bukkit.entity.Player;

public class AreYouSureDeleteMenu extends AbstractMenu {
  public AreYouSureDeleteMenu(Player player, QuestsConfig config, Runnable successRunnable) {
    super(player, config, config.getLang(player).getAreYouSureDeleteInventory());

    addClickHandler(
        ButtonType.ARE_YOU_SURE_YES,
        (event) -> {
          successRunnable.run();
          player.closeInventory();
        });

    addClickHandler(
        ButtonType.ARE_YOU_SURE_NO,
        (event) -> {
          player.closeInventory();
        });
  }
}
