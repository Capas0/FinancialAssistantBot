package ru.hse.edu.srzhuchkov.statemachine.process.purchase;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.hse.edu.srzhuchkov.database.TempPurchase;
import ru.hse.edu.srzhuchkov.statemachine.State;
import ru.hse.edu.srzhuchkov.statemachine.process.StateProcessor;

import java.math.BigDecimal;
import java.util.Arrays;

public class PurchaseAmountProcessor extends StateProcessor {
    boolean button;
    BigDecimal amount;

    @Override
    protected boolean validate(Message message) {
        if (!message.hasText()) {
            sendMessage.setText("Не могу распознать сумму, попробуйте еще раз.");
            return false;
        }
        button = Arrays.asList(getState().getReplies()).contains(message.getText());
        try {
            amount = BigDecimal.valueOf(Double.parseDouble(message.getText()));
        } catch (NumberFormatException e) {
            amount = BigDecimal.valueOf(-1);
        }
        if (!button && amount.doubleValue() < 0) {
            sendMessage.setText("Не могу распознать сумму, попробуйте еще раз.");
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
            tempPurchase.setAmount(amount);
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
        return State.ADD_PURCHASE_AMOUNT;
    }
}
