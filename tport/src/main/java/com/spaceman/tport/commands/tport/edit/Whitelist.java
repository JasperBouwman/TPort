package com.spaceman.tport.commands.tport.edit;

import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.edit.whitelist.*;
import org.bukkit.entity.Player;

import static com.spaceman.tport.commandHandler.CommandTemplate.runCommands;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;

public class Whitelist extends SubCommand {
    
    public Whitelist() {
        addAction(new Add());
        addAction(new Remove());
        addAction(new List());
        addAction(new Clone());
        addAction(new Visibility());
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport edit <TPort name> whitelist <add|remove> <players names...>
        // tport edit <TPort name> whitelist list
        // tport edit <TPort name> whitelist clone <TPort name>
        // tport edit <tport name> whitelist visibility [state]
        
        if (args.length > 3) {
            if (runCommands(getActions(), args[3], args, player)) {
                return;
            }
        }
        sendErrorTranslation(player, "tport.command.wrongUsage", "/tport edit <TPort name> whitelist <add|remove|list|clone|visibility>");
    }
}
