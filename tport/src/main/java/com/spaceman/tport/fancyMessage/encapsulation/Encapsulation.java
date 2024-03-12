package com.spaceman.tport.fancyMessage.encapsulation;

import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fancyMessage.events.HoverEvent;

public abstract class Encapsulation {
    
    public abstract String asString();
    
    public Message toMessage(String color, String varColor) {
        return new Message(new TextComponent(asString(), varColor));
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
