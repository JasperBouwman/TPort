package com.spaceman.tport.tpEvents.restrictions;

import com.spaceman.tport.Main;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.tpEvents.TPRestriction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.UUID;

import static com.spaceman.tport.commandHandler.SubCommand.lowerCaseFirst;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatInfoTranslation;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;

public class DoSneakRestriction extends TPRestriction implements Listener {
    
    private UUID uuid = null;
    private boolean canTP = false;
    
    public DoSneakRestriction() {
        reset();
    }
    
    private void reset() {
        uuid = null;
        this.canTP = false;
        disable();
    }
    
    @EventHandler
    @SuppressWarnings("unused")
    public void moveEvent(PlayerToggleSneakEvent e) {
        if (e.getPlayer().getUniqueId().equals(uuid)) {
            canTP = true;
        }
    }
    
    @Override
    public String getRestrictionName() {
        return lowerCaseFirst(this.getClass().getSimpleName());
    }
    
    @Override
    public void start(Player player, int taskID) {
        this.reset();
        this.activate();
        uuid = player.getUniqueId();
    }
    
    @Override
    public void cancel() {
        reset();
    }
    
    @Override
    public Message getDescription() {
        return formatInfoTranslation("tport.tpEvents.restrictions.doSneakRestriction.description");
    }
    
    @Override
    public boolean shouldTeleport(Player player) {
        if (!canTP) {
            sendErrorTranslation(player, "tport.tpEvents.restrictions.doSneakRestriction.error");
        }
        return canTP;
    }
    
    @Override
    public void activate() {
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }
    
    @Override
    public void disable() {
        PlayerToggleSneakEvent.getHandlerList().unregister(this);
    }
}
