package com.spaceman.tport.commands.tport.edit;

import com.spaceman.tport.colorFormatter.ColorTheme;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import static com.spaceman.tport.colorFormatter.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.colorFormatter.ColorTheme.sendSuccessTheme;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;

public class Location extends SubCommand {
    
    @Override
    public Message getCommandDescription() {
        return new Message(textComponent("This command is used to edit the location of the given TPort, the location of where you are will become the new location",
                ColorTheme.ColorType.infoColor),
                textComponent("\n\nPermissions: ", ColorTheme.ColorType.infoColor), textComponent("TPort.edit.location", ColorTheme.ColorType.varInfoColor),
                textComponent(" or ", ColorTheme.ColorType.infoColor), textComponent("TPort.basic", ColorTheme.ColorType.varInfoColor));
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport edit <TPort name> location
        
        if (!hasPermission(player, true, true, "TPort.edit.location", "TPort.basic")) {
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
