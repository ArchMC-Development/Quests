package com.codepunisher.quests.commands.subcommands;

import com.codepunisher.quests.cache.QuestPlayerCache;
import com.codepunisher.quests.commands.QuestsSubCommand;
import com.codepunisher.quests.commands.lib.CommandCall;
import com.codepunisher.quests.config.QuestsConfig;
import com.codepunisher.quests.database.QuestPlayerStorageDatabase;
import com.codepunisher.quests.models.LangCmd;
import com.codepunisher.quests.models.PlayerStorageData;
import com.codepunisher.quests.util.UtilChat;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@AllArgsConstructor
public class QuestLanguageSubCommand implements QuestsSubCommand {
  private final JavaPlugin plugin;
  private final QuestsConfig questsConfig;
  private final QuestPlayerCache playerCache;
  private final QuestPlayerStorageDatabase storageDatabase;

  @Override
  public Consumer<CommandCall> getCommandCallConsumer() {
    return call -> {
      Player player = call.asPlayer();
      if (call.hasNoArgs() || !questsConfig.getLanguageCommandMap().containsKey(call.getArg(1))) {
        player.sendMessage(
            ChatColor.RED
                + "That language does not exist "
                + questsConfig.getLanguageCommandMap().keySet());
        return;
      }

      String language = call.getArg(1);
      UUID uuid = player.getUniqueId();
      PlayerStorageData storageData = playerCache.getPlayerStorageData(uuid);
      storageData.setLanguage(language);

      playerCache.addPlayerStorage(uuid, storageData);
      storageDatabase.insert(uuid, storageData);
      plugin
          .getLogger()
          .info(String.format("%s changed their language to %s", player.getName(), language));

      player.sendMessage(UtilChat.colorize("&aYour language has been set to " + language));
    };
  }
}
