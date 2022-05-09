package com.spaceman.tport.fancyMessage.language.subCommands;

import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.language.Language;
import org.bukkit.entity.Player;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;

public class Get extends SubCommand {
    
    @Override
    public Message getCommandDescription() {
        return formatInfoTranslation("tport.command.language.get.commandDescription");
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport language get
        
        if (args.length == 2) {
            String lang = Language.getPlayerLangName(player.getUniqueId());
            
            if (lang.equals("custom")) {
                sendInfoTranslation(player, "tport.command.language.get.succeeded.custom", lang, "/tport help language");
            } else if (lang.equals("server")) {
                sendInfoTranslation(player, "tport.command.language.get.succeeded.server", lang);
            } else {
                sendInfoTranslation(player, "tport.command.language.get.succeeded.other", lang);
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport language get");
        }
    }
}
