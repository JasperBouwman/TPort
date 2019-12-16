package com.spaceman.tport.commands.tport.edit;

import com.spaceman.tport.colorFormatter.ColorTheme;
import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.spaceman.tport.colorFormatter.ColorTheme.ColorType.infoColor;
import static com.spaceman.tport.colorFormatter.ColorTheme.ColorType.varInfoColor;
import static com.spaceman.tport.colorFormatter.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;

public class Private extends SubCommand {
    
    public Private() {
        EmptyCommand emptyCommand = new EmptyCommand();
        emptyCommand.setCommandName("statement", ArgumentType.OPTIONAL);
        emptyCommand.setCommandDescription(textComponent("This command is used to edit the private statement of the given TPort.", infoColor),
                textComponent("\nStatement ", infoColor),
                textComponent("on", varInfoColor),
                textComponent(" means that only players in your whitelist can teleport to that TPort.", infoColor),
                textComponent("\nStatement ", infoColor),
                textComponent("off", varInfoColor),
                textComponent(" means that all players can teleport to that TPort.", infoColor),
                textComponent("\nStatement ", infoColor),
                textComponent("online", varInfoColor),
                textComponent(" means that all players can teleport to that TPort when your are online, and not when your are offline", infoColor),
                textComponent("\nStatement ", infoColor),
                textComponent("prion", varInfoColor),
                textComponent(" (PRIvate ONline) means that all players can teleport to that TPort when your are online, and not when your are offline. " +
                        "But players in your whitelist can still teleport", infoColor),
                textComponent("\n\nPermissions: ", ColorTheme.ColorType.infoColor), textComponent("TPort.edit.private", ColorTheme.ColorType.varInfoColor),
                textComponent(" or ", ColorTheme.ColorType.infoColor), textComponent("TPort.basic", ColorTheme.ColorType.varInfoColor));
        addAction(emptyCommand);
    }
    
    @Override
    public List<String> tabList(Player player, String[] args) {
        return Arrays.stream(TPort.PrivateStatement.values()).map(s -> s.name().toLowerCase()).collect(Collectors.toList());
    }
    
    @Override
    public Message getCommandDescription() {
        return new Message(textComponent("This command is used to get the private statement of the given TPort", infoColor));
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport edit <TPort> private [statement]
        
        if (args.length == 3) {
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
            
            if (tport == null) {
                sendErrorTheme(player, "No TPort found called %s", args[1]);
                return;
            }
            sendInfoTheme(player, "The TPort %s is %s", tport.getName(), tport.getPrivateStatement().getDisplayName());
        } else if (args.length == 4) {
            if (!hasPermission(player, true, true, "TPort.edit.private", "TPort.basic")) {
                return;
            }
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
            
            if (tport == null) {
                sendErrorTheme(player, "No TPort found called %s", args[1]);
                return;
            }
            if (tport.isOffered()) {
                sendErrorTheme(player, "You can't edit TPort %s while its offered to %s", tport.getName(), PlayerUUID.getPlayerName(tport.getOfferedTo()));
                return;
            }
            
            TPort.PrivateStatement ps;
            try {
                ps = TPort.PrivateStatement.valueOf(args[3].toUpperCase());
            } catch (IllegalArgumentException iae) {
                sendErrorTheme(player, "%s is not a valid private statement", args[3]);
                return;
            }
            if (tport.isPublicTPort()) {
                if (!ps.canGoPublic()) {
                    sendErrorTheme(player, "TPort %s is a Public TPort, Private Statement %s can't go Public", tport.getName(), ps.getDisplayName());
                    return;
                }
            }
            tport.setPrivateStatement(ps);
            tport.save();
            sendSuccessTheme(player, "Successfully set TPort %s to %s", tport.getName(), ps.getDisplayName());
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport edit <TPort name> private [statement]");
        }

    }
}
