package com.spaceman.tport.commands.tport.edit.description;

import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;

public class Remove extends SubCommand {
    
    public Remove() {
        setCommandDescription(formatInfoTranslation("tport.command.edit.description.remove.commandDescription"));
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport edit <TPort name> description remove
        if (args.length == 4) {
            TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
            if (tport == null) {
                sendErrorTranslation(player, "tport.command.noTPortFound", args[1]);
                return;
            }
            if (tport.isOffered()) {
                sendErrorTranslation(player, "tport.command.edit.description.remove.isOffered",
                        tport, asPlayer(tport.getOfferedTo()));
                return;
            }
            if (!tport.hasDescription()) {
                sendInfoTranslation(player, "tport.command.edit.description.remove.noDescription", tport);
                return;
            }
            tport.setDescription(null);
            tport.save();
            sendSuccessTranslation(player, "tport.command.edit.description.remove.succeeded", tport);
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport edit <Tport name> description remove");
        }
    }
}
