package com.spaceman.tport.commands.tport;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.spaceman.tport.Main;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
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
    
    //return true if current version is the latest version
    //return false if current version is not the latest version
    //return null if getLatestVersionName could not check for latest version
    public static Boolean isLatestVersion() {
        String latestVersionName = getLatestVersionName();
        if (latestVersionName == null) {
            //could not check the latest version
            return null;
        }
        String latestVersion  = latestVersionName.substring(6);
        String currentVersion = Main.getInstance().getDescription().getVersion();
        
        //latest version is always higher or the same then the current version
        return latestVersion.equals(currentVersion);
    }
    
    public static void checkForLatestVersion() {
        Boolean isLatest = isLatestVersion();
        
        if (isLatest == null) {
            Main.getInstance().getLogger().log(Level.INFO, "Could not check for the latest version");
            return;
        }
        if (isLatest) {
            Main.getInstance().getLogger().log(Level.INFO, "Your are running the latest version of TPort");
            return;
        }
        
        String githubLink = "https://github.com/JasperBouwman/TPort/releases/tag/" + latestVersionName;
        String bukkitLink = "https://dev.bukkit.org/projects/tport/files";
        
        Main.getInstance().getLogger().log(Level.INFO, "There is a new version of TPort available. Download TPort here:");
        Main.getInstance().getLogger().log(Level.INFO, githubLink);
        Main.getInstance().getLogger().log(Level.INFO, "  or here:");
        Main.getInstance().getLogger().log(Level.INFO, bukkitLink);
        Main.getInstance().getLogger().log(Level.INFO, "Before downloading please check first for compatibility with your Minecraft/Bukkit version");
    }
    
    @Override
    public Message getCommandDescription() {
        return formatInfoTranslation("tport.command.version.commandDescription", Main.getInstance().getDescription().getVersion());
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport version
        
        if (args.length == 1) {
            
            String translationID;
            //noinspection ConstantConditions
            if (Main.supportedVersions.length == 1) translationID = "tport.command.version.succeeded.singular";
            else translationID = "tport.command.version.succeeded.multiple";
            
            Message versionsMessage = new Message();
            for (String version : Main.supportedVersions) {
                versionsMessage.addText(textComponent(version, varInfoColor));
                versionsMessage.addText(textComponent(", ", infoColor));
            }
            versionsMessage.removeLast();
            
            sendInfoTranslation(player, translationID,
                    Main.getInstance().getDescription().getVersion(),
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
            
            Boolean isLatest = isLatestVersion();
            if (isLatest == null) {
                sendErrorTranslation(player, "tport.command.version.couldNotCheck");
            } else if (isLatest) {
                sendSuccessTranslation(player, "tport.command.version.isLatest");
            } else {
                sendInfoTranslation(player, "tport.command.version.isNotLatest", latestVersionName);
            }
            
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport version");
        }
    }
}
