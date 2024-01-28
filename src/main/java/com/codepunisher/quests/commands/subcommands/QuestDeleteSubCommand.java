package com.codepunisher.quests.commands.subcommands;

import com.codepunisher.quests.cache.QuestCache;
import com.codepunisher.quests.cache.QuestPlayerCache;
import com.codepunisher.quests.commands.QuestsSubCommand;
import com.codepunisher.quests.commands.lib.CommandCall;
import com.codepunisher.quests.config.QuestsConfig;
import com.codepunisher.quests.database.QuestDatabase;
import com.codepunisher.quests.menu.AreYouSureDeleteMenu;
import com.codepunisher.quests.models.Quest;
import com.codepunisher.quests.models.ActiveQuestPlayerData;
import com.codepunisher.quests.redis.RedisActiveQuests;
import com.codepunisher.quests.util.UtilChat;
import lombok.AllArgsConstructor;
import me.drepic.proton.common.ProtonManager;
import me.drepic.proton.common.message.MessageHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;
import java.util.function.Consumer;

@AllArgsConstructor
public class QuestDeleteSubCommand implements QuestsSubCommand {
  private final JavaPlugin plugin;
  private final QuestsConfig questsConfig;
  private final QuestDatabase questDatabase;
  private final RedisActiveQuests redisActiveQuests;
  private final QuestCache questCache;
  private final QuestPlayerCache playerCache;
  private final ProtonManager proton;

  @Override
  public Consumer<CommandCall> getCommandCallConsumer() {
    return call -> {
      Player player = call.asPlayer();
      try {
        Optional<Quest> optionalQuest = questCache.getQuest(call.getArg(1));
        if (optionalQuest.isEmpty()) {
          throw new NullPointerException();
        }

        new AreYouSureDeleteMenu(
                player,
                questsConfig,
                () -> {
                  Quest quest = optionalQuest.get();
                  questDatabase.remove(quest);
                  redisActiveQuests.removeQuest(quest);
                  wipeFromCaches(quest.getId());
                  proton.broadcast("quest-plugin", "quest-delete", quest.getId());
                  plugin
                      .getLogger()
                      .info(
                          String.format(
                              "%s just deleted the quest %s", player.getName(), quest.getId()));
                  player.sendMessage(
                      UtilChat.colorize(
                          questsConfig
                              .getLang(player)
                              .getQuestDeleted()
                              .replaceAll("%1%", quest.getId())));
                })
            .open(player);
      } catch (Exception e) {
        player.sendMessage(
            UtilChat.colorize(
                questsConfig
                    .getLang(player)
                    .getQuestDoesNotExist()
                    .replaceAll(
                        "%1%",
                        questCache.getQuests().stream().map(Quest::getId).toList().toString())));
      }
    };
  }

  @MessageHandler(namespace = "quest-plugin", subject = "quest-delete")
  public void onCacheUpdateReceive(String id) {
    wipeFromCaches(id);
  }

  private void wipeFromCaches(String id) {
    questCache.remove(id);
    questCache.removeActiveQuest(id);
    playerCache
        .getActiveQuestEntrySet()
        .forEach(
            entry -> {
              ActiveQuestPlayerData playerData = entry.getValue();
              if (playerData.getCurrentQuestId().equalsIgnoreCase(id)) {
                playerData.optOutOfCurrentQuestId();
                Optional.ofNullable(Bukkit.getPlayer(entry.getKey()))
                    .ifPresent(
                        player -> {
                          player.sendMessage(
                              UtilChat.colorize(
                                  questsConfig.getLang(player).getQuestDeletedByAdmin()));
                        });
              }
            });
  }
}
