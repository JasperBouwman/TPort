package com.spaceman.tport.metrics;

import java.util.HashMap;
import java.util.Map;

public class CommandCounter {
    
    private CommandCounter() { }
    private static final CommandCounter instance = new CommandCounter();
    public static CommandCounter getInstance() {
        return instance;
    }
    
    private final HashMap<String, Integer> count = new HashMap<>();
    
    public static void add(String[] args) {
        String command = "TPort";
        if (args.length >= 1) command += " " + args[0].toLowerCase();
        getInstance().count.merge(command, 1, Integer::sum);
    }
    
    public static Map<String, Integer> getData() {
    
        Map<String, Integer> data = new HashMap<>(getInstance().count);
        getInstance().count.clear();
        
        return data;
    }
}
