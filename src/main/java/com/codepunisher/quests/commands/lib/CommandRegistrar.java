package com.codepunisher.quests.commands.lib;

import com.codepunisher.quests.QuestsPlugin;
import com.codepunisher.quests.config.QuestsConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;

public class CommandRegistrar {
  private static CommandMap COMMAND_MAP;
  private final Map<Object, TabDynamic> tabDynamicMap;
  private final JavaPlugin plugin;
  private final QuestsConfig questsConfig;

  public CommandRegistrar(JavaPlugin plugin, QuestsConfig questsConfig) {
    this.tabDynamicMap = new HashMap<>();
    this.plugin = plugin;
    this.questsConfig = questsConfig;
  }

  /**
   * @param object the object of the class that gets iterated through
   * @param commands This option is designed for a single command to reside within a class, and this
   *     will override the commands/aliases of all commands in the class.
   */
  @SuppressWarnings("unchecked")
  public void registerCommands(Object object, String... commands) {
    // Tab dynamic consideration (this is basically filling it)
    if (object instanceof Consumer) {
      Consumer<TabDynamic> consumer = (Consumer<TabDynamic>) object;
      TabDynamic tabDynamic = new TabDynamicImpl();
      consumer.accept(tabDynamic);
      tabDynamicMap.put(object, tabDynamic);
    }

    for (Method method : object.getClass().getDeclaredMethods()) {
      if (method.isAnnotationPresent(Command.class)) {
        Command command = method.getAnnotation(Command.class);
        CommandHandler commandHandler =
            new CommandHandler(
                tabDynamicMap, object, method, command, plugin, questsConfig, commands);

        // Unregistering before registering. This allows for plugin reloading/overriding commands
        unregisterCommand(command.label());

        // Now registering command
        COMMAND_MAP.register("", commandHandler);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private void unregisterCommand(String command) {
    Field commandMap;
    Field knownCommands;
    try {
      commandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
      commandMap.setAccessible(true);
      knownCommands = SimpleCommandMap.class.getDeclaredField("knownCommands");
      knownCommands.setAccessible(true);
      ((Map<String, Command>) knownCommands.get(commandMap.get(Bukkit.getServer())))
          .remove(command);
    } catch (Exception e) {
      plugin.getLogger().log(Level.WARNING, "Error with unregistering", e);
    }
  }

  static {
    try {
      final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
      bukkitCommandMap.setAccessible(true);
      COMMAND_MAP = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
    } catch (Exception e) {
      QuestsPlugin.getPlugin(QuestsPlugin.class)
          .getLogger()
          .log(Level.WARNING, "Error with command map", e);
    }
  }
}
