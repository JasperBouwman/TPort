package com.spaceman.tport.history;

import com.spaceman.tport.Main;
import com.spaceman.tport.Pair;
import com.spaceman.tport.fancyMessage.inventories.InventoryModel;
import com.spaceman.tport.history.locationSource.CraftLocationSource;
import com.spaceman.tport.history.locationSource.IgnoreLocationSource;
import com.spaceman.tport.history.locationSource.LocationSource;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import static com.spaceman.tport.fileHander.Files.tportConfig;

public class TeleportHistory {
    
    public static HashMap<UUID, ArrayList<HistoryElement>> teleportHistory = new HashMap<>();
    
    public static void setHistorySize(int size) {
        tportConfig.getConfig().set("history.size", size);
        tportConfig.saveConfig();
    }
    public static int getHistorySize() {
        return tportConfig.getConfig().getInt("history.size", 20);
    }
    
    
    public static HashMap<String, InventoryModel> pluginFilterModels = new HashMap<>();
    public static boolean registerPluginFilterModel(String pluginName, InventoryModel model) {
        return pluginFilterModels.put(pluginName, model) != null;
    }
    public static boolean registerPluginFilterModel(Plugin plugin, InventoryModel model) {
        return registerPluginFilterModel(plugin.getName(), model);
    }
    
    
    private static final HashMap<UUID, Pair<LocationSource, InventoryModel>> locationSources = new HashMap<>();
    static Pair<LocationSource, InventoryModel> getTeleportData(UUID uuid) {
        return Main.getOrDefault(locationSources.remove(uuid), new Pair<>(new CraftLocationSource(), null));
    }
    public static void setLocationSource(UUID uuid, LocationSource data) {
        setLocationSource(uuid, data, data.getInventoryModel());
    }
    public static void setLocationSource(UUID uuid, LocationSource data, @Nullable InventoryModel inventoryModel) {
        locationSources.put(uuid, new Pair<>(data, inventoryModel));
    }
    public static void ignoreTeleport(UUID uuid) {
        setLocationSource(uuid, new IgnoreLocationSource(), null);
    }
    
    
    static void addHistory(Player player, HistoryElement historyElement) {
        Location newLoc = historyElement.newLocation().getLocation(player);
        if (Objects.equals(historyElement.oldLocation().getWorld(), newLoc.getWorld())) {
            if (historyElement.oldLocation().distance(newLoc) < 1) {
                return;
            }
        }
        ArrayList<HistoryElement> history = teleportHistory.getOrDefault(player.getUniqueId(), new ArrayList<>(getHistorySize() + 1));
        history.add(historyElement);
        while (history.size() > getHistorySize()) history.remove(0);
        teleportHistory.put(player.getUniqueId(), history);
    }
}
