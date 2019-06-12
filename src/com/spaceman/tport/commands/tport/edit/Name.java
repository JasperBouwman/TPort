package com.spaceman.tport.commands.tport.edit;

import com.spaceman.tport.Permissions;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import org.bukkit.entity.Player;

public class Name extends SubCommand {

    @Override
    public void run(String[] args, Player player) {
        Files tportData = GettingFiles.getFile("TPortData");
        String playerUUID = player.getUniqueId().toString();

        for (String s : tportData.getConfig().getConfigurationSection("tport." + playerUUID + ".items").getKeys(false)) {

            String name = tportData.getConfig().getString("tport." + playerUUID + ".items." + s + ".name");
            if (name.equalsIgnoreCase(args[1])) {

                if (!Permissions.hasPermission(player, "TPort.command.edit.name", false)) {
                    if (!Permissions.hasPermission(player, "TPort.basic", false)) {
                        Permissions.sendNoPermMessage(player, "TPort.command.edit.name", "TPort.basic");
                        return;
                    }
                }

                if (args.length == 4) {
                    tportData.getConfig().set("tport." + playerUUID + ".items." + s + ".name", args[3]);
                    tportData.saveConfig();
                    player.sendMessage("§3New name set to " + args[3]);
                } else {
                    player.sendMessage("§cUse: §4/tport edit " + args[1] + " name <new TPort name>");
                }
                return;
            }
        }
        player.sendMessage("§cNo TPort found called §4" + args[1]);
    }
}
