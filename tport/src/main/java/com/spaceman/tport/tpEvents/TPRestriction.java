package com.spaceman.tport.tpEvents;

import com.spaceman.tport.fancyMessage.Message;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Set;

public abstract class TPRestriction {
    
    private static final HashMap<String, RestrictionCreator> restrictions = new HashMap<>();
    
    public static Set<String> getRestrictions() {
        return restrictions.keySet();
    }
    
    public static TPRestriction getNewRestriction(String restrictionName) {
        if (restrictions.containsKey(restrictionName)) {
            try {
                return restrictions.get(restrictionName).create();
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
    
    public static boolean registerRestriction(RestrictionCreator restriction) {
        Validate.notNull(restriction, "The given RestrictionCreator can not be null");
        return restrictions.put(restriction.create().getRestrictionName(), restriction) != null;
    }
    
    public abstract String getRestrictionName();
    
    public abstract void start(Player player, int taskID);
    
    public abstract boolean shouldTeleport(Player player);
    
    public abstract void cancel();
    
    public abstract Message getDescription();
    
    public void activate() {
    }
    
    public void disable() {
    }
    
    @FunctionalInterface
    public interface RestrictionCreator {
        TPRestriction create();
    }
    
}
