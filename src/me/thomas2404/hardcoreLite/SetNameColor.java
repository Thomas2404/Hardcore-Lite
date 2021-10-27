package me.thomas2404.hardcoreLite;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SetNameColor {

    public void changeNameColor(Player player, int lives) {
        switch (lives) {
            case 5: player.setDisplayName(ChatColor.DARK_GREEN + player.getName());
                player.setPlayerListName(player.getDisplayName());
                break;
            case 4: player.setDisplayName(ChatColor.GREEN + player.getName());
                player.setPlayerListName(player.getDisplayName());
                break;
            case 3: player.setDisplayName(ChatColor.YELLOW + player.getName());
                player.setPlayerListName(player.getDisplayName());
                break;
            case 2: player.setDisplayName(ChatColor.RED + player.getName());
                player.setPlayerListName(player.getDisplayName());
                break;
            case 1: player.setDisplayName(ChatColor.DARK_RED + player.getName());
                player.setPlayerListName(player.getDisplayName());
                break;
            default: if (lives > 5) {
                player.setDisplayName(ChatColor.DARK_GREEN + player.getName());
            } else {
                player.setDisplayName(ChatColor.GRAY + player.getName());
            }
                player.setPlayerListName(player.getDisplayName());
                break;
        }
    }
}
