package com.codepunisher.quests;

import com.codepunisher.quests.commands.QuestsSubCommandCache;
import com.codepunisher.quests.commands.QuestsSuperCommand;
import org.bukkit.command.CommandSender;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertFalse;

public class QuestsCommandTest {
    private QuestsSubCommandCache subCommandCache;
    private QuestsSuperCommand superCommand;
    private CommandSender playerSender;
    private CommandSender consoleSender;

    @Before
    public void setUp() {
        subCommandCache = new QuestsSubCommandCache();
        superCommand = new QuestsSuperCommand(subCommandCache);
        playerSender = Mockito.mock(CommandSender.class);
    }

    @After
    public void tearDown() {
        subCommandCache = null;
        superCommand = null;
    }

    @Test
    public void testOnCommand() {
        //assertFalse(superCommand.onCommand());
    }
}

