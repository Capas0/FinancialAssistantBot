package ru.hse.edu.srzhuchkov.database;

import lombok.Setter;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.hse.edu.srzhuchkov.telegram.Bot;

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

    public static void execute(int userId, long chatId) {
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), "В данном интервале трат не было.");
        try (Connection connection = DBManager.getInstance().getConnection()) {
            int sliderId = DateSlider.create(userId);
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT purchase_id,\n" +
                            " LAG(purchase_id) OVER (ORDER BY pdate, purchase_id) AS prev,\n" +
                            " LEAD(purchase_id) OVER (ORDER BY pdate, purchase_id) AS next\n" +
                            "FROM purchase\n" +
                            "JOIN amount_expenses_settings settings ON purchase.user_id = settings.user_id\n" +
                            "WHERE settings.user_id = ? AND pdate BETWEEN settings.beg_date AND settings.end_date\n" +
                            "LIMIT 1"
            );
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            int prev = 0, next = 0;
            int purchaseId = 0;
            if (resultSet.next()) {
                purchaseId = resultSet.getInt("purchase_id");
                prev = resultSet.getInt("prev");
                next = resultSet.getInt("next");
            }
            Purchase purchase = Purchase.load(userId, purchaseId);
            if (purchase != Purchase.DUMMY) {
                sendMessage.setText(purchase.toString());
            }
            sendMessage.setReplyMarkup(DateSlider.getMarkup(sliderId, prev, next));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            Bot.getInstance().execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return String.format("Будут отображены траты, совершенные с %s по %s.",
                dateFormat.format(beginDate),
                dateFormat.format(endDate));
    }
}
