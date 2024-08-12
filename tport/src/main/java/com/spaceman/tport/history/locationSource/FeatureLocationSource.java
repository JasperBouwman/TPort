package com.spaceman.tport.history.locationSource;

import com.spaceman.tport.commands.tport.SafetyCheck;
import com.spaceman.tport.fancyMessage.encapsulation.FeatureEncapsulation;
import com.spaceman.tport.fancyMessage.inventories.InventoryModel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

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
    public void teleportToLocation(Player player, boolean safetyCheck) {//todo fix message
        if (!safetyCheck || SafetyCheck.isSafe(featureLoc)) {
            requestTeleportPlayer(player, featureLoc,
                    () -> sendSuccessTranslation(Bukkit.getPlayer(player.getUniqueId()), "tport.command.back.FEATURE.to.succeeded", super.feature),
                    (p, delay, tickMessage, seconds, secondMessage) -> sendSuccessTranslation(p, "tport.command.back.FEATURE.to.tpRequested", super.feature, delay, tickMessage, seconds, secondMessage));
        } else {
            sendErrorTranslation(player, "tport.command.back.FEATURE.to.notSafe", super.feature);
        }
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
    
}
