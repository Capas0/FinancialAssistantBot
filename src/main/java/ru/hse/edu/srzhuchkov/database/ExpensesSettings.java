package ru.hse.edu.srzhuchkov.database;

import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExpensesSettings {
    private final int userId;

    @Setter
    private Date beginDate;
    @Setter
    private Date endDate;

    static final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    public ExpensesSettings(int userId) {
        this.userId = userId;

        beginDate = new Date();
        endDate = new Date();
    }

    public ExpensesSettings(ResultSet rs) throws SQLException {
        userId = rs.getInt("user_id");
        beginDate = new Date(rs.getLong("beg_date"));
        endDate = new Date(rs.getLong("end_date"));
    }

    public static ExpensesSettings create(int userId) {
        ExpensesSettings settings = new ExpensesSettings(userId);
        try (Connection connection = DBManager.getInstance().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO amount_expenses_settings (user_id, beg_date, end_date, currency) VALUES \n" +
                            "(?, ?, ?, 'RUB')\n" +
                            "ON CONFLICT (user_id) DO UPDATE SET\n" +
                            "end_date = ?\n" +
                            "RETURNING user_id, beg_date, end_date");
            statement.setInt(1, userId);
            statement.setLong(2, settings.beginDate.getTime());
            statement.setLong(3, settings.endDate.getTime());
            statement.setLong(4, settings.endDate.getTime());
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return new ExpensesSettings(resultSet);
        } catch (SQLException throwables) {
            System.out.println("Unable to create a settings for expenses.");
            throwables.printStackTrace();
        }
        return settings;
    }

    public static ExpensesSettings load(int userId) {
        try (Connection connection = DBManager.getInstance().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM amount_expenses_settings WHERE user_id = ?"
            );
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return new ExpensesSettings(resultSet);
        } catch (SQLException throwables) {
            System.out.println("Unable to load the settings for expenses.");
            throwables.printStackTrace();
        }
        return new ExpensesSettings(userId);
    }

    public static String execute(int userId) {
        return null;
    }

    public void save() {
        try (Connection connection = DBManager.getInstance().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE amount_expenses_settings SET\n" +
                            "beg_date = ?, end_date = ? WHERE user_id = ?"
            );
            statement.setLong(1, beginDate.getTime());
            statement.setLong(2, endDate.getTime());
            statement.setInt(3, userId);
            statement.executeUpdate();
        } catch (SQLException throwables) {
            System.out.println("Unable to save the settings for expenses.");
            throwables.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return String.format("Будут траты, совершенные с %s по %s.",
                dateFormat.format(beginDate),
                dateFormat.format(endDate));
    }
}
