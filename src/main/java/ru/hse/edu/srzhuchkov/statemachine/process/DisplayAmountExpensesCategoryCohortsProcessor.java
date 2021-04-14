package ru.hse.edu.srzhuchkov.statemachine.process;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.hse.edu.srzhuchkov.database.DBManager;
import ru.hse.edu.srzhuchkov.statemachine.State;
import ru.hse.edu.srzhuchkov.telegram.Bot;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.io.*;
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
                    sendChart(message.getChatId(), categories, amounts);
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

    private static void sendChart(long chatId, List<String> categories, List<String> amounts) {
        InputStream stream = createChart(createDataset(categories, amounts));
        SendPhoto sendPhoto = new SendPhoto(String.valueOf(chatId), new InputFile(stream, "chart.png"));
        try {
            Bot.getInstance().execute(sendPhoto);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private static PieDataset<String> createDataset(List<String> categories, List<String> amounts) {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        for (int i = 0; i < categories.size(); i++) {
            dataset.setValue(categories.get(i), Double.valueOf(amounts.get(i).replace(',', '.')));
        }
        return dataset;
    }

    private static InputStream createChart(PieDataset<String> dataset) {
        JFreeChart chart = ChartFactory.createPieChart(
                "", dataset, false, true, false
        );

        chart.setBackgroundPaint(Color.lightGray);

        PiePlot plot = (PiePlot) chart.getPlot();

        plot.setInteriorGap(0.04);
        plot.setOutlineVisible(false);
        plot.setLabelBackgroundPaint(Color.white);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(chart.createBufferedImage(750, 450), "png", os);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(os.toByteArray());
    }
}
