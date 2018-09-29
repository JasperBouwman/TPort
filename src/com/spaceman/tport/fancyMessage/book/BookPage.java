package com.spaceman.tport.fancyMessage.book;

import com.spaceman.tport.fancyMessage.TextComponent;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;

public class BookPage {

    private int pageNumber;

    private ArrayList<TextComponent> text = new ArrayList<>();

    private BookPage(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    private BookPage(TextComponent text, int pageNumber) {
        this.text.add(text);
        this.pageNumber = pageNumber;
    }

    static BookPage newBookPage(int pageNumber) {
        return new BookPage(pageNumber);
    }

    static BookPage newBookPage(TextComponent text, int pageNumber) {
        return new BookPage(text, pageNumber);
    }

    public void addText(TextComponent textComponent, TextComponent... followUp) {
        this.text.add(textComponent);
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

    public int getPageNumber() {
        return pageNumber + 1;
    }
}
