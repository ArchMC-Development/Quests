package lol.arch.quests.api;

import lol.arch.quests.profile.QuestPlayerDataService;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Blocking;

@AllArgsConstructor
public class QuestsApiImpl implements QuestsAPI {

    @Blocking
    @Override
    public int getTotalCompletedQuests(Player player) {
        var profile = QuestPlayerDataService.INSTANCE.byId(player.getUniqueId()).join();
        if (profile == null) {
            return 0;
        }
        return profile.getCompletedQuests();
    }
}
