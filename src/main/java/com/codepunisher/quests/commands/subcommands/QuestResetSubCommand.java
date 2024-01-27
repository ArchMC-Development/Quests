package com.codepunisher.quests.commands.subcommands;

import com.codepunisher.quests.cache.QuestCache;
import com.codepunisher.quests.cache.QuestPlayerCache;
import com.codepunisher.quests.commands.QuestsSubCommand;
import com.codepunisher.quests.commands.lib.CommandCall;
import com.codepunisher.quests.models.Quest;
import com.codepunisher.quests.redis.RedisActiveQuests;
import com.codepunisher.quests.redis.RedisPlayerData;
import lombok.AllArgsConstructor;
import me.drepic.proton.common.ProtonManager;
import me.drepic.proton.common.message.MessageHandler;
import java.util.Random;
import java.util.function.Consumer;

@AllArgsConstructor
public class QuestResetSubCommand implements QuestsSubCommand {
  private static final int RANDOMIZE_POOL_AMOUNT = 3;
  private final RedisActiveQuests redisActiveQuests;
  private final RedisPlayerData redisPlayerData;
  private final QuestCache questCache;
  private final QuestPlayerCache playerCache;
  private final ProtonManager proton;

  @Override
  public Consumer<CommandCall> getCommandCallConsumer() {
    return call -> {
      // Clearing
      questCache.removeAllActiveQuests();
      playerCache.removeAll();
      redisActiveQuests.clear();
      redisPlayerData.clear();

      // Re-instantiating
      randomizeValuesForEachQuestAndCacheAsActive();
      redisActiveQuests.addLocalCacheToRedis();

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

  private void randomizeValuesForEachQuestAndCacheAsActive() {
    Random random = new Random();
    int counter = 0;
    for (Quest quest : questCache.getQuests()) {
      if (counter >= RANDOMIZE_POOL_AMOUNT) {
        break;
      }

      int randomRequirement =
          random.nextInt((quest.getMax() - quest.getMin()) + 1) + quest.getMin();
      questCache.addActiveQuest(quest.getId(), randomRequirement);

      counter++;
    }
  }
}
