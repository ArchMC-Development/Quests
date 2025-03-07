package com.codepunisher.quests.database.impl;

import com.codepunisher.quests.database.QuestDatabase;
import com.codepunisher.quests.models.Quest;
import com.codepunisher.quests.models.QuestType;
import com.zaxxer.hikari.HikariDataSource;
import lombok.AllArgsConstructor;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
public class QuestDatabaseImpl implements QuestDatabase {
    private final JavaPlugin plugin;
    private final HikariDataSource hikariDataSource;

    @Override
    public void createQuestTable() {
        plugin
                .getServer()
                .getScheduler()
                .runTaskAsynchronously(
                        plugin,
                        () -> {
                            try (Connection connection = hikariDataSource.getConnection()) {
                                Statement statement = connection.createStatement();
                                String createTableQuery =
                                        "CREATE TABLE IF NOT EXISTS quests ("
                                                + "id VARCHAR(255) PRIMARY KEY,"
                                                + "name VARCHAR(255),"
                                                + "quest_type VARCHAR(255),"
                                                + "associated_object VARCHAR(255),"
                                                + "min INT,"
                                                + "max INT,"
                                                + "permission VARCHAR(255),"
                                                + "console_command_rewards VARCHAR(255)"
                                                + ")";

                                statement.executeUpdate(createTableQuery);
                                plugin.getLogger().info("Quest table successfully created!");
                            } catch (SQLException e) {
                                plugin
                                        .getLogger()
                                        .severe(
                                                "Error when loading quest table! " + Arrays.toString(e.getStackTrace()));
                            }
                        });
    }

    @Override
    public void insert(Quest quest) {
        plugin
                .getServer()
                .getScheduler()
                .runTaskAsynchronously(
                        plugin,
                        () -> {
                            try (Connection connection = hikariDataSource.getConnection()) {
                                String insertQuery =
                                        "INSERT INTO quests (id, name, quest_type, associated_object, min, max, permission, console_command_rewards) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

                                try (PreparedStatement preparedStatement =
                                             connection.prepareStatement(insertQuery)) {
                                    preparedStatement.setString(1, quest.getId());
                                    preparedStatement.setString(2, quest.getDisplayName());
                                    preparedStatement.setString(3, quest.getQuestType().toString());
                                    preparedStatement.setString(
                                            4,
                                            quest
                                                    .getQuestType()
                                                    .getInputFromAssociatedObject(quest.getAssociatedObject()));
                                    preparedStatement.setInt(5, quest.getMin());
                                    preparedStatement.setInt(6, quest.getMax());
                                    preparedStatement.setString(7, quest.getPermission());
                                    preparedStatement.setString(8, String.join(",", quest.getRewards()));

                                    preparedStatement.executeUpdate();
                                    plugin.getLogger().info("Quest successfully inserted into the database!");
                                } catch (SQLException e) {
                                }
                            } catch (SQLException e) {
                                plugin
                                        .getLogger()
                                        .severe(
                                                "Error when inserting quest into the database! "
                                                        + Arrays.toString(e.getStackTrace()));
                            }
                        });
    }

    @Override
    public void remove(Quest quest) {
        plugin
                .getServer()
                .getScheduler()
                .runTaskAsynchronously(
                        plugin,
                        () -> {
                            try (Connection connection = hikariDataSource.getConnection()) {
                                String removeQuery = "DELETE FROM quests WHERE id = ?";

                                try (PreparedStatement preparedStatement =
                                             connection.prepareStatement(removeQuery)) {
                                    preparedStatement.setString(1, quest.getId());

                                    int affectedRows = preparedStatement.executeUpdate();
                                    if (affectedRows > 0) {
                                        plugin.getLogger().info("Quest successfully removed from the database!");
                                    } else {
                                        plugin.getLogger().warning("Quest not found in the database!");
                                    }
                                } catch (SQLException e) {
                                    plugin
                                            .getLogger()
                                            .severe(
                                                    "Error when removing quest from the database! "
                                                            + Arrays.toString(e.getStackTrace()));
                                }
                            } catch (SQLException e) {
                                plugin
                                        .getLogger()
                                        .severe(
                                                "Error when removing quest from the database! "
                                                        + Arrays.toString(e.getStackTrace()));
                            }
                        });
    }

    @Override
    public CompletableFuture<List<Quest>> getAllQuests() {
        CompletableFuture<List<Quest>> future = new CompletableFuture<>();

        plugin
                .getServer()
                .getScheduler()
                .runTaskAsynchronously(
                        plugin,
                        () -> {
                            try (Connection connection = hikariDataSource.getConnection()) {
                                String selectQuery = "SELECT * FROM quests";

                                try (Statement statement = connection.createStatement();
                                     ResultSet resultSet = statement.executeQuery(selectQuery)) {

                                    List<Quest> questList = new ArrayList<>();
                                    while (resultSet.next()) {
                                        String id = resultSet.getString("id");
                                        String name = resultSet.getString("name");
                                        QuestType questType =
                                                QuestType.valueOf(resultSet.getString("quest_type".toUpperCase()));
                                        Object associatedObject =
                                                questType.getAssociationFromInput(resultSet.getString("associated_object"));
                                        int min = resultSet.getInt("min");
                                        int max = resultSet.getInt("max");
                                        String permission = resultSet.getString("permission");
                                        String[] rewards = resultSet.getString("console_command_rewards").split(",");

                                        Quest quest =
                                                new Quest(id, name, questType, associatedObject, min, max, permission, rewards);

                                        questList.add(quest);
                                    }

                                    future.complete(questList);
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
