package com.spaceman.tport.metrics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BiomeSearchCounter {
    
    private BiomeSearchCounter() { }
    private static final BiomeSearchCounter instance = new BiomeSearchCounter();
    public static BiomeSearchCounter getInstance() {
        return instance;
    }
    
    private final HashMap<String, Integer> count = new HashMap<>();
    
    public static void add(List<String> biomes) {
        for (String b : biomes) {
            getInstance().count.merge(b, 1, Integer::sum);
        }
    }
    
    public static Map<String, Integer> getData() {
    
        Map<String, Integer> data = new HashMap<>(getInstance().count);
        getInstance().count.clear();
        
        return data;
    }
}
