package com.spaceman.tport.commands.tport.searchArea;

import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.searchAreaHander.SearchAreaHandler;
import org.bukkit.entity.Player;

import java.util.Collection;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendSuccessTheme;

public class Set extends SubCommand {
    
    public Set() {
        EmptyCommand emptySetType = new EmptyCommand();
        emptySetType.setCommandName("type", ArgumentType.OPTIONAL);
        addAction(emptySetType);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return SearchAreaHandler.getAreas();
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport searchArea set [type]
        
        if (args.length == 3) {
            SearchAreaHandler searchArea = SearchAreaHandler.getSearchAreaHandler(args[2]);
            
            if (searchArea != null) {
                SearchAreaHandler.setSearchAreaHandler(searchArea);
                sendSuccessTheme(player, "Successfully set the SearchArea to %s", searchArea.getName());
            } else {
                sendErrorTheme(player, "Given SearchArea does not exist", args[2]);
            }
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport searchArea set <type>");
        }
    }
}