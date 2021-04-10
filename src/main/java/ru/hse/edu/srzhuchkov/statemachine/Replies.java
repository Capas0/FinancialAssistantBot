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

    static String[] getPurchaseDateReplies() {
        return new String[] {"Сегодня", "Отмена"};
    }

    static String[] getPurchaseCategoryReplies() {
        return new String[] {"Без категории", "Авиабилеты", "Автоуслуги", "Аптеки", "Аренда авто", "Дом и ремонт",
                "Железнодорожные билеты", "Животные", "Искусство", "Каршеринг", "Кино", "Книги", "Красота",
                "Музыка", "Одежда и обувь", "Развлечения", "Рестораны", "Спорттовары", "Сувениры",
                "Супермаркеты", "Такси", "Топливо", "Транспорт", "Фастфуд", "Фото и видео", "Цветы", "Дьюти-фри",
                "Отмена"};
    }

    static String[] getPurchaseDescriptionReplies() {
        return new String[] {"-", "Отмена"};
    }
}
