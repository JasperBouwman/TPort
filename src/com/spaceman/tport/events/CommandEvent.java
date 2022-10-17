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
        
        if (Redirect.Redirects.Locate_FeatureTP.isEnabled() && e.getMessage().matches("/locate structure .+")) {
            e.setCancelled(true);
            String structureQuery = e.getMessage().substring(18);
            TPortCommand.executeInternal(e.getPlayer(), "FeatureTP search " + structureQuery);
            if (Redirect.Redirects.ConsoleFeedback.isEnabled()) {
                Main.getInstance().getLogger().log(Level.INFO, "Redirected the command '" + e.getMessage() + "' to '/tport " + "FeatureTP search " + structureQuery + "'");
            }
        }
        
        if (Redirect.Redirects.LocateBiome_BiomeTP.isEnabled() && e.getMessage().matches("/locate biome .+")) {
            e.setCancelled(true);
            String biomeQuery = e.getMessage().substring(14);
            System.out.println(biomeQuery);
            if (biomeQuery.startsWith("#")) {
                TPortCommand.executeInternal(e.getPlayer(), "BiomeTP preset " + biomeQuery);
                if (Redirect.Redirects.ConsoleFeedback.isEnabled()) {
                    Main.getInstance().getLogger().log(Level.INFO, "Redirected the command '" + e.getMessage() + "' to '/tport " + "BiomeTP preset " + biomeQuery + "'");
                }
            } else {
                TPortCommand.executeInternal(e.getPlayer(), "BiomeTP whitelist " + biomeQuery);
                if (Redirect.Redirects.ConsoleFeedback.isEnabled()) {
                    Main.getInstance().getLogger().log(Level.INFO, "Redirected the command '" + e.getMessage() + "' to '/tport " + "BiomeTP whitelist " + biomeQuery + "'");
                }
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
