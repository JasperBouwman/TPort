package com.spaceman.tport.history;

import com.spaceman.tport.Main;
import com.spaceman.tport.Pair;
import com.spaceman.tport.commands.tport.Features;
import com.spaceman.tport.fancyMessage.inventories.InventoryModel;
import com.spaceman.tport.history.locationSource.CraftLocationSource;
import com.spaceman.tport.history.locationSource.IgnoreLocationSource;
import com.spaceman.tport.history.locationSource.LocationSource;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.plugin.PluginDescriptionFile;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.UUID;

import static com.spaceman.tport.history.TeleportHistory.addHistory;
import static com.spaceman.tport.history.TeleportHistory.getTeleportData;

public class HistoryEvents implements Listener {
    
    private static final HistoryEvents instance = new HistoryEvents();
    public static HistoryEvents getInstance() {
        return instance;
    }
    
    public static void load() {
        if (Features.Feature.History.isEnabled()) {
            onStateChange(true);
        }
    }
    public static void onStateChange(boolean newState) {
        if (newState) {
            Bukkit.getPluginManager().registerEvents(getInstance(), Main.getInstance());
        } else {
            HandlerList.unregisterAll(getInstance());
        }
    }
    
    private final HashMap<UUID, Location> vehicleHistories = new HashMap<>();
    @EventHandler
    @SuppressWarnings("unused")
    public void minecraftEvent(VehicleEnterEvent e) {
        if (!e.getEntered().getType().equals(EntityType.PLAYER)) {
            return;
        }
        Entity vehicle = e.getVehicle();
        
        vehicleHistories.put(e.getEntered().getUniqueId(), vehicle.getLocation());
    }
    
    @EventHandler
    @SuppressWarnings("unused")
    public void minecraftEvent(VehicleExitEvent e) {
        if (!e.getExited().getType().equals(EntityType.PLAYER)) {
            return;
        }
        Entity vehicle = e.getVehicle();
        
        Location vehicleHistory = vehicleHistories.remove(e.getExited().getUniqueId());
        if (vehicleHistory != null) {
            Player player = (Player) e.getExited();
            
            HistoryElement element = new HistoryElement(vehicleHistory,
                    new CraftLocationSource(vehicle.getLocation()), vehicle.getType().name(), null, null); //todo add vehicle inventory model
            addHistory(player, element);
        }
    }
    
    @EventHandler
    @SuppressWarnings("unused")
    public void onTeleport(PlayerTeleportEvent e) {
        Pair<LocationSource, InventoryModel> teleportData = getTeleportData(e.getPlayer().getUniqueId());
        if (teleportData.getLeft() instanceof IgnoreLocationSource) return;
        teleportData.getLeft().setLocation(e.getTo());
        
        String plugin = null;
        if (e.getCause().equals(PlayerTeleportEvent.TeleportCause.PLUGIN)) {
            plugin = findPlugin(searchStack());
        }
        InventoryModel inventoryModel = teleportData.getRight();
        HistoryElement element = new HistoryElement(e.getFrom(), teleportData.getLeft(), e.getCause().name(), plugin, inventoryModel);
        addHistory(e.getPlayer(), element);
    }
    
    @Nullable
    private String searchStack() {
        boolean foundCraftPlayer = false;
        for (StackTraceElement stackTraceElement : new Throwable().getStackTrace()) {
            String stackClassName = stackTraceElement.getClassName();
            if (stackClassName.endsWith("entity.CraftPlayer")) {
                foundCraftPlayer = true;
            } else if (foundCraftPlayer) {
                if (stackClassName.endsWith("entity.CraftEntity")) {
                    continue;
                }
                
                return stackClassName;
            }
        }
        return null;
    }
    
    @Nullable
    private String findPlugin(@Nullable String className) {
        if (className == null) return null;
        try {
            Class<?> executedClass = Class.forName(className);
            
            Class<?> pluginClassLoaderClass = Class.forName("org.bukkit.plugin.java.PluginClassLoader");
            Field descriptionField = pluginClassLoaderClass.getDeclaredField("description");
            descriptionField.setAccessible(true);
            
            ClassLoader classLoader = executedClass.getClassLoader();
            PluginDescriptionFile description = (PluginDescriptionFile) descriptionField.get(classLoader);
            return description.getName();
        } catch (Throwable ignore) { }
        return null;
    }
    
}
