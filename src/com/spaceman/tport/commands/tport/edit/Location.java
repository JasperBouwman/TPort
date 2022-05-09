package com.spaceman.tport.commands.tport.edit;

import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;

public class Location extends SubCommand {
    
    public Location() {
        setPermissions("TPort.edit.location", "TPort.basic");
    }
    
    @Override
    public Message getCommandDescription() {
        return formatInfoTranslation("tport.command.edit.location.commandDescription");
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport edit <TPort name> location
        
        if (!hasPermissionToRun(player, true)) {
            return;
        }
        if (args.length == 3) {
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
            
            if (tport == null) {
                sendErrorTranslation(player, "tport.command.noTPortFound", args[1]);
                return;
            }
            if (tport.isOffered()) {
                sendErrorTranslation(player, "tport.command.edit.location.isOffered",
                        tport, asPlayer(tport.getOfferedTo()));
                return;
            }
            tport.setLocation(player.getLocation());
            tport.save();
            sendSuccessTranslation(player, "tport.command.edit.location.succeeded", tport);
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport edit <TPort name> name <new TPort name>");
        }
    }
}
