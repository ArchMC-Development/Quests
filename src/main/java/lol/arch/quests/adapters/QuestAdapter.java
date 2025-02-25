package lol.arch.quests.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import lol.arch.quests.models.Quest;
import lol.arch.quests.models.QuestType;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

/**
 * Created on 25/02/2025
 *
 * @author Preva1l
 */
public class QuestAdapter extends TypeAdapter<Quest> {
    @Override
    public void write(JsonWriter out, Quest quest) throws IOException {
        if (quest == null) {
            out.nullValue();
            return;
        }

        out.beginObject();
        out.name("identifier").value(quest.getIdentifier().toString());
        out.name("id").value(quest.getId());
        out.name("displayName").value(quest.getDisplayName());
        out.name("questType").value(quest.getQuestType().name());
        out.name("associatedObject").value(quest.getQuestType().getInputFromAssociatedObject(quest.getAssociatedObject()));
        out.name("min").value(quest.getMin());
        out.name("max").value(quest.getMax());
        out.name("permission").value(quest.getPermission());
        out.name("rewards").value(Arrays.toString(quest.getRewards()));
        out.endObject();
    }

    @Override
    @Nullable
    public Quest read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        // mongo sorts the names alphabetically for some reason
        in.beginObject();

        if (!in.nextName().equals("_id")) {
            return readRedis(in);
        }

        UUID identifier = UUID.fromString(in.nextString());
        in.nextName();
        String obj = in.nextString();
        in.nextName();
        String name = in.nextString();
        in.nextName();
        String id = in.nextString();
        if (in.nextName().equals("identifier")) in.skipValue();
        int max = in.nextInt();
        in.nextName();
        int min = in.nextInt();
        in.nextName();
        String permission = in.nextString();
        in.nextName();
        QuestType questType = QuestType.valueOf(in.nextString().toUpperCase());
        Object associatedObject = questType.getAssociationFromInput(obj);
        in.nextName();
        String rewardArray = in.nextString();
        String[] rewards = rewardArray.substring(1, rewardArray.length() - 1).split(", ");
        in.endObject();
        return new Quest(identifier, id, name, questType, associatedObject, min, max, permission, rewards);
    }

    private Quest readRedis(JsonReader in) throws IOException {
        UUID identifier = UUID.fromString(in.nextString());
        in.nextName();
        String id = in.nextString();
        in.nextName();
        String name = in.nextString();
        in.nextName();
        QuestType questType = QuestType.valueOf(in.nextString().toUpperCase());
        in.nextName();
        Object associatedObject = questType.getAssociationFromInput(in.nextString());
        in.nextName();
        int min = in.nextInt();
        in.nextName();
        int max = in.nextInt();
        in.nextName();
        String permission = in.nextString();
        in.nextName();
        String rewardArray = in.nextString();
        String[] rewards = rewardArray.substring(1, rewardArray.length() - 1).split(", ");
        in.endObject();
        return new Quest(identifier, id, name, questType, associatedObject, min, max, permission, rewards);
    }
}
