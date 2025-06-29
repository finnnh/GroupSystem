package de.finn.groupsystem.player;

import de.finn.groupsystem.database.PostgreSQLClient;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerRepository {
    private final PostgreSQLClient postgresql;

    public PlayerRepository(PostgreSQLClient postgresql) {
        this.postgresql = postgresql;
    }

    public boolean doesPlayerExist(UUID uuid) {
        String query = "SELECT EXISTS (SELECT 1 FROM users WHERE uuid = ?);";

        try (PreparedStatement statement = postgresql.getConnection().prepareStatement(query)) {
            statement.setObject(1, uuid);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBoolean(1);
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return false;
    }

    public boolean doesPlayerExist(String username) {
        String query = "SELECT EXISTS (SELECT 1 FROM users WHERE username = ?);";

        try (PreparedStatement statement = postgresql.getConnection().prepareStatement(query)) {
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBoolean(1);
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return false;
    }

    public void insertPlayer(UUID uuid, String username, String language) {
        String query = "INSERT INTO users (uuid, username, lang, group_name, group_expires_at) VALUES (?, ?, ?, 'default', -1);";

        try (PreparedStatement statement = postgresql.getConnection().prepareStatement(query)) {
            statement.setObject(1, uuid);
            statement.setString(2, username);
            statement.setString(3, language);

            statement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public List<String> getAllUsernamesWithGroup(String groupName) {
        List<String> usernames = new ArrayList<>();
        String query = "SELECT username FROM users WHERE group_name = ?;";

        try (PreparedStatement statement = postgresql.getConnection().prepareStatement(query)) {
            statement.setString(1, groupName);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    usernames.add(resultSet.getString("username"));
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return usernames;
    }

    public String getUsername(UUID uuid) {
        String query = "SELECT username FROM users WHERE uuid = ?;";

        try (PreparedStatement statement = postgresql.getConnection().prepareStatement(query)) {
            statement.setObject(1, uuid);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("username");
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public void updateUsernameByUUID(UUID uuid, String username) {
        String query = "UPDATE users SET username = ? WHERE uuid = ?;";

        try (PreparedStatement statement = postgresql.getConnection().prepareStatement(query)) {
            statement.setString(1, username);
            statement.setObject(2, uuid);

            statement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public String getGroupNameOfPlayer(String playerName) {
        String query = "SELECT group_name FROM users WHERE username = ?;";

        try(PreparedStatement statement = postgresql.getConnection().prepareStatement(query)) {
            statement.setString(1, playerName);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("group_name");
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public String getGroupNameOfPlayer(UUID uuid) {
        String query = "SELECT group_name FROM users WHERE uuid = ?;";

        try (PreparedStatement statement = postgresql.getConnection().prepareStatement(query)) {
            statement.setObject(1, uuid);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("group_name");
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public Long getDurationOfGroup(String playerName) {
        String query = "SELECT group_expires_at FROM users WHERE username = ?;";

        try (PreparedStatement statement = postgresql.getConnection().prepareStatement(query)) {
            statement.setString(1, playerName);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getLong("group_expires_at");
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public Long getExpiryOfPlayerGroup(UUID uuid) {
        String query = "SELECT group_expires_at FROM users WHERE uuid = ?;";

        try (PreparedStatement statement = postgresql.getConnection().prepareStatement(query)) {
            statement.setObject(1, uuid);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getLong("group_expires_at");
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return -1L;
    }

    public void setGroupOfPlayer(String playerName, String groupName, Long expiryLong) {
        String query = "UPDATE users SET group_name = ?, group_expires_at = ? WHERE username = ?";

        try (PreparedStatement statement = postgresql.getConnection().prepareStatement(query)) {
            statement.setString(1,  groupName);
            statement.setLong(2, expiryLong);
            statement.setString(3, playerName);

            statement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public String getLanguage(UUID uuid) {
        String query = "SELECT lang FROM users WHERE uuid = ?;";

        try (PreparedStatement statement = postgresql.getConnection().prepareStatement(query)) {
            statement.setObject(1, uuid);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("lang");
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public void updateLanguageByUUID(UUID uuid, String language) {
        String query = "UPDATE users SET lang = ? WHERE uuid = ?;";

        try (PreparedStatement statement = postgresql.getConnection().prepareStatement(query)) {
            statement.setString(1, language);
            statement.setObject(2, uuid);

            statement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}