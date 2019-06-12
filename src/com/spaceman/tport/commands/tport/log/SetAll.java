package com.spaceman.tport.commands.tport.log;

import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.logbook.Logbook;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

import static com.spaceman.tport.commands.tport.Own.getOwnTPorts;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;

public class SetAll extends SubCommand {

    @Override
    public List<String> tabList(Player player, String[] args) {
        return getOwnTPorts(player);
    }

    @Override
    public void run(String[] args, Player player) {
        //tport log setAll <TPort name> <LogMode>

        if (args.length == 4) {
            Files tportData = getFile("TPortData");
            String uuid = player.getUniqueId().toString();
            if (tportData.getConfig().contains("tport." + uuid + ".items")) {
                for (String slot : tportData.getConfig().getConfigurationSection("tport." + uuid + ".items").getKeys(false)) {
                    if (tportData.getConfig().contains("tport." + uuid + ".items." + slot)) {
                        String tport = tportData.getConfig().getString("tport." + uuid + ".items." + slot + ".name");
                        if (args[2].equals(tport)) {

                            for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                                UUID logUUID = offlinePlayer.getUniqueId();

                                Logbook.LogMode logMode;
                                try {
                                    logMode = Logbook.LogMode.valueOf(args[3].toUpperCase());
                                } catch (IllegalArgumentException iae) {
                                    player.sendMessage(args[3].toUpperCase() + " is not a valid log mode");
                                    return;
                                }

                                if (Logbook.isLogged(player.getUniqueId(), tport, logUUID)) {
                                    player.sendMessage(ChatColor.DARK_AQUA + "Log mode of player " + ChatColor.BLUE + offlinePlayer.getName() + ChatColor.DARK_AQUA
                                            + " is set to: " + ChatColor.BLUE + logMode.name());
                                    Logbook.setPlayerMode(player.getUniqueId(), tport, logUUID, logMode);
                                }
                            }
                            return;
                        }
                    }
                }
            }
            player.sendMessage("§cNo TPort found called §4" + args[2]);
        } else {
            player.sendMessage("§cUse: §4/tport log setAll <TPort name> <LogMode>");
        }
    }
}
