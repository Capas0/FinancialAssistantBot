package ru.hse.edu.srzhuchkov.statemachine;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import ru.hse.edu.srzhuchkov.statemachine.process.*;
import ru.hse.edu.srzhuchkov.statemachine.process.deposit.FundDepositAmountProcessor;
import ru.hse.edu.srzhuchkov.statemachine.process.deposit.FundDepositCurrencyProcessor;
import ru.hse.edu.srzhuchkov.statemachine.process.expenses.ExpensesBeginDateProcessor;
import ru.hse.edu.srzhuchkov.statemachine.process.expenses.ExpensesEndDateProcessor;
import ru.hse.edu.srzhuchkov.statemachine.process.expensesamount.ExpensesAmountBeginDateProcessor;
import ru.hse.edu.srzhuchkov.statemachine.process.expensesamount.ExpensesAmountCurrencyProcessor;
import ru.hse.edu.srzhuchkov.statemachine.process.expensesamount.ExpensesAmountEndDateProcessor;
import ru.hse.edu.srzhuchkov.statemachine.process.fund.FundDepositProcessor;
import ru.hse.edu.srzhuchkov.statemachine.process.fund.FundGoalProcessor;
import ru.hse.edu.srzhuchkov.statemachine.process.fund.goal.FundGoalAmountProcessor;
import ru.hse.edu.srzhuchkov.statemachine.process.fund.goal.FundGoalCurrencyProcessor;
import ru.hse.edu.srzhuchkov.statemachine.process.purchase.*;

public enum State {
    INITIAL(new InitialProcessor(), Replies.getInitialReplies()),
    ADD_PURCHASE(new AddPurchaseProcessor(), Replies.getAddPurchaseReplies()),
    ADD_PURCHASE_AMOUNT(new PurchaseAmountProcessor(), Replies.getCancelReplies()),
    ADD_PURCHASE_CURRENCY(new PurchaseCurrencyProcessor(), Replies.getCurrencyReplies()),
    ADD_PURCHASE_DATE(new PurchaseDateProcessor(), Replies.getDateReplies()),
    ADD_PURCHASE_CATEGORY(new PurchaseCategoryProcessor(), Replies.getCategoryReplies()),
    ADD_PURCHASE_DESCRIPTION(new PurchaseDescriptionProcessor(), Replies.getPurchaseDescriptionReplies()),
    ADD_PURCHASE_QR(new PurchaseQRProcessor(), Replies.getCancelReplies()),
    DISPLAY_EXPENSES_AMOUNT(new DisplayExpensesAmountProcessor(), Replies.getDisplayExpensesAmountReplies()),
    EXPENSES_AMOUNT_BEGIN_DATE(new ExpensesAmountBeginDateProcessor(), Replies.getDateReplies()),
    EXPENSES_AMOUNT_END_DATE(new ExpensesAmountEndDateProcessor(), Replies.getDateReplies()),
    EXPENSES_AMOUNT_CURRENCY(new ExpensesAmountCurrencyProcessor(), Replies.getCurrencyReplies()),
    DISPLAY_EXPENSES(new DisplayExpensesProcessor(), Replies.getDisplayExpensesReplies()),
    EXPENSES_BEGIN_DATE(new ExpensesBeginDateProcessor(), Replies.getDateReplies()),
    EXPENSES_END_DATE(new ExpensesEndDateProcessor(), Replies.getDateReplies()),
    DISPLAY_EXPENSES_CATEGORY(new DisplayExpensesCategoryProcessor(), Replies.getCategoryReplies()),
    DISPLAY_AMOUNT_EXPENSES_CATEGORY_COHORTS(new DisplayAmountExpensesCategoryCohortsProcessor(), Replies.getCurrencyReplies()),
    FUND(new FundProcessor(), Replies.getFundReplies()),
    FUND_DEPOSIT(new FundDepositProcessor(), Replies.getFundDepositReplies()),
    FUND_DEPOSIT_AMOUNT(new FundDepositAmountProcessor(), Replies.getCancelReplies()),
    FUND_DEPOSIT_CURRENCY(new FundDepositCurrencyProcessor(), Replies.getCurrencyReplies()),
    FUND_GOAL(new FundGoalProcessor(), Replies.getFundGoalReplies()),
    FUND_GOAL_AMOUNT(new FundGoalAmountProcessor(), Replies.getCancelReplies()),
    FUND_GOAL_CURRENCY(new FundGoalCurrencyProcessor(), Replies.getCurrencyReplies());

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
