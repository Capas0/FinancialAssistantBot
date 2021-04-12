package ru.hse.edu.srzhuchkov.statemachine.process;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.hse.edu.srzhuchkov.database.BotUser;
import ru.hse.edu.srzhuchkov.statemachine.State;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class StateProcessor {
    protected int userId;
    protected SendMessage sendMessage;
    protected State state;

    /**
     * Processes the received message in a certain state
     *
     * @param message the received message
     * @return reply message
     */
    public SendMessage process(Message message) {
        sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());
        userId = message.getFrom().getId();
        state = getState();
        if (validate(message)) {
            deepProcess(message);
            BotUser.setState(userId, state);
        }
        sendMessage.setReplyMarkup(state.display());

        return sendMessage;
    }

    protected boolean validate(Message message) {
        if (!(message.hasText() && Arrays.asList(getState().getReplies()).contains(message.getText()))) {
            sendMessage.setText("Я Вас не понимаю, попробуйте пользоваться кнопками.");
            return false;
        }
        return true;
    }

    /**
     * Processes the received message in a certain state
     *
     * @param message the received message
     */
    protected abstract void deepProcess(Message message);

    /**
     * Sets the keyboard view for the state
     *
     * @return the keyboard
     */
    public ReplyKeyboard display() {
        List<KeyboardRow> keyboard = new ArrayList<>();
        for (String str : getState().getReplies()) {
            KeyboardRow row = new KeyboardRow();
            row.add(str);
            keyboard.add(row);
        }
        return new ReplyKeyboardMarkup(keyboard, true, true, true);
    }

    /**
     * Returns the associated state
     *
     * @return the state
     */
    public abstract State getState();
}
