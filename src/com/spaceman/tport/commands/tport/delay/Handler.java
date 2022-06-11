package com.spaceman.tport.commands.tport.delay;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static com.spaceman.tport.commands.tport.Delay.isPermissionBased;
import static com.spaceman.tport.commands.tport.Delay.setPermissionBased;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfo2Color;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;

public class Handler extends SubCommand {
    
    private final EmptyCommand emptyHandlerState;
    
    public Handler() {
        emptyHandlerState = new EmptyCommand();
        emptyHandlerState.setCommandName("state", ArgumentType.OPTIONAL);
        emptyHandlerState.setCommandDescription(formatInfoTranslation("tport.command.delay.handler.state.commandDescription", "true", "TPort.delay.time.<time in minecraft ticks>"));
        emptyHandlerState.setPermissions("TPort.delay.handler.set", "TPort.admin.delay");
        
        addAction(emptyHandlerState);
        setPermissions("TPort.delay.handler.get", "TPort.admin.delay");
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
        return formatInfoTranslation("tport.command.delay.handler.commandDescription");
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport delay handler [state]
        
        if (args.length == 2) {
            if (!this.hasPermissionToRun(player, true)) {
                return;
            }
            Message stateMessage;
            if (isPermissionBased()) {
                stateMessage = formatTranslation(varInfoColor, varInfo2Color, "tport.command.delay.type.permissions");
                sendInfoTranslation(player, "tport.command.delay.handler.succeeded", stateMessage);
                sendInfoTranslation(player, "tport.command.delay.handler.permission", "TPort.delay.time.<time in minecraft ticks>");
            } else {
                stateMessage = formatTranslation(varInfoColor, varInfo2Color, "tport.command.delay.type.command");
                sendInfoTranslation(player, "tport.command.delay.handler.succeeded", stateMessage);
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
            if (state) {
                stateMessage = formatTranslation(varInfoColor, varInfo2Color, "tport.command.delay.type.permissions");
            } else {
                stateMessage = formatTranslation(varInfoColor, varInfo2Color, "tport.command.delay.type.command");
            }
            sendSuccessTranslation(player, "tport.command.delay.handler.state.succeeded", stateMessage);
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport delay handler [state]");
        }
    }
}
