package com.spaceman.tport.fancyMessage.encapsulation;

import com.spaceman.tport.commands.tport.FeatureTP;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import com.spaceman.tport.history.LocationSource;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

import static com.spaceman.tport.fancyMessage.events.HoverEvent.hoverEvent;

public class WorldEncapsulation extends LocationSource {
    
    private final World world;
    
    public WorldEncapsulation(World world) {
        this.world = world;
    }
    
    @Override
    public String asString() {
        return world.getName();
    }
    
    private String command() {
        return "/tport world " + world.getName();
    }
    
    @Override
    public HoverEvent getHoverEvent() {
        return hoverEvent(command(), ColorTheme.ColorType.infoColor);
    }
    
    @Override
    public ClickEvent getClickEvent() {
        return ClickEvent.runCommand(command());
    }
    
    @Override
    @Nullable
    public Location getLocation(Player player) {
        Location location = FeatureTP.setSafeY(world, world.getSpawnLocation().getBlockX(), world.getSpawnLocation().getBlockZ());
        if (location != null) location.add(0.5, 0.1, 0.5);
        return location;
    }
    
    @Override
    public void teleportToLocation(Player player, boolean safetyCheck) {
        Bukkit.dispatchCommand(player, command());
    }
}
