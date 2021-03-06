package ru.hse.edu.srzhuchkov.statemachine.process;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.hse.edu.srzhuchkov.database.FundDeposit;
import ru.hse.edu.srzhuchkov.database.FundGoal;
import ru.hse.edu.srzhuchkov.statemachine.State;

public class FundProcessor extends StateProcessor {
    /**
     * Processes the received message in a certain state
     *
     * @param message the received message
     */
    @Override
    protected void deepProcess(Message message) {
        switch (message.getText()) {
            case "Назад":
                state = State.INITIAL;
                sendMessage.setText("Выберите действие.");
                break;
            case "Пополнить":
                state = State.FUND_DEPOSIT;
                sendMessage.setText(FundDeposit.create(userId).toString());
                break;
            case "Цель для накопления":
                state = State.FUND_GOAL;
                sendMessage.setText(FundGoal.create(userId).toString());
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
        return State.FUND;
    }
}
