package com.spaceman.tport;

import com.spaceman.tport.commands.TPort;
import com.spaceman.tport.commands.tport.Reload;
import com.spaceman.tport.events.CompassEvents;
import com.spaceman.tport.events.DeathEvent;
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

public class Main extends JavaPlugin {

    public static Main getInstance() {
        return JavaPlugin.getPlugin(Main.class);
    }

    public static Location getLocation(String path) {

        Files tportData = GettingFiles.getFile("TPortData");

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
        Files tportData = GettingFiles.getFile("TPortData");

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
         * changelog 1.14 update:
         * add permissions
         * chanced the command structure (code update)
         * chanced the tabComplete (more efficient) (code update)
         * changed the cooldown names
         * fixed:
         *  not showing the last TPort in TPort gui
         *  feature finder does not break on update anymore (for now)
         *  fixed for 1.14
         * /tport PLTP tp <player>
         * /tport compass [player] [TPort name] -> /tport compass <type> [data]
         */
    
        /*
         * todo
         * /tport log
         * use ColorTheme
         * /tport colorTheme <theme>
         * */
    
        Reload.reloadTPort();
        
        TPort tPort = new TPort();
        getCommand("tport").setExecutor(tPort);
        getCommand("tport").setTabCompleter(tPort);

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new InventoryClick(), this);
        pm.registerEvents(new CompassEvents(), this);
        pm.registerEvents(new JoinEvent(this), this);
        pm.registerEvents(new DeathEvent(), this);

        if (!GettingFiles.getFile("TPortData").getConfig().contains("tport")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                JoinEvent.setData(this, player);
            }
        }
    }

}
