package ru.hse.edu.srzhuchkov.statemachine.process;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.hse.edu.srzhuchkov.database.AmountExpensesSettings;
import ru.hse.edu.srzhuchkov.statemachine.State;

import java.util.ArrayList;
import java.util.List;

public class DisplayExpensesAmountProcessor extends StateProcessor {
    /**
     * Processes the received message in a certain state
     *
     * @param message the received message
     */
    @Override
    protected void deepProcess(Message message) {
        switch (message.getText()) {
            case "Дата начала":
                state = State.EXPENSES_AMOUNT_BEGIN_DATE;
                sendMessage.setText("Введите дату в формате дд.мм.гггг");
                break;
            case "Дата конца":
                state = State.EXPENSES_AMOUNT_END_DATE;
                sendMessage.setText("Введите дату в формате дд.мм.гггг");
                break;
            case "Валюта":
                state = State.EXPENSES_AMOUNT_CURRENCY;
                sendMessage.setText("Выберите валюту.");
                break;
            case "Отобразить результат":
                state = State.INITIAL;
                sendMessage.setText(AmountExpensesSettings.execute(userId));
                break;
            case "Отмена":
                state = State.INITIAL;
                sendMessage.setText("Выберите действие.");
                break;
        }
    }

    /**
     * Sets the keyboard view for the state
     *
     * @return the keyboard
     */
    @Override
    public ReplyKeyboard display() {
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row;

        row = new KeyboardRow();
        row.add("Дата начала");
        row.add("Дата конца");
        keyboard.add(row);

        row = new KeyboardRow();
        row.add("Валюта");
        keyboard.add(row);

        row = new KeyboardRow();
        row.add("Отобразить результат");
        keyboard.add(row);

        row = new KeyboardRow();
        row.add("Отмена");
        keyboard.add(row);

        return new ReplyKeyboardMarkup(keyboard, true, true, true);
    }

    /**
     * Returns the associated state
     *
     * @return the state
     */
    @Override
    public State getState() {
        return State.DISPLAY_EXPENSES_AMOUNT;
    }
}
