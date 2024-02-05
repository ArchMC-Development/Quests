package com.codepunisher.quests.menu;

import com.codepunisher.quests.cache.QuestCache;
import com.codepunisher.quests.cache.QuestPlayerCache;
import com.codepunisher.quests.config.QuestsConfig;
import com.codepunisher.quests.models.ActiveQuestPlayerData;
import com.codepunisher.quests.models.Quest;
import com.codepunisher.quests.models.gui.ActiveQuestGuiInventory;
import com.codepunisher.quests.util.ItemBuilder;
import com.codepunisher.quests.util.UtilChat;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ActiveQuestsMenu extends AbstractMenu {
  private final QuestPlayerCache playerCache;

  public ActiveQuestsMenu(
      Player player,
      QuestsConfig config,
      QuestCache questCache,
      QuestPlayerCache playerCache,
      boolean... optionalPlayOpenSound) {
    super(
        player, config, config.getLang(player).getActiveQuestGuiInventory(), optionalPlayOpenSound);
    this.playerCache = playerCache;

    for (Map.Entry<String, Integer> entry : questCache.getActiveQuestsEntrySet()) {
      String id = entry.getKey();

      Optional<Quest> optionalQuest = questCache.getQuest(id);
      if (optionalQuest.isEmpty()) {
        continue;
      }

      Quest quest = optionalQuest.get();
      if (!player.hasPermission(quest.getPermission())) {
        return;
      }

      ActiveQuestGuiInventory inv = (ActiveQuestGuiInventory) guiInventory;
      ActiveQuestPlayerData playerData = getPlayerData(player);
      int activeQuestRequirement = entry.getValue();
      addItem(
          getQuestItemStack(playerData, quest, activeQuestRequirement),
          (event) -> {
            // Do nothing if complete
            if (isCompleted(playerData, quest)) {
              return;
            }

            // Option to leave quest
            boolean hasJoined = hasJoined(playerData, quest);
            if (hasJoined(playerData, quest)) {
              new AreYouSureMenu(
                      player,
                      config,
                      config.getLang(player).getAreYouSureLeaveInventory(),
                      () -> {
                        playerData.optOutOfCurrentQuestId();

                        if (inv.getLeaveSound() != null)
                          player.playSound(player.getLocation(), inv.getLeaveSound(), 0.35f, 1.25f);

                        player.sendMessage(
                            UtilChat.colorize(config.getLang(player).getQuestLeave())
                                .replaceAll("%1%", quest.getId().replaceAll("_", " ")));
                        new ActiveQuestsMenu(player, config, questCache, playerCache).open(player);
                      },
                      () -> {
                        new ActiveQuestsMenu(player, config, questCache, playerCache).open(player);
                      })
                  .open(player);
              return;
            }

            // Switching quests
            if (!hasJoined && isCurrentlyJoinedToAQuest(playerData)) {
              new AreYouSureMenu(
                      player,
                      config,
                      config.getLang(player).getAreYouSureSwitchInventory(),
                      () -> {
                        playerData.optOutOfCurrentQuestId();
                        playerData.setCurrentQuestId(quest.getId());
                        playerCache.addActiveQuestUser(player.getUniqueId(), playerData);

                        if (inv.getSwitchSound() != null)
                          player.playSound(
                              player.getLocation(), inv.getSwitchSound(), 0.35f, 1.25f);

                        player.sendMessage(
                            UtilChat.colorize(config.getLang(player).getQuestSwitch())
                                .replaceAll("%1%", quest.getId().replaceAll("_", " ")));
                        new ActiveQuestsMenu(player, config, questCache, playerCache).open(player);
                      },
                      () -> {
                        new ActiveQuestsMenu(player, config, questCache, playerCache).open(player);
                      })
                  .open(player);
              return;
            }

            // Normal joining
            playerData.setCurrentQuestId(quest.getId());
            playerCache.addActiveQuestUser(player.getUniqueId(), playerData);

            if (inv.getJoinSound() != null)
              player.playSound(player.getLocation(), inv.getJoinSound(), 0.35f, 1.25f);

            player.sendMessage(
                UtilChat.colorize(
                    config
                        .getLang(player)
                        .getQuestJoin()
                        .replaceAll("%1%", quest.getId().replaceAll("_", " "))));
            new ActiveQuestsMenu(player, config, questCache, playerCache, false).open(player);
          });
    }
  }

  private ItemStack getQuestItemStack(
      ActiveQuestPlayerData playerData, Quest quest, int activeQuestRequirement) {
    ActiveQuestGuiInventory inv = (ActiveQuestGuiInventory) guiInventory;
    String updatedName =
        getUpdatedStringWithPlaceholders(
            String.valueOf(inv.getName().toCharArray()), playerData, quest, activeQuestRequirement);

    List<String> updatedLore = new ArrayList<>();
    for (String lore : inv.getLore()) {
      String clonedLore = String.valueOf(lore.toCharArray());
      updatedLore.add(
          getUpdatedStringWithPlaceholders(clonedLore, playerData, quest, activeQuestRequirement));
    }
    Material type =
        isCompleted(playerData, quest) ? Material.RED_STAINED_GLASS_PANE : quest.getDisplay();

    return ItemBuilder.of(type)
        .name(updatedName)
        .lore(updatedLore)
        .glowIf(meta -> hasJoined(playerData, quest))
        .build();
  }

  /** Making sure each string within the configurations work with the advertised placeholders */
  private String getUpdatedStringWithPlaceholders(
      String input, ActiveQuestPlayerData playerData, Quest quest, int activeQuestRequirement) {
    boolean hasJoined = hasJoined(playerData, quest);
    boolean isCompleted = isCompleted(playerData, quest);
    boolean isCurrentlyJoinedToAQuest = isCurrentlyJoinedToAQuest(playerData);
    String color = isCompleted ? "&c" : hasJoined ? "&6" : "&a";

    String clickAction = "";
    if (!hasJoined && !isCompleted && !isCurrentlyJoinedToAQuest) clickAction = "join";

    if (hasJoined && !isCompleted) clickAction = "leave";

    if (!hasJoined && !isCompleted && isCurrentlyJoinedToAQuest) clickAction = "switch";

    if (isCompleted && !isCurrentlyJoinedToAQuest) clickAction = "do nothing";

    return input
        .replaceAll("%quest_id%", UtilChat.capitalize(quest.getId()))
        .replaceAll("%quest_type%", UtilChat.capitalize(quest.getQuestType().name()))
        .replaceAll(
            "%quest_associated_object%",
            UtilChat.capitalize(
                quest.getQuestType().getInputFromAssociatedObject(quest.getAssociatedObject())))
        .replaceAll("%quest_current_progress%", playerData.getCurrentQuestProgress(quest) + "")
        .replaceAll("%quest_required_progress%", activeQuestRequirement + "")
        .replaceAll("%quest_color%", color)
        .replaceAll("%quest_click_action%", clickAction);
  }

  private boolean hasJoined(ActiveQuestPlayerData playerData, Quest quest) {
    return playerData.getCurrentQuestId().equals(quest.getId());
  }

  private boolean isCompleted(ActiveQuestPlayerData playerData, Quest quest) {
    return playerData.getCompletedDailyQuests().contains(quest.getId());
  }

  private boolean isCurrentlyJoinedToAQuest(ActiveQuestPlayerData playerData) {
    return !playerData.getCurrentQuestId().isEmpty();
  }

  private ActiveQuestPlayerData getPlayerData(Player player) {
    Optional<ActiveQuestPlayerData> optionalPlayerData =
        playerCache.getActiveQuestPlayerData(player.getUniqueId());
    return optionalPlayerData.orElseGet(ActiveQuestPlayerData::new);
  }
}
