package ru.hse.edu.srzhuchkov.statemachine.process.purchase;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.hse.edu.srzhuchkov.database.TempPurchase;
import ru.hse.edu.srzhuchkov.statemachine.State;
import ru.hse.edu.srzhuchkov.statemachine.process.StateProcessor;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class PurchaseDateProcessor extends StateProcessor {
    static DateFormat format = new SimpleDateFormat("dd.MM.yyyy");

    boolean button;
    Date date;

    @Override
    protected boolean validate(Message message) {
        if (!message.hasText()) {
            sendMessage.setText("Не могу распознать дату, попробуйте еще раз.");
            return false;
        }
        button = Arrays.asList(getState().getReplies()).contains(message.getText());
        try {
            date = format.parse(message.getText());
        } catch (ParseException e) {
            date = null;
        }
        if (!button && date == null) {
            sendMessage.setText("Не могу распознать дату, попробуйте еще раз.");
            return false;
        }
        return true;
    }

    /**
     * Processes the received message in a certain state
     *
     * @param message the received message
     */
    @Override
    protected void deepProcess(Message message) {
        state = State.ADD_PURCHASE;

        TempPurchase tempPurchase = TempPurchase.load(userId);
        if (!button) {
            tempPurchase.setDate(date);
            tempPurchase.save();
        }
        else if (message.getText().equals("Сегодня")) {
            tempPurchase.setDate(new Date());
            tempPurchase.save();
        }
        sendMessage.setText(tempPurchase.toString());
    }

    /**
     * Returns the associated state
     *
     * @return the state
     */
    @Override
    public State getState() {
        return State.ADD_PURCHASE_DATE;
    }
}
