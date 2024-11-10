package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.CommandTemplate;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.history.Back;
import com.spaceman.tport.commands.tport.history.*;
import com.spaceman.tport.inventories.TPortInventories;
import org.bukkit.entity.Player;

import static com.spaceman.tport.commandHandler.CommandTemplate.runCommands;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatInfoTranslation;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;

public class HistoryCommand extends SubCommand {
    
    @Override
    public String getName(String arg) {
        return "history";
    }
    
    public HistoryCommand() {
        EmptyCommand empty = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return "";
            }
        };
        empty.setCommandName("", ArgumentType.FIXED);
        empty.setCommandDescription(formatInfoTranslation("tport.command.history.commandDescription"));
        addAction(empty);
        
        addAction(new Back());
        addAction(new TmpName()); //todo rename command
        addAction(new Clear());
        addAction(new Size());
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport history
        // tport history back [filter]
        // tport history tmpName [filter]
        // tport history clear [filter]
        // tport history size [size]
        
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
