package de.finn.groupsystem.group;

import de.finn.groupsystem.database.PostgreSQLClient;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class GroupRepository {
    private final PostgreSQLClient postgresql;

    public GroupRepository(PostgreSQLClient postgresql) {
        this.postgresql = postgresql;
    }

    public void createGroup(String groupName, String groupPrefix, List<String> permissions) {
        String query = "INSERT INTO groups (group_name, group_prefix, permissions) VALUES (?, ?, ?) ON CONFLICT (group_name) DO NOTHING;";

        try(PreparedStatement statement = postgresql.getConnection().prepareStatement(query)) {
            statement.setString(1, groupName);
            statement.setString(2, groupPrefix);

            java.sql.Array sqlArray = statement.getConnection().createArrayOf("text", permissions.toArray());
            statement.setArray(3, sqlArray);

            statement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void deleteGroup(String groupName) {
        String query = "DELETE FROM groups WHERE group_name = ?;";

        try (PreparedStatement statement = postgresql.getConnection().prepareStatement(query)) {
            statement.setString(1, groupName);

            statement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public boolean doesGroupExist(String groupName) {
        String query = "SELECT 1 FROM groups WHERE group_name = ?;";

        try(PreparedStatement statement = postgresql.getConnection().prepareStatement(query)) {
            statement.setString(1, groupName);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return false;
    }

    public List<Group> loadAllGroups() {
        List<Group> groups = new ArrayList<>();
        String query = "SELECT group_name, group_prefix, permissions FROM groups";

        try (PreparedStatement statement = postgresql.getConnection().prepareStatement(query)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String groupName = resultSet.getString("group_name");
                    String groupPrefix = resultSet.getString("group_prefix");

                    java.sql.Array sqlArray = resultSet.getArray("permissions");
                    String[] permissions;
                    if (sqlArray != null) {
                        permissions = (String[]) sqlArray.getArray();
                    } else {
                        permissions = new String[0];
                    }

                    groups.add(new Group(groupName, groupPrefix, new ArrayList<>(Arrays.asList(permissions))));
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return groups;
    }

    public void addPermissionToGroup(String groupName, String permission) {
        String query = """
                UPDATE groups
                SET permissions =
                    CASE
                        WHEN NOT (? = ANY (permissions)) THEN permissions || ARRAY[?]
                        ELSE permissions
                    END
                WHERE group_name = ?;
            """;

        try(PreparedStatement statement = postgresql.getConnection().prepareStatement(query)) {
            statement.setString(1, permission);
            statement.setString(2, permission);
            statement.setString(3, groupName);

            statement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void removePermissionFromGroup(String groupName, String permission) {
        String query = """
            UPDATE groups
            SET permissions = array_remove(permissions, ?)
            WHERE group_name = ?;
        """;

        try(PreparedStatement statement = postgresql.getConnection().prepareStatement(query)) {
            statement.setString(1, permission);
            statement.setString(2, groupName);

            statement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}