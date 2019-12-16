package com.spaceman.tport.commands.tport.backup;

import com.spaceman.tport.Main;
import com.spaceman.tport.colorFormatter.ColorTheme;
import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fileHander.Files;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

import static com.spaceman.tport.colorFormatter.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.colorFormatter.ColorTheme.sendSuccessTheme;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;

public class Save extends SubCommand {
    
    public Save() {
        EmptyCommand emptyCommand = new EmptyCommand();
        emptyCommand.setCommandName("name", ArgumentType.REQUIRED);
        emptyCommand.setCommandDescription(TextComponent.textComponent("This command is used to save the TPort data to a file", ColorTheme.ColorType.infoColor),
                textComponent("\n\nPermission: ", ColorTheme.ColorType.infoColor), textComponent("TPort.admin.backup.save", ColorTheme.ColorType.varInfoColor));
        addAction(emptyCommand);
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport backup save <name>
        
        if (!hasPermission(player, true, "TPort.admin.backup.save")) {
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
