package com.spaceman.tport.tpEvents;

import com.spaceman.tport.Main;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.tpEvents.animations.SimpleAnimation;
import com.spaceman.tport.tpEvents.restrictions.NoneRestriction;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.UUID;

import static com.spaceman.tport.fileHander.GettingFiles.getFile;

public class TPEManager {
    
    private static final HashMap<UUID, ParticleAnimation> newLocAnimations = new HashMap<>();
    private static final HashMap<UUID, ParticleAnimation> oldLocAnimations = new HashMap<>();
    private static final HashMap<UUID, TPRestriction> tpRestrictions = new HashMap<>();
    private static final HashMap<UUID, Integer> taskIDs = new HashMap<>();
    
    public static void saveTPE(Files file) {
        for (UUID uuid : newLocAnimations.keySet()) {
            ParticleAnimation pa = newLocAnimations.get(uuid);
            pa.save(file.getConfig().createSection("ParticleAnimations.players." + uuid.toString() + ".new.data"));
            file.getConfig().set("ParticleAnimations.players." + uuid.toString() + ".new.name", pa.getAnimationName());
            file.getConfig().set("ParticleAnimations.players." + uuid.toString() + ".new.enabled", pa.isEnabled());
        }
        for (UUID uuid : oldLocAnimations.keySet()) {
            ParticleAnimation pa = oldLocAnimations.get(uuid);
            pa.save(file.getConfig().createSection("ParticleAnimations.players." + uuid.toString() + ".old.data"));
            file.getConfig().set("ParticleAnimations.players." + uuid.toString() + ".old.name", pa.getAnimationName());
            file.getConfig().set("ParticleAnimations.players." + uuid.toString() + ".old.enabled", pa.isEnabled());
        }
        for (UUID uuid : tpRestrictions.keySet()) {
            TPRestriction type = tpRestrictions.get(uuid);
            file.getConfig().set("restriction.type." + uuid.toString(), type.getRestrictionName());
        }
        file.saveConfig();
    }
    
    public static void loadTPE(Files file) {
        for (String uuidS : file.getKeys("restriction.type")) {
            UUID uuid = UUID.fromString(uuidS);
            TPRestriction type = TPRestriction.getNewRestriction(file.getConfig().getString("restriction.type." + uuidS));
            setTPRestriction(uuid, type);
        }
        for (String uuidS : file.getKeys("ParticleAnimations.players")) {
            UUID uuid = UUID.fromString(uuidS);
            if (file.getConfig().contains("ParticleAnimations.players." + uuidS + ".new")) {
                ParticleAnimation newPA = ParticleAnimation.getNewAnimation(file.getConfig().getString("ParticleAnimations.players." + uuidS + ".new.name"));
                if (newPA != null) {
                    try {
                        newPA.setEnabled(file.getConfig().getBoolean("ParticleAnimations.players." + uuidS + ".new.enabled", true));
                        if (file.getConfig().contains("ParticleAnimations.players." + uuidS + ".new.data")) {
                            newPA.load(file.getConfig().getConfigurationSection("ParticleAnimations.players." + uuidS + ".new.data"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    newLocAnimations.put(uuid, newPA);
                } else {
                    Main.getInstance().getLogger().warning("Could not find particle animation " + file.getConfig().getString("ParticleAnimations.players." + uuidS + ".new.name"));
                }
            }
    
            if (file.getConfig().contains("ParticleAnimations.players." + uuidS + ".old")) {
                ParticleAnimation oldPA = ParticleAnimation.getNewAnimation(file.getConfig().getString("ParticleAnimations.players." + uuidS + ".old.name"));
                if (oldPA != null) {
                    try {
                        oldPA.setEnabled(file.getConfig().getBoolean("ParticleAnimations.players." + uuidS + ".old.enabled", true));
                        if (file.getConfig().contains("ParticleAnimations.players." + uuidS + ".old.data")) {
                            oldPA.load(file.getConfig().getConfigurationSection("ParticleAnimations.players." + uuidS + ".old.data"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    oldLocAnimations.put(uuid, oldPA);
                } else {
                    Main.getInstance().getLogger().warning("Could not find particle animation " + file.getConfig().getString("ParticleAnimations.players." + uuidS + ".old.name"));
                }
            }
        }
    }
    
    public static ParticleAnimation setNewLocAnimation(UUID uuid, ParticleAnimation pa) {
        newLocAnimations.put(uuid, pa);
        return pa;
    }
    
    public static ParticleAnimation setOldLocAnimation(UUID uuid, ParticleAnimation pa) {
        oldLocAnimations.put(uuid, pa);
        return pa;
    }
    
    public static void setTPRestriction(UUID uuid, @Nullable TPRestriction type) {
        if (type == null) {
            type = new NoneRestriction();
        }
        if (tpRestrictions.containsKey(uuid)) {
            tpRestrictions.get(uuid).disable();
        }
        tpRestrictions.put(uuid, type);
    }
    
    @Nonnull
    public static ParticleAnimation getNewLocAnimation(UUID uuid) {
        if (newLocAnimations.getOrDefault(uuid, null) == null) {
            return setNewLocAnimation(uuid, new SimpleAnimation());
        }
        return newLocAnimations.get(uuid);
    }
    
    @Nonnull
    public static ParticleAnimation getOldLocAnimation(UUID uuid) {
        if (oldLocAnimations.getOrDefault(uuid, null) == null) {
            return setOldLocAnimation(uuid, new SimpleAnimation(false));
        }
        return oldLocAnimations.get(uuid);
    }
    
    private static TPRestriction getUnmodifiedTPRestriction(UUID uuid) {
        if (!tpRestrictions.containsKey(uuid)) {
            setTPRestriction(uuid, null);
        }
        return tpRestrictions.get(uuid);
    }
    
    public static TPRestriction getTPRestriction(UUID uuid) {
    
        if (getFile("TPortConfig").getConfig().getBoolean("restriction.permission", false)) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                for (PermissionAttachmentInfo p : player.getEffectivePermissions()) {
                    if (p.getPermission().toLowerCase().startsWith("tport.restriction.type.")) {
                        setTPRestriction(uuid, TPRestriction.getNewRestriction(p.getPermission().toLowerCase().replace("tport.restriction.type.", "")));
                        break;
                    }
                }
            }
            setTPRestriction(uuid, null);
        }
        return getUnmodifiedTPRestriction(uuid);
    }
    
    public static int registerTP(UUID uuid, int taskID) {
        taskIDs.put(uuid, taskID);
        return taskID;
    }
    
    public static boolean hasTPRequest(UUID uuid) {
        return taskIDs.containsKey(uuid);
    }
    
    public static void removeTP(UUID uuid) {
        taskIDs.remove(uuid);
    }
    
    public static boolean cancelTP(UUID uuid) {
        if (taskIDs.containsKey(uuid)) {
            TPRestriction tpr = TPEManager.getUnmodifiedTPRestriction(uuid);
            if (tpr != null) tpr.cancel();
            Bukkit.getScheduler().cancelTask(taskIDs.remove(uuid));
            return true;
        }
        return false;
    }
}
