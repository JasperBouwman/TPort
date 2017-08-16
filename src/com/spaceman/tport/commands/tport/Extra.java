package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commands.CmdHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class Extra extends CmdHandler {

    @Override
    public void run(String[] args, Player player) {

        // tport extra item
        // tport extra tp [on:off]
        // tport extra whitelist

        if (args.length == 1) {
            player.sendMessage("§cuse: §4/tport extra <item:tp:whitelist>");
            return;
        }

        if (args[1].equalsIgnoreCase("item")) {

            if (args.length == 2) {

                ItemStack item = new ItemStack(player.getInventory().getItemInMainHand());

                if (item.getItemMeta() == null) {
                    player.sendMessage("§cplace an item in you main hand");
                    return;
                }

                item.setAmount(1);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(player.getName());
                item.setItemMeta(meta);
                p.getConfig().set("tport." + player.getName() + ".item", item);
                p.saveConfig();
                player.sendMessage("§3succesfully edited");

            } else {
                player.sendMessage("§use: §4/tport extra item");
            }

        } else if (args[1].equalsIgnoreCase("tp")) {

            if (args.length != 3) {
                player.sendMessage("§cuse: §4/tport extra tp [true:false]");
                return;
            }

            if (args[2].equalsIgnoreCase("on")) {

                if (p.getConfig().getString("tport." + player.getName() + ".tp.statement").equals("on")) {
                    player.sendMessage("§cthis is already set to on");
                    return;
                }

                player.sendMessage("§3succesfully set to on");
                p.getConfig().set("tport." + player.getName() + ".tp.statement", "on");
                p.saveConfig();

            } else if (args[2].equalsIgnoreCase("off")) {

                if (p.getConfig().getString("tport." + player.getName() + ".tp.statement").equals("off")) {
                    player.sendMessage("§cthis is already set to off");
                    return;
                }

                player.sendMessage("§3succesfully set to off");
                p.getConfig().set("tport." + player.getName() + ".tp.statement", "off");
                p.saveConfig();

            }
        } else if (args[1].equalsIgnoreCase("whitelist")) {

            if (args.length == 3) {

                if (args[2].equalsIgnoreCase("list")) {
                    ArrayList<String> list = (ArrayList<String>) p.getConfig()
                            .getStringList("tport." + player.getName() + ".tp.players");
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

            if (args.length != 4) {
                player.sendMessage("§cuse: §4/tport extra whitelist [add:remove] <playername>");
                return;
            }

            ArrayList<String> list = (ArrayList<String>) p.getConfig()
                    .getStringList("tport." + player.getName() + ".tp.players");
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
                p.getConfig().set("tport." + player.getName() + ".tp.players", list);
                p.saveConfig();
                player.sendMessage("§3succesfully added");
                Bukkit.getPlayerExact(args[3]).sendMessage("§3you are in the main whitelist of §9" + player.getName());

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
                p.getConfig().set("tport." + player.getName() + ".tp.players", list);
                p.saveConfig();
                player.sendMessage("§3succesfully removed");
                Bukkit.getPlayerExact(args[3])
                        .sendMessage("§3you are removed in the main whitelist of §9" + player.getName());

            } else {
                player.sendMessage("§cuse: §4/tport extra whitelist [add:remove] <playername>");
            }
        }
    }
}
