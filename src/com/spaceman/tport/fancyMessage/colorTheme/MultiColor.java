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
    
    public MultiColor(String hexColor) {
        this.setColor(hexColor);
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
    
    public void setColor(String hexColor) {
        if (hexColor.matches("#[0-9a-fA-F]{6}")) {
            this.hexColor = hexColor;
        } else if (hexColor.matches("[0-9a-fA-F]{6}")) {
            this.hexColor = "#" + hexColor;
        } else {
            this.hexColor = "#ffffff";
        }
    }
    
    public void setColor(ChatColor color) {
        switch (color) {
            case DARK_BLUE:
                this.hexColor = "#0000aa";
                break;
            case DARK_GREEN:
                this.hexColor = "#00aa00";
                break;
            case DARK_AQUA:
                this.hexColor = "#00aaaa";
                break;
            case DARK_RED:
                this.hexColor = "#aa0000";
                break;
            case DARK_PURPLE:
                this.hexColor = "#aa00aa";
                break;
            case GOLD:
                this.hexColor = "#ffaa00";
                break;
            case GRAY:
                this.hexColor = "#aaaaaa";
                break;
            case DARK_GRAY:
                this.hexColor = "#555555";
                break;
            case BLUE:
                this.hexColor = "#5555ff";
                break;
            case GREEN:
                this.hexColor = "#55ff55";
                break;
            case AQUA:
                this.hexColor = "#55ffff";
                break;
            case RED:
                this.hexColor = "#ff5555";
                break;
            case LIGHT_PURPLE:
                this.hexColor = "#ff55ff";
                break;
            case YELLOW:
                this.hexColor = "#ffff55";
                break;
            case WHITE:
                this.hexColor = "#ffffff";
                break;
            case BLACK:
            default:
                this.hexColor = "#000000";
        }
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
        return getStringColor();
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
