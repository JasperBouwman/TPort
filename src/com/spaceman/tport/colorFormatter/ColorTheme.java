package com.spaceman.tport.colorFormatter;

import com.spaceman.tport.fileHander.Files;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings({"WeakerAccess", "unused", "UnusedReturnValue"})
public class ColorTheme implements ConfigurationSerializable {
    
    public static HashMap<String, ColorTheme> defaultThemes = new HashMap<>();
    private static HashMap<UUID, ColorTheme> colorThemeMap = new HashMap<>();
    
    static {
        defaultThemes.put("ocean", new ColorTheme(ChatColor.DARK_AQUA, ChatColor.BLUE, ChatColor.DARK_BLUE, ChatColor.GREEN, ChatColor.DARK_GREEN, ChatColor.DARK_GREEN, ChatColor.RED, ChatColor.DARK_RED, ChatColor.DARK_RED));
        defaultThemes.put("nature", new ColorTheme(ChatColor.GREEN, ChatColor.DARK_GREEN, ChatColor.DARK_GREEN, ChatColor.GREEN, ChatColor.DARK_GREEN, ChatColor.DARK_GREEN, ChatColor.RED, ChatColor.DARK_RED, ChatColor.DARK_RED));
        defaultThemes.put("pig", new ColorTheme(ChatColor.LIGHT_PURPLE, ChatColor.DARK_PURPLE, ChatColor.DARK_PURPLE, ChatColor.GREEN, ChatColor.DARK_GREEN, ChatColor.DARK_GREEN, ChatColor.RED, ChatColor.DARK_RED, ChatColor.DARK_RED));
        defaultThemes.put("sun", new ColorTheme(ChatColor.YELLOW, ChatColor.GOLD, ChatColor.GOLD, ChatColor.GREEN, ChatColor.DARK_GREEN, ChatColor.DARK_GREEN, ChatColor.RED, ChatColor.DARK_RED, ChatColor.DARK_RED));
        defaultThemes.put("dark", new ColorTheme(ChatColor.DARK_GRAY, ChatColor.GRAY, ChatColor.GRAY, ChatColor.DARK_GRAY, ChatColor.GRAY, ChatColor.GRAY, ChatColor.RED, ChatColor.DARK_RED, ChatColor.DARK_RED));
        defaultThemes.put("light", new ColorTheme(ChatColor.WHITE, ChatColor.GRAY, ChatColor.GRAY, ChatColor.WHITE, ChatColor.GRAY, ChatColor.GRAY, ChatColor.RED, ChatColor.DARK_RED, ChatColor.DARK_RED));
    }
    
    private ChatColor infoColor;
    private ChatColor varInfoColor;
    private ChatColor varInfo2Color;
    private ChatColor successColor;
    private ChatColor varSuccessColor;
    private ChatColor varSuccess2Color;
    private ChatColor errorColor;
    private ChatColor varErrorColor;
    private ChatColor varError2Color;
    
    public ColorTheme() {
        if (!defaultThemes.isEmpty()) {
            for (ColorType type : ColorType.values()) {
                type.setColor(this, type.getColor(defaultThemes.values().toArray(new ColorTheme[0])[0]));
            }
        } else {
            for (ColorType type : ColorType.values()) {
                type.setColor(this, ChatColor.DARK_RED);
            }
        }
    }
    
    public ColorTheme(ChatColor infoColor, ChatColor varInfoColor, ChatColor varInfo2Color,
                      ChatColor successColor, ChatColor varSuccessColor, ChatColor varSuccess2Color,
                      ChatColor errorColor, ChatColor varErrorColor, ChatColor varError2Color) {
        this.infoColor = infoColor;
        this.varInfoColor = varInfoColor;
        this.varInfo2Color = varInfo2Color;
        this.successColor = successColor;
        this.varSuccessColor = varSuccessColor;
        this.varSuccess2Color = varSuccess2Color;
        this.errorColor = errorColor;
        this.varErrorColor = varErrorColor;
        this.varError2Color = varError2Color;
    }
    
