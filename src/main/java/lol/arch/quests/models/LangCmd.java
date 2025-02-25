package lol.arch.quests.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@AllArgsConstructor
public class LangCmd {
    private final Set<String> primaryCommands;
    private final Map<String, SubCommand> subCommands;
    private List<String> questCommandsViewList;
    private String questSubCommandView;

    @Getter
    public static class SubCommand {
        private final String usage;
        private final String permission;
        private final CmdType type;

        public SubCommand(String usage, String permission, CmdType type) {
            this.usage = usage;
            this.permission = permission;
            this.type = type;
        }
    }
}
