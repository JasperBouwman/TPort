package com.spaceman.tport.fancyMessage.events;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.MessageUtils;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fancyMessage.colorTheme.MultiColor;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;

@TextEvent.InteractiveTextEvent
public class HoverEvent implements TextEvent {
    
    private final ArrayList<TextComponent> text = new ArrayList<>();
    private ItemStack item = null;
    
    public static final String SHOW_TEXT = "show_text";
    public static final String SHOW_ITEM = "show_item";
    
    private String type = "show_text";
    
    public HoverEvent() {
    }
    
    public HoverEvent(String type) {
        this.type = type;
    }
    
    public HoverEvent(TextComponent... textComponents) {
        this.addText(textComponents);
    }
    
    public HoverEvent(Message message) {
        this.addMessage(message);
    }
    
    @Override
    public String toString() {
        return text.stream().map(TextComponent::toString).collect(Collectors.joining());
    }
    
    public static HoverEvent hoverEvent(String simpleText) {
        return hoverEvent(textComponent(simpleText));
    }
    
    public static HoverEvent hoverEvent(String simpleText, ChatColor color) {
        return hoverEvent(textComponent(simpleText, color));
    }
    
    public static HoverEvent hoverEvent(String simpleText, String color) {
        return hoverEvent(textComponent(simpleText, color));
    }
    
    public static HoverEvent hoverEvent(String simpleText, Color color) {
        return hoverEvent(textComponent(simpleText, color));
    }
    
    public static HoverEvent hoverEvent(String simpleText, MultiColor color) {
        return hoverEvent(textComponent(simpleText, color));
    }
    
    public static HoverEvent hoverEvent(String simpleText, ColorTheme.ColorType type) {
        return hoverEvent(textComponent(simpleText, type));
    }
    
    public static HoverEvent hoverEvent(TextComponent... textComponent) {
        HoverEvent hEvent = new HoverEvent();
        for (TextComponent text : textComponent) {
            hEvent.addText(text);
        }
        return hEvent;
    }
    
    public static HoverEvent hoverEvent(ItemStack is) {
        HoverEvent hoverEvent = new HoverEvent(SHOW_ITEM);
        hoverEvent.item = is;
        return hoverEvent;
    }
    
    @Override
    public JsonObject translateJSON(ColorTheme theme) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", type);
        if (type.equals(SHOW_TEXT)) {
            JsonArray jsonArray = new JsonArray();
            text.stream().map(t -> t.translateJSON(theme)).forEach(jsonArray::add);
            jsonObject.add("contents", jsonArray);
        }
        if (type.equals(SHOW_ITEM)) {
            jsonObject.addProperty("value", MessageUtils.toString(item).toString());
        }
        return jsonObject;
    }
    
    @Override
    public String name() {
        return "hoverEvent";
    }
    
    public void addMessage(Message message) {
        if (message != null) {
            for (TextComponent textComponent : message.getText()) {
                addText(textComponent);
            }
        }
    }
    
    public void addText(TextComponent... text) {
        this.type = SHOW_TEXT;
        for (TextComponent textComponent : text) {
            textComponent.clearInteractiveEvents();
            this.text.add(textComponent);
        }
    }
    
    public void addText(String simpleText) {
        this.addText(textComponent(simpleText));
    }
    
    public void addText(String simpleText, ChatColor color) {
        this.addText(textComponent(simpleText, color));
    }
    
    public ArrayList<TextComponent> getText() {
        return text;
    }
    
    public void removeLast() {
        if (!text.isEmpty()) {
            this.text.remove(text.size() - 1);
        }
    }
    
    public void setItem(ItemStack is) {
        this.type = SHOW_ITEM;
        this.item = is;
    }
}
