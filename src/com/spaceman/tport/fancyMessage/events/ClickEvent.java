package com.spaceman.tport.fancyMessage.events;

import com.spaceman.tport.colorFormatter.ColorTheme;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.book.BookPage;

import static com.spaceman.tport.fancyMessage.book.BookPage.getActivePageReplacer;

@TextEvent.InteractiveTextEvent
public class ClickEvent implements TextEvent {
    
    public final static String RUN_COMMAND = "run_command";
    public final static String CHANGE_PAGE = "change_page";
    public final static String OPEN_URL = "open_url";
    public final static String SUGGEST_COMMAND = "suggest_command";
    public final static String COPY_TO_CLIPBOARD = "copy_to_clipboard";
    
    private String action;
    private String value;
    private BookPage pageNumber = null;
    
    public ClickEvent(String action, String value) {
        this.action = action;
        this.value = value;
    }
    
    public ClickEvent(String event, BookPage page) {
        this.action = event;
        this.value = "{APN}";
        this.pageNumber = page;
    }
    
    public static ClickEvent clickEvent(String event, String value) {
        return new ClickEvent(event, value);
    }
    
    public static ClickEvent clickEvent(String event, int value) {
        return new ClickEvent(event, String.valueOf(value));
    }
    
    public static ClickEvent clickEvent(String event, BookPage page) {
        return new ClickEvent(event, page);
    }
    
    public static ClickEvent runCommand(String value) {
        return new ClickEvent(RUN_COMMAND, value);
    }
    
    public static ClickEvent changePage(String value) {
        return new ClickEvent(CHANGE_PAGE, value);
    }
    
    public static ClickEvent changePage(int value) {
        return new ClickEvent(CHANGE_PAGE, String.valueOf(value));
    }
    
    public static ClickEvent openUrl(String value) {
        return new ClickEvent(OPEN_URL, value);
    }
    
    public static ClickEvent suggestCommand(String value) {
        return new ClickEvent(SUGGEST_COMMAND, value);
    }
    
    public static ClickEvent copyToClipboard(String value) {
        return new ClickEvent(COPY_TO_CLIPBOARD, value);
    }
    
    public static ClickEvent copyToClipBoard(String value) {
        return new ClickEvent(COPY_TO_CLIPBOARD, value);
    }
    
    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public String getValue() {
        if (pageNumber != null) {
            return value.replace(getActivePageReplacer(), String.valueOf(pageNumber.getPageNumber()));
        }
        return value;
    }
    
    public void setPageNumber(BookPage pageNumber) {
        this.pageNumber = pageNumber;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    @Override
    public String translateJSON(Message.TranslateMode mode, ColorTheme theme) {
        String q = mode.getQuote();
        return String.format(q + "clickEvent" + q + ":{" + q + "action" + q + ":" + q + "%s" + q + "," + q + "value" + q + ":" + q + "%s" + q + "}",
                action, value.replace(getActivePageReplacer(), (pageNumber == null ? "" : String.valueOf(pageNumber.getPageNumber()))));
    }
}

