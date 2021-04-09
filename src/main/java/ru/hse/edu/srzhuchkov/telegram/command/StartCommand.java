package ru.hse.edu.srzhuchkov.telegram.command;

import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.hse.edu.srzhuchkov.database.BotUser;
import ru.hse.edu.srzhuchkov.statemachine.State;

public class StartCommand extends BaseCommand {
    /**
     * Construct a command
     *
     * @param commandIdentifier the unique identifier of this command (e.g. the command string to
     *                          enter into chat)
     * @param description       the description of this command
     */
    public StartCommand(String commandIdentifier, String description) {
        super(commandIdentifier, description);
    }

    /**
     * Execute the command
     *
     * @param absSender absSender to send messages over
     * @param user      the user who sent the command
     * @param chat      the chat, to be able to send replies
     * @param arguments passed arguments
     */
    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        BotUser.create(user.getId());
        sendAnswer(absSender,
                chat.getId(),
                "Давайте начнём! Если Вам нужна помощь, нажмите /help",
                State.INITIAL.display());
    }
}
