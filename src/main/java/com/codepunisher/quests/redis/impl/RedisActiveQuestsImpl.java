package com.codepunisher.quests.redis.impl;

import com.codepunisher.quests.cache.QuestCache;
import com.codepunisher.quests.redis.RedisActiveQuests;
import lombok.AllArgsConstructor;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@AllArgsConstructor
public class RedisActiveQuestsImpl implements RedisActiveQuests {
  private static final String JEDIS_DAILY_QUEST_CYCLE = "daily_quest_cycle";
  private final JavaPlugin plugin;
  private final QuestCache questCache;
  private final JedisPool jedisPool;

  @Override
  public void clear() {
    plugin
        .getServer()
        .getScheduler()
        .runTaskAsynchronously(
            plugin,
            () -> {
              try (Jedis jedis = jedisPool.getResource()) {
                jedis.del(JEDIS_DAILY_QUEST_CYCLE);
              } catch (Exception e) {
                plugin
                    .getLogger()
                    .severe(
                        String.format(
                            "Could not remove %s from redis %s",
                            JEDIS_DAILY_QUEST_CYCLE, e.getMessage()));
              }
            });
  }

  @Override
  public void addLocalCacheToRedis() {
    plugin
        .getServer()
        .getScheduler()
        .runTaskAsynchronously(
            plugin,
            () -> {
              try (Jedis jedis = jedisPool.getResource()) {
                for (Map.Entry<String, Integer> entry : questCache.getActiveQuestsEntrySet()) {
                  jedis.hset(
                      JEDIS_DAILY_QUEST_CYCLE, entry.getKey(), String.valueOf(entry.getValue()));
                }
              } catch (Exception e) {
                plugin
                    .getLogger()
                    .severe(
                        String.format("Could not add the daily cycle to redis %s", e.getMessage()));
              }
            });
  }

  @Override
  public CompletableFuture<Map<String, Integer>> getDailyQuests() {
    CompletableFuture<Map<String, Integer>> future = new CompletableFuture<>();
    plugin
        .getServer()
        .getScheduler()
        .runTaskAsynchronously(
            plugin,
            () -> {
              try (Jedis jedis = jedisPool.getResource()) {
                Map<String, String> questData = jedis.hgetAll(JEDIS_DAILY_QUEST_CYCLE);
                Map<String, Integer> questMap = convertValuesToIntegers(questData);
                future.complete(questMap);
              } catch (Exception e) {
                future.completeExceptionally(e);
                plugin
                    .getLogger()
                    .severe("Error when retrieving daily quests from Redis: " + e.getMessage());
              }
            });

    return future;
  }

  private Map<String, Integer> convertValuesToIntegers(Map<String, String> data) {
    return data.entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getKey, entry -> Integer.parseInt(entry.getValue())));
  }
}
