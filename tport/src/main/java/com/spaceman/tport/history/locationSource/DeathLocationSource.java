package com.spaceman.tport.history.locationSource;

import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.TextComponent;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class DeathLocationSource extends LocationSource {
    
    private final Location location;
    
    public DeathLocationSource(Location location) {
        this.location = location;
    }
    
    @Override
    public String asString() {
        return null;
    }
    
    @Override
    public Message toMessage(String color, String varColor) {
        return new Message(new TextComponent("death", varColor));
    }
    
    @Override
    @Nullable
    public Location getLocation(Player player) {
        return location;
    }
    
    @Override
    public void teleportToLocation(Player player, boolean safetyCheck) {
    
    }
}
