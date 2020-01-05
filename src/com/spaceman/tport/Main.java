package com.spaceman.tport;

import com.spaceman.tport.colorFormatter.ColorTheme;
import com.spaceman.tport.commands.TPortCommand;
import com.spaceman.tport.commands.tport.Reload;
import com.spaceman.tport.commands.tport.backup.Auto;
import com.spaceman.tport.events.CompassEvents;
import com.spaceman.tport.events.DeathEvent;
import com.spaceman.tport.events.InventoryClick;
import com.spaceman.tport.events.JoinEvent;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import com.spaceman.tport.tpEvents.ParticleAnimation;
import com.spaceman.tport.tpEvents.TPEManager;
import com.spaceman.tport.tpEvents.TPRestriction;
import com.spaceman.tport.tpEvents.animations.ExplosionAnimation;
import com.spaceman.tport.tpEvents.animations.SimpleAnimation;
import com.spaceman.tport.tpEvents.restrictions.NoneRestriction;
import com.spaceman.tport.tpEvents.restrictions.WalkRestriction;
import com.spaceman.tport.tport.TPort;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.util.*;

import static com.spaceman.tport.colorFormatter.ColorTheme.sendErrorTheme;

public class Main extends JavaPlugin {
    
    public static <O> O getOrDefault(@Nullable O object, O def) {
        return object == null ? def : object;
    }
    
    public static Main getInstance() {
        return JavaPlugin.getPlugin(Main.class);
    }
    
    public static ArrayList<ItemStack> giveItems(Player player, List<ItemStack> items) {
        ArrayList<ItemStack> returnList = new ArrayList<>();
        for (ItemStack item : player.getInventory().addItem(items.toArray(new ItemStack[0])).values()) {
            player.getWorld().dropItem(player.getLocation(), item);
            returnList.add(item);
            sendErrorTheme(player, "Your inventory is full, dropped item %s on the ground", item.getType().name());
        }
        return returnList;
    }
    
    public static ArrayList<ItemStack> giveItems(Player player, ItemStack... items) {
        return giveItems(player, Arrays.asList(items));
    }
    
    public static Location getLocation(String path) {
        return getLocation(path, GettingFiles.getFile("TPortData"));
    }
    
    public static Location getLocation(String path, Files file) {
        if (!file.getConfig().contains(path)) {
            return null;
        }
        World world;
        try {
            world = Bukkit.getWorld(file.getConfig().getString(path + ".world"));
        } catch (Exception e) {
            return null;
        }
        if (world == null) {
            return null;
        }
        
        double x = file.getConfig().getDouble(path + ".x");
        double y = file.getConfig().getDouble(path + ".y");
        double z = file.getConfig().getDouble(path + ".z");
        
        float yaw = file.getConfig().getInt(path + ".yaw");
        float pitch = file.getConfig().getInt(path + ".pitch");
        
        return new Location(world, x, y, z, yaw, pitch);
    }
    
    public static void saveLocation(String path, Location location, Files tportData) {
        tportData.getConfig().set(path + ".world", Objects.requireNonNull(location.getWorld()).getName());
        tportData.getConfig().set(path + ".x", location.getX());
        tportData.getConfig().set(path + ".y", location.getY());
        tportData.getConfig().set(path + ".z", location.getZ());
        tportData.getConfig().set(path + ".pitch", location.getPitch());
        tportData.getConfig().set(path + ".yaw", location.getYaw());
        tportData.saveConfig();
    }
    
    public static ArrayList<String> getPlayerNames() {
        ArrayList<String> list = new ArrayList<>();
        Files tportData = GettingFiles.getFile("TPortData");
        for (OfflinePlayer op : Bukkit.getOnlinePlayers()) {
            if (tportData.getConfig().contains("tport." + op.getUniqueId())) {
                list.add(op.getName());
            }
        }
        return list;
    }
    
    public static ArrayList<UUID> getPlayerUUIDs() {
        ArrayList<UUID> list = new ArrayList<>();
        Files tportData = GettingFiles.getFile("TPortData");
        for (OfflinePlayer op : Bukkit.getOnlinePlayers()) {
            if (tportData.getConfig().contains("tport." + op.getUniqueId())) {
                list.add(op.getUniqueId());
            }
        }
        return list;
    }
    
    public void onEnable() {
        
        /*
         * changelog 1.15.2 update:
         *
         * fixed TPort back: when teleporting back to a TPort it did not store the correct location
         */
        
        /*
         * todo
         * remove converting methods
         * improve TPort back code
         * */
        
        ConfigurationSerialization.registerClass(ColorTheme.class, "ColorTheme");
        ConfigurationSerialization.registerClass(TPort.class, "TPort");
        ConfigurationSerialization.registerClass(Pair.class, "Pair");
        
        ParticleAnimation.registerAnimation(SimpleAnimation::new);
        ParticleAnimation.registerAnimation(ExplosionAnimation::new);
        TPRestriction.registerRestriction(NoneRestriction::new);
        TPRestriction.registerRestriction(WalkRestriction::new);
        
        Reload.reloadTPort();
        
        TPEManager.loadTPE(GettingFiles.getFile("TPortConfig"));
        
        ColorTheme.loadThemes(GettingFiles.getFile("TPortConfig"));
        
        
        Glow.registerGlow();
        
        new TPortCommand();
        
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new InventoryClick(), this);
        pm.registerEvents(new CompassEvents(), this);
        pm.registerEvents(new JoinEvent(this), this);
        pm.registerEvents(new DeathEvent(), this);
        pm.registerEvents(new OfflineLocationManager(), this);
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            JoinEvent.setData(this, player);
        }
    }
    
    @Override
    public void onDisable() {
        TPEManager.saveTPE(GettingFiles.getFile("TPortConfig"));
        ColorTheme.saveThemes(GettingFiles.getFile("TPortConfig"));
        Auto.save();
    }
}
