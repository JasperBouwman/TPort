package com.spaceman.tport.colorFormatter;

import com.spaceman.tport.fileHander.Files;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public enum ColorTheme {
    //&00&11&22&33&44&55&66&77&88&99&aa&bb&cc&dd&ee&ff
    
    OCEAN(ChatColor.DARK_AQUA, ChatColor.BLUE, ChatColor.DARK_BLUE),
    NATURE(ChatColor.GREEN, ChatColor.DARK_GREEN),
    DARK(ChatColor.DARK_GRAY, ChatColor.GRAY),
    PIG(ChatColor.LIGHT_PURPLE, ChatColor.DARK_PURPLE),
    SUN(ChatColor.YELLOW, ChatColor.GOLD);
    
    /*
     * DARK_AQUA - BLUE - DARK_BLUE
     * GREEN - DARK_GREEN
     * DARK_GRAY - GRAY
     * LIGHT_PURPLE - DARK_PURPLE
     * YELLOW - GOLD
     * */
    
    private static HashMap<UUID, ColorTheme> themes = new HashMap<>();
    
    public static void saveThemes(Files file) {
        for (UUID uuid : themes.keySet()) {
            file.getConfig().set("plugin.colorTheme." + uuid, themes.get(uuid).name());
        }
        file.saveConfig();
    }
    
    public static void loadThemes(Files file) {
        themes = new HashMap<>();
        for (String uuid : file.getConfigurationSection("plugin.colorTheme")) {
            themes.put(UUID.fromString(uuid), ColorTheme.valueOf(file.getConfig().getString("plugin.colorTheme." + uuid)));
        }
    }
    
    private ChatColor infoColor;
    private ChatColor varInfoColor;
    private ChatColor varInfo2Color;
    private ChatColor successColor = ChatColor.GREEN;
    private ChatColor varSuccessColor = ChatColor.DARK_GREEN;
    private ChatColor errColor = ChatColor.RED;
    private ChatColor varErrColor = ChatColor.DARK_RED;
    
    ColorTheme(ChatColor infoColor, ChatColor varColor) {
        this.infoColor = infoColor;
        this.varInfoColor = varColor;
        this.varInfo2Color = varColor;
    }
    ColorTheme(ChatColor infoColor, ChatColor varInfoColor, ChatColor varInfo2Color) {
        this.infoColor = infoColor;
        this.varInfoColor = varInfoColor;
        this.varInfo2Color = varInfo2Color;
    }
    
    public ChatColor getInfoColor() {
        return infoColor;
    }
    public ChatColor getVarInfoColor() {
        return varInfoColor;
    }
    public ChatColor getVarInfo2Color() {
        return varInfo2Color;
    }
    public ChatColor getSuccessColor() {
        return successColor;
    }
    public ChatColor getErrColor() {
        return errColor;
    }
    public ChatColor getVarSuccessColor() {
        return varSuccessColor;
    }
    public ChatColor getVarErrColor() {
        return varErrColor;
    }
    
    public String formatInfo(String baseString, String... args) {
        return formatInfo(this, baseString, args);
    }
    public String formatError(String baseString, String... args) {
        return formatError(this, baseString, args);
    }
    public String formatSucess(String baseString, String... args) {
        return formatSuccess(this, baseString, args);
    }
    
    public static ColorTheme getTheme(Player player) {
        return themes.getOrDefault(player.getUniqueId(), OCEAN);
    }
    public static void setTheme(Player player, ColorTheme colorTheme) {
        themes.put(player.getUniqueId(), colorTheme);
    }
    
    public static String formatInfo(ColorTheme colorTheme, String baseString, String... args) {
        baseString = colorTheme.infoColor + baseString.replace("%s", colorTheme.varInfoColor + "%s" + colorTheme.infoColor);
        for (String arg : args) {
            baseString = baseString.replace("%s", arg);
        }
        return baseString;
    }
    public static String formatError(ColorTheme colorTheme, String baseString, String... args) {
        baseString = colorTheme.errColor + baseString.replace("%s", colorTheme.varErrColor + "%s" + colorTheme.errColor);
        for (String arg : args) {
            baseString = baseString.replace("%s", arg);
        }
        return baseString;
    }
    public static String formatSuccess(ColorTheme colorTheme, String baseString, String... args) {
        baseString = colorTheme.successColor + baseString.replace("%s", colorTheme.varSuccessColor + "%s" + colorTheme.successColor);
        for (String arg : args) {
            baseString = baseString.replace("%s", arg);
        }
        return baseString;
    }
    
    public static String formatInfoTheme(Player player, String baseString, String... args) {
        return formatInfo(getTheme(player), baseString, args);
    }
    public static String formatErrorTheme(Player player, String baseString, String... args) {
        return formatError(getTheme(player), baseString, args);
    }
    public static String formatSuccessTheme(Player player, String baseString, String... args) {
        return formatSuccess(getTheme(player), baseString, args);
    }
    
    public static void sendInfoTheme(Player player, String baseMessage, String... args) {
        player.sendMessage(formatInfoTheme(player, baseMessage, args));
    }
    public static void sendErrorTheme(Player player, String baseMessage, String... args) {
        player.sendMessage(formatErrorTheme(player, baseMessage, args));
    }
    public static void sendSuccessTheme(Player player, String baseMessage, String... args) {
        player.sendMessage(formatSuccessTheme(player, baseMessage, args));
    }
}
