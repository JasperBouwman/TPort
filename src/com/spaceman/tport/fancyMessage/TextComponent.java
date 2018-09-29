package com.spaceman.tport.fancyMessage;


import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import org.bukkit.ChatColor;

public class TextComponent {

    private String text;

    private String color;

    private Attribute[] attributes;

    private ClickEvent clickEvent;
    private HoverEvent hoverEvent;

    public final static String APOSTROPHE = "\\\\\\\"";
    public final static String NEW_LINE = "\n";

    private TextComponent(String text, String color, ClickEvent clickEvent, HoverEvent hoverEvent, Attribute... attribute) {
        this.text = text;
        this.color = color;
        this.clickEvent = clickEvent;
        this.hoverEvent = hoverEvent;
        this.attributes = attribute;
    }

    public static TextComponent textComponent(String text, Attribute... attribute) {
        return new TextComponent(text, null, null, null, attribute);
    }

    public static TextComponent textComponent(String text, String color, Attribute... attribute) {
        return new TextComponent(text, color, null, null, attribute);
    }

    public static TextComponent textComponent(String text, ChatColor color, Attribute... attribute) {
        return new TextComponent(text, color.name().toLowerCase(), null, null, attribute);
    }

    public static TextComponent textComponent(String text, String color, ClickEvent clickEvent, Attribute... attribute) {
        return new TextComponent(text, color, clickEvent, null, attribute);
    }

    public static TextComponent textComponent(String text, ChatColor color, ClickEvent clickEvent, Attribute... attribute) {
        return new TextComponent(text, color.name().toLowerCase(), clickEvent, null, attribute);
    }

    public static TextComponent textComponent(String text, String color, HoverEvent hoverEvent, Attribute... attribute) {
        return new TextComponent(text, color, null, hoverEvent, attribute);
    }

    public static TextComponent textComponent(String text, ChatColor color, HoverEvent hoverEvent, Attribute... attribute) {
        return new TextComponent(text, color.name().toLowerCase(), null, hoverEvent, attribute);
    }

    public static TextComponent textComponent(String text, String color, ClickEvent clickEvent, HoverEvent hoverEvent, Attribute... attribute) {
        return new TextComponent(text, color, clickEvent, hoverEvent, attribute);
    }

    public static TextComponent textComponent(String text, ChatColor color, ClickEvent clickEvent, HoverEvent hoverEvent, Attribute... attribute) {
        return new TextComponent(text, color.name().toLowerCase(), clickEvent, hoverEvent, attribute);
    }

    public void setClickEvent(ClickEvent clickEvent) {
        this.clickEvent = clickEvent;
    }

    public void setHoverEvent(HoverEvent hoverEvent) {
        this.hoverEvent = hoverEvent;
    }


    public void clearEvents() {
        this.clickEvent = null;
        this.hoverEvent = null;
    }

    public String getText() {
        return text;
    }

    public String getColor() {
        return color;
    }

    public Attribute[] getAttributes() {
        return attributes;
    }

    public ClickEvent getClickEvent() {
        return clickEvent;
    }

    public HoverEvent getHoverEvent() {
        return hoverEvent;
    }

    public boolean hasClickEvent() {
        return clickEvent != null;
    }

    public boolean hasHoverEvent() {
        return hoverEvent != null;
    }
}
