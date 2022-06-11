package com.spaceman.tport.commands.tport.restriction;

import com.spaceman.tport.Main;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static com.spaceman.tport.commands.tport.Restriction.isPermissionBased;
import static com.spaceman.tport.commands.tport.Restriction.setPermissionBased;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfo2Color;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfoColor;

public class Handler extends SubCommand {
    
    private final EmptyCommand emptyHandlerState;
    
    public Handler() {
        emptyHandlerState = new EmptyCommand();
        emptyHandlerState.setCommandName("state", ArgumentType.OPTIONAL);
        emptyHandlerState.setCommandDescription(formatInfoTranslation("tport.command.restriction.handler.state.commandDescription", "true", "TPort.restriction.type.<restriction name>"));
        emptyHandlerState.setPermissions("TPort.restriction.handler.set", "TPort.admin.restriction");
        
        addAction(emptyHandlerState);
        setPermissions("TPort.restriction.handler.get", "TPort.admin.restriction");
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        if (emptyHandlerState.hasPermissionToRun(player, false)) {
            return Arrays.asList("permissions", "command");
        } else {
            return Collections.emptyList();
        }
    }
    
    @Override
    public Message getCommandDescription() {
        return formatInfoTranslation("tport.command.restriction.handler.commandDescription");
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport restriction handler [state]
        
        if (args.length == 2) {
            if (!hasPermissionToRun(player, true)) {
                return;
            }
            Message stateMessage;
            if (isPermissionBased()) {
                stateMessage = formatTranslation(varInfoColor, varInfo2Color, "tport.command.restriction.type.permission");
                sendInfoTranslation(player, "tport.command.restriction.handler.succeeded", stateMessage);
                sendInfoTranslation(player, "tport.command.restriction.handler.permission", "TPort.restriction.type.<restriction name>");
            } else {
                stateMessage = formatTranslation(varInfoColor, varInfo2Color, "tport.command.restriction.type.command");
                sendInfoTranslation(player, "tport.command.restriction.handler.succeeded", stateMessage);
            }
        } else if (args.length == 3) {
            if (!emptyHandlerState.hasPermissionToRun(player, true)) {
                return;
            }
            
            boolean state;
            if (args[2].equalsIgnoreCase("permissions")) {
                state = true;
            } else if (args[2].equalsIgnoreCase("command")) {
                state = false;
            } else {
                sendErrorTranslation(player, "tport.command.wrongUsage", "/tport delay handler [permissions|command]");
                return;
            }
            setPermissionBased(state);
            
            Message stateMessage;
            if (Main.isTrue(args[2])) {
                stateMessage = formatTranslation(varInfoColor, varInfo2Color, "tport.command.restriction.type.permission");
            } else {
                stateMessage = formatTranslation(varInfoColor, varInfo2Color, "tport.command.restriction.type.command");
            }
            sendInfoTranslation(player, "tport.command.restriction.handler.state.succeeded", stateMessage);
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport restriction handler [state]");
        }
    }
}
