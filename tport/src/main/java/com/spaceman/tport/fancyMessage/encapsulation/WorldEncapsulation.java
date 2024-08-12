package com.spaceman.tport.fancyMessage.encapsulation;

import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import org.bukkit.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.spaceman.tport.fancyMessage.events.HoverEvent.hoverEvent;

public class WorldEncapsulation implements Encapsulation {
    
    protected final World world;
    
    public WorldEncapsulation(World world) {
        this.world = world;
    }
    
    @Override
    public String asString() {
        return world.getName();
    }
    
    @Nonnull
    @Override
    public Message toMessage(String color, String varColor) {
        return new Message(new TextComponent(asString(), varColor));
    }
    
    protected String command(boolean withSlash) {
        return (withSlash ? "/" : "") + "tport world " + world.getName();
    }
    
    @Override
    public HoverEvent getHoverEvent() {
        return hoverEvent(command(true), ColorTheme.ColorType.infoColor);
    }
    
    @Override
    public ClickEvent getClickEvent() {
        return ClickEvent.runCommand(command(true));
    }
    
    @Nullable
    @Override
    public String getInsertion() {
        return asString();
    }
    
}
