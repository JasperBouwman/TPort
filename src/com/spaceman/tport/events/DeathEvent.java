package com.spaceman.tport.events;

import com.spaceman.tport.commands.tport.Back;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import static com.spaceman.tport.commands.tport.Back.prevTPort;

public class DeathEvent implements Listener {
    
    @EventHandler
    @SuppressWarnings("unused")
    public void onDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        prevTPort.put(player.getUniqueId(), new Back.PrevTPort(null, null, null, player.getLocation()));
    }
    
}
