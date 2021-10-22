package me.thomas2404.hardcoreLite;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ReloadEvent extends Event {

    private Player player;
    private int lives;

    public ReloadEvent(Player player, int lives) {
        this.player = player;
        this.lives = lives;
    }

    public Player getPlayer() {
        return this.player;
    }

    public int getLives() {
        return this.lives;
    }

    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
