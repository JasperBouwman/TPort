package com.spaceman.tport.commands.tport.requests;

import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.tpEvents.TPRequest;
import org.bukkit.entity.Player;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatInfoTranslation;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;

public class Revoke extends SubCommand {
    
    public Revoke() {
        setCommandDescription(formatInfoTranslation("tport.command.requests.revoke.commandDescription"));
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport requests revoke
        
        if (args.length == 2) {
            TPRequest request = TPRequest.getRequest(player.getUniqueId());
            if (request == null) {
                sendErrorTranslation(player, "tport.command.requests.revoke.notRequesting");
            } else {
                request.revokeRequest();
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport requests revoke");
        }
    }
}
