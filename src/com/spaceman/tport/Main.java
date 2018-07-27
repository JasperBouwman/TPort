package com.spaceman.tport;

import com.spaceman.tport.commands.TPort;
import com.spaceman.tport.events.CompassEvents;
import com.spaceman.tport.events.InventoryClick;
import com.spaceman.tport.events.JoinEvent;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import static com.spaceman.tport.fileHander.GettingFiles.getFiles;

public class Main extends JavaPlugin {

    public static Location getLocation(String path) {

        Files tportData = getFiles("TPortData");

        if (!tportData.getConfig().contains(path)) {
            return null;
        }
        World world;
        try {
            world = Bukkit.getWorld(tportData.getConfig().getString(path + ".world"));
        } catch (Exception e) {
            return (Location) tportData.getConfig().get(path);
        }
        if (world == null) {
            return null;
        }

        double x = tportData.getConfig().getDouble(path + ".x");
        double y = tportData.getConfig().getDouble(path + ".y");
        double z = tportData.getConfig().getDouble(path + ".z");

        float yaw = tportData.getConfig().getInt(path + ".yaw");
        float pitch = tportData.getConfig().getInt(path + ".pitch");

        return new Location(world, x, y, z, yaw, pitch);

    }

    public static void saveLocation(String path, Location location) {
        Files tportData = getFiles("TPortData");

        tportData.getConfig().set(path + ".world", location.getWorld().getName());
        tportData.getConfig().set(path + ".x", location.getX());
        tportData.getConfig().set(path + ".y", location.getY());
        tportData.getConfig().set(path + ".z", location.getZ());
        tportData.getConfig().set(path + ".pitch", location.getPitch());
        tportData.getConfig().set(path + ".yaw", location.getYaw());
        tportData.saveConfig();
    }

    public void onEnable() {
        /*
         * changelog:
         *
         * better text
         * /tport removePlayer <player name>
         * /tport compass [player name] [TPort name]
         *      works if compass is in ItemFrame
         * /tport own [TPort name]
         * /tport whitelist <TPort name> <add:remove> <player name>     -->   /tport edit <TPort name> whitelist <add:remove> <players names...>
         * /tport whitelist <TPort name> list                           -->   /tport edit <TPort name> whitelist list
         * horse friendly
         * zombie horse friendly
         * skeleton horse friendly
         * pig friendly
         * llama friendly
         * username change friendly
         * whitelists are sometimes cleared on plugin update
         * removed /tport extra item
         */

        getCommand("tport").setExecutor(new TPort());
        getCommand("tport").setTabCompleter(new TabComplete());

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new InventoryClick(), this);
        pm.registerEvents(new CompassEvents(), this);
        pm.registerEvents(new JoinEvent(this), this);

        new GettingFiles(this);

        boolean tmp = !getFiles("TPortData").getConfig().contains("tport");
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (tmp) {
                JoinEvent.setData(this, player);
            }
        }
    }
}
