package ru.hse.edu.srzhuchkov.statemachine.process;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.hse.edu.srzhuchkov.database.ExpensesSettings;
import ru.hse.edu.srzhuchkov.statemachine.State;

public class DisplayExpensesProcessor extends StateProcessor {
    /**
     * Processes the received message in a certain state
     *
     * @param message the received message
     */
    @Override
    protected void deepProcess(Message message) {
        switch (message.getText()) {
            case "Дата начала":
                state = State.EXPENSES_BEGIN_DATE;
                sendMessage.setText("Введите дату в формате дд.мм.гггг");
                break;
            case "Дата конца":
                state = State.EXPENSES_END_DATE;
                sendMessage.setText("Введите дату в формате дд.мм.гггг");
                break;
            case "Отобразить результат":
                state = State.INITIAL;
                ExpensesSettings.execute(userId, message.getChatId());
                sendMessage.setText("Выберите действие.");
                break;
            case "Отмена":
                state = State.INITIAL;
                sendMessage.setText("Выберите действие.");
                break;
        }
    }

    /**
     * Returns the associated state
     *
     * @return the state
     */
    @Override
    public State getState() {
        return State.DISPLAY_EXPENSES;
    }
}
