package com.spaceman.tport.tpEvents.restrictions;

import com.spaceman.tport.Main;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.tpEvents.TPEManager;
import com.spaceman.tport.tpEvents.TPRestriction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;

import static com.spaceman.tport.commandHandler.SubCommand.lowerCaseFirst;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;

public class WalkRestriction extends TPRestriction implements Listener {
    
    private UUID uuid = null;
    private Location oldLoc = null;
    
    public WalkRestriction() {
        reset();
    }
    
    private void reset() {
        uuid = null;
        oldLoc = null;
        disable();
    }
    
    @EventHandler
    @SuppressWarnings("unused")
    public void moveEvent(PlayerMoveEvent e) {
        if (e.getPlayer().getUniqueId().equals(uuid) && oldLoc != null) {
            if (oldLoc.getX() != e.getPlayer().getLocation().getX() ||
                    oldLoc.getY() != e.getPlayer().getLocation().getY() ||
                    oldLoc.getZ() != e.getPlayer().getLocation().getZ()) {
                sendErrorTranslation(e.getPlayer(), "tport.tpEvents.restrictions.walkRestriction.error");
                TPEManager.cancelTP(uuid);
                reset();
            }
        }
    }
    
    @Override
    public String getRestrictionName() {
        return lowerCaseFirst(this.getClass().getSimpleName());
    }
    
    @Override
    public void start(Player player, int taskID) {
        this.activate();
        this.oldLoc = player.getLocation();
        uuid = player.getUniqueId();
    }
    
    @Override
    public void cancel() {
        reset();
    }
    
    @Override
    public Message getDescription() {
        return formatInfoTranslation("tport.tpEvents.restrictions.walkRestriction.description");
    }
    
    @Override
    public boolean shouldTeleport(Player player) {
        reset();
        return true;
    }
    
    @Override
    public void activate() {
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }
    
    @Override
    public void disable() {
        PlayerMoveEvent.getHandlerList().unregister(this);
    }
}
