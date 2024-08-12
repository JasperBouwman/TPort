package com.spaceman.tport.history.locationSource;

import com.spaceman.tport.commands.tport.SafetyCheck;
import com.spaceman.tport.fancyMessage.encapsulation.BiomeEncapsulation;
import com.spaceman.tport.fancyMessage.inventories.InventoryModel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

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
    public void teleportToLocation(Player player, boolean safetyCheck) {//todo fix message
        if (!safetyCheck || SafetyCheck.isSafe(biomeLoc)) {
            requestTeleportPlayer(player, biomeLoc,
                    () -> sendSuccessTranslation(Bukkit.getPlayer(player.getUniqueId()), "tport.command.back.BIOME.to.succeeded", super.biome),
                    (p, delay, tickMessage, seconds, secondMessage) -> sendSuccessTranslation(p, "tport.command.back.BIOME.to.tpRequested", super.biome, delay, tickMessage, seconds, secondMessage));
        } else {
            sendErrorTranslation(player, "tport.command.back.BIOME.to.notSafe", super.biome);
        }
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
    
}
