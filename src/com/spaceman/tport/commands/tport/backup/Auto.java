package com.spaceman.tport.commands.tport.backup;

import com.spaceman.tport.Main;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fileHander.Files;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfoColor;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;

public class Auto extends SubCommand {
    
    private final EmptyCommand emptyCount;
    
    public Auto() {
        emptyCount = new EmptyCommand();
        emptyCount.setCommandName("count", ArgumentType.OPTIONAL);
        emptyCount.setCommandDescription(formatInfoTranslation("tport.command.backup.auto.count.commandDescription"));
        emptyCount.setPermissions("TPort.admin.backup.auto");
        
        EmptyCommand emptyState = new EmptyCommand();
        emptyState.setCommandName("state", ArgumentType.OPTIONAL);
        emptyState.setCommandDescription(formatInfoTranslation("tport.command.backup.auto.state.commandDescription"));
        emptyState.setPermissions(emptyCount.getPermissions());
        
        addAction(emptyState);
        addAction(emptyCount);
    }
    
    public static void save() {
        Files tportConfig = getFile("TPortConfig");
        if (tportConfig.getConfig().getBoolean("backup.auto.state", false)) {
            File dir = new File(Main.getInstance().getDataFolder(), "/backup");
            if (!dir.mkdir()) {
                ArrayList<File> files = new ArrayList<>();
                for (File f : dir.listFiles()) {
                    if (f.getName().startsWith("auto-")) {
                        files.add(f);
                    }
                }
                files.sort(Comparator.comparingLong(File::lastModified));
                int count = tportConfig.getConfig().getInt("backup.auto.count", 10);
                if (files.size() >= count) {
                    for (int i = 0; i <= files.size() - count; i++) {
                        try {
                            if (!files.get(i).delete()) {
                                Main.getInstance().getLogger().warning("Could not delete redundant backup " + files.get(i).getName());
                            }
                        } catch (SecurityException se) {
                            Main.getInstance().getLogger().warning("Could not delete redundant backup " + files.get(i).getName());
                        }
                    }
                }
            }
            String name = new SimpleDateFormat("dd-MMM-yyyy-hh;mm;ss").format(Calendar.getInstance().getTime());
            int version = 0;
            while (true) {
                String suffix = "";
                if (version != 0) {
                    suffix = "(" + version + ")";
                }
                File file = new File(Main.getInstance().getDataFolder(), "/backup/auto-" + name + suffix + ".yml");
                try {
                    if (file.createNewFile()) {
                        Files configFile = new Files(Main.getInstance(), "/backup/auto-" + name + suffix + ".yml");
                        Files tportData = getFile("TPortData");
                        
                        configFile.getConfig().set("tport", tportData.getConfig().getConfigurationSection("tport"));
                        configFile.getConfig().set("public", tportData.getConfig().getConfigurationSection("public"));
                        configFile.saveConfig();
                        Main.getInstance().getLogger().info("Auto backup " + name + " succeeded");
                        return;
                    }
                    version++;
                } catch (IOException e) {
                    e.printStackTrace();
                    Main.getInstance().getLogger().warning("Could not auto backup file " + name + ": " + e.getMessage());
                    return;
                }
            }
        }
    }
    
    @Override
    public Message getCommandDescription() {
        return formatInfoTranslation("tport.command.backup.auto.commandDescription");
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return emptyCount.hasPermissionToRun(player, false) ? Arrays.asList("true", "false", "<count>") : Collections.emptyList();
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport backup auto [state|count]
        
        if (!emptyCount.hasPermissionToRun(player, true)) {
            return;
        }
        
        if (args.length == 2) {
            Files tportConfig = getFile("TPortConfig");
            int count = tportConfig.getConfig().getInt("backup.auto.count", 10);
            
            Message stateAsMessage;
            if (tportConfig.getConfig().getBoolean("backup.auto.state", false)) {
                stateAsMessage = formatTranslation(ColorType.goodColor, varInfoColor, "tport.command.backup.auto.true");
            } else {
                stateAsMessage = formatTranslation(ColorType.badColor, varInfoColor, "tport.command.backup.auto.false");
            }
            sendInfoTranslation(player, "tport.command.backup.auto.getStateAndCount", stateAsMessage, count);
        } else if (args.length == 3) {
            Files tportConfig = getFile("TPortConfig");
            try {
                int newCount = Integer.parseInt(args[2]);
                tportConfig.getConfig().set("backup.auto.count", newCount);
                tportConfig.saveConfig();
                sendSuccessTranslation(player, "tport.command.backup.auto.setStateSucceeded", newCount);
            } catch (NumberFormatException nfe) {
                Boolean newState = Main.toBoolean(args[2]);
                if (newState == null) {
                    sendErrorTranslation(player, "tport.command.wrongUsage", "/tport backup auto [true|false]");
                    return;
                }
                
                tportConfig.getConfig().set("backup.auto.state", newState);
                tportConfig.saveConfig();
                sendSuccessTranslation(player, "tport.command.backup.auto.setCountSucceeded", newState);
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport backup auto [state|count]");
        }
    }
}
