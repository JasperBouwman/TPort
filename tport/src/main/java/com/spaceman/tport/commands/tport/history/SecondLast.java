package com.spaceman.tport.commands.tport.history;

import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.history.HistoryElement;
import com.spaceman.tport.history.HistoryEvents;
import com.spaceman.tport.tpEvents.TPEManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;

import static com.spaceman.tport.history.HistoryEvents.teleportHistory;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendSuccessTranslation;

public class SecondLast extends SubCommand {
    
    @Override
    public void run(String[] args, Player player) {
        // tport history secondLast
        
        ArrayList<HistoryElement> history = teleportHistory.getOrDefault(player.getUniqueId(), new ArrayList<>());
        
        if (history.size() < 2) {
            sendErrorTranslation(player, "empty");
            return;
        }
        
        HistoryElement element = history.get(history.size() -2);
        
        HistoryEvents.setLocationSource(player.getUniqueId(), element.newLocation());
        TPEManager.requestTeleportPlayer(player, element.newLocation().getLocation(player),
                () -> sendSuccessTranslation(Bukkit.getPlayer(player.getUniqueId()), "succeeded"),
                (p, delay, tickMessage, seconds, secondMessage) -> sendSuccessTranslation(p, "tport.command.biomeTP.randomTP.succeededRequested", delay, tickMessage, seconds, secondMessage));
        
    }
}
