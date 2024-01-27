package com.codepunisher.quests.expansions;

import com.codepunisher.quests.cache.QuestCache;
import com.codepunisher.quests.cache.QuestPlayerCache;
import com.codepunisher.quests.models.Quest;
import com.codepunisher.quests.models.QuestPlayerData;
import com.codepunisher.quests.util.UtilChat;
import lombok.AllArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@AllArgsConstructor
public class QuestsExpansion extends PlaceholderExpansion {
  private final QuestCache questCache;
  private final QuestPlayerCache playerCache;

  @Override
  public @NotNull String getIdentifier() {
    return "quests";
  }

  @Override
  public @NotNull String getAuthor() {
    return "CodePunisher";
  }

  @Override
  public @NotNull String getVersion() {
    return "1.0";
  }

  @Override
  public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
    Optional<QuestPlayerData> dataOptional = playerCache.get(player.getUniqueId());
    if (dataOptional.isEmpty()) {
      return "None";
    }

    QuestPlayerData playerData = dataOptional.get();
    Optional<Quest> questOptional = questCache.getQuest(playerData.getCurrentQuestId());
    if (questOptional.isEmpty()) {
      return "None";
    }

    Quest quest = questOptional.get();
    Optional<Integer> optionalRequirement = questCache.getRequirement(quest.getId());
      return optionalRequirement.map(integer -> switch (params) {
          case "current_id" -> UtilChat.capitalize(quest.getId());
          case "current_type" -> UtilChat.capitalize(quest.getQuestType().name());
          case "current_associated_object" -> UtilChat.capitalize(
                  quest.getQuestType().getInputFromAssociatedObject(quest.getAssociatedObject()));
          case "current_progress" -> playerData.getCurrentQuestProgress() + "";
          case "current_requirement" -> integer + "";
          case "current_active_completed" -> playerData.getCompletedDailyQuests().size() + "";
          case "current_active_requirement" -> questCache.getActiveQuestsEntrySet().size() + "";
          default -> "Null";
      }).orElse("None");
  }
}
