package com.codepunisher.quests.menu;

import com.codepunisher.quests.cache.QuestCache;
import com.codepunisher.quests.util.ItemBuilder;
import fr.mrmicky.fastinv.FastInv;
import org.bukkit.inventory.ItemStack;

public class QuestsDailyCycleMenu extends FastInv {
    public QuestsDailyCycleMenu(QuestCache questCache) {
        super(27, "Test");

        questCache.getActiveQuestsEntrySet().forEach(entry -> {
            questCache.getQuest(entry.getKey()).ifPresent(quest -> {
                ItemStack item = ItemBuilder.of(quest.getDisplay())
                        .name("&a" + quest.getQuestType().name())
                        .lore("&7Requirement: " + entry.getValue())
                        .build();

                addItem(item);
            });
        });
    }
}
