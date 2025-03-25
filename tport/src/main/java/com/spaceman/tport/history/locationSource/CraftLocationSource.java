package com.spaceman.tport.history.locationSource;

import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import com.spaceman.tport.fancyMessage.inventories.InventoryModel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.spaceman.tport.commands.tport.SafetyCheck.SafetyCheckSource.TPORT_BACK;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.tpEvents.TPEManager.requestTeleportPlayer;

public class CraftLocationSource implements LocationSource {
    
    private Location location;
    private String type = null;
    
    public CraftLocationSource() { }
    
    public CraftLocationSource(Location location) {
        setLocation(location);
    }
    
    public CraftLocationSource(Location location, @Nullable String type) {
        setLocation(location);
        this.type = type;
    }
    
    @Nonnull
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
    public String asString() {
        return null;
    }
    
    @Nullable
    @Override
    public Location getLocation(Player player) {
        return location;
    }
    
    @Override
    public void setLocation(Location location) {
        this.location = location;
    }
    
    @Override
    public void teleportToLocation(Player player) {
        requestTeleportPlayer(player, location,
                () -> sendSuccessTranslation(Bukkit.getPlayer(player.getUniqueId()), "tport.history.locationSource.CraftLocationSource.teleportToLocation.succeeded"),
                (p, delay, tickMessage, seconds, secondMessage) -> sendSuccessTranslation(p, "tport.history.locationSource.CraftLocationSource.teleportToLocation.tpRequested", "null", delay, tickMessage, seconds, secondMessage));
    }
    
    @Override
    public void notSafeToTeleport(Player player) {
        sendErrorTranslation(player, "tport.history.locationSource.CraftLocationSource.notSafeToTeleport");
    }
    
    @Override
    public InventoryModel getInventoryModel() {
        return null;
    }
    
    @Override
    @Nullable
    public String getType() {
        return type;
    }
    
    @Override
    public boolean getSafetyCheckState(Player player) {
        return TPORT_BACK.getState(player);
    }
}