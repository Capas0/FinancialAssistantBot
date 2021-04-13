package ru.hse.edu.srzhuchkov.database;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class Fund {
    public static Map<String, BigDecimal> getFund(int userId) {
        Map<String, BigDecimal> res = new HashMap<>();
        try (Connection connection = DBManager.getInstance().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT value, currency FROM fund WHERE user_id = ?"
            );
            statement.setInt(1, userId);

            ResultSet resultSet = statement.executeQuery();
            BigDecimal value;
            String currency;
            while (resultSet.next()) {
                value = resultSet.getBigDecimal("value");
                currency = resultSet.getString("currency");
                res.put(currency, value);
            }
        } catch (SQLException throwables) {
            System.out.println("Unable to get the user's fund.");
            throwables.printStackTrace();
        }
        return res;
    }

    public static void updateFund(int userId, BigDecimal delta, String currency) {
        try (Connection connection = DBManager.getInstance().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE fund SET value = value + ? WHERE user_id = ? AND currency = ?"
            );
            statement.setBigDecimal(1, delta);
            statement.setInt(2, userId);
            statement.setString(3, currency);

            statement.executeUpdate();
        } catch (SQLException throwables) {
            System.out.println("Unable to update the user's fund.");
            throwables.printStackTrace();
        }
    }

    public static String format(Map<String, BigDecimal> map) {
        DecimalFormat format = new DecimalFormat("0.00");
        StringBuilder res = new StringBuilder();
        for (var entry : map.entrySet()) {
            res.append(String.format("%s %s\n", format.format(entry.getValue()), entry.getKey()));
        }
        if (res.length() == 0) {
            return "Нет данных.";
        }
        return String.format("Состояние счета:\n%s", res.toString());
    }
}
