package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commands.CmdHandler;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Remove extends CmdHandler {

    @Override
    public void run(String[] args, Player player) {

        // tport remove <name of item>

        if (args.length == 1) {
            player.sendMessage("§cuse: §4/tport remove <TPort>");
            return;
        }
        if (args.length == 2) {
            boolean bool = true;
            for (int i = 0; i <= 10; i++) {
                if (p.getConfig().contains("tport." + player.getName() + ".items." + i)) {
                    ItemStack item = p.getConfig().getItemStack("tport." + player.getName() + ".items." + i + ".item");
                    ItemMeta meta = item.getItemMeta();
                    if (meta.getDisplayName().equalsIgnoreCase(args[1])) {
                        p.getConfig().set("tport." + player.getName() + ".items." + i, null);
                        p.saveConfig();
                        player.sendMessage("§3succesfully removed §9" + args[1]);
                        bool = false;
                    }
                }
            }

            if (bool) {
                player.sendMessage("§cno TPort found called §4" + args[1]);
            }

        } else {
            player.sendMessage("§cuse: §4/tport remove <TPort>");
        }
    }
}
