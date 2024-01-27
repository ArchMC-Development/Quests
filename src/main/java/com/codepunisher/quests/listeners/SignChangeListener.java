package com.codepunisher.quests.listeners;

import com.codepunisher.quests.cache.QuestSignCache;
import com.codepunisher.quests.database.QuestSignDatabase;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

@AllArgsConstructor
public class SignChangeListener implements Listener {
  private final QuestSignDatabase signDatabase;
  private final QuestSignCache signCache;

  @EventHandler
  public void onSignUpdate(SignChangeEvent event) {
    if (!event.getPlayer().hasPermission("quests.sign.admin")) {
      return;
    }

    if (event.getLines()[0].equalsIgnoreCase("[quest]")) {
      signDatabase.insert(event.getBlock().getLocation());
      signCache.add(event.getBlock().getLocation());
      event.getPlayer().sendMessage(ChatColor.GREEN + "Successfully updated!");
    }
  }
}
