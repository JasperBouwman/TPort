package com.spaceman.tport.commands.tport.publc;

import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;
import static com.spaceman.tport.tport.TPortManager.getTPort;

public class ListSize extends SubCommand {
    
    private final EmptyCommand emptySize;
    
    public ListSize() {
        emptySize = new EmptyCommand();
        emptySize.setCommandName("size", ArgumentType.OPTIONAL);
        emptySize.setCommandDescription(textComponent("This command is used to set the maximum public TPorts", ColorType.infoColor));
        emptySize.setPermissions("TPort.public.listSize", "TPort.admin.public");
        addAction(emptySize);
    }
    
    public static int getPublicTPortSize() {
        return getFile("TPortConfig").getConfig().getInt("public.size", 70);
    }
    
    private static void setPublicTPortSize(int size) {
        Files tportConfig = getFile("TPortConfig");
        Files tportData = getFile("TPortData");
    
        for (int publicSlot = size + 1; publicSlot < ListSize.getPublicTPortSize(); publicSlot++) {
            if (tportData.getConfig().contains("public.tports." + publicSlot)) {
                String tportID = tportData.getConfig().getString("public.tports." + publicSlot, TPortManager.defUUID.toString());
                //noinspection ConstantConditions
                TPort tport = getTPort(UUID.fromString(tportID));
                tportData.getConfig().set("public.tports." + publicSlot, null);
    
                if (tport != null) {
                    tport.setPublicTPort(false);
                    tport.save();
                    Player owner = Bukkit.getPlayer(tport.getOwner());
                    if (owner != null) {
                        sendInfoTheme(owner, "Due to decrease of Public TPorts TPort %s is not public anymore", tport.getName());
                    }
                    return;
                }
            }
        }
        
        tportData.saveConfig();
        tportConfig.getConfig().set("public.size", size);
        tportConfig.saveConfig();
    }
    
    @Override
    public Message getCommandDescription() {
        return new Message(textComponent("This command is used to get the maximum public TPorts", ColorType.infoColor));
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport public listSize [size]
    
        if (args.length == 2) {
            sendInfoTheme(player, "The amount of maximum public TPorts are %s", getPublicTPortSize());
        } else if (args.length == 3) {
            if (emptySize.hasPermissionToRun(player, true)) {
                try {
                    setPublicTPortSize(Integer.parseInt(args[2]));
                    sendSuccessTheme(player, "Successfully set the maximum public TPorts to %s", args[2]);
                } catch (NumberFormatException nfe) {
                    sendErrorTheme(player, "%s is not a valid number", args[2]);
                }
            }
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport public listSize [size]");
        }
    }
}
