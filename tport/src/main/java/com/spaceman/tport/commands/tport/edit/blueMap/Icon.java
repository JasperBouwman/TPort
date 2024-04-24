package com.spaceman.tport.commands.tport.edit.blueMap;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import com.spaceman.tport.webMaps.BlueMapHandler;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;
import static com.spaceman.tport.fancyMessage.encapsulation.TPortEncapsulation.asTPort;
import static com.spaceman.tport.webMaps.BlueMapHandler.getTPortIconName;

public class Icon extends SubCommand {
    
    private final EmptyCommand emptyIcon;
    
    public Icon() {
        emptyIcon = new EmptyCommand();
        emptyIcon.setCommandName("icon", ArgumentType.OPTIONAL);
        emptyIcon.setCommandDescription(formatInfoTranslation("tport.command.edit.blueMap.icon.icon.commandDescription"));
        emptyIcon.setPermissions("TPort.edit.blueMap.setIcon", "TPort.basic");
        addAction(emptyIcon);
        
        setCommandDescription(formatInfoTranslation("tport.command.edit.blueMap.icon.commandDescription"));
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        if (!emptyIcon.hasPermissionToRun(player, false)) {
            return Collections.emptyList();
        }
        
        try {
            return BlueMapHandler.getBlueMapImages();
        } catch (Exception e) {
            return Collections.emptyList();
        }
        
    }
    @Override
    public void run(String[] args, Player player) {
        // tport edit <TPort name> blueMap icon [icon]
        
        if (args.length == 4) {
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
            if (tport == null) {
                sendErrorTranslation(player, "tport.command.noTPortFound", args[1]);
                return;
            }
            
            // the BlueMap command already checks if BlueMap is loaded, so no error should occur
            String tportIcon = null;
            try {
                tportIcon = getTPortIconName(tport);
            } catch (Exception ignore) { }
            
            sendInfoTranslation(player, "tport.command.edit.blueMap.icon.succeeded", asTPort(tport), tportIcon);
        } else if (args.length == 5) {
            if (!emptyIcon.hasPermissionToRun(player, true)) {
                return;
            }
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
            if (tport == null) {
                sendErrorTranslation(player, "tport.command.noTPortFound", args[1]);
                return;
            }
            if (tport.isOffered()) {
                sendErrorTranslation(player, "tport.command.edit.blueMap.icon.icon.isOffered",
                        asTPort(tport), asPlayer(tport.getOfferedTo()));
                return;
            }
            
            ArrayList<String> images;
            try { images =  BlueMapHandler.getBlueMapImages(); } catch (Exception e) { images = new ArrayList<>(); }
            if (!images.contains(args[4])) {
                sendErrorTranslation(player, "tport.command.edit.blueMap.icon.icon.iconNotFound", args[4]);
                return;
            }
            
            tport.setBlueMapIcon(args[4]);
            tport.save();
            sendSuccessTranslation(player, "tport.command.edit.blueMap.icon.icon.succeeded", tport, args[4]);
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport edit <TPort name> blueMap show [state]");
        }
    }
}
