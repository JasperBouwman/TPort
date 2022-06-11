package com.spaceman.tport.cooldown;

import com.spaceman.tport.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.PluginManager;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varError2Color;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varErrorColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fileHander.Files.tportConfig;

public enum CooldownManager {
    
    TPortTP("3000"),
    PlayerTP("3000"),
    FeatureTP("3000"),
    BiomeTP("3000"),
    Search("10000"),
    Back("TPortTP");
    
    public static boolean loopCooldown = false;
    private static String errorOccurredWith = "null";
    private static final HashMap<UUID, HashMap<CooldownManager, Long>> cooldownTime = new HashMap<>();
    
    private final String defaultValue;
    
    CooldownManager(String defValue) {
        this.defaultValue = defValue;
    }
    
    public static void setDefaultValues() {
        CooldownManager.loopCooldown = false;
        PluginManager pm = Bukkit.getPluginManager();
        for (CooldownManager cooldown : CooldownManager.values()) {
            if (!tportConfig.getConfig().contains("cooldown." + cooldown.name())) {
                cooldown.edit(cooldown.defaultValue);
            }
            
            //registering the permissions in Bukkit
            for (CooldownManager innerCooldown : CooldownManager.values()) {
                if (cooldown != innerCooldown) {
                    try {
                        pm.addPermission(new Permission("TPort.cooldown." + cooldown.name() + "." + innerCooldown.name()));
                    } catch (Exception ignore) { }
                }
            }
        }
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
    
    public static boolean contains(String name) {
        return get(name) != null;
    }
    public static CooldownManager get(@Nullable String name) {
        for (CooldownManager cooldown : values()) {
            if (cooldown.name().equalsIgnoreCase(name)) {
                return cooldown;
            }
        }
        return null;
    }
    
    public void printValue(Player player) {
        printValue(player, null);
    }
    private void printValue(Player player, CooldownManager start) {
        if (this.name().equals((start == null ? "" : start.name())) || loopCooldown) {
            Main.getInstance().getLogger().log(Level.WARNING, "There is a loop in the cooldown configuration. This was triggered with player " + errorOccurredWith +
                    ". Check their permissions when using permissions, and check the Cooldown configuration in 'TPortConfig.yml'. Cooldown now disabled");
            loopCooldown = true;
            sendErrorTranslation(player, "tport.cooldown.cooldownManager.cooldownLoopError.user");
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (CooldownCommand.emptyCooldownValue.hasPermissionToRun(player, false)) {
                    sendErrorTranslation(onlinePlayer, "tport.cooldown.cooldownManager.cooldownLoopError.admin", errorOccurredWith);
                }
            }
            return;
        }
        
        sendInfoTranslation(player, "tport.cooldown.cooldownManager.printValue", this.name(), this.value());
        
        CooldownManager linkingTo = CooldownManager.get(this.value());
        if (linkingTo != null) {
            if (start == null) {
                start = this;
                errorOccurredWith = player.getName();
            }
            linkingTo.printValue(player, start);
        }
        if (this.value().equalsIgnoreCase("permission")) {
            for (PermissionAttachmentInfo permissionInfo : player.getEffectivePermissions()) {
                String permissionString = "TPort.cooldown." + this.name() + ".";
                if (permissionInfo.getPermission().toLowerCase().startsWith((permissionString).toLowerCase())) {
                    
                    String valueFromPermission = permissionInfo.getPermission().substring(permissionString.length());
                    linkingTo = CooldownManager.get(valueFromPermission);
                    if (linkingTo != null) {
                        if (start == null) {
                            start = this;
                            errorOccurredWith = player.getName();
                        }
                        sendInfoTranslation(player, "tport.cooldown.cooldownManager.printPermissionValue", valueFromPermission);
                        linkingTo.printValue(player, start);
                        return;
                    }
                }
            }
        }
    }
    
