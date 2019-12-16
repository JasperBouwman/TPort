package com.spaceman.tport.commands.tport.backup;

import com.spaceman.tport.Main;
import com.spaceman.tport.colorFormatter.ColorTheme;
import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fileHander.Files;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.spaceman.tport.colorFormatter.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.colorFormatter.ColorTheme.sendSuccessTheme;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;

public class Auto extends SubCommand {
    
    public Auto() {
        EmptyCommand emptyCount = new EmptyCommand();
        emptyCount.setCommandName("count", ArgumentType.OPTIONAL);
        emptyCount.setCommandDescription(textComponent("This command is used set the count of auto saved files", ColorTheme.ColorType.infoColor),
                textComponent("\n\nPermission: ", ColorTheme.ColorType.infoColor), textComponent("TPort.admin.backup.auto", ColorTheme.ColorType.varInfoColor));
        
        EmptyCommand emptyState = new EmptyCommand();
        emptyState.setCommandName("state", ArgumentType.OPTIONAL);
        emptyState.setCommandDescription(textComponent("This command is used to enable/disable the auto save function", ColorTheme.ColorType.infoColor),
                textComponent("\n\nPermission: ", ColorTheme.ColorType.infoColor), textComponent("TPort.admin.backup.auto", ColorTheme.ColorType.varInfoColor));
        
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
                        files.get(i).delete();
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
                        Bukkit.getLogger().info("[TPort] auto backup " + name + " succeeded");
                        return;
                    }
                    version++;
                } catch (IOException e) {
                    e.printStackTrace();
                    Bukkit.getLogger().warning("[TPort] Could not auto backup file " + name + ": " + e.getMessage());
                    return;
                }
            }
        }
    }
    
    @Override
    public Message getCommandDescription() {
        return new Message(textComponent("This command is used to get the data of the auto save function. " +
                "When the state is true TPort will backup on reload/shutdown. " +
                "The count says how many auto saved files will exist", ColorTheme.ColorType.infoColor));
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return hasPermission(player, false, "TPort.admin.backup.auto") ? Arrays.asList("true", "false", "<count>") : Collections.emptyList();
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport backup auto [state|count]
    
        if (!hasPermission(player, true, "TPort.admin.backup.auto")) {
            return;
        }
        
        if (args.length == 2) {
            Files tportConfig = getFile("TPortConfig");
            
            Message message = new Message();
            message.addText(textComponent("Auto save state is: ", ColorTheme.ColorType.infoColor));
            boolean state = tportConfig.getConfig().getBoolean("backup.auto.state", false);
            if (state) {
                message.addText(textComponent("true\n", ChatColor.GREEN));
            } else {
                message.addText(textComponent("false\n", ChatColor.RED));
            }
            int count = tportConfig.getConfig().getInt("backup.auto.count", 10);
            message.addText(textComponent("Count of auto saved files is: ", ColorTheme.ColorType.infoColor));
            message.addText(textComponent(String.valueOf(count), ColorTheme.ColorType.varInfoColor));
            message.sendMessage(player);
        } else if (args.length == 3) {
            Files tportConfig = getFile("TPortConfig");
            try {
                int newCount = Integer.parseInt(args[2]);
                tportConfig.getConfig().set("backup.auto.count", newCount);
                tportConfig.saveConfig();
                sendSuccessTheme(player, "Successfully set backup count to %s", String.valueOf(newCount));
            } catch (NumberFormatException nfe) {
                boolean newState = Boolean.parseBoolean(args[2]);
                tportConfig.getConfig().set("backup.auto.state", newState);
                tportConfig.saveConfig();
                sendSuccessTheme(player, "Successfully set backup state to %s", String.valueOf(newState));
            }
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport backup auto [state]");
        }
    }
}
