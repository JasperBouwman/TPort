package com.spaceman.tport.commands.tport.log;

import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.logbook.Logbook;
import com.spaceman.tport.playerUUID.PlayerUUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.spaceman.tport.fileHander.GettingFiles.getFile;

public class GetMode extends SubCommand {

    public GetMode() {
        EmptyCommand emptyCommand = new EmptyCommand();
        emptyCommand.setTabRunnable((args, player) -> {
            String tport = args[2];
            ArrayList<String> list = new ArrayList<>();
            for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                if (Logbook.isLogged(player.getUniqueId(), tport, offlinePlayer.getUniqueId())) {
                    list.add(offlinePlayer.getName());
                }
            }
            return list;
        });
        addAction(emptyCommand);
    }

    @Override
    public List<String> tabList(Player player, String[] args) {
        return Logbook.getList(player.getUniqueId());
    }

    @Override
    public void run(String[] args, Player player) {
        //tport log getMode <TPort name> <player name>

        if (args.length == 4) {
            Files tportData = getFile("TPortData");
            String uuid = player.getUniqueId().toString();
            if (tportData.getConfig().contains("tport." + uuid + ".items")) {
                for (String slot : tportData.getConfig().getConfigurationSection("tport." + uuid + ".items").getKeys(false)) {
                    if (tportData.getConfig().contains("tport." + uuid + ".items." + slot)) {
                        String tport = tportData.getConfig().getString("tport." + uuid + ".items." + slot + ".name");
                        if (args[2].equals(tport)) {

                            String logUUID = PlayerUUID.getPlayerUUID(args[3]);
                            if (logUUID == null) {
                                player.sendMessage(ChatColor.RED + "This player is not logged");
                                return;
                            }

                            if (Logbook.isLogged(player.getUniqueId(), tport, UUID.fromString(logUUID))) {
                                player.sendMessage(ChatColor.DARK_AQUA + "Log mode of player " + ChatColor.BLUE + args[3] + ChatColor.DARK_AQUA
                                        + " in the TPort " + ChatColor.BLUE + tport + ChatColor.DARK_AQUA + " is: " + ChatColor.BLUE
                                        + Logbook.getPlayerMode(player.getUniqueId(), tport, UUID.fromString(logUUID)));
                            } else {
                                player.sendMessage(ChatColor.RED + "This player is not logged");
                            }
                            return;
                        }
                    }
                }
            }
            player.sendMessage("§cNo TPort found called §4" + args[2]);
        } else {
            player.sendMessage("§cUse: §4/tport log getMode <TPort name> <player name>");
        }
    }
}
