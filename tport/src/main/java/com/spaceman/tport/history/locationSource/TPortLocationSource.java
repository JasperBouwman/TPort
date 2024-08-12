package com.spaceman.tport.history.locationSource;

import com.spaceman.tport.fancyMessage.encapsulation.TPortEncapsulation;
import com.spaceman.tport.fancyMessage.inventories.InventoryModel;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

import static com.spaceman.tport.inventories.TPortInventories.history_element_tport_model;

public class TPortLocationSource extends TPortEncapsulation implements LocationSource {
    
    
    public TPortLocationSource(TPort tport) {
        super(tport);
    }
    
    @Override
    @Nullable
    public Location getLocation(Player player) {
        if (super.tport.canTeleport(player, false, false, false)) {
            return super.tport.getLocation();
        }
        return null;
    }
    
    @Override
    public void setLocation(Location location) { }
    
    @Override
    public void teleportToLocation(Player player, boolean safetyCheck) {
        String ownerName = PlayerUUID.getPlayerName(tport.getOwner());
        Bukkit.dispatchCommand(player, "tport open " + ownerName + " " + super.tport.getName() + " " + safetyCheck);
    }
    
    @Override
    public InventoryModel getInventoryModel() {
        return history_element_tport_model;
    }
    
    @Override
    @Nullable
    public String getType() {
        return "TPort";
    }
    
}
