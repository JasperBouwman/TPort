package com.spaceman.tport.events;

import com.spaceman.tport.Main;
import com.spaceman.tport.commands.TPortCommand;
import com.spaceman.tport.commands.tport.Redirect;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.logging.Level;

public class CommandEvent implements Listener {
    
    @EventHandler
    @SuppressWarnings("unused")
    void onCommand(PlayerCommandPreprocessEvent e) {
        if (Redirect.Redirects.TP_PLTP.isEnabled() && e.getMessage().matches("/tp [a-zA-Z_0-9]{3,16}")) {
            e.setCancelled(true);
            TPortCommand.executeInternal(e.getPlayer(), "PLTP tp" + e.getMessage().substring(3));
            if (Redirect.Redirects.ConsoleFeedback.isEnabled()) {
                Main.getInstance().getLogger().log(Level.INFO, "Redirected the command '" + e.getMessage() + "' to '/tport " + "PLTP tp" + e.getMessage().substring(3) + "'");
            }
        }
        
        if (Redirect.Redirects.Locate_FeatureTP.isEnabled() && e.getMessage().matches("/locate .+")) {
            e.setCancelled(true);
            TPortCommand.executeInternal(e.getPlayer(), "FeatureTP search" + e.getMessage().substring(7));
            if (Redirect.Redirects.ConsoleFeedback.isEnabled()) {
                Main.getInstance().getLogger().log(Level.INFO, "Redirected the command '" + e.getMessage() + "' to '/tport " + "FeatureTP search" + e.getMessage().substring(7) + "'");
            }
        }
        
        if (Redirect.Redirects.LocateBiome_BiomeTP.isEnabled() && e.getMessage().matches("/locateBiome .+")) {
            e.setCancelled(true);
            TPortCommand.executeInternal(e.getPlayer(), "BiomeTP whitelist" + e.getMessage().substring(12));
            if (Redirect.Redirects.ConsoleFeedback.isEnabled()) {
                Main.getInstance().getLogger().log(Level.INFO, "Redirected the command '" + e.getMessage() + "' to '/tport " + "BiomeTP whitelist" + e.getMessage().substring(12) + "'");
            }
        }
        
        if (Redirect.Redirects.Home_TPortHome.isEnabled() && e.getMessage().matches("/home")) {
            e.setCancelled(true);
            TPortCommand.executeInternal(e.getPlayer(), "home");
            if (Redirect.Redirects.ConsoleFeedback.isEnabled()) {
                Main.getInstance().getLogger().log(Level.INFO, "Redirected the command '" + e.getMessage() + "' to '/tport home'");
            }
        }
        
        if (Redirect.Redirects.Back_TPortBack.isEnabled() && e.getMessage().matches("/back")) {
            e.setCancelled(true);
            TPortCommand.executeInternal(e.getPlayer(), "back");
            if (Redirect.Redirects.ConsoleFeedback.isEnabled()) {
                Main.getInstance().getLogger().log(Level.INFO, "Redirected the command '" + e.getMessage() + "' to '/tport back'");
            }
        }
        
    }
}
