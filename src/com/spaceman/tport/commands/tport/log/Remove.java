package com.spaceman.tport.commands.tport.log;

import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import com.spaceman.tport.logbook.Logbook;
import com.spaceman.tport.playerUUID.PlayerUUID;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.spaceman.tport.commands.tport.Own.getOwnTPorts;

public class Remove extends SubCommand {//todo colorize

    @Override
    public List<String> tabList(Player player, String[] args) {
        return getOwnTPorts(player);
    }

    @Override
    public void run(String[] args, Player player) {
        //tport log remove <TPort name> <player name...>

        if (args.length >= 4) {

            Files tportData = GettingFiles.getFile("TPortData");

            if (tportData.getConfig().contains("tport." + player.getUniqueId().toString() + ".items")) {
                for (String i : tportData.getConfig().getConfigurationSection("tport." + player.getUniqueId().toString() + ".items").getKeys(false)) {
                    if (tportData.getConfig().contains("tport." + player.getUniqueId().toString() + ".items." + i)) {
                        if (args[2].equalsIgnoreCase(tportData.getConfig().getString("tport." + player.getUniqueId().toString() + ".items." + i + ".name"))) {

                            for (String name : Arrays.asList(args).subList(3, args.length)) {
                                String uuid = PlayerUUID.getPlayerUUID(name);
                                if (uuid == null) {
                                    player.sendMessage("player " + name + " does not exist");
                                    continue;
                                }

                                try {
                                    try {
                                        Logbook.removePlayer(player.getUniqueId(), args[2], UUID.fromString(uuid));
                                        player.sendMessage("removed " + name);
                                    } catch (IllegalArgumentException iae) {
                                        player.sendMessage("player " + name + " is not logged");
                                    }
                                } catch (Exception e) {
                                    Logbook.addPlayer(player.getUniqueId(), args[2], UUID.fromString(uuid));
                                    player.sendMessage("added " + name + " with the logMode " + Logbook.LogMode.ALL.name().toLowerCase());
                                }


                            }
                            return;
                        }
                    }
                }
            }
            player.sendMessage("could not find TPort");
        } else {
            player.sendMessage("Use: /tport log remove <TPort name> <player name>...");
        }
    }
}
