package com.spaceman.tport.commands.tport.actions;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.spaceman.tport.Main;
import com.spaceman.tport.commandStuff.CommandInterface;

public class Remove implements CommandInterface {

	Main p;

	public Remove(Main instance) {
		p = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player player = (Player) sender;

		if (args.length == 1) {
			player.sendMessage("§cuse: §4/tport remove <name of item>");
			return false;
		}
		if (args.length == 2) {
			boolean bool = true;
			for (int i = 0; i <= 10; i++) {
				if (p.getConfig().contains("tport." + player.getName() + ".items." + i)) {
					ItemStack item = p.getConfig().getItemStack("tport." + player.getName() + ".items." + i + ".item");
					ItemMeta meta = item.getItemMeta();
					if (meta.getDisplayName().equalsIgnoreCase(args[1])) {
						p.getConfig().set("tport." + player.getName() + ".items." + i, null);
						p.saveConfig();
						player.sendMessage("§3succesfully removed §9" + args[1]);
						bool = false;
					}
				}
			}

			if (bool) {
				player.sendMessage("§cno item found called §4" + args[1]);
			}

		} else {
			player.sendMessage("§cuse: §4/tport remove <name of item>");
		}

		return false;
	}
}