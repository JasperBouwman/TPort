package com.spaceman.tport.commands.tport;

import com.spaceman.tport.Main;
import com.spaceman.tport.advancements.TPortAdvancement;
import com.spaceman.tport.advancements.TPortAdvancementManager;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.CommandTemplate;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.home.Get;
import com.spaceman.tport.commands.tport.home.Set;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.*;

import static com.spaceman.tport.advancements.TPortAdvancement.Advancement_movingTruckComingThrough;
import static com.spaceman.tport.commandHandler.CommandTemplate.runCommands;
import static com.spaceman.tport.commands.tport.SafetyCheck.SafetyCheckSource.TPORT_HOME;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatInfoTranslation;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;
import static com.spaceman.tport.fileHander.Files.tportData;

public class Home extends SubCommand {
    
    private final EmptyCommand emptyHomeSafetyCheck;
    
    public static boolean hasHome(Player player, boolean checkTPortValidity) {
        if (checkTPortValidity) {
            if (!hasHome(player, false)) {
                return false;
            }
            
            return getHome(player) != null;
        } else {
            return tportData.getConfig().contains("tport." + player.getUniqueId() + ".home");
        }
    }
    
    @Nullable
    public static TPort getHome(Player player) {
        String homeID = tportData.getConfig().getString("tport." + player.getUniqueId() + ".home", null);
        if (homeID == null) {
            return null;
        }
        return TPortManager.getTPort(UUID.fromString(homeID));
    }
    public static void setHome(Player player, TPort home) {
        Advancement_movingTruckComingThrough.grant(player);
        tportData.getConfig().set("tport." + player.getUniqueId() + ".home", home.getTportID().toString());
        tportData.saveConfig();
    }
    
    public Home() {
        emptyHomeSafetyCheck = new EmptyCommand() {
            @Override
            public Message permissionsHover() {
                return formatInfoTranslation("tport.command.home.safetyCheck.permissionHover", "TPort.open", TPORT_HOME.getPermission(), "TPort.basic");
            }
            
            @Override
            public String getName(String argument) {
                if (Main.isTrue(argument) || Main.isFalse(argument)) {
                    return argument;
                }
                return "";
            }
        };
        emptyHomeSafetyCheck.setCommandName("safetyCheck", ArgumentType.OPTIONAL);
        emptyHomeSafetyCheck.setCommandDescription(formatInfoTranslation("tport.command.home.safetyCheck.commandDescription"));
        emptyHomeSafetyCheck.setRunnable(((args, player) -> {
            if (!emptyHomeSafetyCheck.hasPermissionToRun(player, true)) {
                return;
            }
            
            if (!hasHome(player, false)) {
                sendErrorTranslation(player, "tport.command.home.noHome");
                return;
            }
            TPort tport = getHome(player);
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
            
            tport.teleport(player, safetyCheckState, TPortAdvancement.Advancement_homeSweetHome);
        }));
        emptyHomeSafetyCheck.setPermissions("TPort.own", TPORT_HOME.getPermission(), "TPort.basic");
        
        addAction(emptyHomeSafetyCheck);
        addAction(new Get());
        addAction(new Set());
        
        setPermissions("TPort.home", "TPort.basic");
        setCommandDescription(formatInfoTranslation("tport.command.home.commandDescription"));
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        Collection<String> tabList = super.tabList(player, args);
        
        if (!emptyHomeSafetyCheck.hasPermissionToRun(player, false)) {
            tabList.addAll(Arrays.asList("true", "false"));
        }
        
        return tabList;
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport home [safetyCheck]
        //tport home get
        //tport home set <player> <TPort name>
    
        if (args.length == 1) {
            if (!hasPermissionToRun(player, true)) {
                return;
            }
            
            if (!hasHome(player, false)) {
                sendErrorTranslation(player, "tport.command.home.noHome");
                return;
            }
            TPort tport = getHome(player);
            if (tport == null) {
                sendErrorTranslation(player, "tport.command.home.homeNotFound");
                return;
            }
            
            tport.teleport(player, TPORT_HOME.getState(player), TPortAdvancement.Advancement_homeSweetHome);
        } else {
            if (!runCommands(getActions(), args[1], args, player)) {
                sendErrorTranslation(player, "tport.command.wrongUsage", "/tport home " + CommandTemplate.convertToArgs(getActions(), true));
            }
        }
    }
}
