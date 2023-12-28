package org.JavaWolf.goldeconomy.database;

import java.sql.*;
import java.util.*;

public class Handler {
    public static void createDB(String databaseFile) {

        try {

            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile);


            Statement statement = connection.createStatement();


            String createTableQuery = "CREATE TABLE IF NOT EXISTS player_economy (UUID TEXT PRIMARY KEY, USERNAME TEXT NOT NULL,GOLD DOUBLE NOT NULL DEFAULT 0,SILVER DOUBLE NOT NULL DEFAULT 0)";
            // UUID
            // USERNAME
            // balance1 = GOLD
            // balance2 = SILVER

            statement.executeUpdate(createTableQuery);


            statement.close();
            connection.close();

            System.out.println("Database created successfully!");

        } catch (SQLException e) {
            System.out.println("Error creating database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static boolean TouchDatabase(String databaseFile) {
        try {

            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile);


            connection.close();

            return true;

        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean databasePlayerExist(String username, String databaseFile) {
        try {

            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile);


            String query = "SELECT COUNT(*) AS count FROM player_economy WHERE USERNAME = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();

            int count = resultSet.getInt("count");


            resultSet.close();
            preparedStatement.close();
            connection.close();

            return count > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static void newPlayerRow(String uuid, String username, double GOLD, double SILVER, String databaseFile) {
        try {

            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile);


            String query = "INSERT INTO player_economy (UUID, USERNAME, GOLD, SILVER) VALUES (?, ?, ?, ?)";


            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, uuid);
            preparedStatement.setString(2, username);
            preparedStatement.setDouble(3, GOLD);
            preparedStatement.setDouble(4, SILVER);


            preparedStatement.executeUpdate();


            preparedStatement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // TODO : implement executeTransaction() instead addCurrency() and removeFromBalance() let them to admin op only
    public static void addCurrency(String uuid, double amount, String currencyType, String databaseFile) {
        String columnName = currencyType.equalsIgnoreCase("GOLD") ? "GOLD" : "SILVER";

        String sql = "UPDATE player_economy SET " + columnName + " = " + columnName + " + ? WHERE UUID = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setDouble(1, amount);
            preparedStatement.setString(2, uuid);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Failed to add currency: " + e.getMessage());
        }
    }
    public static boolean removeFromBalance(String uuid, double amount, String currencyType, String databaseFile) {
        String columnName = currencyType.equalsIgnoreCase("GOLD") ? "GOLD" : "SILVER";

        String sql = "UPDATE player_economy SET " + columnName + " = " + columnName + " - ? WHERE UUID = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setDouble(1, amount);
            preparedStatement.setString(2, uuid);

            preparedStatement.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public static double getPlayerBalance(String uuid, String coinType, String databaseFile) {
        double amount = 0.0;

        try {

            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile);


            String query = "SELECT " + coinType + " FROM player_economy WHERE UUID = ?";


            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, uuid);


            ResultSet resultSet = preparedStatement.executeQuery();


            if (resultSet.next()) {

                amount = resultSet.getDouble(coinType);
            }


            resultSet.close();
            preparedStatement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return amount;
    }

    public static boolean executeTransaction(String senderUUID, String receiverUUID, double amount, String coinType, String databaseFile) {

        try {

            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile);

            connection.setAutoCommit(false);

            String checkBalanceQuery = "SELECT " + coinType + " FROM player_economy WHERE UUID = ?";
            PreparedStatement checkBalanceStatement = connection.prepareStatement(checkBalanceQuery);
            checkBalanceStatement.setString(1, senderUUID);
            ResultSet senderResult = checkBalanceStatement.executeQuery();

            String deductQuery = "UPDATE player_economy SET " + coinType + " = " + coinType + " - ? WHERE UUID = ?";
            PreparedStatement deductStatement = connection.prepareStatement(deductQuery);
            deductStatement.setDouble(1, amount);
            deductStatement.setString(2, senderUUID);
            deductStatement.executeUpdate();

            String addQuery = "UPDATE player_economy SET " + coinType + " = " + coinType + " + ? WHERE UUID = ?";
            PreparedStatement addStatement = connection.prepareStatement(addQuery);
            addStatement.setDouble(1, amount);
            addStatement.setString(2, receiverUUID);
            addStatement.executeUpdate();

            connection.commit();
            connection.setAutoCommit(true);

            senderResult.close();
            checkBalanceStatement.close();
            deductStatement.close();
            addStatement.close();
            connection.close();

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



    public static Map<String, Double> getSortedMap(String databaseFile, int moltiplicator) {
        Map<String, Double> sumsMap = new HashMap<>();

        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile);

            String query = "SELECT UUID, SUM(GOLD * " + moltiplicator + ") AS GOLD_SUM, SUM(SILVER) AS SILVER_SUM " +
                    "FROM player_economy GROUP BY UUID";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                String uuid = resultSet.getString("UUID");
                double goldSum = resultSet.getDouble("GOLD_SUM");
                double silverSum = resultSet.getDouble("SILVER_SUM");

                double totalSum = goldSum + silverSum;
                sumsMap.put(uuid, totalSum);
            }

            resultSet.close();
            statement.close();
            connection.close();

            // Sort the HashMap by value (total sums) in descending order
            List<Map.Entry<String, Double>> sortedList = new ArrayList<>(sumsMap.entrySet());
            sortedList.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

            // Create a LinkedHashMap to preserve the insertion order (sorted order)
            Map<String, Double> sortedSums = new LinkedHashMap<>();
            for (Map.Entry<String, Double> entry : sortedList) {
                sortedSums.put(entry.getKey(), entry.getValue());
            }

            return sortedSums;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sumsMap;
    }


    public static String getUUID(String databaseFile, String username) {
        String uuid = null;

        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile);

            String query = "SELECT UUID FROM player_economy WHERE USERNAME = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                uuid = resultSet.getString("UUID");
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return uuid;
    }


    public static void UpdateUUID(String databaseFile, String username, String newUUID) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile);

            String updateQuery = "UPDATE player_economy SET UUID = ? WHERE USERNAME = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
            preparedStatement.setString(1, newUUID);
            preparedStatement.setString(2, username);

            int rowsUpdated = preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();

            if (rowsUpdated > 0) {
                System.out.println("UUID updated successfully for username: " + username);
            } else {
                System.out.println("No rows were updated for username: " + username);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static String getUsername(String databaseFile, String uuid) {
        String username = null;

        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile);

            String query = "SELECT USERNAME FROM player_economy WHERE UUID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, uuid);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                username = resultSet.getString("USERNAME");
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return username;
    }



}
