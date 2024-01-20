package com.codepunisher.quests.config;

import dev.dejvokep.boostedyaml.YamlDocument;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Getter
public class QuestsConfig {
  private static final String LANG_FOLDER_NAME = "lang/";
  private List<String> questCommandsViewList;
  private List<String> questCommands;
  private String questSubCommandView;
  private String questReloadSubCommand;
  private String questReloadSubCommandUsage;
  private String questReloadSubCommandPermission;
  private String questConfigureSubCommand;
  private String questConfigureSubCommandUsage;
  private String questConfigureSubCommandPermission;
  private String questJoinSubCommand;
  private String questJoinSubCommandUsage;
  private String questJoinSubCommandPermission;
  private String questLeaveSubCommand;
  private String questLeaveSubCommandUsage;
  private String questLeaveSubCommandPermission;
  private String questStatusSubCommand;
  private String questStatusSubCommandUsage;
  private String questStatusSubCommandPermission;
  private String questMenuSubCommand;
  private String questMenuSubCommandUsage;
  private String questMenuSubCommandPermission;
  private String commandDoesNotExist;
  private String noPermission;
  private String noConsole;

  public void reload(JavaPlugin plugin) {
    try {
      YamlDocument config =
          YamlDocument.create(
              new File(plugin.getDataFolder(), "config.yml"),
              Objects.requireNonNull(plugin.getResource("config.yml")));

      // Handling language based on config setting
      config
          .getOptionalString(LANG_FOLDER_NAME.replace("/", ""))
          .ifPresent(
              language -> {
                reloadLanguage(plugin, language);
              });
    } catch (IOException e) {
      plugin.getLogger().severe("Error in yaml configuration " + e.getMessage());
    }
  }

  private void reloadLanguage(JavaPlugin plugin, String language) {
    try {
      String langFilePath = LANG_FOLDER_NAME + language;
      File languageFile = new File(plugin.getDataFolder(), langFilePath);

      // Loading folder/values if first time
      boolean isPluginLoadingForFirstTime =
          !new File(plugin.getDataFolder(), LANG_FOLDER_NAME).exists();
      if (isPluginLoadingForFirstTime) {
        YamlDocument newConfig =
            YamlDocument.create(
                languageFile, Objects.requireNonNull(plugin.getResource(langFilePath)));
        cacheLanguageConfigSettings(newConfig);
        return;
      }

      if (!languageFile.exists()) {
        plugin.getLogger().warning("Error: The file " + langFilePath + " does not exist!");
        return;
      }

      // Instantiating from existing config
      cacheLanguageConfigSettings(YamlDocument.create(languageFile));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void cacheLanguageConfigSettings(YamlDocument config) {
    this.questCommandsViewList = config.getStringList("messages.QuestCommandsViewList");
    this.questCommands = config.getStringList("messages.QuestCommands");
    this.questSubCommandView = config.getString("messages.QuestSubCommandView");
    this.questReloadSubCommand = config.getString("messages.QuestReloadSubCommand");
    this.questReloadSubCommandUsage = config.getString("messages.QuestReloadSubCommandUsage");
    this.questReloadSubCommandPermission =
        config.getString("messages.QuestReloadSubCommandPermission");
    this.questConfigureSubCommand = config.getString("messages.QuestConfigureSubCommand");
    this.questConfigureSubCommandUsage = config.getString("messages.QuestConfigureSubCommandUsage");
    this.questConfigureSubCommandPermission =
        config.getString("messages.QuestConfigureSubCommandPermission");
    this.questJoinSubCommand = config.getString("messages.QuestJoinSubCommand");
    this.questJoinSubCommandUsage = config.getString("messages.QuestJoinSubCommandUsage");
    this.questJoinSubCommandPermission = config.getString("messages.QuestJoinSubCommandPermission");
    this.questLeaveSubCommand = config.getString("messages.QuestLeaveSubCommand");
    this.questLeaveSubCommandUsage = config.getString("messages.QuestLeaveSubCommandUsage");
    this.questLeaveSubCommandPermission =
        config.getString("messages.QuestLeaveSubCommandPermission");
    this.questStatusSubCommand = config.getString("messages.QuestStatusSubCommand");
    this.questStatusSubCommandUsage = config.getString("messages.QuestStatusSubCommandUsage");
    this.questStatusSubCommandPermission =
        config.getString("messages.QuestStatusSubCommandPermission");
    this.questMenuSubCommand = config.getString("messages.QuestMenuSubCommand");
    this.questMenuSubCommandUsage = config.getString("messages.QuestMenuSubCommandUsage");
    this.questMenuSubCommandPermission = config.getString("messages.QuestMenuSubCommandPermission");
    this.commandDoesNotExist = config.getString("messages.CommandDoesNotExist");
    this.noPermission = config.getString("messages.NoPermission");
    this.noConsole = config.getString("messages.NoConsole");
  }
}
