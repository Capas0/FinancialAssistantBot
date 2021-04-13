package ru.hse.edu.srzhuchkov.statemachine.process.goal;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.hse.edu.srzhuchkov.database.FundGoal;
import ru.hse.edu.srzhuchkov.statemachine.State;
import ru.hse.edu.srzhuchkov.statemachine.process.StateProcessor;

import java.util.Currency;

public class FundGoalCurrencyProcessor extends StateProcessor {
    /**
     * Processes the received message in a certain state
     *
     * @param message the received message
     */
    @Override
    protected void deepProcess(Message message) {
        state = State.FUND_GOAL;

        FundGoal goal = FundGoal.load(userId);
        if (!message.getText().equals("Отмена") && goal != null) {
            goal.setCurrency(Currency.getInstance(message.getText()));
            goal.save();
        }
        if (goal != null) {
            sendMessage.setText(goal.toString());
        }
        else {
            sendMessage.setText("Не удалось загрузить информацию.");
        }
    }

    /**
     * Returns the associated state
     *
     * @return the state
     */
    @Override
    public State getState() {
        return State.FUND_GOAL_CURRENCY;
    }
}
