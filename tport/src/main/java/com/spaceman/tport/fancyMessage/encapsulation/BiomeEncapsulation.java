package com.spaceman.tport.fancyMessage.encapsulation;

import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fancyMessage.TextType;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fancyMessage.events.HoverEvent;

import static com.spaceman.tport.fancyMessage.events.HoverEvent.hoverEvent;

public class BiomeEncapsulation extends Encapsulation {
    
    private final String biome;
    
    public BiomeEncapsulation(String biome) {
        this.biome = biome;
    }
    
    @Override
    public String asString() {
        return biome;
    }
    
    public TextComponent asText(String color) {
        return new TextComponent("biome.minecraft." + biome.toLowerCase(), color).setType(TextType.TRANSLATE).ignoreTranslator(true);
    }
    
    @Override
    public HoverEvent getHoverEvent() {
        return hoverEvent("/tport biomeTP whitelist " + biome, ColorTheme.ColorType.infoColor);
    }
    
    @Override
    public ClickEvent getClickEvent() {
        return ClickEvent.runCommand("/tport biomeTP whitelist " + biome);
    }
    
}
