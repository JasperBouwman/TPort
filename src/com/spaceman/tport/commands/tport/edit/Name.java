package com.spaceman.tport.commands.tport.edit;

import com.spaceman.tport.colorFormatter.ColorTheme;
import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import static com.spaceman.tport.colorFormatter.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.colorFormatter.ColorTheme.sendSuccessTheme;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;

public class Name extends SubCommand {
    
    public Name() {
        EmptyCommand emptyCommand = new EmptyCommand();
        emptyCommand.setCommandName("new TPort name", ArgumentType.REQUIRED);
        emptyCommand.setCommandDescription(textComponent("This command is used to rename the given TPort", ColorTheme.ColorType.infoColor),
                textComponent("\n\nPermissions: ", ColorTheme.ColorType.infoColor), textComponent("TPort.edit.name", ColorTheme.ColorType.varInfoColor),
                textComponent(" or ", ColorTheme.ColorType.infoColor), textComponent("TPort.basic", ColorTheme.ColorType.varInfoColor));
        addAction(emptyCommand);
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport edit <TPort name> name <new TPort name>
    
        if (!hasPermission(player, true, true, "TPort.edit.name", "TPort.basic")) {
            return;
        }
        if (args.length == 4) {
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
        
            if (tport == null) {
                sendErrorTheme(player, "No TPort found called %s", args[1]);
                return;
            }
            if (tport.isOffered()) {
                sendErrorTheme(player, "You can't edit TPort %s while its offered to %s", tport.getName(), PlayerUUID.getPlayerName(tport.getOfferedTo()));
                return;
            }
            if (TPortManager.getTPort(player.getUniqueId(), args[3]) != null) {
                sendErrorTheme(player, "Name %s is already in use", args[3]);
                return;
            }
            try {
                Long.parseLong(args[3]);
                sendErrorTheme(player, "TPort name can't be a number, but it can contain a number");
                return;
            } catch (NumberFormatException ignore) {
            }
            
            tport.setName(args[3]);
            tport.save();
            sendSuccessTheme(player, "Successfully set new name to %s", args[3]);
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport edit <TPort name> name <new TPort name>");
        }
        
    }
}
