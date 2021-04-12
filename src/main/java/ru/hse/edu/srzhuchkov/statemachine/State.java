package ru.hse.edu.srzhuchkov.statemachine;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import ru.hse.edu.srzhuchkov.statemachine.process.*;
import ru.hse.edu.srzhuchkov.statemachine.process.purchase.*;

public enum State {
    INITIAL(new InitialProcessor(), Replies.getInitialReplies()),
    ADD_PURCHASE(new AddPurchaseProcessor(), Replies.getAddPurchaseReplies()),
    ADD_PURCHASE_AMOUNT(new PurchaseAmountProcessor(), Replies.getCancelReplies()),
    ADD_PURCHASE_CURRENCY(new PurchaseCurrencyProcessor(), Replies.getPurchaseCurrencyReplies()),
    ADD_PURCHASE_DATE(new PurchaseDateProcessor(), Replies.getPurchaseDateReplies()),
    ADD_PURCHASE_CATEGORY(new PurchaseCategoryProcessor(), Replies.getPurchaseCategoryReplies()),
    ADD_PURCHASE_DESCRIPTION(new PurchaseDescriptionProcessor(), Replies.getPurchaseDescriptionReplies()),
    ADD_PURCHASE_QR(new PurchaseQRProcessor(), Replies.getCancelReplies()),
    DISPLAY_EXPENSES_AMOUNT(new DisplayExpensesAmountProcessor(), Replies.getDisplayExpensesAmountReplies());

    private final StateProcessor processor;
    private final String[] replies;

    State(StateProcessor processor, String[] replies) {
        this.processor = processor;
        this.replies = replies;
    }

    public SendMessage process(Message message) {
        return processor.process(message);
    }

    public ReplyKeyboard display() {
        return processor.display();
    }

    public String[] getReplies() {
        return replies;
    }
}
