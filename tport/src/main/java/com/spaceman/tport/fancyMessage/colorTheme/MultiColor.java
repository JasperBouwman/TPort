package com.spaceman.tport.fancyMessage.colorTheme;

import com.spaceman.tport.Main;
import com.spaceman.tport.Pair;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import javax.annotation.Nonnull;
import java.util.Map;

public class MultiColor implements ConfigurationSerializable {
    
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
    
    public MultiColor(String color) {
        this.setColor(color);
    }
    
    public MultiColor(ChatColor color) {
        this.setColor(color);
    }
    
    public MultiColor(Color color) {
        this.setColor(color);
    }
    
    public MultiColor(java.awt.Color color) {
        this.setColor(color);
    }
    
    public void setColor(String color) {
        if (color.matches("#[0-9a-fA-F]{6}")) {
            this.hexColor = color;
        } else if (color.matches("[0-9a-fA-F]{6}")) {
            this.hexColor = "#" + color;
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
