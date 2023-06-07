package com.spaceman.tport.dynmap;

import com.google.gson.JsonObject;
import com.spaceman.tport.Main;
import com.spaceman.tport.Pair;
import com.spaceman.tport.commands.tport.Features;
import com.spaceman.tport.commands.tport.dynmap.Colors;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fancyMessage.language.Language;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerIcon;
import org.dynmap.markers.MarkerSet;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static com.spaceman.tport.fileHander.Files.tportData;

public class DynmapHandler {
    
    public static final String tport_dynmap_icon = "tport_icon";
    
    //only is true when shouldEnable is true AND when Dynmap API could be found/used
    private static boolean enabled = false;
    
    public static boolean isEnabled() {
        return enabled;
    }
    
    public static boolean shouldEnable() {
        return Features.Feature.Dynmap.isEnabled();
    }
    
    public static void enable() {
        if (!shouldEnable()) {
            return;
        }
        
        Plugin dynmapPlugin = Bukkit.getServer().getPluginManager().getPlugin("dynmap");
        
        if (dynmapPlugin != null) {
            if (!enabled) {
                try {
                    DynmapAPI dynmap = (DynmapAPI) dynmapPlugin;
                    MarkerAPI markerAPI = dynmap.getMarkerAPI();
                    
                    MarkerIcon tportMarkerIcon = markerAPI.createMarkerIcon(tport_dynmap_icon, "TPort", DynmapHandler.class.getResourceAsStream("tport.png"));
                    if (tportMarkerIcon == null) tportMarkerIcon = markerAPI.getMarkerIcon(tport_dynmap_icon);
                    
                    MarkerSet set = markerAPI.createMarkerSet("tports", "TPorts", null, false);
                    set.setDefaultMarkerIcon(tportMarkerIcon);
                    loadTPorts(markerAPI, set);
                    Main.getInstance().getLogger().log(Level.INFO, "Enabled Dynmap support");
                    enabled = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    Main.getInstance().getLogger().log(Level.WARNING, "Tried to enable Dynmap support, error: ", e.getMessage());
                }
            } else {
                Main.getInstance().getLogger().log(Level.WARNING, "Tried to enable Dynmap support, it was already enabled");
            }
        } else {
            enabled = false;
            Main.getInstance().getLogger().log(Level.SEVERE, "Tried to enable Dynmap support, Dynmap was not found");
        }
    }
    
    public static void disable() {
        if (enabled) {
            enabled = false;
            DynmapAPI dynmap = (DynmapAPI) Bukkit.getServer().getPluginManager().getPlugin("dynmap");
            if (dynmap != null) {
                MarkerAPI markerAPI = dynmap.getMarkerAPI();
                MarkerSet markerSet = markerAPI.getMarkerSet("tports");
                for (Marker m : markerSet.getMarkers()) {
                    m.deleteMarker();
                }
                markerSet.deleteMarkerSet();
                Main.getInstance().getLogger().log(Level.INFO, "Disabled Dynmap support");
            }
        }
    }
    
    private static void loadTPorts(MarkerAPI markerAPI, MarkerSet markerSet) {
        for (String uuid : tportData.getKeys("tport")) {
            for (TPort tport : TPortManager.getTPortList(UUID.fromString(uuid))) {
                if (tport.showOnDynmap()) {
                    updateTPort(tport, markerAPI, markerSet);
                }
            }
        }
    }
    
    public static void updateAllTPorts() {
        DynmapAPI dynmap = (DynmapAPI) Bukkit.getServer().getPluginManager().getPlugin("dynmap");
        MarkerAPI markerAPI = dynmap.getMarkerAPI();
        MarkerSet markerSet = markerAPI.getMarkerSet("tports");
        
        for (String uuid : tportData.getKeys("tport")) {
            for (TPort tport : TPortManager.getTPortList(UUID.fromString(uuid))) {
                if (tport.showOnDynmap()) {
                    updateTPort(tport, markerAPI, markerSet);
                }
            }
        }
    }
    
