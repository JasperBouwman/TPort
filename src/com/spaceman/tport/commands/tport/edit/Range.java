package com.spaceman.tport.commands.tport.edit;

import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.infoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;

public class Range extends SubCommand {
    
    private final EmptyCommand emptyRange;
    
    public Range() {
        emptyRange = new EmptyCommand();
        emptyRange.setCommandName("range", ArgumentType.OPTIONAL);
        emptyRange.setCommandDescription(textComponent("This command is used to edit the range of the given TPort, to turn off set range to ", infoColor),
                textComponent("0", varInfoColor));
        emptyRange.setPermissions("TPort.edit.range", "TPort.basic");
        addAction(emptyRange);
    }
    
    @Override
    public Message getCommandDescription() {
        return new Message(textComponent("This command is used to get the range of the given TPort. " +
                "When a range is set you must be in that range of the TPort for other people to teleport to that TPort, " +
                "the private statement will be still active with a set range", infoColor));
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport edit <TPort name> range [range]
        
        if (args.length == 3) {
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
            
            if (tport == null) {
                sendErrorTheme(player, "No TPort found called %s", args[1]);
                return;
            }
            
            if (tport.hasRange()) {
                sendInfoTheme(player, "TPort %s has no range set", tport.getName());
            } else {
                sendInfoTheme(player, "TPort %s has its range set to %s", tport.getName(), String.valueOf(tport.getRange()));
            }
        } else if (args.length == 4) {
            if (!emptyRange.hasPermissionToRun(player, true)) {
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
    
            int range;
            try {
                range = Integer.parseInt(args[3]);
            } catch (NumberFormatException nfe) {
                sendErrorTheme(player, "%s is not a number", args[3]);
                return;
            }
            tport.setRange(range);
            tport.save();
            if (range == 0) {
                sendSuccessTheme(player, "Successfully set range of TPort %s %s", tport.getName(), "off");
            } else {
                sendSuccessTheme(player, "Successfully set range of TPort %s to %s", tport.getName(), String.valueOf(range));
            }
            
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport edit <TPort name> range [range]");
        }
    }
}
