package com.spaceman.tport.fancyMessage.encapsulation;

import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fancyMessage.TextType;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fancyMessage.events.HoverEvent;

import javax.annotation.Nonnull;

import static com.spaceman.tport.fancyMessage.events.HoverEvent.hoverEvent;

public class BiomeEncapsulation implements Encapsulation {
    
    protected final String biome;
    
    public BiomeEncapsulation(String biome) {
        this.biome = biome.toLowerCase();
    }
    
    @Override
    public String asString() {
        return biome;
    }
    
    @Nonnull
    public Message toMessage(String color, String varColor) {
        if (biome.equals("random")) {
            return new Message(new TextComponent("biome.minecraft.random", varColor).setType(TextType.TRANSLATE));
        } else {
            return new Message(new TextComponent("biome.minecraft." + biome, varColor).setType(TextType.TRANSLATE).ignoreTranslator(true));
        }
    }
    
    @Override
    public HoverEvent getHoverEvent() {
        if (biome.equals("random")) {
            return hoverEvent("/tport biomeTP random", ColorTheme.ColorType.infoColor);
        } else {
            return hoverEvent("/tport biomeTP whitelist " + biome, ColorTheme.ColorType.infoColor);
        }
    }
    
    @Override
    public ClickEvent getClickEvent() {
        if (biome.equals("random")) {
            return ClickEvent.runCommand("/tport biomeTP random");
        } else {
            return ClickEvent.runCommand("/tport biomeTP whitelist " + biome);
        }
    }
    
    @Override
    public String getInsertion() {
        return biome;
    }
}
