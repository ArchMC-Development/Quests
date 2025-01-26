package com.codepunisher.quests.commands.lib;

import com.codepunisher.quests.config.QuestsConfig;
import com.codepunisher.quests.util.UtilChat;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;

public class CommandHandler extends org.bukkit.command.Command {
    private final Map<Object, TabDynamic> dynamicMap;
    private final Object object;
    private final Method method;
    private final Command command;
    private final JavaPlugin plugin;
    private final QuestsConfig questsConfig;

    protected CommandHandler(
            Map<Object, TabDynamic> dynamicMap,
            Object object,
            Method method,
            Command command,
            JavaPlugin plugin,
            QuestsConfig questsConfig,
            String... commands) {
        super(commands.length > 0 ? commands[0] : command.label());
        this.dynamicMap = dynamicMap;
        this.object = object;
        this.method = method;
        this.command = command;
        this.plugin = plugin;
        this.questsConfig = questsConfig;
        setPermission(command.permission());
        setDescription(command.description());

        if (commands.length > 0) {
            List<String> list = new ArrayList<>(List.of(commands));
            list.remove(0);
            setAliases(list);
        } else {
            setAliases(List.of(command.aliases()));
        }
    }

    @Override
    public boolean execute(
            @NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        try {
            if (command.player() && sender instanceof ConsoleCommandSender) {
                sender.sendMessage(UtilChat.colorize(questsConfig.getDefaultLang().getNoConsole()));
                return false;
            }

            if (!hasPermission(sender)) {
                sender.sendMessage(UtilChat.colorize(questsConfig.getLang(sender).getNoPermission()));
                return false;
            }

            method.invoke(object, new CommandCall(sender, args, getName()));
            return true;
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Command Error", e);
            return false;
        }
    }

    @Override
    public @NotNull List<String> tabComplete(
            @NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args)
            throws IllegalArgumentException {
        List<String> tabCompleteList = new ArrayList<>();
        if (!hasPermission(sender)) {
            return tabCompleteList;
        }

        String currentArgument = args[args.length - 1];
        for (CommandArgument commandArgument : command.commandArgumentList()) {
            int index = commandArgument.index();
            if (index != args.length - 1) {
                continue;
            }

            // Manual string list checks
            for (String s : commandArgument.values()) {
                if (s.toLowerCase().contains(currentArgument)) {
                    tabCompleteList.add(s);
                }
            }

            // For dynamic lists
            if (!commandArgument.dynamicId().isEmpty() && object instanceof Consumer) {
                dynamicMap
                        .get(object)
                        .get(commandArgument.dynamicId())
                        .ifPresent(
                                stringIterator -> {
                                    while (stringIterator.hasNext()) {
                                        String s = stringIterator.next();
                                        if (s == null) {
                                            continue;
                                        }

                                        if (s.toLowerCase().contains(currentArgument)) {
                                            tabCompleteList.add(s.toLowerCase());
                                        }
                                    }
                                });

                if (sender instanceof Player player) {
                    dynamicMap
                            .get(object)
                            .getPlayerList(player, commandArgument.dynamicId())
                            .ifPresent(
                                    stringIterator -> {
                                        while (stringIterator.hasNext()) {
                                            String s = stringIterator.next();
                                            if (s == null) {
                                                continue;
                                            }

                                            if (s.toLowerCase().contains(currentArgument)) {
                                                tabCompleteList.add(s.toLowerCase());
                                            }
                                        }
                                    });
                }
            }

            // Enum list
            Class<?> classEnum = commandArgument.enumClass();
            if (!(classEnum.equals(NoEnumClass.class)) && classEnum.isEnum()) {
                for (Object enumConstant : classEnum.getEnumConstants()) {
                    Enum<?> enumInstance = (Enum<?>) enumConstant;
                    String name = enumInstance.name();

                    if (name.toLowerCase().contains(currentArgument)) {
                        tabCompleteList.add(name.toLowerCase());
                    }
                }
            }
        }

        return tabCompleteList;
    }

    private boolean hasPermission(CommandSender sender) {
        if (command.permission().isEmpty()) {
            return true;
        }

        return sender.hasPermission(command.permission());
    }
}
