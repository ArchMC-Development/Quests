package com.codepunisher.quests.commands.subcommands;

import com.codepunisher.quests.commands.QuestsSubCommand;
import com.codepunisher.quests.commands.lib.CommandCall;
import com.codepunisher.quests.util.UtilChat;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class QuestLanguageSubCommand implements QuestsSubCommand {
    public static final Map<UUID, String> playerTempLanguageMap = new HashMap<>();

    @Override
    public Consumer<CommandCall> getCommandCallConsumer() {
        return call -> {
            Player player = call.asPlayer();
            String language = call.getArg(1);
            player.sendMessage(UtilChat.colorize("&aYour language has been set to " + language));
            playerTempLanguageMap.put(player.getUniqueId(), language);
        };
    }
}
