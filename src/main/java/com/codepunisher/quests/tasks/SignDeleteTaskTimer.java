package com.codepunisher.quests.tasks;

import com.codepunisher.quests.cache.QuestSignCache;
import com.codepunisher.quests.database.QuestSignDatabase;
import com.codepunisher.quests.models.LocationWrapper;
import lombok.AllArgsConstructor;
import org.bukkit.Location;
import org.bukkit.block.Sign;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class SignDeleteTaskTimer implements Runnable {
    private final QuestSignDatabase signDatabase;
    private final QuestSignCache signCache;

    @Override
    public void run() {
        List<Location> locationsToRemove = new ArrayList<>();
        for (LocationWrapper wrapper : signCache.getKeySet()) {
            Location location = wrapper.getLocation();
            if (!(location.getBlock().getState() instanceof Sign)) {
                locationsToRemove.add(location);
            }
        }

        for (Location location : locationsToRemove) {
            signCache.remove(location);
            signDatabase.delete(location);
        }
    }
}
