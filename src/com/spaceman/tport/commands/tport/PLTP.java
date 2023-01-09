package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commandHandler.CommandTemplate;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.pltp.*;
import com.spaceman.tport.commands.tport.pltp.Preview;
import org.bukkit.entity.Player;

import static com.spaceman.tport.commandHandler.CommandTemplate.runCommands;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;

public class PLTP extends SubCommand {
    
    public PLTP() {
        addAction(new State());
        addAction(new Consent());
        addAction(new Whitelist());
        addAction(new TP());
        addAction(new Offset());
        addAction(new Preview());
    }
    
    @Override
    public String getName(String arg) {
        return this.getClass().getSimpleName();
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport PLTP state [state]
        // tport PLTP consent [state]
        // tport PLTP whitelist list
        // tport PLTP whitelist <add|remove> <player...>
        // tport PLTP tp <player>
        // tport PLTP offset [offset]
        // tport PLTP preview [state]
        
        if (args.length > 1) {
            if (runCommands(getActions(), args[1], args, player)) {
                return;
            }
        }
        sendErrorTranslation(player, "tport.command.wrongUsage", "/tport PLTP " + CommandTemplate.convertToArgs(getActions(), false));
    }
}
