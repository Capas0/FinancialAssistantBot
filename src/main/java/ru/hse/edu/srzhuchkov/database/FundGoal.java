package ru.hse.edu.srzhuchkov.database;

import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Currency;

public class FundGoal {
    private final int userId;

    @Setter
    private BigDecimal amount;
    @Setter
    private Currency currency;

    private boolean enabled;

    private FundGoal(int userId) {
        this.userId = userId;
        amount = BigDecimal.ZERO;
        currency = Currency.getInstance("RUB");
        enabled = false;
    }

    private FundGoal(ResultSet rs) throws SQLException {
        userId = rs.getInt("user_id");
        amount = rs.getBigDecimal("amount");
        currency = Currency.getInstance(rs.getString("currency"));
        enabled = rs.getBoolean("enabled");
    }

    public static FundGoal create(int userId) {
        FundGoal goal = new FundGoal(userId);
        try (Connection connection = DBManager.getInstance().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO fund_goal (user_id, amount, currency)\n" +
                            "VALUES (?, 0, 'RUB')\n" +
                            "ON CONFLICT (user_id) DO NOTHING\n" +
                            "RETURNING 1");
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                goal = load(userId);
            }
        } catch (SQLException throwables) {
            System.out.println("Unable to create a fund goal.");
            throwables.printStackTrace();
        }
        return goal;
    }

    public static FundGoal load(int userId) {
        try (Connection connection = DBManager.getInstance().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT user_id, amount, currency, enabled FROM fund_goal WHERE user_id = ?"
            );
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new FundGoal(resultSet);
            }
        } catch (SQLException throwables) {
            System.out.println("Unable to load the fund goal.");
            throwables.printStackTrace();
        }
        return null;
    }

    public static void enable(int userId) {
        try (Connection connection = DBManager.getInstance().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE fund_goal SET enabled = TRUE WHERE user_id = ?"
            );
            statement.setInt(1, userId);
            statement.executeUpdate();
        } catch (SQLException throwables) {
            System.out.println("Unable to enable the fund goal.");
            throwables.printStackTrace();
        }
    }

    public static void disable(int userId) {
        try (Connection connection = DBManager.getInstance().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE fund_goal SET enabled = FALSE WHERE user_id = ?"
            );
            statement.setInt(1, userId);
            statement.executeUpdate();
        } catch (SQLException throwables) {
            System.out.println("Unable to disable the fund goal.");
            throwables.printStackTrace();
        }
    }

    public static boolean check(int userId) {
        boolean res = false;
        try (Connection connection = DBManager.getInstance().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT 1\n" +
                            "FROM fund\n" +
                            "JOIN fund_goal fg ON fund.user_id = fg.user_id\n" +
                            " AND fund.currency = fg.currency\n" +
                            "WHERE fg.user_id = ?\n" +
                            " AND enabled\n" +
                            " AND fund.value >= fg.amount"
            );
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            res = resultSet.next();
        } catch (SQLException throwables) {
            System.out.println("Unable to disable the fund goal.");
            throwables.printStackTrace();
        }

        if (res) {
            disable(userId);
        }

        return res;
    }

    public void save() {
        try (Connection connection = DBManager.getInstance().getConnection()) {
            enabled = false;
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE fund_goal SET\n" +
                            "amount = ?, currency = ?, enabled = FALSE\n" +
                            "WHERE user_id = ?"
            );
            statement.setBigDecimal(1, amount);
            statement.setString(2, currency.getCurrencyCode());
            statement.setInt(3, userId);
            statement.executeUpdate();
        } catch (SQLException throwables) {
            System.out.println("Unable to save the fund goal.");
            throwables.printStackTrace();
        }
    }

    @Override
    public String toString() {
        String begin = enabled ? "Текущая" : "Устанавливается";
        return String.format("%s цель: %s %s.",
                begin,
                new DecimalFormat("0.00").format(amount),
                currency.toString());
    }
}
