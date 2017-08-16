package com.spaceman.tport;

import com.spaceman.tport.commands.TPort;
import com.spaceman.tport.events.InventoryClick;
import com.spaceman.tport.events.JoinEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public static Location getLocation(Main p, String path) {

        if (!p.getConfig().contains(path)) {
            return null;
        }
        World world;
        try {
            world = Bukkit.getWorld(p.getConfig().getString(path + ".world"));
        } catch (Exception e) {
            return (Location) p.getConfig().get(path);
        }
        if (world == null) {
            return null;
        }

        int x = p.getConfig().getInt(path + ".x");
        int y = p.getConfig().getInt(path + ".y");
        int z = p.getConfig().getInt(path + ".z");

        float yaw = p.getConfig().getInt(path + ".yaw");
        float pitch = p.getConfig().getInt(path + ".pitch");

        return new Location(world, x, y, z, yaw, pitch);

    }

    public static void saveLocation(Main p, String path, Location location) {
        p.getConfig().set(path + ".world", location.getWorld().getName());
        p.getConfig().set(path + ".x", location.getX());
        p.getConfig().set(path + ".y", location.getY());
        p.getConfig().set(path + ".z", location.getZ());
        p.getConfig().set(path + ".pitch", location.getPitch());
        p.getConfig().set(path + ".yaw", location.getYaw());
        p.saveConfig();
    }

    public void onEnable() {

        getCommand("tport").setExecutor(new TPort(this));

        getCommand("tport").setTabCompleter(new TabComplete(this));

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new InventoryClick(this), this);
        pm.registerEvents(new JoinEvent(this), this);

        if (!getConfig().contains("tport")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                JoinEvent.setData(player, this);
            }

        }
    }
}
