package com.spaceman.tport.search;

import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.MessageUtils;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fancyMessage.inventories.InventoryModel;

import javax.annotation.Nullable;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatInfoTranslation;
import static com.spaceman.tport.inventories.SettingsInventories.*;

public enum SearchMode implements MessageUtils.MessageDescription {
    EQUALS(String::contentEquals,                                  (search, query) -> search == query, settings_search_mode_equal_model),
    NOT_EQUALS((search, query) -> !search.equalsIgnoreCase(query), (search, query) -> search != query, settings_search_mode_not_equal_model),
    CONTAINS(String::contains,                                     (search, query) -> search >= query, settings_search_mode_contains_model),
    NOT_CONTAINS(String::contains,                                 (search, query) -> search <= query, settings_search_mode_not_contains_model),
    STARTS(String::startsWith,                                     null,                    settings_search_mode_starts_model),
    ENDS(String::endsWith,                                         null,                    settings_search_mode_ends_model);
    
    private final StringFitter stringFitter;
    private final IntegerFitter integerFitter;
    private final InventoryModel inventoryModel;
    
    SearchMode(StringFitter stringFitter, @Nullable IntegerFitter integerFitter, InventoryModel inventoryModel) {
        this.stringFitter = stringFitter;
        this.integerFitter = integerFitter;
        this.inventoryModel = inventoryModel;
    }
    
    public static SearchMode get(String s) {
        try {
            return valueOf(s.toUpperCase());
        } catch (IllegalArgumentException iae) {
            return null;
        }
    }
    
    public boolean fits(String search, String query) {
        return this.stringFitter.fit(search.toLowerCase(), query.toLowerCase());
    }
    
    public boolean fits(int search, int query) {
        if (hasIntegerFitter()) return this.integerFitter.fit(search, query);
        else return false;
    }
    
    public boolean hasIntegerFitter() {
        return this.integerFitter != null;
    }
    
    public InventoryModel getInventoryModel() {
        return inventoryModel;
    }
    
    @Override
    public Message getDescription() {
        return formatInfoTranslation("tport.search.SearchMode." + this.name() + ".description", this.name());
    }
    
    @Override
    public Message getName(String color, String varColor) {
        return new Message(new TextComponent(name(), varColor));
    }
    
    @Override
    public String getInsertion() {
        return this.name();
    }
    
    @FunctionalInterface
    private interface StringFitter {
        boolean fit(String search, String query);
    }
    @FunctionalInterface
    private interface IntegerFitter {
        boolean fit(int search, int query);
    }
}