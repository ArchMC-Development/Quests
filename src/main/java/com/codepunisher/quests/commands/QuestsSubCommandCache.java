package com.codepunisher.quests.commands;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class QuestsSubCommandCache {
    private final Map<String, QuestsSubCommand> subCommandMap = new HashMap<>();

    public void register(QuestsSubCommand subCommand) {
        subCommandMap.put(subCommand.getCommand().toLowerCase(), subCommand);
    }

    public Optional<QuestsSubCommand> getSubCommand(String key) {
        return Optional.ofNullable(subCommandMap.get(key.toLowerCase()));
    }

    public Collection<QuestsSubCommand> getSubCommands() {
        return subCommandMap.values();
    }
}
