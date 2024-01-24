package com.codepunisher.quests.commands.subcommands;

import com.codepunisher.quests.cache.QuestCache;
import com.codepunisher.quests.cache.QuestPlayerCache;
import com.codepunisher.quests.commands.QuestsSubCommand;
import com.codepunisher.quests.commands.lib.CommandCall;
import lombok.AllArgsConstructor;
import me.drepic.proton.common.ProtonManager;
import me.drepic.proton.common.message.MessageHandler;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

@AllArgsConstructor
public class QuestResetDailySubCommand implements QuestsSubCommand {
  private static final JedisPool jedisPool = new JedisPool("localhost", 6379);
  private static final String JEDIS_DAILY_QUEST_CYCLE = "daily_quest_cycle";
  private static final String JEDIS_PLAYER_DATA = "quest_player_data";
  private final JavaPlugin plugin;
  private final QuestCache questCache;
  private final QuestPlayerCache playerCache;
  private final ProtonManager proton;

  @Override
  public Consumer<CommandCall> getCommandCallConsumer() {
    return call -> {
      // Clearing
      questCache.removeAllActiveQuests();
      playerCache.removeAll();
      clearRedisData();

      // Re-instantiating
      randomizeValuesForEachQuestAndCacheAsActive();
      addLocalActiveQuestsToRedis();

      // Updating caches across other server instances running this plugin
      proton.broadcast("quest-plugin", "quest-reset", true);
      call.asPlayer().sendMessage("Reset and refreshed successfully!");
    };
  }

  @MessageHandler(namespace = "quest-plugin", subject = "quest-reset")
  public void onCacheUpdateReceive(boolean reset) {
    if (!reset) {
      return;
    }

    questCache.removeAllActiveQuests();
    playerCache.removeAll();
    randomizeValuesForEachQuestAndCacheAsActive();
  }

  private void clearRedisData() {
    plugin
        .getServer()
        .getScheduler()
        .runTaskAsynchronously(
            plugin,
            () -> {
              try (Jedis jedis = jedisPool.getResource()) {
                jedis.del(JEDIS_DAILY_QUEST_CYCLE);
                jedis.del(JEDIS_PLAYER_DATA);
              } catch (Exception e) {
                plugin
                    .getLogger()
                    .severe(
                        String.format(
                            "Could not remove %s or %s from redis %s",
                            JEDIS_DAILY_QUEST_CYCLE, JEDIS_PLAYER_DATA, e.getMessage()));
              }
            });
  }

  private void randomizeValuesForEachQuestAndCacheAsActive() {
    Random random = new Random();
    questCache
        .getQuests()
        .forEach(
            quest -> {
              int randomRequirement =
                  random.nextInt((quest.getMax() - quest.getMin()) + 1) + quest.getMin();
              questCache.addActiveQuest(quest.getId(), randomRequirement);
            });
  }

  private void addLocalActiveQuestsToRedis() {
    plugin
        .getServer()
        .getScheduler()
        .runTaskAsynchronously(
            plugin,
            () -> {
              try (Jedis jedis = jedisPool.getResource()) {
                Map<String, String> questData = new HashMap<>();
                for (Map.Entry<String, Integer> entry : questCache.getActiveQuestsEntrySet()) {
                  questData.put(entry.getKey(), String.valueOf(entry.getValue()));
                }
                jedis.hset(JEDIS_DAILY_QUEST_CYCLE, questData);
              } catch (Exception e) {
                plugin
                    .getLogger()
                    .severe(
                        String.format("Could not add the daily cycle to redis %s", e.getMessage()));
              }
            });
  }
}
