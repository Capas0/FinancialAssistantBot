package ru.hse.edu.srzhuchkov.statemachine.view;

import ru.hse.edu.srzhuchkov.statemachine.State;

public class PurchaseDescriptionView extends StateView {
    /**
     * Returns the associated state
     *
     * @return the state
     */
    @Override
    public State getState() {
        return State.ADD_PURCHASE_DESCRIPTION;
    }
}
