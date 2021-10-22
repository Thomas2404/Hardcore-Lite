package me.thomas2404.hardcoreLite;

import org.bukkit.ChatColor;
import org.bukkit.EntityEffect;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class HardcoreLite extends JavaPlugin {

    public static final String CHAT_PREFIX = ChatColor.RED + "[Hardcore Lite] ";

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

        private void removeLife(Player player) {
            String uid = String.valueOf(player.getUniqueId());

            int lives = fileConfiguration.getInt("players." + uid + ".lives") - 1;
            fileConfiguration.set("players." + uid + ".lives", lives);
            saveConfig();

            setNameColor(player, lives);

            String lifeWord = "lives.";
            if (lives == 1) {
                lifeWord = "life.";
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
