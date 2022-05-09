package com.spaceman.tport.commands.tport.restriction;

import com.spaceman.tport.Main;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fileHander.Files;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfo2Color;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfoColor;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;

public class Permission extends SubCommand {
    
    private final EmptyCommand emptyPermissionState;
    
    public Permission() {
        emptyPermissionState = new EmptyCommand();
        emptyPermissionState.setCommandName("state", ArgumentType.OPTIONAL);
        emptyPermissionState.setCommandDescription(formatInfoTranslation("tport.command.restriction.permission.state.commandDescription",
                "true", "TPort.restriction.type.<restriction name>"));
        emptyPermissionState.setPermissions("TPort.restriction.permission.set", "TPort.admin.restriction");
        
        addAction(emptyPermissionState);
        setPermissions("TPort.restriction.permission.get", "TPort.admin.restriction");
    }
    
    @Override
    public Message getCommandDescription() {
        return formatInfoTranslation("tport.command.restriction.permission.commandDescription");
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return Arrays.asList("true", "false");
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport restriction permission [state]
        
        if (args.length == 2) {
            if (hasPermissionToRun(player, true)) {
                Files tportConfig = getFile("TPortConfig");
                Message stateMessage;
                if (tportConfig.getConfig().getBoolean("restriction.permission", false)) {
                    stateMessage = formatTranslation(varInfoColor, varInfo2Color, "tport.command.restriction.permission.permission");
                } else {
                    stateMessage = formatTranslation(varInfoColor, varInfo2Color, "tport.command.restriction.permission.command");
                }
                sendInfoTranslation(player, "tport.command.restriction.permission.succeeded", stateMessage);
            }
        } else if (args.length == 3) {
            if (emptyPermissionState.hasPermissionToRun(player, true)) {
                Files tportConfig = getFile("TPortConfig");
                tportConfig.getConfig().set("restriction.permission", Main.isTrue(args[2]));
                tportConfig.saveConfig();
                
                Message stateMessage;
                if (Main.isTrue(args[2])) {
                    stateMessage = formatTranslation(varInfoColor, varInfo2Color, "tport.command.restriction.permission.permission");
                } else {
                    stateMessage = formatTranslation(varInfoColor, varInfo2Color, "tport.command.restriction.permission.command");
                }
                sendInfoTranslation(player, "tport.command.restriction.permission.state.succeeded", stateMessage);
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport restriction permission [state]");
        }
    }
}
