package com.codepunisher.quests.expansions;

import com.codepunisher.quests.cache.QuestCache;
import com.codepunisher.quests.cache.QuestPlayerCache;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        return switch (params) {
            case "current_name" -> playerManager.getClaimBlocks(player) + "";
            case "current_progress" -> getClaimOwnerAtPlayerChunk(player);
            case "current_requirement" -> getPlayerRankAtClaimChunk(player);
            case "current_active_completed" -> getPlayerRankAtClaimChunk(player);
            case "current_active_requirement" -> getPlayerRankAtClaimChunk(player);
            default -> "Null";
        };

    }
}
