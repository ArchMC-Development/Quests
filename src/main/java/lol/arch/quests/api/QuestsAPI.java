package lol.arch.quests.api;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Blocking;

public interface QuestsAPI {
    @Blocking
    int getTotalCompletedQuests(Player player);
}
