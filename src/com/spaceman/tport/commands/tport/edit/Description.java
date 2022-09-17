package com.spaceman.tport.commands.tport.edit;

import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.edit.description.Get;
import com.spaceman.tport.commands.tport.edit.description.Remove;
import com.spaceman.tport.commands.tport.edit.description.Set;
import org.bukkit.entity.Player;

import static com.spaceman.tport.commandHandler.CommandTemplate.runCommands;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;

public class Description extends SubCommand {
    
    public Description() {
        addAction(new Set());
        addAction(new Remove());
        addAction(new Get());
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport edit <TPort name> description set <description...>
        // tport edit <TPort name> description remove
        // tport edit <TPort name> description get
        
        if (args.length > 3) {
            if (runCommands(getActions(), args[3], args, player)) {
                return;
            }
        }
        sendErrorTranslation(player, "tport.command.wrongUsage", "/tport edit <TPort name> description <set|remove|get>");
    }
}
