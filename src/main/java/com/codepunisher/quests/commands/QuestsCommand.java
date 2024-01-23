package com.codepunisher.quests.commands;

import com.codepunisher.quests.cache.QuestSubCommandCache;
import com.codepunisher.quests.commands.lib.Command;
import com.codepunisher.quests.commands.lib.CommandArgument;
import com.codepunisher.quests.commands.lib.CommandCall;
import com.codepunisher.quests.commands.lib.TabDynamic;
import com.codepunisher.quests.commands.subcommands.QuestLanguageSubCommand;
import com.codepunisher.quests.config.QuestsConfig;
import com.codepunisher.quests.models.LangCmd;
import com.codepunisher.quests.util.UtilChat;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@AllArgsConstructor
public class QuestsCommand implements Consumer<TabDynamic> {
  private final JavaPlugin plugin;
  private final QuestsConfig questsConfig;
  private final QuestSubCommandCache subCommandCache;

  @Command(
      label = "%s",
      player = true,
      commandArgumentList = {@CommandArgument(index = 0, dynamicId = "subcommands")})
  public void onPrimaryCommand(CommandCall call) {
    Player player = call.asPlayer();
    String language = getLanguage(player);
    LangCmd langCmd = questsConfig.getLanguageCommandMap().get(language);

    if (call.hasNoArgs()) {
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
                            player.sendMessage(UtilChat.colorize(display));
                          });
                } else {
                  player.sendMessage(UtilChat.colorize(s));
                }
              });
      return;
    }

    Optional.ofNullable(langCmd.getSubCommands().get(call.getArg(0)))
        .ifPresentOrElse(
            (subCommand -> {
              if (!player.hasPermission(subCommand.getPermission())) {
                player.sendMessage(UtilChat.colorize(questsConfig.getNoPermission()));
                return;
              }

              subCommandCache
                  .getQuestSubCommand(subCommand.getType())
                  .ifPresent(
                      questSubCommand -> {
                        questSubCommand.getCommandCallConsumer().accept(call);
                      });
            }),
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
            questsConfig
                .getLanguageCommandMap()
                .get(getLanguage(player))
                .getSubCommands()
                .entrySet()
                .stream()
                .filter(entry -> player.hasPermission(entry.getValue().getPermission()))
                .map(Map.Entry::getKey)
                .toList()
                .iterator());
  }

  private String getLanguage(Player player) {
    String language = QuestLanguageSubCommand.playerTempLanguageMap.get(player.getUniqueId());
    if (language == null) {
      return "en.yml";
    }

    return language;
  }
}
