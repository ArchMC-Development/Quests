package com.codepunisher.quests.commands.lib;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class TabDynamicImpl implements TabDynamic {
    private final Map<String, Supplier<Iterator<String>>> dynamicMapTabList = new HashMap<>();
    private final Map<String, Function<Player, Iterator<String>>> playerDynamicTabList =
            new HashMap<>();

    @Override
    public TabDynamic add(String id, Supplier<Iterator<String>> function) {
        dynamicMapTabList.put(id.toLowerCase(), function);
        return this;
    }

    @Override
    public TabDynamic add(String id, Function<Player, Iterator<String>> function) {
        playerDynamicTabList.put(id, function);
        return this;
    }

    @Override
    public Optional<Iterator<String>> get(String id) {
        var function = dynamicMapTabList.get(id);
        if (function != null) {
            return Optional.ofNullable(function.get());
        }

        return Optional.empty();
    }

    @Override
    public Optional<Iterator<String>> getPlayerList(Player player, String id) {
        var function = playerDynamicTabList.get(id);
        if (function != null) {
            return Optional.ofNullable(function.apply(player));
        }

        return Optional.empty();
    }
}
