package com.spaceman.tport.history.locationSource;

import com.spaceman.tport.fancyMessage.encapsulation.Encapsulation;
import com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation;
import com.spaceman.tport.fancyMessage.inventories.InventoryModel;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

/** Information about the location */
public abstract class LocationSource extends Encapsulation {
    
    public Location location = null;
    
    /**
     * Get the location to teleport to. If the location is dynamic
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
