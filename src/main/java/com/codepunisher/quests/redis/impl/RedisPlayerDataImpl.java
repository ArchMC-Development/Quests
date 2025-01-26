package com.codepunisher.quests.redis.impl;

import com.codepunisher.quests.cache.QuestPlayerCache;
import com.codepunisher.quests.models.ActiveQuestPlayerData;
import com.codepunisher.quests.redis.RedisPlayerData;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
public class RedisPlayerDataImpl implements RedisPlayerData {
    private static final String JEDIS_PLAYER_DATA = "daily_player_data";
    private final JavaPlugin plugin;
    private final QuestPlayerCache playerCache;
    private final JedisPool jedisPool;
    private final Gson gson;

    @Override
    public void clear() {
        plugin
                .getServer()
                .getScheduler()
                .runTaskAsynchronously(
                        plugin,
                        () -> {
                            try (Jedis jedis = jedisPool.getResource()) {
                                Set<String> keysToDelete = jedis.keys(JEDIS_PLAYER_DATA + ":*");
                                plugin.getLogger().info("Redis player data has been cleared successfully!");
                                if (!keysToDelete.isEmpty()) {
                                    jedis.del(keysToDelete.toArray(new String[0]));
                                }
                            } catch (Exception e) {
                                plugin
                                        .getLogger()
                                        .severe(
                                                String.format(
                                                        "Could not remove %s from redis %s",
                                                        JEDIS_PLAYER_DATA, e.getMessage()));
                            }
                        });
    }

    @Override
    public void clear(UUID uuid) {
        plugin
                .getServer()
                .getScheduler()
                .runTaskAsynchronously(
                        plugin,
                        () -> {
                            try (Jedis jedis = jedisPool.getResource()) {
                                jedis.del(getKey(uuid));
                                plugin.getLogger().info("Redis player data cleared: " + uuid);
                            } catch (Exception e) {
                                plugin
                                        .getLogger()
                                        .severe(
                                                String.format("Could not remove %s from redis %s", uuid, e.getMessage()));
                            }
                        });
    }

    @Override
    public CompletableFuture<Void> loadRedisDataIntoLocalCache(Player player) {
        return CompletableFuture.runAsync(
                () -> {
                    try (Jedis jedis = jedisPool.getResource()) {
                        UUID uuid = player.getUniqueId();
                        String redisValue = jedis.get(getKey(uuid));
                        if (redisValue == null) {
                            return;
                        }

                        ActiveQuestPlayerData playerData = gson.fromJson(redisValue, ActiveQuestPlayerData.class);
                        playerCache.addActiveQuestUser(uuid, playerData);
                    } catch (Exception e) {
                        plugin
                                .getLogger()
                                .severe(
                                        String.format(
                                                "Error pulling data from Redis for %s %s: ",
                                                player.getName(), e.getMessage()));
                    }
                });
    }

    @Override
    public void updateRedisFromLocalCache(UUID uuid, ActiveQuestPlayerData activeQuestPlayerData) {
        CompletableFuture.runAsync(
                () -> {
                    try (Jedis jedis = jedisPool.getResource()) {
                        jedis.set(getKey(uuid), gson.toJson(activeQuestPlayerData));
                    } catch (Exception e) {
                        plugin
                                .getLogger()
                                .severe(
                                        String.format(
                                                "Error storing player data into Redis for %s %s: ", uuid, e.getMessage()));
                    }
                });
    }

    private String getKey(UUID uuid) {
        return JEDIS_PLAYER_DATA + ":" + uuid;
    }
}
