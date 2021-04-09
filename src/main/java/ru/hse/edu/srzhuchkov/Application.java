package ru.hse.edu.srzhuchkov;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.hse.edu.srzhuchkov.database.DBManager;
import ru.hse.edu.srzhuchkov.telegram.Bot;

import java.util.Map;

public class Application {
    private static final Map<String, String> env = System.getenv();

    public static void main(String[] args) {
        DBManager.getInstance().createTables();
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new Bot(env.get("BOT_NAME"), env.get("BOT_TOKEN")));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
