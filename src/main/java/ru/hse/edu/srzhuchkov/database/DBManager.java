package ru.hse.edu.srzhuchkov.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

public class DBManager {
    private static volatile DBManager instance;

    private final String url;
    private final String user;
    private final String password;

    private DBManager() {
        Map<String, String> env = System.getenv();
        url = env.get("DB_URL");
        user = env.get("DB_USER");
        password = env.get("DB_PASSWORD");
    }

    public static DBManager getInstance() {
        DBManager localInstance = instance;
        if (localInstance == null) {
            synchronized (DBManager.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new DBManager();
                }
            }
        }
        return localInstance;
    }

    public Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException throwables) {
            System.out.println("Unable to create a connection.");
            throwables.printStackTrace();
        }
        return connection;
    }

    public void createTables() {
        try (Connection connection = getConnection()) {
            createTable(connection, "CATEGORY",
                    "CATEGORY_ID SERIAL PRIMARY KEY,\n" +
                            "TITLE TEXT UNIQUE"
            );
            fillCategories(connection);
            createTable(connection, "PURCHASE",
                    "PURCHASE_ID SERIAL PRIMARY KEY,\n" +
                            "USER_ID INTEGER NOT NULL,\n" +
                            "PDATE BIGINT,\n" +
                            "AMOUNT NUMERIC(18,2) NOT NULL CHECK (AMOUNT >= 0),\n" +
                            "CURRENCY TEXT NOT NULL,\n" +
                            "CATEGORY_ID INTEGER NOT NULL,\n" +
                            "DESCRIPTION TEXT,\n" +
                            "FOREIGN KEY (CATEGORY_ID) REFERENCES CATEGORY(CATEGORY_ID)"
            );
            createTable(connection, "TEMP_PURCHASE",
                    "USER_ID INTEGER PRIMARY KEY,\n" +
                            "PDATE BIGINT,\n" +
                            "AMOUNT NUMERIC(18,2) CHECK (AMOUNT >= 0),\n" +
                            "CURRENCY TEXT,\n" +
                            "CATEGORY_ID INTEGER,\n" +
                            "DESCRIPTION TEXT"
            );
            createTable(connection, "STATE",
                    "USER_ID INTEGER PRIMARY KEY,\n" +
                            "VALUE INTEGER CHECK (VALUE >= 0)"
            );
            createTable(connection, "FUND",
                    "USER_ID INTEGER,\n" +
                            "VALUE NUMERIC(18,2),\n" +
                            "CURRENCY TEXT,\n" +
                            "PRIMARY KEY (USER_ID, CURRENCY)"
            );
            createTable(connection, "FUND_DEPOSIT",
                    "USER_ID INTEGER PRIMARY KEY,\n" +
                            "AMOUNT NUMERIC(18,2) CHECK (AMOUNT >= 0),\n" +
                            "CURRENCY TEXT"
            );
            createTable(connection, "FUND_GOAL",
                    "USER_ID INTEGER PRIMARY KEY,\n" +
                            "AMOUNT NUMERIC(18,2) CHECK (AMOUNT >= 0),\n" +
                            "CURRENCY TEXT,\n" +
                            "ENABLED BOOLEAN"
            );
            createTable(connection, "AMOUNT_EXPENSES_SETTINGS",
                    "USER_ID INTEGER PRIMARY KEY,\n" +
                            "BEG_DATE BIGINT,\n" +
                            "END_DATE BIGINT,\n" +
                            "CURRENCY TEXT"
            );
            createTable(connection, "DATE_SLIDER",
                    "SLIDER_ID SERIAL PRIMARY KEY,\n" +
                            "USER_ID INTEGER NOT NULL,\n" +
                            "BEG_DATE BIGINT NOT NULL,\n" +
                            "END_DATE BIGINT NOT NULL"
            );
            createTable(connection, "CATEGORY_SLIDER",
                    "SLIDER_ID SERIAL PRIMARY KEY,\n" +
                            "USER_ID INTEGER NOT NULL,\n" +
                            "CATEGORY_ID INTEGER NOT NULL");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private static void createTable(Connection connection, String name, String params) {
        try {
            connection.createStatement().execute(
                    String.format("CREATE TABLE IF NOT EXISTS %s\n(\n%s\n);", name, params)
            );
        } catch (SQLException sqlException) {
            System.out.printf("Unable to create a table %s.", name);
            sqlException.printStackTrace();
        }
    }

    private static void fillCategories(Connection connection) {
        try {
            connection.createStatement().execute(
                    "INSERT INTO CATEGORY (TITLE) VALUES\n" +
                            "('Без категории'),\n" +
                            "('Авиабилеты'),\n" +
                            "('Автоуслуги'),\n" +
                            "('Аптеки'),\n" +
                            "('Аренда авто'),\n" +
                            "('Дом и ремонт'),\n" +
                            "('Железнодорожные билеты'),\n" +
                            "('Животные'),\n" +
                            "('Искусство'),\n" +
                            "('Каршеринг'),\n" +
                            "('Кино'),\n" +
                            "('Книги'),\n" +
                            "('Красота'),\n" +
                            "('Музыка'),\n" +
                            "('Одежда и обувь'),\n" +
                            "('Развлечения'),\n" +
                            "('Рестораны'),\n" +
                            "('Спорттовары'),\n" +
                            "('Сувениры'),\n" +
                            "('Супермаркеты'),\n" +
                            "('Такси'),\n" +
                            "('Топливо'),\n" +
                            "('Транспорт'),\n" +
                            "('Фастфуд'),\n" +
                            "('Фото и видео'),\n" +
                            "('Цветы'),\n" +
                            "('Дьюти-фри')\n" +
                            "ON CONFLICT DO NOTHING;"
            );
        } catch (SQLException sqlException) {
            System.out.println("Unable to fill the table CATEGORY.");
            sqlException.printStackTrace();
        }
    }
}