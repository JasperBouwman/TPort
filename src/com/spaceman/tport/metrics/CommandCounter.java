package com.spaceman.tport.metrics;

import java.util.HashMap;
import java.util.Map;

public class CommandCounter {
    
    private static CommandCounter instance = null;
    
    public static CommandCounter getInstance() {
        if (instance == null) {
            instance = new CommandCounter();
        }
        return instance;
    }
    
    private CommandCounter() { }
    
    private final HashMap<String, Integer> count = new HashMap<>();
    
    public static void add(String[] args) {
        String command = "TPort";
        if (args.length >= 1) command += " " + args[0].toLowerCase();
        getInstance().count.merge(command, 1, Integer::sum);
    }
    
    public static Map<String, Integer> getData() {
        Map<String, Integer> data = new HashMap<>();
        
        getInstance().count.forEach(data::put);
        getInstance().count.clear();
        
        return data;
    }
}
