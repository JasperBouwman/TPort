package com.spaceman.tport.fancyMessage.encapsulation;

import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import com.spaceman.tport.history.LocationSource;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

import static com.spaceman.tport.fancyMessage.events.HoverEvent.hoverEvent;

public class FeatureEncapsulation extends LocationSource {
    
    private final String feature;
    
    private Location featureLoc = null;
    
    public FeatureEncapsulation(String biome) {
        this.feature = biome;
    }
    
    @Override
    public String asString() {
        return feature;
    }
    
    @Override
    public HoverEvent getHoverEvent() {
        return hoverEvent("/tport featureTP search " + feature, ColorTheme.ColorType.infoColor);
    }
    
    @Override
    public ClickEvent getClickEvent() {
        return ClickEvent.runCommand("/tport featureTP search " + feature);
    }
    
    @Override
    public String getInsertion() {
        return feature;
    }
    
    @Override
    @Nullable
    public Location getLocation(Player player) {
        return featureLoc;
    }
    
    @Override
    public void teleportToLocation(Player player, boolean safetyCheck) {
    
    }
    
    @Override
    public void setLocation(Location location) {
        this.featureLoc = location;
    }
}
