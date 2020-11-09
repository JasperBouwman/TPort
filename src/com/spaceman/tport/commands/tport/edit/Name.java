package com.spaceman.tport.commands.tport.edit;

import com.spaceman.tport.commands.tport.publc.ListSize;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.UUID;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendSuccessTheme;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.tport.TPortManager.getTPort;

public class Name extends SubCommand {
    
    private final EmptyCommand emptyName;
    
    public Name() {
        emptyName = new EmptyCommand();
        emptyName.setCommandName("new TPort name", ArgumentType.REQUIRED);
        emptyName.setCommandDescription(textComponent("This command is used to rename the given TPort", ColorTheme.ColorType.infoColor));
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
                sendErrorTheme(player, "No TPort found called %s", args[1]);
                return;
            }
            if (tport.isOffered()) {
                sendErrorTheme(player, "You can't edit TPort %s while its offered to %s", tport.getName(), PlayerUUID.getPlayerName(tport.getOfferedTo()));
                return;
            }
            if (TPortManager.getTPort(player.getUniqueId(), args[3]) != null) {
                sendErrorTheme(player, "Name %s is already in use", args[3]);
                return;
            }
            try {
                Long.parseLong(args[3]);
                sendErrorTheme(player, "TPort name can't be a number, but it can contain a number");
                return;
            } catch (NumberFormatException ignore) {
            }
            
            if (tport.isPublicTPort()) {
                Files tportData = GettingFiles.getFile("TPortData");
                for (int publicSlot = 0; publicSlot < ListSize.getPublicTPortSize(); publicSlot++) {
                    if (tportData.getConfig().contains("public.tports." + publicSlot)) {
                        String tportID = tportData.getConfig().getString("public.tports." + publicSlot, TPortManager.defUUID.toString());
                        //noinspection ConstantConditions
                        TPort publicTPort = getTPort(UUID.fromString(tportID));
                        
                        if (publicTPort != null && publicTPort.getName().equalsIgnoreCase(args[3])) {
                            sendErrorTheme(player, "Name %s is already used as a Public TPort name", args[3]);
                            return;
                        }
                    }
                }
            }
            
            tport.setName(args[3]);
            tport.save();
            sendSuccessTheme(player, "Successfully set new name to %s", args[3]);
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport edit <TPort name> name <new TPort name>");
        }
        
    }
}
