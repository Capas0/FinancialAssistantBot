package ru.hse.edu.srzhuchkov.database;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.hse.edu.srzhuchkov.statemachine.State;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BotUser {
    private static final DBManager dbManager = DBManager.getInstance();

    private int id;
    private State state = State.INITIAL;

    private BotUser(int id) {
        this.id = id;
    }

    public static void create(int id) {
        BotUser user = new BotUser(id);
        try (Connection connection = dbManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO STATE (user_id, value) VALUES (?, ?)\n" +
                            "ON CONFLICT (user_id) DO UPDATE SET value = 0"
            );
            statement.setInt(1, id);
            statement.setInt(2, user.state.ordinal());
            statement.executeUpdate();
        } catch (SQLException throwables) {
            System.out.println("Unable to create user.");
            throwables.printStackTrace();
        }
    }

    public static BotUser load(int id) {
        BotUser user = new BotUser(id);
        try (Connection connection = dbManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT value FROM STATE WHERE user_id = ?"
            );
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            user.state = State.values()[resultSet.getInt(1)];
        } catch (SQLException throwables) {
            System.out.println("Unable to load a user's state.");
            throwables.printStackTrace();
        }
        return user;
    }

    public SendMessage process(Message message) {
        return state.process(message);
    }
}
