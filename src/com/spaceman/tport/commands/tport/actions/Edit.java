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

public class Edit implements CommandInterface {

	private Main p;

	public Edit(Main instance) {
		p = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player player = (Player) sender;

		// tport edit <name item> lore set <lore>
		// tport edit <name item> lore remove
		// tport edit <name item> name <name of item>
		// tport edit <name item> item
		// tport edit <name item> location
		// tport edit <name item> private [true:false]

		if (args.length <= 2) {
			player.sendMessage("§cwrong use, use §4/tport help §cfor all the uses");
			return false;
		}
		
		if (!p.getConfig().contains("tport." + player.getName() + ".items")) {
			player.sendMessage("§cno item found called §4" + args[1]);
			return false;
		}
		
		boolean nameb = false;
		for (String s : p.getConfig().getConfigurationSection("tport." + player.getName() + ".items").getKeys(false)) {

			ItemStack item = p.getConfig().getItemStack("tport." + player.getName() + ".items." + s + ".item");
			ItemMeta meta = item.getItemMeta();
			String name = meta.getDisplayName();
			if (name.equalsIgnoreCase(args[1])) {

				if (args[2].equalsIgnoreCase("lore")) {
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
						return false;
					} else if (args[3].equalsIgnoreCase("remove")) {
						if (args.length == 4) {
							ArrayList<String> list = new ArrayList<>();
							meta.setLore(list);
							item.setItemMeta(meta);
							p.getConfig().set("tport." + player.getName() + ".items." + s + ".item", item);
							p.saveConfig();
							player.sendMessage("§3lore is succesfully removed");
							return false;
						} else {
							player.sendMessage("§cwrong use, use §4/tport edit <name> remove");
							return false;
						}
					} else {
						player.sendMessage("§cwrong use, use §4/tport help §cfor more help");
						return false;
					}
				} else if (args[2].equalsIgnoreCase("name")) {
					if (args.length == 4) {

						meta.setDisplayName(args[3]);
						item.setItemMeta(meta);
						p.getConfig().set("tport." + player.getName() + ".items." + s + ".item", item);
						p.saveConfig();
						player.sendMessage("§3new name set to " + args[3]);
						return false;
					} else {
						player.sendMessage("§cwrong use, use §4/tport edit " + args[1] + " <new name of item>");
						return false;
					}
				} else if (args[2].equalsIgnoreCase("item")) {
					if (args.length == 3) {
						ItemStack newitem = new ItemStack(player.getInventory().getItemInMainHand());

						if (newitem.getItemMeta() == null) {
							player.sendMessage("§cmust place an item in you main hand");
							return false;
						}

						ItemMeta newmeta = newitem.getItemMeta();
						newmeta.setDisplayName(name);
						newmeta.setLore(meta.getLore());
						newitem.setItemMeta(newmeta);

						p.getConfig().set("tport." + player.getName() + ".items." + s + ".item", newitem);
						p.saveConfig();

						player.sendMessage("§3new item set to " + newitem.getType());
						return false;
					} else {
						player.sendMessage("§cwrong use, use §4/tport help 4for more help");
						return false;
					}
				} else if (args[2].equalsIgnoreCase("location")) {
					if (args.length == 3) {
						Location l = player.getLocation();
						p.getConfig().set("tport." + player.getName() + ".items." + s + ".location", l);
						player.sendMessage("§3succesfully edited the location");
						return false;
					} else {
						player.sendMessage("§cwrong use location, use §4/tport edit <name item> location");
						return false;
					}

				} else if (args[2].equalsIgnoreCase("private")) {
					if (args.length != 4) {
						player.sendMessage("§cwrong use, use §4/tport help 4for more help");
						return false;
					}

					if (args[3].equalsIgnoreCase("true")) {
						p.getConfig().set("tport." + player.getName() + ".items." + s + ".private.statement", "true");
						p.saveConfig();
						player.sendMessage("§3item is now private");
						return false;
					} else if (args[3].equalsIgnoreCase("false")) {
						p.getConfig().set("tport." + player.getName() + ".items." + s + ".private.statement", "false");
						p.saveConfig();
						player.sendMessage("§3item is now open");
						return false;
					}

				} else {
					player.sendMessage("§cwrong use, use §4/tport help 4for more help");
				}
			} else {
				nameb = true;
			}
		}

		if (nameb) {
			player.sendMessage("§cno item found called §4" + args[1]);
			return false;
		}

		return false;
	}
}