package com.spaceman.tport.tport;

import com.spaceman.tport.Main;
import com.spaceman.tport.colorFormatter.ColorTheme;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fileHander.Files;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.spaceman.tport.colorFormatter.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.events.InventoryClick.TPortSize;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;
import static com.spaceman.tport.permissions.PermissionHandler.sendNoPermMessage;

public class TPortManager {
    
    public final static UUID defUUID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    
    public static ArrayList<TPort> getTPortList(UUID owner) {
        Files tportData = getFile("TPortData");
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
            sendErrorTheme(owner, "TPort name can't be a number, but it can contain a number");
            return null;
        } catch (NumberFormatException ignore) {
        }
        
        for (TPort tmpTPort : getTPortList(owner.getUniqueId())) {
            if (tmpTPort.getName().equalsIgnoreCase(tport.getName())) {
                if (sendMessage) sendErrorTheme(owner, "TPort %s name is already used", tmpTPort.getName());
                return null;
            }
        }

        int slot = getNextSlot(owner.getUniqueId(), 0, maxTPorts);
        
        if (slot == -1) {
            if (sendMessage) sendErrorTheme(owner, "Your TPort list is full, remove an old one if possible");
            return null;
        } else if (slot == -2) {
            if (sendMessage)
                sendErrorTheme(owner, "You have exceeded your maximal TPort list size, permission: %s", "TPort.add." + maxTPorts);
            return null;
        } else {
            tport.setSlot(slot);
            if (tport.getTportID() == null) {
                tport.setTportID(getNextUUID());
            }
            tport.setOwner(owner.getUniqueId());
            tport.setOfferedTo(null);
            saveTPort(tport);
            if (sendMessage) {
                Message message = new Message();
                message.addText(textComponent("Successfully added the TPort ", ColorTheme.ColorType.infoColor));
                message.addText(textComponent(tport.getName(), ColorTheme.ColorType.varSuccessColor,
                        ClickEvent.runCommand("/tport open " + owner.getName() + " " + tport.getName())));
                message.sendMessage(owner);
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
        Files tportData = getFile("TPortData");
        for (String tportID : tportData.getKeys("tport." + owner + ".tports")) {
            TPort tmpTPort = (TPort) tportData.getConfig().get("tport." + owner + ".tports." + tportID);
            if (tmpTPort != null && tmpTPort.getSlot() == testSlot) {
                return getNextSlot(owner, testSlot + 1, maxTPorts);
            }
        }
        return testSlot;
    }
    
    /*
     * returns TPort if removed
     * returns null if TPort was not found
     * */
    public static TPort removeTPort(UUID owner, TPort tport) {
        if (tport != null) {
            Files tportData = getFile("TPortData");
            tportData.getConfig().set("tport." + owner + ".tports." + tport.getTportID(), null);
            tportData.saveConfig();
            return tport;
        }
        return null;
    }
    
    public static TPort removeTPort(UUID owner, String name) {
        return removeTPort(owner, getTPort(owner, name));
    }
    
    public static TPort getTPort(UUID tportID) {
        Files tportData = getFile("TPortData");
        for (String playerUUID : tportData.getKeys("tport")) {
            TPort tport = getTPort(UUID.fromString(playerUUID), tportID);
            if (tport != null) {
                return tport;
            }
        }
        return null;
    }
    
    public static TPort getTPort(UUID owner, int slot) {
        Files tportData = getFile("TPortData");
        
        for (String tportID : tportData.getKeys("tport." + owner.toString() + ".tports")) {
            TPort tport = (TPort) tportData.getConfig().get("tport." + owner.toString() + ".tports." + tportID);
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
        Files tportData = getFile("TPortData");
        TPort tport = (TPort) tportData.getConfig().get("tport." + owner.toString() + ".tports." + tportID);
        if (tport != null) {
            tport.setOwner(owner);
            tport.setTportID(tportID);
        }
        return tport;
    }
    
    public static TPort getTPort(UUID owner, String name) {
        Files tportData = getFile("TPortData");
        for (String tportID : tportData.getKeys("tport." + owner.toString() + ".tports")) {
            TPort tport = (TPort) tportData.getConfig().get("tport." + owner.toString() + ".tports." + tportID);
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
        Files tportData = getFile("TPortData");
        tportData.getConfig().set("tport." + tport.getOwner() + ".tports." + tport.getTportID(), tport);
        tportData.saveConfig();
    }
    
    public static void convertOldToNew(Files saveFile) {
        for (String uuid : saveFile.getKeys("tport")) {
            for (String slot : saveFile.getKeys("tport." + uuid + ".items")) {
                ItemStack is = saveFile.getConfig().getItemStack("tport." + uuid + ".items." + slot + ".item");
                ItemMeta im = is.getItemMeta();
                im.setDisplayName(null);
                String lore = null;
                if (im.hasLore()) {
                    lore = String.join(" ", im.getLore());
                    im.setLore(new ArrayList<>());
                }
                is.setItemMeta(im);
                String name = saveFile.getConfig().getString("tport." + uuid + ".items." + slot + ".name", RandomStringUtils.randomAlphabetic(5));
                
                try {
                    Long.parseLong(name);
                    name += "S";
                } catch (NumberFormatException ignore) {
                }
                
                Location location = Main.getLocation("tport." + uuid + ".items." + slot + ".location");
                String privateStatement = saveFile.getConfig().getString("tport." + uuid + ".items." + slot + ".private.statement", "OFF");
                List<String> whiteList = saveFile.getConfig().getStringList("tport." + uuid + ".items." + slot + ".private.players");
                whiteList.remove(uuid);
                
                TPort newTPort = new TPort(UUID.fromString(uuid), name, location, is);
                newTPort.setDescription(lore);
                if (newTPort.getLocation() == null) {
                    location = new Location(Bukkit.getWorlds().get(0), 1, 1, 1);
                    location.setX(saveFile.getConfig().getDouble("tport." + uuid + ".items." + slot + ".location.x"));
                    location.setY(saveFile.getConfig().getDouble("tport." + uuid + ".items." + slot + ".location.y"));
                    location.setZ(saveFile.getConfig().getDouble("tport." + uuid + ".items." + slot + ".location.z"));
                    location.setYaw(saveFile.getConfig().getInt("tport." + uuid + ".items." + slot + ".location.yaw"));
                    location.setPitch(saveFile.getConfig().getInt("tport." + uuid + ".items." + slot + ".location.pitch"));
                    World world = Bukkit.getWorld(saveFile.getConfig().getString("tport." + uuid + ".items." + slot + ".location.world", ""));
                    if (world == null) {
                        newTPort.setInactiveWorldName(saveFile.getConfig().getString("tport." + uuid + ".items." + slot + ".location.world", ""));
                    }
                    newTPort.setLocation(location);
                }
                
                //noinspection ConstantConditions
                newTPort.setPrivateStatement(TPort.PrivateStatement.valueOf(privateStatement.toUpperCase()));
                for (String tmpUUID : whiteList) {
                    newTPort.addWhitelist(UUID.fromString(tmpUUID));
                }
                newTPort.setSlot(Integer.parseInt(slot));
                saveFile.getConfig().set("tport." + uuid + ".tports." + getNextUUID(), newTPort);
            }
            saveFile.getConfig().set("tport." + uuid + ".items", null);
        }
        
        saveFile.saveConfig();
    }
    
    public static UUID getNextUUID() {
        UUID nextUUID = UUID.randomUUID();
        if (nextUUID.equals(defUUID)) {
            return getNextUUID();
        }
        Files tportData = getFile("TPortData");
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
