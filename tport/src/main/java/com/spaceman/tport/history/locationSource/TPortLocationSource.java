package com.spaceman.tport.history.locationSource;

import com.spaceman.tport.commands.tport.Open;
import com.spaceman.tport.commands.tport.SafetyCheck;
import com.spaceman.tport.fancyMessage.encapsulation.TPortEncapsulation;
import com.spaceman.tport.fancyMessage.inventories.InventoryModel;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;
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
    public void teleportToLocation(Player player) {
        if (!Open.getInstance().emptyOpenPlayerTPort.hasPermissionToRun(player, true)) {
            return;
        }
        tport.teleport(player, false, false,
                "tport.history.locationSource.TPortLocationSource.teleportToLocation.succeeded",
                "tport.history.locationSource.TPortLocationSource.teleportToLocation.tpRequested");
    }
    
    @Override
    public void notSafeToTeleport(Player player) {
        sendErrorTranslation(player, "tport.history.locationSource.TPortLocationSource.notSafeToTeleport", super.tport);
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
    
    @Override
    public boolean getSafetyCheckState(Player player) {
        if (super.tport.getOwner().equals(player.getUniqueId())) {
            return SafetyCheck.SafetyCheckSource.TPORT_OWN.getState(player);
        } else {
            return SafetyCheck.SafetyCheckSource.TPORT_OPEN.getState(player);
        }
    }
    
}
