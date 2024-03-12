package com.spaceman.tport.history;

import com.spaceman.tport.Main;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import static com.spaceman.tport.fileHander.Files.tportConfig;

public class HistoryEvents implements Listener {
    
    private static final HistoryEvents instance = new HistoryEvents();
    public static HistoryEvents getInstance() {
        return instance;
    }
    
    private HistoryEvents() {
        historySize = tportConfig.getConfig().getInt("history.size", 20);
    }
    
    private static int historySize = 20;
    public static void setHistorySize(int size) {
        tportConfig.getConfig().set("history.size", size);
        tportConfig.saveConfig();
    }
    public static int getHistorySize() {
        return historySize;
    }
    
    public static HashMap<UUID, ArrayList<HistoryElement>> teleportHistory = new HashMap<>();
    
    private static final HashMap<UUID, LocationSource> locationSources = new HashMap<>();
    private static LocationSource getLocationSource(UUID uuid) {
        return Main.getOrDefault(locationSources.remove(uuid), new CraftLocationSource());
    }
    public static void setLocationSource(UUID uuid, LocationSource data) {
        locationSources.put(uuid, data);
    }
    public static void ignoreTeleport(UUID uuid) {
        setLocationSource(uuid, new IgnoreLocationSource());
    }
    
    private void addHistory(Player player, HistoryElement historyElement) {
        Location newLoc = historyElement.newLocation().getLocation(player);
        if (Objects.equals(historyElement.oldLocation().getWorld(), newLoc.getWorld())) {
            if (historyElement.oldLocation().distance(newLoc) < 1) {
                return;
            }
        }
        ArrayList<HistoryElement> history = teleportHistory.getOrDefault(player.getUniqueId(), new ArrayList<>(historySize + 1));
        history.add(historyElement);
        while (history.size() > historySize) history.remove(0);
        teleportHistory.put(player.getUniqueId(), history);
    }
    
    public static void load() {
//        if (Features.Feature.History.isEnabled()) {
//            onStateChange(true);
//        }
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
            
            HistoryElement element = new HistoryElement(vehicleHistory, new CraftLocationSource(vehicle.getLocation()), vehicle.getType().name(), null);
            addHistory(player, element);
        }
    }
    
    @EventHandler
    @SuppressWarnings("unused")
    public void onTeleport(PlayerTeleportEvent e) {
        
        LocationSource locationSource = getLocationSource(e.getPlayer().getUniqueId());
        if (locationSource instanceof IgnoreLocationSource) return;
        locationSource.setLocation(e.getTo());
        
        if (e.getCause().equals(PlayerTeleportEvent.TeleportCause.PLUGIN)) {
            String plugin = findPlugin(searchStack());
            
            HistoryElement element = new HistoryElement(e.getFrom(), locationSource, e.getCause().name(), plugin);
            addHistory(e.getPlayer(), element);
        } else {
            HistoryElement element = new HistoryElement(e.getFrom(), locationSource, e.getCause().name(), null);
            addHistory(e.getPlayer(), element);
        }
    }
    
    @Nullable
    private String searchStack() {
        boolean foundBukkit = false;
        for (StackTraceElement stackTraceElement : new Throwable().getStackTrace()) {
            if (stackTraceElement.getClassName().startsWith("org.bukkit")) {
                foundBukkit = true;
            } else if (foundBukkit) {
                return stackTraceElement.getClassName();
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
