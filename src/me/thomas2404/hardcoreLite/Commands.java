package me.thomas2404.hardcoreLite;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;

public class Commands implements CommandExecutor {

    HardcoreLite plugin;
    public Commands(HardcoreLite pl) {
        plugin = pl;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Console can not run this command.");
            return false;
        } else {

            String uid = String.valueOf(((Player) sender).getUniqueId());

            if (args.length == 0) {
                sender.sendMessage(ChatColor.YELLOW + " ------------------------------------ ");
                sender.sendMessage(ChatColor.RED + "Welcome to Hardcore Lite!");
                sender.sendMessage(ChatColor.WHITE + "Available Commands");
                sender.sendMessage(ChatColor.WHITE + " - " + ChatColor.RED + "lives " + ChatColor.WHITE + "View your remaining lives.");
                sender.sendMessage(ChatColor.WHITE + " - " + ChatColor.RED + "about " + ChatColor.WHITE + "Get a summary of what Hardcore Lite is.");
                sender.sendMessage(ChatColor.WHITE + " - " + ChatColor.RED + "colors " + ChatColor.WHITE + "Learn about what the different name colors mean.");
                sender.sendMessage(ChatColor.WHITE + " - " + ChatColor.RED + "list " + ChatColor.WHITE + "See a list of all players remaining lives.");
                sender.sendMessage(ChatColor.WHITE + " - " + ChatColor.RED + "give " + ChatColor.WHITE + "Give a player one or more of your lives.");
                sender.sendMessage(ChatColor.WHITE + " - " + ChatColor.RED + "add " + ChatColor.WHITE + "Add one or more life to a players life count. Input a negative number to remove lives. Can only be used if you are oped.");
                sender.sendMessage(ChatColor.YELLOW + " ------------------------------------ ");


            } else {
                String command = args[0].toLowerCase(Locale.ROOT);
                switch (command) {
                    case "lives":
                        sender.sendMessage(ChatColor.WHITE + "You currently have " + ChatColor.RED + plugin.fileConfiguration.getInt("players." + uid + ".lives") + ChatColor.WHITE + " lives remaining");
                        break;
                    case "about":
                        break;
                    case "colors":
                        sender.sendMessage(ChatColor.RED + "Dark green = 5 or more lives, light green = 4 lives, yellow = 3 lives, light red = 2 lives, dark red = 1 life, and grey = 0 lives remaining.");
                        break;
                    case "list":
                        int i = 1;
                        for (String key : plugin.fileConfiguration.getConfigurationSection("players").getKeys(false)) {
                            sender.sendMessage(ChatColor.WHITE + String.valueOf(i) + ". " + ChatColor.RED + plugin.fileConfiguration.getString("players." + key + ".name") + ChatColor.WHITE + " currently has " + ChatColor.RED + plugin.fileConfiguration.getInt("players." + key + ".lives") + ChatColor.WHITE + " remaining.");
                            i ++;
                        }
                        break;
                    case "add":
                        if (sender.isOp()) {
                            if (args.length == 3) {

                                String receiverName = args[1];
                                int addedLives = Integer.parseInt(args[2]);

                                if (String.valueOf(Bukkit.getOnlinePlayers()).contains(receiverName)) {

                                    Player receiver = Bukkit.getPlayer(receiverName);
                                    String receiverUID = String.valueOf(receiver.getUniqueId());
                                    int currentLives = plugin.fileConfiguration.getInt("players." + receiverUID + ".lives");


                                    plugin.fileConfiguration.set("players." + receiverUID + ".lives", currentLives + addedLives);

                                    //If lives are set to a number less than zero, bump the player back up to 0 lives.
                                    if (plugin.fileConfiguration.getInt("players." + receiverUID + ".lives") < 0) {
                                        plugin.fileConfiguration.set("players." + receiverUID + ".lives", 0);
                                    }

                                    plugin.saveConfig();

                                    int totalLives = plugin.fileConfiguration.getInt("players." + receiverUID + ".lives");

                                    String lifeWord = "life";
                                    if (addedLives != 1) {
                                        lifeWord = "lives";
                                    }

                                    String totalLifeWord = "life";
                                    if (totalLives != 1) {
                                        lifeWord = "lives";
                                    }

                                    sender.sendMessage(ChatColor.WHITE + "You have given " + ChatColor.RED + receiverName + " "  + addedLives + " " + ChatColor.WHITE + lifeWord + ".");
                                    receiver.sendMessage(ChatColor.WHITE + "You have been given " + ChatColor.RED + addedLives + " " + ChatColor.WHITE + lifeWord + ".");
                                    receiver.sendMessage(ChatColor.WHITE + "You now have " + ChatColor.RED + totalLives + ChatColor.WHITE + " lives.");

                                    plugin.getServer().broadcastMessage(ChatColor.RED + receiverName + ChatColor.WHITE + " has been given " + ChatColor.RED + addedLives + " " + ChatColor.WHITE + lifeWord + ". They now have " + ChatColor.RED + totalLives + ChatColor.WHITE + " " + totalLifeWord + ".");

                                    ReloadEvent event = new ReloadEvent(((Player) sender).getPlayer(), currentLives + addedLives);
                                    Bukkit.getPluginManager().callEvent(event);
                                }
                            } else {
                                sender.sendMessage(ChatColor.RED + "Invalid usage! Please do /hcl add [name] [number].");
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                        }
                        break;
                    case "give":
                            if (args.length == 3) {

                                String receiverName = args[1];

                                Player receiver = Bukkit.getPlayer(receiverName);
                                Player lifeGiver = ((Player) sender).getPlayer();
                                int givenLives = Integer.parseInt(args[2]);

                                String receiverUID = String.valueOf(receiver.getUniqueId());
                                String giverUID = String.valueOf(lifeGiver.getUniqueId());


                                //If the receiving player isn't online, exit the command.
                                if (!String.valueOf(Bukkit.getOnlinePlayers()).contains(receiverName)) {
                                    sender.sendMessage(ChatColor.RED + "That player is not online! Did you misspell their name?");
                                    break;
                                }

                                //If the person running the command has enough lives to give.
                                if (plugin.fileConfiguration.getInt("players." + giverUID + ".lives") >= givenLives) {

                                    int startingGiverLives = plugin.fileConfiguration.getInt("players." + giverUID + ".lives");
                                    int startingReceiverLives = plugin.fileConfiguration.getInt("players." + receiverUID + ".lives");

                                    int endGiverLives = startingGiverLives - givenLives;
                                    int endReceiverLives = startingReceiverLives + givenLives;

                                    plugin.fileConfiguration.set("players." + giverUID + ".lives", endGiverLives);
                                    plugin.fileConfiguration.set("players." + receiverUID + ".lives", endReceiverLives);

                                    plugin.saveConfig();

                                    String lifeWord = "life";
                                    if (givenLives != 1) {
                                        lifeWord = "lives";
                                    }

                                    String endLifeWord = "life";
                                    if (endReceiverLives != 1) {
                                        endLifeWord = "lives";
                                    }

                                    receiver.sendMessage(ChatColor.WHITE + "You were given " + ChatColor.RED + givenLives + " " + ChatColor.WHITE + lifeWord + " by " + ChatColor.RED + lifeGiver.getName() + ChatColor.WHITE + ".");
                                    lifeGiver.sendMessage(ChatColor.WHITE + "You gave " + ChatColor.RED + receiver.getName() + " " + givenLives + ChatColor.WHITE + " " + lifeWord + ".");

                                    plugin.getServer().broadcastMessage(ChatColor.RED + lifeGiver.getName() + ChatColor.WHITE + " has given " + ChatColor.RED + receiverName + " " + ChatColor.RED + givenLives + " " + ChatColor.WHITE + lifeWord + ". " + ChatColor.RED + receiverName + ChatColor.RED + " now has " + ChatColor.RED + endReceiverLives + ChatColor.WHITE + " " + endLifeWord + ".");

                                    ReloadEvent giverEvent = new ReloadEvent(lifeGiver, endGiverLives);
                                    ReloadEvent receiverEvent = new ReloadEvent(receiver, endReceiverLives);

                                    Bukkit.getPluginManager().callEvent(giverEvent);
                                    Bukkit.getPluginManager().callEvent(receiverEvent);
                                }
                            } else {
                                sender.sendMessage(ChatColor.RED + "Invalid usage. Please do /hcl give [name] [number]");
                            }
                        break;
                    default:
                        sender.sendMessage(ChatColor.RED + "Invalid command! Please do /hcl to see a list of available commands.");
                }
            }
            return true;
        }
    }
}
