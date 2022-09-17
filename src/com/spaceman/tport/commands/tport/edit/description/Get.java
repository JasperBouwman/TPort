package com.spaceman.tport.commands.tport.edit.description;

import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;

public class Get extends SubCommand {
    
    @Override
    public Message getCommandDescription() {
        return formatInfoTranslation("tport.command.edit.description.get.commandDescription");
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport edit <TPort name> description get
        if (args.length == 4) {
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
            if (tport == null) {
                sendErrorTranslation(player, "tport.command.noTPortFound", args[1]);
                return;
            }
            if (tport.hasDescription()) {
                sendInfoTranslation(player, "tport.command.edit.description.get.succeeded", tport, tport.getDescription());
            } else {
                sendInfoTranslation(player, "tport.command.edit.description.get.noDescription", tport);
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport edit <Tport name> description get");
        }
    }
}
