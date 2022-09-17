package com.spaceman.tport.commands.tport.publc;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fileHander.Files.tportConfig;
import static com.spaceman.tport.fileHander.Files.tportData;
import static com.spaceman.tport.tport.TPortManager.getTPort;

public class ListSize extends SubCommand {
    
    private final EmptyCommand emptySize;
    
    public ListSize() {
        emptySize = new EmptyCommand();
        emptySize.setCommandName("size", ArgumentType.OPTIONAL);
        emptySize.setCommandDescription(formatInfoTranslation("tport.command.public.listSize.size.commandDescription"));
        emptySize.setPermissions("TPort.public.listSize", "TPort.admin.public");
        addAction(emptySize);
    }
    
    public static int getPublicTPortSize() {
        return tportConfig.getConfig().getInt("public.size", 70);
    }
    
    private static void setPublicTPortSize(int size) {
        for (int publicSlot = size + 1; publicSlot < ListSize.getPublicTPortSize(); publicSlot++) {
            if (tportData.getConfig().contains("public.tports." + publicSlot)) {
                String tportID = tportData.getConfig().getString("public.tports." + publicSlot, TPortManager.defUUID.toString());
                TPort tport = getTPort(UUID.fromString(tportID));
                tportData.getConfig().set("public.tports." + publicSlot, null);
                
                if (tport != null) {
                    tport.setPublicTPort(false);
                    tport.save();
                    Player owner = Bukkit.getPlayer(tport.getOwner());
                    if (owner != null) {
                        sendInfoTranslation(owner, "tport.command.public.listSize.size.removedSmallerList", tport);
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
        return formatInfoTranslation("tport.command.public.listSize.commandDescription");
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport public listSize [size]
        
        if (args.length == 2) {
            sendInfoTranslation(player, "tport.command.public.listSize.succeeded", getPublicTPortSize());
        } else if (args.length == 3) {
            if (!emptySize.hasPermissionToRun(player, true)) {
                return;
            }
            try {
                setPublicTPortSize(Integer.parseInt(args[2]));
                sendSuccessTranslation(player, "tport.command.public.listSize.size.succeeded", args[2]);
            } catch (NumberFormatException nfe) {
                sendErrorTranslation(player, "tport.command.public.listSize.size.invalidNumber", args[2]);
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport public listSize [size]");
        }
    }
}
