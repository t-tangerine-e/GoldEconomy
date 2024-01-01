package org.JavaWolf.goldeconomy.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AdminDB {
    // 2nd handler dedicated only for admin operations


    // copy of database.Handler.addCurrency()
    public static boolean addToBalance(String uuid, double amount, String currencyType, String databaseFile) {
        String columnName = currencyType.equalsIgnoreCase("GOLD") ? "GOLD" : "SILVER";

        String sql = "UPDATE player_economy SET " + columnName + " = " + columnName + " + ? WHERE UUID = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setDouble(1, amount);
            preparedStatement.setString(2, uuid);

            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Failed to add currency: " + e.getMessage());
        }
        return false;
    }



    public static boolean setBalance(String uuid, double amount, String currencyType, String databaseFile) {
        String columnName = currencyType.equalsIgnoreCase("GOLD") ? "GOLD" : "SILVER";

        String sql = "UPDATE player_economy SET " + columnName + " = ? WHERE UUID = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setDouble(1, amount);
            preparedStatement.setString(2, uuid);

            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static boolean resetBalance(String uuid, double GOLD, double SILVER, String databaseFile) {
        String sql = "UPDATE player_economy SET GOLD = ?, SILVER = ? WHERE UUID = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setDouble(1, GOLD);
            preparedStatement.setDouble(2, SILVER);
            preparedStatement.setString(3, uuid);

            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Failed to set currency amount: " + e.getMessage());
            return false;
        }
    }



}
