package com.codepunisher.quests.config;

import com.codepunisher.quests.models.CmdType;
import com.codepunisher.quests.models.GuiInventory;
import com.codepunisher.quests.models.GuiItem;
import com.codepunisher.quests.models.LangCmd;
import dev.dejvokep.boostedyaml.YamlDocument;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

//TODO: Menu configuration with multi-language support
//TODO: Separate menu system between lang/config
//TODO: Back button support
//TODO: Async
@Getter
public class QuestsConfig {
  private static final String LANG_FOLDER_NAME = "lang/";
  private static final String CONFIG_COMMANDS_PATH = "commands";
  private static final String MESSAGES_COMMANDS_PATH = "messages.commands";

  // ----- ( COMMANDS ) -----
  // Key -> language from file name
  // Value -> Language command object (all commands for language)
  private final Map<String, LangCmd> languageCommandMap = new HashMap<>();

  // ----- ( MENU ) -----
  private List<GuiItem> backGroundItems;
  private GuiInventory configurationInventory;
  private GuiInventory configurationEditCategoryInventory;

  // ----- ( GENERAL MESSAGES ) -----
  private String commandDoesNotExist;
  private String noPermission;
  private String noConsole;

  public void reload(JavaPlugin plugin) {
    try {
      YamlDocument defaultConfig =
          YamlDocument.create(
              new File(plugin.getDataFolder(), "config.yml"),
              Objects.requireNonNull(plugin.getResource("config.yml")));

      loadAllMessageYamlIntoCache(defaultConfig, plugin);
    } catch (IOException e) {
      plugin.getLogger().severe("Error in yaml configuration " + e.getMessage());
      throw new RuntimeException(e);
    }
  }

  // Loads if it does not exist, pulls if already exists
  private void loadAllMessageYamlIntoCache(YamlDocument defaultConfig, JavaPlugin plugin)
      throws IOException {
    Optional<List<String>> optionalLangList = defaultConfig.getOptionalStringList("lang");
    if (optionalLangList.isEmpty()) {
      plugin.getLogger().severe("The \"lang\" option in the config.yml does not exist!");
      return;
    }

    // Loading folder/values if first time
    boolean isPluginLoadingForFirstTime =
        !new File(plugin.getDataFolder(), LANG_FOLDER_NAME).exists();

    for (String langFile : optionalLangList.get()) {
      String langFilePath = LANG_FOLDER_NAME + langFile;
      File languageFile = new File(plugin.getDataFolder(), langFilePath);

      if (isPluginLoadingForFirstTime) {
        YamlDocument messagesConfig =
            YamlDocument.create(
                languageFile, Objects.requireNonNull(plugin.getResource(langFilePath)));
        loadMessageYamlSettingsIntoCache(defaultConfig, messagesConfig, langFile);
        continue;
      }

      if (!languageFile.exists()) {
        plugin.getLogger().warning("The file " + langFilePath + " does not exist!");
        continue;
      }

      // Instantiating from existing config
      loadMessageYamlSettingsIntoCache(defaultConfig, YamlDocument.create(languageFile), langFile);
    }
  }

  /**
   * This loads all related values from the messages yaml file into cache, but also utilizes the
   * default config for a few of the cross-over objects such as commands and inventories, which need
   * to be loaded into the same object
   */
  private void loadMessageYamlSettingsIntoCache(
      YamlDocument defaultConfig, YamlDocument messageConfig, String langKey) {
    // ----- ( COMMANDS ) -----
    languageCommandMap.put(langKey, getLandCmdFromLangFile(defaultConfig, messageConfig));

    // ----- ( MENU ) -----
    List<GuiItem> backGroundItems = new ArrayList<>();
    messageConfig
        .getSection("messages.GenericBackGroundItems")
        .getRoutesAsStrings(false)
        .forEach(
            s -> {
              backGroundItems.add(
                  getGuiItemFromConfigPath("messages.GenericBackGroundItems." + s, messageConfig));
            });
    this.backGroundItems = backGroundItems;
    this.configurationInventory =
        getGuiInventoryFromPath("messages.QuestConfigurationMenu", messageConfig);
    this.configurationEditCategoryInventory =
        getGuiInventoryFromPath("messages.QuestConfigurationCategoryMenu", messageConfig);

    // ----- ( GENERAL MESSAGES ) -----
    this.commandDoesNotExist = messageConfig.getString("messages.CommandDoesNotExist");
    this.noPermission = messageConfig.getString("messages.NoPermission");
    this.noConsole = messageConfig.getString("messages.NoConsole");
  }

  // LangCmd object per language file
  private LangCmd getLandCmdFromLangFile(YamlDocument defaultConfig, YamlDocument messageConfig) {
    Set<String> primaryCommands =
        new HashSet<>(messageConfig.getStringList(MESSAGES_COMMANDS_PATH + ".QuestCommands"));
    Map<String, LangCmd.SubCommand> langSubCommands = new HashMap<>();
    for (CmdType cmdType : CmdType.values()) {
      String command =
          messageConfig.getString(
              MESSAGES_COMMANDS_PATH + "." + cmdType.getConfigPath() + ".Command");
      String usage =
          messageConfig.getString(
              MESSAGES_COMMANDS_PATH + "." + cmdType.getConfigPath() + ".Usage");
      String permission =
          defaultConfig.getString(
              CONFIG_COMMANDS_PATH + "." + cmdType.getConfigPath() + ".Permission");
      langSubCommands.put(
          command.toLowerCase(), new LangCmd.SubCommand(usage, permission, cmdType));
    }

    return new LangCmd(
        primaryCommands,
        langSubCommands,
        messageConfig.getStringList(MESSAGES_COMMANDS_PATH + ".QuestCommandsViewList"),
        messageConfig.getString(MESSAGES_COMMANDS_PATH + ".QuestSubCommandView"));
  }

  private GuiInventory getGuiInventoryFromPath(String path, YamlDocument config) {
    List<GuiItem> guiItems = new ArrayList<>();
    config
        .getSection(path + ".GuiItems")
        .getRoutesAsStrings(false)
        .forEach(
            s -> {
              guiItems.add(getGuiItemFromConfigPath(path + ".GuiItems." + s, config));
            });

    int size = config.getInt(path + ".Size");
    String title = config.getString(path + ".Title");
    String openSound = config.getString(path + ".OpenSound");
    String guiType = config.getString(path + ".MenuType");

    return openSound == null
        ? new GuiInventory(guiItems, size, title, guiType)
        : new GuiInventory(guiItems, size, title, guiType, openSound);
  }

  private GuiItem getGuiItemFromConfigPath(String path, YamlDocument config) {
    Material material = Material.valueOf(config.getString(path + ".Material").toUpperCase());
    String name = config.getString(path + ".Name");
    List<String> lore = config.getStringList(path + ".Lore");
    List<String> menuTypes = config.getStringList(path + ".MenuTypes");
    List<Integer> slots = config.getIntList(path + ".Slots");
    String clickSound = config.getString(path + ".ClickSound");
    String buttonType = config.getString(path + ".ButtonType");
    boolean closeOnClick = config.getBoolean(path + ".CloseOnClick");

    return clickSound == null
        ? new GuiItem(material, name, lore, menuTypes, slots, buttonType, closeOnClick)
        : new GuiItem(material, name, lore, menuTypes, slots, buttonType, closeOnClick, clickSound);
  }
}
