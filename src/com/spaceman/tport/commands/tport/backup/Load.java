package com.spaceman.tport.commands.tport.backup;

import com.spaceman.tport.Main;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.Reload;
import com.spaceman.tport.fileHander.Files;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fileHander.Files.tportData;

public class Load extends SubCommand {
    
    private final EmptyCommand emptyName;
    
    public Load() {
        emptyName = new EmptyCommand();
        emptyName.setCommandName("name", ArgumentType.REQUIRED);
        emptyName.setCommandDescription(formatInfoTranslation("tport.command.backup.load.commandDescription"));
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
                Files configFile = new Files(Main.getInstance(),"/backup/" + fileName);
                if (configFile.getConfig().contains("tport")) {
                    tportData.getConfig().set("tport", configFile.getConfig().getConfigurationSection("tport"));
                    tportData.getConfig().set("public", configFile.getConfig().getConfigurationSection("public"));
                    tportData.saveConfig();
                    
                    sendInfoTranslation(player, "tport.command.backup.load.reloading");
                    Reload.reloadTPort();
                    sendSuccessTranslation(player, "tport.command.backup.load.succeeded", fileName);
                } else {
                    sendErrorTranslation(player, "tport.command.backup.load.error", file.getName());
                }
            } else {
                sendErrorTranslation(player, "tport.command.backup.load.fileNotFound", fileName + ".yml");
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport backup load <name>");
        }
    }
}
