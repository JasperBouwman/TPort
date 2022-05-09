package com.spaceman.tport.commands.tport.edit.dynmap;

import com.spaceman.tport.Main;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.infoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfo2Color;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;

public class Show extends SubCommand {
    
    private final EmptyCommand emptyState;
    
    public Show() {
        emptyState = new EmptyCommand();
        emptyState.setCommandName("state", ArgumentType.OPTIONAL);
        emptyState.setCommandDescription(formatInfoTranslation("tport.command.edit.dynmap.show.state.commandDescription"));
        emptyState.setPermissions("TPort.edit.dynmap.setShow", "TPort.basic");
        addAction(emptyState);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return Arrays.asList("true", "false");
    }
    
    @Override
    public Message getCommandDescription() {
        return formatInfoTranslation("tport.command.edit.dynmap.show.commandDescription");
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport edit <TPort name> dynmap show [state]
        
        if (args.length == 4) {
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
            if (tport == null) {
                sendErrorTranslation(player, "tport.command.noTPortFound", args[1]);
                return;
            }
            sendInfoTranslation(player, "tport.command.edit.dynmap.show.succeeded",
                    tport, formatTranslation(infoColor, varInfo2Color, "tport.command.edit.dynmap." + (tport.showOnDynmap() ? "shown" : "hidden")));
        } else if (args.length == 5) {
            if (!emptyState.hasPermissionToRun(player, true)) {
                return;
            }
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
            if (tport == null) {
                sendErrorTranslation(player, "tport.command.noTPortFound", args[1]);
                return;
            }
            if (tport.isOffered()) {
                sendErrorTranslation(player, "tport.command.edit.dynmap.show.state.isOffered",
                        tport, asPlayer(tport.getOfferedTo()));
                return;
            }
            
            Boolean show = Main.toBoolean(args[4]);
            if (show == null) {
                sendErrorTranslation(player, "tport.command.wrongUsage", "/tport edit <TPort name> dynmap show [true|false]");
                return;
            }
            
            tport.showOnDynmap(show);
            sendInfoTranslation(player, "tport.command.edit.dynmap.show.state.succeeded",
                    tport, formatTranslation(infoColor, varInfo2Color, "tport.command.edit.dynmap." + (tport.showOnDynmap() ? "shown" : "hidden")));
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport edit <TPort name> dynmap show [state]");
        }
    }
}
