package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commands.CmdHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class Whitelist extends CmdHandler {

    @Override
    public void run(String[] args, Player player) {
        // tport whitelist <name item> add <player>
        // tport whitelist <name item> remove <player>
        // tport whitelist <name item> list

        if (args.length == 3) {

            if (!p.getConfig().contains("tport." + player.getName() + ".items")) {
                player.sendMessage("§cno TPort found called §4" + args[1]);
                return;
            }

            for (String s : p.getConfig().getConfigurationSection("tport." + player.getName() + ".items")
                    .getKeys(false)) {
                ItemStack item = p.getConfig().getItemStack("tport." + player.getName() + ".items." + s + ".item");
                ItemMeta meta = item.getItemMeta();
                String name = meta.getDisplayName();

                if (args[1].equalsIgnoreCase(name)) {
                    if (args[2].equalsIgnoreCase("list")) {
                        ArrayList<String> list = (ArrayList<String>) p.getConfig()
                                .getStringList("tport." + player.getName() + ".items." + s + ".private.players");
                        int i = 0;
                        player.sendMessage("§3players in your whitelist:");
                        for (String ss : list) {
                            if (i == 0) {
                                player.sendMessage("§9" + ss);
                                i++;
                            } else {
                                player.sendMessage("§3" + ss);
                                i = 0;
                            }

                        }
                        return;
                    }
                }
            }
        }
        if (args.length != 4) {
            player.sendMessage("§cuse: §4/tport whitelist <TPort> <add:remove> <player>" +
                    "§c or§4 /tport whitelist <TPort> list");
            return;
        }
        if (!p.getConfig().contains("tport." + player.getName() + ".items")) {
            player.sendMessage("§cno item found called §4" + args[1]);
            return;
        }
        boolean bool = true;
        for (String s : p.getConfig().getConfigurationSection("tport." + player.getName() + ".items").getKeys(false)) {

            ItemStack item = p.getConfig().getItemStack("tport." + player.getName() + ".items." + s + ".item");
            ItemMeta meta = item.getItemMeta();
            String name = meta.getDisplayName();

            if (args[1].equalsIgnoreCase(name)) {
                bool = false;
                ArrayList<String> list = (ArrayList<String>) p.getConfig()
                        .getStringList("tport." + player.getName() + ".items." + s + ".private.players");
                if (args[2].equalsIgnoreCase("add")) {
                    if (Bukkit.getPlayerExact(args[3]) == null) {
                        player.sendMessage("§cthis player must be online");
                        return;
                    }

                    if (args[3].equalsIgnoreCase(player.getName())) {
                        player.sendMessage("§cyou don't have to put yourself in your whitelist");
                        return;
                    }

                    if (list.contains(args[3])) {
                        player.sendMessage("§cthis player is already in you list");
                        return;
                    }

                    list.add(args[3]);
                    p.getConfig().set("tport." + player.getName() + ".items." + s + ".private.players", list);
                    p.saveConfig();
                    player.sendMessage("§3succesfully added");
                    Bukkit.getPlayerExact(args[3]).sendMessage(
                            "§3you are in the whitelist of §9" + player.getName() + " §3in the item §9" + name);

                } else if (args[2].equalsIgnoreCase("remove")) {
                    if (Bukkit.getPlayerExact(args[3]) == null) {
                        player.sendMessage("§cthis player must be online");
                        return;
                    }

                    if (!list.contains(args[3])) {
                        player.sendMessage("§cthis player is not in your list");
                        return;
                    }

                    list.remove(args[3]);
                    p.getConfig().set("tport." + player.getName() + ".items." + s + ".private.players", list);
                    p.saveConfig();
                    player.sendMessage("§3succesfully removed");
                    Bukkit.getPlayerExact(args[3]).sendMessage(
                            "§3you are removed in the whitelist of §9" + player.getName() + " §3in the item §9" + name);

                } else {
                    player.sendMessage("§cuse: §4/tport whitelist <TPort> <add:remove> <player>" +
                            "§c or§4 /tport whitelist <TPort> list");
                    return;
                }
            }
        }

        if (bool) {
            player.sendMessage("§cno TPort found called §4" + args[1]);
        }
    }
}
