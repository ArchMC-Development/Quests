package com.codepunisher.quests.commands;

import com.codepunisher.quests.commands.lib.Command;
import com.codepunisher.quests.commands.lib.CommandArgument;
import com.codepunisher.quests.commands.lib.CommandCall;
import com.codepunisher.quests.commands.lib.TabDynamic;
import com.codepunisher.quests.config.QuestsConfig;
import com.codepunisher.quests.util.UtilChat;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

@AllArgsConstructor
public class QuestsCommand implements Consumer<TabDynamic> {
  private final QuestsSubCommandCache questsSubCommandCache;
  private final QuestsConfig questsConfig;

  @Command(
      label = "%s",
      player = true,
      commandArgumentList = {@CommandArgument(index = 0, dynamicId = "subcommands")})
  public void onPrimaryCommand(CommandCall call) {
    Player player = call.asPlayer();
    if (call.hasNoArgs()) {
      questsConfig
          .getQuestCommandsViewList()
          .forEach(
              s -> {
                // Replacing the line with sub commands display
                if (s.contains("%subcommands%")) {
                  questsSubCommandCache
                      .getSubCommands()
                      .forEach(
                          subCommand -> {
                            String display =
                                questsConfig
                                    .getQuestSubCommandView()
                                    .replaceAll("%1%", call.getName())
                                    .replaceAll("%2%", subCommand.getCommand())
                                    .replaceAll("%3%", subCommand.getUsage());
                            player.sendMessage(UtilChat.colorize(display));
                          });
                } else {
                  player.sendMessage(UtilChat.colorize(s));
                }
              });
      return;
    }

    questsSubCommandCache
        .getSubCommand(call.getArg(0))
        .ifPresentOrElse(
            subCommand -> {
              if (!player.hasPermission(subCommand.getPermission())) {
                player.sendMessage(UtilChat.colorize(questsConfig.getNoPermission()));
                return;
              }

              subCommand.getCommandCallConsumer().accept(call);
            },
            () -> {
              player.sendMessage(UtilChat.colorize(questsConfig.getCommandDoesNotExist()));
            });
  }

  // This is the return for the tab completion list
  @Override
  public void accept(TabDynamic tabDynamic) {
    tabDynamic.add(
        "subcommands",
        (player) ->
            questsSubCommandCache.getSubCommands().stream()
                .filter(subCommand -> player.hasPermission(subCommand.getPermission()))
                .map(QuestsSubCommand::getCommand)
                .toList()
                .iterator());
  }
}
