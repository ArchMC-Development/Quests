package lol.arch.quests.commands.subcommands;

import lol.arch.quests.commands.QuestsSubCommand;
import lol.arch.quests.commands.lib.CommandCall;
import lol.arch.quests.config.QuestsConfig;
import lol.arch.quests.data.QuestDataService;
import lol.arch.quests.models.Quest;
import lol.arch.quests.models.QuestType;
import lol.arch.quests.util.UtilChat;
import lombok.AllArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

@AllArgsConstructor
public class QuestsAddSubCommand implements QuestsSubCommand {
    private final JavaPlugin plugin;
    private final QuestsConfig questsConfig;

    @Override
    public Consumer<CommandCall> getCommandCallConsumer() {
        return call -> {
            CommandSender sender = call.getSender();
            try {
                String id = call.getArg(1).toLowerCase();
                Quest q = QuestDataService.INSTANCE.byVisualId(id).join();
                if (q != null) {
                    sender.sendMessage(UtilChat.colorize("&cThat quest id already exists!"));
                    return;
                }

                QuestType questType = QuestType.valueOf(call.getArg(2).toUpperCase());
                Object associatedObject = questType.getAssociationFromInput(call.getArg(3));
                int min = Integer.parseInt(call.getArg(4));
                int max = Integer.parseInt(call.getArg(5));
                String permission = call.getArg(6);
                String[] consoleCommandRewards =
                        Objects.requireNonNull(call.getStringFromArgumentsAtIndex(7)).split(",");

                Quest quest =
                        new Quest(UUID.randomUUID(), id, id, questType, associatedObject, min, max, permission, consoleCommandRewards);
                QuestDataService.INSTANCE.save(quest).exceptionally(t -> {
                    t.printStackTrace();
                    return null;
                });
                plugin
                        .getLogger()
                        .info(String.format("%s has been added by %s", quest.getId(), sender.getName()));
                sender.sendMessage(
                        UtilChat.colorize(
                                questsConfig.getLang(sender).getQuestAddSuccess().replaceAll("%1%", id.replaceAll("_", " "))));
            } catch (RuntimeException e) {
                e.printStackTrace();
                String options = Arrays.toString(QuestType.values());
                sender.sendMessage(
                        UtilChat.colorize(questsConfig.getLang(sender).getInvalidQuestAddUsage())
                                .replaceAll(
                                        "%1%",
                                        "/"
                                                + call.getName()
                                                + " add <id> "
                                                + options
                                                + " <association> <min> <max> <permission> <console-command-rewards>"));
            }
        };
    }
}
