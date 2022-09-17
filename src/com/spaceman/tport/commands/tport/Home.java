package com.spaceman.tport.commands.tport;

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
import java.util.Collections;
import java.util.UUID;

import static com.spaceman.tport.commands.tport.SafetyCheck.SafetyCheckSource.TPORT_HOME;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatInfoTranslation;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;
import static com.spaceman.tport.fileHander.Files.tportData;

public class Home extends SubCommand {
    
    private final EmptyCommand emptyHomeSafetyCheck;
    
    public Home() {
        emptyHomeSafetyCheck = new EmptyCommand() {
            @Override
            public Message permissionsHover() {
                return formatInfoTranslation("tport.command.home.safetyCheck.permissionHover", "TPort.open", TPORT_HOME.getPermission(), "TPort.basic");
            }
        };
        emptyHomeSafetyCheck.setCommandName("safetyCheck", ArgumentType.OPTIONAL);
        emptyHomeSafetyCheck.setCommandDescription(formatInfoTranslation("tport.command.home.safetyCheck.commandDescription"));
        emptyHomeSafetyCheck.setPermissions("TPort.own", TPORT_HOME.getPermission(), "TPort.basic");
        
        addAction(emptyHomeSafetyCheck);
        setPermissions("TPort.home", "TPort.basic");
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        if (!emptyHomeSafetyCheck.hasPermissionToRun(player, false)) {
            return Collections.emptyList();
        }
        return Arrays.asList("true", "false");
    }
    
    @Override
    public Message getCommandDescription() {
        return formatInfoTranslation("tport.command.home.commandDescription");
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport home [safetyCheck]
        
        //todo
        // tport home
        // tport home <safetyCheck>
        // tport home get
        // tport home set <player> <TPort name>
        
        if (args.length != 1 && args.length != 2) {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport home [safetyCheck]");
            return;
        }
        if (!hasPermissionToRun(player, true)) {
            return;
        }
        
        if (!tportData.getConfig().contains("tport." + player.getUniqueId() + ".home")) {
            sendErrorTranslation(player, "tport.command.home.noHome");
            return;
        }
        String homeID = tportData.getConfig().getString("tport." + player.getUniqueId() + ".home", TPortManager.defUUID.toString());
        TPort tport = TPortManager.getTPort(UUID.fromString(homeID));
        if (tport == null) {
            sendErrorTranslation(player, "tport.command.home.homeNotFound");
            return;
        }
        
        Boolean safetyCheckState;
        if (args.length == 2) {
            if (TPORT_HOME.hasPermission(player, true)) {
                safetyCheckState = Main.toBoolean(args[1]);
                if (safetyCheckState == null) {
                    sendErrorTranslation(player, "tport.command.wrongUsage", "/tport home [true|false]");
                    return;
                }
            } else {
                return;
            }
        } else {
            safetyCheckState = TPORT_HOME.getState(player);
        }
        
        tport.teleport(player, safetyCheckState);
    }
}
