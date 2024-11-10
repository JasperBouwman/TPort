package com.spaceman.tport.history.locationSource;

import com.spaceman.tport.fancyMessage.encapsulation.BiomeEncapsulation;
import com.spaceman.tport.fancyMessage.inventories.InventoryModel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

import static com.spaceman.tport.commands.tport.SafetyCheck.SafetyCheckSource.TPORT_BACK;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendSuccessTranslation;
import static com.spaceman.tport.inventories.TPortInventories.history_element_biome_tp_model;
import static com.spaceman.tport.tpEvents.TPEManager.requestTeleportPlayer;

public class BiomeLocationSource extends BiomeEncapsulation implements LocationSource {
    
    private Location biomeLoc = null;
    
    public BiomeLocationSource(String biome) {
        super(biome);
    }
    
    @Override
    @Nullable
    public Location getLocation(Player player) {
        return biomeLoc;
    }
    
    @Override
    public void setLocation(Location location) {
        this.biomeLoc = location;
    }
    
    @Override
    public void teleportToLocation(Player player) {
        requestTeleportPlayer(player, biomeLoc,
                () -> sendSuccessTranslation(Bukkit.getPlayer(player.getUniqueId()), "tport.history.locationSource.BiomeLocationSource.teleportToLocation.succeeded", super.biome),
                (p, delay, tickMessage, seconds, secondMessage) -> sendSuccessTranslation(p, "tport.history.locationSource.BiomeLocationSource.teleportToLocation.tpRequested", super.biome, delay, tickMessage, seconds, secondMessage));
    }
    
    @Override
    public void notSafeToTeleport(Player player) {
        sendErrorTranslation(player, "tport.history.locationSource.BiomeLocationSource.notSafeToTeleport", super.biome);
    }
    
    @Override
    public InventoryModel getInventoryModel() {
        return history_element_biome_tp_model;
    }
    
    @Override
    @Nullable
    public String getType() {
        return "BiomeTP";
    }
    
    @Override
    public boolean getSafetyCheckState(Player player) {
        return TPORT_BACK.getState(player);
    }
}
