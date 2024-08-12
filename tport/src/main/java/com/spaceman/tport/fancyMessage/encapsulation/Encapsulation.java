package com.spaceman.tport.fancyMessage.encapsulation;

import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fancyMessage.events.HoverEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Encapsulation {
    
    String asString();
    
    @Nonnull
    Message toMessage(String color, String varColor);
//    {
//        return new Message(new TextComponent(asString(), varColor));
//    }
    
    @Nullable
    HoverEvent getHoverEvent();
    
    @Nullable
    ClickEvent getClickEvent();
    
    @Nullable
    String getInsertion();
//    {
//        return asString();
//    }
    
}
