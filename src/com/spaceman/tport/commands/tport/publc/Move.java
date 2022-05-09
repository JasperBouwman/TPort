package com.spaceman.tport.commands.tport.publc;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.tport.TPortManager.getTPort;

@SuppressWarnings("ConstantConditions")
public class Move extends SubCommand {
    
    public static final EmptyCommand emptySlot = new EmptyCommand();
    
    public Move() {
        emptySlot.setTabRunnable((args, player) -> {
            ArrayList<String> list = new ArrayList<>();
            if (emptySlot.hasPermissionToRun(player, false)) {
                Files tportData = GettingFiles.getFile("TPortData");
                for (String publicTPortSlot : tportData.getKeys("public.tports")) {
                    String tportID = tportData.getConfig().getString("public.tports." + publicTPortSlot, TPortManager.defUUID.toString());
                    TPort tport = getTPort(UUID.fromString(tportID));
                    if (tport != null) {
                        if (!tport.getName().equalsIgnoreCase(args[2])) {
                            list.add(tport.getName());
                            try {
                                int slot = Integer.parseInt(publicTPortSlot) + 1;
                                list.add(String.valueOf(slot));
                            } catch (NumberFormatException ignore) {
                            }
                        }
                    }
                }
            }
            return list;
        });
        emptySlot.setCommandName("slot", ArgumentType.REQUIRED);
        emptySlot.setCommandDescription(formatInfoTranslation("tport.command.public.move.slot.commandDescription"));
        emptySlot.setPermissions("TPort.public.move", "TPort.admin.public");
        
        EmptyCommand emptyTPort = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return "";
            }
        };
        emptyTPort.setCommandName("TPort name", ArgumentType.REQUIRED);
        emptyTPort.setCommandDescription(formatInfoTranslation("tport.command.public.move.TPort.commandDescription"));
        emptyTPort.setPermissions(emptySlot.getPermissions());
        
        addAction(emptySlot);
        addAction(emptyTPort);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        ArrayList<String> list = new ArrayList<>();
        if (emptySlot.hasPermissionToRun(player, false)) {
            Files tportData = GettingFiles.getFile("TPortData");
            for (String publicTPortSlot : tportData.getKeys("public.tports")) {
                String tportID = tportData.getConfig().getString("public.tports." + publicTPortSlot, TPortManager.defUUID.toString());
                TPort tport = getTPort(UUID.fromString(tportID));
                if (tport != null) {
                    list.add(tport.getName());
                }
            }
        }
        return list;
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport public move <TPort name> <slot|TPort name>
        
        if (!emptySlot.hasPermissionToRun(player, true)) {
            return;
        }
        
        if (args.length == 4) {
            
            Files tportData = GettingFiles.getFile("TPortData");
            
            for (String publicTPortSlot : tportData.getKeys("public.tports")) {
                String tportID = tportData.getConfig().getString("public.tports." + publicTPortSlot, TPortManager.defUUID.toString());
                
                TPort tport = getTPort(UUID.fromString(tportID));
                if (tport != null) {
                    if (tport.getName().equalsIgnoreCase(args[2])) {
                        try {
                            int slot = Integer.parseInt(args[3]) - 1;
                            if (tportData.getConfig().contains("public.tports." + slot)) {
                                
                                String tportID2 = tportData.getConfig().getString("public.tports." + slot, TPortManager.defUUID.toString());
                                TPort tport2 = getTPort(UUID.fromString(tportID2));
                                if (tport2 == null) {
                                    tportData.getConfig().set("public.tports." + slot, tportID);
                                    sendSuccessTranslation(player, "tport.command.public.move.succeededMoved", tport, String.valueOf(slot + 1));
                                } else {
                                    tportData.getConfig().set("public.tports." + publicTPortSlot, tportData.getConfig().getString("public.tports." + slot));
                                    tportData.getConfig().set("public.tports." + slot, tportID);
                                    sendSuccessTranslation(player, "tport.command.public.move.succeeded", tport.parseAsPublic(true), tport2.parseAsPublic(true));
                                }
                                tportData.saveConfig();
                            } else {
                                sendErrorTranslation(player, "tport.command.public.move.TPortSlotNotFound", String.valueOf(slot + 1));
                            }
                        } catch (NumberFormatException nfe) {
                            
                            for (String publicTPortSlot2 : tportData.getKeys("public.tports")) {
                                String tportID2 = tportData.getConfig().getString("public.tports." + publicTPortSlot2, TPortManager.defUUID.toString());
                                
                                TPort tport2 = getTPort(UUID.fromString(tportID2));
                                if (tport2 != null) {
                                    if (tport2.getName().equalsIgnoreCase(args[3])) {
                                        tportData.getConfig().set("public.tports." + publicTPortSlot, tportData.getConfig().getString("public.tports." + publicTPortSlot2));
                                        tportData.getConfig().set("public.tports." + publicTPortSlot2, tportID);
                                        sendSuccessTranslation(player, "tport.command.public.move.succeeded", tport.parseAsPublic(true), tport2.parseAsPublic(true));
                                        tportData.saveConfig();
                                        return;
                                    }
                                }
                            }
                            sendErrorTranslation(player, "tport.command.public.TPortNotFound", args[3]);
                        }
                        return;
                    }
                }
            }
            sendErrorTranslation(player, "tport.command.public.TPortNotFound", args[2]);
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport public move <TPort name> <slot|TPort name>");
        }
    }
}
