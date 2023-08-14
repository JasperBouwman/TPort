package com.spaceman.tport.keyValueHelper;

public class ExtendedKey extends Key {

    private ValueSetter valueSetter = (o, v) -> {};

    public ExtendedKey(String key, ValueChecker checker, boolean optional) {
        super(key, checker, optional);
    }

    public ExtendedKey(String key, ValueChecker checker, boolean optional, ValueSetter valueSetter) {
        super(key, checker, optional);
        this.valueSetter = valueSetter;
    }

    public void set(Object o, Object value) {
        valueSetter.setData(o, value);
    }

    @FunctionalInterface
    public interface ValueSetter {
        void setData(Object o, Object value);
    }
}
