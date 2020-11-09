package com.spaceman.tport.fancyMessage.colorTheme;

import com.spaceman.tport.fileHander.Files;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
public class ColorTheme implements ConfigurationSerializable {
    
    public static HashMap<String, ColorTheme> defaultThemes = new HashMap<>();
    private static HashMap<UUID, ColorTheme> colorThemeMap = new HashMap<>();
    
    static {
        defaultThemes.put("fallTheme", new ColorTheme(new Color(251, 246, 1), new Color(251, 205, 38), new Color(251, 205, 38), new Color(143, 187, 9), new Color(214, 231, 21), new Color(214, 231, 21), new Color(246, 77, 13), new Color(250, 142, 4), new Color(250, 142, 4)));
        defaultThemes.put("winterTheme", new ColorTheme(new Color(35, 119, 164), new Color(80, 163, 198), new Color(80, 163, 198), new Color(121, 192, 215), new Color(248, 248, 248), new Color(248, 248, 248), new Color(134, 124, 124), new Color(221, 223, 223), new Color(221, 223, 223)));
        defaultThemes.put("springTheme", new ColorTheme(new Color(185, 212, 98), new Color(255, 236, 46), new Color(255, 236, 46), new Color(55, 139, 27), new Color(127, 178, 48), new Color(127, 178, 48), new Color(237, 63, 65), new Color(251, 106, 75), new Color(251, 106, 75)));
        defaultThemes.put("summerTheme", new ColorTheme(new Color(22, 121, 151), new Color(115, 189, 188), new Color(115, 189, 188), new Color(231, 145, 6), new Color(250, 183, 12), new Color(250, 183, 12), new Color(236, 32, 79), new Color(253, 103, 58), new Color(253, 103, 58)));
        defaultThemes.put("natureTheme", new ColorTheme(ChatColor.GREEN, ChatColor.DARK_GREEN, ChatColor.DARK_GREEN, ChatColor.GREEN, ChatColor.DARK_GREEN, ChatColor.DARK_GREEN, ChatColor.RED, ChatColor.DARK_RED, ChatColor.DARK_RED));
        defaultThemes.put("pigTheme", new ColorTheme(ChatColor.LIGHT_PURPLE, ChatColor.DARK_PURPLE, ChatColor.DARK_PURPLE, ChatColor.GREEN, ChatColor.DARK_GREEN, ChatColor.DARK_GREEN, ChatColor.RED, ChatColor.DARK_RED, ChatColor.DARK_RED));
        defaultThemes.put("sunTheme", new ColorTheme(ChatColor.YELLOW, ChatColor.GOLD, ChatColor.GOLD, ChatColor.GREEN, ChatColor.DARK_GREEN, ChatColor.DARK_GREEN, ChatColor.RED, ChatColor.DARK_RED, ChatColor.DARK_RED));
        defaultThemes.put("darkTheme", new ColorTheme(ChatColor.DARK_GRAY, ChatColor.GRAY, ChatColor.GRAY, ChatColor.DARK_GRAY, ChatColor.GRAY, ChatColor.GRAY, ChatColor.RED, ChatColor.DARK_RED, ChatColor.DARK_RED));
        defaultThemes.put("lightTheme", new ColorTheme(ChatColor.WHITE, ChatColor.GRAY, ChatColor.GRAY, ChatColor.WHITE, ChatColor.GRAY, ChatColor.GRAY, ChatColor.RED, ChatColor.DARK_RED, ChatColor.DARK_RED));
        defaultThemes.put("oceanTheme", new ColorTheme(ChatColor.DARK_AQUA, ChatColor.BLUE, ChatColor.DARK_BLUE, ChatColor.GREEN, ChatColor.DARK_GREEN, ChatColor.DARK_GREEN, ChatColor.RED, ChatColor.DARK_RED, ChatColor.DARK_RED));
    }
    
    private MultiColor infoColor;
    private MultiColor varInfoColor;
    private MultiColor varInfo2Color;
    private MultiColor successColor;
    private MultiColor varSuccessColor;
    private MultiColor varSuccess2Color;
    private MultiColor errorColor;
    private MultiColor varErrorColor;
    private MultiColor varError2Color;
    
