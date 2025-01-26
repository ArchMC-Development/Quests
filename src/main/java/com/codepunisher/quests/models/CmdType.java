package com.codepunisher.quests.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CmdType {
    RESET("ResetSubCommand", true),
    ADD("AddSubCommand", true),
    DELETE("DeleteSubCommand", false),
    MENU("MenuSubCommand", false),
    LANGUAGE("LanguageSubCommand", false),
    RELOAD("ReloadSubCommand", false);

    private final String configPath;
    private final boolean console;
}
