package com.spaceman.tport.history.locationSource;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class LookLocationSource extends LocationSource {
    
    private final Material lookedMaterial;
    private final EntityType entityType;
    
    public LookLocationSource(Material material) {
        this.lookedMaterial = material;
        this.entityType = null;
    }
    public LookLocationSource(EntityType entityType) {
        this.entityType = entityType;
        this.lookedMaterial = null;
    }
    
    @Override
    public String asString() {
        if (entityType != null) {
            return "look-" + entityType.name();
        }
        if (lookedMaterial != null) {
            return "look-" + lookedMaterial.name();
        }
        return "look";
    }
    
    @Override
    public String getInsertion() {
        return null;
    }
    
    @Override
    public void teleportToLocation(Player player, boolean safetyCheck) {
    
    }
}
