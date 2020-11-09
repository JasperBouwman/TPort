package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.UUID;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;

public class Home extends SubCommand {
    
    public Home() {
        setPermissions("TPort.home", "TPort.basic");
    }
    
    @Override
    public Message getCommandDescription() {
        return new Message(TextComponent.textComponent("This command is used to teleport to your home TPort", ColorTheme.ColorType.infoColor));
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport home
        
        if (!hasPermissionToRun(player, true)) {
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
