package com.spaceman.tport.commands.tport;

import com.spaceman.tport.Main;
import com.spaceman.tport.Permissions;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.cooldown.CooldownManager;
import com.spaceman.tport.fileHander.GettingFiles;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.logging.Level;

import static com.spaceman.tport.Permissions.loadPermissionConfig;

public class Reload extends SubCommand {

    @Override
    public void run(String[] args, Player player) {
        //tport reload

        if (!Permissions.hasPermission(player, "TPort.admin.reload")) {
            return;
        }

        reloadTPort();

        player.sendMessage(ChatColor.DARK_AQUA + "TPort is reloaded");
    }

    public static void reloadTPort() {

        for (String file : Arrays.asList("TPortConfig.yml", "Permissions.txt")) {
            if (!new File(Main.getInstance().getDataFolder(), file).exists()) {
                InputStream inputStream = Main.getInstance().getResource(file);
                try {
                    byte[] buffer = new byte[inputStream.available()];
                    inputStream.read(buffer);
                    new FileOutputStream(new File(Main.getInstance().getDataFolder(), file)).write(buffer);
                    Bukkit.getLogger().log(Level.INFO, "[TPort] " + file + " did not exist, resetting it...");
                } catch (IOException ignore) {
                }
            }
        }

        GettingFiles.loadFiles();
        CooldownManager.loopCooldown = false;
        loadPermissionConfig();
    }
}
