package com.spaceman.tport.commands.tport.publc;

import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static com.spaceman.tport.commands.tport.Own.getOwnTPorts;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendSuccessTheme;
import static com.spaceman.tport.tport.TPortManager.getTPort;

public class Add extends SubCommand {
    
    private final EmptyCommand emptyTPort;
    
    public Add() {
        emptyTPort = new EmptyCommand();
        emptyTPort.setCommandName("TPort name", ArgumentType.REQUIRED);
        emptyTPort.setCommandDescription(TextComponent.textComponent("This command is used to add the given TPort to the Public TPort list", ColorTheme.ColorType.infoColor));
        emptyTPort.setPermissions("TPort.public.add");
        addAction(emptyTPort);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        List<String> list = getOwnTPorts(player);
        if (emptyTPort.hasPermissionToRun(player, false)) {
            Files tportData = GettingFiles.getFile("TPortData");
            for (String publicTPortSlot : tportData.getKeys("public.tports")) {
                String tportID = tportData.getConfig().getString("public.tports." + publicTPortSlot, TPortManager.defUUID.toString());
                //noinspection ConstantConditions
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
                Files tportData = GettingFiles.getFile("TPortData");
                if (tport.isOffered()) {
                    sendErrorTheme(player, "You can't make TPort %s public while its offered to player %s", tport.getName(), PlayerUUID.getPlayerName(tport.getOfferedTo()));
                    return;
                }
                
                for (int publicSlot = 0; publicSlot < ListSize.getPublicTPortSize(); publicSlot++) {
                    if (!tportData.getConfig().contains("public.tports." + publicSlot)) {
                        tportData.getConfig().set("public.tports." + publicSlot, tport.getTportID().toString());
                        tportData.saveConfig();
                        tport.setPublicTPort(true, player);
                        tport.save();
                        sendSuccessTheme(player, "Successfully made TPort %s public", tport.getName());
                        return;
                    } else {
                        String tportID = tportData.getConfig().getString("public.tports." + publicSlot, TPortManager.defUUID.toString());
                        //noinspection ConstantConditions
                        TPort tmpTPort = getTPort(UUID.fromString(tportID));
                        
                        if (tmpTPort != null && tmpTPort.getName().equalsIgnoreCase(tport.getName())) {
                            sendErrorTheme(player, "TPort %s name is already used", tport.getName());
                            return;
                        }
                    }
                }
                sendErrorTheme(player, "The Public TPort list is full, could not make TPort %s public", tport.getName());
            } else {
                sendErrorTheme(player, "No TPort found called %s", args[2]);
            }
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport public add <TPort name>");
        }
        
    }
}
