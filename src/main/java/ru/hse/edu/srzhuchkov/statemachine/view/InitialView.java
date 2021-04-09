package ru.hse.edu.srzhuchkov.statemachine.view;

import ru.hse.edu.srzhuchkov.statemachine.State;

public class InitialView extends StateView {
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
