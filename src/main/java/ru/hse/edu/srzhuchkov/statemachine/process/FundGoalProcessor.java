package ru.hse.edu.srzhuchkov.statemachine.process;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.hse.edu.srzhuchkov.database.Fund;
import ru.hse.edu.srzhuchkov.database.FundGoal;
import ru.hse.edu.srzhuchkov.statemachine.State;

public class FundGoalProcessor extends StateProcessor {
    /**
     * Processes the received message in a certain state
     *
     * @param message the received message
     */
    @Override
    protected void deepProcess(Message message) {
        switch (message.getText()) {
            case "Сумма":
                state = State.FUND_GOAL_AMOUNT;
                sendMessage.setText("Введите сумму.");
                break;
            case "Валюта":
                state = State.FUND_GOAL_CURRENCY;
                sendMessage.setText("Выберите валюту.");
                break;
            case "Создать":
                state = State.INITIAL;
                FundGoal.enable(userId);
                sendMessage.setText("Цель создана.");
                break;
            case "Удалить":
                state = State.INITIAL;
                FundGoal.disable(userId);
                sendMessage.setText("Цель удалена.");
                break;
            case "Назад":
                state = State.FUND;
                sendMessage.setText(Fund.load(userId).toString());
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
        return State.FUND_GOAL;
    }
}
