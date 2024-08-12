package com.spaceman.tport.commands.tport.history;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.history.TeleportHistory;
import com.spaceman.tport.history.HistoryElement;
import com.spaceman.tport.history.HistoryFilter;
import com.spaceman.tport.tpEvents.TPEManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendSuccessTranslation;
import static com.spaceman.tport.history.TeleportHistory.teleportHistory;

public class Last extends SubCommand {
    
    public Last() {
        EmptyCommand emptyFilter = new EmptyCommand();
        emptyFilter.setCommandName("filter", ArgumentType.OPTIONAL);
        addAction(emptyFilter);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return HistoryFilter.getFilters();
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport history last
        
        if (args.length == 2) {
            ArrayList<HistoryElement> history = teleportHistory.getOrDefault(player.getUniqueId(), new ArrayList<>());
            
            if (history.isEmpty()) {
                sendErrorTranslation(player, "empty");
                return;
            }
            
            HistoryElement element = history.get(history.size() - 1);
            
            TeleportHistory.setLocationSource(player.getUniqueId(), element.newLocation());
            TPEManager.requestTeleportPlayer(player, element.newLocation().getLocation(player),
                    () -> sendSuccessTranslation(Bukkit.getPlayer(player.getUniqueId()), "succeeded"),
                    (p, delay, tickMessage, seconds, secondMessage) -> sendSuccessTranslation(p, "tport.command.biomeTP.randomTP.succeededRequested", delay, tickMessage, seconds, secondMessage));
        } else if (args.length == 3) {
            String filter = HistoryFilter.exist(args[2]);
            if (filter == null) {
                sendErrorTranslation(player, "no filter");
                return;
            }
            
            ArrayList<HistoryElement> history = teleportHistory.getOrDefault(player.getUniqueId(), new ArrayList<>());
            if (history.isEmpty()) {
                sendErrorTranslation(player, "empty");
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
                sendErrorTranslation(player, "empty");
                return;
            }
            
            element.newLocation().teleportToLocation(player, false);
            
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport history last [filter]");
        }
    }
}
