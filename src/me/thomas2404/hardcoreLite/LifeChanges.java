package me.thomas2404.hardcoreLite;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class LifeChanges {

    HardcoreLite plugin;

    ConfigGetter configGetter = new ConfigGetter();
    SetNameColor setNameColor = new SetNameColor();
    EventHandler eventHandler = new EventHandler();
    LifeWord lifeWord = new LifeWord();

    public LifeChanges() {
    }

    public void removeLife(Player player) {

        String uid = String.valueOf(player.getUniqueId());
        //If lives are set to a number less than zero, bump the player back up to 0 lives.
        if (configGetter.currentLives(player) < 0) {
            configGetter.setLives(player, 0);
        }
        //If the player has a life, remove one and broadcast that they lost a life.
        if (hasLife(player)) {
            //Remove a life from the player
            int lives = configGetter.currentLives(player) - 1;
            configGetter.setLives(player,lives);
            //Call the name color method.
            setNameColor.changeNameColor(player, lives);
            //"Ban" player in they're on 0 lives, and you're supposed to ban players.
            if (eventHandler.banPlayers() && configGetter.currentLives(player) == 0) {
                plugin.getServer().broadcastMessage(ChatColor.RED + player.getName() + ChatColor.WHITE + " is at " + ChatColor.RED + "0" + ChatColor.WHITE + " lives! They have now been " + ChatColor.RED + ChatColor.BOLD + " banned" + ChatColor.WHITE + "!");
                player.kickPlayer(ChatColor.WHITE + "You are on " + ChatColor.RED + "0" + ChatColor.WHITE + " lives.");
            } else {

                //Broadcast that the player lost a life.
                plugin.getServer().broadcastMessage(ChatColor.RED + player.getName() + ChatColor.WHITE + " has lost a life! They now have " + ChatColor.RED + lives + ChatColor.WHITE + " " + lifeWord.getWord(lives));
            }
        } else {
            plugin.getServer().broadcastMessage(ChatColor.RED + player.getName() + ChatColor.WHITE + " has lost a life! They have " + ChatColor.RED + "0" + ChatColor.WHITE + " lives.");
        }
    }

    public void addLife(Player player) {
        int lives = configGetter.currentLives(player) + 1;
        configGetter.setLives(player, lives);
        setNameColor.changeNameColor(player, lives);

        plugin.getServer().broadcastMessage(ChatColor.WHITE + player.getName() + " has gained a life! They now have " + ChatColor.RED + lives + ChatColor.WHITE + " " + lifeWord.getWord(lives));
    }

    private boolean hasLife(Player player) {
        int lives = configGetter.currentLives(player);
        return lives > 0;
    }

}
