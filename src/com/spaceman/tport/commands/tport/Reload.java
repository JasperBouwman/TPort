package com.spaceman.tport.commands.tport;

import com.spaceman.tport.Main;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.backup.Auto;
import com.spaceman.tport.cooldown.CooldownManager;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import com.spaceman.tport.tpEvents.TPEManager;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.logging.Level;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatInfoTranslation;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendInfoTranslation;
import static com.spaceman.tport.fancyMessage.language.Language.loadLanguages;

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
        
        Files tportConfig = GettingFiles.getFile("TPortConfig");
        if (!tportConfig.getConfig().contains("biomeTP.searches")) {
            tportConfig.getConfig().set("biomeTP.searches", 100);
            tportConfig.saveConfig();
        }
        if (!tportConfig.getConfig().contains("tags.list")) {
            Tag.resetTags();
        }
        
        loadLanguages();
        CooldownManager.setDefaultValues();
        TPEManager.loadTPE(tportConfig);
        ColorTheme.loadThemes(tportConfig);
        Tag.loadTags();
    }
    
    @Override
    public Message getCommandDescription() {
        return formatInfoTranslation("tport.command.reload.commandDescription");
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport reload
        
        if (hasPermissionToRun(player, true)) {
            reloadTPort();
            Auto.save();
            sendInfoTranslation(player, "tport.command.reload.succeeded");
        }
    }
}
