package com.spaceman.tport.cooldown;

import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

public enum CooldownManager {

    TPortTP,
    PlayerTP,
    FeatureTP,
    BiomeTP,
    Back;

    public static boolean loopCooldown = false;
    private static HashMap<UUID, HashMap<CooldownManager, Long>> cooldownTime = new HashMap<>();
    private static Files tportConfig = GettingFiles.getFile("TPortConfig");

    public static boolean contains(String name) {
        for (CooldownManager cooldownManager : CooldownManager.values()) {
            if (name.equals(cooldownManager.name())) {
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

}