    public ColorTheme() {
        if (!defaultThemes.isEmpty()) {
            for (ColorType type : ColorType.values()) {
                type.setColor(this, type.getColor(defaultThemes.values().toArray(new ColorTheme[0])[0]));
            }
        } else {
            for (ColorType type : ColorType.values()) {
                type.setColor(this, new MultiColor(ChatColor.DARK_RED));
            }
        }
    }
    
    public ColorTheme(ChatColor infoColor, ChatColor varInfoColor, ChatColor varInfo2Color,
                      ChatColor successColor, ChatColor varSuccessColor, ChatColor varSuccess2Color,
                      ChatColor errorColor, ChatColor varErrorColor, ChatColor varError2Color) {
        this.infoColor = new MultiColor(infoColor);
        this.varInfoColor = new MultiColor(varInfoColor);
        this.varInfo2Color = new MultiColor(varInfo2Color);
        this.successColor = new MultiColor(successColor);
        this.varSuccessColor = new MultiColor(varSuccessColor);
        this.varSuccess2Color = new MultiColor(varSuccess2Color);
        this.errorColor = new MultiColor(errorColor);
        this.varErrorColor = new MultiColor(varErrorColor);
        this.varError2Color = new MultiColor(varError2Color);
    }
    
    public ColorTheme(String infoColor, String varInfoColor, String varInfo2Color,
                      String successColor, String varSuccessColor, String varSuccess2Color,
                      String errorColor, String varErrorColor, String varError2Color) {
        this.infoColor = new MultiColor(infoColor);
        this.varInfoColor = new MultiColor(varInfoColor);
        this.varInfo2Color = new MultiColor(varInfo2Color);
        this.successColor = new MultiColor(successColor);
        this.varSuccessColor = new MultiColor(varSuccessColor);
        this.varSuccess2Color = new MultiColor(varSuccess2Color);
        this.errorColor = new MultiColor(errorColor);
        this.varErrorColor = new MultiColor(varErrorColor);
        this.varError2Color = new MultiColor(varError2Color);
    }
    
    public ColorTheme(Color infoColor, Color varInfoColor, Color varInfo2Color,
                      Color successColor, Color varSuccessColor, Color varSuccess2Color,
                      Color errorColor, Color varErrorColor, Color varError2Color) {
        this.infoColor = new MultiColor(infoColor);
        this.varInfoColor = new MultiColor(varInfoColor);
        this.varInfo2Color = new MultiColor(varInfo2Color);
        this.successColor = new MultiColor(successColor);
        this.varSuccessColor = new MultiColor(varSuccessColor);
        this.varSuccess2Color = new MultiColor(varSuccess2Color);
        this.errorColor = new MultiColor(errorColor);
        this.varErrorColor = new MultiColor(varErrorColor);
        this.varError2Color = new MultiColor(varError2Color);
    }
    
