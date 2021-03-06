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

public class AmountExpensesSettings {
    private final int userId;

    @Setter
    private Date beginDate;
    @Setter
    private Date endDate;
    @Setter
    private Currency currency;

    static final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    private AmountExpensesSettings(int userId) {
        this.userId = userId;

        beginDate = new Date();
        endDate = new Date();
        currency = Currency.getInstance("RUB");
    }

    private AmountExpensesSettings(ResultSet rs) throws SQLException {
        userId = rs.getInt("user_id");
        beginDate = new Date(rs.getLong("beg_date"));
        endDate = new Date(rs.getLong("end_date"));
        currency = Currency.getInstance(rs.getString("currency"));
    }

    public static AmountExpensesSettings create(int userId) {
        AmountExpensesSettings settings = new AmountExpensesSettings(userId);
        try (Connection connection = DBManager.getInstance().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO amount_expenses_settings (user_id, beg_date, end_date, currency) VALUES \n" +
                            "(?, ?, ?, 'RUB')\n" +
                            "ON CONFLICT (user_id) DO UPDATE SET\n" +
                            "end_date = ?\n" +
                            "RETURNING user_id, beg_date, end_date, currency");
            statement.setInt(1, userId);
            statement.setLong(2, settings.beginDate.getTime());
            statement.setLong(3, settings.endDate.getTime());
            statement.setLong(4, settings.endDate.getTime());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new AmountExpensesSettings(resultSet);
            }
        } catch (SQLException throwables) {
            System.out.println("Unable to create a settings for amount expenses.");
            throwables.printStackTrace();
        }
        return settings;
    }

    public static AmountExpensesSettings load(int userId) {
        try (Connection connection = DBManager.getInstance().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM amount_expenses_settings WHERE user_id = ?"
            );
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new AmountExpensesSettings(resultSet);
            }
        } catch (SQLException throwables) {
            System.out.println("Unable to load the settings for amount expenses.");
            throwables.printStackTrace();
        }
        return new AmountExpensesSettings(userId);
    }

    public void save() {
        try (Connection connection = DBManager.getInstance().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE amount_expenses_settings SET\n" +
                            "beg_date = ?, end_date = ?, currency = ? WHERE user_id = ?"
            );
            statement.setLong(1, beginDate.getTime());
            statement.setLong(2, endDate.getTime());
            statement.setString(3, currency.getCurrencyCode());
            statement.setInt(4, userId);
            statement.executeUpdate();
        } catch (SQLException throwables) {
            System.out.println("Unable to save the settings for amount expenses.");
            throwables.printStackTrace();
        }
    }

    public static String execute(int userId) {
        BigDecimal result = BigDecimal.ZERO;
        AmountExpensesSettings settings = AmountExpensesSettings.load(userId);
        try (Connection connection = DBManager.getInstance().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT sum(purchase.amount)\n" +
                            "FROM purchase\n" +
                            "JOIN amount_expenses_settings AS settings\n" +
                            "ON purchase.user_id = settings.user_id\n" +
                            "WHERE purchase.user_id = ?\n" +
                            "AND purchase.currency = settings.currency\n" +
                            "AND purchase.pdate >= settings.beg_date\n" +
                            "AND purchase.pdate < settings.end_date"
            );
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getBigDecimal(1);
            }
            if (result == null) {
                result = BigDecimal.ZERO;
            }
        } catch (SQLException throwables) {
            System.out.println("Unable to calculate amount expenses.");
            throwables.printStackTrace();
        }
        return String.format("?????????????????? ?????????????? ?? %s ???? %s ???????????????????? %s %s.",
                dateFormat.format(settings.beginDate),
                dateFormat.format(settings.endDate),
                new DecimalFormat("0.00").format(result),
                settings.currency);
    }

    @Override
    public String toString() {
        return String.format("?????????? ???????????????????? ?????????????????? ??????????, ?????????????????????? ?? %s ???? %s ?? %s.",
                dateFormat.format(beginDate),
                dateFormat.format(endDate),
                currency.getCurrencyCode());
    }
}
