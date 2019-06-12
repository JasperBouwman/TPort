package com.spaceman.tport.commands.tport.log;

import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import com.spaceman.tport.logbook.Logbook;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

import static com.spaceman.tport.commands.tport.Own.getOwnTPorts;

public class AddAll extends SubCommand {

    @Override
    public List<String> tabList(Player player, String[] args) {
        return getOwnTPorts(player);
    }

    @Override
    public void run(String[] args, Player player) {
        //tport log addAll <TPort name>

        if (args.length == 3) {
            Files tportData = GettingFiles.getFile("TPortData");

            if (tportData.getConfig().contains("tport." + player.getUniqueId().toString() + ".items")) {
                for (String i : tportData.getConfig().getConfigurationSection("tport." + player.getUniqueId().toString() + ".items").getKeys(false)) {
                    if (tportData.getConfig().contains("tport." + player.getUniqueId().toString() + ".items." + i)) {
                        if (args[2].equalsIgnoreCase(tportData.getConfig().getString("tport." + player.getUniqueId().toString() + ".items." + i + ".name"))) {
                            for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                                if (offlinePlayer.getUniqueId().equals(player.getUniqueId())) {
                                    continue;
                                }
                                Logbook.addPlayer(player.getUniqueId(), args[2], offlinePlayer.getUniqueId());
                                player.sendMessage("added " + offlinePlayer.getName() + " with the logMode " + Logbook.LogMode.ALL.name().toLowerCase());
                            }
                            return;
                        }
                    }
                }
                player.sendMessage("could not find TPort");

            } else {
                player.sendMessage("Use: /tport log addAll <TPort name>");
            }

        }
    }
}
