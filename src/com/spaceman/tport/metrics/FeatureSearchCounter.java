package com.spaceman.tport.metrics;

import com.spaceman.tport.commands.tport.FeatureTP;

import java.util.HashMap;
import java.util.Map;

public class FeatureSearchCounter {
    
    private static FeatureSearchCounter instance = null;
    
    public static FeatureSearchCounter getInstance() {
        if (instance == null) {
            instance = new FeatureSearchCounter();
        }
        return instance;
    }
    
    private FeatureSearchCounter() {
    }
    
    private final HashMap<String, Integer> count = new HashMap<>();
    
    public static void add(FeatureTP.FeatureType featureType) {
        getInstance().count.merge(featureType.name().toLowerCase(), 1, Integer::sum);
    }
    
    public static Map<String, Integer> getData() {
        Map<String, Integer> data = new HashMap<>();
        
        getInstance().count.forEach(data::put);
        getInstance().count.clear();
        
        return data;
    }
}
