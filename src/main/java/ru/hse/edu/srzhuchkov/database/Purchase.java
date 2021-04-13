package ru.hse.edu.srzhuchkov.database;

import lombok.Data;
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

@Data
public class Purchase {
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

    public static Purchase DUMMY = new Purchase();

    private Purchase() {
        date = new Date();
        amount = BigDecimal.ZERO;
        currency = Currency.getInstance("RUB");
        category = "Без категории";
        description = "-";
    }

    private Purchase(ResultSet rs) throws SQLException {
        date = new Date(rs.getLong("pdate"));
        amount = rs.getBigDecimal("amount");
        currency = Currency.getInstance(rs.getString("currency"));
        category = rs.getString("category");
        description = rs.getString("description");
    }

    public static Purchase load(int userId, int purchaseId) {
        try (Connection connection = DBManager.getInstance().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT pdate, amount, currency, title as category, description\n" +
                            "FROM purchase JOIN category c ON purchase.category_id = c.category_id\n" +
                            "WHERE purchase_id = ? AND user_id = ?"
            );
            statement.setInt(1, purchaseId);
            statement.setInt(2, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new Purchase(resultSet);
            }
        } catch (SQLException throwables) {
            System.out.println("Unable to load the purchase for expenses.");
            throwables.printStackTrace();
        }
        return DUMMY;
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
