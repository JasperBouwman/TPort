package com.spaceman.tport.commands.tport.log;

import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import com.spaceman.tport.logbook.Logbook;
import com.spaceman.tport.playerUUID.PlayerUUID;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IsLogged extends SubCommand {

    public IsLogged() {
        EmptyCommand emptyCommand = new EmptyCommand();
        emptyCommand.setTabRunnable(
                (args, player) -> {
                    ArrayList<String> list = new ArrayList<>();
                    Files tportData = GettingFiles.getFile("TPortData");

                    String argOneUUID = PlayerUUID.getPlayerUUID(args[2]);
                    if (argOneUUID == null) {
                        ArrayList<String> globalNames = PlayerUUID.getGlobalPlayerUUID(args[2]);
                        if (globalNames.size() == 1) {
                            argOneUUID = globalNames.get(0);
                        }
                    }

                    if (tportData.getConfig().contains("tport." + argOneUUID + ".items")) {
                        for (String s : tportData.getConfig().getConfigurationSection("tport." + argOneUUID + ".items").getKeys(false)) {
                            String name = tportData.getConfig().getString("tport." + argOneUUID + ".items." + s + ".name");

                            if (tportData.getConfig().getString("tport." + argOneUUID + ".items." + s + ".private.statement").equals("true")) {
                                ArrayList<String> listTmp = (ArrayList<String>) tportData.getConfig().getStringList("tport." + argOneUUID + ".items." + s + ".private.players");
                                if (listTmp.contains(player.getUniqueId().toString())) {
                                    list.add(name);
                                }
                            } else {
                                list.add(name);
                            }
                        }
                    }

                    return list;
                }
        );
        addAction(emptyCommand);
    }

    @Override
    public List<String> tabList(Player player, String[] args) {
        ArrayList<String> list = new ArrayList<>();
        Files tportData = GettingFiles.getFile("TPortData");
        for (String s : tportData.getConfig().getConfigurationSection("tport").getKeys(false)) {
            list.add(PlayerUUID.getPlayerName(s));
        }
        return list;
    }

    @Override
    public void run(String[] args, Player player) {
        //tport log isLogged <player name> <TPort name>

        if (args.length == 4) {

            Files tportData = GettingFiles.getFile("TPortData");

            String newPlayerName = args[2];
            String newPlayerUUID = PlayerUUID.getPlayerUUID(newPlayerName);

            if (newPlayerUUID == null) {

                ArrayList<String> globalNames = PlayerUUID.getGlobalPlayerUUID(newPlayerName);

                if (globalNames.size() == 1) {
                    newPlayerUUID = globalNames.get(0);
                } else if (globalNames.size() == 0) {
                    player.sendMessage(ChatColor.RED + "Could not find any players named " + ChatColor.DARK_RED + newPlayerName);
                    return;
                } else {
                    player.sendMessage(ChatColor.RED + "There are more players found with the name " + ChatColor.DARK_RED + newPlayerName + ChatColor.RED
                            + ", please type the correct name with correct capitals");
                    return;
                }
            }

            if (!tportData.getConfig().contains("tport." + newPlayerUUID)) {
                player.sendMessage("§cThis isn't a player that has ever been online");
                return;
            }

            if (tportData.getConfig().contains("tport." + newPlayerUUID + ".items")) {
                for (String i : tportData.getConfig().getConfigurationSection("tport." + newPlayerUUID + ".items").getKeys(false)) {
                    if (tportData.getConfig().contains("tport." + newPlayerUUID + ".items." + i + ".item")) {
                        if (args[3].equals(tportData.getConfig().getString("tport." + newPlayerUUID + ".items." + i + ".name"))) {

                            if (Logbook.isLogged(UUID.fromString(newPlayerUUID), args[3], player.getUniqueId())) {
                                player.sendMessage("You are being logged in the TPort " + args[3] + " of " + newPlayerName);//todo colors
                            } else {
                                player.sendMessage("You are not being logged in the TPort " + args[3] + " of " + newPlayerName);//todo colors
                            }
                            return;
                        }
                    }
                }
            }
            player.sendMessage("§cThe TPort §4" + args[3] + "§c doesn't exist");


        } else {
            player.sendMessage(ChatColor.RED + "Use: " + ChatColor.DARK_RED + "/tport log isLogged <player name> <TPort name>");
        }

    }
}
