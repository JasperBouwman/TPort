package com.spaceman.tport.commandStuff;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public interface CommandInterface {

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args);

}
