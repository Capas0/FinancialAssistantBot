package ru.hse.edu.srzhuchkov.statemachine;

class Replies {
    static String[] getInitialReplies() {
        return new String[] {"Добавить покупку", "Общая сумма расходов", "Список расходов", "Расходы в категории", "Распределение расходов по категориям", "Счет"};
    }

    static String[] getAddPurchaseReplies() {
        return new String[] {"Сумма", "Валюта", "Дата", "Категория", "Описание", "Сканировать QR код", "Подтвердить", "Отмена"};
    }

    static String[] getCancelReplies() {
        return new String[] {"Отмена"};
    }

    static String[] getCurrencyReplies() {
        return new String[] {"RUB", "USD", "EUR", "Отмена"};
    }

    static String[] getDateReplies() {
        return new String[] {"Сегодня", "Отмена"};
    }

    static String[] getCategoryReplies() {
        return new String[] {"Без категории", "Авиабилеты", "Автоуслуги", "Аптеки", "Аренда авто", "Дом и ремонт",
                "Железнодорожные билеты", "Животные", "Искусство", "Каршеринг", "Кино", "Книги", "Красота",
                "Музыка", "Одежда и обувь", "Развлечения", "Рестораны", "Спорттовары", "Сувениры",
                "Супермаркеты", "Такси", "Топливо", "Транспорт", "Фастфуд", "Фото и видео", "Цветы", "Дьюти-фри",
                "Отмена"};
    }

    static String[] getPurchaseDescriptionReplies() {
        return new String[] {"-", "Отмена"};
    }

    static String[] getDisplayExpensesAmountReplies() {
        return new String[] {"Дата начала", "Дата конца", "Валюта", "Отобразить результат", "Отмена"};
    }

    static String[] getDisplayExpensesReplies() {
        return new String[] {"Дата начала", "Дата конца", "Отобразить результат", "Отмена"};
    }

    static String[] getFundReplies() {
        return new String[] {"Пополнить", "Создать цель для накопления", "Назад"};
    }

    public static String[] getFundDepositReplies() {
        return new String[] {"Сумма", "Валюта", "Подтвердить", "Отмена"};
    }
}
