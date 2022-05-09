package com.spaceman.tport;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class Pair<L, R> implements ConfigurationSerializable {
    
    private L left;
    private R right;
    
    public Pair(L l, R r) {
        this.left = l;
        this.right = r;
    }
    
    public static <L, R> Pair<L, R> deserialize(Map<String, Object> args) {
        //noinspection unchecked
        return new Pair<>((L) args.get("left"), (R) args.get("right"));
    }
    
    public L getLeft() {
        return left;
    }
    
    public void setLeft(L left) {
        this.left = left;
    }
    
    public R getRight() {
        return right;
    }
    
    public void setRight(R right) {
        this.right = right;
    }
    
    @Override
    public String toString() {
        return String.format("Pair={left:%s, right:%s}", left.toString(), right.toString());
    }
    
    @Nonnull
    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("left", left);
        map.put("right", right);
        return map;
    }
}
