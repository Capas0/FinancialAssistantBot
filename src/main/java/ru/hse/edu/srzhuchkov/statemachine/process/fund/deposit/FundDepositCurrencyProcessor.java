package ru.hse.edu.srzhuchkov.statemachine.process.fund.deposit;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.hse.edu.srzhuchkov.database.FundDeposit;
import ru.hse.edu.srzhuchkov.statemachine.State;
import ru.hse.edu.srzhuchkov.statemachine.process.StateProcessor;

import java.util.Currency;

public class FundDepositCurrencyProcessor extends StateProcessor {
    /**
     * Processes the received message in a certain state
     *
     * @param message the received message
     */
    @Override
    protected void deepProcess(Message message) {
        state = State.FUND_DEPOSIT;

        FundDeposit deposit = FundDeposit.load(userId);
        if (!message.getText().equals("Отмена") && deposit != null) {
            deposit.setCurrency(Currency.getInstance(message.getText()));
            deposit.save();
        }
        if (deposit != null) {
            sendMessage.setText(deposit.toString());
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
        return State.FUND_DEPOSIT_CURRENCY;
    }
}
