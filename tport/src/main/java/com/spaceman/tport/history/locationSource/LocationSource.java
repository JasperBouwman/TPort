package com.spaceman.tport.history.locationSource;

import com.spaceman.tport.fancyMessage.encapsulation.Encapsulation;
import com.spaceman.tport.fancyMessage.inventories.InventoryModel;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

/** Information about the location */
public interface LocationSource extends Encapsulation {
    
    /**
     * Get the location to teleport to.
     * @return Location of the teleport, when null the location could not be given.
     * example: When the location is a TPort and the player needs to ask for consent
    * */
    @Nullable
    Location getLocation(Player player);
    
    void setLocation(Location location);
    
    void teleportToLocation(Player player);
    
    void notSafeToTeleport(Player player);
    
    @Nullable
    InventoryModel getInventoryModel();
    
    @Nullable
    String getType();
    
    /**
     * When true, a safety check will be preformed before executing ({@link LocationSource#teleportToLocation}).
     * The location from ({@link LocationSource#getLocation(Player)}) is checked.
     * If the location is unsafe, the teleportation will be canceled.
     * See {@link com.spaceman.tport.commands.tport.SafetyCheck#isSafe(Location)} for information about the safety check.
     * <p>
     * When false, no safety check will be preformed.
     * */
    boolean getSafetyCheckState(Player player);
}
