package ru.hse.edu.srzhuchkov.statemachine.process;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.hse.edu.srzhuchkov.database.DBManager;
import ru.hse.edu.srzhuchkov.statemachine.State;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DisplayAmountExpensesCategoryCohortsProcessor extends StateProcessor {
    /**
     * Processes the received message in a certain state
     *
     * @param message the received message
     */
    @Override
    protected void deepProcess(Message message) {
        state = State.INITIAL;

        if (message.getText().equals("Отмена")) {
            sendMessage.setText("Выберите действие.");
        }
        else {
            String res = "";
            String currency = message.getText();
            try (Connection connection = DBManager.getInstance().getConnection()) {
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT title AS category, SUM(amount) AS amount\n" +
                                "FROM purchase\n" +
                                "JOIN category c ON c.category_id = purchase.category_id\n" +
                                "WHERE user_id = ? AND currency = ?\n" +
                                "GROUP BY title"
                );
                statement.setInt(1, userId);
                statement.setString(2, currency);
                ResultSet resultSet = statement.executeQuery();

                ArrayList<String> categories = new ArrayList<>();
                ArrayList<String> amounts = new ArrayList<>();
                DecimalFormat dF = new DecimalFormat("0.00");
                while (resultSet.next()) {
                    categories.add(resultSet.getString("category"));
                    amounts.add(dF.format(resultSet.getBigDecimal("amount")));
                }
                if (categories.size() > 0) {
                    res = format(categories, amounts, currency);
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            String text = res.length() > 0 ? "`" + res + "`" : "Нет трат в данной валюте.";
            sendMessage.enableMarkdown(true);
            sendMessage.setText(text);
        }
    }

    /**
     * Returns the associated state
     *
     * @return the state
     */
    @Override
    public State getState() {
        return State.DISPLAY_AMOUNT_EXPENSES_CATEGORY_COHORTS;
    }

    String format(List<String> categories, List<String> amounts, String currency) {
        Optional<Integer> maxCategoryLength = categories.stream().map(String::length).max(Integer::compareTo);
        Optional<Integer> maxAmountLength = amounts.stream().map(String::length).max(Integer::compareTo);

        int categoryLength = 10, amountLength = 3;
        if (maxCategoryLength.isPresent() && maxAmountLength.isPresent()) {
            categoryLength = maxCategoryLength.get();
            amountLength = maxAmountLength.get();
        }

        String fmt = String.format("%%-%ds %%%ds %%s\n", categoryLength, amountLength);

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < categories.size(); i++) {
            stringBuilder.append(String.format(fmt, categories.get(i), amounts.get(i), currency));
        }

        return stringBuilder.toString();
    }
}
