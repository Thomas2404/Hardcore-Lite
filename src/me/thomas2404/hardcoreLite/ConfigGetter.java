package me.thomas2404.hardcoreLite;

import org.bukkit.entity.Player;

public class ConfigGetter {

    private HardcoreLite plugin;
    public ConfigGetter(HardcoreLite plugin) {
        this.plugin = plugin;
    }

    public int currentLives(Player player) {
        return plugin.fileConfiguration.getInt("players." + player.getUniqueId() + ".lives");
    }

    public String livesPath(Player player) {
        return ("players." + player.getUniqueId().toString() + ".lives");
    }

    public String namePath(Player player) {
        return ("players." + player.getUniqueId() + ".name");
    }

    public void setLives(Player player, int lives) {
        plugin.fileConfiguration.set(livesPath(player), lives);
        plugin.saveConfig();
    }

    public void setName(Player player) {
        plugin.fileConfiguration.set(namePath(player), player.getName());
        plugin.saveConfig();
    }

    public void createSections(Player player) {
        plugin.fileConfiguration.createSection(livesPath(player));
        plugin.fileConfiguration.createSection(namePath(player));
        setLives(player, 5);
        plugin.saveConfig();
    }


}
