package com.spaceman.tport.commands.tport.edit;

import com.spaceman.tport.Main;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.publc.ListSize;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.UUID;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;
import static com.spaceman.tport.tport.TPortManager.getTPort;

public class Name extends SubCommand {
    
    private final EmptyCommand emptyName;
    
    public Name() {
        emptyName = new EmptyCommand();
        emptyName.setCommandName("new TPort name", ArgumentType.REQUIRED);
        emptyName.setCommandDescription(formatInfoTranslation("tport.command.edit.name.commandDescription"));
        emptyName.setPermissions("TPort.edit.name", "TPort.basic");
        addAction(emptyName);
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport edit <TPort name> name <new TPort name>
        
        if (!emptyName.hasPermissionToRun(player, true)) {
            return;
        }
        if (args.length == 4) {
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
            
            if (tport == null) {
                sendErrorTranslation(player, "tport.command.noTPortFound", args[1]);
                return;
            }
            if (tport.isOffered()) {
                sendErrorTranslation(player, "tport.command.edit.name.isOffered", tport,
                        tport, asPlayer(tport.getOfferedTo()));
                return;
            }
            TPort nameDuplicationTPort = TPortManager.getTPort(player.getUniqueId(), args[3]);
            if (nameDuplicationTPort != null) {
                if (args[3].equals(tport.getName())) {
                    sendErrorTranslation(player, "tport.command.edit.name.sameName", tport, args[3]);
                    return;
                } else if (!args[3].equalsIgnoreCase(tport.getName())) {
                    sendErrorTranslation(player, "tport.command.edit.name.nameUsed", nameDuplicationTPort);
                    return;
                }
            }
            try {
                Long.parseLong(args[3]);
                sendErrorTranslation(player, "tport.command.edit.name.numberName");
                return;
            } catch (NumberFormatException ignore) {
            }
            if (Main.containsSpecialCharacter(args[3])) {
                sendErrorTranslation(player, "tport.command.edit.name.specialChars", "A-Z", "0-9", "-", "_");
                return;
            }
            
            if (tport.isPublicTPort()) {
                Files tportData = GettingFiles.getFile("TPortData");
                for (int publicSlot = 0; publicSlot < ListSize.getPublicTPortSize(); publicSlot++) {
                    if (tportData.getConfig().contains("public.tports." + publicSlot)) {
                        String tportID = tportData.getConfig().getString("public.tports." + publicSlot, TPortManager.defUUID.toString());
                        TPort publicTPort = getTPort(UUID.fromString(tportID));
                        
                        if (publicTPort != null && publicTPort.getName().equalsIgnoreCase(args[3])) {
                            sendErrorTranslation(player, "tport.command.edit.name.nameUsedPublic", publicTPort);
                            return;
                        }
                    }
                }
            }
            
            tport.setName(args[3]);
            tport.save();
            sendSuccessTranslation(player, "Successfully set new name to %s", tport);
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport edit <TPort name> name <new TPort name>");
        }
        
    }
}
