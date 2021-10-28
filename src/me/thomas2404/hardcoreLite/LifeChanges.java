package me.thomas2404.hardcoreLite;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class LifeChanges {

    private HardcoreLite plugin;
    public LifeChanges(HardcoreLite plugin) {
        this.plugin = plugin;
    }

    public void removeLife(Player player) {
        //If lives are set to a number less than zero, bump the player back up to 0 lives.
        if (plugin.configGetter.currentLives(player) < 0) {
            plugin.configGetter.setLives(player, 0);
        }
        //If the player has a life, remove one and broadcast that they lost a life.
        if (hasLife(player)) {
            //Remove a life from the player
            int lives = plugin.configGetter.currentLives(player) - 1;
            plugin.configGetter.setLives(player,lives);
            //Call the name color method.
            plugin.setNameColor.changeNameColor(player, lives);
            //"Ban" player in they're on 0 lives, and you're supposed to ban players.
            if (plugin.eventHandler.banPlayers() && plugin.configGetter.currentLives(player) == 0) {
                plugin.getServer().broadcastMessage(ChatColor.RED + player.getName() + ChatColor.WHITE + " is at " + ChatColor.RED + "0" + ChatColor.WHITE + " lives! They have now been " + ChatColor.RED + ChatColor.BOLD + " banned" + ChatColor.WHITE + "!");
                player.kickPlayer(ChatColor.WHITE + "You are on " + ChatColor.RED + "0" + ChatColor.WHITE + " lives.");
            } else {
                //Broadcast that the player lost a life.
                plugin.getServer().broadcastMessage(ChatColor.RED + player.getName() + ChatColor.WHITE + " has lost a life! They now have " + ChatColor.RED + lives + ChatColor.WHITE + " " + plugin.lifeWord.getWord(lives) + ".");
            }
        } else {
            plugin.getServer().broadcastMessage(ChatColor.RED + player.getName() + ChatColor.WHITE + " has lost a life! They have still have " + ChatColor.RED + "0" + ChatColor.WHITE + " lives.");
        }
    }

    public void addLife(Player player) {
        //Set up the life variable.
        int lives = plugin.configGetter.currentLives(player) + 1;

        plugin.configGetter.setLives(player, lives);
        plugin.setNameColor.changeNameColor(player, lives);

        plugin.getServer().broadcastMessage(ChatColor.WHITE + player.getName() + " has gained a life! They now have " + ChatColor.RED + lives + ChatColor.WHITE + " " + plugin.lifeWord.getWord(lives) + ".");
    }

    private boolean hasLife(Player player) {
        int lives = plugin.configGetter.currentLives(player);
        return lives > 0;
    }
}
