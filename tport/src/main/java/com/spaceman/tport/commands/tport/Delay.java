package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.delay.Get;
import com.spaceman.tport.commands.tport.delay.Handler;
import com.spaceman.tport.commands.tport.delay.Set;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.UUID;

import static com.spaceman.tport.commandHandler.CommandTemplate.runCommands;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;
import static com.spaceman.tport.fileHander.Files.tportConfig;

public class Delay extends SubCommand {
    
    public Delay() {
        addAction(new Handler());
        addAction(new Set());
        addAction(new Get());
    }
    
    public static void setPermissionBased(boolean state) {
        tportConfig.getConfig().set("delay.permission", state);
        tportConfig.saveConfig();
    }
    public static boolean isPermissionBased() {
        return tportConfig.getConfig().getBoolean("delay.permission", false);
    }
    
    public static int delayTime(Player player) {
        if (isPermissionBased()) {
            for (PermissionAttachmentInfo p : player.getEffectivePermissions()) {
                if (p.getPermission().toLowerCase().startsWith("tport.delay.time.")) {
                    try {
                        return Integer.parseInt(p.getPermission().toLowerCase().replace("tport.delay.time.", ""));
                    } catch (NumberFormatException ignore) {
                    }
                }
            }
        } else {
            return tportConfig.getConfig().getInt("delay.time." + player.getUniqueId(), 0);
        }
        return 0;
    }
    public static int delayTime(UUID uuid) {
        if (isPermissionBased()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                return 0;
            }
            for (PermissionAttachmentInfo p : player.getEffectivePermissions()) {
                if (p.getPermission().toLowerCase().startsWith("tport.delay.time.")) {
                    try {
                        return Integer.parseInt(p.getPermission().toLowerCase().replace("tport.delay.time.", ""));
                    } catch (NumberFormatException ignore) {
                    }
                }
            }
        } else {
            return tportConfig.getConfig().getInt("delay.time." + uuid, 0);
        }
        return 0;
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport delay handler [state]
        // tport delay set <player> <delay>
        // tport delay get [player]
        
        /*
         * delay time is defined with the permission: TPort.delay.time.<time in minecraft ticks>
         * */
        
        if (args.length > 1) {
            if (runCommands(getActions(), args[1], args, player)) {
                return;
            }
        }
        sendErrorTranslation(player, "tport.command.wrongUsage", "/tport delay <handler|set|get>");
    }
}
