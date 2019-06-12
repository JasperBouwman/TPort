package com.spaceman.tport.fancyMessage.events;

import com.spaceman.tport.fancyMessage.TextComponent;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;

public class HoverEvent {

    private ArrayList<TextComponent> text = new ArrayList<>();

    public HoverEvent(TextComponent textComponent) {
        textComponent.clearEvents();
        this.text.add(textComponent);
    }

    public static HoverEvent hoverEvent(String simpleText) {
        return hoverEvent(textComponent(simpleText));
    }

    public static HoverEvent hoverEvent(String simpleText, ChatColor color) {
        return hoverEvent(textComponent(simpleText, color));
    }

    public static HoverEvent hoverEvent(TextComponent textComponent) {
        return new HoverEvent(textComponent);
    }

    public void addText(TextComponent textComponent) {
        textComponent.clearEvents();
        this.text.add(textComponent);
    }

    public void addText(TextComponent text, TextComponent... followUp) {
        text.clearEvents();
        this.text.add(text);
        for (TextComponent textComponent : followUp) {
            textComponent.clearEvents();
            this.text.add(textComponent);
        }
    }

    public void addText(String simpleText) {
        this.text.add(textComponent(simpleText));
    }

    public void addText(String simpleText, ChatColor color) {
        this.text.add(textComponent(simpleText, color));
    }

    public ArrayList<TextComponent> getText() {
        return text;
    }

    public void removeLast() {
        this.text.remove(text.size() - 1);
    }
}
