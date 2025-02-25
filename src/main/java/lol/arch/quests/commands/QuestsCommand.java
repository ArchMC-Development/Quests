package lol.arch.quests.commands;

import lol.arch.quests.cache.QuestSubCommandCache;
import lol.arch.quests.commands.lib.Command;
import lol.arch.quests.commands.lib.CommandArgument;
import lol.arch.quests.commands.lib.CommandCall;
import lol.arch.quests.commands.lib.TabDynamic;
import lol.arch.quests.config.QuestsConfig;
import lol.arch.quests.models.CmdType;
import lol.arch.quests.models.LangCmd;
import lol.arch.quests.util.UtilChat;
import lombok.AllArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@AllArgsConstructor
public class QuestsCommand implements Consumer<TabDynamic> {
    private final QuestsConfig questsConfig;
    private final QuestSubCommandCache subCommandCache;

    @Command(
            label = "%s",
            commandArgumentList = {@CommandArgument(index = 0, dynamicId = "subcommands")})
    public void onPrimaryCommand(CommandCall call) {
        CommandSender sender = call.getSender();
        LangCmd langCmd = questsConfig.getLang(sender).getLangCmd();
        if (call.hasNoArgs()) {
            if (questsConfig.isDisplayMenuWhenNoArguments()) {
                executeSubCommand(CmdType.MENU, call);
            } else {
                langCmd
                        .getQuestCommandsViewList()
                        .forEach(
                                s -> {
                                    // Replacing the line with sub commands display
                                    if (s.contains("%subcommands%")) {
                                        langCmd
                                                .getSubCommands()
                                                .forEach(
                                                        (cmd, subCommand) -> {
                                                            String display =
                                                                    langCmd
                                                                            .getQuestSubCommandView()
                                                                            .replaceAll("%1%", call.getName())
                                                                            .replaceAll("%2%", cmd)
                                                                            .replaceAll("%3%", subCommand.getUsage());
                                                            sender.sendMessage(UtilChat.colorize(display));
                                                        });
                                    } else {
                                        sender.sendMessage(UtilChat.colorize(s));
                                    }
                                });
            }
            return;
        }

        Optional.ofNullable(langCmd.getSubCommands().get(call.getArg(0)))
                .ifPresentOrElse(
                        (subCommand -> {
                            if (!sender.hasPermission(subCommand.getPermission())) {
                                sender.sendMessage(
                                        UtilChat.colorize(questsConfig.getLang(sender).getNoPermission()));
                                return;
                            }

                            executeSubCommand(subCommand.getType(), call);
                        }),
                        () -> {
                            sender.sendMessage(
                                    UtilChat.colorize(questsConfig.getLang(sender).getCommandDoesNotExist()));
                        });
    }

    // This is the return for the tab completion list
    @Override
    public void accept(TabDynamic tabDynamic) {
        tabDynamic.add(
                "subcommands",
                (player) ->
                        questsConfig.getLang(player).getLangCmd().getSubCommands().entrySet().stream()
                                .filter(entry -> player.hasPermission(entry.getValue().getPermission()))
                                .map(Map.Entry::getKey)
                                .toList()
                                .iterator());
    }

    private void executeSubCommand(CmdType cmdType, CommandCall call) {
        CommandSender sender = call.getSender();
        if (!(sender instanceof Player) && !cmdType.isConsole()) {
            sender.sendMessage(UtilChat.colorize(questsConfig.getLang(sender).getNoConsole()));
            return;
        }

        subCommandCache
                .getQuestSubCommand(cmdType)
                .ifPresent(
                        questSubCommand -> {
                            questSubCommand.getCommandCallConsumer().accept(call);
                        });
    }
}
