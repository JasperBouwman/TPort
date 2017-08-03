package com.spaceman.tport.commands.tport.actions;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.spaceman.tport.Main;
import com.spaceman.tport.commandStuff.CommandInterface;

public class Open implements CommandInterface {

	private Main p;

	public Open(Main instance) {
		p = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

		Player player = (Player) sender;

		if (args.length == 1) {
			player.sendMessage("§cplease give up a playername");
			return false;
		}
		if (!p.getConfig().contains("tport." + args[1])) {
			player.sendMessage("§cthis isn't a player that has ever been online");
			return false;
		}
		if (args.length == 3) {
			boolean b = true;
			for (int i = 0; i < 7; i++) {
				if (p.getConfig().contains("tport." + args[1] + ".items." + i + ".item")) {
					ItemStack items = p.getConfig().getItemStack("tport." + args[1] + ".items." + i + ".item");
					if (args[2].equals(items.getItemMeta().getDisplayName())) {

						player.closeInventory();
						player.teleport((Location) p.getConfig().get("tport." + args[1] + ".items." + i + ".location"));
						player.sendMessage("§3teleported to §9" + items.getItemMeta().getDisplayName());
						b = false;
					} 
				}
			}
			if (b) {
				player.sendMessage("§cthe TPort §4" + args[2] + "§c doesn't exist");
			}
			return false;
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

		return false;
	}

}
