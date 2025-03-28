package com.spaceman.tport.commands.tport;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.spaceman.tport.Main;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.infoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.events.HoverEvent.hoverEvent;

public class Version extends SubCommand {
    
    public static String latestVersionName = "";
    public static String latestVersionJAR = "";
    public static String getLatestVersionName() {
        try {
            String urlAsString = "https://api.github.com/repos/JasperBouwman/TPort/releases/latest";
            URL url = new URL(urlAsString);
            
            URLConnection connection = url.openConnection();
            connection.connect();
            
            JsonElement element = JsonParser.parseReader(new InputStreamReader((InputStream) connection.getContent()));
            JsonObject object = element.getAsJsonObject();
            
            for (JsonElement assetElement : object.getAsJsonArray("assets")) {
                JsonObject asset = assetElement.getAsJsonObject();
                
                String assetName = asset.get("name").toString();
                if (assetName.endsWith(".jar")) {
                    latestVersionJAR = asset.get("browser_download_url").toString();
                }
            }
            
            String versionName = object.get("tag_name").toString();
            versionName = versionName.substring(1, versionName.length() - 1);
            latestVersionName = versionName;
            
            return versionName;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static boolean isLatest(String[] latestVersion, String[] currentVersion, int index) {
        if (latestVersion.length == index && currentVersion.length == index) {
            return true; // if both have no sub version listed. this version equals latest version
        }
        
        int latest  = latestVersion.length  == index ? 0 : Integer.parseInt( latestVersion[index]);
        int current = currentVersion.length == index ? 0 : Integer.parseInt(currentVersion[index]);
        if (current > latest) { // if current version is greater than latest version, this version is running future version (dev build)
            return true;
        } else if (current == latest) { // if current version equals latest version, check sub version
            return isLatest(latestVersion, currentVersion, index + 1);
        } else { // current version is smaller than latest, this version is older
            return false;
        }
    }
    //return true if current version is the latest version
    //return false if current version is not the latest version
    //return null if getLatestVersionName could not check for latest version
    public static Boolean isLatestVersion() {
        String latestVersionName = getLatestVersionName();
        if (latestVersionName == null) {
            //could not check the latest version
            return null;
        }
        String[] latestVersion  = latestVersionName.substring(6).split("\\.");
        String[] currentVersion = getCurrentVersion().split("\\.");
        
        //latest version is always higher or the same then the current version
        return isLatest(latestVersion, currentVersion, 0);
    }
    
    public static void checkForLatestVersion() {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            Boolean isLatest = isLatestVersion();
            
            if (isLatest == null) {
                Main.getInstance().getLogger().log(Level.INFO, "Could not check for the latest version");
                return;
            }
            if (isLatest) {
                Main.getInstance().getLogger().log(Level.INFO, "You are running the latest version of TPort");
                return;
            }
            
            String githubLink = "https://github.com/JasperBouwman/TPort/releases/tag/" + latestVersionName;
            String bukkitLink = "https://dev.bukkit.org/projects/tport/files";
            
            Main.getInstance().getLogger().log(Level.INFO, "There is a new version of TPort available. Download TPort here:");
            Main.getInstance().getLogger().log(Level.INFO, githubLink);
            Main.getInstance().getLogger().log(Level.INFO, "  or here:");
            Main.getInstance().getLogger().log(Level.INFO, bukkitLink);
            Main.getInstance().getLogger().log(Level.INFO, "Before downloading please check first for compatibility with your Minecraft/Bukkit version");
        });
    }
    
    public static String getCurrentVersion() {
        return Main.getInstance().getDescription().getVersion();
    }
    
    public Version() {
        setCommandDescription(formatInfoTranslation("tport.command.version.commandDescription", getCurrentVersion()));
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport version
        
        if (args.length == 1) {
            
            int versions = Main.supportedVersions.length;
            
            String translationID;
            if (versions > 1) translationID = "tport.command.version.succeeded.multiple";
            else translationID = "tport.command.version.succeeded.singular";
            
            Message versionsMessage = new Message();
            if (versions != 0) {
                for (String version : Main.supportedVersions) {
                    versionsMessage.addText(textComponent(version, varInfoColor));
                    versionsMessage.addText(textComponent(", ", infoColor));
                }
                versionsMessage.removeLast();
            } else {
                versionsMessage = formatTranslation(varInfoColor, varInfoColor, "tport.command.version.succeeded.unknown");
            }
            
            sendInfoTranslation(player, translationID,
                    getCurrentVersion(),
                    versionsMessage,
                    textComponent(
                            "https://github.com/JasperBouwman/TPort",
                            varInfoColor,
                            hoverEvent(textComponent("https://github.com/JasperBouwman/TPort", varInfoColor)),
                            ClickEvent.openUrl("https://github.com/JasperBouwman/TPort")
                    ),
                    textComponent(
                            Main.getInstance().getDescription().getWebsite(),
                            varInfoColor,
                            hoverEvent(textComponent(Main.getInstance().getDescription().getWebsite(), varInfoColor)),
                            ClickEvent.openUrl(Main.getInstance().getDescription().getWebsite())
                    ),
                    textComponent(
                            Main.discordLink,
                            varInfoColor,
                            hoverEvent(textComponent(Main.discordLink, varInfoColor)),
                            ClickEvent.openUrl(Main.discordLink)
                    ));
            
            Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                Boolean isLatest = isLatestVersion();
                if (isLatest == null) {
                    sendErrorTranslation(player, "tport.command.version.couldNotCheck");
                } else if (isLatest) {
                    sendSuccessTranslation(player, "tport.command.version.isLatest");
                } else {
                    sendInfoTranslation(player, "tport.command.version.isNotLatest", latestVersionName);
                }
            });
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport version");
        }
    }
}
