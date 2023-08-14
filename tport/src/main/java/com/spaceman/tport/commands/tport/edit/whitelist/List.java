package com.spaceman.tport.commands.tport.edit.whitelist;

import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfo2Color;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;

public class List extends SubCommand {
    
    public List() {
        setCommandDescription(formatInfoTranslation("tport.command.edit.whitelist.list.commandDescription"));
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport edit <TPort name> whitelist list
        
        if (args.length != 4) {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport edit <TPort name> whitelist list");
            return;
        }
        
        TPort tport = TPortManager.getTPort(player.getUniqueId(), args[1]);
        
        if (tport == null) {
            sendErrorTranslation(player, "tport.command.noTPortFound", args[1]);
            return;
        }
        ArrayList<UUID> whitelist = tport.getWhitelist();
        boolean color = true;
        
        Message playerList = new Message();
        for (int i = 0; i < whitelist.size(); i++) {
            if (color) {
                playerList.addMessage(formatTranslation(varInfoColor, varInfoColor, "%s",
                        asPlayer(whitelist.get(i))));
            } else {
                playerList.addMessage(formatTranslation(varInfo2Color, varInfo2Color, "%s",
                        asPlayer(whitelist.get(i))));
            }
            
            if (i + 2 == whitelist.size()) playerList.addMessage(formatInfoTranslation("tport.command.edit.whitelist.list.succeeded.lastDelimiter"));
            else                           playerList.addMessage(formatInfoTranslation("tport.command.edit.whitelist.list.succeeded.delimiter"));
            
            color = !color;
        }
        playerList.removeLast();
        
        if (whitelist.isEmpty()) {
            sendInfoTranslation(player, "tport.command.edit.whitelist.list.succeeded.empty", tport);
        } else if (whitelist.size() == 1) {
            sendInfoTranslation(player, "tport.command.edit.whitelist.list.succeeded.singular", tport, playerList);
        } else {
            sendInfoTranslation(player, "tport.command.edit.whitelist.list.succeeded.multiple", tport, playerList);
        }
    }
}
