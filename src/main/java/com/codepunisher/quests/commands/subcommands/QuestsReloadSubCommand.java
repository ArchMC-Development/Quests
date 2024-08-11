package com.codepunisher.quests.commands.subcommands;

import com.codepunisher.quests.commands.QuestsSubCommand;
import com.codepunisher.quests.commands.lib.CommandCall;
import com.codepunisher.quests.config.QuestsConfig;
import com.codepunisher.quests.util.UtilChat;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;

@AllArgsConstructor
public class QuestsReloadSubCommand implements QuestsSubCommand {
    private final JavaPlugin questsPlugin;
    private final QuestsConfig questsConfig;

    @Override
    public Consumer<CommandCall> getCommandCallConsumer() {
        return call -> {
            Player player = call.asPlayer();
            questsConfig.reload(questsPlugin);
            player.sendMessage(UtilChat.colorize("&aPlugin reloaded!"));
        };
    }
}
