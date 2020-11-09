package com.spaceman.tport.commands.tport.edit;

import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.commands.tport.edit.whitelist.Add;
import com.spaceman.tport.commands.tport.edit.whitelist.Clone;
import com.spaceman.tport.commands.tport.edit.whitelist.List;
import com.spaceman.tport.commands.tport.edit.whitelist.Remove;
import org.bukkit.entity.Player;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.commandHander.CommandTemplate.runCommands;

public class Whitelist extends SubCommand {
    
    public Whitelist() {
        addAction(new Add());
        addAction(new Remove());
        addAction(new List());
        addAction(new Clone());
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport edit <TPort name> whitelist <add|remove> <players names...>
        // tport edit <TPort name> whitelist list
        // tport edit <TPort name> whitelist clone <TPort name>
        
        if (args.length > 3) {
            if (runCommands(getActions(), args[3], args, player)) {
                return;
            }
        }
        sendErrorTheme(player, "Usage: %s", "/tport edit <TPort name> whitelist <add|remove|list|clone>");
    }
}
