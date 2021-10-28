package me.thomas2404.hardcoreLite;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class HardcoreLite extends JavaPlugin {

    public static HardcoreLite plugin;


    FileConfiguration fileConfiguration = getConfig();

    LifeChanges lifeChanges = new LifeChanges(this);
    EventHandler eventHandler = new EventHandler(this);
    ConfigGetter configGetter = new ConfigGetter(this);
    SetNameColor setNameColor = new SetNameColor();
    LifeWord lifeWord = new LifeWord();

    @Override
    public void onEnable() {

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
