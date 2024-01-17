package com.codepunisher.quests;

import be.seeseemelk.mockbukkit.MockBukkit;
import com.codepunisher.quests.config.QuestsConfig;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class QuestsConfigTest {
    private JavaPlugin plugin;
    private QuestsConfig questsConfig;

    @Before
    public void setUp() {
        MockBukkit.mock();
        plugin = MockBukkit.createMockPlugin();
        questsConfig = new QuestsConfig();
    }

    @After
    public void tearDown() {
        MockBukkit.unmock();
        this.plugin = null;
        this.questsConfig = null;
    }

    @Test
    public void questReloadTest() {
        questsConfig.reload(plugin);
        assertNotNull(questsConfig.getHelloMessage());
    }
}
