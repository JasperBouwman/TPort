package com.spaceman.tport.commands.tport.edit;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;
import static com.spaceman.tport.fancyMessage.events.HoverEvent.hoverEvent;

public class Private extends SubCommand {
    
    private Message getEmptyStateCommandDescription() {
        Message message = formatInfoTranslation("tport.command.edit.private.state.commandDescription");
        for (TPort.PrivateState privateState : TPort.PrivateState.values()) {
            message.addText(textComponent("\n"));
            message.addMessage(privateState.getDescription());
        }
        return message;
    }
    
    private final EmptyCommand emptyState;
    
    public Private() {
        emptyState = new EmptyCommand();
        emptyState.setCommandName("state", ArgumentType.OPTIONAL);
        emptyState.setCommandDescription(getEmptyStateCommandDescription());
        emptyState.setPermissions("TPort.edit.private", "TPort.basic");
        addAction(emptyState);
    }
    
    @Override
    public List<String> tabList(Player player, String[] args) {
        return Arrays.stream(TPort.PrivateState.values()).map(s -> s.name().toLowerCase()).collect(Collectors.toList());
    }
    
    @Override
    public Message getCommandDescription() {
        return formatInfoTranslation("tport.command.edit.private.commandDescription");
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport edit <TPort name> private [state]
        
        if (args.length == 3) {
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
            
            if (tport == null) {
                sendErrorTranslation(player, "tport.command.noTPortFound", args[1]);
                return;
            }
            Message hereMessage = formatTranslation(varInfoColor, varInfo2Color, "tport.command.edit.private.here");
            hereMessage.getText().forEach(text -> text
                    .setInsertion("/tport help tport edit <tport name> private <state>")
                    .addTextEvent(hoverEvent(textComponent("/tport help tport edit <tport name> private <state>", infoColor)))
                    .addTextEvent(ClickEvent.runCommand("/tport help tport edit <tport name> private <state>"))
            );
            sendInfoTranslation(player, "tport.command.edit.private.succeeded", tport, tport.getPrivateState(), hereMessage);
        } else if (args.length == 4) {
            if (!emptyState.hasPermissionToRun(player, true)) {
                return;
            }
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
            
            if (tport == null) {
                sendErrorTranslation(player, "tport.command.noTPortFound", args[1]);
                return;
            }
            if (tport.isOffered()) {
                sendErrorTranslation(player, "tport.command.edit.private.state.isOffered",
                        tport, asPlayer(tport.getOfferedTo()));
                return;
            }
            
            TPort.PrivateState ps;
            try {
                ps = TPort.PrivateState.valueOf(args[3].toUpperCase());
            } catch (IllegalArgumentException iae) {
                sendErrorTranslation(player, "tport.command.edit.private.state.stateNotFound", args[3]);
                return;
            }
            if (tport.isPublicTPort()) {
                if (!ps.canGoPublic()) {
                    sendErrorTranslation(player, "tport.command.edit.private.state.isPublic", tport, ps);
                    return;
                }
            }
            tport.setPrivateState(ps);
            tport.save();
            sendSuccessTranslation(player, "tport.command.edit.private.state.succeeded", tport, ps);
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport edit <TPort name> private [state]");
        }
        
    }
}
