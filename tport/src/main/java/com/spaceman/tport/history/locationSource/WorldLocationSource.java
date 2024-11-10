package com.spaceman.tport.history.locationSource;

import com.spaceman.tport.commands.tport.FeatureTP;
import com.spaceman.tport.fancyMessage.encapsulation.WorldEncapsulation;
import com.spaceman.tport.fancyMessage.inventories.InventoryModel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

import static com.spaceman.tport.commands.tport.SafetyCheck.SafetyCheckSource.TPORT_BACK;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;
import static com.spaceman.tport.inventories.TPortInventories.history_element_world_tp_model;

public class WorldLocationSource extends WorldEncapsulation implements LocationSource {
    
    public WorldLocationSource(World world) {
        super(world);
    }
    
    @Override
    @Nullable
    public Location getLocation(Player player) {
        Location location = FeatureTP.setSafeY(world, world.getSpawnLocation().getBlockX(), world.getSpawnLocation().getBlockZ());
        if (location != null) location.add(0.5, 0.1, 0.5);
        return location;
    }
    
    @Override
    public void setLocation(Location location) { }
    
    @Override
    public void teleportToLocation(Player player) { //todo add safetyCheck
        String command = "tport world " + this.world;
//        if (safetyCheck != null) command += " " + safetyCheck;
        Bukkit.dispatchCommand(player, command);
    }
    
    @Override
    public void notSafeToTeleport(Player player) {
        sendErrorTranslation(player, "tport.history.locationSource.WorldLocationSource.notSafeToTeleport");
    }
    
    @Override
    public InventoryModel getInventoryModel() {
        return history_element_world_tp_model;
    }
    
    @Override
    @Nullable
    public String getType() {
        return "WorldTP";
    }
    
    @Override
    public boolean getSafetyCheckState(Player player) {
        return TPORT_BACK.getState(player); // todo add safety check
    }
    
}
