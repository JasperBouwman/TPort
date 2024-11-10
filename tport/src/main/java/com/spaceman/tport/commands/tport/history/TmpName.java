package com.spaceman.tport.commands.tport.history;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.SafetyCheck;
import com.spaceman.tport.history.HistoryElement;
import com.spaceman.tport.history.HistoryFilter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatInfoTranslation;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;
import static com.spaceman.tport.history.TeleportHistory.teleportHistory;

public class TmpName extends SubCommand {
    
    public TmpName() {
        EmptyCommand emptyFilter = new EmptyCommand();
        emptyFilter.setCommandName("filter", ArgumentType.OPTIONAL);
        emptyFilter.setCommandDescription(formatInfoTranslation("tport.command.history.tmpName.filter.commandDescription"));
        addAction(emptyFilter);
        
        setCommandDescription(formatInfoTranslation("tport.command.history.tmpName.commandDescription"));
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
    
    public static void run(HistoryElement element, Player player, boolean invertedSafetyCheck) {
        boolean innerSafetyCheck = element.newLocation().getSafetyCheckState(player);

        if (invertedSafetyCheck) {
            innerSafetyCheck = !innerSafetyCheck;
        }
        
        Location loc = element.newLocation().getLocation(player);
        if (!innerSafetyCheck || SafetyCheck.isSafe(loc)) {
            element.newLocation().teleportToLocation(player);
        } else {
            element.newLocation().notSafeToTeleport(player);
        }
    }
    
    @Override
    public void run(String[] args, Player player) { //todo add safety check to command
        // tport history tmpName [filter]
        
        if (args.length == 2) {
            ArrayList<HistoryElement> history = teleportHistory.getOrDefault(player.getUniqueId(), new ArrayList<>());
            
            if (history.isEmpty()) {
                sendErrorTranslation(player, "tport.command.history.tmpName.historyEmpty");
                return;
            }
            
            HistoryElement element = history.get(history.size() -1);
            if (element == null) {
                sendErrorTranslation(player, "tport.command.history.tmpName.historyEmpty");
                return;
            }
            
            run(element, player, false);
        } else if (args.length == 3) {
            
            String filter = HistoryFilter.exist(args[2]);
            if (filter == null) {
                sendErrorTranslation(player, "tport.command.history.tmpName.filter.invalidFilter", args[2]);
                return;
            }
            
            ArrayList<HistoryElement> history = teleportHistory.getOrDefault(player.getUniqueId(), new ArrayList<>());
            if (history.isEmpty()) {
                sendErrorTranslation(player, "tport.command.history.tmpName.filter.historyEmpty");
                return;
            }
            
            HistoryElement element = null;
            for (int i = history.size() - 1; i >= 0; i--) {
                HistoryElement historyElement = history.get(i);
                if (HistoryFilter.fits(historyElement, filter)) {
                    element = historyElement;
                    break;
                }
            }
            
            if (element == null) {
                sendErrorTranslation(player, "tport.command.history.tmpName.filter.noFound", filter);
                return;
            }
            
            run(element, player, false);
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport history tmpName [filter]");
        }
    }
}
