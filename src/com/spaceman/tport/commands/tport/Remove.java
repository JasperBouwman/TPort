package com.spaceman.tport.commands.tport;

import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.commands.CmdHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static com.spaceman.tport.fileHander.GettingFiles.getFiles;

public class Remove extends CmdHandler {

    @Override
    public void run(String[] args, Player player) {

        // tport remove <TPort name>

        if (args.length == 1) {
            player.sendMessage("§cUse: §4/tport remove <TPort name>");
            return;
        }
        if (args.length == 2) {
            String playerUUID = player.getUniqueId().toString();

            Files tportData = getFiles("TPortData");

            boolean bool = true;

            if (tportData.getConfig().contains("tport." + playerUUID + ".items")) {
                for (String i : tportData.getConfig().getConfigurationSection("tport." + playerUUID + ".items").getKeys(false)) {
                    if (tportData.getConfig().contains("tport." + playerUUID + ".items." + i)) {
                        ItemStack item = tportData.getConfig().getItemStack("tport." + playerUUID + ".items." + i + ".item");
                        ItemMeta meta = item.getItemMeta();
//                        if (meta.getDisplayName().equalsIgnoreCase(args[1])) {
                        if (args[1].equalsIgnoreCase(tportData.getConfig().getString("tport." + playerUUID + ".items." + i + ".name"))) {

//                            meta.setDisplayName(tportData.getConfig().getString("tport." + playerUUID + ".items." + i + ".name"));
                            meta.setLore(null);
                            item.setItemMeta(meta);

                            for (ItemStack is : player.getInventory().addItem(item).values()) {
                                player.getWorld().dropItem(player.getLocation(), is);
                                player.sendMessage(ChatColor.RED + "Due to not enough space in your inventory the item is dropped on the ground");
                            }

                            tportData.getConfig().set("tport." + playerUUID + ".items." + i, null);
                            tportData.saveConfig();
                            player.sendMessage("§3Successfully removed §9" + args[1]);
                            bool = false;
                        }
                    }
                }
            }

            if (bool) {
                player.sendMessage("§cNo TPort found called §4" + args[1]);
            }

        } else {
            player.sendMessage("§cUse: §4/tport remove <TPort name>");
        }
    }
}
