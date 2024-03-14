package com.spaceman.tport.webMaps;

import com.flowpowered.math.vector.Vector3d;
import com.google.gson.JsonObject;
import com.spaceman.tport.Main;
import com.spaceman.tport.commands.tport.Features;
import com.spaceman.tport.commands.tport.dynmap.Colors;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fancyMessage.language.Language;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import de.bluecolored.bluemap.api.AssetStorage;
import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.markers.POIMarker;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

import static com.spaceman.tport.fileHander.Files.tportData;

public class BlueMapHandler {
    
    private static final int iconSize = 32;
    
    //only is true when shouldEnable is true AND when BlueMap API could be found/used
    private static boolean enabled = false;
    
    public static boolean isEnabled() {
        return enabled;
    }
    
    private static boolean shouldEnable() {
        return Features.Feature.BlueMap.isEnabled();
    }
    
    public static void enable() {
        if (!shouldEnable()) {
            return;
        }
        
        Plugin blueMapPlugin = Bukkit.getServer().getPluginManager().getPlugin("BlueMap");
        
        if (blueMapPlugin == null) {
            enabled = false;
            Main.getInstance().getLogger().log(Level.SEVERE, "Tried to enable BlueMap support, BlueMap API was not found");
            return;
        }
        if (enabled) {
            Main.getInstance().getLogger().log(Level.WARNING, "Tried to enable BlueMap support, it was already enabled");
            enabled = false;
            return;
        }
        
        BlueMapAPI.onEnable(api -> {
            enabled = true;
            markerSets.clear();
            loadTPorts();
            Main.getInstance().getLogger().log(Level.INFO, "Enabled BlueMap support");
        });
        
    }
    
    public static void disable() {
        if (enabled) {
            BlueMapAPI.getInstance().ifPresent(api -> {
                for (BlueMapMap map : api.getMaps()){
                    map.getMarkerSets().remove("tport");
                }
            });
            enabled = false;
            Main.getInstance().getLogger().log(Level.INFO, "Disabled BlueMap support");
        }
    }
    
    private static void loadTPorts() {
        for (String uuid : tportData.getKeys("tport")) {
            for (TPort tport : TPortManager.getTPortList(UUID.fromString(uuid))) {
                if (tport.showOnBlueMap()) {
                    updateTPort(tport);
                }
            }
        }
    }
    
    private static final HashMap<String /*world name*/, MarkerSet> markerSets = new HashMap<>();
    private static MarkerSet getMarkerSet(World world) {
        MarkerSet m = markerSets.get(world.getName());
        if (m == null) {
            m = MarkerSet.builder()
                    .label("TPort")
                    .toggleable(true)
                    .defaultHidden(false).build();
            MarkerSet finalM = m;
            BlueMapAPI.getInstance().flatMap(api -> api.getWorld(world))
                    .ifPresent(blueMapWorld -> blueMapWorld.getMaps().forEach(map -> {
                        map.getMarkerSets().putIfAbsent("tport", finalM);
                        
                        AssetStorage assetStorage = map.getAssetStorage();
                        try (OutputStream out = assetStorage.writeAsset("tport.png")) {
                            InputStream is = Main.getInstance().getResource("tport.png");
                            BufferedImage img = ImageIO.read(is);
                            
                            BufferedImage scale = new BufferedImage(iconSize, iconSize, img.getType());
                            Graphics2D graphics = scale.createGraphics();
                            graphics.drawImage(img, 0, 0, iconSize, iconSize, null);
                            graphics.dispose();
                            
                            ImageIO.write(scale, "png", out);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }));
            markerSets.put(world.getName(), m);
        }
        return m;
    }
    
    public static void updateTPort(TPort tport) {
        if (!enabled) return;
        BlueMapAPI.getInstance().ifPresent(api -> {
            
            MarkerSet markerSet = getMarkerSet(tport.getWorld());
            POIMarker marker = (POIMarker) markerSet.get(tport.getTportID().toString());
            
            Vector3d vec = new Vector3d(tport.getLocation().getX(), tport.getLocation().getY(), tport.getLocation().getZ());
            if (marker == null && tport.showOnBlueMap()) {
                marker = POIMarker.builder()
                        .label(tport.getName())
                        .position(vec)
                        .icon("maps/" + tport.getWorld().getName() + "/assets/tport.png", iconSize/2, iconSize/2)
                        .build();
                setMarkerDescription(marker, tport);
                markerSet.put(tport.getTportID().toString(), marker);
                
            } else if (marker != null && !tport.showOnBlueMap()) {
                markerSet.remove(tport.getTportID().toString());
                
            } else if (marker != null && tport.showOnBlueMap()) {
                marker.setPosition(vec);
                setMarkerDescription(marker, tport);
                marker.setIcon("maps/" + tport.getWorld().getName() + "/assets/tport.png", iconSize/2, iconSize/2);
            }
            
        });
    }
    
    private static void setMarkerDescription(POIMarker marker, TPort tport) { //todo add TPort name to marker
        JsonObject serverLang = Language.getServerLang();
        ColorTheme colorTheme = Colors.getDynmapTheme();
        
        StringBuilder newBuilder = new StringBuilder();
        for (Message m : tport.getHoverData(true)) {
            Message translated = m.translateMessage(serverLang);
            String translate = translated.translateHTML(colorTheme);
            newBuilder.append(translate).append("<br />");
        }
        marker.setDetail(newBuilder.toString());
    }
    
}
