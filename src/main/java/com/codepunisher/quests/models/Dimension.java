package com.codepunisher.quests.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Dimension {
    OVERWORLD("Overworld"),
    NETHER("Nether"),
    THE_END("The End")
    ;
    private final String name;
}
