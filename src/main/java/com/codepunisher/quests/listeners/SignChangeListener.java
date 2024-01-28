package com.codepunisher.quests.listeners;

import com.codepunisher.quests.cache.QuestSignCache;
import com.codepunisher.quests.config.QuestsConfig;
import com.codepunisher.quests.config.QuestsLanguageConfig;
import com.codepunisher.quests.database.QuestSignDatabase;
import com.codepunisher.quests.util.UtilChat;
import lombok.AllArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

@AllArgsConstructor
public class SignChangeListener implements Listener {
  private final QuestsConfig questsConfig;
  private final QuestSignDatabase signDatabase;
  private final QuestSignCache signCache;

  @EventHandler
  public void onSignUpdate(SignChangeEvent event) {
    Player player = event.getPlayer();
    if (!player.hasPermission(questsConfig.getSignCreatePermission())) {
      return;
    }

    QuestsLanguageConfig lang = questsConfig.getLang(player);
    if (event.getLines()[0].equalsIgnoreCase(lang.getQuestSignConfiguration())) {
      Location location = event.getBlock().getLocation();
      signDatabase.insert(location);
      signCache.add(location);
      player.sendMessage(UtilChat.colorize(lang.getQuestSignUpdate()));
    }
  }
}
