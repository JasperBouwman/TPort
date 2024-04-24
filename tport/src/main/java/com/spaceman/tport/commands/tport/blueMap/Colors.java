package com.spaceman.tport.commands.tport.blueMap;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.webMaps.BlueMapHandler;
import org.bukkit.entity.Player;

import java.util.Collection;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;
import static com.spaceman.tport.fileHander.Files.tportConfig;

public class Colors extends SubCommand {
    private final EmptyCommand colorTheme;
    
    public Colors() {
        colorTheme = new EmptyCommand();
        colorTheme.setCommandName("color theme", ArgumentType.OPTIONAL);
        colorTheme.setCommandDescription(formatInfoTranslation("tport.command.blueMapCommand.colors.colorTheme.commandDescription"));
        colorTheme.setPermissions("TPort.blueMap.colors", "TPort.admin.blueMap");
        
        addAction(colorTheme);
        
        this.setCommandDescription(formatInfoTranslation("tport.command.blueMapCommand.colors.commandDescription"));
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return ColorTheme.getDefaultThemes();
    }
    
    public static String getBlueMapThemeName() {
        return tportConfig.getConfig().getString("blueMap.colors", "developerTheme");
    }
    public static ColorTheme getBlueMapTheme() {
        return ColorTheme.getDefaultTheme(getBlueMapThemeName());
    }
    public static void setBlueMapTheme(String theme) {
        if (ColorTheme.getDefaultThemes().contains(theme)) {
            tportConfig.getConfig().set("blueMap.colors", theme);
            tportConfig.saveConfig();
            try { BlueMapHandler.loadTPorts(); } catch (Exception ignored) { }
        }
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport blueMap colors [color theme]
        
        if (args.length == 2) {
            String blueMapThemeName = getBlueMapThemeName();
            sendInfoTranslation(player, "tport.command.blueMapCommand.colors.succeeded", blueMapThemeName);
        } else if (args.length == 3) {
            if (!colorTheme.hasPermissionToRun(player, true)) {
                return;
            }
            if (ColorTheme.getDefaultThemesMap().containsKey(args[2])) {
                setBlueMapTheme(args[2]);
                sendSuccessTranslation(player, "tport.command.blueMapCommand.colors.colorTheme.succeeded", args[2]);
            } else {
                sendErrorTranslation(player, "tport.command.blueMapCommand.colors.colorTheme.themeNotFound", args[2]);
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport blueMap colors [colorTheme]");
        }
    }
}
