package com.spaceman.tport.commands.tport.home;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.Home;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.encapsulation.TPortEncapsulation.asTPort;

public class Set extends SubCommand {
    
    EmptyCommand emptySetPlayerTPort;
    
    public Set() {
        emptySetPlayerTPort = new EmptyCommand();
        emptySetPlayerTPort.setCommandName("TPort name", ArgumentType.REQUIRED);
        emptySetPlayerTPort.setCommandDescription(formatInfoTranslation("tport.command.home.set.player.tportName.commandDescription"));
        emptySetPlayerTPort.setPermissions("TPort.home.set", "TPort.basic");
        
        EmptyCommand emptySetPlayer = new EmptyCommand();
        emptySetPlayer.setCommandName("player", ArgumentType.REQUIRED);
        emptySetPlayer.setTabRunnable((args, player) -> {
            UUID argOneUUID = PlayerUUID.getPlayerUUID(args[2]);
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
        emptySetPlayer.addAction(emptySetPlayerTPort);
        addAction(emptySetPlayer);
    }
    
    @Override
    public List<String> tabList(Player player, String[] args) {
        return PlayerUUID.getPlayerNames();
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport home set <player> <TPort name>
        
        if (args.length != 4) {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport home set <player> <TPort>");
            return;
        }
        
        if (!emptySetPlayerTPort.hasPermissionToRun(player, true)) {
            return;
        }
        
        String newPlayerName = args[2];
        UUID newPlayerUUID = PlayerUUID.getPlayerUUID(newPlayerName, player);
        if (newPlayerUUID == null) {
            return;
        }
        
        TPort tport = TPortManager.getTPort(newPlayerUUID, args[3]);
        if (tport == null) {
            sendErrorTranslation(player, "tport.command.noTPortFound", args[3]);
            return;
        }
        
        Home.setHome(player, tport);
        sendSuccessTranslation(player, "tport.command.home.set.player.tportName.succeeded", asTPort(tport));
    }
}
