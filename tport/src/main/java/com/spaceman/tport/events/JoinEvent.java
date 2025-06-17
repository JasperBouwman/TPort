package com.spaceman.tport.events;

import com.spaceman.tport.Main;
import com.spaceman.tport.advancements.TPortAdvancement;
import com.spaceman.tport.advancements.TPortAdvancementManager;
import com.spaceman.tport.commands.tport.ResourcePack;
import com.spaceman.tport.fancyMessage.inventories.keyboard.QuickType;
import com.spaceman.tport.tpEvents.TPRequest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

import java.util.ArrayList;
import java.util.UUID;

import static com.spaceman.tport.advancements.TPortAdvancementManager.getOrCreateManager;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendSuccessTranslation;
import static com.spaceman.tport.fileHander.Files.tportData;

public class JoinEvent implements Listener {
    
    public static void setData(Player player) {
        String playerUUID = player.getUniqueId().toString();

        if (!tportData.getConfig().contains("tport." + playerUUID)) {
            tportData.getConfig().set("tport." + playerUUID + ".tports.0", "welcome");
            tportData.getConfig().set("tport." + playerUUID + ".tports.0", null);
            tportData.saveConfig();
        }
        
        QuickType.setQuickTypeSignHandler(player);
        
        if (TPortAdvancement.isActive()) {
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                // create manager after 10 ticks, after this delay the player should be fully joined and packets can be sent
                getOrCreateManager(player);
            }, 20);
        }
    }
    
    @EventHandler(priority=EventPriority.MONITOR)
    public void join(PlayerJoinEvent e) {
        setData(e.getPlayer());
        ResourcePack.updateResourcePack(e.getPlayer(), true);
    }
    
    @EventHandler
    public void leave(PlayerQuitEvent e) {
        TPRequest.playerLeft(e.getPlayer());
        QuickType.removeQuickTypeSignHandler(e.getPlayer());
        
        if (TPortAdvancement.isActive()) TPortAdvancementManager.removeAdvancementManager(e.getPlayer());
    }
    
    public static ArrayList<UUID> playerResourceList = new ArrayList<>();
    @EventHandler
    public void playerResourcePackStatusEvent(PlayerResourcePackStatusEvent e) {
        if (!playerResourceList.contains(e.getPlayer().getUniqueId())) {
            return;
        }
        
        switch (e.getStatus()) {
            case SUCCESSFULLY_LOADED -> {
                sendSuccessTranslation(e.getPlayer(), "tport.events.JoinEvent.playerResourcePackStatusEvent.succeeded");
                playerResourceList.remove(e.getPlayer().getUniqueId());
            }
            case DECLINED, DISCARDED -> {
                sendErrorTranslation(e.getPlayer(), "tport.events.JoinEvent.playerResourcePackStatusEvent.denied");
                playerResourceList.remove(e.getPlayer().getUniqueId());
                ResourcePack.setResourcePackState(e.getPlayer().getUniqueId(), false);
            }
            case FAILED_DOWNLOAD, FAILED_RELOAD, INVALID_URL -> {
                sendErrorTranslation(e.getPlayer(), "tport.events.JoinEvent.playerResourcePackStatusEvent.error");
                playerResourceList.remove(e.getPlayer().getUniqueId());
                ResourcePack.setResourcePackState(e.getPlayer().getUniqueId(), false);
            }
            case ACCEPTED, DOWNLOADED -> {
                // still working...
            }
        }
        
    }
    
}
