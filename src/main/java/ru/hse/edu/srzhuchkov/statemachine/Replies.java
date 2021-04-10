package ru.hse.edu.srzhuchkov.statemachine;

class Replies {
    static String[] getInitialReplies() {
        return new String[] {"Добавить покупку", "Создать цель"};
    }

    static String[] getAddPurchaseReplies() {
        return new String[] {"Сумма", "Валюта", "Дата", "Категория", "Описание", "Подтвердить", "Отмена"};
    }

    static String[] getCancelReplies() {
        return new String[] {"Отмена"};
    }

    static String[] getPurchaseCurrencyReplies() {
        return new String[] {"RUB", "USD", "EUR", "Отмена"};
    }
}
