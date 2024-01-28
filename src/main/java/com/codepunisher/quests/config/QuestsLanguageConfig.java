package com.codepunisher.quests.config;

import com.codepunisher.quests.models.gui.ActiveQuestGuiInventory;
import com.codepunisher.quests.models.gui.GuiInventory;
import com.codepunisher.quests.models.GuiItem;
import com.codepunisher.quests.models.LangCmd;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QuestsLanguageConfig {
    private LangCmd langCmd;
    private List<GuiItem> backGroundItems;
    private GuiInventory areYouSureDeleteInventory;
    private GuiInventory areYouSureLeaveInventory;
    private GuiInventory areYouSureSwitchInventory;
    private ActiveQuestGuiInventory activeQuestGuiInventory;
    private String commandDoesNotExist;
    private String noPermission;
    private String noConsole;
    private String questDeleted;
    private String questDeletedByAdmin;
    private String questDoesNotExist;
    private String languageDoesNotExist;
    private String languageChangeSuccess;
    private String questsResetSuccess;
    private String questsResetToPlayers;
    private String questAddSuccess;
    private String invalidQuestAddUsage;
    private String questJoin;
    private String questSwitch;
    private String questLeave;
    private String questSignConfiguration;
    private String questCompleteTopTitle;
    private String questCompleteSubTitle;
    private String questCompletedAll;
    private String questProgressActionBar;
    private String questBossBar;
    private String questSignUpdate;
    private List<String> questSign;
}
