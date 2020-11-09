package com.spaceman.tport.commands.tport.edit;

import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendSuccessTheme;

public class Location extends SubCommand {
    
    public Location() {
        setPermissions("TPort.edit.location", "TPort.basic");
    }
    
    @Override
    public Message getCommandDescription() {
        return new Message(textComponent("This command is used to edit the location of the given TPort, the location of where you are will become the new location",
                ColorTheme.ColorType.infoColor));
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
                sendErrorTheme(player, "No TPort found called %s", args[1]);
                return;
            }
            if (tport.isOffered()) {
                sendErrorTheme(player, "You can't edit TPort %s while its offered to %s", tport.getName(), PlayerUUID.getPlayerName(tport.getOfferedTo()));
                return;
            }
            tport.setLocation(player.getLocation());
            tport.save();
            sendSuccessTheme(player, "Successfully edited the location of TPort %s", tport.getName());
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport edit <TPort name> name <new TPort name>");
        }
    }
}
