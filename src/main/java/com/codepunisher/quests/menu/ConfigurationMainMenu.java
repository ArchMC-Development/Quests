package com.codepunisher.quests.menu;

import com.codepunisher.quests.config.QuestsConfig;
import org.bukkit.entity.Player;

public class ConfigurationMainMenu extends AbstractMenu {
    public ConfigurationMainMenu(Player player, QuestsConfig questsConfig) {
        super(player, questsConfig, questsConfig.getConfigurationInventory());
    }
}
