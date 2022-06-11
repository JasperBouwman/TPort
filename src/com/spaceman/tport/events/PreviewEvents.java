package com.spaceman.tport.events;

import com.spaceman.tport.Main;
import com.spaceman.tport.tport.TPort;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.UUID;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendInfoTranslation;

public class PreviewEvents implements Listener {
    
    private PreviewEvents() { }
    private static final PreviewEvents instance = new PreviewEvents();
    public static PreviewEvents getInstance() {
        return instance;
    }
    
    public static void register() {
        Bukkit.getServer().getPluginManager().registerEvents(instance, Main.getInstance());
    }
    
    public static void unregister() {
        PlayerMoveEvent.getHandlerList().unregister(instance);
        PlayerInteractEvent.getHandlerList().unregister(instance);
    }
    
    @EventHandler
    @SuppressWarnings("unused")
    public void onPlayerMove(PlayerMoveEvent e) {
        if (e.getTo() != null) {
            if (locationEquals(e.getFrom(), e.getTo())) {
                return;
            }
            if (isPreviewing(e.getPlayer().getUniqueId())) {
                cancelPreview(e.getPlayer());
            }
        }
    }
    
    private boolean locationEquals(Location l1, Location l2) {
        return l1.getX() == l2.getX() &&
                l1.getY() == l2.getY() &&
                l1.getZ() == l2.getZ();
    }
    
    @EventHandler
    @SuppressWarnings("unused")
    public void onWorldInteract(PlayerInteractEvent e) {
        if (isPreviewing(e.getPlayer().getUniqueId())) {
            cancelPreview(e.getPlayer());
        }
    }
    
    private record PreviewObject(Location oldLocation, TPort tport, GameMode oldGameMode) { }
    private static final HashMap<UUID, PreviewObject> previews = new HashMap<>();
    
    public static boolean isPreviewing(UUID uuid) {
        return previews.containsKey(uuid);
    }
    public static boolean cancelPreview(Player player) {
        return cancelPreview(player, true);
    }
    public static boolean cancelPreview(Player player, boolean sendMessage) {
        PreviewObject p = previews.remove(player.getUniqueId());
        if (p != null) {
            player.teleport(p.oldLocation());
            player.setGameMode(p.oldGameMode());
            if (sendMessage) sendInfoTranslation(player, "tport.tport.tport.cancelPreview.succeeded", p.tport());
            return true;
        }
        return false;
    }
    public static void cancelAllPreviews() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (cancelPreview(player, false))
                sendInfoTranslation(player, "tport.tport.tport.cancelPreview.reload");
        }
    }
    public static void preview(Player player, TPort tport) {
        if (!isPreviewing(player.getUniqueId())) {
            previews.put(player.getUniqueId(), new PreviewObject(player.getLocation(), tport, player.getGameMode()));
        }
        player.setGameMode(GameMode.SPECTATOR);
        player.teleport(tport.getLocation());
        
        sendInfoTranslation(player, "tport.tport.tport.preview.succeeded", tport);
    }
    
}
