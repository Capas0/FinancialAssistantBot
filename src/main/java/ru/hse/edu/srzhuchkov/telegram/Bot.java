package ru.hse.edu.srzhuchkov.telegram;

import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.hse.edu.srzhuchkov.database.BotUser;
import ru.hse.edu.srzhuchkov.statemachine.State;
import ru.hse.edu.srzhuchkov.telegram.command.ClearCommand;
import ru.hse.edu.srzhuchkov.telegram.command.HelpCommand;
import ru.hse.edu.srzhuchkov.telegram.command.StartCommand;

public class Bot extends TelegramLongPollingCommandBot {
    private final String BOT_NAME;
    private final String BOT_TOKEN;

    public Bot(String botName, String botToken) {
        super();
        BOT_NAME = botName;
        BOT_TOKEN = botToken;
    }

    /**
     * Is called when bot gets registered
     */
    @Override
    public void onRegister() {
        register(new StartCommand("start", "Старт"));
        register(new HelpCommand("help", "Помощь"));
        register(new ClearCommand("clear", "Удалить данные"));
    }

    /**
     * @return Bot username
     */
    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    /**
     * Returns the token of the bot to be able to perform Telegram Api Requests
     *
     * @return Token of the bot
     */
    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    /**
     * Process all updates, that are not commands.
     *
     * @param update the update
     * @warning Commands that have valid syntax but are not registered on this bot,
     * won't be forwarded to this method <b>if a default action is present</b>.
     */
    @Override
    public void processNonCommandUpdate(Update update) {
        Message message = update.getMessage();
        State state = BotUser.getState(message.getFrom().getId());
        SendMessage sendMessage = state.process(message);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
