package com.spaceman.tport.history.locationSource;

import com.spaceman.tport.commands.tport.SafetyCheck;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import com.spaceman.tport.fancyMessage.inventories.InventoryModel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
    public void teleportToLocation(Player player, boolean safetyCheck) {//todo fix message
        if (!safetyCheck || SafetyCheck.isSafe(location)) {
            requestTeleportPlayer(player, location,
                    () -> sendSuccessTranslation(Bukkit.getPlayer(player.getUniqueId()), "tport.command.back.BIOME.to.succeeded", "super.biome"),
                    (p, delay, tickMessage, seconds, secondMessage) -> sendSuccessTranslation(p, "tport.command.back.BIOME.to.tpRequested", "super.biome", delay, tickMessage, seconds, secondMessage));
        } else {
            sendErrorTranslation(player, "tport.command.back.BIOME.to.notSafe", "super.biome");
        }
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
}