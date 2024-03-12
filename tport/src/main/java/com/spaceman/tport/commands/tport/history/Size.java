package com.spaceman.tport.commands.tport.history;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.history.HistoryEvents;
import org.bukkit.entity.Player;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;

public class Size extends SubCommand {
    
    public Size() {
        EmptyCommand emptySize = new EmptyCommand();
        emptySize.setCommandName("size", ArgumentType.OPTIONAL);
        addAction(emptySize);
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport history size [size]
        
        if (args.length == 2) {
            sendInfoTranslation(player, "size %s", HistoryEvents.getHistorySize());
        } else if (args.length == 3) {
            
            int size;
            try {
                size = Integer.parseInt(args[2]);
            } catch (NumberFormatException nfe) {
                sendErrorTranslation(player, "error");
                return;
            }
            
            if (size < 0) {
                sendErrorTranslation(player, "too small");
            } else if (size == 0) {
                sendErrorTranslation(player, "to disable use features");
            }
            
            HistoryEvents.setHistorySize(size);
            sendSuccessTranslation(player, "success");
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport history size [size]");
        }
    }
}
