package com.spaceman.tport.commands.tport;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.spaceman.tport.Main;
import com.spaceman.tport.commandStuff.CommandInterface;

public class TPort implements CommandInterface {

	private Main p;

	public TPort(Main instance) {
		p = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

		Player player = (Player) sender;

		int d = -1;
		p.getConfig().set("tport." + player.getName() + ".gui", d);
		p.saveConfig();

		Inventory inv = Bukkit.createInventory(null, 45, "choose a player (1)");
		int i = 10;

		for (String s : p.getConfig().getConfigurationSection("tport").getKeys(false)) {

			if (i >= 35) {
				ItemStack item = new ItemStack(Material.HOPPER);
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(ChatColor.DARK_AQUA + "next");
				item.setItemMeta(meta);
				inv.setItem(44, item);

				p.getConfig().set("tport." + player.getName() + ".gui", d);
				p.saveConfig();
				break;
			}

			if (i == 17 || i == 26) {
				i = i + 2;
			}
			if (!(i == 44)) {
				inv.setItem(i, p.getConfig().getItemStack("tport." + s + ".item"));
				i++;
			}
		}
		player.openInventory(inv);

		return false;
	}
}
