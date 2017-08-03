package com.spaceman.tport.commands.tport.actions;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.spaceman.tport.Main;
import com.spaceman.tport.commandStuff.CommandInterface;

public class Whitelist implements CommandInterface {

	private Main p;

	public Whitelist(Main instance) {
		p = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player player = (Player) sender;
		// tport whitelist <name item> add <player>
		// tport whitelist <name item> remove <player>
		// tport whitelist <name item> list

		if (args.length == 3) {
			
			if (!p.getConfig().contains("tport." + player.getName() + ".items")) {
				player.sendMessage("§cno item found called §4" + args[1]);
				return false;
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
						return false;
					}
				}
			}
		}

		if (args.length != 4) {
			player.sendMessage("§cwrong use, use §4/tport whitelist <name item> [add:remove] <player>");
			return false;
		}

		if (!p.getConfig().contains("tport." + player.getName() + ".items")) {
			player.sendMessage("§cno item found called §4" + args[1]);
			return false;
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
						return false;
					}
					
					if (args[3].equalsIgnoreCase(player.getName())) {
						player.sendMessage("§cyou don't have to put yourself in your whitelist");
						return false;
					}
					
					if (list.contains(args[3])) {
						player.sendMessage("§cthis player is already in you list");
						return false;
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
						return false;
					}

					if (!list.contains(args[3])) {
						player.sendMessage("§cthis player is not in your list");
						return false;
					}

					list.remove(args[3]);
					p.getConfig().set("tport." + player.getName() + ".items." + s + ".private.players", list);
					p.saveConfig();
					player.sendMessage("§3succesfully removed");
					Bukkit.getPlayerExact(args[3]).sendMessage(
							"§3you are removed in the whitelist of §9" + player.getName() + " §3in the item §9" + name);

				} else {
					player.sendMessage("§cwrong use, §4/tport whitelist <name of item> [add:remove] <player>");
					return false;
				}
			}
		}

		if (bool) {
			player.sendMessage("§cno item found called §4" + args[1]);
		}
		return false;
	}
}
