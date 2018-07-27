package com.spaceman.tport.fancyMessage.book;

import com.spaceman.tport.fancyMessage.TextComponent;
import com.sun.istack.internal.NotNull;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;

public class BookPage {

    private ArrayList<TextComponent> text = new ArrayList<>();

    public BookPage() {
    }

    public BookPage(TextComponent text) {
        this.text.add(text);
    }

    public static BookPage newBookPage() {
        return new BookPage();
    }

    public static BookPage newBookPage(TextComponent text) {
        return new BookPage(text);
    }

    public void addText(TextComponent text, TextComponent... followUp) {
        this.text.add(text);
        this.text.addAll(Arrays.asList(followUp));
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

}

