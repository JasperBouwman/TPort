package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.encapsulation.TPortEncapsulation.asTPort;
import static com.spaceman.tport.fileHander.Files.tportData;

public class SetHome extends SubCommand {
    EmptyCommand emptySetHomePlayerTPort;
    
    public SetHome() {
        EmptyCommand emptySetHome = new EmptyCommand(){
            @Override
            public String getName(String argument) {
                return "";
            }
        };
        emptySetHome.setCommandName("", ArgumentType.FIXED);
        emptySetHome.setCommandDescription(formatInfoTranslation("tport.command.setHome.commandDescription"));
        
        emptySetHomePlayerTPort = new EmptyCommand();
        emptySetHomePlayerTPort.setCommandName("TPort name", ArgumentType.REQUIRED);
        emptySetHomePlayerTPort.setCommandDescription(formatInfoTranslation("tport.command.setHome.player.tportName.commandDescription"));
        emptySetHomePlayerTPort.setPermissions("TPort.setHome", "TPort.basic");
        
        EmptyCommand emptySetHomePlayer = new EmptyCommand();
        emptySetHomePlayer.setCommandName("player", ArgumentType.REQUIRED);
        emptySetHomePlayer.setTabRunnable((args, player) -> {
            UUID argOneUUID = PlayerUUID.getPlayerUUID(args[1]);
            if (argOneUUID == null) {
                return Collections.emptyList();
            }
            ArrayList<String> list = new ArrayList<>();
            for (TPort tport : TPortManager.getTPortList(argOneUUID)) {
                Boolean access = tport.hasAccess(player.getUniqueId());
                if (access == null || access) {
                    list.add(tport.getName());
                }
            }
            
            return list;
        });
        emptySetHomePlayer.addAction(emptySetHomePlayerTPort);
        addAction(emptySetHome);
        addAction(emptySetHomePlayer);
    }
    
    @Override
    public List<String> tabList(Player player, String[] args) {
        return PlayerUUID.getPlayerNames();
    }
    
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
        String homeID = tportData.getConfig().getString("tport." + player.getUniqueId() + ".home", TPortManager.defUUID.toString());
        return TPortManager.getTPort(UUID.fromString(homeID));
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport setHome
        //tport setHome <player> <TPort name>
        
        if (args.length == 1) {
            if (!hasHome(player, false)) {
                sendErrorTranslation(player, "tport.command.setHome.noHome");
                return;
            }
            TPort tport = getHome(player);
            if (tport == null) {
                sendErrorTranslation(player, "tport.command.setHome.homeNotFound");
                return;
            }
            
            sendInfoTranslation(player, "tport.command.setHome.succeeded", tport);
        }
        else if (args.length == 3) {
            if (!emptySetHomePlayerTPort.hasPermissionToRun(player, true)) {
                return;
            }
            
            String newPlayerName = args[1];
            UUID newPlayerUUID = PlayerUUID.getPlayerUUID(newPlayerName, player);
            if (newPlayerUUID == null) {
                return;
            }
            
            TPort tport = TPortManager.getTPort(newPlayerUUID, args[2]);
            if (tport == null) {
                sendErrorTranslation(player, "tport.command.noTPortFound", args[2]);
                return;
            }
            
            tportData.getConfig().set("tport." + player.getUniqueId() + ".home", tport.getTportID().toString());
            tportData.saveConfig();
            sendSuccessTranslation(player, "tport.command.setHome.player.tportName.succeeded", asTPort(tport));
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport setHome [<player> <TPort>]");
        }
    }
}
