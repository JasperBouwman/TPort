package com.spaceman.tport.webMaps;

import com.flowpowered.math.vector.Vector3d;
import com.google.gson.JsonObject;
import com.spaceman.tport.Main;
import com.spaceman.tport.commands.tport.Features;
import com.spaceman.tport.commands.tport.blueMap.Colors;
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
import org.apache.commons.io.FilenameUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Level;

import static com.spaceman.tport.fileHander.Files.tportData;

public class BlueMapHandler {
    
    private static final int iconSize = 32;
    
    //only is true when shouldEnable is true AND when BlueMap API could be found/used
    private static boolean enabled = false;
    
    public static boolean isEnabled() throws Exception {
        return enabled;
    }
    
    private static boolean shouldEnable() {
        return Features.Feature.BlueMap.isEnabled();
    }
    
    public static void enable() throws Exception {
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
        
        BlueMapAPI.onEnable(getConsumer());
    }
    
    private static Consumer<BlueMapAPI> getConsumer() {
        return api -> {
            enabled = true;
            markerSets.clear();
            try { loadTPorts(); } catch (Exception ignored) { }
            Main.getInstance().getLogger().log(Level.INFO, "Enabled BlueMap support");
        };
    }
    
    public static void disable() throws Exception {
        if (enabled) {
            BlueMapAPI.unregisterListener(getConsumer());
            
            BlueMapAPI.getInstance().ifPresent(api -> {
                for (BlueMapMap map : api.getMaps()){
                    map.getMarkerSets().remove("tport");
                    AssetStorage assetStorage = map.getAssetStorage();
                    for (String s : blueMapImages) {
                        try {
                            assetStorage.deleteAsset(s);
                        } catch (IOException e) {
                            Main.getInstance().getLogger().log(Level.WARNING, "Could not remove " + s + " from the storage of BlueMap");
                        }
                    }
                }
            });
            blueMapImages.clear();
            markerSets.clear();
            enabled = false;
            Main.getInstance().getLogger().log(Level.INFO, "Disabled BlueMap support");
        }
    }
    
