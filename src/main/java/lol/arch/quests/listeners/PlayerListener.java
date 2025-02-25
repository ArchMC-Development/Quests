package lol.arch.quests.listeners;

import lol.arch.quests.profile.QuestPlayerDataService;
import lol.arch.quests.profile.ShortTermDataService;
import net.evilblock.cubed.util.bukkit.Tasks;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created on 25/02/2025
 *
 * @author Preva1l
 */
public class PlayerListener implements Listener {
    private final Map<UUID, BukkitTask> loading = new HashMap<>();

    @EventHandler
    public void onPlayerLogin(AsyncPlayerPreLoginEvent event) {
        QuestPlayerDataService.INSTANCE.cache(event.getUniqueId());
        ShortTermDataService.INSTANCE.cache(event.getUniqueId());
        loading.put(event.getUniqueId(), Tasks.delayed(20L * 20L, () -> {
            QuestPlayerDataService.INSTANCE.invalidate(event.getUniqueId());
            ShortTermDataService.INSTANCE.invalidate(event.getUniqueId());
        }));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        var task = loading.get(event.getPlayer().getUniqueId());
        if (task != null) task.cancel();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        QuestPlayerDataService.INSTANCE.invalidate(event.getPlayer().getUniqueId());
        ShortTermDataService.INSTANCE.invalidate(event.getPlayer().getUniqueId());
    }
}
