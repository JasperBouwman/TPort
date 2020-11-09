package com.spaceman.tport.commands.tport.searchArea;

import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.searchAreaHander.SearchAreaHandler;
import org.bukkit.entity.Player;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendInfoTheme;

public class Get extends SubCommand {
    
    @Override
    public void run(String[] args, Player player) {
        // tport searchArea get
    
        if (args.length == 2) {
            sendInfoTheme(player, "SearchArea is set to %s", SearchAreaHandler.getSearchAreaHandler().getName());
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport searchArea get");
        }
    }
}
