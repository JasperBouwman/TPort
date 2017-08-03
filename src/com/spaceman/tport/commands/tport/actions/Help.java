package com.spaceman.tport.commands.tport.actions;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

//import com.spaceman.tport.Main;
import com.spaceman.tport.commandStuff.CommandInterface;

public class Help implements CommandInterface {

//	private Main p;
//
//	public Help(Main instance) {
//		p = instance;
//	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

		Player player = (Player) sender;
		player.sendMessage("§3welcome by the TPort help centrum");
		player.sendMessage("");
		player.sendMessage("§9/tport §3opens the main gui");
		player.sendMessage("§9/tport open <playername> §3opens the gui of the selected player");
		player.sendMessage("§9/tport open <playername> <tport name> §3teleports you to that tport");
		player.sendMessage("§9/tport add <name> §3adds the item in your main hand with the name <name>");
		player.sendMessage(
				"§9/tport add <name> <lore> §3adds the item in your main hand with the name <name> and the lore <lore>");
		player.sendMessage("§9/tport remove <name> §3removes the item with the name <name>");
		player.sendMessage("§9/tport edit <name> name <new name> §3edits the name");
		player.sendMessage("§9/tport edit <name> lore add <lore> §3edits the lore of the item");
		player.sendMessage("§9/tport edit <name> lore remove §3removed the lore of the item");
		player.sendMessage("§9/tport edit <name> item §3set the item to the item of you main hand");
		player.sendMessage("§9/tport edit <name> location §3edits the location of the item of you main hand");
		player.sendMessage("§9/tport edit <name> private [true:false] §3set to private/open");
		player.sendMessage("§9/tport whitelist <name> [add:remove] <player> §3add/removes a player of the item");
		player.sendMessage("§9/tport whitelist <name> list §3to see all the players in your whitelist of the item");
		player.sendMessage("§9/tport extra item §3set the item of the item in your main hand in the main gui");
		player.sendMessage("§9/tport extra tp [on:off] §3toggels the tp function of your head to warp to you");
		player.sendMessage("§9/tport extra whitelist [add:remove] §3add/removes a player to tp to you");
		player.sendMessage("§9/tport extra whitelist list §3to see all the players in you whitelist");
		return false;
	}
}