package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commandHander.CommandTemplate;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.commands.tport.transfer.*;
import org.bukkit.entity.Player;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;

public class Transfer extends SubCommand {
    
    public Transfer() {
        addAction(new Offer());
        addAction(new Revoke());
        addAction(new Accept());
        addAction(new Reject());
        addAction(new List());
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport transfer offer <player> <TPort name>
        // tport transfer revoke <TPort name>
        // tport transfer accept <player> <TPort name>
        // tport transfer reject <player> <TPort name>
        // tport transfer list
        
        if (args.length > 1) {
            if (CommandTemplate.runCommands(getActions(), args[1], args, player)) {
                return;
            }
        }
        sendErrorTheme(player, "Usage: %s", "/tport transfer <offer|accept|reject|list>");
    }
}
