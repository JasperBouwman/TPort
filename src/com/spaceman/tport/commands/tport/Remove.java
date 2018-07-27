package com.spaceman.tport.commands.tport;

import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.commands.CmdHandler;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static com.spaceman.tport.fileHander.GettingFiles.getFiles;

public class Remove extends CmdHandler {

    @Override
    public void run(String[] args, Player player) {

        // tport remove <name of item>

        if (args.length == 1) {
            player.sendMessage("§cUse: §4/tport remove <TPort name>");
            return;
        }
        if (args.length == 2) {
            String playerUUID = player.getUniqueId().toString();

            Files tportData = getFiles("TPortData");

            boolean bool = true;
            for (int i = 0; i <= 10; i++) {
                if (tportData.getConfig().contains("tport." + playerUUID + ".items." + i)) {
                    ItemStack item = tportData.getConfig().getItemStack("tport." + playerUUID + ".items." + i + ".item");
                    ItemMeta meta = item.getItemMeta();
                    if (meta.getDisplayName().equalsIgnoreCase(args[1])) {

                        meta.setDisplayName(null);
                        meta.setLore(null);
                        item.setItemMeta(meta);

                        player.getInventory().addItem(item);

                        tportData.getConfig().set("tport." + playerUUID + ".items." + i, null);
                        tportData.saveConfig();
                        player.sendMessage("§3Successfully removed §9" + args[1]);
                        bool = false;
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
