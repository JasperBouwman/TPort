package com.spaceman.tport.colorFormatter;

import com.spaceman.tport.fancyMessage.colorTheme.MultiColor;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Map;

public class ColorTheme implements ConfigurationSerializable {
    
    @SuppressWarnings("unused")
    public static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme deserialize(Map<String, Object> args) {
        try {
            com.spaceman.tport.fancyMessage.colorTheme.ColorTheme theme = new com.spaceman.tport.fancyMessage.colorTheme.ColorTheme();
            for (com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType type : com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.values()) {
                if (args.containsKey(type.name())) {
                    type.setColor(theme, new MultiColor(ChatColor.valueOf((String) args.get(type.name()))));
                }
            }
            return theme;
        } catch (IllegalArgumentException iae) {
            return new com.spaceman.tport.fancyMessage.colorTheme.ColorTheme();
        }
    }
    
    @Override
    public Map<String, Object> serialize() {
        return null;
    }
}
