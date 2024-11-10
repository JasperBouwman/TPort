package com.spaceman.tport.commands.tport.history;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.SafetyCheck;
import com.spaceman.tport.history.HistoryElement;
import com.spaceman.tport.history.HistoryFilter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.spaceman.tport.commands.tport.SafetyCheck.SafetyCheckSource.TPORT_BACK;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.history.TeleportHistory.teleportHistory;
import static com.spaceman.tport.tpEvents.TPEManager.requestTeleportPlayer;

public class Back extends SubCommand {
    
    public Back() {
        EmptyCommand emptyFilter = new EmptyCommand();
        emptyFilter.setCommandName("filter", ArgumentType.OPTIONAL);
        emptyFilter.setCommandDescription(formatInfoTranslation("tport.command.history.back.filter.commandDescription"));
        addAction(emptyFilter);
        
        setCommandDescription(formatInfoTranslation("tport.command.history.back.commandDescription"));
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
        boolean innerSafetyCheck = TPORT_BACK.getState(player);
        
        if (invertedSafetyCheck) {
            innerSafetyCheck = !innerSafetyCheck;
        }
        
        Location loc = element.oldLocation();
        if (!innerSafetyCheck || SafetyCheck.isSafe(loc)) {
            
            requestTeleportPlayer(player, loc,
                    () -> sendSuccessTranslation(Bukkit.getPlayer(player.getUniqueId()), "tport.tportInventories.openHistory.teleportToOld.succeeded"),
                    (p, delay, tickMessage, seconds, secondMessage) -> sendSuccessTranslation(p, "tport.tportInventories.openHistory.teleportToOld.tpRequested", delay, tickMessage, seconds, secondMessage));
        } else {
            sendErrorTranslation(player, "tport.tportInventories.openHistory.teleportToOld.notSafeToTeleport");;
        }
    }
    
    @Override
    public void run(String[] args, Player player) { //todo add safety check to command
        // tport history back [filter]
        
        if (args.length == 2) {
            ArrayList<HistoryElement> history = teleportHistory.getOrDefault(player.getUniqueId(), new ArrayList<>());
            
            if (history.isEmpty()) {
                sendErrorTranslation(player, "tport.command.history.back.historyEmpty");
                return;
            }
            
            HistoryElement element = history.get(history.size() -1);
            if (element == null) {
                sendErrorTranslation(player, "tport.command.history.back.historyEmpty");
                return;
            }
            
            run(element, player, false);
        } else if (args.length == 3) {
            
            String filter = HistoryFilter.exist(args[2]);
            if (filter == null) {
                sendErrorTranslation(player, "tport.command.history.back.filter.invalidFilter", args[2]);
                return;
            }
            
            ArrayList<HistoryElement> history = teleportHistory.getOrDefault(player.getUniqueId(), new ArrayList<>());
            if (history.isEmpty()) {
                sendErrorTranslation(player, "tport.command.history.back.filter.historyEmpty");
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
                sendErrorTranslation(player, "tport.command.history.back.filter.noFound", filter);
                return;
            }
            
            run(element, player, false);
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport history back [filter]");
        }
    }
}
