package com.spaceman.tport.commands.tport;

import com.spaceman.tport.Permissions;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import com.spaceman.tport.playerUUID.PlayerUUID;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RemovePlayer extends SubCommand {

    @Override
    public List<String> tabList(Player player, String[] args) {
        ArrayList<String> list = new ArrayList<>();
        if (player.isOp()) {
            Files tportData = GettingFiles.getFile("TPortData");
            for (String s : tportData.getConfig().getConfigurationSection("tport").getKeys(false)) {
                list.add(PlayerUUID.getPlayerName(s));
            }
        }
        return list;
    }

    @Override
    public void run(String[] args, Player player) {
        //tport removePlayer <playerName>

        if (!Permissions.hasPermission(player, "TPort.admin.removePlayer")) {
            return;
        }

        if (!player.isOp()) {
            player.sendMessage(ChatColor.RED + "You must be an OP to use this");
            return;
        }
        if (args.length != 2) {
            player.sendMessage(ChatColor.RED + "Usage: /tport removePlayer <player name>");
            return;
        }
        Files tportData = GettingFiles.getFile("TPortData");

        String newPlayerName = args[1];
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

        if (tportData.getConfig().contains("tport." + newPlayerUUID)) {
            tportData.getConfig().set("tport." + newPlayerUUID, null);
            tportData.saveConfig();
            player.sendMessage("§3Successfully removed " + newPlayerName + "from the TPort plugin");
        } else {
            player.sendMessage(ChatColor.RED + "This player is not registered in the TPort plugin");
        }
    }
}
