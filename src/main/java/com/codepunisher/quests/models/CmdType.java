package com.codepunisher.quests.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CmdType {
  RELOAD("ReloadSubCommand"),
  RESET("ResetSubCommand"),
  ADD("AddSubCommand"),
  DELETE("DeleteSubCommand"),
  STATUS("StatusSubCommand"),
  MENU("MenuSubCommand"),
  LANGUAGE("LanguageSubCommand");

  private final String configPath;
}
