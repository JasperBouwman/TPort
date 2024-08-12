package com.spaceman.tport.fancyMessage.encapsulation;

import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fancyMessage.events.HoverEvent;

import javax.annotation.Nonnull;

import static com.spaceman.tport.fancyMessage.events.HoverEvent.hoverEvent;

public class FeatureEncapsulation implements Encapsulation {
    
    protected final String feature;
    
    public FeatureEncapsulation(String feature) {
        this.feature = feature;
    }
    
    @Override
    public String asString() {
        return feature;
    }
    
    @Nonnull
    @Override
    public Message toMessage(String color, String varColor) {
        return new Message(new TextComponent(asString(), varColor));
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
