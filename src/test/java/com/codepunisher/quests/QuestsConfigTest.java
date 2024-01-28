package com.codepunisher.quests;

import be.seeseemelk.mockbukkit.MockBukkit;
import com.codepunisher.quests.cache.QuestPlayerCache;
import com.codepunisher.quests.config.QuestsConfig;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.assertNotNull;

public class QuestsConfigTest {
  private JavaPlugin plugin;
  private QuestsConfig questsConfig;

  @Before
  public void setUp() {
    MockBukkit.mock();
    plugin = MockBukkit.createMockPlugin();
    questsConfig = new QuestsConfig(new QuestPlayerCache());
  }

  @After
  public void tearDown() {
    MockBukkit.unmock();
    this.plugin = null;
    this.questsConfig = null;
  }

  @Test
  public void questReloadTest() throws IllegalAccessException {
    questsConfig.reload(plugin);

    for (Field field : questsConfig.getClass().getDeclaredFields()) {
      field.setAccessible(true);
      assertNotNull(field.get(questsConfig));
    }
  }
}
