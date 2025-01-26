package com.codepunisher.quests.adapters;

import com.codepunisher.quests.models.Quest;
import com.codepunisher.quests.models.QuestType;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Arrays;

public class QuestAdapter extends TypeAdapter<Quest> {
    @Override
    public void write(JsonWriter out, Quest quest) throws IOException {
        if (quest == null) {
            out.nullValue();
            return;
        }

        out.value(quest.getId());
        out.value(quest.getQuestType().name());
        out.value(quest.getQuestType().getInputFromAssociatedObject(quest.getAssociatedObject()));
        out.value(quest.getMin());
        out.value(quest.getMax());
        out.value(quest.getPermission());
        out.value(Arrays.toString(quest.getRewards()));
    }

    @Override
    @Nullable
    public Quest read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }

        String id = in.nextString();
        QuestType questType = QuestType.valueOf(in.nextString().toUpperCase());
        Object associatedObject = questType.getAssociationFromInput(in.nextString());
        int min = in.nextInt();
        int max = in.nextInt();
        String permission = in.nextString();
        String[] rewards = in.nextString().substring(1, in.nextString().length() - 1).split(", ");
        return new Quest(id, questType, associatedObject, min, max, permission, rewards);
    }
}
