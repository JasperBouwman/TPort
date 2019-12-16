package com.spaceman.tport;

import com.spaceman.tport.fileHander.Files;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

import static com.spaceman.tport.fileHander.GettingFiles.getFile;

public class OfflineLocationManager implements Listener {
    
    @EventHandler
    @SuppressWarnings("unused")
    public void onJoin(PlayerJoinEvent e) {
        Files file = getFile("TPortConfig");
        file.getConfig().set("offlineLoc." + e.getPlayer().getUniqueId().toString(), null);
        file.saveConfig();
    }
    
    @EventHandler
    @SuppressWarnings("unused")
    public void onLeave(PlayerQuitEvent e) {
        Main.saveLocation("offlineLoc." + e.getPlayer().getUniqueId().toString(), e.getPlayer().getLocation(), getFile("TPortConfig"));
    }
    
    public static Location getOfflineLocation(UUID uuid) {
        return Main.getLocation("offlineLoc." + uuid.toString(), getFile("TPortConfig"));
    }
    
    public static Location getLocation(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        return player != null ? player.getLocation() : getOfflineLocation(uuid);
    }
}
