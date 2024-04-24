package com.spaceman.tport.search;

import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.MessageUtils;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fancyMessage.inventories.InventoryModel;

import javax.annotation.Nullable;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatInfoTranslation;
import static com.spaceman.tport.inventories.SettingsInventories.*;

public enum SearchMode implements MessageUtils.MessageDescription {
    EQUALS(String::contentEquals,                                          (element, searched) -> element == searched, settings_search_mode_equal_model),
    NOT_EQUALS((element, searched) -> !element.equalsIgnoreCase(searched), (element, searched) -> element != searched, settings_search_mode_not_equal_model),
    CONTAINS(String::contains,                                             (element, searched) -> element >= searched, settings_search_mode_contains_model),
    NOT_CONTAINS(String::contains,                                         (element, searched) -> element <= searched, settings_search_mode_not_contains_model),
    STARTS(String::startsWith,                                             null,                    settings_search_mode_starts_model),
    ENDS(String::endsWith,                                                 null,                    settings_search_mode_ends_model);
    
    private final StringFitter stringFitter;
    private final IntegerFitter integerFitter;
    private final InventoryModel inventoryModel;
    
    SearchMode(StringFitter stringFitter, @Nullable IntegerFitter integerFitter, InventoryModel inventoryModel) {
        this.stringFitter = stringFitter;
        this.integerFitter = integerFitter;
        this.inventoryModel = inventoryModel;
    }
    
    public static SearchMode get(String searchModeName) {
        try {
            return valueOf(searchModeName.toUpperCase());
        } catch (IllegalArgumentException iae) {
            return null;
        }
    }
    
    public boolean fits(String element, String searched) {
        return this.stringFitter.fit(element.toLowerCase(), searched.toLowerCase());
    }
    
    public boolean fits(int element, int searched) {
        if (hasIntegerFitter()) return this.integerFitter.fit(element, searched);
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
        boolean fit(String element, String searched);
    }
    @FunctionalInterface
    private interface IntegerFitter {
        boolean fit(int element, int searched);
    }
}