package com.spaceman.tport.commands.tport.edit.dynmap;

import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;

public class Show extends SubCommand {
    
    private final EmptyCommand emptyState;
    
    public Show() {
        emptyState = new EmptyCommand();
        emptyState.setCommandName("state", ArgumentType.OPTIONAL);
        emptyState.setCommandDescription(textComponent("This command is used to show/hide the given TPort on Dynmap", ColorType.infoColor));
        emptyState.setPermissions("TPort.edit.dynmap.setShow", "TPort.basic");
        addAction(emptyState);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return Arrays.asList("true", "false");
    }
    
    @Override
    public Message getCommandDescription() {
        return new Message(textComponent("This command is used to see if the given TPort is shown/hidden on Dynmap", ColorType.infoColor));
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport edit <TPort name> dynmap show [state]
        
        if (args.length == 4) {
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
            if (tport == null) {
                sendErrorTheme(player, "No TPort found called %s", args[1]);
                return;
            }
            sendInfoTheme(player, "TPort %s is shown on Dynmap", tport.getName());
        } else if (args.length == 5) {
            if (!emptyState.hasPermissionToRun(player, true)) {
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
    
            boolean show = Boolean.parseBoolean(args[4]);
            tport.showOnDynmap(show);
    
            if (show) {
                sendSuccessTheme(player, "Successfully show TPort %s on Dynmap", tport.getName());
            } else {
                sendSuccessTheme(player, "Successfully hide TPort %s on Dynmap", tport.getName());
            }
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport edit <TPort name> dynmap show [state]");
        }
    }
}
