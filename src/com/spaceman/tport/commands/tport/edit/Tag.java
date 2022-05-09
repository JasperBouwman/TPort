package com.spaceman.tport.commands.tport.edit;

import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.edit.tag.Add;
import com.spaceman.tport.commands.tport.edit.tag.Remove;
import org.bukkit.entity.Player;

import static com.spaceman.tport.commandHandler.CommandTemplate.convertToArgs;
import static com.spaceman.tport.commandHandler.CommandTemplate.runCommands;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;

public class Tag extends SubCommand {
    
    public Tag() {
        addAction(new Add());
        addAction(new Remove());
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport edit <TPort name> tag add <tag>
        // tport edit <TPort name> tag remove <tag>
        
        if (args.length > 3) {
            if (runCommands(getActions(), args[3], args, player)) {
                return;
            }
        }
        sendErrorTranslation(player, "tport.command.wrongUsage", "/tport edit <TPort name> tag " + convertToArgs(getActions(), false));
    }
}
