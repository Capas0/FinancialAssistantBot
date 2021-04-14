package ru.hse.edu.srzhuchkov.database;

import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;

public class TempPurchase {
    private final int userId;

    @Setter
    private Date date;
    @Setter
    private BigDecimal amount;
    @Setter
    private Currency currency;
    @Setter
    private String category;
    @Setter
    private String description;

    private TempPurchase(int userId) {
        this.userId = userId;

        date = new Date();
        currency = Currency.getInstance("RUB");
        category = "Без категории";
        amount = BigDecimal.valueOf(0);
        description = "";
    }

    private TempPurchase(ResultSet rs) throws SQLException {
        userId = rs.getInt("user_id");
        date = new Date(rs.getLong("pdate"));
        amount = rs.getBigDecimal("amount");
        currency = Currency.getInstance(rs.getString("currency"));
        category = rs.getString("category");
        description = rs.getString("description");
    }

    public static TempPurchase create(int userId) {
        TempPurchase tempPurchase = new TempPurchase(userId);
        try (Connection connection = DBManager.getInstance().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO temp_purchase (user_id, pdate, amount, currency, category_id, description)\n" +
                            "SELECT ?, ?, 0, 'RUB', 1, '-'\n" +
                            "ON CONFLICT (user_id) DO UPDATE SET pdate = ?, amount = 0, currency = 'RUB', category_id = 1, description = '-'\n" +
                            "RETURNING user_id, pdate, amount, currency, 'Без категории' as category, description");
            statement.setInt(1, userId);
            statement.setLong(2, tempPurchase.date.getTime());
            statement.setLong(3, tempPurchase.date.getTime());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new TempPurchase(resultSet);
            }
        } catch (SQLException throwables) {
            System.out.println("Unable to create a temp purchase.");
            throwables.printStackTrace();
        }
        return tempPurchase;
    }

    public static TempPurchase load(int userId) {
        try (Connection connection = DBManager.getInstance().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT user_id, pdate, amount, currency,\n" +
                            "(SELECT title FROM category WHERE category.category_id = temp_purchase.category_id) as category,\n" +
                            "description FROM temp_purchase WHERE user_id = ?"
            );
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new TempPurchase(resultSet);
            }
        } catch (SQLException throwables) {
            System.out.println("Unable to load the temp purchase.");
            throwables.printStackTrace();
        }
        return new TempPurchase(userId);
    }

    public void save() {
        try (Connection connection = DBManager.getInstance().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE temp_purchase SET pdate = ?, amount = ?, currency = ?,\n" +
                            "category_id = (SELECT category_id FROM category WHERE title = ?),\n" +
                            "description = ?\n" +
                            "WHERE user_id = ?"
            );
            statement.setLong(1, date.getTime());
            statement.setBigDecimal(2, amount);
            statement.setString(3, currency.getCurrencyCode());
            statement.setString(4, category);
            statement.setString(5, description);
            statement.setInt(6, userId);
            statement.executeUpdate();
        } catch (SQLException throwables) {
            System.out.println("Unable to save the temp purchase.");
            throwables.printStackTrace();
        }
    }

    public static void confirm(int userId) {
        try (Connection connection = DBManager.getInstance().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO purchase (user_id, pdate, amount, currency, category_id, description)\n" +
                            "SELECT temp_purchase.user_id, temp_purchase.pdate, temp_purchase.amount,\n" +
                            "temp_purchase.currency, temp_purchase.category_id, temp_purchase.description\n" +
                            "FROM temp_purchase WHERE temp_purchase.user_id = ?;"
            );
            statement.setInt(1, userId);
            statement.executeUpdate();

            statement = connection.prepareStatement(
                    "INSERT INTO fund (user_id, value, currency)\n" +
                            "SELECT user_id, 0, currency\n" +
                            "FROM temp_purchase\n" +
                            "WHERE user_id = ?\n" +
                            "ON CONFLICT DO NOTHING "
            );
            statement.setInt(1, userId);

            statement = connection.prepareStatement(
                    "UPDATE fund\n" +
                            "SET value = value - amount\n" +
                            "FROM temp_purchase\n" +
                            "WHERE fund.user_id = temp_purchase.user_id\n" +
                            "AND fund.currency = temp_purchase.currency\n" +
                            "AND temp_purchase.user_id = ?"
            );
            statement.setInt(1, userId);
            statement.executeUpdate();

            statement = connection.prepareStatement("DELETE FROM temp_purchase WHERE user_id = ?;");
            statement.setInt(1, userId);
            statement.executeUpdate();
        } catch (SQLException throwables) {
            System.out.println("Unable to confirm the temp purchase.");
            throwables.printStackTrace();
        }
    }

    @Override
    public String toString() {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        return String.format("Дата: %s\nСумма: %s %s\nКатегория: %s\nОписание: %s ",
                dateFormat.format(date),
                new DecimalFormat("0.00").format(amount), currency.toString(),
                category,
                description);
    }
}
