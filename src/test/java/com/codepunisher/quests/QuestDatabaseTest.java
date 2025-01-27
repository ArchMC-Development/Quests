package com.codepunisher.quests;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.WorldMock;
import com.codepunisher.quests.cache.QuestPlayerCache;
import com.codepunisher.quests.config.QuestsConfig;
import com.codepunisher.quests.database.QuestDatabase;
import com.codepunisher.quests.database.QuestPlayerStorageDatabase;
import com.codepunisher.quests.database.QuestSignDatabase;
import com.codepunisher.quests.database.impl.QuestDatabaseImpl;
import com.codepunisher.quests.database.impl.QuestPlayerStorageDataImpl;
import com.codepunisher.quests.database.impl.QuestSignDatabaseImpl;
import com.codepunisher.quests.models.PlayerStorageData;
import com.codepunisher.quests.models.Quest;
import com.codepunisher.quests.models.QuestType;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.Statement;
import java.util.UUID;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

public class QuestDatabaseTest {
  @Mock private HikariDataSource hikariDataSource;

  @Mock private Connection connection;

  @Mock private Statement statement;

  @Mock private JavaPlugin plugin;

  private QuestsConfig questsConfig;
  private QuestDatabase questDatabase;
  private QuestPlayerStorageDatabase playerStorageDatabase;
  private QuestSignDatabase signDatabase;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.openMocks(this);
    MockBukkit.mock();
    when(hikariDataSource.getConnection()).thenReturn(connection);
    when(connection.createStatement()).thenReturn(statement);
    plugin = MockBukkit.createMockPlugin();
    questsConfig = new QuestsConfig(new QuestPlayerCache());
    questDatabase = new QuestDatabaseImpl(plugin, hikariDataSource);
    playerStorageDatabase = new QuestPlayerStorageDataImpl(plugin, hikariDataSource);
    signDatabase = new QuestSignDatabaseImpl(plugin, questsConfig, hikariDataSource);
  }

  @After
  public void tearDown() {
    hikariDataSource = null;
    connection = null;
    plugin = null;
    questDatabase = null;
    questsConfig = null;
    playerStorageDatabase = null;
    signDatabase = null;
  }

  @Test
  public void testCreateQuestTable() {
    try {
      questDatabase.createQuestTable();
      playerStorageDatabase.createTable();
      signDatabase.createSignTable();
      assertTrue(true);
    } finally {
      hikariDataSource.close();
    }
  }

  @Test
  public void testQuestInsert() {
    try {
      questDatabase.insert(getMockQuestObject());
      playerStorageDatabase.insert(UUID.randomUUID(), new PlayerStorageData());
      signDatabase.insert(new Location(new WorldMock(), 0, 0, 0));
      assertTrue(true);
    } catch (Exception e) {
      fail("Unexpected exception: " + e.getMessage());
    } finally {
      hikariDataSource.close();
    }
  }

  @Test
  public void testQuestRemove() {
    try {
      questDatabase.remove(getMockQuestObject());
      playerStorageDatabase.read(UUID.randomUUID());
      signDatabase.delete(new Location(new WorldMock(), 0, 0, 0));
      assertTrue(true);
    } catch (Exception e) {
      fail("Unexpected exception: " + e.getMessage());
    } finally {
      hikariDataSource.close();
    }
  }

  private Quest getMockQuestObject() {
    return new Quest(
        "test",
        "Test Quest",
        QuestType.BLOCK_BREAK,
        Material.DIAMOND_BLOCK,
        5,
        100,
        "quests.test",
        new String[] {"give %player% diamond 1", "give %player% emerald 1"});
  }
}
