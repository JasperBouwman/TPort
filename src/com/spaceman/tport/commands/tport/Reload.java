package com.spaceman.tport.commands.tport;

import com.spaceman.tport.Main;
import com.spaceman.tport.colorFormatter.ColorTheme;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.cooldown.CooldownManager;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fileHander.GettingFiles;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.logging.Level;

import static com.spaceman.tport.colorFormatter.ColorTheme.sendInfoTheme;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;
import static com.spaceman.tport.permissions.PermissionHandler.loadPermissionConfig;

public class Reload extends SubCommand {
    
    public static void reloadTPort() {
        
        for (String file : Arrays.asList("TPortConfig.yml", "Permissions.txt")) {
            if (!new File(Main.getInstance().getDataFolder(), file).exists()) {
                InputStream inputStream = Main.getInstance().getResource(file);
                try {
                    byte[] buffer = new byte[inputStream.available()];
                    inputStream.read(buffer);
                    new FileOutputStream(new File(Main.getInstance().getDataFolder(), file)).write(buffer);
                    Bukkit.getLogger().log(Level.INFO, "[TPort] " + file + " did not exist, resetting it...");
                } catch (IOException ignore) {
                }
            }
        }
        
        GettingFiles.loadFiles();
        CooldownManager.setDefaultValues();
        CooldownManager.loopCooldown = false;
        loadPermissionConfig();
    }
    
    @Override
    public Message getCommandDescription() {
        return new Message(TextComponent.textComponent("This command is used to reload the plugin", ColorTheme.ColorType.infoColor),
                textComponent("\n\nPermissions: ", ColorTheme.ColorType.infoColor), textComponent("TPort.admin.reload", ColorTheme.ColorType.varInfoColor));
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport reload
        
        if (!hasPermission(player, true, "TPort.admin.reload")) {
            return;
        }
        
        reloadTPort();
        sendInfoTheme(player, "TPort has been reloaded");
    }
}
