package com.spaceman.tport.keyValueHelper;

import com.spaceman.tport.Pair;

import java.util.ArrayList;
import java.util.List;

public class KeyValueTabArgument {

    /*
    * This is the class for the arguments of the TabConstructor
    * Each Argument is uses as -> key=value,AnotherKey=anotherValue...
    *
    * */

    private final String key;
    private List<String> values;
    private GetValuesRun getValues = (o, tabArguments) -> values;
    private Object o = null;

    public KeyValueTabArgument(String key, List<String> values) {
        this.key = key;
        this.values = values;
    }

    public KeyValueTabArgument(String key, Enum[] enums) {
        this.key = key;
        List<String> list = new ArrayList<>();
        for (Enum e : enums) {
            list.add(e.name());
        }
        this.values = list;
    }

    public KeyValueTabArgument(String key, Object o, GetValuesRun getValues) {
        this.key = key;
        this.values = new ArrayList<>();
        this.o = o;
        this.getValues = getValues;
    }

    public List<String> getValues(List<Pair<String, String>> writtenPairs) {
        return getValues.search(o, writtenPairs);
    }

    public String getKey() {
        return key;
    }

    public boolean containsValue(String testValue, List<Pair<String, String>> writtenPairs) {
        return this.getValues(writtenPairs).stream().anyMatch(s -> s.equalsIgnoreCase(testValue));
    }

    @FunctionalInterface
    public interface GetValuesRun {
        List<String> search(Object o, List<Pair<String, String>> writtenPairs);
    }
}
