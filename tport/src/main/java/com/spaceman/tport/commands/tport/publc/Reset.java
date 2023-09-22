package com.spaceman.tport.commands.tport.publc;

import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fileHander.Files.tportData;
import static com.spaceman.tport.tport.TPortManager.getTPort;

public class Reset extends SubCommand {
    
    public Reset() {
        this.setPermissions("TPort.public.reset", "TPort.admin.public");
        this.setCommandDescription(formatInfoTranslation("tport.command.public.reset.commandDescription"));
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport public reset
        
        if (args.length == 2) {
            if (!this.hasPermissionToRun(player, true)) {
                return;
            }
            
            for (String publicTPortSlot : tportData.getKeys("public.tports")) {
                String tportID = tportData.getConfig().getString("public.tports." + publicTPortSlot, TPortManager.defUUID.toString());
                TPort tport = getTPort(UUID.fromString(tportID));
                if (tport == null) {
                    continue;
                }
                tport.setPublicTPort(false);
                sendInfoTranslation(Bukkit.getPlayer(tport.getOwner()), "tport.command.public.reset.removeMessage", player, tport);
            }
            tportData.getConfig().set("public.tports.0", null);
            tportData.saveConfig();
            
            sendSuccessTranslation(player, "tport.command.public.reset.succeeded");
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport public reset");
        }
    }
}
