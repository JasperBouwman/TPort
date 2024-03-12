package com.spaceman.tport.fancyMessage.inventories.keyboard;

import com.spaceman.tport.Main;
import com.spaceman.tport.Pair;
import com.spaceman.tport.adapters.TPortAdapter;
import com.spaceman.tport.commands.tport.Features;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.UUID;

public class QuickType {
    
    private static final HashMap<UUID, Pair<Callback, Location>> quickMap = new HashMap<>();
    
    public static void setQuickTypeSignHandler(Player player) {
        try {
            Main.getInstance().adapter.setQuickTypeSignHandler(player);
        } catch (Throwable e) {
            Features.Feature.printSmallNMSErrorInConsole("QuickType", false);
            if (Features.Feature.PrintErrorsInConsole.isEnabled()) e.printStackTrace();
        }
    }
    
    public static void removeQuickTypeSignHandler(Player player) {
        try {
            Main.getInstance().adapter.removeQuickTypeSignHandler(player);
        } catch (Throwable e) {
            Features.Feature.printSmallNMSErrorInConsole("QuickType", false);
            if (Features.Feature.PrintErrorsInConsole.isEnabled()) e.printStackTrace();
        }
    }
    
    public static boolean onSignEdit(String[] lines, UUID uuid) {
        Pair<Callback, Location> pair = quickMap.remove(uuid);
        if (pair != null) {
            pair.getLeft().onDone(lines);
            TPortAdapter adapter = Main.getInstance().adapter;
            
            Location blockLoc = pair.getRight();
            Player player = Bukkit.getPlayer(uuid);
            try {
                if (player != null) adapter.sendBlockChange(player, blockLoc, blockLoc.getBlock());
            } catch (Throwable e) {
                Features.Feature.printSmallNMSErrorInConsole("QuickType", false);
                if (Features.Feature.PrintErrorsInConsole.isEnabled()) e.printStackTrace();
            }
            return true;
        }
        return false;
    }
    
    @FunctionalInterface
    public interface Callback {
        void onDone(String[] lines);
    }
    
    /** Inspired by <a href="https://github.com/Cleymax/SignGUI">SignGUI</a> */
    public static void open(Player player, Callback callback) {
        player.closeInventory();
        TPortAdapter adapter = Main.getInstance().adapter;
        
        Location blockLoc = player.getEyeLocation().clone();
        blockLoc.add(blockLoc.getDirection().multiply(-4));
        
        try {
            adapter.sendBlockChange(player, blockLoc, Material.OAK_SIGN);
            adapter.sendSignEditor(player, blockLoc);
        } catch (Throwable e) {
            Features.Feature.printSmallNMSErrorInConsole("QuickType", false);
            if (Features.Feature.PrintErrorsInConsole.isEnabled()) e.printStackTrace();
        }
        quickMap.put(player.getUniqueId(), new Pair<>(callback, blockLoc));
    }
    

}
