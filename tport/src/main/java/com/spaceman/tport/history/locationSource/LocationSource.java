package com.spaceman.tport.history.locationSource;

import com.spaceman.tport.fancyMessage.encapsulation.Encapsulation;
import com.spaceman.tport.fancyMessage.inventories.InventoryModel;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

/** Information about the location */
public interface LocationSource extends Encapsulation {
    
    /**
     * Get the location to teleport to. If the location is dynamic
     * @return Location of the teleport, when null the location could not be given.
     * example: When the location is a TPort and the player needs to ask for consent
    * */
    @Nullable
    Location getLocation(Player player);
    
    void setLocation(Location location);
    
    void teleportToLocation(Player player, boolean safetyCheck);
    
    @Nullable
    InventoryModel getInventoryModel();
    
    @Nullable
    String getType();
}
