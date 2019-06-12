package com.spaceman.tport.commands.tport.log;

import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.logbook.Logbook;
import org.bukkit.entity.Player;

public class Clear extends SubCommand {

    @Override
    public void run(String[] args, Player player) {
        //tport log clear [TPort name...]

        if (args.length == 2) {

        } else if (args.length > 2) {

        } else {
            player.sendMessage("§cUse: §4/tport log clear [TPort name...]");
        }

    }
}
