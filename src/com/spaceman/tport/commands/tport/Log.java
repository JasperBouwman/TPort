package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commands.CmdHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Log extends CmdHandler {

    @Override
    public void run(String[] args, Player player) {

        //tport log start <TPort name> [mode]
        //tport log stop <TPort name>
        //tport log list
        //tport log default <mode>
        //tport log players <TPort name> add <playerName[:mode]>...
        //tport log players <TPort name> addAll
        //tport log players <TPort name> remove <playerName[:mode]>
        //tport log players <TPort name> removeAll
        //tport log players <TPort name> list
        //tport log players <TPort name> default <mode>
        //tport log players <TPort name> setAll <mode>
        //modes: online, offline, all


        player.sendMessage(ChatColor.RED + "This command is not ready yet...");
    }
}
