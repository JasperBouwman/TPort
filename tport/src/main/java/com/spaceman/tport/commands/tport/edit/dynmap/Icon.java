package com.spaceman.tport.commands.tport.edit.dynmap;

import com.spaceman.tport.Main;
import com.spaceman.tport.Pair;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.dynmap.DynmapHandler;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;

public class Icon extends SubCommand {
    
    private final EmptyCommand emptyIcon;
    
    public Icon() {
        emptyIcon = new EmptyCommand();
        emptyIcon.setCommandName("icon", ArgumentType.OPTIONAL);
        emptyIcon.setCommandDescription(formatInfoTranslation("tport.command.edit.dynmap.icon.icon.commandDescription"));
        emptyIcon.setPermissions("TPort.edit.dynmap.setIcon", "TPort.basic");
        addAction(emptyIcon);
        
        setCommandDescription(formatInfoTranslation("tport.command.edit.dynmap.icon.commandDescription"));
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        if (!emptyIcon.hasPermissionToRun(player, false)) {
            return Collections.emptyList();
        }
        return Main.getOrDefault(DynmapHandler.getIcons(), new ArrayList<Pair<String, String>>()).stream().map(Pair::getRight).collect(Collectors.toList());
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport edit <TPort name> dynmap icon [icon]
        
        if (args.length == 4) {
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
            if (tport == null) {
                sendErrorTranslation(player, "tport.command.noTPortFound", args[1]);
                return;
            }
            sendInfoTranslation(player, "tport.command.edit.dynmap.icon.succeeded", tport, DynmapHandler.getTPortIconName(tport));
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
                sendErrorTranslation(player, "tport.command.edit.dynmap.icon.icon.isOffered",
                        tport, asPlayer(tport.getOfferedTo()));
                return;
            }
            
            String id = DynmapHandler.iconLabelToID(args[4]);
            
            if (id == null) {
                sendErrorTranslation(player, "tport.command.edit.dynmap.icon.icon.iconNotFound", args[4]);
                return;
            }
            tport.setDynmapIconID(id);
            tport.save();
            sendSuccessTranslation(player, "tport.command.edit.dynmap.icon.icon.succeeded", tport, DynmapHandler.getTPortIconName(tport));
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport edit <TPort name> dynmap show [state]");
        }
    }
}
