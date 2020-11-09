package com.spaceman.tport.metrics;

import org.bukkit.block.Biome;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BiomeSearchCounter {
    
    private static BiomeSearchCounter instance = null;
    
    public static BiomeSearchCounter getInstance() {
        if (instance == null) {
            instance = new BiomeSearchCounter();
        }
        return instance;
    }
    
    private BiomeSearchCounter() { }
    
    private final HashMap<String, Integer> count = new HashMap<>();
    
    public static void add(List<Biome> biomes) {
        for (Biome b : biomes) {
            getInstance().count.merge(b.name(), 1, Integer::sum);
        }
    }
    
    public static Map<String, Integer> getData() {
        Map<String, Integer> data = new HashMap<>();
        
        getInstance().count.forEach(data::put);
        getInstance().count.clear();
        
        return data;
    }
}
