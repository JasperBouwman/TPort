package com.spaceman.tport.fancyMessage.events;

import com.google.gson.JsonObject;
import com.spaceman.tport.Main;
import com.spaceman.tport.fancyMessage.book.BookPage;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;

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
    
    @Override
    public String toString() {
        return "ClickEvent{" +
                "action='" + action + '\'' +
                ", value='" + value + '\'' +
                ", pageNumber=" + pageNumber +
                '}';
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
    public JsonObject translateJSON(ColorTheme theme) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", action);
        
        if (Main.getInstance().adapter.JSONVersion() == 0 || Main.getInstance().adapter.JSONVersion() == -1) {
            jsonObject.addProperty("value", value.replace(getActivePageReplacer(), (pageNumber == null ? "" : String.valueOf(pageNumber.getPageNumber()))));
        }
        
        if (Main.getInstance().adapter.JSONVersion() == 1 || Main.getInstance().adapter.JSONVersion() == -1) {
            switch (action) {
                case OPEN_URL ->
                        jsonObject.addProperty("url", value.replace(getActivePageReplacer(), (pageNumber == null ? "" : String.valueOf(pageNumber.getPageNumber()))));
                case RUN_COMMAND, SUGGEST_COMMAND ->
                        jsonObject.addProperty("command", value.replace(getActivePageReplacer(), (pageNumber == null ? "" : String.valueOf(pageNumber.getPageNumber()))));
                case CHANGE_PAGE -> {
                    if (value.equals(getActivePageReplacer())) {
                        jsonObject.addProperty("page", pageNumber.getPageNumber());
                    } else {
                        try {
                            jsonObject.addProperty("page", Integer.parseInt(value));
                        } catch (NumberFormatException ignore) {
                        }
                    }
                }
            }
        }
        
        return jsonObject;
    }
    
    @Override
    public String[] name() {
        return switch (Main.getInstance().adapter.JSONVersion()) {
            case -1 -> new String[]{"clickEvent", "click_event"};
            case 0 -> new String[]{"clickEvent"};
            case 1 -> new String[]{"click_event"};
            default -> throw new IllegalStateException("Unexpected value: " + Main.getInstance().adapter.JSONVersion());
        };
    }
}
