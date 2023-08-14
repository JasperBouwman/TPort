package com.spaceman.tport.tpEvents.restrictions;

import com.spaceman.tport.Main;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.tpEvents.TPEManager;
import com.spaceman.tport.tpEvents.TPRestriction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.UUID;

import static com.spaceman.tport.commandHandler.SubCommand.lowerCaseFirst;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatInfoTranslation;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;

public class InteractRestriction extends TPRestriction implements Listener {
    
    private UUID uuid = null;
    
    public InteractRestriction() {
        reset();
    }
    
    private void reset() {
        uuid = null;
        disable();
    }
    
    @EventHandler
    @SuppressWarnings("unused")
    public void moveEvent(PlayerInteractEvent e) {
        if (e.getPlayer().getUniqueId().equals(uuid)) {
            sendErrorTranslation(e.getPlayer(), "tport.tpEvents.restrictions.interactRestriction.error");
            TPEManager.cancelTP(uuid);
            reset();
        }
    }
    
    @Override
    public String getRestrictionName() {
        return lowerCaseFirst(this.getClass().getSimpleName());
    }
    
    @Override
    public void start(Player player, int taskID) {
        this.activate();
        uuid = player.getUniqueId();
    }
    
    @Override
    public void cancel() {
        reset();
    }
    
    @Override
    public Message getDescription() {
        return formatInfoTranslation("tport.tpEvents.restrictions.interactRestriction.description");
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
        PlayerInteractEvent.getHandlerList().unregister(this);
    }
}