    public static void saveThemes(Files file) {
        for (UUID uuid : colorThemeMap.keySet()) {
            file.getConfig().set("colorTheme." + uuid, colorThemeMap.get(uuid));
        }
        file.saveConfig();
    }
    
    public static void loadThemes(Files file) {
        colorThemeMap = new HashMap<>();
        for (String uuid : file.getKeys("colorTheme")) {
            colorThemeMap.put(UUID.fromString(uuid), (ColorTheme) file.getConfig().get("colorTheme." + uuid));
        }
    }
    
    @SuppressWarnings("unused")
    public static ColorTheme deserialize(Map<String, Object> args) {
        try {
            ColorTheme theme = new ColorTheme();
            for (ColorType type : ColorType.values()) {
                if (args.containsKey(type.name())) {
                    type.setColor(theme, ChatColor.valueOf((String) args.get(type.name())));
                }
            }
            return theme;
        } catch (IllegalArgumentException iae) {
            return new ColorTheme();
        }
    }
    
    public static String formatInfo(ColorTheme colorTheme, String baseString, String... args) {
        for (String arg : args) {
            baseString = baseString.replaceFirst("%s", colorTheme.varInfoColor + arg + colorTheme.infoColor);
        }
        return colorTheme.infoColor + baseString;
    }
    
    public static String formatError(ColorTheme colorTheme, String baseString, String... args) {
        for (String arg : args) {
            baseString = baseString.replaceFirst("%s", colorTheme.varErrorColor + arg + colorTheme.errorColor);
        }
        return colorTheme.errorColor + baseString;
    }
    
    public static String formatSuccess(ColorTheme colorTheme, String baseString, String... args) {
        for (String arg : args) {
            baseString = baseString.replaceFirst("%s", colorTheme.varSuccessColor + arg + colorTheme.successColor);
        }
        return colorTheme.successColor + baseString;
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
    
    public static ColorTheme getTheme(Player player) {
        return getTheme(player.getUniqueId());
    }
    
    public static ColorTheme getTheme(UUID uuid) {
        if (!colorThemeMap.containsKey(uuid)) {
            setTheme(uuid, new ColorTheme());
        }
        return colorThemeMap.get(uuid);
    }
    
    public static void setTheme(Player player, ColorTheme theme) {
        setTheme(player.getUniqueId(), theme);
    }
    
    public static void setTheme(UUID uuid, ColorTheme theme) {
        colorThemeMap.put(uuid, new ColorTheme()); //reset theme
        Arrays.stream(ColorType.values()).forEach((type) -> type.setColor(uuid, type.getColor(theme))); //set all colors in new theme
    }
    
    //return true if theme is found
    public static boolean setDefaultTheme(Player player, String theme) {
        if (defaultThemes.containsKey(theme)) {
            setTheme(player, defaultThemes.get(theme));
            return false;
        }
        return false;
    }
    
    public static ArrayList<String> getDefaultThemes() {
        return new ArrayList<>(defaultThemes.keySet());
    }
    
    public static ColorTheme getDefaultTheme(String name) {
        return defaultThemes.getOrDefault(name, defaultThemes.values().iterator().next());
    }
    
    public static ColorTheme removeDefaultTheme(String theme) {
        return defaultThemes.remove(theme);
    }
    
    public static boolean addDefaultTheme(String themeName, ColorTheme theme) {
        if (defaultThemes.containsKey(themeName)) {
            defaultThemes.put(themeName, theme);
            return true;
        }
        return false;
    }
    
    @SuppressWarnings("NullableProblems")
    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> map = new HashMap<>();
        for (ColorType type : ColorType.values()) {
            map.put(type.name(), type.getColor(this).name());
        }
        return map;
    }
    
    public String formatInfo(String baseString, String... args) {
        return formatInfo(this, baseString, args);
    }
    
    public String formatError(String baseString, String... args) {
        return formatError(this, baseString, args);
    }
    
    public String formatSuccess(String baseString, String... args) {
        return formatSuccess(this, baseString, args);
    }
    
