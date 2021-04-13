package ru.hse.edu.srzhuchkov.database;

import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DateSlider {
    public static int create(int userId) {
        int sliderId = 0;
        try (Connection connection = DBManager.getInstance().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO date_slider (user_id, beg_date, end_date)\n" +
                            "SELECT settings.user_id, settings.beg_date, settings.end_date\n" +
                            "FROM amount_expenses_settings settings\n" +
                            "WHERE settings.user_id = ?\n" +
                            "RETURNING slider_id"
            );
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                sliderId = resultSet.getInt(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return sliderId;
    }

    public static Map<String, Integer> load(int userId, int sliderId, int purchaseId) {
        HashMap<String, Integer> res = new HashMap<>();
        try (Connection connection = DBManager.getInstance().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT prev, next\n" +
                            "FROM (\n" +
                            "SELECT purchase_id,\n" +
                            " LAG(purchase_id) OVER (ORDER BY pdate, purchase_id)  AS prev,\n" +
                            " LEAD(purchase_id) OVER (ORDER BY pdate, purchase_id) AS next\n" +
                            "FROM purchase\n" +
                            "JOIN date_slider slider ON purchase.user_id = slider.user_id\n" +
                            "WHERE slider.user_id = ? AND slider_id = ?\n" +
                            "AND pdate BETWEEN slider.beg_date AND slider.end_date\n" +
                            ") AS near\n" +
                            "WHERE purchase_id = ?"
            );
            statement.setInt(1, userId);
            statement.setInt(2, sliderId);
            statement.setInt(3, purchaseId);
            ResultSet set = statement.executeQuery();
            if (set.next()) {
                res.put("prev", set.getInt("prev"));
                res.put("next", set.getInt("next"));
            }
            else {
                res.put("prev", 0);
                res.put("next", 0);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return res;
    }

    public static EditMessageText process(CallbackQuery query) {
        int userId = query.getFrom().getId();
        long messageId = query.getMessage().getMessageId();
        long chatId = query.getMessage().getChatId();

        String callData = query.getData();

        EditMessageText newMessage = new EditMessageText();
        newMessage.setChatId(String.valueOf(chatId));
        newMessage.setMessageId(Math.toIntExact(messageId));

        String[] params = callData.substring(5).split(":");
        int sliderId = Integer.parseInt(params[0]);
        int purchaseId = Integer.parseInt(params[1]);

        newMessage.setText(Purchase.load(userId, purchaseId).toString());

        Map<String, Integer> near = load(userId, sliderId, purchaseId);
        int prev = near.get("prev");
        int next = near.get("next");

        newMessage.setReplyMarkup(getMarkup(sliderId, prev, next));

        return newMessage;
    }

    public static InlineKeyboardMarkup getMarkup(int sliderId, int prev, int next) {
        if (prev == 0 && next == 0) {
            return null;
        }

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        InlineKeyboardButton button;

        if (prev > 0) {
            button = new InlineKeyboardButton();
            button.setText("<");
            button.setCallbackData(String.format("date %d:%d", sliderId, prev));
            rowInline.add(button);
        }
        if (next > 0) {
            button = new InlineKeyboardButton();
            button.setText(">");
            button.setCallbackData(String.format("date %d:%d", sliderId, next));
            rowInline.add(button);
        }
        rowsInline.add(rowInline);

        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }
}
