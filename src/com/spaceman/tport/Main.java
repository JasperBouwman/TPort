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
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

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
         * changelog 1.13.3 update:
         *
         */

        /*
        * todo
        * make tab complete more efficient
        * /tport log
        * add permissions
        *
        * */

        if (!new File(this.getDataFolder(), "TPortConfig.yml").exists()) {
            InputStream inputStream = this.getResource("TPortConfig.yml");
            try {
                byte[] buffer = new byte[inputStream.available()];
                inputStream.read(buffer);
                new FileOutputStream(new File(this.getDataFolder(), "TPortConfig.yml")).write(buffer);
                this.getLogger().log(Level.INFO, "[TPort] TPortConfig.yml did not exist, resetting it...");
            } catch (IOException ignore) {
            }
        }

        new GettingFiles(this);

        getCommand("tport").setExecutor(new TPort());
        getCommand("tport").setTabCompleter(new TabComplete());

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new InventoryClick(), this);
        pm.registerEvents(new CompassEvents(), this);
        pm.registerEvents(new JoinEvent(this), this);

        if (!getFiles("TPortData").getConfig().contains("tport")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                JoinEvent.setData(this, player);
            }
        }
    }

    public static class Cooldown {
        private static HashMap<UUID, Long> cooldownTPortTP = new HashMap<>();
        private static HashMap<UUID, Long> cooldownPlayerTP = new HashMap<>();
        private static HashMap<UUID, Long> cooldownFeatureTP = new HashMap<>();
        private static HashMap<UUID, Long> cooldownBiomeTP = new HashMap<>();
        private static HashMap<UUID, Long> cooldownBack = new HashMap<>();
        public static boolean loopCooldown = false;

        public static void updateTPortTPCooldown(Player player) {
            if (loopCooldown) {
                return;
            }
            Files tportConfig = getFiles("TPortConfig");
            if (!tportConfig.getConfig().contains("TPortTPCooldown")) {
                return;
            }
            switch (tportConfig.getConfig().getString("TPortTPCooldown")) {
                case "PlayerTPCooldown":
                    updatePlayerTPCooldown(player);
                    break;
                case "FeatureTPCooldown":
                    updateFeatureTPCooldown(player);
                    break;
                case "BiomeTPCooldown":
                    updateBiomeTPCooldown(player);
                    break;
                case "BackCooldown":
                    updateBackCooldown(player);
                    break;
                default:
                    cooldownTPortTP.put(player.getUniqueId(), System.currentTimeMillis());
            }
        }

        public static void updatePlayerTPCooldown(Player player) {
            if (loopCooldown) {
                return;
            }
            Files tportConfig = getFiles("TPortConfig");
            if (!tportConfig.getConfig().contains("PlayerTPCooldown")) {
                return;
            }
            switch (tportConfig.getConfig().getString("PlayerTPCooldown")) {
                case "TPortTPCooldown":
                    updateTPortTPCooldown(player);
                    break;
                case "FeatureTPCooldown":
                    updateFeatureTPCooldown(player);
                    break;
                case "BiomeTPCooldown":
                    updateBiomeTPCooldown(player);
                    break;
                case "BackCooldown":
                    updateBackCooldown(player);
                    break;
                default:
                    cooldownPlayerTP.put(player.getUniqueId(), System.currentTimeMillis());
            }
        }

        public static void updateFeatureTPCooldown(Player player) {
            if (loopCooldown) {
                return;
            }
            Files tportConfig = getFiles("TPortConfig");
            if (!tportConfig.getConfig().contains("FeatureTPCooldown")) {
                return;
            }
            switch (tportConfig.getConfig().getString("FeatureTPCooldown")) {
                case "TPortTPCooldown":
                    updateTPortTPCooldown(player);
                    break;
                case "PlayerTPCooldown":
                    updatePlayerTPCooldown(player);
                    break;
                case "BiomeTPCooldown":
                    updateBiomeTPCooldown(player);
                    break;
                case "BackCooldown":
                    updateBackCooldown(player);
                    break;
                default:
                    cooldownFeatureTP.put(player.getUniqueId(), System.currentTimeMillis());
            }
        }

        public static void updateBiomeTPCooldown(Player player) {
            if (loopCooldown) {
                return;
            }
            Files tportConfig = getFiles("TPortConfig");
            if (!tportConfig.getConfig().contains("BiomeTPCooldown")) {
                return;
            }
            switch (tportConfig.getConfig().getString("BiomeTPCooldown")) {
                case "TPortTPCooldown":
                    updateTPortTPCooldown(player);
                    break;
                case "PlayerTPCooldown":
                    updatePlayerTPCooldown(player);
                    break;
                case "FeatureTPCooldown":
                    updateFeatureTPCooldown(player);
                    break;
                case "BackCooldown":
                    updateBackCooldown(player);
                    break;
                default:
                    cooldownBiomeTP.put(player.getUniqueId(), System.currentTimeMillis());
            }
        }

        public static void updateBackCooldown(Player player) {
            if (loopCooldown) {
                return;
            }
            Files tportConfig = getFiles("TPortConfig");
            if (!tportConfig.getConfig().contains("BackCooldown")) {
                return;
            }
            switch (tportConfig.getConfig().getString("BackCooldown")) {
                case "TPortTPCooldown":
                    updateTPortTPCooldown(player);
                    break;
                case "PlayerTPCooldown":
                    updatePlayerTPCooldown(player);
                    break;
                case "FeatureTPCooldown":
                    updateFeatureTPCooldown(player);
                    break;
                case "BiomeTPCooldown":
                    updateBackCooldown(player);
                    break;
                default:
                    cooldownBack.put(player.getUniqueId(), System.currentTimeMillis());
            }
        }

        public static long cooldownTPortTP(Player player) {
            return cooldownTPortTP(player, "");
        }

        public static long cooldownPlayerTP(Player player) {
            return cooldownPlayerTP(player, "");
        }

        public static long cooldownFeatureTP(Player player) {
            return cooldownFeatureTP(player, "");
        }

        public static long cooldownBiomeTP(Player player) {
            return cooldownBiomeTP(player, "");
        }

        public static long cooldownBack(Player player) {
            return cooldownBack(player, "");
        }

        private static long cooldownTPortTP(Player player, String initial) {
            if (initial.equals("TPortTPCooldown")) {
                Bukkit.getLogger().log(Level.WARNING, "[TPort] There is a loop in the cooldown configuration...");
                loopCooldown = true;
                return 0;
            }

            Files tportConfig = getFiles("TPortConfig");

            if (!tportConfig.getConfig().contains("TPortTPCooldown")) {
                return 0;
            } else {
                switch (tportConfig.getConfig().getString("TPortTPCooldown")) {
                    case "permission":
                        for (PermissionAttachmentInfo permissionInfo : player.getEffectivePermissions()) {
                            if (permissionInfo.getPermission().startsWith("TPort.TPortTPCooldown")) {
                                try {
                                    long cooldownPlayer = Long.parseLong(permissionInfo.getPermission().replace("TPort.TPortTPCooldown.", ""));
                                    return (cooldownTPortTP.getOrDefault(player.getUniqueId(), 0L) + cooldownPlayer) - System.currentTimeMillis();
                                } catch (NumberFormatException nfe) {
                                    Bukkit.getLogger().log(Level.WARNING, "[TPort] Permission TPort.TPortTPCooldown.X is not a valid Long value");
                                    return 0;
                                }
                            }
                        }
                        break;
                    case "PlayerTPCooldown":
                        if (initial.equals("")) {
                            initial = "TPortTPCooldown";
                        }
                        return cooldownPlayerTP(player, initial);
                    case "FeatureTPCooldown":
                        if (initial.equals("")) {
                            initial = "TPortTPCooldown";
                        }
                        return cooldownFeatureTP(player, initial);
                    case "BiomeTPCooldown":
                        if (initial.equals("")) {
                            initial = "TPortTPCooldown";
                        }
                        return cooldownBiomeTP(player, initial);
                    case "BackCooldown":
                        if (initial.equals("")) {
                            initial = "TPortTPCooldown";
                        }
                        return cooldownBack(player, initial);
                    default:
                        long value = tportConfig.getConfig().getLong("TPortTPCooldown");
                        return (cooldownTPortTP.getOrDefault(player.getUniqueId(), 0L) + value) - System.currentTimeMillis();
                }
            }

            return 0;
        }

        private static long cooldownPlayerTP(Player player, String initial) {
            if (initial.equals("PlayerTPCooldown")) {
                Bukkit.getLogger().log(Level.WARNING, "[TPort] There is a loop in the cooldown configuration...");
                loopCooldown = true;
                return 0;
            }
            Files tportConfig = getFiles("TPortConfig");

            if (!tportConfig.getConfig().contains("PlayerTPCooldown")) {
                return 0;
            } else {
                switch (tportConfig.getConfig().getString("PlayerTPCooldown")) {
                    case "permission":
                        for (PermissionAttachmentInfo permissionInfo : player.getEffectivePermissions()) {
                            if (permissionInfo.getPermission().startsWith("TPort.PlayerTPCooldown")) {
                                try {
                                    long cooldownPlayer = Long.parseLong(permissionInfo.getPermission().replace("TPort.PlayerTPCooldown.", ""));
                                    return (cooldownTPortTP.getOrDefault(player.getUniqueId(), 0L) + cooldownPlayer) - System.currentTimeMillis();
                                } catch (NumberFormatException nfe) {
                                    Bukkit.getLogger().log(Level.WARNING, "[TPort] Permission TPort.PlayerTPCooldown.X is not a valid Long value");
                                    return 0;
                                }
                            }
                        }
                        break;
                    case "TPortTPCooldown":
                        if (initial.equals("")) {
                            initial = "PlayerTPCooldown";
                        }
                        return cooldownTPortTP(player, initial);
                    case "FeatureTPCooldown":
                        if (initial.equals("")) {
                            initial = "PlayerTPCooldown";
                        }
                        return cooldownFeatureTP(player, initial);
                    case "BiomeTPCooldown":
                        if (initial.equals("")) {
                            initial = "PlayerTPCooldown";
                        }
                        return cooldownBiomeTP(player, initial);
                    case "BackCooldown":
                        if (initial.equals("")) {
                            initial = "PlayerTPCooldown";
                        }
                        return cooldownBack(player, initial);
                    default:
                        long value = tportConfig.getConfig().getLong("PlayerTPCooldown");
                        return (cooldownPlayerTP.getOrDefault(player.getUniqueId(), 0L) + value) - System.currentTimeMillis();
                }
            }

            return 0;
        }

        private static long cooldownFeatureTP(Player player, String initial) {
            if (initial.equals("FeatureTPCooldown")) {
                Bukkit.getLogger().log(Level.WARNING, "[TPort] There is a loop in the cooldown configuration...");
                loopCooldown = true;
                return 0;
            }
            Files tportConfig = getFiles("TPortConfig");

            if (!tportConfig.getConfig().contains("FeatureTPCooldown")) {
                return 0;
            } else {
                switch (tportConfig.getConfig().getString("FeatureTPCooldown")) {
                    case "permission":
                        for (PermissionAttachmentInfo permissionInfo : player.getEffectivePermissions()) {
                            if (permissionInfo.getPermission().startsWith("TPort.FeatureTPCooldown")) {
                                try {
                                    long cooldownPlayer = Long.parseLong(permissionInfo.getPermission().replace("TPort.FeatureTPCooldown.", ""));
                                    return (cooldownTPortTP.getOrDefault(player.getUniqueId(), 0L) + cooldownPlayer) - System.currentTimeMillis();
                                } catch (NumberFormatException nfe) {
                                    Bukkit.getLogger().log(Level.WARNING, "[TPort] Permission TPort.FeatureTPCooldown.X is not a valid Long value");
                                    return 0;
                                }
                            }
                        }
                        break;
                    case "PlayerTPCooldown":
                        if (initial.equals("")) {
                            initial = "FeatureTPCooldown";
                        }
                        return cooldownPlayerTP(player, initial);
                    case "TPortTPCooldown":
                        if (initial.equals("")) {
                            initial = "FeatureTPCooldown";
                        }
                        return cooldownTPortTP(player, initial);
                    case "BiomeTPCooldown":
                        if (initial.equals("")) {
                            initial = "FeatureTPCooldown";
                        }
                        return cooldownBiomeTP(player, initial);
                    case "BackCooldown":
                        if (initial.equals("")) {
                            initial = "FeatureTPCooldown";
                        }
                        return cooldownBack(player, initial);
                    default:
                        long value = tportConfig.getConfig().getLong("FeatureTPCooldown");
                        return (cooldownFeatureTP.getOrDefault(player.getUniqueId(), 0L) + value) - System.currentTimeMillis();
                }
            }

            return 0;
        }

        private static long cooldownBiomeTP(Player player, String initial) {
            if (initial.equals("BiomeTPCooldown")) {
                Bukkit.getLogger().log(Level.WARNING, "[TPort] There is a loop in the cooldown configuration...");
                loopCooldown = true;
                return 0;
            }
            Files tportConfig = getFiles("TPortConfig");

            if (!tportConfig.getConfig().contains("BiomeTPCooldown")) {
                return 0;
            } else {
                switch (tportConfig.getConfig().getString("BiomeTPCooldown")) {
                    case "permission":
                        for (PermissionAttachmentInfo permissionInfo : player.getEffectivePermissions()) {
                            if (permissionInfo.getPermission().startsWith("TPort.BiomeTPCooldown")) {
                                try {
                                    long cooldownPlayer = Long.parseLong(permissionInfo.getPermission().replace("TPort.BiomeTPCooldown.", ""));
                                    return (cooldownTPortTP.getOrDefault(player.getUniqueId(), 0L) + cooldownPlayer) - System.currentTimeMillis();
                                } catch (NumberFormatException nfe) {
                                    Bukkit.getLogger().log(Level.WARNING, "[TPort] Permission TPort.BiomeTPCooldown.X is not a valid Long value");
                                    return 0;
                                }
                            }
                        }
                        break;
                    case "PlayerTPCooldown":
                        if (initial.equals("")) {
                            initial = "BiomeTPCooldown";
                        }
                        return cooldownPlayerTP(player, initial);
                    case "FeatureTPCooldown":
                        if (initial.equals("")) {
                            initial = "BiomeTPCooldown";
                        }
                        return cooldownFeatureTP(player, initial);
                    case "TPortTPCooldown":
                        if (initial.equals("")) {
                            initial = "BiomeTPCooldown";
                        }
                        return cooldownTPortTP(player, initial);
                    case "BackCooldown":
                        if (initial.equals("")) {
                            initial = "BiomeTPCooldown";
                        }
                        return cooldownBack(player, initial);
                    default:
                        long value = tportConfig.getConfig().getLong("BiomeTPCooldown");
                        return (cooldownBiomeTP.getOrDefault(player.getUniqueId(), 0L) + value) - System.currentTimeMillis();
                }
            }

            return 0;
        }

        private static long cooldownBack(Player player, String initial) {
            if (initial.equals("BackCooldown")) {
                Bukkit.getLogger().log(Level.WARNING, "[TPort] There is a loop in the cooldown configuration...");
                loopCooldown = true;
                return 0;
            }
            Files tportConfig = getFiles("TPortConfig");

            if (!tportConfig.getConfig().contains("BackCooldown")) {
                return 0;
            } else {
                switch (tportConfig.getConfig().getString("BackCooldown")) {
                    case "permission":
                        for (PermissionAttachmentInfo permissionInfo : player.getEffectivePermissions()) {

                            if (permissionInfo.getPermission().startsWith("TPort.BackCooldown")) {
                                try {
                                    long cooldownPlayer = Long.parseLong(permissionInfo.getPermission().replace("TPort.BackCooldown.", ""));
                                    return (cooldownTPortTP.getOrDefault(player.getUniqueId(), 0L) + cooldownPlayer) - System.currentTimeMillis();
                                } catch (NumberFormatException nfe) {
                                    Bukkit.getLogger().log(Level.WARNING, "[TPort] Permission TPort.BackCooldown.X is not a valid Long value");
                                    return 0;
                                }
                            }
                        }
                        break;
                    case "PlayerTPCooldown":
                        if (initial.equals("")) {
                            initial = "BackCooldown";
                        }
                        return cooldownPlayerTP(player, initial);
                    case "FeatureTPCooldown":
                        if (initial.equals("")) {
                            initial = "BackCooldown";
                        }
                        return cooldownFeatureTP(player, initial);
                    case "BiomeTPCooldown":
                        if (initial.equals("")) {
                            initial = "BackCooldown";
                        }
                        return cooldownBiomeTP(player, initial);
                    case "TPortTPCooldown":
                        if (initial.equals("")) {
                            initial = "BackCooldown";
                        }
                        return cooldownTPortTP(player, initial);
                    default:
                        long value = tportConfig.getConfig().getLong("BackCooldown");
                        return (cooldownBack.getOrDefault(player.getUniqueId(), 0L) + value) - System.currentTimeMillis();
                }
            }

            return 0;
        }

    }
}
