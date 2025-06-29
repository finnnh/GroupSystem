package de.finn.groupsystem.signs;

import de.finn.groupsystem.player.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.ArrayList;


public class SignManager {
    private final SignRepository signRepository;
    private final PlayerManager playerManager;
    private final ArrayList<Location> signLocations;

    public SignManager(SignRepository signRepository, PlayerManager playerManager) {
        this.signRepository = signRepository;
        this.playerManager = playerManager;
        this.signLocations = loadAllSignLocations();
    }

    private ArrayList<Location> loadAllSignLocations() {
        return signRepository.loadAllSignLocations();
    }

    public boolean doesSignExist(Location location) {
        return signLocations.contains(location);
    }

    public void addSign(Sign sign) {
        int x = sign.getX();
        int y = sign.getY();
        int z = sign.getZ();

        signRepository.addSign(sign.getWorld().getName(), x, y, z);
        signLocations.add(new Location(sign.getWorld(), x, y, z));
        updateAllSignsForAllPlayers();
    }

    private void updateAllSignsForAllPlayers() {
        Bukkit.getServer().getOnlinePlayers().forEach(this::updateAllSignsPerPlayer);
    }

    public void updateAllSignsPerPlayer(Player player) {
        signLocations.forEach(signLocation -> player.sendSignChange(signLocation, new String[] {"---", player.getName(), playerManager.getGroupOfPlayer(player).groupName(), "---"}));
    }
}