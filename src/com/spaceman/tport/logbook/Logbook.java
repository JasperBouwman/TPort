package com.spaceman.tport.logbook;

import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.playerUUID.PlayerUUID;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static com.spaceman.tport.fileHander.GettingFiles.getFile;

public class Logbook {
    
    private static HashMap<UUID, LogMode> getLog(UUID uuid, String tport) {
        Files tportData = getFile("TPortData");
        
        if (tportData.getConfig().contains("tport." + uuid + ".items")) {
            for (String slot : tportData.getConfig().getConfigurationSection("tport." + uuid + ".items").getKeys(false)) {
                if (tportData.getConfig().contains("tport." + uuid + ".items." + slot)) {
                    if (tport.equalsIgnoreCase(tportData.getConfig().getString("tport." + uuid + ".items." + slot + ".name"))) {
                        if (tportData.getConfig().contains("tport." + uuid + ".items." + slot + ".logSetting")) {
                            HashMap<UUID, LogMode> log = new HashMap<>();
                            for (String tmpUUID : tportData.getConfig().getConfigurationSection("tport." + uuid + ".items." + slot + ".logSetting").getKeys(false)) {
                                log.put(UUID.fromString(tmpUUID), LogMode.valueOf(tportData.getConfig().getString("tport." + uuid + ".items." + slot + ".logSetting." + tmpUUID)));
                            }
                            return log;
                        } else {
                            return new HashMap<>();
                        }
                    }
                }
            }
        }
        return new HashMap<>();
    }
    
    private static void saveLog(UUID uuid, String tport, HashMap<UUID, LogMode> log) {
        Files tportData = getFile("TPortData");
        
        if (tportData.getConfig().contains("tport." + uuid + ".items")) {
            for (String slot : tportData.getConfig().getConfigurationSection("tport." + uuid + ".items").getKeys(false)) {
                if (tportData.getConfig().contains("tport." + uuid + ".items." + slot)) {
                    if (tport.equalsIgnoreCase(tportData.getConfig().getString("tport." + uuid + ".items." + slot + ".name"))) {
                        tportData.getConfig().set("tport." + uuid + ".items." + slot + ".logSetting", null);
                        for (UUID tmpUUID : log.keySet()) {
                            tportData.getConfig().set("tport." + uuid + ".items." + slot + ".logSetting." + tmpUUID.toString(), log.get(tmpUUID).name());
                        }
                        tportData.saveConfig();
                    }
                }
            }
        }
    }
    
    public static void addPlayer(UUID uuid, String tport, UUID addUUID, LogMode logMode) {
        HashMap<UUID, LogMode> log = getLog(uuid, tport);
        if (log.containsKey(addUUID)) {
            throw new IllegalArgumentException("Given player is already in the TPort log");
        }
        
        log.put(addUUID, logMode);
        saveLog(uuid, tport, log);
        if (Bukkit.getPlayer(addUUID) != null) {
            Bukkit.getPlayer(addUUID).sendMessage("You are now added to the TPort log of the TPort " + tport + " from " + PlayerUUID.getPlayerName(uuid.toString()));
        }
    }
    
    public static void addPlayer(UUID uuid, String tport, UUID addUUID) {
        addPlayer(uuid, tport, addUUID, LogMode.ALL);
    }
    
    public static void removePlayer(UUID uuid, String tport, UUID removeUUID) {
        HashMap<UUID, LogMode> log = getLog(uuid, tport);
        if (!log.containsKey(removeUUID)) {
            throw new IllegalArgumentException("Given player is not in the TPort log");
        }
        log.remove(removeUUID);
        saveLog(uuid, tport, log);
        if (Bukkit.getPlayer(removeUUID) != null) {
            Bukkit.getPlayer(removeUUID).sendMessage("You are now removed from the TPort log of the TPort " + tport + " from " + PlayerUUID.getPlayerName(uuid.toString()));
        }
    }
    
    public static boolean isLogged(UUID uuid, String tport, UUID loggedUUID) {
        HashMap log = getLog(uuid, tport);
        return log.containsKey(loggedUUID);
    }
    
    public static void setPlayerMode(UUID uuid, String tport, UUID nUUID, LogMode logMode) {
        HashMap<UUID, LogMode> log = getLog(uuid, tport);
        if (!log.containsKey(nUUID)) {
            throw new IllegalArgumentException("Given player is nog in the TPort log");
        }
        log.put(nUUID, logMode);
        saveLog(uuid, tport, log);
    }
    
    public static LogMode getPlayerMode(UUID uuid, String tport, UUID nUUID) {
        HashMap<UUID, LogMode> log = getLog(uuid, tport);
        if (!log.containsKey(nUUID)) {
            throw new IllegalArgumentException("Given player is nog in the TPort log");
        }
        return log.get(nUUID);
    }
    
    public static ArrayList<String> getList(UUID uuid) {
        Files tportData = getFile("TPortData");
        ArrayList<String> list = new ArrayList<>();
        if (tportData.getConfig().contains("tport." + uuid + ".items")) {
            for (String slot : tportData.getConfig().getConfigurationSection("tport." + uuid + ".items").getKeys(false)) {
                if (tportData.getConfig().contains("tport." + uuid + ".items." + slot)) {
                    String tport = tportData.getConfig().getString("tport." + uuid + ".items." + slot + ".name");
                    
                    if (tportData.getConfig().contains("tport." + uuid + ".items." + slot + ".logSetting")) {
                        HashMap<UUID, LogMode> log = new HashMap<>();
                        for (String tmpUUID : tportData.getConfig().getConfigurationSection("tport." + uuid + ".items." + slot + ".logSetting").getKeys(false)) {
                            log.put(UUID.fromString(tmpUUID), LogMode.valueOf(tportData.getConfig().getString("tport." + uuid + ".items." + slot + ".logSetting." + tmpUUID)));
                        }
                        if (!log.isEmpty()) {
                            list.add(tport);
                        }
                    }
                }
            }
        }
        
        return list;
    }
    
    
    public static void log(UUID uuid, String tport, UUID userUUID) {
        if (!uuid.equals(userUUID)) {
            //todo
        }
    }

    public static void clearLog(UUID uuid, String tport) {
        //todo
        Files tportData = getFile("TPortData");
        
        if (tportData.getConfig().contains("tport." + uuid + ".items")) {
            for (String slot : tportData.getConfig().getConfigurationSection("tport." + uuid + ".items").getKeys(false)) {
                if (tportData.getConfig().contains("tport." + uuid + ".items." + slot)) {
                    if (tport.equalsIgnoreCase(tportData.getConfig().getString("tport." + uuid + ".items." + slot + ".name"))) {
                        tportData.getConfig().set("tport." + uuid + ".items." + slot + ".log", null);
                    }
                }
            }
        }
    }
    
    public enum LogMode {
        ONLINE,
        OFFLINE,
        ALL
    }
    
}
