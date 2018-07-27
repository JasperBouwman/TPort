package com.spaceman.tport.fancyMessage.events;

public class ClickEvent {

    public final static String RUN_COMMAND = "run_command";
    public final static String CHANGE_PAGE = "change_page";
    public final static String OPEN_URL = "open_url";
    public final static String SUGGEST_COMMAND = "suggest_command";

    private String clickEvent;
    private String value;

    public ClickEvent(String event, String value) {
        this.clickEvent = event;
        this.value = value;
    }

    public static ClickEvent clickEvent(String event, String value) {
        return new ClickEvent(event, value);
    }

    public static ClickEvent runCommand(String value) {
        return new ClickEvent(RUN_COMMAND, value);
    }

    public static ClickEvent changePage(String value) {
        return new ClickEvent(CHANGE_PAGE, value);
    }

    public static ClickEvent openUrl(String value) {
        return new ClickEvent(OPEN_URL, value);
    }

    public static ClickEvent suggestCommand(String value) {
        return new ClickEvent(SUGGEST_COMMAND, value);
    }

    public String getClickEvent() {
        return clickEvent;
    }

    public String getValue() {
        return value;
    }
}

