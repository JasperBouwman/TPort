package com.spaceman.tport.fancyMessage.markdown;

import com.spaceman.tport.fancyMessage.Message;

import java.util.ArrayList;

public class Chapter {
    
    private String chapterName;
    private String chapterHover;
    private int chapterLevel;
    private Message chapterHeader;
    private ArrayList<Message> chapterBody;
    private int pageNumber;
    private int tocPageNumber;
    private int volume;
    
    public Chapter(String chapterName, String chapterHover, int chapterLevel, Message chapterHeader, ArrayList<Message> chapterBody, int pageNumber, int tocPageNumber, int volume) {
        this.chapterName = chapterName;
        this.chapterHover = chapterHover;
        this.chapterLevel = chapterLevel;
        this.chapterHeader = chapterHeader;
        this.chapterBody = chapterBody;
        this.pageNumber = pageNumber;
        this.tocPageNumber = tocPageNumber;
        this.volume = volume;
        
        if (chapterBody.isEmpty()) chapterBody.add(new Message());
    }
    
    public int getTocPageNumber() {
        return tocPageNumber;
    }
    
    public int getVolume() {
        return volume;
    }
    
    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }
    
    public int getPageNumber() {
        return pageNumber;
    }
    
    public void setChapterBody(ArrayList<Message> chapterBody) {
        this.chapterBody = chapterBody;
        if (chapterBody.isEmpty()) chapterBody.add(new Message());
    }
    
    public void setChapterHeader(Message chapterHeader) {
        this.chapterHeader = chapterHeader;
    }
    
    public void setChapterHover(String chapterHover) {
        this.chapterHover = chapterHover;
    }
    
    public void setChapterLevel(int chapterLevel) {
        this.chapterLevel = chapterLevel;
    }
    
    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }
    
    public int getChapterLevel() {
        return chapterLevel;
    }
    
    public ArrayList<Message> getChapterBody() {
        return chapterBody;
    }
    
    public Message getChapterBodyLast() {
        return chapterBody.get(chapterBody.size() - 1);
    }
    
    public Message getChapterHeader() {
        return chapterHeader;
    }
    
    public String getChapterHover() {
        return chapterHover;
    }
    
    public String getChapterName() {
        return chapterName;
    }
    
    public boolean isEmpty() {
        
        if (this.chapterHeader.isEmpty()) {
            return true;
        }
        
        
        return false;
    }
}
