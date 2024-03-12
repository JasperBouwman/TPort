package com.spaceman.tport.history;

import com.spaceman.tport.fancyMessage.encapsulation.Encapsulation;
import com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public abstract class LocationSource extends Encapsulation {
    
    public Location location = null;
    
    /**
     * Get the location to teleport to
     * @return Location of the teleport, when null the location could not be given.
     * example: When the location is a TPort and the player needs to ask for consent
    * */
    @Nullable
    public Location getLocation(Player player) {
        return location;
    }
    
    public abstract void teleportToLocation(Player player, boolean safetyCheck);
    
    public void setLocation(Location location) {
        this.location = location;
    }
}
