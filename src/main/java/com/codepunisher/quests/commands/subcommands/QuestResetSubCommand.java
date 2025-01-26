package com.codepunisher.quests.commands.subcommands;

import com.codepunisher.quests.cache.QuestCache;
import com.codepunisher.quests.cache.QuestPlayerCache;
import com.codepunisher.quests.commands.QuestsSubCommand;
import com.codepunisher.quests.commands.lib.CommandCall;
import com.codepunisher.quests.config.QuestsConfig;
import com.codepunisher.quests.models.Quest;
import com.codepunisher.quests.redis.RedisActiveQuests;
import com.codepunisher.quests.redis.RedisPlayerData;
import com.codepunisher.quests.util.UtilChat;
import fr.mrmicky.fastinv.FastInvManager;
import gg.scala.aware.Aware;
import gg.scala.aware.message.AwareMessage;
import gg.scala.aware.thread.AwareThreadContext;
import kotlin.Pair;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

@AllArgsConstructor
public class QuestResetSubCommand implements QuestsSubCommand {
    private final JavaPlugin plugin;
    private final QuestsConfig questsConfig;
    private final RedisActiveQuests redisActiveQuests;
    private final RedisPlayerData redisPlayerData;
    private final QuestCache questCache;
    private final QuestPlayerCache playerCache;
    private final Aware<AwareMessage> aware;

    @Override
    public Consumer<CommandCall> getCommandCallConsumer() {
        return call -> {
            // Close Menus
            FastInvManager.closeAll();

            // Clearing
            questCache.removeAllActiveQuests();
            playerCache.removeAllActiveQuestUsers();
            redisActiveQuests.clear();
            redisPlayerData.clear();

            // Re-instantiating
            randomizeValuesForEachQuestAndCacheAsActive();
            redisActiveQuests.addLocalCacheToRedis();

            // Updating caches across other server instances running this plugin
            informOnlinePlayersAboutReset();
            AwareMessage.of(
                    "quest-reset",
                    aware
            ).publish(AwareThreadContext.ASYNC, "quest-plugin");
            CommandSender sender = call.getSender();
            sender.sendMessage(UtilChat.colorize(questsConfig.getLang(sender).getQuestsResetSuccess()));
            plugin.getLogger().info("Quests have been reset by " + sender.getName());
        };
    }

    public void reset() {
        // Close Menus
        FastInvManager.closeAll();

        questCache.removeAllActiveQuests();
        playerCache.removeAllActiveQuestUsers();
        randomizeValuesForEachQuestAndCacheAsActive();
        informOnlinePlayersAboutReset();
    }

    private void randomizeValuesForEachQuestAndCacheAsActive() {
        Random random = new Random();
        int counter = 0;
        List<Quest> availableQuests = new ArrayList<>(questCache.getQuests());

        while (counter < questsConfig.getRandomizedPoolAmount() && !availableQuests.isEmpty()) {
            int randomIndex = random.nextInt(availableQuests.size());
            Quest quest = availableQuests.remove(randomIndex);

            int randomRequirement = random.nextInt((quest.getMax() - quest.getMin()) + 1) + quest.getMin();
            questCache.addActiveQuest(quest.getId(), randomRequirement);

            counter++;
        }
    }

    private void informOnlinePlayersAboutReset() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(UtilChat.colorize(questsConfig.getLang(player).getQuestsResetToPlayers()));
        }
    }
}
