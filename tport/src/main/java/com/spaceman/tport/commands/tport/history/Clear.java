package com.spaceman.tport.commands.tport.history;

import com.spaceman.tport.commandHandler.SubCommand;
import org.bukkit.entity.Player;

import static com.spaceman.tport.history.HistoryEvents.teleportHistory;

public class Clear extends SubCommand {
    
    @Override
    public void run(String[] args, Player player) {
        //tport history clear
        
        teleportHistory.remove(player.getUniqueId());
    }
}
