package com.spaceman.tport.history.locationSource;

import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import com.spaceman.tport.fancyMessage.inventories.InventoryModel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.spaceman.tport.commands.tport.SafetyCheck.SafetyCheckSource.TPORT_BACK;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendSuccessTranslation;
import static com.spaceman.tport.inventories.TPortInventories.history_element_look_model;
import static com.spaceman.tport.tpEvents.TPEManager.requestTeleportPlayer;

public class LookLocationSource implements LocationSource {
    
    private final Material lookedMaterial;
    private final EntityType entityType;
    private Location location;
    
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
    
    @Override
    public String getInsertion() {
        return asString();
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
    public void teleportToLocation(Player player) {//todo fix message
        requestTeleportPlayer(player, location,
                () -> sendSuccessTranslation(Bukkit.getPlayer(player.getUniqueId()), "tport.history.locationSource.LookLocationSource.teleportToLocation.succeeded", this.asString()),
                (p, delay, tickMessage, seconds, secondMessage) -> sendSuccessTranslation(p, "tport.history.locationSource.LookLocationSource.teleportToLocation.tpRequested", this.asString(), delay, tickMessage, seconds, secondMessage));
    }
    
    @Override
    public void notSafeToTeleport(Player player) {
        sendErrorTranslation(player, "tport.history.locationSource.LookLocationSource.notSafeToTeleport", this.asString());
    }
    
    @Override
    public InventoryModel getInventoryModel() {
        return history_element_look_model;
    }
    
    @Override
    @Nullable
    public String getType() {
        return "LookTP";
    }
    
    @Override
    public boolean getSafetyCheckState(Player player) {
        return TPORT_BACK.getState(player);
    }
    
}
