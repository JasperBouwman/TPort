package com.spaceman.tport.commands.tport;

import com.spaceman.tport.colorFormatter.ColorTheme;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.UUID;

import static com.spaceman.tport.colorFormatter.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;

public class Home extends SubCommand {
    
    @Override
    public Message getCommandDescription() {
        return new Message(TextComponent.textComponent("This command is used to teleport to your home TPort", ColorTheme.ColorType.infoColor),
                textComponent("\n\nPermissions: ", ColorTheme.ColorType.infoColor), textComponent("TPort.home", ColorTheme.ColorType.varInfoColor),
                textComponent(" or ", ColorTheme.ColorType.infoColor), textComponent("TPort.basic", ColorTheme.ColorType.varInfoColor));
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport home
        
        if (!hasPermission(player, true, true, "TPort.home", "TPort.basic")) {
            return;
        }
        Files tportData = GettingFiles.getFile("TPortData");
        if (tportData.getConfig().contains("tport." + player.getUniqueId() + ".home")) {
            String homeID = tportData.getConfig().getString("tport." + player.getUniqueId() + ".home", TPortManager.defUUID.toString());
            TPort tport = TPortManager.getTPort(UUID.fromString(homeID));
            if (tport != null) {
                Open.runNotPerm(new String[]{"open", PlayerUUID.getPlayerName(tport.getOwner()), tport.getName()}, player);
            } else {
                sendErrorTheme(player, "Your home TPort was not found, it may be removed");
            }
        } else {
            sendErrorTheme(player, "You don't have a home set");
        }
    }
}
