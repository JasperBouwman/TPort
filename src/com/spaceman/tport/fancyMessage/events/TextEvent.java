package com.spaceman.tport.fancyMessage.events;

import com.spaceman.tport.colorFormatter.ColorTheme;
import com.spaceman.tport.fancyMessage.Message;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface TextEvent {
    
    public String translateJSON(Message.TranslateMode mode, ColorTheme theme);
    
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface InteractiveTextEvent {
    
    }
}
