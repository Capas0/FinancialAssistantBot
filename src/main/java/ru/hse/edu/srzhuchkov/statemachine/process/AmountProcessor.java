package ru.hse.edu.srzhuchkov.statemachine.process;

import org.telegram.telegrambots.meta.api.objects.Message;

import java.math.BigDecimal;
import java.util.Arrays;

public abstract class AmountProcessor extends StateProcessor {
    protected boolean button;
    protected BigDecimal amount;

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
}
