package ru.hse.edu.srzhuchkov.statemachine;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import ru.hse.edu.srzhuchkov.statemachine.process.AddPurchaseProcessor;
import ru.hse.edu.srzhuchkov.statemachine.process.InitialProcessor;
import ru.hse.edu.srzhuchkov.statemachine.process.StateProcessor;
import ru.hse.edu.srzhuchkov.statemachine.view.AddPurchaseView;
import ru.hse.edu.srzhuchkov.statemachine.view.InitialView;
import ru.hse.edu.srzhuchkov.statemachine.view.StateView;

public enum State {
    INITIAL(new InitialProcessor(), new InitialView(), Replies.getInitialReplies()),
    ADD_PURCHASE(new AddPurchaseProcessor(), new AddPurchaseView(), Replies.getAddPurchaseReplies());

    private final StateProcessor processor;
    private final StateView view;
    private final String[] replies;

    State(StateProcessor processor, StateView view, String[] replies) {
        this.processor = processor;
        this.view = view;
        this.replies = replies;
    }

    public SendMessage process(Message message) {
        return processor.process(message);
    }

    public ReplyKeyboard display() {
        return view.display();
    }

    public String[] getReplies() {
        return replies;
    }
}

