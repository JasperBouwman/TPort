package com.spaceman.tport.waypoint;

import com.spaceman.tport.fancyMessage.inventories.WaypointModel;

import java.util.HashMap;

public class WaypointModels {
    
    private final static HashMap<String, WaypointModel> models = new HashMap<>();
    
    public static final WaypointModel tport_waypoint_model = registerWaypointModel(new WaypointModel("tport", "tport_waypoint", ""));

    public static WaypointModel registerWaypointModel(WaypointModel model) {
        models.put(model.getName(), model);
        return model;
    }
    
    public static boolean exists(String name) {
        return models.containsKey(name);
    }
    
}
