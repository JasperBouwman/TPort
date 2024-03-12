package com.spaceman.tport.history;

import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class HistoryFilter {
    
    private static final String pluginPrefix = "PLUGIN:";
    
    public static List<String> getFilters() {
        ArrayList<String> filters = new ArrayList<>();
        
        for (PlayerTeleportEvent.TeleportCause cause : PlayerTeleportEvent.TeleportCause.values()) {
            filters.add(cause.name());
        }
        
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            filters.add(pluginPrefix + plugin.getName());
        }
        
        return filters;
    }
    
    @Nullable
    public static String exist(String filter) {
        return getFilters().stream().filter(f -> f.equalsIgnoreCase(filter)).findFirst().orElse(null);
    }
    
    public static boolean fits(HistoryElement element, String filter) {
        if (filter.startsWith(pluginPrefix)) {
            if (element.cause().equals("PLUGIN")) {
                String plugin = filter.substring(pluginPrefix.length());
                return element.application() != null && element.application().equals(plugin);
            }
        } else {
            return element.cause().equals(filter);
        }
        return false;
    }
    
}