    public ChatColor getInfoColor() {
        return infoColor;
    }
    
    public void setInfoColor(ChatColor infoColor) {
        this.infoColor = infoColor;
    }
    
    public ChatColor getVarInfoColor() {
        return varInfoColor;
    }
    
    public void setVarInfoColor(ChatColor varInfoColor) {
        this.varInfoColor = varInfoColor;
    }
    
    public ChatColor getVarInfo2Color() {
        return varInfo2Color;
    }
    
    public void setVarInfo2Color(ChatColor varInfo2Color) {
        this.varInfo2Color = varInfo2Color;
    }
    
    public ChatColor getSuccessColor() {
        return successColor;
    }
    
    public void setSuccessColor(ChatColor successColor) {
        this.successColor = successColor;
    }
    
    public ChatColor getVarSuccessColor() {
        return varSuccessColor;
    }
    
    public void setVarSuccessColor(ChatColor varSuccessColor) {
        this.varSuccessColor = varSuccessColor;
    }
    
    public ChatColor getVarSuccess2Color() {
        return varSuccess2Color;
    }
    
    public void setVarSuccess2Color(ChatColor varSuccess2Color) {
        this.varSuccess2Color = varSuccess2Color;
    }
    
    public ChatColor getErrorColor() {
        return errorColor;
    }
    
    public void setErrorColor(ChatColor errorColor) {
        this.errorColor = errorColor;
    }
    
    public ChatColor getVarErrorColor() {
        return varErrorColor;
    }
    
    public void setVarErrorColor(ChatColor varErrorColor) {
        this.varErrorColor = varErrorColor;
    }
    
    public ChatColor getVarError2Color() {
        return varError2Color;
    }
    
    public void setVarError2Color(ChatColor varError2Color) {
        this.varError2Color = varError2Color;
    }
    
    public enum ColorType {
        infoColor(ColorTheme::getInfoColor, ColorTheme::setInfoColor),
        varInfoColor(ColorTheme::getVarInfoColor, ColorTheme::setVarInfoColor),
        varInfo2Color(ColorTheme::getVarInfo2Color, ColorTheme::setVarInfo2Color),
        successColor(ColorTheme::getSuccessColor, ColorTheme::setSuccessColor),
        varSuccessColor(ColorTheme::getVarSuccessColor, ColorTheme::setVarSuccessColor),
        varSuccess2Color(ColorTheme::getVarSuccess2Color, ColorTheme::setVarSuccess2Color),
        errorColor(ColorTheme::getErrorColor, ColorTheme::setErrorColor),
        varErrorColor(ColorTheme::getVarErrorColor, ColorTheme::setVarErrorColor),
        varError2Color(ColorTheme::getVarError2Color, ColorTheme::setVarError2Color);
        
        private ColorGetter colorGetter;
        private ColorSetter colorSetter;
        
        ColorType(ColorGetter colorGetter, ColorSetter colorSetter) {
            this.colorGetter = colorGetter;
            this.colorSetter = colorSetter;
        }
        
        public static List<String> getTypes() {
            return Arrays.stream(ColorType.values()).map(Enum::name).collect(Collectors.toList());
        }
    
        public ChatColor getColor(ColorTheme theme) {
            return colorGetter.getColor(theme);
        }
        public ChatColor getColor(Player player) {
            return getColor(ColorTheme.getTheme(player));
        }
    
        public void setColor(ColorTheme theme, ChatColor color) {
            colorSetter.setColor(theme, color);
        }
        public void setColor(Player player, ChatColor color) {
            setColor(ColorTheme.getTheme(player), color);
        }
        public void setColor(UUID uuid, ChatColor color) {
            setColor(ColorTheme.getTheme(uuid), color);
        }
        
        @FunctionalInterface
        public interface ColorGetter {
            ChatColor getColor(ColorTheme theme);
        }
        
        @FunctionalInterface
        public interface ColorSetter {
            void setColor(ColorTheme theme, ChatColor color);
        }
    }
}
