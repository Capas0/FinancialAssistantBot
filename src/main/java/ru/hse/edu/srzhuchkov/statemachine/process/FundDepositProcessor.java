package ru.hse.edu.srzhuchkov.statemachine.process;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.hse.edu.srzhuchkov.database.Fund;
import ru.hse.edu.srzhuchkov.database.FundDeposit;
import ru.hse.edu.srzhuchkov.statemachine.State;

public class FundDepositProcessor extends StateProcessor {
    /**
     * Processes the received message in a certain state
     *
     * @param message the received message
     */
    @Override
    protected void deepProcess(Message message) {
        switch (message.getText()) {
            case "Сумма":
                state = State.FUND_DEPOSIT_AMOUNT;
                sendMessage.setText("Введите сумму.");
                break;
            case "Валюта":
                state = State.FUND_DEPOSIT_CURRENCY;
                sendMessage.setText("Выберите валюту.");
                break;
            case "Подтвердить":
                state = State.INITIAL;
                FundDeposit.confirm(userId);
                sendMessage.setText("Пополнение совершено.");
                break;
            case "Отмена":
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
        return State.FUND_DEPOSIT;
    }
}
