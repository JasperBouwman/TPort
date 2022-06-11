package com.spaceman.tport.tpEvents;

import com.spaceman.tport.fancyMessage.Message;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatErrorTranslation;

public abstract class ParticleAnimation {
    
    private static final HashMap<String, AnimationCreator> animations = new HashMap<>();
    private boolean enabled = true;
    
    public static Set<String> getAnimations() {
        return animations.keySet();
    }
    
    public static ParticleAnimation getNewAnimation(String animationName) {
        return getNewAnimation(animationName, null, null);
    }
    
    public static ParticleAnimation getNewAnimation(String animationName, String[] data, Player player) {
        if (animations.containsKey(animationName)) {
            try {
                ParticleAnimation animation = animations.get(animationName).create();
                if (data != null && data.length != 0 && player != null) {
                    animation.edit(player, data);
                }
                return animation;
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
    
    public static boolean registerAnimation(AnimationCreator animation) {
        Validate.notNull(animation, "The given AnimationCreator can not be null");
        return animations.put(animation.create().getAnimationName(), animation) != null;
    }
    
    public abstract String getAnimationName();
    
    public abstract void show(Player player, Location l);
    
    public abstract boolean edit(Player player, String[] data);
    
    public abstract void save(ConfigurationSection section);
    
    public abstract void load(ConfigurationSection section);
    
    public final boolean isEnabled() {
        return enabled;
    }
    
    public final void setEnabled(boolean state) {
        this.enabled = state;
    }
    
    public List<String> tabList(Player player, String[] args) {
        return Collections.emptyList();
    }
    
    public final void showIfEnabled(Player player, Location location) {
        if (isEnabled()) {
            this.show(player, location);
        }
    }
    
    public Message getDescription() {
        return formatErrorTranslation("tport.tpEvents.particleAnimation.defaultDescription");
    }
    
    @FunctionalInterface
    public interface AnimationCreator {
        ParticleAnimation create();
    }
}
