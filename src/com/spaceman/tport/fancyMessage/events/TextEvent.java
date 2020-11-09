package com.spaceman.tport.fancyMessage.events;

import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import org.json.simple.JSONObject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface TextEvent {
    
    JSONObject translateJSON(ColorTheme theme);
    
    String name();
    
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface InteractiveTextEvent {
    
    }
}
