package de.finn.groupsystem.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerLanguageChangeEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final String newLanguage;

    public PlayerLanguageChangeEvent(Player player, String newLanguage) {
        this.player = player;
        this.newLanguage = newLanguage;
    }

    public Player getPlayer() {
        return player;
    }

    public String getNewLanguage() {
        return newLanguage;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}