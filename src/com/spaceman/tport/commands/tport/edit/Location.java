package com.spaceman.tport.commands.tport.edit;

import com.spaceman.tport.Main;
import com.spaceman.tport.Permissions;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import org.bukkit.entity.Player;

public class Location extends SubCommand {

    @Override
    public void run(String[] args, Player player) {
        Files tportData = GettingFiles.getFile("TPortData");
        String playerUUID = player.getUniqueId().toString();

        for (String s : tportData.getConfig().getConfigurationSection("tport." + playerUUID + ".items").getKeys(false)) {

            String name = tportData.getConfig().getString("tport." + playerUUID + ".items." + s + ".name");
            if (name.equalsIgnoreCase(args[1])) {

                if (!Permissions.hasPermission(player, "TPort.command.edit.location", false)) {
                    if (!Permissions.hasPermission(player, "TPort.basic", false)) {
                        Permissions.sendNoPermMessage(player, "TPort.command.edit.location", "TPort.basic");
                        return;
                    }
                }

                if (args.length == 3) {
                    org.bukkit.Location l = player.getLocation();
                    Main.saveLocation("tport." + playerUUID + ".items." + s + ".location", l);
                    player.sendMessage("§3Successfully edited the location");
                } else {
                    player.sendMessage("§cUse: §4/tport edit <TPort name> location");
                }

                return;
            }
        }
        player.sendMessage("§cNo TPort found called §4" + args[1]);
    }
}
