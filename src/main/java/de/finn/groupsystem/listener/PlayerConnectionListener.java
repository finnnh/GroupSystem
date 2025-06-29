package de.finn.groupsystem.listener;

import de.finn.groupsystem.GroupSystem;
import de.finn.groupsystem.language.LanguageManager;
import de.finn.groupsystem.player.PlayerManager;
import de.finn.groupsystem.signs.SignManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerConnectionListener implements Listener {
    private final GroupSystem plugin;
    private final PlayerManager playerManager;
    private final LanguageManager languageManager;
    private final SignManager signManager;

    public PlayerConnectionListener(GroupSystem plugin, PlayerManager playerManager, LanguageManager languageManager, SignManager signManager) {
        this.plugin = plugin;
        this.playerManager = playerManager;
        this.languageManager = languageManager;
        this.signManager = signManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        playerManager.connectPlayer(player);

        event.setJoinMessage(null);
        Bukkit.getOnlinePlayers().forEach(players -> {
            players.sendMessage(languageManager.getMessage(players, "player_join", playerManager.getGroupOfPlayer(player).groupPrefix(), player.getName()));
        });

        new BukkitRunnable() {
            @Override
            public void run() {
                signManager.updateAllSignsPerPlayer(player);
            }
        }.runTaskLaterAsynchronously(plugin,  5L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        event.setQuitMessage(null);
        Bukkit.getOnlinePlayers().forEach(players -> {
            players.sendMessage(languageManager.getMessage(players, "player_quit", playerManager.getGroupOfPlayer(player).groupPrefix(), player.getName()));
        });

        playerManager.disconnectPlayer(player);
    }
}