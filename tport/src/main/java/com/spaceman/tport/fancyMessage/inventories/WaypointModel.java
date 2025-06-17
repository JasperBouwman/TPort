package com.spaceman.tport.fancyMessage.inventories;

import org.bukkit.NamespacedKey;

public class WaypointModel {
    
    private String subDir = "";
    private NamespacedKey namespacedKey;
    
    public WaypointModel(String nameSpace, String key, String subDir) {
        createNameSpaceKey(nameSpace, key);
        this.subDir = subDir;
    }
    public WaypointModel(String nameSpace, String key) {
        createNameSpaceKey(nameSpace, key);
    }
    
    private void createNameSpaceKey(String nameSpace, String key) {
        this.namespacedKey = NamespacedKey.fromString( (nameSpace + ":" + key).toLowerCase() );
    }
    
    public NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }
    
    public boolean hasSubDir() {
        return !subDir.isEmpty();
    }
    public String getSubDir() {
        return subDir;
    }
    
    public String getName() {
        return this.namespacedKey.getKey();
    }
    
}
