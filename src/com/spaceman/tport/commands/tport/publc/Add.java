package com.spaceman.tport.commands.tport.publc;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static com.spaceman.tport.commands.tport.Own.getOwnTPorts;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;
import static com.spaceman.tport.fileHander.Files.tportData;
import static com.spaceman.tport.tport.TPortManager.getTPort;

public class Add extends SubCommand {
    
    private final EmptyCommand emptyTPort;
    
    public Add() {
        emptyTPort = new EmptyCommand();
        emptyTPort.setCommandName("TPort name", ArgumentType.REQUIRED);
        emptyTPort.setCommandDescription(formatInfoTranslation("tport.command.public.add.tport.commandDescription"));
        emptyTPort.setPermissions("TPort.public.add");
        addAction(emptyTPort);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        List<String> list = getOwnTPorts(player);
        if (emptyTPort.hasPermissionToRun(player, false)) {
            for (String publicTPortSlot : tportData.getKeys("public.tports")) {
                String tportID = tportData.getConfig().getString("public.tports." + publicTPortSlot, TPortManager.defUUID.toString());
                TPort tport = getTPort(UUID.fromString(tportID));
                if (tport != null) {
                    if (tport.getOwner().equals(player.getUniqueId())) {
                        list.add(tport.getName());
                    }
                }
            }
        }
        return list;
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport public add <TPort name>
        
        if (!emptyTPort.hasPermissionToRun(player, true)) {
            return;
        }
        
        if (args.length == 3) {
            
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[2]);
            
            if (tport != null) {
                if (tport.isOffered()) {
                    sendErrorTranslation(player, "tport.command.public.add.tport.isOffered",
                            tport, asPlayer(tport.getOfferedTo()));
                    return;
                }
                
                for (int publicSlot = 0; publicSlot < ListSize.getPublicTPortSize(); publicSlot++) {
                    if (!tportData.getConfig().contains("public.tports." + publicSlot)) {
                        tportData.getConfig().set("public.tports." + publicSlot, tport.getTportID().toString());
                        tportData.saveConfig();
                        tport.setPublicTPort(true, player);
                        tport.save();
                        sendSuccessTranslation(player, "tport.command.public.add.tport.succeeded", tport.parseAsPublic(true));
                        return;
                    } else {
                        String tportID = tportData.getConfig().getString("public.tports." + publicSlot, TPortManager.defUUID.toString());
                        TPort tmpTPort = getTPort(UUID.fromString(tportID));
                        
                        if (tmpTPort != null && tmpTPort.getName().equalsIgnoreCase(tport.getName())) {
                            sendErrorTranslation(player, "tport.command.public.add.tport.nameUsed", tport, tmpTPort.parseAsPublic(true));
                            return;
                        }
                    }
                }
                sendErrorTranslation(player, "tport.command.public.add.tport.isFull", tport);
            } else {
                sendErrorTranslation(player, "tport.command.noTPortFound", args[2]);
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport public add <TPort name>");
        }
        
    }
}
