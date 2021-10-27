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

    ConfigGetter configGetter = new ConfigGetter();
    SetNameColor setNameColor = new SetNameColor();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Console can not run this command.");
            return false;
        } else {

            String uid = String.valueOf(((Player) sender).getUniqueId());
            Player player = (Player) sender;

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
                sender.sendMessage(ChatColor.WHITE + " - " + ChatColor.RED + "earnlives " + ChatColor.WHITE + "Learn how you can earn more lives.");
                sender.sendMessage(ChatColor.YELLOW + " ------------------------------------ ");

            } else {
                String command = args[0].toLowerCase(Locale.ROOT);
                switch (command) {
                    case "lives":

                        int lives = configGetter.currentLives(player);

                        String lifeWord = "life";
                        if (lives != 1) {
                            lifeWord = "lives";
                        }

                        sender.sendMessage(ChatColor.WHITE + "You currently have " + ChatColor.RED + lives + ChatColor.WHITE + " " + lifeWord + " remaining");
                        break;
                    case "about":
                        break;
                    case "colors":
                        sender.sendMessage(ChatColor.WHITE + "Dark green = " + ChatColor.RED + "5" + ChatColor.WHITE + " or more lives, light green = "
                                + ChatColor.RED + "4" + ChatColor.WHITE + " lives, yellow = " + ChatColor.RED + "3" + ChatColor.WHITE + " lives, light red = "
                                + ChatColor.RED + "2" + ChatColor.WHITE + " lives, dark red = " + ChatColor.RED + "1" + ChatColor.WHITE + " life, and grey = 0 lives remaining.");
                        break;
                    case "list":
                        int i = 1;
                        for (String key : plugin.fileConfiguration.getConfigurationSection("players").getKeys(false)) {

                            lives = plugin.fileConfiguration.getInt("players." + key + ".lives");

                            lifeWord = "life";
                            if (lives != 1) {
                                lifeWord = "lives";
                            }
                            sender.sendMessage(ChatColor.WHITE + String.valueOf(i) + ". " + ChatColor.RED + plugin.fileConfiguration.getString("players." + key + ".name") + ChatColor.WHITE + " currently has " + ChatColor.RED + plugin.fileConfiguration.getInt("players." + key + ".lives") + ChatColor.WHITE + " " + lifeWord + " remaining.");
                            i ++;
                        }
                        break;
                    case "earnlives":
                            sender.sendMessage(ChatColor.WHITE + "You can earn more lives by completing certain " + ChatColor.RED + "advancements" + ChatColor.WHITE + ".");
                            sender.sendMessage(ChatColor.WHITE + "There is the opportunity to earn " + ChatColor.RED + "15" + ChatColor.WHITE + " more lives, giving each player " +
                                    "the ability to reach a total of " + ChatColor.RED + "20" + ChatColor.WHITE + " lives on their own.");
                        break;
                    case "add":
                        if (sender.isOp()) {
                            if (args.length == 3) {

                                String receiverName = args[1];

                                int addedLives;
                                try {
                                    addedLives = Integer.parseInt(args[2]);;
                                }
                                catch (NumberFormatException e)
                                {
                                    addedLives = 0;
                                }

                                if (addedLives != 0 && addedLives != -0) {
                                    if (String.valueOf(Bukkit.getOnlinePlayers()).contains(receiverName)) {

                                        Player receiver = Bukkit.getPlayer(receiverName);
                                        int currentLives = configGetter.currentLives(receiver);
                                        //Set the receivers lives to their current lives + the lives that the giver gave to them.
                                        configGetter.setLives(receiver, currentLives + addedLives);
                                        //If lives are set to a number less than zero, bump the player back up to 0 lives.
                                        if (configGetter.currentLives(receiver) < 0) {
                                            configGetter.setLives(receiver, 0);
                                        }

                                        int totalLives = configGetter.currentLives(receiver);

                                        lifeWord = "life";
                                        if (addedLives != 1) {
                                            lifeWord = "lives";
                                        }

                                        String totalLifeWord = "life";
                                        if (totalLives != 1) {
                                            totalLifeWord = "lives";
                                        }

                                        sender.sendMessage(ChatColor.WHITE + "You have given " + ChatColor.RED + receiverName + " "  + addedLives + " " + ChatColor.WHITE + lifeWord + ".");
                                        receiver.sendMessage(ChatColor.WHITE + "You have been given " + ChatColor.RED + addedLives + " " + ChatColor.WHITE + lifeWord + ".");
                                        receiver.sendMessage(ChatColor.WHITE + "You now have " + ChatColor.RED + totalLives + ChatColor.WHITE + " lives.");
                                        //Send a message to the server about the life transaction.
                                        plugin.getServer().broadcastMessage(ChatColor.RED + receiverName + ChatColor.WHITE + " has been given " + ChatColor.RED + addedLives + " " + ChatColor.WHITE + lifeWord + ". They now have " + ChatColor.RED + totalLives + ChatColor.WHITE + " " + totalLifeWord + ".");
                                        //Reload the name colors
                                        setNameColor.changeNameColor(receiver, totalLives);
                                    }
                                } else {
                                    sender.sendMessage(ChatColor.RED + "Invalid usage. Please make sure you are not adding 0 lives, and that you have correctly typed in a number!");
                                }
                            } else {
                                sender.sendMessage(ChatColor.RED + "Invalid usage! Please do /hcl add [name] [number].");
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                        }
                        break;
                    case "give":
                            if (args.length == 3 && (String.valueOf(Bukkit.getOnlinePlayers()).contains(args[1]))) {

                                String receiverName = args[1];

                                Player receiver = Bukkit.getPlayer(receiverName);
                                Player lifeGiver = ((Player) sender).getPlayer();

                                if (receiver.getName() != lifeGiver.getName()) {

                                    int givenLives;
                                    try {
                                        givenLives = Integer.parseInt(args[2]);;
                                    }
                                    catch (NumberFormatException e)
                                    {
                                        givenLives = 0;
                                    }

                                    String receiverUID = String.valueOf(receiver.getUniqueId());
                                    String giverUID = String.valueOf(lifeGiver.getUniqueId());

                                    //If the person running the command has enough lives to give.
                                    if (plugin.fileConfiguration.getInt("players." + giverUID + ".lives") >= givenLives) {
                                        if (givenLives > 0) {

                                            int startingGiverLives = plugin.fileConfiguration.getInt("players." + giverUID + ".lives");
                                            int startingReceiverLives = plugin.fileConfiguration.getInt("players." + receiverUID + ".lives");

                                            int endGiverLives = startingGiverLives - givenLives;
                                            int endReceiverLives = startingReceiverLives + givenLives;

                                            plugin.fileConfiguration.set("players." + giverUID + ".lives", endGiverLives);
                                            plugin.fileConfiguration.set("players." + receiverUID + ".lives", endReceiverLives);

                                            plugin.saveConfig();

                                            lifeWord = "life";
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

                                            setNameColor.changeNameColor(lifeGiver, endGiverLives);
                                            setNameColor.changeNameColor(receiver, endReceiverLives);

                                        } else {
                                            sender.sendMessage(ChatColor.RED + "Invalid usage. Please make sure you are giving a player at least one life.");
                                        }
                                    } else {
                                        sender.sendMessage(ChatColor.RED + "You don't have enough lives to give!");
                                    }
                                } else {
                                    sender.sendMessage(ChatColor.RED + "You can't give yourself lives!");
                                }
                            } else {
                                sender.sendMessage(ChatColor.RED + "Invalid usage, or the player is not online. Please do /hcl give [name] [number]");
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
