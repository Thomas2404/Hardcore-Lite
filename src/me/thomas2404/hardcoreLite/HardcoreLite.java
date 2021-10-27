package me.thomas2404.hardcoreLite;

import net.minecraft.server.network.PlayerConnection;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
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

        //Organize the code a bit better after figuring out the setup. Put methods in different classes? At least make methods for things like the commands.

        //Final thing is add comments explaining the code.


        plugin = getPlugin(HardcoreLite.class);
        PluginManager pm = getServer().getPluginManager();


        //Register command
        this.getCommand("hcl").setExecutor(new Commands(this));

        //Register listener
        pm.registerEvents(new EventListener(), this);

        //If the ban players section isn't in the config.yml file, make it and set it to no.
        if (!fileConfiguration.contains("BanPlayersOn0Lives")) {
            fileConfiguration.createSection("BanPlayersOn0Lives");

            fileConfiguration.set("BanPlayersOn0Lives", "no");
        }
    }

    @Override
    public void onDisable() {

    }

    public class EventListener implements Listener {

        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent event) {

            Player player = event.getPlayer();
            String uid = String.valueOf(player.getUniqueId());

            if (!fileConfiguration.contains("players." + uid)) {

                getLogger().info("Has never joined before");
                fileConfiguration.createSection("players." + uid + ".lives");
                fileConfiguration.set("players." + uid + ".lives", 5);
                fileConfiguration.createSection("players." + uid + ".name");

            } else if (!fileConfiguration.contains(player.getName())) {
                fileConfiguration.createSection("players." + uid + ".name");
            }

            fileConfiguration.set("players." + uid + ".name", player.getName());
            saveConfig();
            int lives = fileConfiguration.getInt("players." + uid + ".lives");

            if (banPlayers() && lives == 0) {
                player.kickPlayer(ChatColor.WHITE + "You are on " + ChatColor.RED + "0" + ChatColor.WHITE + " lives.");
            }

            setNameColor(player, lives);
        }

        @EventHandler
        public void onPlayerDeath(PlayerDeathEvent event) {

            Player killedPlayer = event.getEntity();

            //If the entity that killed the player is a player
            if (killedPlayer.getKiller() != null) {
                if (killedPlayer.getKiller().getType() == EntityType.PLAYER) {

                    Player killer = killedPlayer.getKiller();

                    //Add a life to the killed player and remove a life from the killer.
                    addLife(killedPlayer);
                    removeLife(killer);

                } else {
                    removeLife(killedPlayer);
                }
            } else {
                removeLife(killedPlayer);
            }
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

            List<String> awardAdvancements = Arrays.asList("story/cure_zombie_villager", "nether/fast_travel", "nether/uneasy_alliance", "nether/create_full_beacon",
                    "nether/all_effects", "nether/all_potions", "end/kill_dragon", "adventure/caves_and_cliffs", "adventure/kill_all_mobs", "adventure/arbalistic",
                    "adventure/adventuring_time", "husbandry/bred_all_animals", "husbandry/complete_catalogue", "husbandry/balanced_diet", "husbandry/obtain_netherite_hoe");

            if (awardAdvancements.contains(advancement)) {
                addLife(player);
            }
        }

        private boolean hasLife(Player player) {

            String uid = player.getUniqueId().toString();
            int lives = fileConfiguration.getInt("players." + uid + ".lives");

            return lives > 0;
        }

        private void addLife(Player player) {

            String uid = String.valueOf(player.getUniqueId());
            int lives = plugin.fileConfiguration.getInt("players." + uid + ".lives") + 1;

            plugin.fileConfiguration.set("players." + uid + ".lives", lives);

            saveConfig();
            setNameColor(player, lives);

            //Set up which version of the word should be used.
            String lifeWord = "life.";
            if (lives != 1) {
                lifeWord = "lives.";
            }

            getServer().broadcastMessage(ChatColor.WHITE + player.getName() + " has gained a life! They now have " + ChatColor.RED + lives + ChatColor.WHITE + " " + lifeWord);
        }

        private void removeLife(Player player) {

            String uid = String.valueOf(player.getUniqueId());
            //If lives are set to a number less than zero, bump the player back up to 0 lives.
            if (plugin.fileConfiguration.getInt("players." + uid + ".lives") < 0) {
                plugin.fileConfiguration.set("players." + uid + ".lives", 0);
            }
            //If the player has a life, remove one and broadcast that they lost a life.
            if (hasLife(player)) {
                //Remove a life from the player
                int lives = fileConfiguration.getInt("players." + uid + ".lives") - 1;
                fileConfiguration.set("players." + uid + ".lives", lives);
                //Save the plugin.yml
                saveConfig();
                //Call the name color method.
                setNameColor(player, lives);

                //Ban player in they're on 0 lives and you're supposed to ban players.
                if (banPlayers() && fileConfiguration.getInt("players." + uid + ".lives") == 0) {
                    getServer().broadcastMessage(ChatColor.RED + player.getName() + ChatColor.WHITE + " is at " + ChatColor.RED + "0" + ChatColor.WHITE + " lives! They have now been " + ChatColor.RED + ChatColor.BOLD + " banned" + ChatColor.WHITE + "!");
                    player.kickPlayer(ChatColor.WHITE + "You are on " + ChatColor.RED + "0" + ChatColor.WHITE + " lives.");
                } else {

                    //Set up which version of the word should be used.
                    String lifeWord = "life.";
                    if (lives != 1) {
                        lifeWord = "lives.";
                    }
                    //Broadcast that the player lost a life.
                    getServer().broadcastMessage(ChatColor.RED + player.getName() + ChatColor.WHITE + " has lost a life! They now have " + ChatColor.RED + lives + ChatColor.WHITE + " " + lifeWord);

                }
            } else {
                getServer().broadcastMessage(ChatColor.RED + player.getName() + ChatColor.WHITE + " has lost a life! They have " + ChatColor.RED + "0" + ChatColor.WHITE + " lives.");
            }
        }

        public boolean banPlayers() {
            String banString = fileConfiguration.getString("BanPlayersOn0Lives");

            if (banString.equalsIgnoreCase("yes") || banString.equalsIgnoreCase("y")) {
                return true;
            } else {
                return false;
            }
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
                } else {
                    player.setDisplayName(ChatColor.GRAY + player.getName());
                }
                    player.setPlayerListName(player.getDisplayName());
                    break;
            }
        }
    }


}
