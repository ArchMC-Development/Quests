package com.codepunisher.quests.redis;

import com.codepunisher.quests.models.QuestPlayerData;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface RedisPlayerData {
  /** Clears everything regarding player data in redis completely */
  void clear();

  /** Loads player data from redis into the in memory local cache */
  void loadRedisDataIntoLocalCache(Player player);

  /** Pulls most recent data from cache and updates the player data in redis */
  void updateRedisFromLocalCache(UUID uuid, QuestPlayerData questPlayerData);
}
