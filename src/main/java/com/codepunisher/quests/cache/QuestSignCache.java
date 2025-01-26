package com.codepunisher.quests.cache;

import com.codepunisher.quests.models.LocationWrapper;
import org.bukkit.Location;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class QuestSignCache {
    private final Map<LocationWrapper, Boolean> signLocationsMap = new ConcurrentHashMap<>();

    public void add(Location location) {
        signLocationsMap.put(new LocationWrapper(location), Boolean.TRUE);
    }

    public void remove(Location location) {
        signLocationsMap.remove(new LocationWrapper(location));
    }

    public Set<LocationWrapper> getKeySet() {
        return signLocationsMap.keySet();
    }
}