    public ColorTheme(MultiColor infoColor, MultiColor varInfoColor, MultiColor varInfo2Color,
                      MultiColor successColor, MultiColor varSuccessColor, MultiColor varSuccess2Color,
                      MultiColor errorColor, MultiColor varErrorColor, MultiColor varError2Color) {
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
    
    public static String formatInfo(ColorTheme colorTheme, String baseString, Object... args) {
        for (Object arg : args) {
            baseString = baseString.replaceFirst("%s", colorTheme.varInfoColor + arg.toString() + colorTheme.infoColor);
        }
        return colorTheme.infoColor + baseString;
    }
    public static String formatError(ColorTheme colorTheme, String baseString, Object... args) {
        for (Object arg : args) {
            baseString = baseString.replaceFirst("%s", colorTheme.varErrorColor + arg.toString() + colorTheme.errorColor);
        }
        return colorTheme.errorColor + baseString;
    }
    public static String formatSuccess(ColorTheme colorTheme, String baseString, Object... args) {
        for (Object arg : args) {
            baseString = baseString.replaceFirst("%s", colorTheme.varSuccessColor + arg.toString() + colorTheme.successColor);
        }
        return colorTheme.successColor + baseString;
    }
    public static String formatInfoTheme(Player player, String baseString, Object... args) {
        return formatInfo(getTheme(player), baseString, args);
    }
    public static String formatErrorTheme(Player player, String baseString, Object... args) {
        return formatError(getTheme(player), baseString, args);
    }
    public static String formatSuccessTheme(Player player, String baseString, Object... args) {
        return formatSuccess(getTheme(player), baseString, args);
    }
    public static void sendInfoTheme(Player player, String baseMessage, Object... args) {
        if (player != null) player.sendMessage(formatInfoTheme(player, baseMessage, args));
    }
    public static void sendErrorTheme(Player player, String baseMessage, Object... args) {
        if (player != null) player.sendMessage(formatErrorTheme(player, baseMessage, args));
    }
    public static void sendSuccessTheme(Player player, String baseMessage, Object... args) {
        if (player != null) player.sendMessage(formatSuccessTheme(player, baseMessage, args));
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
    
    @Nonnull
    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> map = new HashMap<>();
        for (ColorType type : ColorType.values()) {
            map.put(type.name(), type.getColor(this).getColorAsValue());
        }
        return map;
    }
    @SuppressWarnings("unused")
    public static ColorTheme deserialize(Map<String, Object> args) {
        try {
            ColorTheme theme = new ColorTheme();
            for (ColorType type : ColorType.values()) {
                if (args.containsKey(type.name())) {
                    type.setColor(theme, MultiColor.fromString((String) args.get(type.name())));
                }
            }
            return theme;
        } catch (IllegalArgumentException iae) {
            return new ColorTheme();
        }
    }
    
    public String formatInfo(String baseString, Object... args) {
        return formatInfo(this, baseString, args);
    }
    public String formatError(String baseString, Object... args) {
        return formatError(this, baseString, args);
    }
    public String formatSuccess(String baseString, Object... args) {
        return formatSuccess(this, baseString, args);
    }
    
    public MultiColor getInfoColor() {
        return infoColor;
    }
    public void setInfoColor(MultiColor infoColor) {
        this.infoColor = infoColor;
    }
    
    public MultiColor getVarInfoColor() {
        return varInfoColor;
    }
    public void setVarInfoColor(MultiColor varInfoColor) {
        this.varInfoColor = varInfoColor;
    }
    
    public MultiColor getVarInfo2Color() {
        return varInfo2Color;
    }
    public void setVarInfo2Color(MultiColor varInfo2Color) {
        this.varInfo2Color = varInfo2Color;
    }
    
    public MultiColor getSuccessColor() {
        return successColor;
    }
    public void setSuccessColor(MultiColor successColor) {
        this.successColor = successColor;
    }
    
    public MultiColor getVarSuccessColor() {
        return varSuccessColor;
    }
    public void setVarSuccessColor(MultiColor varSuccessColor) {
        this.varSuccessColor = varSuccessColor;
    }
    
    public MultiColor getVarSuccess2Color() {
        return varSuccess2Color;
    }
    public void setVarSuccess2Color(MultiColor varSuccess2Color) {
        this.varSuccess2Color = varSuccess2Color;
    }
    
    public MultiColor getErrorColor() {
        return errorColor;
    }
    public void setErrorColor(MultiColor errorColor) {
        this.errorColor = errorColor;
    }
    
    public MultiColor getVarErrorColor() {
        return varErrorColor;
    }
    public void setVarErrorColor(MultiColor varErrorColor) {
        this.varErrorColor = varErrorColor;
    }
    
    public MultiColor getVarError2Color() {
        return varError2Color;
    }
    public void setVarError2Color(MultiColor varError2Color) {
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
        
        private final ColorGetter colorGetter;
        private final ColorSetter colorSetter;
        
        ColorType(ColorGetter colorGetter, ColorSetter colorSetter) {
            this.colorGetter = colorGetter;
            this.colorSetter = colorSetter;
        }
        
        public static List<String> getTypes() {
            return Arrays.stream(ColorType.values()).map(Enum::name).collect(Collectors.toList());
        }
        
        public MultiColor getColor(ColorTheme theme) {
            return colorGetter.getColor(theme);
        }
        public MultiColor getColor(Player player) {
            return getColor(ColorTheme.getTheme(player));
        }
        
        public void setColor(ColorTheme theme, MultiColor color) {
            colorSetter.setColor(theme, color);
        }
        public void setColor(Player player, MultiColor color) {
            setColor(ColorTheme.getTheme(player), color);
        }
        public void setColor(UUID uuid, MultiColor color) {
            setColor(ColorTheme.getTheme(uuid), color);
        }
        
        @FunctionalInterface
        public interface ColorGetter {
            MultiColor getColor(ColorTheme theme);
        }
        
        @FunctionalInterface
        public interface ColorSetter {
            void setColor(ColorTheme theme, MultiColor color);
        }
    }
}
