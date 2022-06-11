package com.spaceman.tport.metrics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeatureSearchCounter {
    
    private FeatureSearchCounter() { }
    private static final FeatureSearchCounter instance = new FeatureSearchCounter();
    public static FeatureSearchCounter getInstance() {
        return instance;
    }
    
    private final HashMap<String, Integer> count = new HashMap<>();
    
    public static void add(List<String> featureType) {
        featureType.forEach(type -> getInstance().count.merge(type, 1, Integer::sum));
    }
    
    public static Map<String, Integer> getData() {
        Map<String, Integer> data = new HashMap<>(getInstance().count);
        getInstance().count.clear();
        
        return data;
    }
}
