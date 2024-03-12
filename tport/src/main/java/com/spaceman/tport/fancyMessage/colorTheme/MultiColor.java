package com.spaceman.tport.fancyMessage.colorTheme;

import com.spaceman.tport.Main;
import com.spaceman.tport.Pair;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class MultiColor implements ConfigurationSerializable, Serializable {
    
    private String hexColor;
    
    protected static MultiColor fromString(String color) {
        MultiColor multiColor = new MultiColor("");
        try {
            multiColor.setColor(ChatColor.valueOf(color.toUpperCase()));
            return multiColor;
        } catch (IllegalArgumentException ignore) {
        }
        
        multiColor.hexColor = color;
        return multiColor;
    }
    
    public static boolean isColor(String color) {
        return isHexColor(color) || isColorCode(color) || isRGBColor(color);
    }
    public static boolean isHexColor(String color) {
        return color.matches("#[0-9a-fA-F]{6}");
    }
    public static boolean isColorCode(String color) {
        return color.matches("&[0-9a-fA-F]");
    }
    public static boolean isRGBColor(String color) {
        if (color.length() < 6) return false;
        
        char[] charArray = color.toCharArray();
        if (charArray[0] == '$') { //find first dollar
            int redIndex = 0;                              //red dollar found
            int greenIndex = -1;
            int blueIndex = -1;
            int endIndex = -1;
            
            for (int j = 1; j < 5; j++) {                   //find green dollar
                if (redIndex + j >= charArray.length) break;
                char greenDollar = charArray[redIndex + j];
                if (greenDollar == '$') {
                    greenIndex = redIndex + j;
                    break;
                }
            }
            if (greenIndex == -1) return false;
            for (int j = 1; j < 5; j++) {                   //find blue dollar
                if (greenIndex + j >= charArray.length) break;
                char blueDollar = charArray[greenIndex + j];
                if (blueDollar == '$') {
                    blueIndex = greenIndex + j;
                    break;
                }
            }
            if (blueIndex == -1) return false;
            for (int j = 1; j < 5; j++) {                   //find end of blue
                if (blueIndex + j >= charArray.length) {
                    endIndex = blueIndex + j;
                    break;
                }
                char end = charArray[blueIndex + j];
                if (!String.valueOf(end).matches("\\d")) {
                    endIndex = blueIndex + j;
                    break;
                }
            }
            if (endIndex == -1) return false;
            
            String redString = color.substring(redIndex, greenIndex);
            String greenString = color.substring(greenIndex, blueIndex);
            String blueString = color.substring(blueIndex, endIndex);
            for (String colorString : List.of(redString, greenString, blueString)) {
                try {
                    int numericColor = Integer.parseInt(colorString.substring(1));
                    if (numericColor > 255) {
                        return false;
                    }
                } catch (NumberFormatException nfe) {
                    return false;
                }
            }
            return true;
        }
        
        return false;
    }
    
    public MultiColor(String color) {
        this.setColor(color);
    }
    
    public MultiColor(ChatColor color) {
        this.setColor(color);
    }
    
    public MultiColor(DyeColor color) {
        this.setColor(color);
    }
    
    public MultiColor(Color color) {
        this.setColor(color);
    }
    
    public MultiColor(java.awt.Color color) {
        this.setColor(color);
    }
    
    public void setColor(String color) {
        if (isHexColor(color)) {
            this.hexColor = color;
        } else if (color.matches("[0-9a-fA-F]{6}")) {
            this.hexColor = "#" + color;
        } else if (isColorCode(color)) {
            setColor(Main.getOrDefault(ChatColor.getByChar(color.charAt(1)), ChatColor.WHITE));
        } else if (isRGBColor(color)) {
            String[] s = color.split("\\$");
            setColor(new java.awt.Color(Integer.parseInt(s[1]), Integer.parseInt(s[2]), Integer.parseInt(s[3])));
        } else {
            try {
                ChatColor c = ChatColor.valueOf(color.toUpperCase());
                setColor(c);
            } catch (IllegalArgumentException iae) {
                this.hexColor = "#ffffff";
            }
        }
    }
    
    public void setColor(ChatColor color) {
        this.hexColor = switch (color) {
            case DARK_BLUE -> "#0000aa";
            case DARK_GREEN -> "#00aa00";
            case DARK_AQUA -> "#00aaaa";
            case DARK_RED -> "#aa0000";
            case DARK_PURPLE -> "#aa00aa";
            case GOLD -> "#ffaa00";
            case GRAY -> "#aaaaaa";
            case DARK_GRAY -> "#555555";
            case BLUE -> "#5555ff";
            case GREEN -> "#55ff55";
            case AQUA -> "#55ffff";
            case RED -> "#ff5555";
            case LIGHT_PURPLE -> "#ff55ff";
            case YELLOW -> "#ffff55";
            case WHITE -> "#ffffff";
            case BLACK -> "#000000";
            default -> "#000000";
        };
    }
    
    public void setColor(DyeColor color) {
        setColor(color.getColor());
    }
    
    public void setColor(Color color) {
        this.setColor(new java.awt.Color(color.asRGB()));
    }
    
    public void setColor(java.awt.Color color) {
        this.hexColor = "#" + Integer.toHexString(color.getRGB()).substring(2);
    }
    
    public String getColorAsValue() {
        return hexColor;
    }
    
    public java.awt.Color getColor() {
        return new java.awt.Color(Integer.parseInt(hexColor.substring(1, 7), 16));
    }
    
    public String getStringColor() {
        StringBuilder color = new StringBuilder("§x");
        for (char c : hexColor.substring(1).toCharArray()) {
            color.append('§').append(c);
        }
        return color.toString();
    }
    
    public int getRed() {
        return getColor().getRed();
    }
    
    public int getGreen() {
        return getColor().getGreen();
    }
    
    public int getBlue() {
        return getColor().getBlue();
    }
    
    @Override
    public String toString() {
        return getColorAsValue();
    }
    
    @Nonnull
    @Override
    public Map<String, Object> serialize() {
        return Main.asMap(new Pair<>("color", hexColor));
    }
    
    @SuppressWarnings("unused")
    public static MultiColor deserialize(Map<String, Object> args) {
        return new MultiColor((String) args.get("color"));
    }
}
