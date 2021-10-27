package me.thomas2404.hardcoreLite;

import net.minecraft.server.network.PlayerConnection;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
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

    @Override
    public void onEnable() {

        //Organize the code a bit better after figuring out the setup. Put methods in different classes? At least make methods for things like the commands.

        //Final thing is add comments explaining the code.


        plugin = getPlugin(HardcoreLite.class);
        PluginManager pm = getServer().getPluginManager();


        //Register command
        this.getCommand("hcl").setExecutor(new Commands(this));

        //Register listener
        pm.registerEvents(new EventHandler(this), this);

        //If the ban players section isn't in the config.yml file, make it and set it to no.
        if (!fileConfiguration.contains("BanPlayersOn0Lives")) {
            fileConfiguration.createSection("BanPlayersOn0Lives");

            fileConfiguration.set("BanPlayersOn0Lives", "no");
        }
    }

    @Override
    public void onDisable() {

    }


}
