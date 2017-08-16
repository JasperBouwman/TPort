package com.spaceman.tport.commands.tport;

import com.spaceman.tport.Main;
import com.spaceman.tport.commands.CmdHandler;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class Edit extends CmdHandler {

    @Override
    public void run(String[] args, Player player) {

        // tport edit <name item> lore set <lore>
        // tport edit <name item> lore remove
        // tport edit <name item> name <name of item>
        // tport edit <name item> item
        // tport edit <name item> location
        // tport edit <name item> private [true:false]

        if (args.length <= 2) {
            player.sendMessage("§cuse: §4/tport edit <lore:name:item:location:private>");
            return;
        }

        if (!p.getConfig().contains("tport." + player.getName() + ".items")) {
            player.sendMessage("§cno TPort found called §4" + args[1]);
            return;
        }

        boolean nameb = false;
        for (String s : p.getConfig().getConfigurationSection("tport." + player.getName() + ".items").getKeys(false)) {

            ItemStack item = p.getConfig().getItemStack("tport." + player.getName() + ".items." + s + ".item");
            ItemMeta meta = item.getItemMeta();
            String name = meta.getDisplayName();
            if (name.equalsIgnoreCase(args[1])) {

                if (args[2].equalsIgnoreCase("lore")) {
                    if (args.length == 3) {
                        player.sendMessage("§cuse: §4/tport edit " + args[1] + " lore <new lore of item>");
                        return;
                    }
                    if (args[3].equalsIgnoreCase("set")) {

                        ArrayList<String> list = new ArrayList<>();
                        String lore = args[4];
                        for (int ii = 5; ii <= args.length - 1; ii++) {
                            lore = lore + " " + args[ii];
                        }
                        list.add(lore);
                        meta.setLore(list);
                        item.setItemMeta(meta);
                        p.getConfig().set("tport." + player.getName() + ".items." + s + ".item", item);
                        p.saveConfig();
                        player.sendMessage("§3lore is set to: §9" + lore);
                        return;
                    } else if (args[3].equalsIgnoreCase("remove")) {
                        if (args.length == 4) {
                            ArrayList<String> list = new ArrayList<>();
                            meta.setLore(list);
                            item.setItemMeta(meta);
                            p.getConfig().set("tport." + player.getName() + ".items." + s + ".item", item);
                            p.saveConfig();
                            player.sendMessage("§3lore is successfully removed");
                            return;
                        } else {
                            player.sendMessage("§cuse: §4/tport edit <TPort> lore remove");
                            return;
                        }
                    } else {
                        player.sendMessage("§cuse: §4/tport edit <TPort> lore set <lore of TPort§c or§4 /tport edit <TPort> lore remove");
                        return;
                    }
                } else if (args[2].equalsIgnoreCase("name")) {
                    if (args.length == 4) {

                        meta.setDisplayName(args[3]);
                        item.setItemMeta(meta);
                        p.getConfig().set("tport." + player.getName() + ".items." + s + ".item", item);
                        p.saveConfig();
                        player.sendMessage("§3new name set to " + args[3]);
                        return;
                    } else {
                        player.sendMessage("§cuse: §4/tport edit " + args[1] + " name <new TPort name>");
                        return;
                    }
                } else if (args[2].equalsIgnoreCase("item")) {
                    if (args.length == 3) {
                        ItemStack newitem = new ItemStack(player.getInventory().getItemInMainHand());

                        if (newitem.getItemMeta() == null) {
                            player.sendMessage("§cmust place an item in you main hand");
                            return;
                        }

                        ItemMeta newmeta = newitem.getItemMeta();
                        newmeta.setDisplayName(name);
                        newmeta.setLore(meta.getLore());
                        newitem.setItemMeta(newmeta);

                        p.getConfig().set("tport." + player.getName() + ".items." + s + ".item", newitem);
                        p.saveConfig();

                        player.sendMessage("§3new item set to " + newitem.getType());
                        return;
                    } else {
                        player.sendMessage("§cuse: §4/tport edit <TPort> item");
                        return;
                    }
                } else if (args[2].equalsIgnoreCase("location")) {
                    if (args.length == 3) {
                        Location l = player.getLocation();
//                        p.getConfig().set("tport." + player.getName() + ".items." + s + ".location", l);
                        Main.saveLocation(p,"tport." + player.getName() + ".items." + s + ".location", l);
                        player.sendMessage("§3succesfully edited the location");
                        return;
                    } else {
                        player.sendMessage("§cuse: §4/tport edit <TPort> location");
                        return;
                    }

                } else if (args[2].equalsIgnoreCase("private")) {
                    if (args.length != 4) {
                        player.sendMessage("§cuse: §4/tport edit <TPort> private <true:false>");
                        return;
                    }
                    if (args[3].equalsIgnoreCase("true")) {
                        p.getConfig().set("tport." + player.getName() + ".items." + s + ".private.statement", "true");
                        p.saveConfig();
                        player.sendMessage("§3this TPort is now private");
                        return;
                    } else if (args[3].equalsIgnoreCase("false")) {
                        p.getConfig().set("tport." + player.getName() + ".items." + s + ".private.statement", "false");
                        p.saveConfig();
                        player.sendMessage("§3this TPort is now open");
                        return;
                    }

                } else {
                    player.sendMessage("§cuse: §4/tport edit <lore:name:item:location:private>");
                }
            } else {
                nameb = true;
            }
        }

        if (nameb) {
            player.sendMessage("§cno TPort found called §4" + args[1]);
        }
    }
}
