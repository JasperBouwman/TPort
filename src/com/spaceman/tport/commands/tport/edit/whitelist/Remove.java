package com.spaceman.tport.commands.tport.edit.whitelist;

import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import com.spaceman.tport.playerUUID.PlayerUUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Remove extends SubCommand {

    @Override
    public List<String> tabList(Player player, String[] args) {
        ArrayList<String> list = new ArrayList<>();
        Files tportData = GettingFiles.getFile("TPortData");
        String playerUUID = player.getUniqueId().toString();
        if (tportData.getConfig().contains("tport." + playerUUID + ".items")) {
            for (String s : tportData.getConfig().getConfigurationSection("tport." + playerUUID + ".items").getKeys(false)) {
                String name = tportData.getConfig().getString("tport." + playerUUID + ".items." + s + ".name");
                if (name.equalsIgnoreCase(args[1])) {
                    List<String> players = tportData.getConfig().getStringList("tport." + playerUUID + ".items." + s + ".private.players");
                    players.stream().map(PlayerUUID::getPlayerName).filter(Objects::nonNull).forEach(list::add);
//                    for (String uuid : players) {
//                        String tmp = PlayerUUID.getPlayerName(uuid);
//                        if (tmp != null) {
//                            list.add(tmp);
//                        }
//                    }
                }
            }
        }
        return list;
    }

    @Override
    public void run(String[] args, Player player) {
        if (args.length == 4) {
            player.sendMessage("§cUse: §4/tport whitelist <TPort name> <remove> <players...>");
            return;
        }

        Files tportData = GettingFiles.getFile("TPortData");
        String playerUUID = player.getUniqueId().toString();
        for (String s : tportData.getConfig().getConfigurationSection("tport." + playerUUID + ".items").getKeys(false)) {

            String name = tportData.getConfig().getString("tport." + playerUUID + ".items." + s + ".name");
            if (name.equalsIgnoreCase(args[1])) {


                ArrayList<String> list = (ArrayList<String>) tportData.getConfig().getStringList("tport." + playerUUID + ".items." + s + ".private.players");
                for (int i = 4; i < args.length; i++) {

                    String newPlayerName = args[i];
                    String newPlayerUUID = PlayerUUID.getPlayerUUID(newPlayerName);
                    if (newPlayerUUID == null) {
                        ArrayList<String> globalNames = PlayerUUID.getGlobalPlayerUUID(newPlayerName);
                        if (globalNames.size() == 1) {
                            newPlayerUUID = globalNames.get(0);
                        } else if (globalNames.size() == 0) {
                            player.sendMessage(ChatColor.RED + "Could not find any players named " + ChatColor.DARK_RED + newPlayerName);
                            continue;
                        } else {
                            player.sendMessage(ChatColor.RED + "There are more players found with the name " + ChatColor.DARK_RED + newPlayerName + ChatColor.RED
                                    + ", please type the correct name with correct capitals");
                            continue;
                        }
                    }
                    if (!list.contains(newPlayerUUID)) {
                        player.sendMessage("§cThis player is not in your whitelist");
                        continue;
                    }
                    if (newPlayerUUID.equals(playerUUID)) {
                        player.sendMessage(ChatColor.RED + "You can't remove yourself from your whitelist");
                        continue;
                    }

                    list.remove(newPlayerUUID);
                    player.sendMessage("§3Successfully removed " + newPlayerName);

                    Player newPlayer = Bukkit.getPlayer(UUID.fromString(newPlayerUUID));
                    if (newPlayer != null) {
                        newPlayer.sendMessage("§3You are removed in the whitelist of §9" + player.getName() + " §3in the item §9" + name);
                    }
                }
                tportData.getConfig().set("tport." + playerUUID + ".items." + s + ".private.players", list);
                tportData.saveConfig();
                return;
            }
        }
        player.sendMessage("§cNo TPort found called §4" + args[1]);
    }
}
