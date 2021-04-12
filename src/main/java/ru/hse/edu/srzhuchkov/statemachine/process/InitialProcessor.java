package ru.hse.edu.srzhuchkov.statemachine.process;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.hse.edu.srzhuchkov.database.AmountExpensesSettings;
import ru.hse.edu.srzhuchkov.database.ExpensesSettings;
import ru.hse.edu.srzhuchkov.database.TempPurchase;
import ru.hse.edu.srzhuchkov.statemachine.State;

public class InitialProcessor extends StateProcessor {
    /**
     * Processes the received message in a certain state
     *
     * @param message the received message
     */
    @Override
    protected void deepProcess(Message message) {
        switch (message.getText()) {
            case "Добавить покупку":
                state = State.ADD_PURCHASE;
                sendMessage.setText(TempPurchase.create(userId).toString());
                break;
            case "Общая сумма расходов":
                state = State.DISPLAY_EXPENSES_AMOUNT;
                sendMessage.setText(AmountExpensesSettings.create(userId).toString());
                break;
            case "Список расходов":
                state = State.DISPLAY_EXPENSES;
                sendMessage.setText(ExpensesSettings.create(userId).toString());
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
        return State.INITIAL;
    }
}
