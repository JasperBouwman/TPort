package com.spaceman.tport.keyValueHelper;

import java.util.List;

public class Key {

    private String errorMessage = "is not valid";
    private boolean acceptNullValue = false;
    private String key;
    private ValueChecker checker;
    private boolean optional;

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

    public Key setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    public String getErrorMessage() {
        return errorMessage;
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
