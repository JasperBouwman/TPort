package com.spaceman.tport.commands.tport;

import com.spaceman.tport.Main;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;

public class SetHome extends SubCommand {
    EmptyCommand emptyPlayerTPort;
    
    public SetHome() {
        emptyPlayerTPort = new EmptyCommand();
        emptyPlayerTPort.setCommandName("TPort name", ArgumentType.REQUIRED);
        emptyPlayerTPort.setCommandDescription(formatInfoTranslation("tport.command.setHome.commandDescription"));
        emptyPlayerTPort.setPermissions("TPort.setHome", "TPort.basic");
        
        EmptyCommand emptyPlayer = new EmptyCommand();
        emptyPlayer.setCommandName("player", ArgumentType.REQUIRED);
        emptyPlayer.setTabRunnable((args, player) -> {
            ArrayList<String> list = new ArrayList<>();
            
            UUID argOneUUID = PlayerUUID.getPlayerUUID(args[1]);
            if (argOneUUID == null) {
                return Collections.emptyList();
            }
            for (TPort tport : TPortManager.getTPortList(argOneUUID)) {
                Boolean access = tport.hasAccess(player.getUniqueId());
                if (access == null || access) {
                    list.add(tport.getName());
                }
            }
            
            return list;
        });
        emptyPlayer.addAction(emptyPlayerTPort);
        addAction(emptyPlayer);
    }
    
    @Override
    public List<String> tabList(Player player, String[] args) {
        return Main.getPlayerNames();
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport setHome <player> <TPort name>
        
        if (args.length == 3) {
            
            if (!emptyPlayerTPort.hasPermissionToRun(player, true)) {
                return;
            }
            
            Files tportData = getFile("TPortData");
            
            String newPlayerName = args[1];
            UUID newPlayerUUID = PlayerUUID.getPlayerUUID(newPlayerName);
            
            if (newPlayerUUID == null || !tportData.getConfig().contains("tport." + newPlayerUUID)) {
                sendErrorTranslation(player, "tport.command.playerNotFound", newPlayerName);
                return;
            }
            
            TPort tport = TPortManager.getTPort(newPlayerUUID, args[2]);
            if (tport != null) {
                tportData.getConfig().set("tport." + player.getUniqueId() + ".home", tport.getTportID().toString());
                tportData.saveConfig();
                sendSuccessTranslation(player, "tport.command.setHome.succeeded", tport.getName());
            } else {
                sendErrorTranslation(player, "tport.command.noTPortFound", args[2]);
            }
            
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport setHome <player> <TPort>");
        }
    }
}
