package ru.hse.edu.srzhuchkov.statemachine.process;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.hse.edu.srzhuchkov.statemachine.State;

import java.util.Arrays;

public abstract class StateProcessor {
    /**
     * Processes the received message in a certain state
     *
     * @param message the received message
     * @return reply message
     */
    public SendMessage process(Message message) {
        if (!Arrays.asList(getState().getReplies()).contains(message.getText())) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(message.getChatId().toString());
            sendMessage.setText("Я Вас не понимаю, попробуйте пользоваться кнопками.");
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
    protected abstract SendMessage deepProcess(Message message);

    /**
     * Returns the associated state
     *
     * @return the state
     */
    public abstract State getState();
}
