package com.spaceman.tport.commands.tport.history;

import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.encapsulation.PluginEncapsulation;
import com.spaceman.tport.history.locationSource.CraftLocationSource;
import com.spaceman.tport.history.HistoryElement;
import com.spaceman.tport.history.locationSource.LocationSource;
import org.bukkit.entity.Player;

import java.util.ArrayList;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.*;
import static com.spaceman.tport.history.TeleportHistory.teleportHistory;

public class Chat extends SubCommand {
    
    @Override
    public void run(String[] args, Player player) {
        // tport history chat [filter]
        
        Message elements = new Message();
        ArrayList<HistoryElement> history = teleportHistory.getOrDefault(player.getUniqueId(), new ArrayList<>());
        boolean color = true;
        for (HistoryElement element : history) {
            LocationSource oldLoc = new CraftLocationSource(element.oldLocation());
            LocationSource newLoc = element.newLocation();
            String cause = element.cause();
            PluginEncapsulation application = new PluginEncapsulation(element.application());
            
            Message messageElement;
            if (color) {
                messageElement = formatTranslation(infoColor, varInfoColor, "tport.command.history.list.element", oldLoc, newLoc, cause, application);
            } else {
                messageElement = formatTranslation(infoColor, varInfo2Color, "tport.command.history.list.element", oldLoc, newLoc, cause, application);
            }
            elements.addMessage(messageElement);
            elements.addMessage(formatInfoTranslation("tport.command.history.list.delimiter"));
            color = !color;
        }
        elements.removeLast();
        
        sendInfoTranslation(player, "tport.command.history.list", elements);
    }
}
