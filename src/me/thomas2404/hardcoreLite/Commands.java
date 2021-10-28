package me.thomas2404.hardcoreLite;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;

public class Commands implements CommandExecutor {

    private HardcoreLite plugin;
    public Commands(HardcoreLite plugin) {
        this.plugin = plugin;
    }

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
                        //Set up the life word variable and send a message to the sender with how many lives they have.
                        int lives = plugin.configGetter.currentLives(player);
                        String lifeWord = plugin.lifeWord.getWord(lives);
                        sender.sendMessage(ChatColor.WHITE + "You currently have " + ChatColor.RED + lives + ChatColor.WHITE + " " + lifeWord + " remaining");
                        break;
                    case "about":
                        break;
                    case "colors":
                        //Send the sender a message about what each color means.
                        sender.sendMessage(ChatColor.WHITE + "Dark green = " + ChatColor.RED + "5" + ChatColor.WHITE + " or more lives, light green = "
                                + ChatColor.RED + "4" + ChatColor.WHITE + " lives, yellow = " + ChatColor.RED + "3" + ChatColor.WHITE + " lives, light red = "
                                + ChatColor.RED + "2" + ChatColor.WHITE + " lives, dark red = " + ChatColor.RED + "1" + ChatColor.WHITE + " life, and grey = 0 lives remaining.");
                        break;
                    case "list":
                        int i = 1;
                        //For every UUID in the config.
                        for (String key : plugin.fileConfiguration.getConfigurationSection("players").getKeys(false)) {
                            //Set up the lives and life word variables.
                            lives = plugin.fileConfiguration.getInt("players." + key + ".lives");
                            lifeWord = plugin.lifeWord.getWord(lives);
                            //Send a message to the sender about every player's lives.
                            sender.sendMessage(ChatColor.WHITE + String.valueOf(i) + ". " + ChatColor.RED + plugin.fileConfiguration.getString("players." + key + ".name") + ChatColor.WHITE + " currently has " + ChatColor.RED + plugin.fileConfiguration.getInt("players." + key + ".lives") + ChatColor.WHITE + " " + lifeWord + " remaining.");
                            i ++;
                        }
                        break;
                    case "earnlives":
                            //Sends a message to the player about how you can earn more lives.
                            sender.sendMessage(ChatColor.WHITE + "You can earn more lives by completing certain " + ChatColor.RED + "advancements" + ChatColor.WHITE + ".");
                            sender.sendMessage(ChatColor.WHITE + "There is the opportunity to earn " + ChatColor.RED + "15" + ChatColor.WHITE + " more lives, giving each player " +
                                    "the ability to reach a total of " + ChatColor.RED + "20" + ChatColor.WHITE + " lives on their own.");
                        break;
                    case "add":
                        //If the sender is OPed, and there's 3 words in the command.
                        if (sender.isOp()) {
                            if (args.length == 3) {
                                //Try to get an int from the command, catch an error.
                                int addedLives;
                                try {
                                    addedLives = Integer.parseInt(args[2]);;
                                }
                                catch (NumberFormatException e)
                                {
                                    addedLives = 0;
                                }
                                //If you're not adding 0 or -0 lives, and the player is online.
                                if (addedLives != 0 && addedLives != -0) {
                                    if (String.valueOf(Bukkit.getOnlinePlayers()).contains(args[1])) {
                                        //Set up the receiver variable, and current lives.
                                        Player receiver = Bukkit.getPlayer(args[1]);
                                        int currentLives = plugin.configGetter.currentLives(receiver);
                                        //Set the receivers lives to their current lives + the lives that the giver gave to them.
                                        plugin.configGetter.setLives(receiver, currentLives + addedLives);
                                        //If lives are set to a number less than zero, bump the player back up to 0 lives.
                                        if (plugin.configGetter.currentLives(receiver) < 0) {
                                            plugin.configGetter.setLives(receiver, 0);
                                        }
                                        //Set up the lives variable after the lives have been changed, and set up the life words to use.
                                        int totalLives = plugin.configGetter.currentLives(receiver);
                                        String totalLifeWord = plugin.lifeWord.getWord(totalLives);
                                        lifeWord = plugin.lifeWord.getWord(addedLives);

                                        sender.sendMessage(ChatColor.WHITE + "You have given " + ChatColor.RED + receiver.getName() + " "  + addedLives + " " + ChatColor.WHITE + lifeWord + ".");
                                        receiver.sendMessage(ChatColor.WHITE + "You have been given " + ChatColor.RED + addedLives + " " + ChatColor.WHITE + lifeWord + ".");
                                        receiver.sendMessage(ChatColor.WHITE + "You now have " + ChatColor.RED + totalLives + ChatColor.WHITE + " lives.");
                                        //Send a message to the server about the life transaction.
                                        plugin.getServer().broadcastMessage(ChatColor.RED + receiver.getName() + ChatColor.WHITE + " has been given " + ChatColor.RED + addedLives + " " + ChatColor.WHITE + lifeWord + ". They now have " + ChatColor.RED + totalLives + ChatColor.WHITE + " " + totalLifeWord + ".");
                                        //Reload the name colors
                                        plugin.setNameColor.changeNameColor(receiver, totalLives);
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
                    //Lots of nested if's on the give command because there's a few different user error messages.
                    case "give":
                            //If the player that is being sent lives is online.
                            if (args.length == 3 && (String.valueOf(Bukkit.getOnlinePlayers()).contains(args[1]))) {
                                //Set up the player variables.
                                Player receiver = Bukkit.getPlayer(args[1]);
                                Player lifeGiver = ((Player) sender).getPlayer();
                                //If the sender isn't trying to give themselves lives.
                                if (receiver.getName() != lifeGiver.getName()) {
                                    //Try to get an int from the command, catch an error.
                                    int givenLives;
                                    try {
                                        givenLives = Integer.parseInt(args[2]);;
                                    }
                                    catch (NumberFormatException e)
                                    {
                                        givenLives = 0;
                                    }
                                    //Set up the starting and end life values for both players.
                                    int startingGiverLives = plugin.configGetter.currentLives(lifeGiver);
                                    int startingReceiverLives = plugin.configGetter.currentLives(receiver);
                                    int endGiverLives = startingGiverLives - givenLives;
                                    int endReceiverLives = startingReceiverLives + givenLives;
                                    //If the person running the command has enough lives to give.
                                    if (startingGiverLives >= givenLives) {
                                        if (givenLives > 0) {
                                            //Set the lives for the giver and receiver to the correct numbers.
                                            plugin.configGetter.setLives(lifeGiver, endGiverLives);
                                            plugin.configGetter.setLives(receiver, endReceiverLives);
                                            //Set up which word to use when sending messages to the players and server.
                                            String endLifeWord = plugin.lifeWord.getWord(endReceiverLives);
                                            lifeWord = plugin.lifeWord.getWord(givenLives);
                                            //Send messages to the players and server alerting them about the transaction.
                                            receiver.sendMessage(ChatColor.WHITE + "You were given " + ChatColor.RED + givenLives + " " + ChatColor.WHITE + lifeWord + " by " + ChatColor.RED + lifeGiver.getName() + ChatColor.WHITE + ".");
                                            lifeGiver.sendMessage(ChatColor.WHITE + "You gave " + ChatColor.RED + receiver.getName() + " " + givenLives + ChatColor.WHITE + " " + lifeWord + ".");
                                            plugin.getServer().broadcastMessage(ChatColor.RED + lifeGiver.getName() + ChatColor.WHITE + " has given " + ChatColor.RED + receiver.getName() + " " + ChatColor.RED + givenLives + " " + ChatColor.WHITE + lifeWord + ". " + ChatColor.RED + receiver.getName() + ChatColor.RED + " now has " + ChatColor.RED + endReceiverLives + ChatColor.WHITE + " " + endLifeWord + ".");
                                            //Update name colors.
                                            plugin.setNameColor.changeNameColor(lifeGiver, endGiverLives);
                                            plugin.setNameColor.changeNameColor(receiver, endReceiverLives);
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
