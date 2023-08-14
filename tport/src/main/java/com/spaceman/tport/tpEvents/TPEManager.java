package com.spaceman.tport.tpEvents;

import com.spaceman.tport.Main;
import com.spaceman.tport.commands.tport.Back;
import com.spaceman.tport.commands.tport.Delay;
import com.spaceman.tport.commands.tport.Features;
import com.spaceman.tport.commands.tport.pltp.Offset;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.TextType;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.tpEvents.animations.SimpleAnimation;
import com.spaceman.tport.tpEvents.restrictions.NoneRestriction;
import com.spaceman.tport.tport.TPort;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static com.spaceman.tport.commands.tport.Back.prevTPorts;
import static com.spaceman.tport.commands.tport.Restriction.isPermissionBased;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varErrorColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatSuccessTranslation;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;

public class TPEManager {
    
    private static final HashMap<UUID, ParticleAnimation> newLocAnimations = new HashMap<>();
    private static final HashMap<UUID, ParticleAnimation> oldLocAnimations = new HashMap<>();
    private static final HashMap<UUID, TPRestriction> tpRestrictions = new HashMap<>();
    private static final HashMap<UUID, Integer> taskIDs = new HashMap<>();
    
    public static void saveTPE(Files file) {
        for (UUID uuid : newLocAnimations.keySet()) {
            ParticleAnimation pa = newLocAnimations.get(uuid);
            pa.save(file.getConfig().createSection("ParticleAnimations.players." + uuid.toString() + ".new.data"));
            file.getConfig().set("ParticleAnimations.players." + uuid + ".new.name", pa.getAnimationName());
            file.getConfig().set("ParticleAnimations.players." + uuid + ".new.enabled", pa.isEnabled());
        }
        for (UUID uuid : oldLocAnimations.keySet()) {
            ParticleAnimation pa = oldLocAnimations.get(uuid);
            pa.save(file.getConfig().createSection("ParticleAnimations.players." + uuid.toString() + ".old.data"));
            file.getConfig().set("ParticleAnimations.players." + uuid + ".old.name", pa.getAnimationName());
            file.getConfig().set("ParticleAnimations.players." + uuid + ".old.enabled", pa.isEnabled());
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
    
    public static TPRestriction setTPRestriction(UUID uuid, @Nullable TPRestriction type) {
        if (type == null) {
            type = new NoneRestriction();
        }
        if (tpRestrictions.containsKey(uuid)) {
            tpRestrictions.get(uuid).disable();
        }
        tpRestrictions.put(uuid, type);
        return type;
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
            return setTPRestriction(uuid, null);
        }
        return tpRestrictions.get(uuid);
    }
    
    public static TPRestriction getTPRestriction(UUID uuid) {
        if (isPermissionBased()) {
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
    
    
    
    
    @FunctionalInterface
    public interface RequestedRunnable {
        void send(Player player, int delay, Message tickMessage, double seconds, Message secondMessage);
    }
    
    public static void tpPlayerToPlayer(Player player, Player toPlayer, Runnable postRestrictionMessage, RequestedRunnable requestedRunnable) {
        prevTPorts.put(player.getUniqueId(), new Back.PrevTPort(Back.PrevType.PLAYER, "playerUUID", toPlayer.getUniqueId().toString(), "prevLoc", player.getLocation()));
        requestTeleportPlayer(player, Offset.getPLTPOffset(toPlayer).applyOffset(toPlayer.getLocation()), postRestrictionMessage, requestedRunnable);
    }
    
    public static void tpPlayerToTPort(Player player, TPort tport, Runnable postRestrictionMessage, RequestedRunnable requestedRunnable) {
        prevTPorts.put(player.getUniqueId(), new Back.PrevTPort("TPORT", "tportName", tport.getName(), "tportUUID", tport.getTportID(), "tportOwner", tport.getOwner(), "prevLoc", player.getLocation()));
        requestTeleportPlayer(player, tport.getLocation(), postRestrictionMessage, requestedRunnable);
    }
    
    public static void requestTeleportPlayer(Player player, Location l, Runnable successRunnable, RequestedRunnable requestedRunnable) {
        requestTeleportPlayer(player, l, false, successRunnable, requestedRunnable);
    }
    public static void requestTeleportPlayer(Player player, Location l, boolean ignore_interdimensionalTeleporting, Runnable successRunnable, RequestedRunnable requestedRunnable) {
        if (
                !ignore_interdimensionalTeleporting &&
                Features.Feature.InterdimensionalTeleporting.isDisabled() &&
                !player.getWorld().equals(l.getWorld())) {
            sendErrorTranslation(player, "tport.tpEvents.requestTeleportPlayer.InterdimensionalTeleporting.disabled", player.getWorld(), l.getWorld());
            return;
        }
        
        if (TPEManager.hasTPRequest(player.getUniqueId())) {
            Message hereMessage = new Message();
            hereMessage.addText(textComponent("tport.events.inventoryClick.alreadyRequested.here", varErrorColor,
                    new HoverEvent(textComponent("/tport cancel", varInfoColor)), ClickEvent.runCommand("/tport cancel"))
                    .setType(TextType.TRANSLATE).setInsertion("/tport cancel"));
            sendErrorTranslation(player, "tport.events.inventoryClick.alreadyRequested", hereMessage);
            return;
        }
        int delay = Delay.delayTime(player);
        if (delay == 0) {
            teleportPlayer(player, l);
            successRunnable.run();
        } else {
            double seconds = delay / 20D;
            Message secondMessage;
            if (seconds == 1) secondMessage = formatSuccessTranslation("tport.command.second");
            else secondMessage = formatSuccessTranslation("tport.command.seconds");
            Message tickMessage;
            if (delay == 1) tickMessage = formatSuccessTranslation("tport.command.minecraftTick");
            else tickMessage = formatSuccessTranslation("tport.command.minecraftTicks");
            
            TPRestriction tpRestriction = TPEManager.getTPRestriction(player.getUniqueId());
            if (tpRestriction == null) {
                registerTP(player.getUniqueId(),
                        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                            teleportPlayer(Bukkit.getPlayer(player.getUniqueId()), l);
                            successRunnable.run();
                        }, delay).getTaskId());
            } else {
                tpRestriction.start(player, registerTP(player.getUniqueId(),
                        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                            if (tpRestriction.shouldTeleport(player)) {
                                teleportPlayer(Bukkit.getPlayer(player.getUniqueId()), l);
                                successRunnable.run();
                            }
                            cancelTP(player.getUniqueId());
                        }, delay).getTaskId())
                );
            }
            requestedRunnable.send(player, delay, tickMessage, seconds, secondMessage);
        }
    }
    
    
    private static void teleportPlayer(@Nullable Player player, Location l) {
        if (player == null) return;
        
        ArrayList<LivingEntity> slaves = new ArrayList<>();
        for (Entity e : player.getWorld().getEntities()) {
            if (e instanceof LivingEntity livingEntity) {
                if (livingEntity.isLeashed()) {
                    if (livingEntity.getLeashHolder() instanceof Player) {
                        if (livingEntity.getLeashHolder().getUniqueId().equals(player.getUniqueId())) {
                            slaves.add(livingEntity);
                            livingEntity.setLeashHolder(null);
                        }
                    }
                }
            }
        }
        
        LivingEntity horse = null;
        Boat.Type boatType = null;
        Entity sailor = null;
        if (player.getVehicle() instanceof LivingEntity) {
            horse = (LivingEntity) player.getVehicle();
        } else if (player.getVehicle() instanceof Boat b) {
            boatType = b.getBoatType();
            if (b.getPassengers().size() > 1) {
                sailor = b.getPassengers().get(1);
                sailor.leaveVehicle();
                sailor.teleport(l);
            }
            b.remove();
        }
        
        boolean showAnimation = Features.Feature.ParticleAnimation.isEnabled();
        
        if (showAnimation) TPEManager.getOldLocAnimation(player.getUniqueId()).showIfEnabled(player, player.getLocation().clone());
        if (!player.getWorld().equals(l.getWorld())) {
            player.teleport(l);
        }
        player.teleport(l);
        TPEManager.removeTP(player.getUniqueId());
        if (showAnimation) TPEManager.getNewLocAnimation(player.getUniqueId()).showIfEnabled(player, l.clone());
        
        try {
            if (horse != null) {
                horse.teleport(player);
                horse.addPassenger(player);
            } else if (boatType != null) {
                Boat b = player.getWorld().spawn(player.getLocation(), Boat.class);
                b.setBoatType(boatType);
                b.teleport(player);
                b.addPassenger(player);
                if (sailor != null) {
                    sailor.teleport(player);
                    b.addPassenger(sailor);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        for (LivingEntity e : slaves) {
            try {
                e.teleport(player);
                e.setLeashHolder(player);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
