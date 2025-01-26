package com.codepunisher.quests.expansions;

import com.codepunisher.quests.cache.QuestCache;
import com.codepunisher.quests.cache.QuestPlayerCache;
import com.codepunisher.quests.models.ActiveQuestPlayerData;
import com.codepunisher.quests.models.Quest;
import com.codepunisher.quests.util.UtilChat;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@RequiredArgsConstructor
@Getter
public class QuestsExpansion extends PlaceholderExpansion {
    @Getter(AccessLevel.PRIVATE)
    private final QuestCache questCache;
    @Getter(AccessLevel.PRIVATE)
    private final QuestPlayerCache playerCache;

    public String identifier = "quests";
    public String author = "CodePunisher & Preva1l";
    public String version = "1.0";

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (params.equalsIgnoreCase("total_completed")) {
            return String.valueOf(
                    playerCache.getPlayerStorageData(player.getUniqueId()).getCompletedQuests());
        }

        Optional<ActiveQuestPlayerData> dataOptional =
                playerCache.getActiveQuestPlayerData(player.getUniqueId());
        if (dataOptional.isEmpty()) {
            return "None";
        }

        ActiveQuestPlayerData playerData = dataOptional.get();
        Optional<Quest> questOptional = questCache.getQuest(playerData.getCurrentQuestId());
        if (questOptional.isEmpty()) {
            return "None";
        }

        Quest quest = questOptional.get();
        Optional<Integer> optionalRequirement = questCache.getRequirement(quest.getId());
        return optionalRequirement
                .map(
                        integer ->
                                switch (params) {
                                    case "current_id" -> UtilChat.capitalize(quest.getId());
                                    case "current_type" -> UtilChat.capitalize(quest.getQuestType().name());
                                    case "current_associated_object" -> UtilChat.capitalize(
                                            quest
                                                    .getQuestType()
                                                    .getInputFromAssociatedObject(quest.getAssociatedObject()));
                                    case "current_progress" -> playerData.getCurrentQuestProgress() + "";
                                    case "current_requirement" -> integer + "";
                                    case "current_active_completed" -> playerData.getCompletedDailyQuests().size() + "";
                                    case "current_active_requirement" ->
                                            questCache.getActiveQuestsEntrySet().size() + "";
                                    default -> "Null";
                                })
                .orElse("None");
    }
}
