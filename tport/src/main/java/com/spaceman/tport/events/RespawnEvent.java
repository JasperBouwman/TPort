package com.spaceman.tport.events;

import com.spaceman.tport.commands.tport.Back;
import com.spaceman.tport.commands.tport.Features;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import static com.spaceman.tport.commands.tport.Back.prevTPorts;

public class RespawnEvent implements Listener {
    
    @EventHandler
    @SuppressWarnings("unused")
    public void onRespawn(PlayerRespawnEvent e) {
        if (Features.Feature.DeathTP.isEnabled()) {
            Player player = e.getPlayer();
            prevTPorts.put(player.getUniqueId(), new Back.PrevTPort(Back.PrevType.DEATH, "deathLoc", player.getLocation(), "prevLoc", null));
        }
    }
}
