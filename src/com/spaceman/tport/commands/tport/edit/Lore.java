package com.spaceman.tport.commands.tport.edit;

import com.spaceman.tport.Permissions;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Lore extends SubCommand {

    @Override
    public List<String> tabList(Player player, String[] args) {
        return new ArrayList<>(Arrays.asList("set", "remove"));
    }

    @Override
    public void run(String[] args, Player player) {
        Files tportData = GettingFiles.getFile("TPortData");
        String playerUUID = player.getUniqueId().toString();

        for (String s : tportData.getConfig().getConfigurationSection("tport." + playerUUID + ".items").getKeys(false)) {

            String name = tportData.getConfig().getString("tport." + playerUUID + ".items." + s + ".name");
            if (name.equalsIgnoreCase(args[1])) {

                ItemStack item = tportData.getConfig().getItemStack("tport." + playerUUID + ".items." + s + ".item");
                ItemMeta meta = item.getItemMeta();

                if (!Permissions.hasPermission(player, "TPort.command.edit.lore", false)) {
                    if (!Permissions.hasPermission(player, "TPort.basic", false)) {
                        Permissions.sendNoPermMessage(player, "TPort.command.edit.lore", "TPort.basic");
                        return;
                    }
                }

                if (args.length == 3) {
                    player.sendMessage("§cUse: §4/tport edit " + args[1] + " lore set:remove <lore...>");
                    return;
                }
                if (args[3].equalsIgnoreCase("set")) {
                    if (args.length < 5) {
                        player.sendMessage(ChatColor.RED + "§cUse: §4/tport edit <TPort name> lore set <lore...>");
                        return;
                    }

                    StringBuilder lore = new StringBuilder(args[4]);
                    for (int ii = 5; ii <= args.length - 1; ii++) {
                        lore.append(" ").append(args[ii]);
                    }
                    ArrayList<String> list = new ArrayList<>(Arrays.asList(lore.toString().split("\\\\n")));

                    meta.setLore(list);
                    item.setItemMeta(meta);
                    tportData.getConfig().set("tport." + playerUUID + ".items." + s + ".item", item);
                    tportData.saveConfig();
                    player.sendMessage("§3Lore is set to: §9" + lore);
                    return;
                } else if (args[3].equalsIgnoreCase("remove")) {
                    if (args.length == 4) {
                        ArrayList<String> list = new ArrayList<>();
                        meta.setLore(list);
                        item.setItemMeta(meta);
                        tportData.getConfig().set("tport." + playerUUID + ".items." + s + ".item", item);
                        tportData.saveConfig();
                        player.sendMessage("§3Lore is successfully removed");
                        return;
                    } else {
                        player.sendMessage("§cUse: §4/tport edit <TPort name> lore remove");
                        return;
                    }
                } else {
                    player.sendMessage("§cUse: §4/tport edit <TPort name> lore set <lore...>§c or§4 /tport edit <TPort> lore remove");
                    return;
                }

            }
        }
        player.sendMessage("§cNo TPort found called §4" + args[1]);
    }
}
