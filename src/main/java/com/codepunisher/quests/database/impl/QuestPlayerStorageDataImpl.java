package com.codepunisher.quests.database.impl;

import com.codepunisher.quests.database.QuestPlayerStorageDatabase;
import com.codepunisher.quests.models.PlayerStorageData;
import com.zaxxer.hikari.HikariDataSource;
import lombok.AllArgsConstructor;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
public class QuestPlayerStorageDataImpl implements QuestPlayerStorageDatabase {
  private final JavaPlugin plugin;
  private final HikariDataSource hikariDataSource;

  @Override
  public void createTable() {
    plugin
        .getServer()
        .getScheduler()
        .runTaskAsynchronously(
            plugin,
            () -> {
              try (Connection connection = hikariDataSource.getConnection()) {
                Statement statement = connection.createStatement();
                String createTableQuery =
                    "CREATE TABLE IF NOT EXISTS quests_player_storage ("
                        + "uuid VARCHAR(36) PRIMARY KEY,"
                        + "lang VARCHAR(16),"
                        + "completed_quests INT"
                        + ")";

                statement.executeUpdate(createTableQuery);
                plugin.getLogger().info("Quest player storage table successfully created!");
              } catch (SQLException e) {
                plugin
                    .getLogger()
                    .severe(
                        "Error when loading quest player storage table! "
                            + Arrays.toString(e.getStackTrace()));
              }
            });
  }

  @Override
  public void insert(UUID uuid, PlayerStorageData storageData) {
    plugin
        .getServer()
        .getScheduler()
        .runTaskAsynchronously(
            plugin,
            () -> {
              try (Connection connection = hikariDataSource.getConnection()) {
                String insertQuery =
                    "INSERT INTO quests_player_storage (uuid, lang, completed_quests) VALUES (?, ?, ?) "
                        + "ON DUPLICATE KEY UPDATE lang=VALUES(lang), completed_quests=VALUES(completed_quests)";

                try (PreparedStatement preparedStatement =
                    connection.prepareStatement(insertQuery)) {
                  preparedStatement.setString(1, uuid.toString());
                  preparedStatement.setString(2, storageData.getLanguage());
                  preparedStatement.setInt(3, storageData.getCompletedQuests());
                  preparedStatement.executeUpdate();
                  plugin
                      .getLogger()
                      .info(
                          "Player storage successfully inserted/updated in the database: " + uuid);
                } catch (SQLException e) {
                  plugin
                      .getLogger()
                      .severe(
                          "Error when inserting/updating player storage in the database! "
                              + uuid
                              + " "
                              + e.getMessage()
                              + " "
                              + Arrays.toString(e.getStackTrace()));
                }
              } catch (SQLException e) {
                plugin
                    .getLogger()
                    .severe(
                        "Error when establishing a database connection! "
                            + uuid
                            + " "
                            + e.getMessage()
                            + " "
                            + Arrays.toString(e.getStackTrace()));
              }
            });
  }

  @Override
  public CompletableFuture<Optional<PlayerStorageData>> read(UUID uuid) {
    CompletableFuture<Optional<PlayerStorageData>> future = new CompletableFuture<>();

    plugin
        .getServer()
        .getScheduler()
        .runTaskAsynchronously(
            plugin,
            () -> {
              try (Connection connection = hikariDataSource.getConnection()) {
                String selectQuery = "SELECT * FROM quests_player_storage WHERE uuid = ?";

                try (PreparedStatement preparedStatement =
                    connection.prepareStatement(selectQuery)) {
                  preparedStatement.setString(1, uuid.toString());

                  try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                      PlayerStorageData storageData = new PlayerStorageData();
                      storageData.setLanguage(resultSet.getString("lang"));
                      storageData.setCompletedQuests(resultSet.getInt("completed_quests"));

                      future.complete(Optional.of(storageData));
                    } else {
                      future.complete(Optional.empty());
                    }
                  }
                } catch (SQLException e) {
                  future.completeExceptionally(e);
                }
              } catch (SQLException e) {
                future.completeExceptionally(e);
              }
            });

    return future;
  }
}
