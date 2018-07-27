package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commands.CmdHandler;
import com.spaceman.tport.commands.TPort;
import org.bukkit.entity.Player;

public class Own extends CmdHandler {

    @Override
    public void run(String[] args, Player player) {
        if (args.length == 1) {
            TPort.open.run(new String[]{"open", player.getName()}, player);
        } else if (args.length == 2) {
            TPort.open.run(new String[]{"open", player.getName(), args[1]}, player);
        } else {
            player.sendMessage("§cUse: §4/tport own [TPort name]");
        }
    }
}
