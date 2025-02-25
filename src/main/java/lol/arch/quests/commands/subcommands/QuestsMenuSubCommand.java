package lol.arch.quests.commands.subcommands;

import lol.arch.quests.commands.QuestsSubCommand;
import lol.arch.quests.commands.lib.CommandCall;
import lol.arch.quests.config.QuestsConfig;
import lol.arch.quests.menu.ActiveQuestsMenu;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

@AllArgsConstructor
public class QuestsMenuSubCommand implements QuestsSubCommand {
    private final QuestsConfig questsConfig;

    @Override
    public Consumer<CommandCall> getCommandCallConsumer() {
        return call -> {
            Player player = call.asPlayer();
            new ActiveQuestsMenu(player, questsConfig).open(player);
        };
    }
}
