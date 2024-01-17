package com.codepunisher.quests;

import com.codepunisher.quests.config.QuestsConfig;
import org.bukkit.plugin.java.JavaPlugin;

public class QuestsPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        QuestsConfig questsConfig = new QuestsConfig();
        questsConfig.reload(this);
    }
}
