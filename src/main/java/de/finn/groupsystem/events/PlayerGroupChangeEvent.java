package de.finn.groupsystem.events;

import de.finn.groupsystem.group.Group;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerGroupChangeEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final Group newGroup;

    public PlayerGroupChangeEvent(Player player, Group newGroup) {
        this.player = player;
        this.newGroup = newGroup;
    }

    public Player getPlayer() {
        return player;
    }

    public Group getNewGroup() {
        return newGroup;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}