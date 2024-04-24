package com.spaceman.tport.fileHander;

import com.spaceman.tport.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;

public class Files {
    
    public static Files tportData;
    public static Files tportConfig;
    
    public static void reloadFiles() {
        tportData   = new Files(Main.getInstance(), "TPortData.yml");
        tportConfig = new Files(Main.getInstance(), "TPortConfig.yml");
    }
    
    private final JavaPlugin plugin;

    private final File configFile;
    private FileConfiguration fileConfiguration = null;

    public Files(JavaPlugin plugin, String fileName) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), fileName + (fileName.toLowerCase().endsWith(".yml") ? "" : ".yml"));
    }

    public Files(JavaPlugin plugin, String extraPath, String fileName) {
        this.plugin = plugin;
        File f = new File(plugin.getDataFolder(), extraPath);
        this.configFile = new File(f, fileName + (fileName.toLowerCase().endsWith(".yml") ? "" : ".yml"));
    }

    private void reloadConfig() {
        fileConfiguration = YamlConfiguration.loadConfiguration(configFile);
    }

    public FileConfiguration getConfig() {
        if (fileConfiguration == null) {
            this.reloadConfig();
        }
        return fileConfiguration;
    }
    
    public Collection<String> getKeys(String path) {
        if (getConfig().contains(path)) {
            return getConfig().getConfigurationSection(path).getKeys(false);
        } else {
            return Collections.emptyList();
        }
    }

    public void saveConfig() {
        if (fileConfiguration != null && configFile != null) {
            try {
                getConfig().save(configFile);

            } catch (IOException ex) {
                plugin.getLogger().log(Level.SEVERE, "Could not save config to " + configFile, ex);
            }
        } else {
            plugin.getLogger().log(Level.SEVERE, "Could not save config to " + configFile);
        }
    }

}
