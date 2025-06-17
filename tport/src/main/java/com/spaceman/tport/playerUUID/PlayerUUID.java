package com.spaceman.tport.playerUUID;

import com.spaceman.tport.Pair;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;
import static com.spaceman.tport.fileHander.Files.tportData;

public class PlayerUUID {
    
    public static String getPlayerName(String uuid) {
        return getPlayerName(UUID.fromString(uuid));
    }
    
    public static String getPlayerName(UUID uuid) {
        if (uuid == null) return null;
        
        try {
            OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
            
            if ((op.hasPlayedBefore() || op.isOnline()) && op.getName() != null) {
                if (tportData.getConfig().contains("tport." + op.getUniqueId())) {
                    return op.getName();
                }
            }
        } catch (IllegalArgumentException ignore) { }
        return null;
    }
    // returns null values if not found
    @Nonnull
    public static Pair<String, UUID> getProfile(String playerName, @Nullable Player sender) {
        for (OfflinePlayer op : Bukkit.getOfflinePlayers()) {
            if ((op.hasPlayedBefore() || op.isOnline()) && op.getName() != null && op.getName().equalsIgnoreCase(playerName)) {
                if (tportData.getConfig().contains("tport." + op.getUniqueId())) {
                    return new Pair<>(op.getName(), op.getUniqueId());
                } else {
                    // player not registered anymore
                    sendErrorTranslation(sender, "tport.command.playerNotFound", playerName);
                    return new Pair<>(null, null);
                }
            }
        }
        // player never joined
        sendErrorTranslation(sender, "tport.command.playerNotFound", playerName);
        return new Pair<>(null, null);
    }
    
    public static List<Pair<String, UUID>> getProfiles() {
        ArrayList<Pair<String, UUID>> profiles = new ArrayList<>();
        
        for (OfflinePlayer op : Bukkit.getOfflinePlayers()) {
            if ((op.hasPlayedBefore() || op.isOnline()) && op.getName() != null) {
                if (tportData.getConfig().contains("tport." + op.getUniqueId())) {
                    profiles.add(new Pair<>(op.getName(), op.getUniqueId()));
                }
            }
        }
        return profiles;
    }
    
    @Nullable
    public static UUID getPlayerUUID(String playerName) {
        return getPlayerUUID(playerName, null);
    }
    @Nullable
    public static UUID getPlayerUUID(String playerName, @Nullable Player sender) {
        Pair<String, UUID> pair = getProfile(playerName, sender);
        return pair.getRight();
    }
    
    @Nullable
    public static UUID getPlayerUUID_OLD2(String playerName) {
        for (OfflinePlayer op : Bukkit.getOfflinePlayers()) {
            if ((op.hasPlayedBefore() || op.isOnline()) && op.getName() != null && op.getName().equalsIgnoreCase(playerName)) {
                return op.getUniqueId();
            }
        }
        return null;
    }
    public static ArrayList<String> getPlayerNames() {
        ArrayList<String> list = new ArrayList<>();
        for (OfflinePlayer op : Bukkit.getOfflinePlayers()) {
            if (tportData.getConfig().contains("tport." + op.getUniqueId())) {
                list.add(op.getName());
            }
        }
        return list;
    }
    
    public static ArrayList<UUID> getPlayerUUIDs() {
        ArrayList<UUID> list = new ArrayList<>();
        for (OfflinePlayer op : Bukkit.getOnlinePlayers()) {
            if (tportData.getConfig().contains("tport." + op.getUniqueId())) {
                list.add(op.getUniqueId());
            }
        }
        return list;
    }
}
