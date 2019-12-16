package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commandHander.CommandTemplate;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.commands.tport.pltp.*;
import org.bukkit.entity.Player;

import static com.spaceman.tport.colorFormatter.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.commandHander.CommandTemplate.runCommands;

public class PLTP extends SubCommand {
    
    public PLTP() {
        addAction(new State());
        addAction(new Consent());
        addAction(new Accept());
        addAction(new Reject());
        addAction(new Revoke());
        addAction(new Whitelist());
        addAction(new TP());
    }
    
    @Override
    public String getName(String arg) {
        return this.getClass().getSimpleName();
    }
    
    @Override
    public void run(String[] args, Player player) {
        
        // tport PLTP state [state]
        // tport PLTP consent [state]
        // tport PLTP accept [player...]
        // tport PLTP reject <player>
        // tport PLTP revoke <player>
        // tport PLTP whitelist list
        // tport PLTP whitelist <add|remove> <player...>
        // tport PLTP tp <player>
        
        if (args.length > 1) {
            if (runCommands(getActions(), args[1], args, player)) {
                return;
            }
        }
        sendErrorTheme(player, "Usage: %s", "/tport PLTP " + CommandTemplate.convertToArgs(getActions(), false));
    }
}
