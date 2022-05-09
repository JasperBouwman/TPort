package com.spaceman.tport.commands.tport.backup;

import com.spaceman.tport.Main;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fileHander.Files;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;

public class Save extends SubCommand {
    
    private final EmptyCommand emptyName;
    
    public Save() {
        emptyName = new EmptyCommand();
        emptyName.setCommandName("name", ArgumentType.REQUIRED);
        emptyName.setCommandDescription(formatInfoTranslation("tport.command.backup.save.commandDescription"));
        emptyName.setPermissions("TPort.admin.backup.save");
        addAction(emptyName);
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport backup save <name>
        
        if (!emptyName.hasPermissionToRun(player, true)) {
            return;
        }
        
        if (args.length == 3) {
            if (args[2].startsWith("auto-")) {
                sendErrorTranslation(player, "tport.command.backup.save.prefixError", "auto-");
                return;
            }
            
            new File(Main.getInstance().getDataFolder(), "/backup").mkdir();
            File file = new File(Main.getInstance().getDataFolder(), "/backup/" + args[2] + ".yml");
            try {
                if (file.createNewFile()) {
                    Files configFile = new Files(Main.getInstance(), "/backup/" + file.getName());
                    Files tportData = getFile("TPortData");
                    
                    configFile.getConfig().set("tport", tportData.getConfig().getConfigurationSection("tport"));
                    configFile.getConfig().set("public", tportData.getConfig().getConfigurationSection("public"));
                    configFile.saveConfig();
                    sendSuccessTranslation(player, "tport.command.backup.save.succeeded", file.getName());
                } else {
                    sendErrorTranslation(player, "tport.command.backup.save.nameUsed", file.getName());
                }
            } catch (IOException e) {
                e.printStackTrace();
                sendErrorTranslation(player, "tport.command.backup.save.error", e.getMessage());
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport backup save <name>");
        }
    }
}
