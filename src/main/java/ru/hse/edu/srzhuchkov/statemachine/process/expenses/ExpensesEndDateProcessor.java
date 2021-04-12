package ru.hse.edu.srzhuchkov.statemachine.process.expenses;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.hse.edu.srzhuchkov.database.AmountExpensesSettings;
import ru.hse.edu.srzhuchkov.database.ExpensesSettings;
import ru.hse.edu.srzhuchkov.statemachine.State;
import ru.hse.edu.srzhuchkov.statemachine.process.DateProcessor;

import java.util.Date;

public class ExpensesEndDateProcessor extends DateProcessor {
    /**
     * Processes the received message in a certain state
     *
     * @param message the received message
     */
    @Override
    protected void deepProcess(Message message) {
        state = State.DISPLAY_EXPENSES;

        ExpensesSettings settings = ExpensesSettings.load(userId);
        if (!button) {
            settings.setEndDate(date);
            settings.save();
        }
        else if (message.getText().equals("Сегодня")) {
            settings.setEndDate(new Date());
            settings.save();
        }
        sendMessage.setText(settings.toString());
    }

    /**
     * Returns the associated state
     *
     * @return the state
     */
    @Override
    public State getState() {
        return State.EXPENSES_END_DATE;
    }
}
