package com.spaceman.tport.commands.tport;

import com.spaceman.tport.Main;
import com.spaceman.tport.commands.CmdHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;

public class Open extends CmdHandler {

    @Override
    public void run(String[] args, Player player) {

        // tport open <playername> [TPort name]

        if (args.length == 1) {
            player.sendMessage("§cuse: §4/tport open <playername> [TPort name]");
            return;
        }
        if (!p.getConfig().contains("tport." + args[1])) {
            player.sendMessage("§cthis isn't a player that has ever been online");
            return;
        }
        if (args.length == 3) {
            boolean b = true;
            for (int i = 0; i < 7; i++) {
                if (p.getConfig().contains("tport." + args[1] + ".items." + i + ".item")) {
                    ItemStack items = p.getConfig().getItemStack("tport." + args[1] + ".items." + i + ".item");
                    if (args[2].equals(items.getItemMeta().getDisplayName())) {

                        player.closeInventory();

                        Location l = Main.getLocation(p,"tport." + args[1] + ".items." + i + ".location");

                        if (l == null) {
                            player.sendMessage("§cthe world for this location has not been found");
                            return;
                        }

                        player.teleport(l);
                        player.sendMessage("§3teleported to §9" + items.getItemMeta().getDisplayName());
                        b = false;
                    }
                }
            }
            if (b) {
                player.sendMessage("§cthe TPort §4" + args[2] + "§c doesn't exist");
            }
            return;
        }
        if (args.length > 3) {
            player.sendMessage("§cuse: §4/tport open <playername> [TPort name]");
            return;
        }

        Inventory invc = Bukkit.createInventory(null, 9, "tport: " + args[1]);
        for (int i = 0; i < 7; i++) {

            if (args[1].equals(player.getName())) {
                invc.setItem(i, p.getConfig().getItemStack("tport." + args[1] + ".items." + i + ".item"));
            } else

            if (p.getConfig().contains("tport." + args[1] + ".items." + i)) {
                if (p.getConfig().getString("tport." + args[1] + ".items." + i + ".private.statement")
                        .equals("false")) {

                    invc.setItem(i, p.getConfig().getItemStack("tport." + args[1] + ".items." + i + ".item"));

                } else if (p.getConfig().getString("tport." + args[1] + ".items." + i + ".private.statement")
                        .equals("true")) {

                    ArrayList<String> list = (ArrayList<String>) p.getConfig()
                            .getStringList("tport." + args[1] + ".items." + i + ".private.players");
                    if (list.contains(player.getName())) {
                        invc.setItem(i, p.getConfig().getItemStack("tport." + args[1] + ".items." + i + ".item"));
                    }
                }
            }
            ItemStack back = new ItemStack(Material.BARRIER);
            ItemMeta metaback = back.getItemMeta();
            metaback.setDisplayName("back");
            back.setItemMeta(metaback);
            invc.setItem(8, back);

            ItemStack warp = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
            SkullMeta skin = (SkullMeta) warp.getItemMeta();
            skin.setOwner(args[1]);

            if (p.getConfig().getString("tport." + args[1] + ".tp.statement").equals("off")) {

                ArrayList<String> list = (ArrayList<String>) p.getConfig()
                        .getStringList("tport." + args[1] + "tp.players");

                if (list.contains(player.getName())) {
                    skin.setDisplayName("warp to " + args[1]);
                } else {
                    skin.setDisplayName("player tp is off");
                }
            } else if (Bukkit.getPlayerExact(args[1]) != null) {
                skin.setDisplayName("warp to " + args[1]);
            } else {
                skin.setDisplayName("player not online");
            }
            warp.setItemMeta(skin);
            invc.setItem(7, warp);
        }
        player.openInventory(invc);

    }
}
