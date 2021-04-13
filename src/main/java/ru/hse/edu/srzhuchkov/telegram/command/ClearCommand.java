package ru.hse.edu.srzhuchkov.telegram.command;

import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ClearCommand extends BaseCommand {
    /**
     * Construct a command
     *
     * @param commandIdentifier the unique identifier of this command (e.g. the command string to
     *                          enter into chat)
     * @param description       the description of this command
     */
    public ClearCommand(String commandIdentifier, String description) {
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
        try (Connection connection = dbManager.getConnection()) {
            PreparedStatement statement;
            statement = connection.prepareStatement("DELETE FROM purchase WHERE user_id = ?");
            statement.setInt(1, user.getId());
            statement.executeUpdate();

            statement = connection.prepareStatement("DELETE FROM temp_purchase WHERE user_id = ?");
            statement.setInt(1, user.getId());
            statement.executeUpdate();

            statement = connection.prepareStatement("DELETE FROM amount_expenses_settings WHERE user_id = ?");
            statement.setInt(1, user.getId());
            statement.executeUpdate();

            statement = connection.prepareStatement("DELETE FROM date_slider WHERE user_id = ?");
            statement.setInt(1, user.getId());
            statement.executeUpdate();

            statement = connection.prepareStatement("DELETE FROM category_slider WHERE user_id = ?");
            statement.setInt(1, user.getId());
            statement.executeUpdate();

            statement = connection.prepareStatement("UPDATE state SET value = 0 WHERE user_id = ?");
            statement.setInt(1, user.getId());
            statement.executeUpdate();
        } catch (SQLException throwables) {
            System.out.println("Unable to clear the user data.");
            throwables.printStackTrace();
        }

        sendAnswer(absSender, chat.getId(), "Все данные о Вас удалены.");
    }
}
