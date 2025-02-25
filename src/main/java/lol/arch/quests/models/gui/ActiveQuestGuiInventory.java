package lol.arch.quests.models.gui;

import lol.arch.quests.models.ButtonType;
import lol.arch.quests.models.GuiType;
import lombok.Getter;
import org.bukkit.Sound;

import javax.annotation.Nullable;
import java.util.List;

@Getter
public class ActiveQuestGuiInventory extends GuiInventory {
    private final ButtonType buttonType;
    private final String name;
    private final List<String> lore;

    @Nullable private Sound joinSound;

    @Nullable private Sound leaveSound;

    @Nullable private Sound switchSound;

    public ActiveQuestGuiInventory(
            int size,
            String title,
            String name,
            List<String> lore,
            String joinSound,
            String leaveSound,
            String switchSound,
            String openSound) {
        super(size, title, GuiType.ACTIVE_QUESTS_MENU, openSound);
        this.buttonType = ButtonType.ACTIVE_QUEST;
        this.name = name;
        this.lore = lore;

        if (!joinSound.isEmpty()) this.joinSound = Sound.valueOf(joinSound.toUpperCase());
        if (!leaveSound.isEmpty()) this.leaveSound = Sound.valueOf(leaveSound.toUpperCase());
        if (!switchSound.isEmpty()) this.switchSound = Sound.valueOf(switchSound.toUpperCase());
    }
}
