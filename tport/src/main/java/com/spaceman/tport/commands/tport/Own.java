package com.spaceman.tport.commands.tport;

import com.spaceman.tport.Main;
import com.spaceman.tport.advancements.TPortAdvancement;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.spaceman.tport.inventories.TPortInventories.openTPortGUI;
import static com.spaceman.tport.commands.tport.SafetyCheck.SafetyCheckSource.TPORT_OWN;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatInfoTranslation;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;

public class Own extends SubCommand {
    
    private final EmptyCommand emptyOwnTPort;
    
    public Own() {
        EmptyCommand emptyOwnTPortSafetyCheck = new EmptyCommand() {
            @Override
            public Message permissionsHover() {
                return formatInfoTranslation("tport.command.own.tport.safetyCheck.permissionHover", "TPort.open", TPORT_OWN.getPermission(), "TPort.basic");
            }
        };
        emptyOwnTPortSafetyCheck.setCommandName("safetyCheck", ArgumentType.OPTIONAL);
        emptyOwnTPortSafetyCheck.setCommandDescription(formatInfoTranslation("tport.command.own.tport.safetyCheck.commandDescription"));
        emptyOwnTPortSafetyCheck.setPermissions("TPort.own", TPORT_OWN.getPermission(), "TPort.basic");
        
        emptyOwnTPort = new EmptyCommand();
        emptyOwnTPort.setCommandName("TPort name", ArgumentType.OPTIONAL);
        emptyOwnTPort.setCommandDescription(formatInfoTranslation("tport.command.own.tport.commandDescription"));
        emptyOwnTPort.setTabRunnable(((args, player) -> Arrays.asList("true", "false")));
        emptyOwnTPort.addAction(emptyOwnTPortSafetyCheck);
        emptyOwnTPort.setPermissions("TPort.own", "TPort.basic");
        emptyOwnTPort.setTabRunnable(((args, player) -> {
            if (emptyOwnTPortSafetyCheck.hasPermissionToRun(player, false)) {
                return Arrays.asList("true", "false");
            }
            return Collections.emptyList();
        }));
        
        addAction(emptyOwnTPort);
        
        this.setPermissions(emptyOwnTPort.getPermissions());
        this.setCommandDescription(formatInfoTranslation("tport.command.own.commandDescription"));
    }
    
    public static List<String> getOwnTPorts(Player player) {
        return TPortManager.getTPortList(player.getUniqueId()).stream().map(TPort::getName).collect(Collectors.toList());
    }
    
    @Override
    public List<String> tabList(Player player, String[] args) {
        if (!emptyOwnTPort.hasPermissionToRun(player, false)) {
            return Collections.emptyList();
        }
        return getOwnTPorts(player);
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport own [TPort name] [safetyCheck]
        
        if (args.length == 1) {
            openTPortGUI(player.getUniqueId(), player);
        } else if (args.length == 2 || args.length == 3) {
            if (!emptyOwnTPort.hasPermissionToRun(player, true)) {
                return;
            }
            
            Boolean safetyCheckState;
            if (args.length == 3) {
                if (TPORT_OWN.hasPermission(player, true)) {
                    safetyCheckState = Main.toBoolean(args[2]);
                    if (safetyCheckState == null) {
                        sendErrorTranslation(player, "tport.command.wrongUsage", "/tport own [TPort name] [true|false]");
                        return;
                    }
                } else {
                    return;
                }
            } else {
                safetyCheckState = TPORT_OWN.getState(player);
            }
            
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
            if (tport == null) {
                sendErrorTranslation(player, "tport.command.noTPortFound", args[1]);
                return;
            }
            tport.teleport(player, safetyCheckState, TPortAdvancement.Advancement_familiar);
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport own [TPort name] [safetyCheck]");
        }
    }
}
