package com.spaceman.tport.commands.tport.backup;

import com.spaceman.tport.Main;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.Reload;
import com.spaceman.tport.fileHander.Files;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    
    public static List<String> getBackups(boolean sortByModify) {
        Stream<File> s = Arrays.stream(Objects.requireNonNull(new File(Main.getInstance().getDataFolder(), "/backup").listFiles()))
                .filter(f -> f.getName().endsWith(".yml") && !f.getName().contains(" "));
        if (sortByModify) {
            s = s.sorted((file1, file2) -> Long.compare(file2.lastModified(), file1.lastModified()));
        }
        return s.map(f -> f.getName().split("\\.")[0])
                .collect(Collectors.toList());
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        if (!emptyName.hasPermissionToRun(player, false)) {
            return Collections.emptyList();
        }
        return getBackups(false);
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport backup load <name>
    
        if (args.length != 3) {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport backup load <name>");
            return;
        }
        if (!emptyName.hasPermissionToRun(player, true)) {
            return;
        }
        
        String fileName = args[2].split("\\.")[0];
        File file = new File(Main.getInstance().getDataFolder(), "/backup/" + fileName + ".yml");
        if (!file.exists()) {
            sendErrorTranslation(player, "tport.command.backup.load.fileNotFound", fileName + ".yml");
            return;
        }
        Files configFile = new Files(Main.getInstance(),"/backup/" + fileName);
        if (!configFile.getConfig().contains("tport")) {
            sendErrorTranslation(player, "tport.command.backup.load.error", file.getName());
            return;
        }
        
        tportData.getConfig().set("tport", configFile.getConfig().getConfigurationSection("tport"));
        tportData.getConfig().set("public", configFile.getConfig().getConfigurationSection("public"));
        tportData.saveConfig();
        
        sendInfoTranslation(player, "tport.command.backup.load.reloading");
        Reload.reloadTPort();
        sendSuccessTranslation(player, "tport.command.backup.load.succeeded", fileName);
    }
}
