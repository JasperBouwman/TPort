package com.spaceman.tport.commands.tport.dynmap;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.webMaps.DynmapHandler;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import org.bukkit.entity.Player;

import java.util.Collection;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fileHander.Files.tportConfig;

public class Colors extends SubCommand {
    private final EmptyCommand colorTheme;
    
    public Colors() {
        colorTheme = new EmptyCommand();
        colorTheme.setCommandName("color theme", ArgumentType.OPTIONAL);
        colorTheme.setCommandDescription(formatInfoTranslation("tport.command.dynmapCommand.colors.colorTheme.commandDescription"));
        colorTheme.setPermissions("TPort.dynmap.colors", "TPort.admin.dynmap");
        
        addAction(colorTheme);
        
        this.setCommandDescription(formatInfoTranslation("tport.command.dynmapCommand.colors.commandDescription"));
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return ColorTheme.getDefaultThemes();
    }
    
    public static String getDynmapThemeName() {
        return tportConfig.getConfig().getString("dynmap.colors", "developerTheme");
    }
    public static ColorTheme getDynmapTheme() {
        return ColorTheme.getDefaultTheme(getDynmapThemeName());
    }
    public static void setDynmapTheme(String theme) {
        if (ColorTheme.getDefaultThemes().contains(theme)) {
            tportConfig.getConfig().set("dynmap.colors", theme);
            tportConfig.saveConfig();
            DynmapHandler.updateAllTPorts();
        }
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport dynmap colors [color theme]
        
        if (args.length == 2) {
            String dynmapThemeName = getDynmapThemeName();
            sendInfoTranslation(player, "tport.command.dynmapCommand.colors.succeeded", dynmapThemeName);
        } else if (args.length == 3) {
            if (!colorTheme.hasPermissionToRun(player, true)) {
                return;
            }
            if (ColorTheme.getDefaultThemesMap().containsKey(args[2])) {
                setDynmapTheme(args[2]);
                sendSuccessTranslation(player, "tport.command.dynmapCommand.colors.colorTheme.succeeded", args[2]);
            } else {
                sendErrorTranslation(player, "tport.command.dynmapCommand.colors.colorTheme.themeNotFound", args[2]);
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport dynmap colors [colorTheme]");
        }
    }
}
