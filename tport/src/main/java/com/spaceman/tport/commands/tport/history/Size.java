package com.spaceman.tport.commands.tport.history;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.history.TeleportHistory;
import org.bukkit.entity.Player;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;

public class Size extends SubCommand {
    
    private final EmptyCommand emptySize;
    
    public Size() {
        emptySize = new EmptyCommand();
        emptySize.setCommandName("size", ArgumentType.OPTIONAL);
        emptySize.setCommandDescription(formatInfoTranslation("tport.command.history.size.size.commandDescription"));
        emptySize.setPermissions("tport.history.size", "tport.admin");
        addAction(emptySize);
        
        setCommandDescription(formatInfoTranslation("tport.command.history.size.commandDescription"));
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport history size [size]
        
        if (args.length == 2) {
            sendInfoTranslation(player, "tport.command.history.size.succeeded", TeleportHistory.getHistorySize());
        } else if (args.length == 3) {
            
            if (emptySize.hasPermissionToRun(player, true)) {
                return;
            }
            
            int size;
            try {
                size = Integer.parseInt(args[2]);
            } catch (NumberFormatException nfe) {
                sendErrorTranslation(player, "tport.command.history.size.size.notAValidNumber");
                return;
            }
            
            if (size < 0) {
                sendErrorTranslation(player, "tport.command.history.size.size.smallerThan0");
            } else if (size == 0) {
                sendErrorTranslation(player, "tport.command.history.size.size.disable", "/tport features History state false");
            }
            
            TeleportHistory.setHistorySize(size);
            sendSuccessTranslation(player, "tport.command.history.size.size.succeeded", TeleportHistory.getHistorySize());
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport history size [size]");
        }
    }
}
