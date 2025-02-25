package lol.arch.quests.expansions;

import lol.arch.quests.models.Quest;
import lol.arch.quests.profile.QuestPlayerDataService;
import lol.arch.quests.profile.QuestProfile;
import lol.arch.quests.profile.ShortTermDataService;
import lol.arch.quests.profile.ShortTermQuestProfile;
import lol.arch.quests.sync.ActiveQuestsDataSync;
import lol.arch.quests.util.UtilChat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class QuestsExpansion extends PlaceholderExpansion {
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
            QuestProfile e = QuestPlayerDataService.INSTANCE.byId(player.getUniqueId()).join();
            if (e == null) {
                return "None";
            }
            return String.valueOf(e.getCompletedQuests());
        }

        ShortTermQuestProfile profile = ShortTermDataService.INSTANCE.byId(player.getUniqueId()).join();
        if (profile == null) {
            return "None";
        }

        Quest quest = ActiveQuestsDataSync.INSTANCE.cached().getCache().get(UUID.fromString(profile.getCurrentQuestId()));

        int requirement = ActiveQuestsDataSync.INSTANCE.cached().getActive().get(quest.getIdentifier());
        return switch (params) {
            case "current_id" -> UtilChat.capitalize(quest.getId());
            case "current_type" -> UtilChat.capitalize(quest.getQuestType().name());
            case "current_associated_object" -> UtilChat.capitalize(
                    quest
                            .getQuestType()
                            .getInputFromAssociatedObject(quest.getAssociatedObject()));
            case "current_progress" -> profile.getCurrentQuestProgress() + "";
            case "current_requirement" -> requirement + "";
            case "current_active_completed" -> profile.getDailyCompletedQuests().size() + "";
            case "current_active_requirement" -> ActiveQuestsDataSync.INSTANCE.cached().getActive().size() + "";
            default -> "Null";
        };
    }
}
