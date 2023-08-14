package com.spaceman.tport.fancyMessage.encapsulation;

import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fancyMessage.events.HoverEvent;

import static com.spaceman.tport.fancyMessage.events.HoverEvent.hoverEvent;

public class FeatureEncapsulation extends Encapsulation {
    
    private final String feature;
    
    public FeatureEncapsulation(String biome) {
        this.feature = biome;
    }
    
    @Override
    public String asString() {
        return feature;
    }
    
    @Override
    public HoverEvent getHoverEvent() {
        return hoverEvent("/tport featureTP search " + feature, ColorTheme.ColorType.infoColor);
    }
    
    @Override
    public ClickEvent getClickEvent() {
        return ClickEvent.runCommand("/tport featureTP search " + feature);
    }
    
    @Override
    public String getInsertion() {
        return feature;
    }
}
