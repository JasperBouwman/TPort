package com.spaceman.tport.playerUUID;

import com.spaceman.tport.Main;
import com.spaceman.tport.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

public class PlayerUUID {
    
    public static String getPlayerName(String uuid) {
        return getPlayerName(UUID.fromString(uuid));
    }
    
    public static String getPlayerName(UUID uuid) {
        if (uuid == null) return null;
        try {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            return !offlinePlayer.hasPlayedBefore() && !offlinePlayer.isOnline() ? null : offlinePlayer.getName();
        } catch (IllegalArgumentException iae) {
            return null;
        }
    }
    
    // returns null if not found
    public static UUID getPlayerUUID(String playerName) {
        return Main.getOrDefault(getProfile(playerName), new Pair<String, UUID>(null, null)).getRight();
    }
    
    public static UUID getPlayerUUID(Player player, String name) {
        UUID newPlayerUUID = PlayerUUID.getPlayerUUID(name);
        if (newPlayerUUID == null) {
            player.sendMessage(ChatColor.RED + "Could not find a player named " + ChatColor.DARK_RED + name);
            return null;
        }
        return newPlayerUUID;
    }
    
    // returns null values if not found
    public static Pair<String, UUID> getProfile(String playerName) {
        for (OfflinePlayer op : Bukkit.getOfflinePlayers()) {
            if ((op.hasPlayedBefore() || op.isOnline()) && op.getName() != null && op.getName().equalsIgnoreCase(playerName)) {
                return new Pair<>(op.getName(), op.getUniqueId());
            }
        }
        return new Pair<>(null, null);
    }
}
