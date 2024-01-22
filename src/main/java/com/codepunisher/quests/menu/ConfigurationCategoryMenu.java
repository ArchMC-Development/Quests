package com.codepunisher.quests.menu;

import com.codepunisher.quests.config.QuestsConfig;
import com.codepunisher.quests.models.ButtonType;
import com.codepunisher.quests.util.UtilChat;
import org.bukkit.entity.Player;

public class ConfigurationCategoryMenu extends AbstractMenu {
  public ConfigurationCategoryMenu(Player player, QuestsConfig config) {
    super(player, config, config.getConfigurationEditCategoryInventory());
    addClickHandler(
        ButtonType.CATEGORY_ADD,
        (event) -> {
          player.sendMessage(UtilChat.colorize("&aThis totally working bro!"));
        });
  }
}