    public void update(Player player) {
        if (loopCooldown) return; //no recursion catch needed, getTime() is always run first which has the catcher
        
        if (this.value().equalsIgnoreCase("permission")) {
            for (PermissionAttachmentInfo permissionInfo : player.getEffectivePermissions()) {
                String permissionString = "TPort.cooldown." + this.name() + ".";
                if (permissionInfo.getPermission().toLowerCase().startsWith((permissionString).toLowerCase())) {
    
                    String valueFromPermission = permissionInfo.getPermission().substring(permissionString.length());
                    if (contains(valueFromPermission)) {
                        //noinspection ConstantConditions -> CooldownManager#get should not return 'null' because of the CooldownManager#contains
                        CooldownManager.get(valueFromPermission).update(player);
                        return;
                    }
                }
            }
        }
        if (contains(this.value())) {
            //noinspection ConstantConditions -> CooldownManager#get should not return 'null' because of the CooldownManager#contains
            CooldownManager.get(this.value()).update(player);
            return;
        }
        HashMap<CooldownManager, Long> timeMap = cooldownTime.getOrDefault(player.getUniqueId(), new HashMap<>());
        timeMap.put(this, System.currentTimeMillis());
        cooldownTime.put(player.getUniqueId(), timeMap);
    }
    
    public long getTime(Player player) {
        return getTime(player, null);
    }
    private long getTime(Player player, CooldownManager start) {
        if (this.name().equals((start == null ? "" : start.name())) || loopCooldown) {
            Main.getInstance().getLogger().log(Level.WARNING, "There is a loop in the cooldown configuration. This was triggered with player " + errorOccurredWith +
                    ". Check their permissions when using permissions, and check the Cooldown configuration in 'TPortConfig.yml'. Cooldown now disabled");
            loopCooldown = true;
            return 0;
        }
        if (!tportConfig.getConfig().contains("cooldown." + this.name())) {
            return 0;
        } else {
            CooldownManager linkingTo = CooldownManager.get(this.value());
            if (linkingTo != null) {
                if (start == null) {
                    start = this;
                    errorOccurredWith = player.getName();
                }
                return linkingTo.getTime(player, start);
            }
            if (this.value().equals("permission")) {
                for (PermissionAttachmentInfo permissionInfo : player.getEffectivePermissions()) {
                    String permissionString = "TPort.cooldown." + this.name() + ".";
                    if (permissionInfo.getPermission().toLowerCase().startsWith(permissionString.toLowerCase())) {
                        
                        String valueFromPermission = permissionInfo.getPermission().substring(permissionString.length());
                        linkingTo = CooldownManager.get(valueFromPermission);
                        if (linkingTo != null) {
                            if (start == null) {
                                start = this;
                                errorOccurredWith = player.getName();
                            }
                            return linkingTo.getTime(player, start);
                        }
                        try {
                            long longValue = Long.parseLong(valueFromPermission);
                            return (cooldownTime.getOrDefault(player.getUniqueId(), new HashMap<>()).getOrDefault(this, 0L) + longValue) - System.currentTimeMillis();
                        } catch (NumberFormatException nfe) {
                            Main.getInstance().getLogger().log(Level.WARNING, "Permission TPort.cooldown." + this.name() + ".X is not a valid Long value/other cooldown. " +
                                    "This was triggered with player " + player.getName() + ". Check their permissions");
                            return 0;
                        }
                    }
                }
                return 0;
            }
            try {
                long value = Long.parseLong(this.value());
                return (cooldownTime.getOrDefault(player.getUniqueId(), new HashMap<>()).getOrDefault(this, 0L) + value) - System.currentTimeMillis();
            } catch (NumberFormatException nfe) {
                Main.getInstance().getLogger().log(Level.WARNING, "Value set from '" + this.name() + "' is not valid. This was triggered with player " + player.getName() +
                        ". Check the Cooldown configuration in 'TPortConfig.yml'");
                return 0;
            }
        }
    }
    
    public boolean hasCooled(Player player) {
        return hasCooled(player, true);
    }
    public boolean hasCooled(Player player, boolean sendMessage) {
        long cooldownInSeconds = this.getTime(player) / 1000;
        if (cooldownInSeconds > 0) {
            if (sendMessage) {
//                sendErrorTheme(player, "You must wait another %s to use this again",
//                        (cooldownInSeconds) + " second" + ((cooldownInSeconds) == 1 ? "" : "s"));
                
                sendErrorTranslation(player, "tport.cooldown.cooldownManager.delayTime", cooldownInSeconds,
                        (cooldownInSeconds == 1 ? formatTranslation(varErrorColor, varError2Color, "tport.command.second") :
                                formatTranslation(varErrorColor, varError2Color, "tport.command.seconds")));
            }
            return false;
        }
        return true;
    }
}
