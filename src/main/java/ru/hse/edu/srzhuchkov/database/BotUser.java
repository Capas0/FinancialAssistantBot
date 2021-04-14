package ru.hse.edu.srzhuchkov.database;

import ru.hse.edu.srzhuchkov.statemachine.State;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BotUser {
    private static final DBManager dbManager = DBManager.getInstance();

    public static void create(int userId) {
        try (Connection connection = dbManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO state (user_id, value) VALUES (?, ?)\n" +
                            "ON CONFLICT (user_id) DO UPDATE SET value = 0"
            );
            statement.setInt(1, userId);
            statement.setInt(2, State.INITIAL.ordinal());
            statement.executeUpdate();
        } catch (SQLException throwables) {
            System.out.println("Unable to create user.");
            throwables.printStackTrace();
        }
    }

    public static State getState(int userId) {
        State state = State.INITIAL;
        try (Connection connection = dbManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT value FROM STATE WHERE user_id = ?"
            );
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                state = State.values()[resultSet.getInt(1)];
            }
        } catch (SQLException throwables) {
            System.out.println("Unable to load a user's state.");
            throwables.printStackTrace();
        }
        return state;
    }

    public static void setState(int userId, State state) {
        try (Connection connection = dbManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE state SET value = ? WHERE user_id = ?"
            );
            statement.setInt(1, state.ordinal());
            statement.setInt(2, userId);
            statement.executeUpdate();
        } catch (SQLException throwables) {
            System.out.println("Unable to set the user's state.");
            throwables.printStackTrace();
        }
    }
}
