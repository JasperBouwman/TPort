package com.spaceman.tport.history.locationSource;

import com.spaceman.tport.commands.tport.SafetyCheck;
import com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation;
import com.spaceman.tport.fancyMessage.inventories.InventoryModel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

import static com.spaceman.tport.commands.tport.SafetyCheck.SafetyCheckSource.TPORT_BACK;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;
import static com.spaceman.tport.inventories.TPortInventories.history_element_player_model;

public class PlayerLocationSource extends PlayerEncapsulation implements LocationSource {
    
    public PlayerLocationSource(@Nonnull UUID uuid) {
        super(uuid);
    }
    
    public PlayerLocationSource(@Nonnull Player player) {
        super(player);
    }
    
    @Override
    @Nullable
    public Location getLocation(Player player) {
        Player toPlayer = Bukkit.getPlayer(this.name);
        if (toPlayer == null) {
            return null;
        }
        return toPlayer.getLocation();
    }
    
    @Override
    public void setLocation(Location location) { }
    
    @Override
    public void teleportToLocation(Player player) {
        String command = "tport pltp tp " + this.name;
        Bukkit.dispatchCommand(player, command);
    }
    
    @Override
    public void notSafeToTeleport(Player player) {
        sendErrorTranslation(player, "tport.history.locationSource.PlayerLocationSource.notSafeToTeleport");
    }
    
    @Override
    public InventoryModel getInventoryModel() {
        return history_element_player_model;
    }
    
    @Override
    @Nullable
    public String getType() {
        return "PLTP";
    }
    
    @Override
    public boolean getSafetyCheckState(Player player) {
        return SafetyCheck.SafetyCheckSource.PLTP.getState(player);
    }
    
}
