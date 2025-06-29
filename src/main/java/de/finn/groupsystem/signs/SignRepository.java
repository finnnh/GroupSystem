package de.finn.groupsystem.signs;

import de.finn.groupsystem.database.PostgreSQLClient;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SignRepository {
    private final PostgreSQLClient postgresql;

    public SignRepository(PostgreSQLClient postgresql) {
        this.postgresql = postgresql;
    }

    public ArrayList<Location> loadAllSignLocations() {
        ArrayList<Location> signLocations = new ArrayList<>();
        String query = "SELECT world_name, x, y, z FROM signs";

        try (PreparedStatement statement = postgresql.getConnection().prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String worldName = resultSet.getString("world_name");
                int x = resultSet.getInt("x");
                int y = resultSet.getInt("y");
                int z = resultSet.getInt("z");

                signLocations.add(new Location(Bukkit.getWorld(worldName), x, y, z));
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return signLocations;
    }

    public void addSign(String worldName, int x, int y, int z) {
        String query = "INSERT INTO signs (world_name, x, y, z) VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement = postgresql.getConnection().prepareStatement(query)) {
            statement.setString(1, worldName);
            statement.setInt(2, x);
            statement.setInt(3, y);
            statement.setInt(4, z);

            statement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}