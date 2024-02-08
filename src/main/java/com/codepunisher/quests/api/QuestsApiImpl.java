package com.codepunisher.quests.api;

import com.codepunisher.quests.cache.QuestPlayerCache;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class QuestsApiImpl implements QuestsAPI {
  private final QuestPlayerCache playerCache;

  @Override
  public int getTotalCompletedQuests(Player player) {
    return playerCache.getPlayerStorageData(player.getUniqueId()).getCompletedQuests();
  }
}
