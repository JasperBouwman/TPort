package com.spaceman.tport.commands.tport.edit.whitelist;

import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import com.spaceman.tport.playerUUID.PlayerUUID;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class List extends SubCommand {

    @Override
    public void run(String[] args, Player player) {
        Message message = new Message();
        boolean color = false;
        boolean first = true;
        Files tportData = GettingFiles.getFile("TPortData");
        String playerUUID = player.getUniqueId().toString();

        for (String s : tportData.getConfig().getConfigurationSection("tport." + playerUUID + ".items").getKeys(false)) {

            String name = tportData.getConfig().getString("tport." + playerUUID + ".items." + s + ".name");
            if (name.equalsIgnoreCase(args[1])) {
                ArrayList<String> list = (ArrayList<String>) tportData.getConfig().getStringList("tport." + playerUUID + ".items." + s + ".private.players");

                message.addText("Players in the whitelist of the TPort " + ChatColor.DARK_GREEN + args[1] + ChatColor.GREEN + ": ", ChatColor.GREEN);
                message.addText(PlayerUUID.getPlayerName((list.size() > 0 ? list.get(0) : "")), ChatColor.BLUE);

                for (String tmp : list) {
                    if (first) {
                        first = false;
                        continue;
                    }

                    message.addText(", ", ChatColor.GREEN);

                    if (color) {
                        message.addText(PlayerUUID.getPlayerName(tmp), ChatColor.BLUE);
                    } else {
                        message.addText(PlayerUUID.getPlayerName(tmp), ChatColor.DARK_BLUE);
                    }
                    color = !color;
                }

                message.sendMessage(player);
                return;
            }
        }
        player.sendMessage("§cNo TPort found called §4" + args[1]);
    }
}
