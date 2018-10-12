package com.spaceman.tport.commands.tport;

import com.spaceman.tport.Main;
import com.spaceman.tport.commands.CmdHandler;
import com.spaceman.tport.fileHander.GettingFiles;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

public class Reload extends CmdHandler {

    @Override
    public void run(String[] args, Player player) {
        //tport reload

        if (!new File(GettingFiles.main.getDataFolder(), "TPortConfig.yml").exists()) {
            InputStream inputStream = GettingFiles.main.getResource("TPortConfig.yml");
            try {
                byte[] buffer = new byte[inputStream.available()];
                inputStream.read(buffer);
                new FileOutputStream(new File(GettingFiles.main.getDataFolder(), "TPortConfig.yml")).write(buffer);
                Bukkit.getLogger().log(Level.INFO, "[TPort] TPortConfig.yml did not exist, resetting it...");
            } catch (IOException ignore) {
            }
        }

        new GettingFiles(GettingFiles.main);
        Main.Cooldown.loopCooldown = false;

        player.sendMessage(ChatColor.DARK_AQUA + "TPort is reloaded");
    }
}
