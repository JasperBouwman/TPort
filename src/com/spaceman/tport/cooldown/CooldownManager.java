package com.spaceman.tport.cooldown;

import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

import static com.spaceman.tport.colorFormatter.ColorTheme.sendErrorTheme;

public enum CooldownManager {

    TPortTP("3000"),
    PlayerTP("3000"),
    FeatureTP("3000"),
    BiomeTP("3000"),
    Back("TPortTP");

    public static boolean loopCooldown = false;
    private static HashMap<UUID, HashMap<CooldownManager, Long>> cooldownTime = new HashMap<>();
    private static Files tportConfig = GettingFiles.getFile("TPortConfig");
    
    private String defaultValue;
    
    CooldownManager(String defValue) {
        this.defaultValue = defValue;
    }
    
    public static void setDefaultValues() {
        for (CooldownManager cooldown : CooldownManager.values()) {
            if (!tportConfig.getConfig().contains("cooldown." + cooldown.name())) {
                cooldown.edit(cooldown.defaultValue);
            }
        }
    }
    
    public static boolean contains(String name) {
        for (CooldownManager cooldownManager : CooldownManager.values()) {
            if (cooldownManager.name().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public String value() {
        return tportConfig.getConfig().getString("cooldown." + this.name());
    }

    public void edit(String value) {
        tportConfig.getConfig().set("cooldown." + this.name(), value);
        tportConfig.saveConfig();
    }

    public void edit(long value) {
        tportConfig.getConfig().set("cooldown." + this.name(), String.valueOf(value));
        tportConfig.saveConfig();
    }

    public void update(Player player) {
        if (contains(this.value())) {
            CooldownManager.valueOf(this.value()).update(player);
            return;
        }
        HashMap<CooldownManager, Long> timeMap = cooldownTime.getOrDefault(player.getUniqueId(), new HashMap<>());
        timeMap.put(this, System.currentTimeMillis());
        cooldownTime.put(player.getUniqueId(), timeMap);
    }

    private long getTime(Player player, CooldownManager start) {
        if (this.name().equals((start == null ? "" : start.name())) || loopCooldown) {
            Bukkit.getLogger().log(Level.WARNING, "[TPort] There is a loop in the cooldown configuration...");
            loopCooldown = true;
            return 0;
        }

        if (!tportConfig.getConfig().contains("cooldown." + this.name())) {
            return 0;
        } else {
            String cooldownValue = this.value();
            if (cooldownValue.equals("permission")) {
                for (PermissionAttachmentInfo permissionInfo : player.getEffectivePermissions()) {
                    if (permissionInfo.getPermission().startsWith("TPort." + this.name() + ".")) {
                        try {
                            long value = Long.parseLong(permissionInfo.getPermission().replace("TPort." + this.name() + ".", ""));
                            return (cooldownTime.getOrDefault(player.getUniqueId(), new HashMap<>()).getOrDefault(this, 0L) + value) - System.currentTimeMillis();
                        } catch (NumberFormatException nfe) {
                            Bukkit.getLogger().log(Level.WARNING, "[TPort] Permission TPort." + this.name() + ".X is not a valid Long value");
                            return 0;
                        }
                    }
                }
                return 0;
            }
            for (CooldownManager cooldownManager : CooldownManager.values()) {
                if (cooldownValue.equals(cooldownManager.name())) {
                    if (start == null) {
                        start = this;
                    }
                    return cooldownManager.getTime(player, start);
                }
            }
            long value = Long.parseLong(this.value());
            return (cooldownTime.getOrDefault(player.getUniqueId(), new HashMap<>()).getOrDefault(this, 0L) + value) - System.currentTimeMillis();
        }

    }

    public long getTime(Player player) {
        return getTime(player, null);
    }

    public boolean hasCooled(Player player) {
        return hasCooled(player, true);
    }
    
    public boolean hasCooled(Player player, boolean sendMessage) {
        long cooldown = this.getTime(player);
        if (cooldown / 1000 > 0) {
            if (sendMessage) sendErrorTheme(player, "You must wait another %s to use this again",
                    (cooldown / 1000) + " second" + ((cooldown / 1000) == 1 ? "" : "s"));
            return false;
        }
        return true;
    }
}
