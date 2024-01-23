package com.codepunisher.quests;

import be.seeseemelk.mockbukkit.MockBukkit;
import com.codepunisher.quests.database.impl.QuestDatabaseImpl;
import com.codepunisher.quests.models.Quest;
import com.codepunisher.quests.models.QuestType;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.Statement;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

public class QuestDatabaseTest {
  @Mock private HikariDataSource hikariDataSource;

  @Mock private Connection connection;

  @Mock private Statement statement;

  @Mock private JavaPlugin plugin;

  private QuestDatabaseImpl questDatabase;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.openMocks(this);
    MockBukkit.mock();
    when(hikariDataSource.getConnection()).thenReturn(connection);
    when(connection.createStatement()).thenReturn(statement);
    plugin = MockBukkit.createMockPlugin();
    questDatabase = new QuestDatabaseImpl(plugin, hikariDataSource);
  }

  @After
  public void tearDown() {
    hikariDataSource = null;
    connection = null;
    plugin = null;
    questDatabase = null;
  }

  @Test
  public void testCreateQuestTable() {
    try {
      questDatabase.createQuestTable();
      assertTrue(true);
    } finally {
      hikariDataSource.close();
    }
  }

  @Test
  public void testQuestInsert() {
    try {
      Quest quest =
          new Quest(
              "test",
              QuestType.BLOCK_BREAK,
              Material.DIAMOND_BLOCK,
              5,
              100,
              "quests.test",
              new String[] {"give %player% diamond 1", "give %player% emerald 1"});

      questDatabase.insert(quest);
      assertTrue(true);
    } catch (Exception e) {
      fail("Unexpected exception: " + e.getMessage());
    } finally {
      hikariDataSource.close();
    }
  }
}
