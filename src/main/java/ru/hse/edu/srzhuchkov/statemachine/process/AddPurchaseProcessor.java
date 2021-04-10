package ru.hse.edu.srzhuchkov.statemachine.process;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.hse.edu.srzhuchkov.database.BotUser;
import ru.hse.edu.srzhuchkov.statemachine.State;

public class AddPurchaseProcessor extends StateProcessor {
    /**
     * Processes the received message in a certain state
     *
     * @param message the received message
     * @return reply message
     */
    @Override
    public SendMessage deepProcess(Message message) {
        int userId = message.getFrom().getId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());
        State state = getState();

        switch (message.getText()) {
            case "Сумма":
                state = State.ADD_PURCHASE_AMOUNT;
                sendMessage.setText("Введите сумму покупки.");
                break;
            case "Валюта":
                break;
            case "Дата":
                break;
            case "Категория":
                break;
            case "Описание":
                break;
            case "Подтвердить":
                break;
            case "Отмена":
                break;
        }
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
        return State.ADD_PURCHASE;
    }
}
