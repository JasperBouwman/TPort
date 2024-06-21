package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commandHandler.CommandTemplate;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.history.Back;
import com.spaceman.tport.commands.tport.history.*;
import com.spaceman.tport.inventories.TPortInventories;
import org.bukkit.entity.Player;

import static com.spaceman.tport.commandHandler.CommandTemplate.runCommands;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;

public class HistoryCommand extends SubCommand {
    
    @Override
    public String getName(String arg) {
        return "history";
    }
    
    public HistoryCommand() {
        addAction(new Back());
        addAction(new Last());
        addAction(new SecondLast());
        addAction(new Chat());
        addAction(new Clear());
        addAction(new Size());
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport history
        // tport history back       teleport to last history element old location
        // tport history last       teleport to last history element new location
        // tport history clear
        
        if (Features.Feature.History.isDisabled())  {
            Features.Feature.History.sendDisabledMessage(player);
            return;
        }
        
        if (args.length == 1) {
            
            TPortInventories.openHistory(player);
            
        } else {
            if (!runCommands(getActions(), args[1], args, player)) {
                sendErrorTranslation(player, "tport.command.wrongUsage", "/tport history " + CommandTemplate.convertToArgs(getActions(), true));
            }
        }
    }
}
