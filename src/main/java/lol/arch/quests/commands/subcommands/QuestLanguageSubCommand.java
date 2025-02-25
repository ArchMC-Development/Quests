package lol.arch.quests.commands.subcommands;

import lol.arch.quests.commands.QuestsSubCommand;
import lol.arch.quests.commands.lib.CommandCall;
import lol.arch.quests.config.QuestsConfig;
import lol.arch.quests.profile.QuestPlayerDataService;
import lol.arch.quests.profile.QuestProfile;
import lol.arch.quests.util.UtilChat;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.function.Consumer;

@AllArgsConstructor
public class QuestLanguageSubCommand implements QuestsSubCommand {
    private final JavaPlugin plugin;
    private final QuestsConfig questsConfig;

    @Override
    public Consumer<CommandCall> getCommandCallConsumer() {
        return call -> {
            Player player = call.asPlayer();
            if (call.getArgs().length <= 1
                    || !questsConfig.getLanguageCommandMap().containsKey(call.getArg(1) + ".yml")) {
                player.sendMessage(
                        UtilChat.colorize(
                                questsConfig
                                        .getLang(player)
                                        .getLanguageDoesNotExist()
                                        .replace(
                                                "%1%",
                                                questsConfig.getLanguageCommandMap().keySet().stream()
                                                        .map(key -> key.replace(".yml", ""))
                                                        .toList()
                                                        .toString())));

                return;
            }

            String language = call.getArg(1);
            UUID uuid = player.getUniqueId();
            QuestPlayerDataService.INSTANCE.byId(uuid).thenAccept(profile -> {
                if (profile == null) profile = new QuestProfile(uuid, "en",0);
                profile.setLanguage(language);
                profile.save();
            });
            plugin
                    .getLogger()
                    .info(String.format("%s changed their language to %s", player.getName(), language));

            player.sendMessage(
                    UtilChat.colorize(
                            questsConfig.getLang(player).getLanguageChangeSuccess().replaceAll("%1%", language)));
        };
    }
}
