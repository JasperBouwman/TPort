package com.spaceman.tport.keyValueHelper;

import java.util.List;

public class Key {

    private String errorMessageID = " is not valid";
    private boolean acceptNullValue = false;
    private final String key;
    private final ValueChecker checker;
    private final boolean optional;

    public Key(String key, ValueChecker checker, boolean optional) {
        this.key = key;
        this.checker = checker;
        this.optional = optional;
    }

    public static Key getKey(List<? extends Key> keyList, String key) {
        return keyList.stream().filter(k -> k.getKey().equalsIgnoreCase(key)).findFirst().orElse(null);
    }

    public String getKey() {
        return key;
    }

    public Object check(String value) {
        try {
            return checker.check(value);
        } catch (Exception e) {
            return null;
        }
    }

    public Key setErrorMessageID(String errorMessageID) {
        this.errorMessageID = errorMessageID;
        return this;
    }

    public String getErrorMessageID() {
        return errorMessageID;
    }

    public boolean isAcceptNullValue() {
        return acceptNullValue;
    }

    public Key setAcceptNullValue(boolean acceptNullValue) {
        this.acceptNullValue = acceptNullValue;
        return this;
    }

    public boolean isOptional() {
        return optional;
    }

    @FunctionalInterface
    public interface ValueChecker {
        //should return null if value does not exist
        Object check(String value);
    }
}
