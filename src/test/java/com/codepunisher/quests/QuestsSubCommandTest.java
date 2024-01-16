package com.codepunisher.quests;

import com.codepunisher.quests.commands.QuestsSubCommand;
import com.codepunisher.quests.commands.QuestsSubCommandCache;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class QuestsSubCommandTest {
    private static final String SUB_COMMAND_TEST = "SubCommandTest";
    private QuestsSubCommandCache subCommandCache;
    private QuestsSubCommand mockSubCommand;

    @Before
    public void setUp() {
        subCommandCache = new QuestsSubCommandCache();
        mockSubCommand = Mockito.mock(QuestsSubCommand.class);
        Mockito.when(mockSubCommand.getCommand()).thenReturn(SUB_COMMAND_TEST);
    }

    @After
    public void tearDown() {
        subCommandCache = null;
    }

    @Test
    public void testRegistry() {
        subCommandCache.register(mockSubCommand);
        assertTrue(subCommandCache.getSubCommand(mockSubCommand.getCommand()).isPresent());
    }

    @Test
    public void testOptionalGetter() {
        // Making sure is empty if nothing added
        assertTrue(subCommandCache.getSubCommand(mockSubCommand.getCommand()).isEmpty());

        // Making sure is present if added
        subCommandCache.register(mockSubCommand);
        assertTrue(subCommandCache.getSubCommand(SUB_COMMAND_TEST).isPresent());
    }

    @Test
    public void testSubCommandsGetter() {
        // Testing via size of collection
        assertTrue(subCommandCache.getSubCommands().isEmpty());

        subCommandCache.register(mockSubCommand);
        assertFalse(subCommandCache.getSubCommands().isEmpty());
    }
}
