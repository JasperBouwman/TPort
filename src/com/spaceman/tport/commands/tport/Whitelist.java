package com.spaceman.tport.commands.tport;

import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.commands.CmdHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.UUID;

import static com.spaceman.tport.fileHander.GettingFiles.getFiles;

public class Whitelist extends CmdHandler {

    @Override
    public void run(String[] args, Player player) {
        // tport whitelist <name item> add <player>
        // tport whitelist <name item> remove <player>
        // tport whitelist <name item> list

        String playerUUID = player.getUniqueId().toString();

        Files tportData = getFiles("TPortData");

        if (args.length == 3) {

            if (!tportData.getConfig().contains("tport." + playerUUID + ".items")) {
                player.sendMessage("§cNo TPort found called §4" + args[1]);
                return;
            }

            for (String s : tportData.getConfig().getConfigurationSection("tport." + playerUUID + ".items")
                    .getKeys(false)) {
                ItemStack item = tportData.getConfig().getItemStack("tport." + playerUUID + ".items." + s + ".item");
                ItemMeta meta = item.getItemMeta();
                String name = meta.getDisplayName();

                if (args[1].equalsIgnoreCase(name)) {
                    if (args[2].equalsIgnoreCase("list")) {
                        ArrayList<String> list = (ArrayList<String>) tportData.getConfig()
                                .getStringList("tport." + playerUUID + ".items." + s + ".private.players");
                        boolean color = false;
                        player.sendMessage("§3Players in your whitelist:");
                        for (String ss : list) {
                            if (color) {
                                player.sendMessage("§9" + PlayerUUID.getPlayerName(ss));
                                color = false;
                            } else {
                                player.sendMessage("§3" + PlayerUUID.getPlayerName(ss));
                                color = true;
                            }

                        }
                        return;
                    }
                }
            }
        }
        if (args.length != 4) {

            if (args.length == 3) {
                if (args[2].equalsIgnoreCase("add")) {
                    player.sendMessage("§cUse: §4/tport whitelist <TPort> <add> <player>");
                    return;
                }
                if (args[2].equalsIgnoreCase("remove")) {
                    player.sendMessage("§cUse: §4/tport whitelist <TPort> <remove> <player>");
                    return;
                }
            }
            player.sendMessage("§cUse: §4/tport whitelist <TPort> <add:remove> <player>" +
                    "§c or§4 /tport whitelist <TPort> list");
            return;
        }
        if (!tportData.getConfig().contains("tport." + playerUUID + ".items")) {
            player.sendMessage("§cNo TPort found called §4" + args[1]);
            return;
        }
        boolean bool = true;
        for (String s : tportData.getConfig().getConfigurationSection("tport." + playerUUID + ".items").getKeys(false)) {

            ItemStack item = tportData.getConfig().getItemStack("tport." + playerUUID + ".items." + s + ".item");
            ItemMeta meta = item.getItemMeta();
            String name = meta.getDisplayName();

            if (args[1].equalsIgnoreCase(name)) {
                bool = false;
                ArrayList<String> list = (ArrayList<String>) tportData.getConfig().getStringList("tport." + playerUUID + ".items." + s + ".private.players");

                if (args[2].equalsIgnoreCase("add")) {

                    String newPlayerName = args[3];
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

                    if (newPlayerUUID.equals(playerUUID)) {
                        player.sendMessage("§cYou don't have to put yourself in your whitelist");
                        return;
                    }

                    if (list.contains(newPlayerUUID)) {
                        player.sendMessage("§cThis player is already in you list");
                        return;
                    }

                    list.add(newPlayerUUID);
                    tportData.getConfig().set("tport." + playerUUID + ".items." + s + ".private.players", list);
                    tportData.saveConfig();
                    player.sendMessage("§3Successfully added " + newPlayerName);

                    Player newPlayer = Bukkit.getPlayer(UUID.fromString(newPlayerUUID));
                    if (newPlayer != null) {
                        newPlayer.sendMessage("§3You are in the whitelist of §9" + player.getName() + " §3in the item §9" + name);
                    }
                } else if (args[2].equalsIgnoreCase("remove")) {

                    String newPlayerName = args[3];
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


                    if (!list.contains(newPlayerUUID)) {
                        player.sendMessage("§cThis player is not in your list");
                        return;
                    }

                    list.remove(newPlayerUUID);
                    tportData.getConfig().set("tport." + playerUUID + ".items." + s + ".private.players", list);
                    tportData.saveConfig();
                    player.sendMessage("§3Successfully removed " + newPlayerName);

                    Player newPlayer = Bukkit.getPlayer(UUID.fromString(newPlayerUUID));
                    if (newPlayer != null) {
                        newPlayer.sendMessage("§3You are removed in the whitelist of §9" + player.getName() + " §3in the item §9" + name);
                    }
                } else {
                    player.sendMessage("§cUse: §4/tport whitelist <TPort> <add:remove> <player>" +
                            "§c or§4 /tport whitelist <TPort> list");
                    return;
                }
            }
        }

        if (bool) {
            player.sendMessage("§cNo TPort found called §4" + args[1]);
        }
    }
}
