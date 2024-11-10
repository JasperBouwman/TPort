package com.spaceman.tport.history.locationSource;

import com.spaceman.tport.fancyMessage.encapsulation.FeatureEncapsulation;
import com.spaceman.tport.fancyMessage.inventories.InventoryModel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

import static com.spaceman.tport.commands.tport.SafetyCheck.SafetyCheckSource.TPORT_BACK;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendSuccessTranslation;
import static com.spaceman.tport.inventories.TPortInventories.history_element_feature_tp_model;
import static com.spaceman.tport.tpEvents.TPEManager.requestTeleportPlayer;

public class FeatureLocationSource extends FeatureEncapsulation implements LocationSource {
    
    private Location featureLoc = null;
    
    public FeatureLocationSource(String feature) {
        super(feature);
    }
    
    @Override
    @Nullable
    public Location getLocation(Player player) {
        return featureLoc;
    }
    
    @Override
    public void setLocation(Location location) {
        this.featureLoc = location;
    }
    
    @Override
    public void teleportToLocation(Player player) {
        requestTeleportPlayer(player, featureLoc,
                () -> sendSuccessTranslation(Bukkit.getPlayer(player.getUniqueId()), "tport.history.locationSource.FeatureLocationSource.teleportToLocation.succeeded", super.feature),
                (p, delay, tickMessage, seconds, secondMessage) -> sendSuccessTranslation(p, "tport.history.locationSource.FeatureLocationSource.teleportToLocation.tpRequested", super.feature, delay, tickMessage, seconds, secondMessage));
    }
    
    @Override
    public void notSafeToTeleport(Player player) {
        sendErrorTranslation(player, "tport.history.locationSource.FeatureLocationSource.notSafeToTeleport", super.feature);
    }
    
    @Override
    public InventoryModel getInventoryModel() {
        return history_element_feature_tp_model;
    }
    
    @Override
    @Nullable
    public String getType() {
        return "FeatureTP";
    }
    
    @Override
    public boolean getSafetyCheckState(Player player) {
        return TPORT_BACK.getState(player);
    }
    
}
