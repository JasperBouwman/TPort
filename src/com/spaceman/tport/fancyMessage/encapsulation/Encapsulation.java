package com.spaceman.tport.fancyMessage.encapsulation;

import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fancyMessage.events.HoverEvent;

public abstract class Encapsulation {
    
    public abstract String asString();
    
    public TextComponent asText(String color) {
        return new TextComponent(asString(), color);
    }
    
    public HoverEvent getHoverEvent() {
        return null;
    }
    
    public ClickEvent getClickEvent() {
        return null;
    }
    
    public String getInsertion() {
        return asString();
    }
    
}
