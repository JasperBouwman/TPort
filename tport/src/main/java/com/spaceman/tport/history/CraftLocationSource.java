package com.spaceman.tport.history;

import com.spaceman.tport.fancyMessage.Message;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatTranslation;

public class CraftLocationSource extends LocationSource {
    
    public CraftLocationSource() {
    
    }
    
    public CraftLocationSource(Location location) {
        setLocation(location);
    }
    
    @Override
    public Message toMessage(String color, String varColor) {
        return formatTranslation(color, varColor, "tport.history.craftLocationSource.getName",
                this.location.getWorld().getName(),
                this.location.getBlockX(),
                this.location.getBlockY(),
                this.location.getBlockZ(),
                (double) Math.round(this.location.getX() * 10) / 10,
                (double) Math.round(this.location.getY() * 10) / 10,
                (double) Math.round(this.location.getZ() * 10) / 10);
    }
    
    @Override
    public String asString() {
        return null;
    }
    
    
    @Override
    public void teleportToLocation(Player player, boolean safetyCheck) {
        //todo create teleport to location
    }
}