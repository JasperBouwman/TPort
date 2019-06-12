package com.spaceman.tport.commands.tport.pltp.whitelist;

import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import com.spaceman.tport.playerUUID.PlayerUUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Remove extends SubCommand {

    @Override
    public List<String> tabList(Player player, String[] args) {
        Files tportData = GettingFiles.getFile("TPortData");
        ArrayList<String> list = new ArrayList<>();
        new ArrayList<>(tportData.getConfig().getStringList("tport." + player.getUniqueId() + ".tp.players")).stream().map(PlayerUUID::getPlayerName).forEach(list::add);
        return list;
    }

    @Override
    public void run(String[] args, Player player) {
        Files tportData = GettingFiles.getFile("TPortData");
        String playerUUID = player.getUniqueId().toString();
        ArrayList<String> list = (ArrayList<String>) tportData.getConfig()
                .getStringList("tport." + playerUUID + ".tp.players");

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
    }
}
