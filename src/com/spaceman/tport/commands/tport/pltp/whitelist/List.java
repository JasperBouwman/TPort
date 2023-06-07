package com.spaceman.tport.commands.tport.pltp.whitelist;

import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.pltp.Whitelist;
import com.spaceman.tport.fancyMessage.Message;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfo2Color;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;

public class List extends SubCommand {
    
    public List() {
        setCommandDescription(formatInfoTranslation("tport.command.PLTP.whitelist.list.commandDescription"));
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport PLTP whitelist list
        
        if (args.length == 3) {
            boolean color = true;
            ArrayList<String> whitelist = Whitelist.getPLTPWhitelist(player);
            
            Message playerList = new Message();
            for (int i = 0; i < whitelist.size(); i++) {
                if (color) {
                    playerList.addMessage(formatTranslation(varInfoColor, varInfoColor, "%s",
                            asPlayer(UUID.fromString(whitelist.get(i)))));
                } else {
                    playerList.addMessage(formatTranslation(varInfo2Color, varInfo2Color, "%s",
                            asPlayer(UUID.fromString(whitelist.get(i)))));
                }
                
                if (i + 2 == whitelist.size()) playerList.addMessage(formatInfoTranslation("tport.command.PLTP.whitelist.list.succeeded.lastDelimiter"));
                else                           playerList.addMessage(formatInfoTranslation("tport.command.PLTP.whitelist.list.succeeded.delimiter"));
                
                color = !color;
            }
            playerList.removeLast();
            
            Message message;
            if (whitelist.isEmpty()) {
                message = formatInfoTranslation("tport.command.PLTP.whitelist.list.succeeded.empty");
            } else if (whitelist.size() == 1) {
                message = formatInfoTranslation("tport.command.PLTP.whitelist.list.succeeded.singular", playerList);
            } else {
                message = formatInfoTranslation("tport.command.PLTP.whitelist.list.succeeded.multiple", playerList);
            }
            
            message.sendAndTranslateMessage(player);
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport PLTP whitelist list");
        }
    }
}
