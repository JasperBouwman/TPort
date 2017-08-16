package com.spaceman.tport.commands.tport;

import com.spaceman.tport.Main;
import com.spaceman.tport.commands.CmdHandler;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;

public class Add extends CmdHandler {

    @Override
    public void run(String[] args, Player player) {

        // tport add <name of item> [lore of item]

        if (args.length == 1) {
            player.sendMessage("§cuse: §4/tport add <TPort> [lore of TPort]");
            player.sendMessage("§cname is one word, and the lore can be more");
            return;
        }

        if (args.length == 2) {

            // looks if name is used
            for (int i = 0; i < 10; i++) {
                if (p.getConfig().contains("tport." + player.getName() + ".items." + i)) {
                    ItemStack item = p.getConfig().getItemStack("tport." + player.getName() + ".items." + i + ".item");
                    ItemMeta meta = item.getItemMeta();
                    if (meta.getDisplayName().equalsIgnoreCase(args[1])) {
                        player.sendMessage("§cthis TPort name is used");
                        return;
                    }
                }
            }

            ItemStack is = new ItemStack(player.getInventory().getItemInMainHand());

            if (is.getItemMeta() == null) {
                player.sendMessage("§cplace an item in you main hand");
                return;
            }

            // ItemMeta ism = is.getItemMeta();
            // if (ism.getDisplayName().equals(null)) {
            // player.sendMessage("place an item in you main hand");
            // return false;
            // }

            // set new item
            for (int i = 0; i < 9; i++) {
                if (i == 8) {
                    player.sendMessage("§cyour TPort list is full, remove an old one");
                    return;
                } else if (!p.getConfig().contains("tport." + player.getName() + ".items." + i)) {
                    ItemStack item = new ItemStack(player.getInventory().getItemInMainHand());
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName(args[1]);
                    item.setItemMeta(meta);
                    Location l = player.getLocation();
                    p.getConfig().set("tport." + player.getName() + ".items." + i + ".item", item);
//                    p.getConfig().set("tport." + player.getName() + ".items." + i + ".location", l);
                    Main.saveLocation(p,"tport." + player.getName() + ".items." + i + ".location", l);

                    p.getConfig().set("tport." + player.getName() + ".items." + i + ".private.statement", false);
                    ArrayList<String> list = new ArrayList<>();
                    p.getConfig().set("tport." + player.getName() + ".items." + i + ".private.players", list);
                    player.sendMessage("§3succesfully added the TPort §9" + args[1]);
                    p.saveConfig();
                    return;
                }
            }
        } else if (args.length >= 3) {

            ItemStack is = new ItemStack(player.getInventory().getItemInMainHand());

            if (is.getItemMeta() == null) {
                player.sendMessage("§cplace an item in you main hand");
                return;
            }

            // looks if name is used
            for (int i = 0; i < 10; i++) {
                if (p.getConfig().contains("tport." + player.getName() + ".items." + i)) {
                    ItemStack item = p.getConfig().getItemStack("tport." + player.getName() + ".items." + i + ".item");
                    ItemMeta meta = item.getItemMeta();
                    if (meta.getDisplayName().equalsIgnoreCase(args[1])) {
                        player.sendMessage("§cthis TPort name is used");
                        return;
                    }
                }
            }

            // set new item
            for (int i = 0; i < 9; i++) {
                if (i == 8) {
                    player.sendMessage("§cyour TPort list is full, remove an old one");
                    return;
                } else if (!p.getConfig().contains("tport." + player.getName() + ".items." + i)) {
                    ItemStack item = new ItemStack(player.getInventory().getItemInMainHand());
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName(args[1]);
                    ArrayList<String> list = new ArrayList<>();

                    String lore = args[2];
                    for (int ii = 3; ii <= args.length - 1; ii++) {
                        lore = lore + " " + args[ii];
                    }
                    list.add(lore);
                    meta.setLore(list);
                    item.setItemMeta(meta);
                    Location l = player.getLocation();
                    p.getConfig().set("tport." + player.getName() + ".items." + i + ".item", item);
//                    p.getConfig().set("tport." + player.getName() + ".items." + i + ".location", l);
                    Main.saveLocation(p,"tport." + player.getName() + ".items." + i + ".location", l);
                    p.getConfig().set("tport." + player.getName() + ".items." + i + ".private.statement", false);
                    ArrayList<String> list1 = new ArrayList<>();
                    p.getConfig().set("tport." + player.getName() + ".items." + i + ".private.players", list1);
                    player.sendMessage("§3succesfully added the TPort §9" + args[1]);
                    p.saveConfig();
                    return;
                }
            }
        }
    }
}
