package lol.arch.quests.commands.subcommands;

import lol.arch.quests.commands.QuestsSubCommand;
import lol.arch.quests.commands.lib.CommandCall;
import lol.arch.quests.config.QuestsConfig;
import lol.arch.quests.data.QuestDataService;
import lol.arch.quests.menu.AreYouSureMenu;
import lol.arch.quests.models.Quest;
import lol.arch.quests.profile.ShortTermDataService;
import lol.arch.quests.sync.ActiveQuestHolder;
import lol.arch.quests.sync.ActiveQuestsDataSync;
import lol.arch.quests.util.UtilChat;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

@AllArgsConstructor
public class QuestDeleteSubCommand implements QuestsSubCommand {
    private final JavaPlugin plugin;
    private final QuestsConfig questsConfig;

    @Override
    public Consumer<CommandCall> getCommandCallConsumer() {
        return call -> {
            Player player = call.asPlayer();
            try {
                Quest quest = QuestDataService.INSTANCE.byVisualId(call.getArg(1)).join();
                if (quest == null) {
                    player.sendMessage("Quest doesnt exist");
                    return;
                }

                new AreYouSureMenu(
                        player,
                        questsConfig,
                        questsConfig.getLang(player).getAreYouSureDeleteInventory(),
                        () -> {
                            QuestDataService.INSTANCE.delete(quest);
                            deactivate(quest.getIdentifier());
                            plugin
                                    .getLogger()
                                    .info(
                                            String.format(
                                                    "%s just deleted the quest %s", player.getName(), quest.getId()));
                            player.sendMessage(
                                    UtilChat.colorize(
                                            questsConfig
                                                    .getLang(player)
                                                    .getQuestDeleted()
                                                    .replaceAll("%1%", quest.getId())));
                            player.closeInventory();
                        },
                        player::closeInventory)
                        .open(player);
            } catch (Exception e) {
                player.sendMessage(
                        UtilChat.colorize(
                                questsConfig
                                        .getLang(player)
                                        .getQuestDoesNotExist()
                                        .replaceAll(
                                                "%1%",
                                                QuestDataService.INSTANCE.getAll().join().values().stream().map(Quest::getId).toList().toString())));
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
