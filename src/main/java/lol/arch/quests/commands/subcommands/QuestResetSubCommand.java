package lol.arch.quests.commands.subcommands;

import fr.mrmicky.fastinv.FastInvManager;
import gg.scala.aware.Aware;
import gg.scala.aware.message.AwareMessage;
import gg.scala.aware.thread.AwareThreadContext;
import lol.arch.quests.commands.QuestsSubCommand;
import lol.arch.quests.commands.lib.CommandCall;
import lol.arch.quests.config.QuestsConfig;
import lol.arch.quests.data.QuestDataService;
import lol.arch.quests.models.Quest;
import lol.arch.quests.profile.ShortTermDataService;
import lol.arch.quests.profile.ShortTermQuestProfile;
import lol.arch.quests.sync.ActiveQuestHolder;
import lol.arch.quests.sync.ActiveQuestsDataSync;
import lol.arch.quests.util.UtilChat;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

@AllArgsConstructor
public class QuestResetSubCommand implements QuestsSubCommand {
    private final JavaPlugin plugin;
    private final QuestsConfig questsConfig;
    private final Aware<AwareMessage> aware;

    @Override
    public Consumer<CommandCall> getCommandCallConsumer() {
        return call -> {
            // Close Menus
            FastInvManager.closeAll();

            // Clearing
            ActiveQuestHolder holder = ActiveQuestsDataSync.INSTANCE.cached();
            holder.getCache().clear();
            holder.getActive().clear();
            ShortTermDataService.INSTANCE.getAll()
                            .thenAccept(questCache
                                    -> questCache.values().forEach(ShortTermQuestProfile::reset));

            // Re-instantiating
            randomizeValuesForEachQuestAndCacheAsActive(holder);

            // Updating caches across other server instances running this plugin
            informOnlinePlayersAboutReset();
            AwareMessage.of(
                    "quest-reset",
                    aware
            ).publish(AwareThreadContext.ASYNC, "quests:updates");
            CommandSender sender = call.getSender();
            sender.sendMessage(UtilChat.colorize(questsConfig.getLang(sender).getQuestsResetSuccess()));
            plugin.getLogger().info("Quests have been reset by " + sender.getName());
        };
    }

    public void reset() {
        // Close Menus
        FastInvManager.closeAll();
        informOnlinePlayersAboutReset();
    }

    private void randomizeValuesForEachQuestAndCacheAsActive(ActiveQuestHolder holder) {
        Random random = ThreadLocalRandom.current();
        QuestDataService.INSTANCE.getAll()
                .thenAccept(questCache -> {
                    int counter = 0;
                    List<Quest> availableQuests = new ArrayList<>(questCache.values());
                    while (counter < questsConfig.getRandomizedPoolAmount() && !availableQuests.isEmpty()) {
                        int randomIndex = random.nextInt(availableQuests.size());
                        Quest quest = availableQuests.remove(randomIndex);

                        int randomRequirement = random.nextInt((quest.getMax() - quest.getMin()) + 1) + quest.getMin();
                        holder.getActive().put(quest.getIdentifier(), randomRequirement);
                        holder.getCache().put(quest.getIdentifier(), quest);
                        ActiveQuestsDataSync.INSTANCE.sync(holder);

                        counter++;
                    }
                });
    }

    private void informOnlinePlayersAboutReset() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(UtilChat.colorize(questsConfig.getLang(player).getQuestsResetToPlayers()));
        }
    }
}
