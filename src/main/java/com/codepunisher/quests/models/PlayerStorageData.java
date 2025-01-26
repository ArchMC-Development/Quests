package com.codepunisher.quests.models;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;

/**
 * This is data that is more long form not temporary data like an active quest
 */
@Getter
@Setter
public class PlayerStorageData {
    @Nullable
    private String language;
    private int completedQuests;
}