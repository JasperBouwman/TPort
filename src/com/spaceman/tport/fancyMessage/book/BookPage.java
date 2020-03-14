package com.spaceman.tport.fancyMessage.book;

import com.spaceman.tport.colorFormatter.ColorTheme;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.TextComponent;
import org.bukkit.ChatColor;

import java.util.ArrayList;

public class BookPage {
    
    private int pageNumber;
    private Message message;
    
    public BookPage() {
        message = new Message();
    }
    
    public BookPage(TextComponent text) {
        this();
        addText(text);
    }
    
    @Override
    public String toString() {
        return message.toString();
    }
    
    public static String getActivePageReplacer() {
        return "{APN}";
    }
    
    public void addText(TextComponent... text) {
        this.message.addText(text);
    }
    
    public void removeLast() {
        message.removeLast();
    }
    
    public void addText(String simpleText) {
        message.addText(simpleText);
    }
    
    public void addText(String simpleText, ChatColor color) {
        message.addText(simpleText, color);
    }
    
    public void addMessage(Message message) {
        this.message.addMessage(message);
    }
    
    public ArrayList<TextComponent> getText() {
        return message.getText();
    }
    
    public Message getMessage() {
        return message;
    }
    
    public void setMessage(Message message) {
        this.message = message;
    }
    
    public int getPageNumber() {
        return pageNumber;
    }
    
    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }
    
    public String translateJSON(Message.TranslateMode mode, ColorTheme theme) {
        return "\"" + message.translateJSON(mode, theme).replace(getActivePageReplacer(), String.valueOf(pageNumber)) + "\"";
    }
}
