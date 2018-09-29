package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commands.CmdHandler;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.playerUUID.PlayerUUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

import static com.spaceman.tport.fileHander.GettingFiles.getFiles;

public class PLTP extends CmdHandler {

    @Override
    public void run(String[] args, Player player) {

        // tport PLTP [on:off]
        // tport PLTP whitelist list
        // tport PLTP whitelist [add:remove] <playername>

        if (args.length == 1) {
            player.sendMessage("§cUse: §4/tport PLTP <on:off:whitelist>");
            return;
        }

        Files tportData = getFiles("TPortData");
        String playerUUID = player.getUniqueId().toString();

        if (args[1].equalsIgnoreCase("on")) {

            if (args.length != 2) {
                player.sendMessage("§cUse: §4/tport PLTP [on:off]");
                return;
            }

            if (tportData.getConfig().getString("tport." + playerUUID + ".tp.statement").equals("on")) {
                player.sendMessage("§cThis is already set PLTP to on");
                return;
            }

            player.sendMessage("§3Successfully set to on");
            tportData.getConfig().set("tport." + playerUUID + ".tp.statement", "on");
            tportData.saveConfig();


        } else if (args[1].equalsIgnoreCase("off")) {

            if (args.length != 2) {
                player.sendMessage("§cUse: §4/tport PLTP [on:off]");
                return;
            }

            if (tportData.getConfig().getString("tport." + playerUUID + ".tp.statement").equals("off")) {
                player.sendMessage("§cThis is already set PLTP to off");
                return;
            }

            player.sendMessage("§3Successfully set to off");
            tportData.getConfig().set("tport." + playerUUID + ".tp.statement", "off");
            tportData.saveConfig();

        } else if (args[1].equalsIgnoreCase("whitelist")) {

            if (args.length == 3) {

                if (args[2].equalsIgnoreCase("list")) {
                    ArrayList<String> list = (ArrayList<String>) tportData.getConfig()
                            .getStringList("tport." + playerUUID + ".tp.players");
                    int i = 0;
                    player.sendMessage("§3Players in your whitelist:");
                    for (String ss : list) {
                        if (i == 0) {
                            player.sendMessage("§9" + PlayerUUID.getPlayerName(ss));
                            i++;
                        } else {
                            player.sendMessage("§3" + PlayerUUID.getPlayerName(ss));
                            i = 0;
                        }

                    }
                    return;
                }

            }

            if (args.length != 4) {
                player.sendMessage("§cUse: §4/tport PLTP whitelist [add:remove] <playername> §cor §4/tport PLTP whitelist list");
                return;
            }

            ArrayList<String> list = (ArrayList<String>) tportData.getConfig()
                    .getStringList("tport." + playerUUID + ".tp.players");
            if (args[2].equalsIgnoreCase("add")) {
                if (Bukkit.getPlayerExact(args[3]) == null) {
                    player.sendMessage("§cThis player must be online");
                    return;
                }

                String addPlayerName = args[3];
                String addPlayerUUID = PlayerUUID.getPlayerUUID(addPlayerName);

                if (addPlayerUUID == null) {

                    ArrayList<String> globalNames = PlayerUUID.getGlobalPlayerUUID(addPlayerName);

                    if (globalNames.size() == 1) {
                        addPlayerUUID = globalNames.get(0);
                    } else if (globalNames.size() == 0) {
                        player.sendMessage(ChatColor.RED + "Could not find any players named " + ChatColor.DARK_RED + addPlayerName);
                        return;
                    } else {
                        player.sendMessage(ChatColor.RED + "There are more players found with the name " + ChatColor.DARK_RED + addPlayerName + ChatColor.RED
                                + ", please type the correct name with correct capitals");
                        return;
                    }
                }

                if (addPlayerUUID.equals(playerUUID)) {
                    player.sendMessage("§cYou don't have to put yourself in your whitelist");
                    return;
                }

                if (list.contains(addPlayerUUID)) {
                    player.sendMessage("§cThis player is already in you list");
                    return;
                }

                list.add(addPlayerUUID);
                tportData.getConfig().set("tport." + playerUUID + ".tp.players", list);
                tportData.saveConfig();
                player.sendMessage("§3Successfully added");

                Player addPlayer = Bukkit.getPlayer(UUID.fromString(addPlayerUUID));
                if (addPlayer != null) {
                    addPlayer.sendMessage("§3You are new in the main whitelist of §9" + player.getName());
                }

            } else if (args[2].equalsIgnoreCase("remove")) {

                String removePlayerName = args[3];
                String removePlayerUUID = PlayerUUID.getPlayerUUID(removePlayerName);

                if (removePlayerUUID == null) {

                    ArrayList<String> globalNames = PlayerUUID.getGlobalPlayerUUID(removePlayerName);

                    if (globalNames.size() == 1) {
                        removePlayerUUID = globalNames.get(0);
                    } else if (globalNames.size() == 0) {
                        player.sendMessage(ChatColor.RED + "Could not find any players named " + ChatColor.DARK_RED + removePlayerName);
                        return;
                    } else {
                        player.sendMessage(ChatColor.RED + "There are more players found with the name " + ChatColor.DARK_RED + removePlayerName + ChatColor.RED
                                + ", please type the correct name with correct capitals");
                        return;
                    }

                }

                if (!list.contains(removePlayerUUID)) {
                    player.sendMessage("§cThis player is not in your list");
                    return;
                }

                list.remove(removePlayerUUID);
                tportData.getConfig().set("tport." + playerUUID + ".tp.players", list);
                tportData.saveConfig();
                player.sendMessage("§3Successfully removed");

                Player removePlayer = Bukkit.getPlayer(UUID.fromString(removePlayerUUID));
                if (removePlayer != null) {
                    removePlayer.sendMessage("§3You are removed in the main whitelist of §9" + player.getName());
                }
            } else {
                player.sendMessage("§cUse: §4/tport PLTP whitelist [add:remove] <playername> §cor §4/tport PLTP whitelist list");
            }
        } else {
            player.sendMessage("§cUse: §4/tport PLTP <on:off:whitelist>");
        }
    }
}
