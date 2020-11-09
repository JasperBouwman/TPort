package com.spaceman.tport.commands.tport.searchArea;

import com.spaceman.tport.Main;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.searchAreaHander.SearchAreaHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapView;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nonnull;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.UUID;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;

public class Show extends SubCommand {
    
    @Override
    public void run(String[] args, Player player) {
        // tport searchArea show
        
        if (args.length == 2) {
            ItemStack is = new ItemStack(Material.FILLED_MAP);
            
            MapView mapView = Bukkit.createMap(player.getWorld());
            mapView.getRenderers().forEach(mapView::removeRenderer);
            
            MapMeta im = (MapMeta) is.getItemMeta();
            im.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "PolygonMapRenderer"), PersistentDataType.STRING, "true");
            im.setMapView(mapView);
            is.setItemMeta(im);
    
            updatePolygonMap(is, player);
            
            player.getInventory().addItem(is);
            
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport searchArea show");
        }
    }
    
    public static void updatePolygonMap(ItemStack itemStack, Player player) {
        MapMeta im = (MapMeta) itemStack.getItemMeta();
        if (im.hasMapView()) {
            MapView mapView = im.getMapView();
            mapView.getRenderers().forEach(mapView::removeRenderer);
            Show.PolygonMapRenderer mapRenderer = new Show.PolygonMapRenderer(player);
            mapView.addRenderer(mapRenderer);
    
            Polygon polygon = mapRenderer.update();
            Rectangle rect = polygon.getBounds();
            
            ColorTheme theme = ColorTheme.getTheme(player);
            im.setLore(Arrays.asList(
                    "§r" + theme.getInfoColor() + "SearchArea Owner: " + theme.getVarInfoColor() + player.getName(),
                    "§r" + theme.getInfoColor() + "Bounds: ",
                    "§r" + theme.getInfoColor() + "MinX: " + theme.getVarInfoColor() + rect.getMinX() + theme.getInfoColor() + ", MinY: " + theme.getVarInfoColor() + rect.getMinY(),
                    "§r" + theme.getInfoColor() + "MaxX: " + theme.getVarInfoColor() + rect.getMaxX() + theme.getInfoColor() + ", MaxY: " + theme.getVarInfoColor() + rect.getMaxY(),
                    "",
                    "§r" + theme.getInfoColor() + "Your location: " +
                            theme.getInfoColor() + "X: " + theme.getVarInfoColor() + player.getLocation().getBlockX() +
                            theme.getInfoColor() + ", Z: " + theme.getVarInfoColor() + player.getLocation().getBlockZ() +
                            theme.getInfoColor() + " is " + theme.getVarInfoColor() + (polygon.contains(player.getLocation().getX(), player.getLocation().getZ()) ? "inside" : "outside")));
            itemStack.setItemMeta(im);
        }
    }
    
    public static class PolygonMapRenderer extends org.bukkit.map.MapRenderer {
        
        private final UUID uuid;
        private boolean update = true;
        private BufferedImage image = null;
        
        public PolygonMapRenderer(Player player) {
            this.uuid = player.getUniqueId();
        }
        
        public Polygon update() {
            BufferedImage image = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
            Polygon polygon = null;
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                SearchAreaHandler searchArea = SearchAreaHandler.getSearchAreaHandler();
                polygon = searchArea.getSearchArea(player);
    
                double xScale = 1 / (polygon.getBounds().width / 120D);
                double yScale = 1 / (polygon.getBounds().height / 120D);
    
                AffineTransform affineTransform = new AffineTransform();
                affineTransform.scale(xScale, yScale);
                affineTransform.translate(-polygon.getBounds().x, -polygon.getBounds().y);
                Graphics2D gra = image.createGraphics();
                Shape shape = affineTransform.createTransformedShape(polygon);
                gra.draw(shape);
    
                gra.dispose();
                
            }
            this.update = true;
            this.image = image;
            return polygon;
        }
        
        @Override
        public void render(@Nonnull MapView mapView, @Nonnull MapCanvas mapCanvas, @Nonnull Player player) {
            if (update) {
                org.bukkit.map.MapRenderer renderer = mapCanvas.getMapView().getRenderers().get(0);
                if (renderer instanceof PolygonMapRenderer) {
                    PolygonMapRenderer mapRenderer = (PolygonMapRenderer) renderer;
                    if (image == null) {
                        update();
                    }
                    mapCanvas.drawImage(0, 0, new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB));
                    mapCanvas.drawImage(4, 4, mapRenderer.image);
                    mapRenderer.update = false;
                }
            }
        }
    }
    
}
