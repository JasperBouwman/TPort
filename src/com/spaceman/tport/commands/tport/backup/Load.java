package com.spaceman.tport.commands.tport.backup;

import com.spaceman.tport.Main;
import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.commands.tport.Reload;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fileHander.Files;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;

public class Load extends SubCommand {
    
    private final EmptyCommand emptyName;
    
    public Load() {
        emptyName = new EmptyCommand();
        emptyName.setCommandName("name", ArgumentType.REQUIRED);
        emptyName.setCommandDescription(TextComponent.textComponent("This command is used to load the data in the given file to TPort", ColorType.infoColor));
        emptyName.setPermissions("TPort.admin.backup.load");
        addAction(emptyName);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        if (!emptyName.hasPermissionToRun(player, false)) {
            return Collections.emptyList();
        }
        return Arrays.stream(Objects.requireNonNull(new File(Main.getInstance().getDataFolder(), "/backup").listFiles()))
                .filter(f -> f.getName().endsWith(".yml")
                        && !f.getName().contains(" "))
                .map(f -> f.getName().split("\\.")[0])
                .collect(Collectors.toList());
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport backup load <name>
        
        if (!emptyName.hasPermissionToRun(player, true)) {
            return;
        }
        
        if (args.length == 3) {
            String fileName = args[2].split("\\.")[0];
            File file = new File(Main.getInstance().getDataFolder(), "/backup/" + fileName + ".yml");
            if (file.exists()) {
                Files configFile = new Files(Main.getInstance(), fileName);
                Files tportData = getFile("TPortData");
                if (configFile.getConfig().contains("tport")) {
                    tportData.getConfig().set("tport", configFile.getConfig().getConfigurationSection("tport"));
                    tportData.getConfig().set("public", configFile.getConfig().getConfigurationSection("public"));
                    tportData.saveConfig();
                    
                    sendInfoTheme(player, "Reloading TPort to effect the changes");
                    Reload.reloadTPort();
                    sendSuccessTheme(player, "Successfully loaded backup %s", fileName);
                } else {
                    sendErrorTheme(player, "Backup file %s is not correct", fileName + ".yml");
                }
            } else {
                sendErrorTheme(player, "Backup file %s does not exist", fileName + ".yml");
            }
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport backup load <name>");
        }
    }
}
