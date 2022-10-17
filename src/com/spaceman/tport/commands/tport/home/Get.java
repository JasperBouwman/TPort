package com.spaceman.tport.commands.tport.home;

import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.tport.TPort;
import org.bukkit.entity.Player;

import static com.spaceman.tport.commands.tport.Home.getHome;
import static com.spaceman.tport.commands.tport.Home.hasHome;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;

public class Get extends SubCommand {
    
    @Override
    public Message getCommandDescription() {
        return formatInfoTranslation("tport.command.home.get.commandDescription");
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport home get
        
        if (args.length != 2) {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport home get");
            return;
        }
        
        if (!hasHome(player, false)) {
            sendErrorTranslation(player, "tport.command.home.get.noHome");
            return;
        }
        TPort tport = getHome(player);
        if (tport == null) {
            sendErrorTranslation(player, "tport.command.home.get.homeNotFound");
            return;
        }
        
        sendInfoTranslation(player, "tport.command.home.get.succeeded", tport);
        
    }
}
