package ru.hse.edu.srzhuchkov.statemachine.view;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import ru.hse.edu.srzhuchkov.statemachine.State;

public abstract class StateView {
    /**
     * Sets the keyboard view for the state
     *
     * @return the keyboard
     */
    public abstract ReplyKeyboard display();

    /**
     * Returns the associated state
     *
     * @return the state
     */
    public abstract State getState();
}
