package com.spaceman.tport.commands.tport.publc;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;
import static com.spaceman.tport.fancyMessage.encapsulation.TPortEncapsulation.asTPort;
import static com.spaceman.tport.fileHander.Files.tportData;
import static com.spaceman.tport.tport.TPortManager.getTPort;

public class Remove extends SubCommand {
    
    private static final Remove instance = new Remove();
    public static Remove getInstance() {
        return instance;
    }
    
    private final EmptyCommand emptyAll;
    
    private Remove() {
        EmptyCommand emptyOwn = new EmptyCommand();
        emptyOwn.setCommandName("own TPort name", ArgumentType.REQUIRED);
        emptyOwn.setCommandDescription(formatInfoTranslation("tport.command.public.remove.own.commandDescription"));
    
        emptyAll = new EmptyCommand();
        emptyAll.setCommandName("all TPort name", ArgumentType.REQUIRED);
        emptyAll.setCommandDescription(formatInfoTranslation("tport.command.public.remove.all.commandDescription"));
        emptyAll.setPermissions("TPort.public.remove.all", "TPort.admin.public");
        addAction(emptyOwn);
        addAction(emptyAll);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        ArrayList<String> list = new ArrayList<>();
        
        for (String publicTPortSlot : tportData.getKeys("public.tports")) {
            String tportID = tportData.getConfig().getString("public.tports." + publicTPortSlot, TPortManager.defUUID.toString());
            TPort tport = getTPort(UUID.fromString(tportID));
            if (tport == null) {
                continue;
            }
            if (tport.getOwner().equals(player.getUniqueId())) {
                list.add(tport.getName());
            } else {
                if (emptyAll.hasPermissionToRun(player, false)) {
                    list.add(tport.getName());
                }
            }
        }
        return list;
    }
    
    public static void removePublicTPort(String name, Player player, boolean fromDelete) {
        
        for (String publicTPortSlot : tportData.getKeys("public.tports")) {
            String tportID = tportData.getConfig().getString("public.tports." + publicTPortSlot, TPortManager.defUUID.toString());
            
            TPort tport = getTPort(UUID.fromString(tportID));
            if (tport == null) {
                return;
            }
            if (!tport.getName().equalsIgnoreCase(name)) {
                continue;
            }
            if (tport.getOwner().equals(player.getUniqueId())) {
                if (tport.isOffered()) {
                    sendErrorTranslation(player, "tport.command.public.remove.own.isOffered",
                            asTPort(tport.parseAsPublic(true)), asPlayer(tport.getOfferedTo()));
                    return;
                }
                int publicSlot;
                try {
                    publicSlot = Integer.parseInt(publicTPortSlot) + 1;
                } catch (NumberFormatException nfe) {
                    sendErrorTranslation(player, "tport.command.public.remove.errorInFile", asTPort(tport.parseAsPublic(true)), "TPortData.yml");
                    return;
                }
                tportData.getConfig().set("public.tports." + publicTPortSlot, null);
                tport.setPublicTPort(false);
                tport.save();
                sendSuccessTranslation(player, "tport.command.public.remove.own.succeeded", asTPort(tport));
                while (true) {
                    if (tportData.getConfig().contains("public.tports." + publicSlot)) {
                        String publicTPort = tportData.getConfig().getString("public.tports." + publicSlot, TPortManager.defUUID.toString());
                        tportData.getConfig().set("public.tports." + (publicSlot - 1), publicTPort);
                        tportData.getConfig().set("public.tports." + (publicSlot), null);
                        publicSlot++;
                    } else {
                        break;
                    }
                }
                tportData.saveConfig();
                return;
            } else if (!fromDelete) {
                if (!getInstance().emptyAll.hasPermissionToRun(player, true)) {
                    return;
                }
                
                int publicSlot;
                try {
                    publicSlot = Integer.parseInt(publicTPortSlot) + 1;
                } catch (NumberFormatException nfe) {
                    sendErrorTranslation(player, "tport.command.public.remove.errorInFile", asTPort(tport.parseAsPublic(true)), "TPortData.yml");
                    return;
                }
                tportData.getConfig().set("public.tports." + publicTPortSlot, null);
                
                tport.setPublicTPort(false);
                if (tport.isOffered()) {
                    sendInfoTranslation(Bukkit.getPlayer(tport.getOfferedTo()), "tport.command.public.remove.all.offeredPlayer",
                            asTPort(tport), asPlayer(tport.getOwner()));
                }
                tport.save();
                sendSuccessTranslation(player, "tport.command.public.remove.all.succeeded", asTPort(tport), asPlayer(tport.getOwner()));
                sendInfoTranslation(Bukkit.getPlayer(tport.getOwner()), "tport.command.public.remove.all.owner", asPlayer(player), asTPort(tport));
                
                while (true) {
                    if (tportData.getConfig().contains("public.tports." + publicSlot)) {
                        String publicTPort = tportData.getConfig().getString("public.tports." + publicSlot, TPortManager.defUUID.toString());
                        tportData.getConfig().set("public.tports." + (publicSlot - 1), publicTPort);
                        tportData.getConfig().set("public.tports." + (publicSlot), null);
                        publicSlot++;
                    } else {
                        break;
                    }
                }
                tportData.saveConfig();
                return;
            }
            return;
        }
        if (!fromDelete) sendErrorTranslation(player, "tport.command.public.TPortNotFound", name);
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport public remove <own TPort name|all TPort name>
        
        if (args.length == 3) {
            removePublicTPort(args[2], player, false);
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport public remove <TPort name>");
        }
    }
}