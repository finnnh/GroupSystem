package de.finn.groupsystem.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class PostgreSQLClient {
    private final Connection connection;

    public PostgreSQLClient() {
        this.connection = connect();
        createTables();
    }

    private Connection connect() {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(System.getenv("DB_CONNECTION_STRING"), System.getenv("DB_USERNAME"), System.getenv("DB_PASSWORD"));
        } catch (SQLException | ClassNotFoundException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    private void createTables() {
        String CREATE_GROUPS_TABLE = """
            CREATE TABLE IF NOT EXISTS groups (
                group_name TEXT PRIMARY KEY,
                group_prefix TEXT NOT NULL,
                permissions TEXT[] NOT NULL
            );
        """;

        String CREATE_USERS_TABLE = """
            CREATE TABLE IF NOT EXISTS users (
                uuid UUID PRIMARY KEY,
                username TEXT NOT NULL,
                lang TEXT NOT NULL,
                group_name TEXT NOT NULL REFERENCES groups(group_name) ON DELETE SET NULL,
                group_expires_at BIGINT NOT NULL
            );
        """;

        String CREATE_SIGN_TABLE = """
            CREATE TABLE IF NOT EXISTS signs (
                world_name TEXT NOT NULL,
                x INT NOT NULL,
                y INT NOT NULL,
                z INT NOT NULL
            );
        """;

        try (Statement statement = connection.createStatement()) {
            statement.execute(CREATE_GROUPS_TABLE);
            statement.execute(CREATE_USERS_TABLE);
            statement.execute(CREATE_SIGN_TABLE);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

    public Connection getConnection() {
        return connection;
    }
}