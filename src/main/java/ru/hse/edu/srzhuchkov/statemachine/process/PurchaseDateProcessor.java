package ru.hse.edu.srzhuchkov.statemachine.process;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.hse.edu.srzhuchkov.database.BotUser;
import ru.hse.edu.srzhuchkov.database.TempPurchase;
import ru.hse.edu.srzhuchkov.statemachine.State;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class PurchaseDateProcessor extends StateProcessor {
    static DateFormat format = new SimpleDateFormat("dd.MM.yyyy");

    boolean button;
    Date date;

    /**
     * Processes the received message in a certain state
     *
     * @param message the received message
     * @return reply message
     */
    @Override
    public SendMessage process(Message message) {
        button = Arrays.asList(getState().getReplies()).contains(message.getText());
        try {
            date = format.parse(message.getText());
        } catch (ParseException e) {
            date = null;
        }
        if (!button && date == null) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(message.getChatId().toString());
            sendMessage.setText("Не могу распознать дату, попробуйте еще раз.");
            sendMessage.setReplyMarkup(getState().display());
            return sendMessage;
        }
        return deepProcess(message);
    }

    /**
     * Processes the received message in a certain state
     *
     * @param message the received message
     * @return reply message
     */
    @Override
    protected SendMessage deepProcess(Message message) {
        int userId = message.getFrom().getId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());
        State state = State.ADD_PURCHASE;

        TempPurchase tempPurchase = TempPurchase.load(message.getFrom().getId());
        if (!button) {
            tempPurchase.setDate(date);
            tempPurchase.save();
        }
        else if (message.getText().equals("Сегодня")) {
            tempPurchase.setDate(new Date());
            tempPurchase.save();
        }
        sendMessage.setText(tempPurchase.toString());

        BotUser.setState(userId, state);
        sendMessage.setReplyMarkup(state.display());
        return sendMessage;
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
