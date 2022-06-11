package com.spaceman.tport.tport;

import com.spaceman.tport.Main;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.tpEvents.TPRequest;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendSuccessTranslation;
import static com.spaceman.tport.fileHander.Files.tportData;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;
import static com.spaceman.tport.permissions.PermissionHandler.sendNoPermMessage;

public class TPortManager {
    
    public static final int TPortSize = 24;
    public final static UUID defUUID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    
    public static ArrayList<TPort> getTPortList(UUID owner) {
        return getTPortList(tportData, owner);
    }
    
    public static ArrayList<TPort> getTPortList(Files tportData, UUID owner) {
        ArrayList<TPort> tportList = new ArrayList<>();
        for (String tportID : tportData.getKeys("tport." + owner + ".tports")) {
            TPort tport = (TPort) tportData.getConfig().get("tport." + owner + ".tports." + tportID);
            if (tport != null) {
                tport.setOwner(owner);
                tport.setTportID(UUID.fromString(tportID));
                tportList.add(tport);
            }
        }
        return tportList;
    }
    //returns list of TPorts where the TPort slot is in the correct index
    public static List<TPort> getSortedTPortList(Files tportData, UUID owner) {
        TPort[] tportList = new TPort[TPortSize];
        for (String tportID : tportData.getKeys("tport." + owner + ".tports")) {
            TPort tport = (TPort) tportData.getConfig().get("tport." + owner + ".tports." + tportID);
            if (tport != null) {
                tport.setOwner(owner);
                tport.setTportID(UUID.fromString(tportID));
                tportList[tport.getSlot()] = tport;
            }
        }
        return Arrays.asList(tportList);
    }
    
    /*
     * returns given tport if added,
     * returns null when TPort could not be added
     */
    public static TPort addTPort(Player owner, TPort tport, boolean sendMessage) {
        
        //check permission
        int maxTPorts = TPortSize;
        boolean hasPer = false;
        for (int i = TPortSize; i > 0; i--) {
            if (hasPermission(owner, "TPort.add." + i, false)) {
                hasPer = true;
                maxTPorts = i;
                break;
            }
        }
        
        if (!hasPer && !hasPermission(owner, false, true, "TPort.add", "TPort.basic")) {
            if (sendMessage) sendNoPermMessage(owner, "TPort.add.[X]", "TPort.basic");
            return null;
        }
        
        try {
            Long.parseLong(tport.getName());
            sendErrorTranslation(owner, "tport.tport.tportManager.addTPort.numberName");
            return null;
        } catch (NumberFormatException ignore) {
        }
        
        if (Main.containsSpecialCharacter(tport.getName())) {
            sendErrorTranslation(owner, "tport.tport.tportManager.addTPort.specialChars", "A-Z", "0-9", "-", "_");
            return null;
        }
        
        for (TPort tmpTPort : getTPortList(owner.getUniqueId())) {
            if (tmpTPort.getName().equalsIgnoreCase(tport.getName())) {
                if (sendMessage) sendErrorTranslation(owner, "tport.tport.tportManager.addTPort.nameUsed", tmpTPort, tport.getName());
                return null;
            }
        }
        
        int slot = getNextSlot(owner.getUniqueId(), 0, maxTPorts);
        
        if (slot == -1) {
            if (sendMessage) sendErrorTranslation(owner, "tport.tport.tportManager.addTPort.fullList");
            return null;
        } else if (slot == -2) {
            if (sendMessage)
                sendErrorTranslation(owner, "tport.tport.tportManager.addTPort.maxExceeded", "TPort.add." + maxTPorts);
            return null;
        } else {
            tport.setSlot(slot);
            if (tport.getTportID() == null) {
                tport.setTportID(getNextUUID());
            }
            if (!tport.getOwner().equals(owner.getUniqueId())) {
                removeTPort(tport);
                tport.setOwner(owner.getUniqueId());
            }
            tport.setOfferedTo(null);
            saveTPort(tport);
            if (sendMessage) {
                sendSuccessTranslation(owner, "tport.tport.tportManager.addTPort.succeeded", tport);
            }
            return tport;
        }
    }
    
    /*
     * returns valid slot,
     * returns -1 when TPort list is full
     * returns -2 when TPort list is full because of max tport list size from permission
     * */
    private static int getNextSlot(UUID owner, int testSlot, int maxTPorts) {
        if (testSlot >= TPortSize) {
            return -1;
        }
        if (testSlot >= maxTPorts) {
            return -2;
        }
        for (String tportID : tportData.getKeys("tport." + owner + ".tports")) {
            TPort tmpTPort = (TPort) tportData.getConfig().get("tport." + owner + ".tports." + tportID);
            if (tmpTPort != null && tmpTPort.getSlot() == testSlot) {
                return getNextSlot(owner, testSlot + 1, maxTPorts);
            }
        }
        return testSlot;
    }
    
    public static void removeTPort(TPort tport) {
        tportData.getConfig().set("tport." + tport.getOwner().toString() + ".tports." + tport.getTportID(), null);
        tportData.saveConfig();
        
        TPRequest.tportRemoved(tport);
    }
    
    public static TPort getTPort(UUID tportID) {
        for (String playerUUID : tportData.getKeys("tport")) {
            TPort tport = getTPort(UUID.fromString(playerUUID), tportID);
            if (tport != null) {
                return tport;
            }
        }
        return null;
    }
    
    public static TPort getTPort(UUID owner, int slot) {
        for (String tportID : tportData.getKeys("tport." + owner + ".tports")) {
            TPort tport = (TPort) tportData.getConfig().get("tport." + owner + ".tports." + tportID);
            if (tport != null) {
                if (tport.getSlot() == slot) {
                    tport.setOwner(owner);
                    tport.setTportID(UUID.fromString(tportID));
                    return tport;
                }
            }
        }
        return null;
    }
    
    public static TPort getTPort(UUID owner, UUID tportID) {
        if (tportID.equals(defUUID)) {
            return new TPort(owner, null, new Location(Bukkit.getWorlds().get(0), 0, 0, 0), new ItemStack(Material.AIR));
        }
        TPort tport = (TPort) tportData.getConfig().get("tport." + owner + ".tports." + tportID);
        if (tport != null) {
            tport.setOwner(owner);
            tport.setTportID(tportID);
        }
        return tport;
    }
    
    public static TPort getTPort(UUID owner, String name) {
        if (name == null) {
            return new TPort(owner, null, new Location(Bukkit.getWorlds().get(0), 0, 0, 0), new ItemStack(Material.AIR));
        }
        for (String tportID : tportData.getKeys("tport." + owner.toString() + ".tports")) {
            TPort tport = (TPort) tportData.getConfig().get("tport." + owner + ".tports." + tportID);
            if (tport != null) {
                if (name.equalsIgnoreCase(tport.getName())) {
                    tport.setOwner(owner);
                    tport.setTportID(UUID.fromString(tportID));
                    return tport;
                }
            }
        }
        return null;
    }
    
    public static void saveTPort(TPort tport) {
        tportData.getConfig().set("tport." + tport.getOwner() + ".tports." + tport.getTportID(), tport);
        tportData.saveConfig();
    }
    
    public static UUID getNextUUID() {
        UUID nextUUID = UUID.randomUUID();
        if (nextUUID.equals(defUUID)) {
            return getNextUUID();
        }
        for (String uuid : tportData.getKeys("tport")) {
            for (String tportID : tportData.getKeys("tport." + uuid + ".tports")) {
                if (tportID.equals(nextUUID.toString())) {
                    return getNextUUID();
                }
            }
        }
        return nextUUID;
    }
}
