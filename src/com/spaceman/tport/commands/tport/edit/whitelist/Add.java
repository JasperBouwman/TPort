package com.spaceman.tport.commands.tport.edit.whitelist;

import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import com.spaceman.tport.playerUUID.PlayerUUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Add extends SubCommand {

    @Override
    public List<String> tabList(Player player, String[] args) {
        ArrayList<String> list = new ArrayList<>();
        for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
            list.add(p.getName());
        }
        list.remove(player.getName());

        ArrayList<String> removeList = new ArrayList<>();
        Files tportData = GettingFiles.getFile("TPortData");
        String playerUUID = player.getUniqueId().toString();
        if (tportData.getConfig().contains("tport." + playerUUID + ".items")) {
            for (String s : tportData.getConfig().getConfigurationSection("tport." + playerUUID + ".items").getKeys(false)) {
                String name = tportData.getConfig().getString("tport." + playerUUID + ".items." + s + ".name");
                if (name.equalsIgnoreCase(args[1])) {
                    List<String> players = tportData.getConfig().getStringList("tport." + playerUUID + ".items." + s + ".private.players");
                    players.stream().map(PlayerUUID::getPlayerName).filter(Objects::nonNull).forEach(removeList::add);
//                    for (String uuid : players) {
//                        String tmp = PlayerUUID.getPlayerName(uuid);
//                        if (tmp != null) {
//                            list.add(tmp);
//                        }
//                    }
                }
            }
        }
        list.removeAll(removeList);

        return list;
    }

    @Override
    public void run(String[] args, Player player) {
        if (args.length == 4) {
            player.sendMessage("§cUse: §4/tport whitelist <TPort name> <add> <players...>");
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

                    if (newPlayerUUID.equals(playerUUID)) {
                        player.sendMessage("§cYou don't have to put yourself in your whitelist");
                        continue;
                    }
                    if (list.contains(newPlayerUUID)) {
                        player.sendMessage("§cThis player is already in you whitelist");
                        continue;
                    }

                    list.add(newPlayerUUID);
                    player.sendMessage("§3Successfully added " + newPlayerName);

                    Player newPlayer = Bukkit.getPlayer(UUID.fromString(newPlayerUUID));
                    if (newPlayer != null) {
                        newPlayer.sendMessage("§3You are in the whitelist of §9" + player.getName() + " §3in the item §9" + name);
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
