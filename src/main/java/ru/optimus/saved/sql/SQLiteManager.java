package ru.optimus.saved.sql;


import ru.optimus.saved.Main;

import java.io.File;
import java.sql.*;
import java.util.concurrent.CompletableFuture;

public class SQLiteManager {
    private SQLiteConnector connector;

    public SQLiteManager(String databasePath) {
        connector = new SQLiteConnector(databasePath);
    }

    public void createDatabase() {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + connector.getDatabasePath());
             Statement statement = connection.createStatement()) {
            String createTableQuery = "CREATE TABLE IF NOT EXISTS saves (id INTEGER PRIMARY KEY, players TEXT, save LONGTEXT, armor LONGTEXT, offHand LONGTEXT)";
            statement.execute(createTableQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public SQLiteConnector getConnector() {
        return connector;
    }

    public void insertPlayerData(String playerName, String saveData, String armor, String offHand) {
        Connection connection = connector.getConnection();
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO saves (players, save, armor, offHand) VALUES (?, ?, ?, ?)")) {
            statement.setString(1, playerName);
            statement.setString(2, saveData);
            statement.setString(3, armor);
            statement.setString(4, offHand);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deletePlayerData(String playerName) {
        Connection connection = connector.getConnection();
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM saves WHERE players = ?")) {
            statement.setString(1, playerName);
            statement.executeUpdate();
            try (Statement vacuumStatement = connection.createStatement()) {
                vacuumStatement.executeUpdate("VACUUM");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void vacuumDatabase() {
        try (Connection connection = connector.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("VACUUM");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getPlayerSaveData(String playerName) {
        String saveData = null;
        Connection connection = connector.getConnection();
        try (PreparedStatement statement = connection.prepareStatement("SELECT save FROM saves WHERE players = ?")) {
            statement.setString(1, playerName);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    saveData = resultSet.getString("save");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return saveData;
    }

    public String getPlayerArmorData(String playerName) {
        String saveData = null;
        Connection connection = connector.getConnection();
        try (PreparedStatement statement = connection.prepareStatement("SELECT armor FROM saves WHERE players = ?")) {
            statement.setString(1, playerName);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    saveData = resultSet.getString("armor");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return saveData;
    }

    public String getPlayerOffHandData(String playerName) {
        String saveData = null;
        Connection connection = connector.getConnection();
        try (PreparedStatement statement = connection.prepareStatement("SELECT offHand FROM saves WHERE players = ?")) {
            statement.setString(1, playerName);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    saveData = resultSet.getString("offHand");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return saveData;
    }
}
