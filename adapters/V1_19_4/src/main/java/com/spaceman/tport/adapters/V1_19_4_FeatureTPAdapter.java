package com.spaceman.tport.adapters;

import com.spaceman.tport.Pair;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public abstract class V1_19_4_FeatureTPAdapter extends TPortAdapter {
    
    @Override
    public Pair<Location, String> searchFeature(Player player, Location startLocation, List<String> features) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, ClassNotFoundException {
        return null;
    }
    
    @Override
    public List<String> availableFeatures() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, ClassNotFoundException {
        return null;
    }
    
    @Override
    public List<String> availableFeatures(World world) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        return null;
    }
    
    @Override
    public List<Pair<String, List<String>>> getFeatureTags(World world) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, ClassNotFoundException {
        return null;
    }
}
