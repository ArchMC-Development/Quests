package lol.arch.quests.menu;

import lol.arch.quests.config.QuestsConfig;
import lol.arch.quests.models.Quest;
import lol.arch.quests.models.gui.ActiveQuestGuiInventory;
import lol.arch.quests.profile.ShortTermDataService;
import lol.arch.quests.profile.ShortTermQuestProfile;
import lol.arch.quests.sync.ActiveQuestsDataSync;
import lol.arch.quests.util.ItemBuilder;
import lol.arch.quests.util.UtilChat;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ActiveQuestsMenu extends AbstractMenu {
    public ActiveQuestsMenu(
            Player player,
            QuestsConfig config,
            boolean... optionalPlayOpenSound) {
        super(player, config, config.getLang(player).getActiveQuestGuiInventory(), optionalPlayOpenSound);

        for (Map.Entry<UUID, Integer> entry : ActiveQuestsDataSync.INSTANCE.cached().getActive().entrySet()) {
            UUID id = entry.getKey();

            Quest quest = ActiveQuestsDataSync.INSTANCE.cached().getCache().get(id);
            if (quest == null) return;
            System.out.println("ID: " + quest.getId());
            System.out.println("Type: " + quest.getQuestType().name());
            if (!player.hasPermission(quest.getPermission())) return;

            ActiveQuestGuiInventory inv = (ActiveQuestGuiInventory) guiInventory;
            ShortTermQuestProfile profile = getPlayerData(player);
            int activeQuestRequirement = entry.getValue();
            addItem(
                    getQuestItemStack(profile, quest, activeQuestRequirement),
                    (event) -> {
                        // Do nothing if complete
                        if (isCompleted(profile, quest)) {
                            return;
                        }

                        // Option to leave quest
                        boolean hasJoined = hasJoined(profile, quest);
                        if (hasJoined(profile, quest)) {
                            new AreYouSureMenu(
                                    player,
                                    config,
                                    config.getLang(player).getAreYouSureLeaveInventory(),
                                    () -> {
                                        profile.optOutOfCurrentQuest();

                                        if (inv.getLeaveSound() != null)
                                            player.playSound(player.getLocation(), inv.getLeaveSound(), 0.35f, 1.25f);

                                        player.sendMessage(
                                                UtilChat.colorize(config.getLang(player).getQuestLeave())
                                                        .replaceAll("%1%", quest.getId().replaceAll("_", " ")));
                                        new ActiveQuestsMenu(player, config).open(player);
                                    },
                                    () -> {
                                        new ActiveQuestsMenu(player, config).open(player);
                                    })
                                    .open(player);
                            return;
                        }

                        // Switching quests
                        if (!hasJoined && isCurrentlyJoinedToAQuest(profile)) {
                            new AreYouSureMenu(
                                    player,
                                    config,
                                    config.getLang(player).getAreYouSureSwitchInventory(),
                                    () -> {
                                        profile.optOutOfCurrentQuest();
                                        profile.setCurrentQuestId(quest.getIdentifier().toString());
                                        profile.save();

                                        if (inv.getSwitchSound() != null)
                                            player.playSound(
                                                    player.getLocation(), inv.getSwitchSound(), 0.35f, 1.25f);

                                        player.sendMessage(
                                                UtilChat.colorize(config.getLang(player).getQuestSwitch())
                                                        .replaceAll("%1%", quest.getId().replaceAll("_", " ")));
                                        new ActiveQuestsMenu(player, config).open(player);
                                    },
                                    () -> {
                                        new ActiveQuestsMenu(player, config).open(player);
                                    })
                                    .open(player);
                            return;
                        }

                        // Normal joining
                        profile.setCurrentQuestId(quest.getIdentifier().toString());
                        profile.save();

                        if (inv.getJoinSound() != null)
                            player.playSound(player.getLocation(), inv.getJoinSound(), 0.35f, 1.25f);

                        player.sendMessage(
                                UtilChat.colorize(
                                        config
                                                .getLang(player)
                                                .getQuestJoin()
                                                .replaceAll("%1%", quest.getId().replaceAll("_", " "))));
                        new ActiveQuestsMenu(player, config, false).open(player);
                    });
        }
    }

    private ItemStack getQuestItemStack(
            ShortTermQuestProfile playerData, Quest quest, int activeQuestRequirement) {
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

    /**
     * Making sure each string within the configurations work with the advertised placeholders
     */
    private String getUpdatedStringWithPlaceholders(
            String input, ShortTermQuestProfile playerData, Quest quest, int activeQuestRequirement) {
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
                .replace("%quest_id%", UtilChat.capitalize(quest.getId()))
                .replace("%quest_name%", quest.getDisplayName())
                .replace("%quest_type%", UtilChat.capitalize(quest.getQuestType().name()))
                .replace(
                        "%quest_associated_object%",
                        UtilChat.capitalize(
                                quest.getQuestType().getInputFromAssociatedObject(quest.getAssociatedObject())))
                .replace("%quest_current_progress%", playerData.getCurrentQuestProgress(quest) + "")
                .replace("%quest_required_progress%", activeQuestRequirement + "")
                .replace("%quest_color%", color)
                .replace("%quest_click_action%", clickAction);
    }

    private boolean hasJoined(ShortTermQuestProfile playerData, Quest quest) {
        return playerData.getCurrentQuestId().equals(quest.getIdentifier().toString());
    }

    private boolean isCompleted(ShortTermQuestProfile playerData, Quest quest) {
        return playerData.getDailyCompletedQuests().contains(quest.getIdentifier());
    }

    private boolean isCurrentlyJoinedToAQuest(ShortTermQuestProfile playerData) {
        return !playerData.getCurrentQuestId().isEmpty();
    }

    private ShortTermQuestProfile getPlayerData(Player player) {
        var profile = ShortTermDataService.INSTANCE.byId(player.getUniqueId()).join();
        return profile == null ? new ShortTermQuestProfile(player.getUniqueId(), new HashSet<>(), "", 0) : profile;
    }
}
