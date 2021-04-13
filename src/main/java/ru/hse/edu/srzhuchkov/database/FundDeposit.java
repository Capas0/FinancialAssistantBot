package ru.hse.edu.srzhuchkov.database;

import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Currency;

public class FundDeposit {
    private final int userId;

    @Setter
    private BigDecimal amount;
    @Setter
    private Currency currency;

    private FundDeposit(int userId) {
        this.userId = userId;
        amount = BigDecimal.ZERO;
        currency = Currency.getInstance("RUB");
    }

    private FundDeposit(ResultSet rs) throws SQLException {
        userId = rs.getInt("user_id");
        amount = rs.getBigDecimal("amount");
        currency = Currency.getInstance(rs.getString("currency"));
    }

    public static FundDeposit create(int userId) {
        FundDeposit deposit = new FundDeposit(userId);
        try (Connection connection = DBManager.getInstance().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO fund_deposit (user_id, amount, currency)\n" +
                            "VALUES (?, 0, 'RUB')\n" +
                            "ON CONFLICT (user_id) DO UPDATE SET\n" +
                            "amount = 0, currency = 'RUB'");
            statement.setInt(1, userId);
            statement.executeUpdate();
        } catch (SQLException throwables) {
            System.out.println("Unable to create a fund deposit.");
            throwables.printStackTrace();
        }
        return deposit;
    }

    public static FundDeposit load(int userId) {
        try (Connection connection = DBManager.getInstance().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT user_id, amount, currency FROM fund_deposit WHERE user_id = ?"
            );
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return new FundDeposit(resultSet);
        } catch (SQLException throwables) {
            System.out.println("Unable to load the fund deposit.");
            throwables.printStackTrace();
        }
        return null;
    }

    public void save() {
        try (Connection connection = DBManager.getInstance().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE fund_deposit SET\n" +
                            "amount = ?, currency = ?\n" +
                            "WHERE user_id = ?"
            );
            statement.setBigDecimal(1, amount);
            statement.setString(2, currency.getCurrencyCode());
            statement.setInt(3, userId);
            statement.executeUpdate();
        } catch (SQLException throwables) {
            System.out.println("Unable to save the fund deposit.");
            throwables.printStackTrace();
        }
    }

    public static void confirm(int userId) {
        try (Connection connection = DBManager.getInstance().getConnection()) {
            PreparedStatement statement;
            statement = connection.prepareStatement(
                    "INSERT INTO fund (user_id, value, currency)\n" +
                            "SELECT user_id, amount, currency\n" +
                            "FROM fund_deposit\n" +
                            "WHERE user_id = ?\n" +
                            "ON CONFLICT DO NOTHING\n" +
                            "RETURNING 1"
            );
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                statement = connection.prepareStatement(
                        "UPDATE fund\n" +
                                "SET value = value + amount\n" +
                                "FROM fund_deposit\n" +
                                "WHERE fund_deposit.user_id = fund.user_id\n" +
                                "AND fund_deposit.currency = fund.currency\n" +
                                "AND fund_deposit.user_id = ?"
                );
                statement.setInt(1, userId);
                statement.executeUpdate();
            }

            statement = connection.prepareStatement("DELETE FROM fund_deposit WHERE user_id = ?");
            statement.setInt(1, userId);
            statement.executeUpdate();
        } catch (SQLException throwables) {
            System.out.println("Unable to confirm the fund deposit.");
            throwables.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return String.format("Будет совершено пополнение на %s %s.",
                new DecimalFormat("0.00").format(amount),
                currency.toString());
    }
}
