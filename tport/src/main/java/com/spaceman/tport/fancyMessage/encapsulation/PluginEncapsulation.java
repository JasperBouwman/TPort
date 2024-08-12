package com.spaceman.tport.fancyMessage.encapsulation;

import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatInfoTranslation;

public class PluginEncapsulation implements Encapsulation {
    
    private final String plugin;
    
    public PluginEncapsulation(String plugin) {
        if (plugin == null) plugin = "null";
        this.plugin = plugin;
    }
    
    @Override
    public String asString() {
        return plugin;
    }
    
    @Nonnull
    @Override
    public Message toMessage(String color, String varColor) {
        return new Message(new TextComponent(asString(), varColor));
    }
    
    @Override
    public HoverEvent getHoverEvent() {
        Message hoverMessage = new Message();
        
        if (plugin.equals("null")) return null;
        Plugin p = Bukkit.getPluginManager().getPlugin(plugin);
        if (p == null) return null;
        PluginDescriptionFile descriptionFile = p.getDescription();
        
        hoverMessage.addMessage(formatInfoTranslation("tport.fancyMessage.encapsulation.pluginEncapsulation.name", descriptionFile.getName()));
        hoverMessage.addMessage(new Message(TextComponent.NEW_LINE));
        hoverMessage.addMessage(formatInfoTranslation("tport.fancyMessage.encapsulation.pluginEncapsulation.version", descriptionFile.getVersion()));
        
        List<String> authors = descriptionFile.getAuthors();
        if (authors.size() == 1) {
            hoverMessage.addMessage(new Message(TextComponent.NEW_LINE));
            hoverMessage.addMessage(formatInfoTranslation("tport.fancyMessage.encapsulation.pluginEncapsulation.author", descriptionFile.getAuthors().get(0)));
        } else {
            hoverMessage.addMessage(new Message(TextComponent.NEW_LINE));
            hoverMessage.addMessage(formatInfoTranslation("tport.fancyMessage.encapsulation.pluginEncapsulation.authors", String.join(", ", descriptionFile.getAuthors())));
        }
        
        if (descriptionFile.getWebsite() != null) {
            hoverMessage.addMessage(new Message(TextComponent.NEW_LINE));
            hoverMessage.addMessage(formatInfoTranslation("tport.fancyMessage.encapsulation.pluginEncapsulation.website", descriptionFile.getWebsite()));
        }
        
        if (descriptionFile.getDescription() != null) {
            hoverMessage.addMessage(new Message(TextComponent.NEW_LINE));
            hoverMessage.addMessage(formatInfoTranslation("tport.fancyMessage.encapsulation.pluginEncapsulation.description", descriptionFile.getDescription()));
        }
        
        return new HoverEvent(hoverMessage);
    }
    
    @Nullable
    @Override
    public ClickEvent getClickEvent() {
        return null;
    }
    
    @Nullable
    @Override
    public String getInsertion() {
        return asString();
    }
}
