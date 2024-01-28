package com.codepunisher.quests.tasks;

import com.codepunisher.quests.cache.QuestCache;
import com.codepunisher.quests.cache.QuestPlayerCache;
import com.codepunisher.quests.cache.QuestSignCache;
import com.codepunisher.quests.config.QuestsConfig;
import com.codepunisher.quests.models.LocationWrapper;
import com.codepunisher.quests.models.Quest;
import com.codepunisher.quests.models.ActiveQuestPlayerData;
import com.codepunisher.quests.util.UtilChat;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.Optional;

@AllArgsConstructor
public class SignUpdateTaskTimer implements Runnable {
  private final QuestsConfig questsConfig;
  private final QuestCache questCache;
  private final QuestPlayerCache playerCache;
  private final QuestSignCache questSignCache;

  @Override
  public void run() {
    questSignCache.getKeySet().stream()
        .map(LocationWrapper::getLocation)
        .filter(location -> location.getBlock().getState() instanceof Sign)
        .forEach(
            location -> {
              Bukkit.getOnlinePlayers()
                  .forEach(
                      player -> {
                        sendSignUpdateToPlayers(player, location);
                      });
            });
  }

  private void sendSignUpdateToPlayers(Player player, Location signLocation) {
    Optional<ActiveQuestPlayerData> optionalPlayerData =
        playerCache.getActiveQuestPlayerData(player.getUniqueId());
    if (optionalPlayerData.isEmpty()) {
      sendSignUpdate(player, signLocation, "none", "none", "none", "none");
      return;
    }

    ActiveQuestPlayerData playerData = optionalPlayerData.get();
    Optional<Quest> optionalQuest = questCache.getQuest(playerData.getCurrentQuestId());
    if (optionalQuest.isEmpty()) {
      sendSignUpdate(player, signLocation, "none", "none", "none", "none");
      return;
    }

    Quest quest = optionalQuest.get();
    Optional<Integer> optionalInteger = questCache.getRequirement(quest.getId());
    if (optionalInteger.isEmpty()) {
      sendSignUpdate(player, signLocation, "none", "none", "none", "none");
      return;
    }

    sendSignUpdate(
        player,
        signLocation,
        UtilChat.capitalize(quest.getId()),
        UtilChat.capitalize(quest.getQuestType().name()),
        UtilChat.capitalize(
            quest.getQuestType().getInputFromAssociatedObject(quest.getAssociatedObject())),
        playerData.getCurrentQuestProgress() + "/" + optionalInteger.get());
  }

  private void sendSignUpdate(Player player, Location signLocation, String... lines) {
    String[] updatedLines = new String[lines.length];
    int counter = 0;
    for (String line : questsConfig.getLang(player).getQuestSign()) {
      updatedLines[counter] =
          UtilChat.colorize(
              line.replaceAll("%1%", lines[0])
                  .replaceAll("%2%", lines[1])
                  .replaceAll("%3%", lines[2])
                  .replaceAll("%4%", lines[3]));
      counter++;
    }
    player.sendSignChange(signLocation, updatedLines);
  }
}
