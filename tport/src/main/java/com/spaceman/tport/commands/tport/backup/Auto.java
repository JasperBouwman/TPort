package com.spaceman.tport.commands.tport.backup;

import com.spaceman.tport.Main;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.Features;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fileHander.Files;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfoColor;
import static com.spaceman.tport.fileHander.Files.tportConfig;
import static com.spaceman.tport.fileHander.Files.tportData;

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
        
        setCommandDescription(formatInfoTranslation("tport.command.backup.auto.commandDescription"));
    }
    
    public static String getBackupName() {
        return new SimpleDateFormat("dd-MMM-yyyy-hh_mm_ss").format(Calendar.getInstance().getTime());
    }
    private static void removeRedundant() {
        File dir = new File(Main.getInstance().getDataFolder(), "backup");
        if (!dir.mkdir()) {
            ArrayList<File> files = new ArrayList<>();
            for (File f : dir.listFiles()) {
                if (f.getName().startsWith("auto-")) {
                    files.add(f);
                }
            }
            files.sort(Comparator.comparingLong(File::lastModified));
            int count = getBackupCount();
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
    }
    
    public static void save() {
        if (getBackupState()) {
            removeRedundant();
            save("auto-" + getBackupName(), null);
        }
    }
    public static void save(String backupName, Player player) {
        if (getBackupState()) {
            
            if (Main.containsSpecialCharacter(backupName)) {
                sendErrorTranslation(player, "tport.command.backup.save.name.error.specialChars", "A-Z", "0-9", "-", "_");
                return;
            }
            
            int version = 0;
            while (true) {
                String suffix = "";
                if (version != 0) {
                    suffix = "(" + version + ")";
                }
                File file = new File(new File(Main.getInstance().getDataFolder(), "backup"), backupName + suffix + ".yml");
                try {
                    if (file.createNewFile()) {
                        Files configFile = new Files(Main.getInstance(), "backup", backupName + suffix + ".yml");
                        
                        configFile.getConfig().set("tport", tportData.getConfig().getConfigurationSection("tport"));
                        configFile.getConfig().set("public", tportData.getConfig().getConfigurationSection("public"));
                        configFile.saveConfig();
                        sendSuccessTranslation(player, "tport.command.backup.save.name.succeeded", file.getName());
                        
                        if (player == null) {
                            Main.getInstance().getLogger().info("Auto save backup " + backupName + " succeeded");
                        } else {
                            Main.getInstance().getLogger().info("Save backup " + backupName + " succeeded");
                        }
                        return;
                    }
                    version++;
                } catch (IOException e) {
                    if (Features.Feature.PrintErrorsInConsole.isEnabled()) e.printStackTrace();
                    if (player == null) {
                        Main.getInstance().getLogger().warning("Could not auto save backup file " + backupName + ": " + e.getMessage());
                    } else {
                        Main.getInstance().getLogger().warning("Could not save backup file " + backupName + ": " + e.getMessage());
                    }
                    sendErrorTranslation(player, "tport.command.backup.save.name.error", e.getMessage());
                    return;
                }
            }
        }
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return emptyCount.hasPermissionToRun(player, false) ? Arrays.asList("true", "false", "<count>") : Collections.emptyList();
    }
    
    public static int getBackupCount() {
        return tportConfig.getConfig().getInt("backup.auto.count", 10);
    }
    public static void setBackupCount(int count) {
        tportConfig.getConfig().set("backup.auto.count", count);
        tportConfig.saveConfig();
    }
    
    public static boolean getBackupState() {
        return tportConfig.getConfig().getBoolean("backup.auto.state", true);
    }
    public void setBackupState(boolean state) {
        tportConfig.getConfig().set("backup.auto.state", state);
        tportConfig.saveConfig();
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport backup auto [state|count]
        
        if (args.length == 2) {
            Message stateAsMessage;
            if (getBackupState()) {
                stateAsMessage = formatTranslation(ColorType.goodColor, varInfoColor, "tport.command.backup.auto.true");
            } else {
                stateAsMessage = formatTranslation(ColorType.badColor, varInfoColor, "tport.command.backup.auto.false");
            }
            
            int count = getBackupCount();
            sendInfoTranslation(player, "tport.command.backup.auto.getStateAndCount", stateAsMessage, count);
        } else if (args.length == 3) {
            if (!emptyCount.hasPermissionToRun(player, true)) {
                return;
            }
            try {
                int newCount = Integer.parseInt(args[2]);
                setBackupCount(newCount);
                sendSuccessTranslation(player, "tport.command.backup.auto.setCountSucceeded", newCount);
            } catch (NumberFormatException nfe) {
                Boolean newState = Main.toBoolean(args[2]);
                if (newState == null) {
                    sendErrorTranslation(player, "tport.command.wrongUsage", "/tport backup auto [true|false]");
                    return;
                }
                
                setBackupState(newState);
                sendSuccessTranslation(player, "tport.command.backup.auto.setStateSucceeded", newState);
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport backup auto [state|count]");
        }
    }
}
