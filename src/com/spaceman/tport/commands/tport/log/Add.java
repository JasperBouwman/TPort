package com.spaceman.tport.commands.tport.log;

import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import com.spaceman.tport.logbook.Logbook;
import com.spaceman.tport.playerUUID.PlayerUUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.spaceman.tport.commands.tport.Own.getOwnTPorts;

public class Add extends SubCommand {//todo colorize
    
    public Add() {
        EmptyCommand emptyCommand = new EmptyCommand();
        emptyCommand.setTabRunnable((args, player) -> {
            List<String> list = new ArrayList<>();
            
            nextOfflinePlayer:
            for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                if (!Arrays.asList(args).subList(2, args.length - 1).contains(offlinePlayer.getName())) {
                    for (Logbook.LogMode logMode : Logbook.LogMode.values()) {
                        if (Arrays.asList(args).subList(2, args.length - 1).contains(offlinePlayer.getName() + ":" + logMode.name())) {
                            continue nextOfflinePlayer;
                        }
                    }
                    if (args[args.length - 1].equals(offlinePlayer.getName())) {
                        for (Logbook.LogMode logMode : Logbook.LogMode.values()) {
                            list.add(offlinePlayer.getName() + ":" + logMode.name());
                        }
                    } else {
                        list.add(offlinePlayer.getName());
                    }
                }
            }
            
            return list;
        });
        emptyCommand.setLooped(true);
        addAction(emptyCommand);
    }
    
    @Override
    public List<String> tabList(Player player, String[] args) {
        return getOwnTPorts(player);
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport log add <TPort name> <player name[:mode]...>
        
        if (args.length >= 4) {
            Files tportData = GettingFiles.getFile("TPortData");
            
            if (tportData.getConfig().contains("tport." + player.getUniqueId().toString() + ".items")) {
                for (String i : tportData.getConfig().getConfigurationSection("tport." + player.getUniqueId().toString() + ".items").getKeys(false)) {
                    if (tportData.getConfig().contains("tport." + player.getUniqueId().toString() + ".items." + i)) {
                        if (args[2].equalsIgnoreCase(tportData.getConfig().getString("tport." + player.getUniqueId().toString() + ".items." + i + ".name"))) {
                            
                            for (String arg : Arrays.asList(args).subList(3, args.length)) {
                                String name = arg.split(":")[0];
                                if (name.equals(player.getName())) {
                                    player.sendMessage("can log yourself");
                                    continue;
                                }
                                String uuid = PlayerUUID.getPlayerUUID(name);
                                if (uuid == null) {
                                    player.sendMessage("player " + name + " does not exist");
                                    continue;
                                }
                                try {
                                    String mode = arg.split(":")[1].toUpperCase();
                                    Logbook.LogMode logMode;
                                    try {
                                        logMode = Logbook.LogMode.valueOf(mode);
                                    } catch (Exception e) {
                                        player.sendMessage(mode.toLowerCase() + " is not a valid logMode, therefor " + name + " could not be added");
                                        continue;
                                    }
                                    try {
                                        Logbook.addPlayer(player.getUniqueId(), args[2], UUID.fromString(uuid), logMode);
                                        player.sendMessage("added " + name + " with the logMode " + logMode.name().toLowerCase());
                                    } catch (IllegalArgumentException iae) {
                                        player.sendMessage("player " + name + " is already logged");
                                    }
                                } catch (Exception e) {
                                    try {
                                        Logbook.addPlayer(player.getUniqueId(), args[2], UUID.fromString(uuid));
                                        player.sendMessage("added " + name + " with the logMode " + Logbook.LogMode.ALL.name().toLowerCase());
                                    } catch (IllegalArgumentException iae) {
                                        player.sendMessage("player " + name + " is already logged");
                                    }
                                }
                            }
                            return;
                        }
                    }
                }
            }
            player.sendMessage("could not find TPort");
        } else {
            player.sendMessage("Use: /tport log add <TPort name> <player name[:mode]>...");
        }
    }
}
