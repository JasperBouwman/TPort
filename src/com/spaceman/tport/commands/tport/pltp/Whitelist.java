package com.spaceman.tport.commands.tport.pltp;

import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.commands.tport.pltp.whitelist.Add;
import com.spaceman.tport.commands.tport.pltp.whitelist.List;
import com.spaceman.tport.commands.tport.pltp.whitelist.Remove;
import org.bukkit.entity.Player;

import static com.spaceman.tport.colorFormatter.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.commandHander.CommandTemplate.runCommands;

public class Whitelist extends SubCommand {

    public Whitelist() {
        addAction(new Add());
        addAction(new Remove());
        addAction(new List());
    }

    @Override
    public void run(String[] args, Player player) {
        // tport PLTP whitelist list
        // tport PLTP whitelist <add|remove> <playername>
        
        if (args.length < 3) {
            sendErrorTheme(player, "Usage: %s or %s", "tport PLTP whitelist <add|remove> <player...>", "/tport PLTP whitelist list");
            return;
        }
        
        if (!runCommands(getActions(), args[2], args, player)) {
            sendErrorTheme(player, "Usage: %s or %s", "tport PLTP whitelist <add|remove> <player...>", "/tport PLTP whitelist list");
        }
    }
}
