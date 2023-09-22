package com.spaceman.tport.events;

import com.spaceman.tport.commands.tport.ResourcePack;
import com.spaceman.tport.fancyMessage.inventories.keyboard.QuickType;
import com.spaceman.tport.tpEvents.TPRequest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static com.spaceman.tport.fileHander.Files.tportData;

public class JoinEvent implements Listener {
    
    public static void setData(Player player) {
        String playerUUID = player.getUniqueId().toString();

        if (!tportData.getConfig().contains("tport." + playerUUID)) {
            tportData.getConfig().set("tport." + playerUUID + ".tports.0", "welcome");
            tportData.getConfig().set("tport." + playerUUID + ".tports.0", null);
            tportData.saveConfig();
        }
        
        QuickType.setQuickTypeSignHandler(player);
    }
    
    @EventHandler(priority=EventPriority.MONITOR)
    @SuppressWarnings("unused")
    public void join(PlayerJoinEvent e) {
        setData(e.getPlayer());
        ResourcePack.updateResourcePack(e.getPlayer());
    }
    
    @EventHandler
    @SuppressWarnings("unused")
    public void leave(PlayerQuitEvent e) {
        TPRequest.playerLeft(e.getPlayer());
        QuickType.removeQuickTypeSignHandler(e.getPlayer());
    }
}