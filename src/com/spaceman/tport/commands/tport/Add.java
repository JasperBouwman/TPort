package com.spaceman.tport.commands.tport;

import com.spaceman.tport.Main;
import com.spaceman.tport.commands.CmdHandler;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fileHander.Files;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fileHander.GettingFiles.getFiles;

public class Add extends CmdHandler {

    @Override
    public void run(String[] args, Player player) {

        // tport add <TPort name> [lore of item]

        if (args.length == 1) {
            player.sendMessage("§cUse: §4/tport add <TPort name> [lore of TPort]");
            player.sendMessage("§cName is one word, and the lore can be more");
            return;
        }

        Files tportData = getFiles("TPortData");

        if (args.length == 2) {

            String playerUUID = player.getUniqueId().toString();

            // looks if name is used
            for (int i = 0; i < 10; i++) {
                if (tportData.getConfig().contains("tport." + playerUUID + ".items." + i)) {
                    ItemStack item = tportData.getConfig().getItemStack("tport." + playerUUID + ".items." + i + ".item");
                    ItemMeta meta = item.getItemMeta();
                    if (meta.getDisplayName().equalsIgnoreCase(args[1])) {
                        player.sendMessage("§cThis TPort name is used");
                        return;
                    }
                }
            }

            ItemStack is = new ItemStack(player.getInventory().getItemInMainHand());

            if (is.getItemMeta() == null) {
                player.sendMessage("§cPlace an item in you main hand");
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
                    player.sendMessage("§cYour TPort list is full, remove an old one");
                    return;
                } else if (!tportData.getConfig().contains("tport." + playerUUID + ".items." + i)) {
                    ItemStack item = new ItemStack(player.getInventory().getItemInMainHand());
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName(args[1]);
                    item.setItemMeta(meta);
                    Location l = player.getLocation();
                    tportData.getConfig().set("tport." + playerUUID + ".items." + i + ".item", item);
//                    p.getConfig().set("tport." + player.getName() + ".items." + i + ".location", l);
                    Main.saveLocation("tport." + playerUUID + ".items." + i + ".location", l);

                    tportData.getConfig().set("tport." + playerUUID + ".items." + i + ".private.statement", false);
                    ArrayList<String> list = new ArrayList<>();
                    list.add(playerUUID);
                    tportData.getConfig().set("tport." + playerUUID + ".items." + i + ".private.players", list);
                    Message message = new Message();
                    message.addText("Successfully added the TPort ", ChatColor.DARK_AQUA);
                    message.addText(textComponent(args[1], ChatColor.BLUE, ClickEvent.runCommand("/tport open " + player.getName() + " " + args[1])));
                    message.sendMessage(player);
                    tportData.saveConfig();

                    player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemStack(Material.AIR));
                    return;
                }
            }
        } else if (args.length >= 3) {

            ItemStack is = new ItemStack(player.getInventory().getItemInMainHand());

            if (is.getItemMeta() == null) {
                player.sendMessage("§cPlace an item in you main hand");
                return;
            }

            String playerUUID = player.getUniqueId().toString();

            // looks if name is used
            for (int i = 0; i < 10; i++) {
                if (tportData.getConfig().contains("tport." + playerUUID + ".items." + i)) {
                    ItemStack item = tportData.getConfig().getItemStack("tport." + playerUUID + ".items." + i + ".item");
                    ItemMeta meta = item.getItemMeta();
                    if (meta.getDisplayName().equalsIgnoreCase(args[1])) {
                        player.sendMessage("§cThis TPort name is used");
                        return;
                    }
                }
            }

            // set new item
            for (int i = 0; i < 9; i++) {
                if (i == 8) {
                    player.sendMessage("§cYour TPort list is full, remove an old one");
                    return;
                } else if (!tportData.getConfig().contains("tport." + playerUUID + ".items." + i)) {
                    ItemStack item = new ItemStack(player.getInventory().getItemInMainHand());
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName(args[1]);
                    ArrayList<String> list = new ArrayList<>();

                    StringBuilder lore = new StringBuilder(args[2]);
                    for (int ii = 3; ii <= args.length - 1; ii++) {
                        lore.append(" ").append(args[ii]);
                    }
                    list.add(lore.toString());

                    meta.setLore(list);
                    item.setItemMeta(meta);
                    Location l = player.getLocation();
                    tportData.getConfig().set("tport." + playerUUID + ".items." + i + ".item", item);
//                    p.getConfig().set("tport." + player.getName() + ".items." + i + ".location", l);
                    Main.saveLocation("tport." + playerUUID + ".items." + i + ".location", l);
                    tportData.getConfig().set("tport." + playerUUID + ".items." + i + ".private.statement", false);
                    ArrayList<String> list1 = new ArrayList<>();
                    list1.add(playerUUID);
                    tportData.getConfig().set("tport." + playerUUID + ".items." + i + ".private.players", list1);

                    Message message = new Message();
                    message.addText("Successfully added the TPort ", ChatColor.DARK_AQUA);
                    message.addText(textComponent(args[1], ChatColor.BLUE, ClickEvent.runCommand("/tport open " + player.getName() + " " + args[1])));
                    message.sendMessage(player);

                    player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemStack(Material.AIR));
                    tportData.saveConfig();
                    return;
                }
            }
        }
    }
}
