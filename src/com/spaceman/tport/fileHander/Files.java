package com.spaceman.tport.fileHander;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;

public class Files {

    private final JavaPlugin plugin;

    private File configFile;
    private FileConfiguration fileConfiguration;

    public Files(JavaPlugin plugin, String fileName) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), fileName + (fileName.toLowerCase().endsWith(".yml") ? "" : ".yml"));
    }

    public Files(JavaPlugin plugin, String extraPath, String fileName) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder() + extraPath, fileName + (fileName.toLowerCase().endsWith(".yml") ? "" : ".yml"));
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
    
    public Collection<String> getConfigurationSection(String path) {
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
