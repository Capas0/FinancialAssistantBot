package ru.hse.edu.srzhuchkov.statemachine.process;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.hse.edu.srzhuchkov.statemachine.State;

public class InitialProcessor extends StateProcessor {
    /**
     * Processes the received message in a certain state
     *
     * @param message the received message
     * @return reply message
     */
    @Override
    public SendMessage deepProcess(Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText("Начальное состояние.");
        sendMessage.setReplyMarkup(getState().display());
        return sendMessage;
    }

    /**
     * Returns the associated state
     *
     * @return the state
     */
    @Override
    public State getState() {
        return State.INITIAL;
    }
}
