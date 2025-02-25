package lol.arch.quests.commands.subcommands;

import lol.arch.quests.commands.QuestsSubCommand;
import lol.arch.quests.commands.lib.CommandCall;
import lol.arch.quests.config.QuestsConfig;
import lol.arch.quests.data.QuestDataService;
import lol.arch.quests.models.Quest;
import lol.arch.quests.profile.ShortTermDataService;
import lol.arch.quests.sync.ActiveQuestHolder;
import lol.arch.quests.sync.ActiveQuestsDataSync;
import lol.arch.quests.util.UtilChat;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

@AllArgsConstructor
public class QuestEditSubCommand implements QuestsSubCommand {
    private final QuestsConfig questsConfig;

    @Override
    public Consumer<CommandCall> getCommandCallConsumer() {
        return call -> {
            CommandSender sender = call.getSender();
            try {
                String mode = call.getArg(1);
                String id = call.getArg(2).toLowerCase();
                Quest quest = QuestDataService.INSTANCE.byVisualId(id).join();
                if (quest == null) {
                    sender.sendMessage(UtilChat.colorize("&cA quest with the id %s does not exist!".formatted(id)));
                    return;
                }
                String value = call.getStringFromArgumentsAtIndex(3);
                if (value == null) throw new RuntimeException();

                deactivate(quest.getIdentifier());
                Quest newQuest;
                if (mode.equalsIgnoreCase("displayname")) {
                    newQuest = new Quest(
                            quest.getIdentifier(),
                            quest.getId(),
                            value,
                            quest.getQuestType(),
                            quest.getAssociatedObject(),
                            quest.getMin(),
                            quest.getMax(),
                            quest.getPermission(),
                            quest.getRewards()
                    );
                } else if (mode.equalsIgnoreCase("rewards")) {
                    newQuest = new Quest(
                            quest.getIdentifier(),
                            quest.getId(),
                            quest.getDisplayName(),
                            quest.getQuestType(),
                            quest.getAssociatedObject(),
                            quest.getMin(),
                            quest.getMax(),
                            quest.getPermission(),
                            value.split(",")
                    );
                } else {
                    throw new RuntimeException();
                }

                QuestDataService.INSTANCE.save(newQuest);
                sender.sendMessage(
                        UtilChat.colorize(
                                questsConfig.getLang(sender).getQuestEditSuccess().replaceAll("%1%", id.replaceAll("_", " "))));
            } catch (RuntimeException e) {
                e.printStackTrace();
                sender.sendMessage(
                        UtilChat.colorize(questsConfig.getLang(sender).getInvalidQuestEditUsage())
                                .replaceAll(
                                        "%1%",
                                        "/" + call.getName() + " edit <displayname|rewards> <id> <value>"));
            }
        };
    }

    private void deactivate(UUID id) {
        ActiveQuestHolder holder = ActiveQuestsDataSync.INSTANCE.cached();
        holder.getActive().remove(id);
        holder.getCache().remove(id);
        ActiveQuestsDataSync.INSTANCE.sync(holder);
        ShortTermDataService.INSTANCE.getAll()
                .thenAccept(m -> m.forEach((uuid, profile) -> {
                    if (profile.getCurrentQuestId().equals(id.toString())) {
                        profile.optOutOfCurrentQuest();
                        Optional.ofNullable(Bukkit.getPlayer(uuid))
                                .ifPresent(player -> player.sendMessage(
                                        UtilChat.colorize(questsConfig.getLang(player).getQuestDeletedByAdmin())));
                    }
                }));
    }
}
