package com.spaceman.tport.history.locationSource;

import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import com.spaceman.tport.fancyMessage.inventories.InventoryModel;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class IgnoreLocationSource implements LocationSource {
    
    @Override
    public String asString() {
        return null;
    }
    
    @Nonnull
    @Override
    public Message toMessage(String color, String varColor) {
        return new Message(new TextComponent(asString(), varColor));
    }
    
    @Nullable
    @Override
    public HoverEvent getHoverEvent() {
        return null;
    }
    
    @Nullable
    @Override
    public ClickEvent getClickEvent() {
        return null;
    }
    
    @Nullable
    @Override
    public String getInsertion() {
        return null;
    }
    
    @Override
    public Location getLocation(Player player) {
        return null;
    }
    
    @Override
    public void setLocation(Location location) { }
    
    @Override
    public void teleportToLocation(Player player) {
        // not used
    }
    
    @Override
    public void notSafeToTeleport(Player player) { }
    
    @Nullable
    @Override
    public InventoryModel getInventoryModel() {
        return null;
    }
    
    @Nullable
    @Override
    public String getType() {
        return null;
    }
    
    @Override
    public boolean getSafetyCheckState(Player player) {
        return false;
    }
}
