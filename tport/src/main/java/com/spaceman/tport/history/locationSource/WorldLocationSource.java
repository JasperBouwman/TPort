package com.spaceman.tport.history.locationSource;

import com.spaceman.tport.commands.tport.FeatureTP;
import com.spaceman.tport.fancyMessage.encapsulation.WorldEncapsulation;
import com.spaceman.tport.fancyMessage.inventories.InventoryModel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

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
    public void teleportToLocation(Player player, boolean safetyCheck) { //todo add safetyCheck
        Bukkit.dispatchCommand(player, "tport world " + world);
//        Bukkit.dispatchCommand(player, "tport world " + world + " " + safetyCheck);
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
    
}
