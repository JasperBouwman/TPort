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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.spaceman.tport.commands.tport.Own.getOwnTPorts;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;

public class SetMode extends SubCommand {

    public SetMode() {
        EmptyCommand emptyCommand1 = new EmptyCommand();
        emptyCommand1.setTabRunnable((args, player) -> Arrays.stream(Logbook.LogMode.values()).map(Logbook.LogMode::name).collect(Collectors.toList()));

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
        emptyCommand.addAction(emptyCommand1);
        addAction(emptyCommand);
    }

    @Override
    public List<String> tabList(Player player, String[] args) {
        return Logbook.getList(player.getUniqueId());
    }

    @Override
    public void run(String[] args, Player player) {
        //tport log setMode <TPort name> <player name> <LogMode>

        if (args.length == 5) {
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
                            Logbook.LogMode logMode;
                            try {
                                logMode = Logbook.LogMode.valueOf(args[4].toUpperCase());
                            } catch (IllegalArgumentException iae) {
                                player.sendMessage(args[4].toUpperCase() + " is not a valid log mode");
                                return;
                            }

                            if (Logbook.isLogged(player.getUniqueId(), tport, UUID.fromString(logUUID))) {
                                player.sendMessage(ChatColor.DARK_AQUA + "Log mode of player " + ChatColor.BLUE + args[3] + ChatColor.DARK_AQUA
                                        + " in the TPort " + ChatColor.BLUE + tport + ChatColor.DARK_AQUA + " is set to: " + ChatColor.BLUE
                                        + logMode.name());
                                Logbook.setPlayerMode(player.getUniqueId(), tport, UUID.fromString(logUUID), logMode);
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
            player.sendMessage("§cUse: §4/tport log setMode <TPort name> <player name> <LogMode>");
        }
    }
}
