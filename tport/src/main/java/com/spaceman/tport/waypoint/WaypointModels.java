package com.spaceman.tport.waypoint;

import com.spaceman.tport.fancyMessage.inventories.WaypointModel;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import javax.annotation.Nullable;
import java.util.HashMap;

public class WaypointModels {
    
    private final static HashMap<NamespacedKey, WaypointModel> models = new HashMap<>();
    
    public static final WaypointModel tport_waypoint_model = registerWaypointModel(new WaypointModel(Material.OAK_BUTTON, 0,"tport", "tport_waypoint", ""));

    public static WaypointModel registerWaypointModel(WaypointModel model) {
        models.put(model.getNamespacedKey(), model);
        return model;
    }
    
    public static boolean exists(NamespacedKey namespacedKey) {
        return models.containsKey(namespacedKey);
    }
    
    @Nullable
    public static WaypointModel getWaypointModel(NamespacedKey namespacedKey) {
        return models.get(namespacedKey);
    }
    
}