    public static void loadTPorts() throws Exception {
        for (String uuid : tportData.getKeys("tport")) {
            for (TPort tport : TPortManager.getTPortList(UUID.fromString(uuid))) {
                if (tport.showOnBlueMap()) {
                    try { updateTPort(tport); } catch (Exception ignore) { }
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
                        
                        transferImages(map);
                        transferTPort_png(map);
                    }));
            markerSets.put(world.getName(), m);
        }
        return m;
    }
    
    private static final ArrayList<String> blueMapImages = new ArrayList<>();
    public static ArrayList<String> getBlueMapImages() throws Exception {
        return blueMapImages;
    }
    
    public static final String defaultIcon = "tport_tport.png";
    private static void transferTPort_png(BlueMapMap map) {
        blueMapImages.add("poi.svg");
        InputStream is = Main.getInstance().getResource("tport.png");
        if (is == null) {
            Main.getInstance().getLogger().log(Level.WARNING, "Could not find tport.png to write to BlueMap");
            return;
        }
        
        AssetStorage assetStorage = map.getAssetStorage();
        try (OutputStream out = assetStorage.writeAsset(defaultIcon)) {
            BufferedImage img = ImageIO.read(is);
            
            BufferedImage scale = new BufferedImage(iconSize, iconSize, img.getType());
            Graphics2D graphics = scale.createGraphics();
            graphics.drawImage(img, 0, 0, iconSize, iconSize, null);
            graphics.dispose();
            
            ImageIO.write(scale, "png", out);
            blueMapImages.add(defaultIcon);
        } catch (IOException e) {
            Main.getInstance().getLogger().log(Level.WARNING, "Could not write tport.png to BlueMap");
        }
    }
    private static void transferImages(BlueMapMap map) {
        File blueMapImagesDir = new File(Main.getInstance().getDataFolder(), "blueMapImages");
        if (!blueMapImagesDir.exists() && !blueMapImagesDir.mkdir()) {
            Main.getInstance().getLogger().log(Level.INFO, "Could not create the blueMapImages folder in " + Main.getInstance().getDataFolder().getPath());
            return;
        }
        
        File[] files = blueMapImagesDir.listFiles();
        if (files == null) {
            Main.getInstance().getLogger().log(Level.WARNING, "Could not write files to BlueMap");
            return;
        }
        
        AssetStorage assetStorage = map.getAssetStorage();
        for (File file : files) {
            if (file.getName().contains(" ")) {
                Main.getInstance().getLogger().log(Level.WARNING, "File name can not contain any spaces '" + file.getName() + "'");
                continue;
            }
            String formatName = FilenameUtils.getExtension(file.getName());
            if (!formatName.equalsIgnoreCase("png") && !formatName.equalsIgnoreCase("jpg")) {
                Main.getInstance().getLogger().log(Level.WARNING, "only png and jpg are supported '" + file.getName() + "'");
                continue;
            }
            
            try (OutputStream out = assetStorage.writeAsset("tport_" + file.getName())) {
                BufferedImage img = ImageIO.read(file);
                
                if (img.getWidth() != iconSize || img.getHeight() != iconSize) {
                    BufferedImage scale = new BufferedImage(iconSize, iconSize, img.getType());
                    Graphics2D graphics = scale.createGraphics();
                    graphics.drawImage(img, 0, 0, iconSize, iconSize, null);
                    graphics.dispose();
                    img = scale;
                }
                
                ImageIO.write(img, formatName, out);
                blueMapImages.add("tport_" + file.getName());
            } catch (Exception e) {
                Main.getInstance().getLogger().log(Level.WARNING, "Could not write " + file.getName() + " to BlueMap");
            }
        }
        
    }
    
    public static String getTPortIconName(TPort tport) throws Exception {
        try {
            if (getBlueMapImages().contains(tport.getBlueMapIcon())) {
                return tport.getBlueMapIcon();
            }
        } catch (Exception ignore) { }
        return defaultIcon;
    }
    
    public static void forceRemoveTPort(TPort tport) throws Exception {
        if (!enabled) return;
        BlueMapAPI.getInstance().ifPresent(api -> {
            MarkerSet markerSet = getMarkerSet(tport.getWorld());
            markerSet.remove(tport.getTportID().toString());
        });
    }
    
    public static void updateTPort(TPort tport) throws Exception {
        if (!enabled) return;
        BlueMapAPI.getInstance().ifPresent(api -> {
            
            MarkerSet markerSet = getMarkerSet(tport.getWorld());
            POIMarker marker = (POIMarker) markerSet.get(tport.getTportID().toString());
            
            String tportIcon = defaultIcon;
            try {
                tportIcon = getTPortIconName(tport);
            } catch (Exception ignore) { }
            int anchorX;
            int anchorY;
            String iconAddress;
            if (tportIcon.equals("poi.svg")) {
                iconAddress = "assets/poi.svg";
                anchorX = 24;
                anchorY = 45;
            } else {
                iconAddress = "maps/" + tport.getWorld().getName() + "/assets/" + tportIcon;
                anchorX = iconSize/2;
                anchorY = iconSize/2;
            }
            
            Vector3d vec = new Vector3d(tport.getLocation().getX(), tport.getLocation().getY(), tport.getLocation().getZ());
            if (marker == null && tport.showOnBlueMap()) {
                marker = POIMarker.builder()
                        .label(tport.getName())
                        .position(vec)
                        .icon(iconAddress, anchorX, anchorY)
                        .build();
                setMarkerDescription(marker, tport);
                markerSet.put(tport.getTportID().toString(), marker);
                
            } else if (marker != null && !tport.showOnBlueMap()) {
                markerSet.remove(tport.getTportID().toString());
                
            } else if (marker != null && tport.showOnBlueMap()) {
                marker.setPosition(vec);
                setMarkerDescription(marker, tport);
                marker.setIcon(iconAddress, anchorX, anchorY);
            }
            
        });
    }
    
    private static void setMarkerDescription(POIMarker marker, TPort tport) { //todo add TPort name to marker
        JsonObject serverLang = Language.getServerLang();
        ColorTheme colorTheme = Colors.getBlueMapTheme();
        
        StringBuilder newBuilder = new StringBuilder();
        for (Message m : tport.getHoverData(true)) {
            Message translated = m.translateMessage(serverLang);
            String translate = translated.translateHTML(colorTheme);
            newBuilder.append(translate).append("<br />");
        }
        marker.setDetail(newBuilder.toString());
    }
    
}
