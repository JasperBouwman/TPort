package com.spaceman.tport.commands.tport.actions;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.spaceman.tport.Main;
import com.spaceman.tport.commandStuff.CommandInterface;

public class Add implements CommandInterface {

	Main p;

	public Add(Main instance) {
		p = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player player = (Player) sender;

		if (args.length == 1) {
			player.sendMessage("§cuse: §4/tport add <name of item> <lore of item>");
			return false;
		}

		if (args.length == 2) {

			// looks if name is used
			for (int i = 0; i < 10; i++) {
				if (p.getConfig().contains("tport." + player.getName() + ".items." + i)) {
					ItemStack item = p.getConfig().getItemStack("tport." + player.getName() + ".items." + i + ".item");
					ItemMeta meta = item.getItemMeta();
					if (meta.getDisplayName().equalsIgnoreCase(args[1])) {
						player.sendMessage("§cthis name is used");
						return false;
					}
				}
			}

			ItemStack is = new ItemStack(player.getInventory().getItemInMainHand());

			if (is.getItemMeta() == null) {
				player.sendMessage("§cplace an item in you main hand");
				return false;
			}

			// ItemMeta ism = is.getItemMeta();
			// if (ism.getDisplayName().equals(null)) {
			// player.sendMessage("place an item in you main hand");
			// return false;
			// }

			// set new item
			for (int i = 0; i < 9; i++) {
				if (i == 8) {
					player.sendMessage("§cyour TPort is full, remove an old one");
					return false;
				} else if (!p.getConfig().contains("tport." + player.getName() + ".items." + i)) {
					ItemStack item = new ItemStack(player.getInventory().getItemInMainHand());
					ItemMeta meta = item.getItemMeta();
					meta.setDisplayName(args[1]);
					item.setItemMeta(meta);
					Location l = player.getLocation();
					p.getConfig().set("tport." + player.getName() + ".items." + i + ".item", item);
					p.getConfig().set("tport." + player.getName() + ".items." + i + ".location", l);
					p.getConfig().set("tport." + player.getName() + ".items." + i + ".private.statement", false);
					ArrayList<String> list = new ArrayList<String>();
					p.getConfig().set("tport." + player.getName() + ".items." + i + ".private.players", list);
					player.sendMessage("§3succesfully added the item §9" + args[1]);
					p.saveConfig();
					return false;
				}
			}
		} else if (args.length >= 3) {
			
			ItemStack is = new ItemStack(player.getInventory().getItemInMainHand());

			if (is.getItemMeta() == null) {
				player.sendMessage("§cplace an item in you main hand");
				return false;
			}

			// looks if name is used
			for (int i = 0; i < 10; i++) {
				if (p.getConfig().contains("tport." + player.getName() + ".items." + i)) {
					ItemStack item = p.getConfig().getItemStack("tport." + player.getName() + ".items." + i + ".item");
					ItemMeta meta = item.getItemMeta();
					if (meta.getDisplayName().equalsIgnoreCase(args[1])) {
						player.sendMessage("§cthis name is used");
						return false;
					}
				}
			}

			// set new item
			for (int i = 0; i < 11; i++) {
				if (i == 10) {
					player.sendMessage("§cyour TPort is full, remove an old one");
					return false;
				} else if (!p.getConfig().contains("tport." + player.getName() + ".items." + i)) {
					ItemStack item = new ItemStack(player.getInventory().getItemInMainHand());
					ItemMeta meta = item.getItemMeta();
					meta.setDisplayName(args[1]);
					ArrayList<String> list = new ArrayList<String>();
					String lore = args[2];
					for (int ii = 3; ii <= args.length - 1; ii++) {
						lore = lore + " " + args[ii];
					}
					list.add(lore);
					meta.setLore(list);
					item.setItemMeta(meta);
					Location l = player.getLocation();
					p.getConfig().set("tport." + player.getName() + ".items." + i + ".item", item);
					p.getConfig().set("tport." + player.getName() + ".items." + i + ".location", l);
					p.getConfig().set("tport." + player.getName() + ".items." + i + ".private.statement", false);
					ArrayList<String> list1 = new ArrayList<String>();
					p.getConfig().set("tport." + player.getName() + ".items." + i + ".private.players", list1);
					player.sendMessage("§3succesfully added the item §9" + args[1]);
					p.saveConfig();
					return false;
				}
			}
		}
		return false;
	}
}