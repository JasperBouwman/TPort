package com.spaceman.tport.history;

import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class HistoryFilter {
    
//    private static HashMap<String, Pair<InventoryModel, Filter>> historyFilters = new HashMap<>();
//
//    @FunctionalInterface
//    public interface Filter {
//        boolean matches(String cause);
//    }
//
//    public static void registerFilter(String name, InventoryModel inventoryModel) {
//
//    }
    
    
    
    public static final String PLUGIN_PREFIX = "PLUGIN:";
    
    public static List<String> getFilters() {
        ArrayList<String> filters = new ArrayList<>();
        
        for (PlayerTeleportEvent.TeleportCause cause : PlayerTeleportEvent.TeleportCause.values()) {
            filters.add(cause.name());
        }
        
        filters.add("BOAT");
        filters.add("CHEST_BOAT");
        filters.add("MINECART");
        
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            filters.add(PLUGIN_PREFIX + plugin.getName());
        }
        
        return filters;
    }
    
    @Nullable
    public static String exist(String filter) {
        return getFilters().stream().filter(f -> f.equalsIgnoreCase(filter)).findFirst().orElse(null);
    }
    
    public static boolean fits(HistoryElement element, @Nullable String filter) {
        if (filter == null) return true;
        
        if (filter.startsWith(PLUGIN_PREFIX)) {
            if (element.cause().equals("PLUGIN")) {
                String plugin = filter.substring(PLUGIN_PREFIX.length());
                return element.application() != null && element.application().equals(plugin);
            }
        } else {
            return element.cause().equals(filter);
        }
        return false;
    }
    
}
