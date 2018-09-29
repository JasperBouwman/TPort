package com.spaceman.tport.commands.tport;

import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.commands.CmdHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;

import static com.spaceman.tport.fileHander.GettingFiles.getFiles;

public class RemovePlayer extends CmdHandler {

    @Override
    public void run(String[] args, Player player) {
        //tport removePlayer <playerName>

        if (!player.isOp()) {
            player.sendMessage(ChatColor.RED + "You must be an OP to use this");
            return;
        }
        if (args.length != 2) {
            player.sendMessage(ChatColor.RED + "Usage: /tport removePlayer <player name>");
            return;
        }
        Files tportData = getFiles("TPortData");

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
