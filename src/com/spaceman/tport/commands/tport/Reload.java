package com.spaceman.tport.commands.tport;

import com.spaceman.tport.Main;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.commands.tport.backup.Auto;
import com.spaceman.tport.cooldown.CooldownManager;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fileHander.GettingFiles;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.logging.Level;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendInfoTheme;
import static com.spaceman.tport.permissions.PermissionHandler.loadPermissionConfig;

public class Reload extends SubCommand {
    
    public Reload() {
        setPermissions("TPort.admin.reload");
    }
    
    public static void reloadTPort() {
        
        for (String file : Arrays.asList("TPortConfig.yml", "Permissions.txt")) {
            if (!new File(Main.getInstance().getDataFolder(), file).exists()) {
                InputStream inputStream = Main.getInstance().getResource(file);
                try {
                    byte[] buffer = new byte[inputStream.available()];
                    inputStream.read(buffer);
                    new FileOutputStream(new File(Main.getInstance().getDataFolder(), file)).write(buffer);
                    Main.getInstance().getLogger().log(Level.INFO, "" + file + " did not exist, resetting it...");
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
        return new Message(TextComponent.textComponent("This command is used to reload the plugin", ColorTheme.ColorType.infoColor));
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport reload
        
        if (hasPermissionToRun(player, true)) {
            reloadTPort();
            Auto.save();
            sendInfoTheme(player, "TPort has been reloaded");
        }
    }
}
