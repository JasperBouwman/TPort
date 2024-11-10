package com.spaceman.tport.commands.tport.history;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.history.HistoryElement;
import com.spaceman.tport.history.HistoryFilter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.history.TeleportHistory.teleportHistory;

public class Clear extends SubCommand {
    
    public Clear() {
        EmptyCommand emptyFilter = new EmptyCommand();
        emptyFilter.setCommandName("filter", ArgumentType.OPTIONAL);
        emptyFilter.setCommandDescription(formatInfoTranslation("tport.command.history.clear.filter.commandDescription"));
        addAction(emptyFilter);
        
        setCommandDescription(formatInfoTranslation("tport.command.history.clear.commandDescription"));
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        List<String> filters =  HistoryFilter.getFilters();
        ArrayList<String> returnList = new ArrayList<>();
        
        ArrayList<HistoryElement> history = teleportHistory.getOrDefault(player.getUniqueId(), new ArrayList<>());
        if (!history.isEmpty()) {
            for (String filter : filters) {
                for (HistoryElement element : history) {
                    if (HistoryFilter.fits(element, filter)) {
                        returnList.add(filter);
                        break;
                    }
                }
            }
        }
        
        return returnList;
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport history clear [filter]
        
        if (args.length == 2) {
            
            ArrayList<HistoryElement> history = teleportHistory.getOrDefault(player.getUniqueId(), new ArrayList<>());
            if (history.isEmpty()) {
                sendInfoTranslation(player, "tport.command.history.clear.alreadyEmpty");
                return;
            }
            
            teleportHistory.remove(player.getUniqueId());
            sendSuccessTranslation(player, "tport.command.history.clear.succeeded", history.size());
            
        } else if (args.length == 3) {
            
            String filter = HistoryFilter.exist(args[2]);
            if (filter == null) {
                sendErrorTranslation(player, "tport.command.history.clear.filter.invalidFilter", args[2]);
                return;
            }
            
            ArrayList<HistoryElement> history = teleportHistory.getOrDefault(player.getUniqueId(), new ArrayList<>());
            int oldSize = history.size();
            if (history.isEmpty()) {
                sendInfoTranslation(player, "tport.command.history.clear.filter.alreadyEmpty");
                return;
            }
            
            history.removeIf( (historyElement) -> HistoryFilter.fits(historyElement, filter));
            int newSize = history.size();
            
            int cleared = oldSize - newSize;
            if (cleared == 0) {
                sendSuccessTranslation(player, "tport.command.history.clear.filter.succeeded.none", cleared, filter);
            } else if (cleared == 1) {
                sendSuccessTranslation(player, "tport.command.history.clear.filter.succeeded.singular", cleared, filter);
            } else {
                sendSuccessTranslation(player, "tport.command.history.clear.filter.succeeded.multiple", cleared, filter);
            }
            
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport history clear [filter]");
        }
        
    }
}
