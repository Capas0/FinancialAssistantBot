package ru.hse.edu.srzhuchkov.telegram.command;

import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.hse.edu.srzhuchkov.database.DBManager;

public abstract class BaseCommand extends BotCommand {
    protected DBManager dbManager = DBManager.getInstance();

    /**
     * Construct a command
     *
     * @param commandIdentifier the unique identifier of this command (e.g. the command string to
     *                          enter into chat)
     * @param description       the description of this command
     */
    public BaseCommand(String commandIdentifier, String description) {
        super(commandIdentifier, description);
    }

    void sendAnswer(AbsSender absSender, Long chatId, String text) {
        sendAnswer(absSender, chatId, text, null);
    }

    void sendAnswer(AbsSender absSender, Long chatId, String text, ReplyKeyboard keyboard) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setReplyMarkup(keyboard);
        message.setText(text);
        try {
            absSender.execute(message);
        } catch (TelegramApiException e) {
            System.out.println("Exception while sending a message.");
            e.printStackTrace();
        }
    }
}
