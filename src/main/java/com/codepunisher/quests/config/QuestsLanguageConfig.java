package com.codepunisher.quests.config;

import com.codepunisher.quests.models.GuiInventory;
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
    private String commandDoesNotExist;
    private String noPermission;
    private String noConsole;
    private String questDeleted;
    private String questDeletedByAdmin;
    private String questDoesNotExist;
    private boolean displayMenuWhenNoArguments;
}
