package com.spaceman.tport.commands.tport.publc;

import com.spaceman.tport.colorFormatter.ColorTheme;
import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import static com.spaceman.tport.colorFormatter.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;
import static com.spaceman.tport.tport.TPortManager.getTPort;

public class Remove extends SubCommand {
    
    public Remove() {
        EmptyCommand emptyCommand1 = new EmptyCommand();
        emptyCommand1.setCommandName("own TPort name", ArgumentType.REQUIRED);
        emptyCommand1.setCommandDescription(TextComponent.textComponent("This command is used to remove a Public TPort that is yours", ColorType.infoColor));
        EmptyCommand emptyCommand2 = new EmptyCommand();
        emptyCommand2.setCommandName("all TPort name", ArgumentType.REQUIRED);
        emptyCommand2.setCommandDescription(TextComponent.textComponent("This command is used to remove a Public TPort that is not yours", ColorType.infoColor),
                textComponent("\n\nPermissions: ", ColorTheme.ColorType.infoColor), textComponent("TPort.public.remove.all", ColorTheme.ColorType.varInfoColor),
                textComponent(" or ", ColorTheme.ColorType.infoColor), textComponent("TPort.admin.public", ColorTheme.ColorType.varInfoColor));
        addAction(emptyCommand1);
        addAction(emptyCommand2);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        ArrayList<String> list = new ArrayList<>();
        Files tportData = GettingFiles.getFile("TPortData");
        
        for (String publicTPortSlot : tportData.getKeys("public.tports")) {
            String tportID = tportData.getConfig().getString("public.tports." + publicTPortSlot, TPortManager.defUUID.toString());
            TPort tport = getTPort(UUID.fromString(tportID));
            if (tport != null) {
                if (tport.getOwner().equals(player.getUniqueId())) {
                    list.add(tport.getName());
                } else {
                    if (hasPermission(player, false, "TPort.public.remove.all", "TPort.admin.public")) {
                        list.add(tport.getName());
                    }
                }
            }
        }
        return list;
    }
    
    public static void removePublicTPort(String name, Player player, boolean fromDelete) {
    
        Files tportData = GettingFiles.getFile("TPortData");
    
        for (String publicTPortSlot : tportData.getKeys("public.tports")) {
            String tportID = tportData.getConfig().getString("public.tports." + publicTPortSlot, TPortManager.defUUID.toString());
        
            TPort tport = getTPort(UUID.fromString(tportID));
            if (tport != null) {
                if (tport.getName().equalsIgnoreCase(name)) {
                    if (tport.getOwner().equals(player.getUniqueId())) {
                        if (tport.isOffered()) {
                            sendErrorTheme(player, "You can't make TPort %s private while its offered to %s", tport.getName(), PlayerUUID.getPlayerName(tport.getOfferedTo()));
                            return;
                        }
                        int publicSlot;
                        try {
                            publicSlot = Integer.parseInt(publicTPortSlot) + 1;
                        } catch (NumberFormatException nfe) {
                            sendErrorTheme(player, "Can't make TPort %s private, there is an error in the file %s", tport.getName(), "TPortData.yml");
                            return;
                        }
                        tportData.getConfig().set("public.tports." + publicTPortSlot, null);
                        tport.setPublicTPort(false);
                        tport.save();
                        sendSuccessTheme(player, "Successfully made TPort %s private", tport.getName());
                        while (true) {
                            if (tportData.getConfig().contains("public.tports." + publicSlot)) {
                                String publicTPort = tportData.getConfig().getString("public.tports." + publicSlot, "s;s");
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
                        if (hasPermission(player, true, "TPort.public.remove.all", "TPort.admin.public")) {
                            int publicSlot;
                            try {
                                publicSlot = Integer.parseInt(publicTPortSlot) + 1;
                            } catch (NumberFormatException nfe) {
                                sendErrorTheme(player, "Can't make TPort %s private, there is an error in the file %s", tport.getName(), "TPortData.yml");
                                return;
                            }
                            tportData.getConfig().set("public.tports." + publicTPortSlot, null);
    
                            tport.setPublicTPort(false);
                            if (tport.isOffered()) {
                                Player offeredTo = Bukkit.getPlayer(tport.getOfferedTo());
                                if (offeredTo != null) {
                                    sendInfoTheme(offeredTo, "TPort %s is offered to you from player %s, this TPort is not public anymore",
                                            tport.getName(), PlayerUUID.getPlayerName(tport.getOwner()));
                                }
                            }
                            tport.save();
                            sendSuccessTheme(player, "Successfully made TPort %s private", tport.getName());
                        
                            Player playerOwner = Bukkit.getPlayer(tport.getOwner());
                            if (playerOwner != null) {
                                sendInfoTheme(playerOwner, "Player %s has made TPort %s private", player.getName(), tport.getName());
                            }
                        
                            while (true) {
                                if (tportData.getConfig().contains("public.tports." + publicSlot)) {
                                    String publicTPort = tportData.getConfig().getString("public.tports." + publicSlot, "s;s");
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
                    }
                    return;
                }
            }
        }
        if (!fromDelete) sendErrorTheme(player, "No public TPort found called %s", name);
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport public remove <own TPort name|all TPort name>
        
        if (args.length == 3) {
            removePublicTPort(args[2], player, false);
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport public remove <TPort name>");
        }
    }
}