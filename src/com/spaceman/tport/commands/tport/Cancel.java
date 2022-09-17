package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.tpEvents.TPEManager;
import org.bukkit.entity.Player;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;

public class Cancel extends SubCommand {
    
    public Cancel() {
        setPermissions("TPort.cancel", "TPort.basic");
    }
    
    @Override
    public Message getCommandDescription() {
        return formatInfoTranslation("tport.command.cancel.commandDescription");
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport cancel
        
        if (args.length != 1) {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport cancel");
            return;
        }
        if (!hasPermissionToRun(player, true)) {
            return;
        }
        
        if (TPEManager.cancelTP(player.getUniqueId())) {
            sendSuccessTranslation(player, "tport.command.cancel.succeeded");
        } else {
            sendErrorTranslation(player, "tport.command.cancel.noTPRequest");
        }
    }
}
