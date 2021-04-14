package ru.hse.edu.srzhuchkov.statemachine.process.fund.goal;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.hse.edu.srzhuchkov.database.FundGoal;
import ru.hse.edu.srzhuchkov.statemachine.State;
import ru.hse.edu.srzhuchkov.statemachine.process.AmountProcessor;

public class FundGoalAmountProcessor extends AmountProcessor {

    /**
     * Processes the received message in a certain state
     *
     * @param message the received message
     */
    @Override
    protected void deepProcess(Message message) {
        state = State.FUND_GOAL;

        FundGoal goal = FundGoal.load(userId);
        if (!button && goal != null) {
            goal.setAmount(amount);
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
        return State.FUND_GOAL_AMOUNT;
    }
}
