package ru.hse.edu.srzhuchkov.database;

import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public class Slider {
    public static EditMessageText process(CallbackQuery query) {
        String callData = query.getData();
        if (callData.startsWith("date")) {
            return DateSlider.process(query);
        }
        else {
            return CategorySlider.process(query);
        }
    }
}
