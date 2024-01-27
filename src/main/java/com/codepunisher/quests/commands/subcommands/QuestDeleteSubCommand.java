package com.codepunisher.quests.commands.subcommands;

import com.codepunisher.quests.cache.QuestCache;
import com.codepunisher.quests.cache.QuestPlayerCache;
import com.codepunisher.quests.commands.QuestsSubCommand;
import com.codepunisher.quests.commands.lib.CommandCall;
import com.codepunisher.quests.database.QuestDatabase;
import com.codepunisher.quests.menu.AbstractAreYouSureMenu;
import com.codepunisher.quests.models.Quest;
import com.codepunisher.quests.models.QuestPlayerData;
import com.codepunisher.quests.redis.RedisActiveQuests;
import com.codepunisher.quests.util.UtilChat;
import lombok.AllArgsConstructor;
import me.drepic.proton.common.ProtonManager;
import me.drepic.proton.common.message.MessageHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.function.Consumer;

@AllArgsConstructor
public class QuestDeleteSubCommand implements QuestsSubCommand {
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

        new AbstractAreYouSureMenu(
                p -> {
                  Quest quest = optionalQuest.get();
                  questDatabase.remove(quest);
                  redisActiveQuests.removeQuest(quest);
                  wipeFromCaches(quest.getId());
                  proton.broadcast("quest-plugin", "quest-delete", quest.getId());
                  player.sendMessage(
                      UtilChat.colorize("&aYou have deleted the quest: " + quest.getId()));
                },
                "&7This will delete the quest",
                "&7for good! If this quest is active,",
                "&7it will be removed from all",
                "&7players participating!")
            .open(player);
      } catch (NullPointerException e) {
        player.sendMessage(
            UtilChat.colorize(
                "&cThat quest does not exist! Here are all available quests "
                    + questCache.getQuests().stream().map(Quest::getId).toList()));
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
        .getEntrySet()
        .forEach(
            entry -> {
              QuestPlayerData playerData = entry.getValue();
              if (playerData.getCurrentQuestId().equalsIgnoreCase(id)) {
                playerData.optOutOfCurrentQuestId();
              }

              // Informing player
              Optional.ofNullable(Bukkit.getPlayer(entry.getKey()))
                  .ifPresent(
                      player -> {
                        player.sendMessage(
                            UtilChat.colorize(
                                "&cThe quest you were in has been deleted by an admin!"));
                      });
            });
  }
}
