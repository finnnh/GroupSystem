package de.finn.groupsystem.listener;

import de.finn.groupsystem.player.PlayerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {
    private final PlayerManager playerManager;

    public ChatListener(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        String format = "{prefix} {username} » {message}";
        format = format.replace("{prefix}", playerManager.getGroupOfPlayer(event.getPlayer()).groupPrefix())
                .replace("{username}", event.getPlayer().getName())
                .replace("{message}", event.getMessage());

        event.setFormat(format);
    }

}