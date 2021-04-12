package ru.hse.edu.srzhuchkov.statemachine.process;

import org.telegram.telegrambots.meta.api.objects.Message;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public abstract class DateProcessor extends StateProcessor {
    protected static final DateFormat format = new SimpleDateFormat("dd.MM.yyyy");

    protected boolean button;
    protected Date date;

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
}
