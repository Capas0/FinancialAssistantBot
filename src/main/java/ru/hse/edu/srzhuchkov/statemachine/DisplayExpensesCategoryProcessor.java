package ru.hse.edu.srzhuchkov.statemachine;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.hse.edu.srzhuchkov.database.CategorySlider;
import ru.hse.edu.srzhuchkov.database.DBManager;
import ru.hse.edu.srzhuchkov.database.Purchase;
import ru.hse.edu.srzhuchkov.statemachine.process.StateProcessor;
import ru.hse.edu.srzhuchkov.telegram.Bot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DisplayExpensesCategoryProcessor extends StateProcessor {
    /**
     * Processes the received message in a certain state
     *
     * @param message the received message
     */
    @Override
    protected void deepProcess(Message message) {
        state = State.INITIAL;

        if (!message.getText().equals("Отмена")) {
            execute(message);
        }
        sendMessage.setText("Выберите функцию.");
    }

    /**
     * Returns the associated state
     *
     * @return the state
     */
    @Override
    public State getState() {
        return State.DISPLAY_EXPENSES_CATEGORY;
    }

    private void execute(Message message) {
        SendMessage sendMessage = new SendMessage(String.valueOf(message.getChatId()), "В данном интервале трат не было.");
        try (Connection connection = DBManager.getInstance().getConnection()) {
            int sliderId = CategorySlider.create(userId, message.getText());
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT purchase_id,\n" +
                            "LAG(purchase_id) OVER (ORDER BY pdate, purchase_id) AS prev,\n" +
                            "LEAD(purchase_id) OVER (ORDER BY pdate, purchase_id) AS next\n" +
                            "FROM purchase\n" +
                            "JOIN category_slider slider ON purchase.user_id = slider.user_id\n" +
                            "WHERE slider.user_id = ? AND slider_id = ? AND purchase.category_id = slider.category_id\n" +
                            "LIMIT 1"
            );
            statement.setInt(1, userId);
            statement.setInt(2, sliderId);
            ResultSet resultSet = statement.executeQuery();
            int prev = 0, next = 0;
            int purchaseId = 0;
            if (resultSet.next()) {
                purchaseId = resultSet.getInt("purchase_id");
                prev = resultSet.getInt("prev");
                next = resultSet.getInt("next");
            }
            sendMessage.setText(Purchase.load(userId, purchaseId).toString());
            sendMessage.setReplyMarkup(CategorySlider.getMarkup(sliderId, prev, next));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            Bot.getInstance().execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
