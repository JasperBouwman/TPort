package com.spaceman.tport.history.locationSource;

import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import com.spaceman.tport.fancyMessage.inventories.InventoryModel;
import com.spaceman.tport.fancyMessage.language.Language;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.spaceman.tport.commands.tport.SafetyCheck.SafetyCheckSource.TPORT_BACK;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.inventories.TPortInventories.history_element_death_model;
import static com.spaceman.tport.tpEvents.TPEManager.requestTeleportPlayer;

public class DeathLocationSource implements LocationSource {
    
    private final Location location;
    
    public DeathLocationSource(Location location) {
        this.location = location;
    }
    
    @Override
    public String asString() {
        return null;
    }
    
    @Nonnull
    @Override
    public Message toMessage(String color, String varColor) {
        return new Message(new TextComponent("death", varColor));
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
    @Nullable
    public Location getLocation(Player player) {
        return location;
    }
    
    @Override
    public void setLocation(Location location) { }
    
    @Override
    public void teleportToLocation(Player player) { //todo fix message
        Message death = formatTranslation(ColorTheme.ColorType.varInfoColor, ColorTheme.ColorType.varInfoColor, "tport.history.locationSource.DeathLocationSource.teleportToLocation.death").translateMessage(Language.getPlayerLang(player));
        requestTeleportPlayer(player, location,
                () -> sendSuccessTranslation(Bukkit.getPlayer(player.getUniqueId()), "tport.history.locationSource.DeathLocationSource.teleportToLocation.succeeded", death),
                (p, delay, tickMessage, seconds, secondMessage) -> sendSuccessTranslation(p, "tport.history.locationSource.DeathLocationSource.teleportToLocation.tpRequested", death, delay, tickMessage, seconds, secondMessage));
    }
    
    @Override
    public void notSafeToTeleport(Player player) {
        sendErrorTranslation(player, "tport.history.locationSource.DeathLocationSource.notSafeToTeleport");
    }
    
    @Override
    public InventoryModel getInventoryModel() {
        return history_element_death_model;
    }
    
    @Override
    @Nullable
    public String getType() {
        return "DeathTP";
    }
    
    @Override
    public boolean getSafetyCheckState(Player player) {
        return TPORT_BACK.getState(player);
    }

}
