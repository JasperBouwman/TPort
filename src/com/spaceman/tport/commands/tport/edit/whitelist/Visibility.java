package com.spaceman.tport.commands.tport.edit.whitelist;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.*;
import static com.spaceman.tport.fancyMessage.events.HoverEvent.hoverEvent;

public class Visibility extends SubCommand {
    
    private Message getEmptyCommandDescription() {
        Message message = formatInfoTranslation("tport.command.edit.whitelist.visibility.state.commandDescription");
        for (TPort.WhitelistVisibility states : TPort.WhitelistVisibility.values()) {
            message.addText(textComponent("\n"));
            message.addMessage(states.getDescription());
        }
        return message;
    }
    
    private final EmptyCommand emptyState;
    
    public Visibility() {
        emptyState = new EmptyCommand();
        emptyState.setCommandName("state", ArgumentType.OPTIONAL);
        emptyState.setCommandDescription(getEmptyCommandDescription());
        emptyState.setPermissions("TPort.edit.whitelist.visibility", "TPort.basic");
        addAction(emptyState);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        if (!emptyState.hasPermissionToRun(player, false)) {
            return Collections.emptyList();
        }
        return Arrays.stream(TPort.WhitelistVisibility.values()).map(Enum::name).collect(Collectors.toList());
    }
    
    @Override
    public Message getCommandDescription() {
        return formatInfoTranslation("tport.command.edit.whitelist.visibility.commandDescription");
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport edit <tport name> whitelist visibility [state]
        
        if (args.length == 4) {
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
            
            if (tport == null) {
                sendErrorTranslation(player, "tport.command.noTPortFound", args[1]);
                return;
            }
            Message hereMessage = formatTranslation(varInfoColor, varInfo2Color, "tport.command.edit.private.here");
            hereMessage.getText().forEach(text -> text
                    .setInsertion("/tport help tport edit <tport name> whitelist visibility <state>")
                    .addTextEvent(hoverEvent(textComponent("/tport help tport edit <tport name> whitelist visibility <state>", infoColor)))
                    .addTextEvent(ClickEvent.runCommand("/tport help tport edit <tport name> whitelist visibility <state>"))
            );
            sendInfoTranslation(player, "tport.command.edit.whitelist.visibility.succeeded", tport, tport.getWhitelistVisibility(), hereMessage);
        } else if (args.length == 5) {
            if (!emptyState.hasPermissionToRun(player, true)) {
                return;
            }
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
            if (tport == null) {
                sendErrorTranslation(player, "tport.command.noTPortFound", args[1]);
                return;
            }
            
            TPort.WhitelistVisibility visibility;
            try {
                visibility = TPort.WhitelistVisibility.valueOf(args[4].toUpperCase());
            } catch (IllegalArgumentException iae) {
                sendErrorTranslation(player, "tport.command.edit.whitelist.visibility.state.stateNotFound", args[4]);
                return;
            }
            tport.setWhitelistVisibility(visibility);
            tport.save();
            sendSuccessTranslation(player, "tport.command.edit.whitelist.visibility.state.succeeded", tport, visibility);
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport edit <TPort name> whitelist visibility [state]");
        }
    }
}
