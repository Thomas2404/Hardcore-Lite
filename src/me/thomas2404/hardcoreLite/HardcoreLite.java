package me.thomas2404.hardcoreLite;

import net.minecraft.advancements.Advancement;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

public class HardcoreLite extends JavaPlugin {

    public static HardcoreLite plugin;

    public static HardcoreLite getPlugin() {
        return plugin;
    }

    FileConfiguration fileConfiguration = getConfig();

    public Commands commandsClass = new Commands(this);


    @Override
    public void onEnable() {



        plugin = getPlugin(HardcoreLite.class);
        PluginManager pm = getServer().getPluginManager();


        //Register command
        this.getCommand("hcl").setExecutor(new Commands(this));

        //Register listener
        pm.registerEvents(new EventListener(), this);
    }

    @Override
    public void onDisable() {

    }

    public class EventListener implements Listener {

        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent event) {

            Player player = event.getPlayer();
            String uid = String.valueOf(player.getUniqueId());

            if (!fileConfiguration.contains(uid)) {
                fileConfiguration.createSection("players." + uid + ".lives");
                fileConfiguration.set("players." + uid + ".lives", 5);

                fileConfiguration.createSection("players." + uid + ".name");

            } else {
                getLogger().info(fileConfiguration.getString("players." + uid + ".lives"));

                if (!fileConfiguration.contains(player.getName())) {
                    fileConfiguration.createSection("players." + uid + ".name");
                }
            }



            fileConfiguration.set("players." + uid + ".name", player.getName());

            saveConfig();

            int lives = fileConfiguration.getInt("players." + uid + ".lives");


            setNameColor(player, lives);
        }

        @EventHandler
        public void onPlayerDeath(PlayerDeathEvent event) {

            Player player = event.getEntity();

            removeLife(player);

        }

        @EventHandler
        public void onEntityResurrectEvent(EntityResurrectEvent event) {

            if (!event.isCancelled() && event.getEntity() instanceof Player) {

                Player player = (Player) event.getEntity();

                removeLife(player);
            }
        }

        @EventHandler
        public void onReloadEvent(ReloadEvent event) {

            Player player = event.getPlayer();
            int lives = event.getLives();

            setNameColor(player, lives);
        }

        @EventHandler
        public void onAdvancementEvent(PlayerAdvancementDoneEvent event) {

            Player player = event.getPlayer();
            String advancement = event.getAdvancement().getKey().getKey();
            String uid = String.valueOf(player.getUniqueId());
            int lives = fileConfiguration.getInt("players." + uid + ".lives") + 1;

            List<String> awardAdvancements = Arrays.asList("story/cure_zombie_villager", "nether/fast_travel", "nether/uneasy_alliance", "nether/create_full_beacon",
                    "nether/all_effects", "nether/all_potions", "end/kill_dragon", "adventure/caves_and_cliffs", "adventure/kill_all_mobs", "adventure/arbalistic",
                    "adventure/adventuring_time", "husbandry/bred_all_animals", "husbandry/complete_catalogue", "husbandry/balanced_diet", "husbandry/obtain_netherite_hoe");

            if (awardAdvancements.contains(advancement)) {
                fileConfiguration.set("players." + uid + ".lives", lives);

                saveConfig();
                setNameColor(player, lives);

                String lifeWord = "life.";
                if (lives != 1) {
                    lifeWord = "lives.";
                }

                getServer().broadcastMessage(ChatColor.RED + player.getName() + ChatColor.WHITE + " has earned a life! They now have " + ChatColor.RED + lives + ChatColor.WHITE + " " + lifeWord);
            }
        }

        private void removeLife(Player player) {
            String uid = String.valueOf(player.getUniqueId());

            int lives = fileConfiguration.getInt("players." + uid + ".lives") - 1;
            fileConfiguration.set("players." + uid + ".lives", lives);

            //If lives are set to a number less than zero, bump the player back up to 0 lives.
            if (plugin.fileConfiguration.getInt("players." + uid + ".lives") < 0) {
                plugin.fileConfiguration.set("players." + uid + ".lives", 0);
            }

            saveConfig();

            setNameColor(player, lives);

            String lifeWord = "life.";
            if (lives != 1) {
                lifeWord = "lives.";
            }
            getServer().broadcastMessage(ChatColor.RED + player.getName() + ChatColor.WHITE + " has lost a life! They now have " + ChatColor.RED + lives + ChatColor.WHITE + " " + lifeWord);
        }

        public void setNameColor(Player player, int lives) {
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
                    player.setPlayerListName(player.getDisplayName());
                } else if (lives <= 0) {
                    player.setDisplayName(ChatColor.GRAY + player.getName());
                    player.setPlayerListName(player.getDisplayName());
                }
                    break;
            }
        }
    }


}
