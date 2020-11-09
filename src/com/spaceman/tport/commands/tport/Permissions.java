package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.permissions.PermissionHandler;
import org.bukkit.entity.Player;

import java.util.Arrays;

import static com.spaceman.tport.commandHander.CommandTemplate.convertToArgs;
import static com.spaceman.tport.commandHander.CommandTemplate.runCommands;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.infoColor;
import static com.spaceman.tport.permissions.PermissionHandler.isPermissionEnabled;

public class Permissions extends SubCommand {
    
    public Permissions() {
        
        EmptyCommand emptyEnableState = new EmptyCommand();
        emptyEnableState.setCommandName("state", ArgumentType.OPTIONAL);
        emptyEnableState.setCommandDescription(textComponent("This command is used to set if Permissions is enabled", infoColor));
        emptyEnableState.setPermissions("TPort.permissions.enable", "TPort.admin.permissions");
        EmptyCommand emptyEnable = new EmptyCommand(){
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptyEnable.setCommandName("enable", ArgumentType.FIXED);
        emptyEnable.setCommandDescription(textComponent("This command is used to get if Permissions is enabled", infoColor));
        emptyEnable.setTabRunnable(((args, player) -> Arrays.asList("true", "false")));
        emptyEnable.setRunnable(((args, player) -> {
            if (args.length == 2) {
                sendInfoTheme(player, "TPort permissions is %s", (isPermissionEnabled() ? "enabled" : "disabled"));
            } else if (args.length == 3) {
                if (emptyEnableState.hasPermissionToRun(player, true)) {
                    if (args[2].equalsIgnoreCase("true")) {
                        if (PermissionHandler.enablePermissions(true)) {
                            sendSuccessTheme(player, "Successfully %s TPort permissions", "enabled");
                        } else {
                            sendErrorTheme(player, "TPort permissions is already %s", "enabled");
                        }
                    } else if (args[2].equalsIgnoreCase("false")) {
                        if (PermissionHandler.enablePermissions(false)) {
                            sendSuccessTheme(player, "Successfully disabled TPort %s", "disabled");
                        } else {
                            sendErrorTheme(player, "TPort permissions is already %s", "disabled");
                        }
                    } else {
                        sendErrorTheme(player, "Usage: %s", "/tport permissions enable [true|false]");
                    }
                }
            } else {
                sendErrorTheme(player, "Usage: %s", "/tport permissions enable [state]");
            }
        }));
        emptyEnable.addAction(emptyEnableState);
        
        addAction(emptyEnable);
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport permissions enable [state]
    
        if (args.length > 1) {
            if (runCommands(getActions(), args[1], args, player)) {
                return;
            }
        }
        sendErrorTheme(player, "Usage: %s", "/tport permissions " + convertToArgs(getActions(), false));
        
    }
}
