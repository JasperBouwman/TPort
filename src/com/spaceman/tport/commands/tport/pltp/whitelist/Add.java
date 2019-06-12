package com.spaceman.tport.commands.tport.pltp.whitelist;

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
        Files tportData = GettingFiles.getFile("TPortData");

        for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
            list.add(p.getName());
        }
        new ArrayList<>(tportData.getConfig().getStringList("tport." + player.getUniqueId() + ".tp.players")).stream().map(PlayerUUID::getPlayerName).filter(Objects::nonNull).forEach(list::remove);
        list.remove(player.getName());
        return list;
    }

    @Override
    public void run(String[] args, Player player) {

        Files tportData = GettingFiles.getFile("TPortData");
        String playerUUID = player.getUniqueId().toString();
        ArrayList<String> list = (ArrayList<String>) tportData.getConfig()
                .getStringList("tport." + playerUUID + ".tp.players");

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
    }
}
