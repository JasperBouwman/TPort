package com.spaceman.tport.commands.tport.backup;

import com.spaceman.tport.Main;
import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fileHander.Files;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendSuccessTheme;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;

public class Save extends SubCommand {
    
    private final EmptyCommand emptyName;
    
    public Save() {
        emptyName = new EmptyCommand();
        emptyName.setCommandName("name", ArgumentType.REQUIRED);
        emptyName.setCommandDescription(TextComponent.textComponent("This command is used to save the TPort data to a file", ColorTheme.ColorType.infoColor));
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
                sendErrorTheme(player, "Name can not begin with '%s'", "auto-");
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
                    sendSuccessTheme(player, "Successfully saved backup to %s", file.getName());
                } else {
                    sendErrorTheme(player, "Backup file name %s is already a file", args[2] + ".yml");
                }
            } catch (IOException e) {
                e.printStackTrace();
                sendErrorTheme(player, "Could not create backup file, %s", e.getMessage());
            }
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport backup save <name>");
        }
    }
}
