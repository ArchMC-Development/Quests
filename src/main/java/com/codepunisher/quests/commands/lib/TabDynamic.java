package com.codepunisher.quests.commands.lib;

import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public interface TabDynamic {
    static TabDynamic of(String id, Supplier<Iterator<String>> function) {
        TabDynamicImpl dynamicImpl = new TabDynamicImpl();
        dynamicImpl.add(id, function);
        return dynamicImpl;
    }

    TabDynamic add(String id, Supplier<Iterator<String>> function);

    TabDynamic add(String id, Function<Player, Iterator<String>> function);

    Optional<Iterator<String>> get(String id);

    Optional<Iterator<String>> getPlayerList(Player player, String id);
}
