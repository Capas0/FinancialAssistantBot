package ru.hse.edu.srzhuchkov.statemachine.process;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.hse.edu.srzhuchkov.database.BotUser;
import ru.hse.edu.srzhuchkov.database.TempPurchase;
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
                state = State.ADD_PURCHASE_CURRENCY;
                sendMessage.setText("Выберите валюту покупки.");
                break;
            case "Дата":
                state = State.ADD_PURCHASE_DATE;
                sendMessage.setText("Введите дату в формате дд.мм.гггг");
                break;
            case "Категория":
                state = State.ADD_PURCHASE_CATEGORY;
                sendMessage.setText("Выберите категорию.");
                break;
            case "Описание":
                state = State.ADD_PURCHASE_DESCRIPTION;
                sendMessage.setText("Введите описание покупки.");
                break;
            case "Сканировать QR код":
                state = State.ADD_PURCHASE_QR;
                sendMessage.setText("Отправьте фото QR кода с чека.");
                break;
            case "Подтвердить":
                TempPurchase.confirm(message.getFrom().getId());
                state = State.INITIAL;
                sendMessage.setText("Покупка добавлена.");
                break;
            case "Отмена":
                state = State.INITIAL;
                sendMessage.setText("Добавление покупки отменено.");
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
