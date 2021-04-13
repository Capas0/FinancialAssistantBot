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
    private final Map<String, BigDecimal> list;

    private Fund(Map<String, BigDecimal> list) {
        this.list = list;
    }

    public static Fund load(int userId) {
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
        return new Fund(res);
    }

    @Override
    public String toString() {
        DecimalFormat format = new DecimalFormat("0.00");
        StringBuilder res = new StringBuilder();
        for (var entry : list.entrySet()) {
            res.append(String.format("%s %s\n", format.format(entry.getValue()), entry.getKey()));
        }
        if (res.length() == 0) {
            return "Нет данных.";
        }
        return String.format("Состояние счета:\n%s", res.toString());
    }
}
