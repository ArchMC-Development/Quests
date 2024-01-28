package com.codepunisher.quests.database.impl;

import com.codepunisher.quests.config.QuestsConfig;
import com.codepunisher.quests.database.QuestSignDatabase;
import com.zaxxer.hikari.HikariDataSource;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
public class QuestSignDatabaseImpl implements QuestSignDatabase {
  private final JavaPlugin plugin;
  private final QuestsConfig questsConfig;
  private final HikariDataSource hikariDataSource;

  @Override
  public void createSignTable() {
    plugin
        .getServer()
        .getScheduler()
        .runTaskAsynchronously(
            plugin,
            () -> {
              try (Connection connection = hikariDataSource.getConnection()) {
                Statement statement = connection.createStatement();
                String createTableQuery =
                    "CREATE TABLE IF NOT EXISTS quests_signs ("
                        + "server VARCHAR(16) PRIMARY KEY,"
                        + "world VARCHAR(16),"
                        + "x DOUBLE,"
                        + "y DOUBLE,"
                        + "z DOUBLE"
                        + ")";

                statement.executeUpdate(createTableQuery);
                plugin.getLogger().info("Quest sign table successfully created!");
              } catch (SQLException e) {
                plugin
                    .getLogger()
                    .severe(
                        "Error when loading quest sign table! "
                            + Arrays.toString(e.getStackTrace()));
              }
            });
  }

  @Override
  public void insert(Location location) {
    plugin
        .getServer()
        .getScheduler()
        .runTaskAsynchronously(
            plugin,
            () -> {
              try (Connection connection = hikariDataSource.getConnection()) {
                String insertQuery =
                    "INSERT INTO quests_signs (server, world, x, y, z) VALUES (?, ?, ?, ?, ?)";

                try (PreparedStatement preparedStatement =
                    connection.prepareStatement(insertQuery)) {
                  preparedStatement.setString(1, questsConfig.getServer());
                  preparedStatement.setString(2, location.getWorld().getName());
                  preparedStatement.setDouble(3, location.getX());
                  preparedStatement.setDouble(4, location.getY());
                  preparedStatement.setDouble(5, location.getZ());

                  preparedStatement.executeUpdate();
                  plugin.getLogger().info("Sign successfully inserted into the database!");
                } catch (SQLException e) {
                }
              } catch (SQLException e) {
                plugin
                    .getLogger()
                    .severe(
                        "Error when inserting sign location into the database! "
                            + Arrays.toString(e.getStackTrace()));
              }
            });
  }

  @Override
  public void delete(Location location) {
    plugin
        .getServer()
        .getScheduler()
        .runTaskAsynchronously(
            plugin,
            () -> {
              try (Connection connection = hikariDataSource.getConnection()) {
                String deleteQuery =
                    "DELETE FROM quests_signs WHERE server = ? AND world = ? AND x = ? AND y = ? AND z = ?";

                try (PreparedStatement preparedStatement =
                    connection.prepareStatement(deleteQuery)) {
                  preparedStatement.setString(1, questsConfig.getServer());
                  preparedStatement.setString(2, location.getWorld().getName());
                  preparedStatement.setDouble(3, location.getX());
                  preparedStatement.setDouble(4, location.getY());
                  preparedStatement.setDouble(5, location.getZ());

                  int rowsAffected = preparedStatement.executeUpdate();

                  if (rowsAffected > 0) {
                    plugin.getLogger().info("Sign successfully deleted from the database!");
                  } else {
                    plugin.getLogger().warning("Sign not found in the database for deletion.");
                  }
                } catch (SQLException e) {
                  plugin
                      .getLogger()
                      .severe(
                          "Error when deleting sign location from the database! "
                              + Arrays.toString(e.getStackTrace()));
                }
              } catch (SQLException e) {
                plugin
                    .getLogger()
                    .severe(
                        "Error when deleting sign location from the database! "
                            + Arrays.toString(e.getStackTrace()));
              }
            });
  }

  @Override
  public CompletableFuture<List<Location>> getSignLocations() {
    CompletableFuture<List<Location>> future = new CompletableFuture<>();

    plugin
        .getServer()
        .getScheduler()
        .runTaskAsynchronously(
            plugin,
            () -> {
              try (Connection connection = hikariDataSource.getConnection()) {
                String selectQuery = "SELECT * FROM quests_signs";

                try (Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(selectQuery)) {

                  List<Location> signLocations = new ArrayList<>();
                  while (resultSet.next()) {
                    String server = resultSet.getString("server");
                    if (!server.equalsIgnoreCase(questsConfig.getServer())) {
                      continue;
                    }

                    String world = resultSet.getString("world");
                    double x = resultSet.getDouble("x");
                    double y = resultSet.getDouble("y");
                    double z = resultSet.getDouble("z");

                    signLocations.add(new Location(Bukkit.getWorld(world), x, y, z));
                  }

                  future.complete(signLocations);
                  plugin.getLogger().info("Retrieved all quests from the database!");
                } catch (SQLException e) {
                  future.completeExceptionally(e);
                }
              } catch (SQLException e) {
                future.completeExceptionally(e);
                plugin
                    .getLogger()
                    .severe(
                        "Error when retrieving all quests from the database! "
                            + Arrays.toString(e.getStackTrace()));
              }
            });

    return future;
  }
}
