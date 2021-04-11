package ru.hse.edu.srzhuchkov.statemachine.process;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.hse.edu.srzhuchkov.database.BotUser;
import ru.hse.edu.srzhuchkov.database.TempPurchase;
import ru.hse.edu.srzhuchkov.statemachine.State;

import java.math.BigDecimal;
import java.util.Arrays;

public class PurchaseAmountProcessor extends StateProcessor {
    boolean button;
    BigDecimal amount;

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
            amount = BigDecimal.valueOf(Double.parseDouble(message.getText()));
        } catch (NumberFormatException e) {
            amount = BigDecimal.valueOf(-1);
        }
        if (!button && amount.doubleValue() < 0) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(message.getChatId().toString());
            sendMessage.setText("Не могу распознать сумму, попробуйте еще раз.");
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

        TempPurchase tempPurchase = TempPurchase.load(userId);
        if (!button) {
            tempPurchase.setAmount(amount);
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
        return State.ADD_PURCHASE_AMOUNT;
    }
}
