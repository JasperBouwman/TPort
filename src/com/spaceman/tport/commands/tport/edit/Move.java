package com.spaceman.tport.commands.tport.edit;

import com.spaceman.tport.Permissions;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static com.spaceman.tport.events.InventoryClick.TPortSize;

public class Move extends SubCommand {

    @Override
    public List<String> tabList(Player player, String[] args) {
        ArrayList<String> list = new ArrayList<>();
        Files tportData = GettingFiles.getFile("TPortData");
        for (int i = 0; i < TPortSize; i++) {
            if (!tportData.getConfig().contains("tport." + player.getUniqueId().toString() + ".items." + i)) {
                list.add(String.valueOf((i + 1)));
            }
        }
        return list;
    }

    @Override
    public void run(String[] args, Player player) {

        Files tportData = GettingFiles.getFile("TPortData");
        String playerUUID = player.getUniqueId().toString();

        for (String s : tportData.getConfig().getConfigurationSection("tport." + playerUUID + ".items").getKeys(false)) {

            String name = tportData.getConfig().getString("tport." + playerUUID + ".items." + s + ".name");
            if (name.equalsIgnoreCase(args[1])) {

                if (!Permissions.hasPermission(player, "TPort.command.edit.move", false)) {
                    if (!Permissions.hasPermission(player, "TPort.basic", false)) {
                        Permissions.sendNoPermMessage(player, "TPort.command.edit.move", "TPort.basic");
                        return;
                    }
                }

                if (args.length == 4) {
                    int slot;
                    try {
                        slot = Integer.parseInt(args[3]);
                    } catch (NumberFormatException nfe) {
                        player.sendMessage(ChatColor.RED + "Slot number " + ChatColor.DARK_RED + args[3] + ChatColor.RED + " is not a number");
                        return;
                    }
                    if (slot < 1 || slot > TPortSize) {
                        player.sendMessage(ChatColor.RED + "Slot number must between 1 and " + TPortSize);
                        return;
                    }
                    //convert to real slot number
                    slot--;
                    if (tportData.getConfig().contains("tport." + playerUUID + ".items." + slot)) {
                        player.sendMessage(ChatColor.RED + "Slot " + ChatColor.DARK_RED + (slot + 1) + ChatColor.RED + " is taken, choose another one");
                        return;
                    }
                    tportData.getConfig().set("tport." + playerUUID + ".items." + slot, tportData.getConfig().getConfigurationSection("tport." + playerUUID + ".items." + s));
                    tportData.getConfig().set("tport." + playerUUID + ".items." + s, null);
                    tportData.saveConfig();
                    player.sendMessage(ChatColor.DARK_AQUA + "Tport has been moved to slot " + ChatColor.BLUE + (slot + 1));
                    return;

                } else {
                    player.sendMessage(ChatColor.RED + "Use: " + ChatColor.DARK_RED + "/tport edit <TPort name> move <slot>");
                }



                return;
            }
        }
        player.sendMessage("§cNo TPort found called §4" + args[1]);

    }
}
