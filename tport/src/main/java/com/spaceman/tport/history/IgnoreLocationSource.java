package com.spaceman.tport.history;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class IgnoreLocationSource extends LocationSource {
    
    @Override
    public String asString() {
        return null;
    }
    
    @Override
    public Location getLocation(Player player) {
        return null;
    }
    
    @Override
    public void teleportToLocation(Player player, boolean safetyCheck) {
    
    }
}
