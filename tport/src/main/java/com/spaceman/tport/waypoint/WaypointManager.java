package com.spaceman.tport.waypoint;

import com.spaceman.tport.Main;
import com.spaceman.tport.adapters.TPortAdapter;
import com.spaceman.tport.commands.tport.Features;
import com.spaceman.tport.tport.TPort;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static com.spaceman.tport.commands.tport.Waypoints.getWaypointShowType;

public class WaypointManager implements Listener {
    
    public WaypointManager() {
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }
    
    @EventHandler
    @SuppressWarnings("unused")
    public void playerTeleport(PlayerTeleportEvent pte) {
        Location to = pte.getTo();
        if (to == null) return;
        World worldTo = to.getWorld();
        World worldFrom = pte.getFrom().getWorld();
        if (worldTo == null || worldFrom == null) return;
        
        if (!Objects.equals(worldFrom, worldTo)) {
            removeFromWorld(pte.getPlayer(), worldTo.getName(), worldFrom.getName());
        }
    }
    
    @EventHandler
    @SuppressWarnings("unused")
    public void playerLogin(PlayerLoginEvent ple) {
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            removeFromWorld(ple.getPlayer(), ple.getPlayer().getWorld().getName(), null);
        }, 40);
    }
    
    // world name, list of TPorts that can shows a waypoint in that world
    private static final HashMap<String, ArrayList<TPort>> waypoints = new HashMap<>();
    
    public static void registerTPort(TPort tport) {
        ArrayList<TPort> w = waypoints.getOrDefault(tport.getWorld().getName(), new ArrayList<>());
        
        w.add(tport);
        
        waypoints.put(tport.getWorld().getName(), w);
    }
    
    private static void checkWaypoint(Player player, TPort tport, @Nullable TPortAdapter tportAdapter) {
        if (tportAdapter == null) tportAdapter = Main.getInstance().adapter;
        
        if (Features.Feature.Waypoints.isDisabled()) {
            return;
        }
        
        if (!tport.isShowWaypoint()) {
            try {
                tportAdapter.removeWaypoint(player, tport);
            } catch (Throwable ex) {
                Features.Feature.printSmallNMSErrorInConsole("Waypoint", false);
                if (Features.Feature.PrintErrorsInConsole.isEnabled()) ex.printStackTrace();
            }
            return;
        }
        
        if (!getWaypointShowType(player.getUniqueId()).show(player, tport)) {
            return;
        }
        
        try {
            tportAdapter.sendWaypoint(player, tport);
        } catch (Throwable ex) {
            Features.Feature.printSmallNMSErrorInConsole("Waypoint", false);
            if (Features.Feature.PrintErrorsInConsole.isEnabled()) ex.printStackTrace();
        }
    }
    
    public static void updateTPort(TPort tport) {
        TPortAdapter adapter = Main.getInstance().adapter;
        for (Player player : tport.getWorld().getPlayers()) {
            checkWaypoint(player, tport, adapter);
        }
    }
    
    public static void removeFromWorld(TPort tport, @Nullable World oldWorld) {
        if (oldWorld == null) return;
        TPortAdapter adapter = Main.getInstance().adapter;
        for (Player player : oldWorld.getPlayers()) {
            try {
                adapter.removeWaypoint(player, tport);
            } catch (Throwable ex) {
                Features.Feature.printSmallNMSErrorInConsole("Waypoint", false);
                if (Features.Feature.PrintErrorsInConsole.isEnabled()) ex.printStackTrace();
            }
        }
    }
    
    public static void removeFromWorld(Player player, String worldNameNew, @Nullable String worldNameOld) {
        TPortAdapter adapter = Main.getInstance().adapter;
        
        ArrayList<TPort> oldWaypoints = (worldNameOld == null ? null : waypoints.getOrDefault(worldNameOld, new ArrayList<>()));
        if (oldWaypoints != null) {
            for (TPort tport : oldWaypoints) {
                try {
                    adapter.removeWaypoint(player, tport);
                } catch (Throwable ex) {
                    Features.Feature.printSmallNMSErrorInConsole("Waypoint", false);
                    if (Features.Feature.PrintErrorsInConsole.isEnabled()) ex.printStackTrace();
                }
            }
        }
        
        ArrayList<TPort> newWaypoints = waypoints.getOrDefault(worldNameNew, new ArrayList<>());
        for (TPort tport : newWaypoints) {
            checkWaypoint(player, tport, adapter);
        }
    }
}
