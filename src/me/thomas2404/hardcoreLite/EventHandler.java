package me.thomas2404.hardcoreLite;

import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Arrays;
import java.util.List;

public class EventHandler implements Listener {

    private HardcoreLite plugin;
    public EventHandler(HardcoreLite plugin) {
        this.plugin = plugin;
    }

    @org.bukkit.event.EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String uid = String.valueOf(player.getUniqueId());
        if (!plugin.fileConfiguration.contains("players." + uid)) {
            plugin.configGetter.createSections(player);
        }
        plugin.configGetter.setName(player);
        int lives = plugin.configGetter.currentLives(player);
        if (banPlayers() && lives == 0) {
            player.kickPlayer(ChatColor.WHITE + "You are on " + ChatColor.RED + "0" + ChatColor.WHITE + " lives.");
        }
        plugin.setNameColor.changeNameColor(player, lives);
    }

    @org.bukkit.event.EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player killedPlayer = event.getEntity();
        //If the entity that killed the player is a player
        if (killedPlayer.getKiller() != null) {
            if (killedPlayer.getKiller().getType() == EntityType.PLAYER) {
                Player killer = killedPlayer.getKiller();
                //Add a life to the killed player and remove a life from the killer.
                plugin.lifeChanges.addLife(killedPlayer);
                plugin.lifeChanges.removeLife(killer);
            } else {
               plugin.lifeChanges.removeLife(killedPlayer);
            }
        } else {
            plugin.lifeChanges.removeLife(killedPlayer);
        }
    }

    @org.bukkit.event.EventHandler
    public void onEntityResurrectEvent(EntityResurrectEvent event) {
        if (!event.isCancelled() && event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            plugin.lifeChanges.removeLife(player);
        }
    }


    @org.bukkit.event.EventHandler
    public void onAdvancementEvent(PlayerAdvancementDoneEvent event) {

        Player player = event.getPlayer();
        String advancement = event.getAdvancement().getKey().getKey();

        List<String> awardAdvancements = Arrays.asList("story/cure_zombie_villager", "nether/fast_travel", "nether/uneasy_alliance", "nether/create_full_beacon",
                "nether/all_effects", "nether/all_potions", "end/kill_dragon", "adventure/caves_and_cliffs", "adventure/kill_all_mobs", "adventure/arbalistic",
                "adventure/adventuring_time", "husbandry/bred_all_animals", "husbandry/complete_catalogue", "husbandry/balanced_diet", "husbandry/obtain_netherite_hoe");

        if (awardAdvancements.contains(advancement)) {
            plugin.lifeChanges.addLife(player);
        }
    }

    public boolean banPlayers() {
        String banString = plugin.fileConfiguration.getString("BanPlayersOn0Lives");
        if (banString.equalsIgnoreCase("yes") || banString.equalsIgnoreCase("y")) {
            return true;
        } else {
            return false;
        }
    }
}