    public static void updateTPort(TPort tport) {
        if (enabled) {
            DynmapAPI dynmap = (DynmapAPI) Bukkit.getServer().getPluginManager().getPlugin("dynmap");
            MarkerAPI markerAPI = dynmap.getMarkerAPI();
            MarkerSet markerSet = markerAPI.getMarkerSet("tports");
            updateTPort(tport, markerAPI, markerSet);
        }
    }
    
    private static void updateTPort(TPort tport, MarkerAPI markerAPI, MarkerSet markerSet) {
        MarkerIcon markerIcon = markerAPI.getMarkerIcons().stream().filter(m -> m.getMarkerIconID().equals(tport.getDynmapIconID())).findFirst().orElse(markerSet.getDefaultMarkerIcon());
        
        Marker m = null;
        
        for (Marker marker : markerSet.getMarkers()) {
            if (marker.getMarkerID().equals("TPort-" + tport.getTportID())) {
                m = marker;
                break;
            }
        }
        
        if (m == null && tport.showOnDynmap()) {
            m = markerSet.createMarker("TPort-" + tport.getTportID(), "TPort: " + tport.getName(), false,
                    tport.getLocation().getWorld().getName(), tport.getLocation().getX(), tport.getLocation().getY(), tport.getLocation().getZ(),
                    markerIcon, false);
            //new line support thanks to u/DirtyEarplug https://www.reddit.com/r/Dynmap/comments/hni0xn/comment/iqyczk4/?utm_source=share&utm_medium=web2x&context=3
            setMarkerDescription(m, tport);
        } else if (m != null && !tport.showOnDynmap()) {
            m.deleteMarker();
        } else if (m != null && tport.showOnDynmap()) {
            m.setLabel("TPort: " + tport.getName());
            m.setLocation(tport.getLocation().getWorld().getName(), tport.getLocation().getX(), tport.getLocation().getY(), tport.getLocation().getZ());
            m.setMarkerIcon(markerIcon);
            setMarkerDescription(m, tport);
        }
    }
    
    private static void setMarkerDescription(Marker marker, TPort tport) {
        //new line support thanks to u/DirtyEarplug https://www.reddit.com/r/Dynmap/comments/hni0xn/comment/iqyczk4/?utm_source=share&utm_medium=web2x&context=3
        JsonObject serverLang = Language.getServerLang();
        ColorTheme colorTheme = Colors.getDynmapTheme();
        
        StringBuilder newBuilder = new StringBuilder();
        for (Message m : tport.getHoverData(true)) {
            Message translated = m.translateMessage(serverLang);
            String translate = translated.translateHTML(colorTheme);
            newBuilder.append(translate).append("<br />");
        }
        marker.setDescription(newBuilder.toString());
    }
    
    public static String getTPortIconName(TPort tport) {
        if (enabled) {
            DynmapAPI dynmap = (DynmapAPI) Bukkit.getServer().getPluginManager().getPlugin("dynmap");
            MarkerAPI markerAPI = dynmap.getMarkerAPI();
            MarkerSet markerSet = markerAPI.getMarkerSet("tports");
            return markerAPI.getMarkerIcons().stream().filter(m -> m.getMarkerIconID().equals(tport.getDynmapIconID())).findFirst().orElse(markerSet.getDefaultMarkerIcon()).getMarkerIconLabel();
        } else {
            return null;
        }
    }
    
    public static String iconLabelToID(String label) {
        if (enabled) {
            DynmapAPI dynmap = (DynmapAPI) Bukkit.getServer().getPluginManager().getPlugin("dynmap");
            MarkerAPI markerAPI = dynmap.getMarkerAPI();
            
            for (MarkerIcon icon : markerAPI.getMarkerIcons()) {
                if (icon.getMarkerIconLabel().equalsIgnoreCase(label)) {
                    return icon.getMarkerIconID();
                }
            }
        }
        return null;
    }
    
    @Nullable
    public static List<Pair<String, String>> getIcons() {
        if (enabled) {
            DynmapAPI dynmap = (DynmapAPI) Bukkit.getServer().getPluginManager().getPlugin("dynmap");
            MarkerAPI markerAPI = dynmap.getMarkerAPI();
            return markerAPI.getMarkerIcons().stream().map(icon -> new Pair<>(icon.getMarkerIconID(), icon.getMarkerIconLabel())).collect(Collectors.toList());
        }
        return null;
    }
}
