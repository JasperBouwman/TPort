package com.spaceman.tport.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class TeleportEvents implements Listener {
    
    @EventHandler
    @SuppressWarnings("unused")
    public void onTeleport(PlayerTeleportEvent e) {
//        System.out.println(e.getCause().name());
    }
    
    
}
