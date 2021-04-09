package ru.hse.edu.srzhuchkov.statemachine.view;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.hse.edu.srzhuchkov.statemachine.State;

import java.util.ArrayList;
import java.util.List;

public class InitialView extends StateView {
    /**
     * Sets the keyboard view for the state
     *
     * @return the keyboard
     */
    @Override
    public ReplyKeyboard display() {
        List<KeyboardRow> keyboard = new ArrayList<>();
        for (String str : getState().getReplies()) {
            KeyboardRow row = new KeyboardRow();
            row.add(str);
            keyboard.add(row);
        }
        return new ReplyKeyboardMarkup(keyboard, true, true, true);
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
