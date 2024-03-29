package com.spaceman.tport.events;

import com.spaceman.tport.Main;
import com.spaceman.tport.commands.TPortCommand;
import com.spaceman.tport.commands.tport.FeatureTP;
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
            TPortCommand.executeTPortCommand(e.getPlayer(), "PLTP tp" + e.getMessage().substring(3));
            if (Redirect.Redirects.ConsoleFeedback.isEnabled()) {
                Main.getInstance().getLogger().log(Level.INFO, "Redirected the command '" + e.getMessage() + "' to '/tport " + "PLTP tp" + e.getMessage().substring(3) + "'");
            }
        }
        
        if (Redirect.Redirects.Locate_FeatureTP.isEnabled()) {
            if (e.getMessage().matches("/locate structure .+")) {
                e.setCancelled(true);
                String structureQuery = e.getMessage().substring(18);
                TPortCommand.executeTPortCommand(e.getPlayer(), "FeatureTP search " + structureQuery);
                if (Redirect.Redirects.ConsoleFeedback.isEnabled()) {
                    Main.getInstance().getLogger().log(Level.INFO, "Redirected the command '" + e.getMessage() + "' to '/tport " + "FeatureTP search " + structureQuery + "'");
                }
            } else if (e.getMessage().matches("/locate .+")) {
                String possibleStructure = e.getMessage().substring(8);
                if (possibleStructure.startsWith("#")) possibleStructure = possibleStructure.substring(1);
                if (possibleStructure.startsWith("minecraft:")) possibleStructure = possibleStructure.substring(10);
                
                for (String structure : FeatureTP.getFeatures()) {
                    if (structure.startsWith("#")) structure = structure.substring(1);
                    if (structure.startsWith("minecraft:")) structure = structure.substring(10);
                    
                    if (structure.equalsIgnoreCase(possibleStructure)) {
                        e.setCancelled(true);
                        TPortCommand.executeTPortCommand(e.getPlayer(), "FeatureTP search " + possibleStructure);
                        if (Redirect.Redirects.ConsoleFeedback.isEnabled()) {
                            Main.getInstance().getLogger().log(Level.INFO, "Redirected the command '" + e.getMessage() + "' to '/tport " + "FeatureTP search " + possibleStructure + "'");
                        }
                        break;
                    }
                }
            }
        }
        
        if (Redirect.Redirects.LocateBiome_BiomeTP.isEnabled()) {
            if (e.getMessage().matches("/locate biome .+") || e.getMessage().matches("/locatebiome .+")) {
                e.setCancelled(true);
                String biomeQuery = e.getMessage().substring(14);
                if (biomeQuery.startsWith("#")) {
                    TPortCommand.executeTPortCommand(e.getPlayer(), "BiomeTP preset " + biomeQuery);
                    if (Redirect.Redirects.ConsoleFeedback.isEnabled()) {
                        Main.getInstance().getLogger().log(Level.INFO, "Redirected the command '" + e.getMessage() + "' to '/tport " + "BiomeTP preset " + biomeQuery + "'");
                    }
                } else {
                    TPortCommand.executeTPortCommand(e.getPlayer(), "BiomeTP whitelist " + biomeQuery);
                    if (Redirect.Redirects.ConsoleFeedback.isEnabled()) {
                        Main.getInstance().getLogger().log(Level.INFO, "Redirected the command '" + e.getMessage() + "' to '/tport " + "BiomeTP whitelist " + biomeQuery + "'");
                    }
                }
            }
        }
        
        if (Redirect.Redirects.Home_TPortHome.isEnabled() && e.getMessage().matches("/home")) {
            e.setCancelled(true);
            TPortCommand.executeTPortCommand(e.getPlayer(), "home");
            if (Redirect.Redirects.ConsoleFeedback.isEnabled()) {
                Main.getInstance().getLogger().log(Level.INFO, "Redirected the command '" + e.getMessage() + "' to '/tport home'");
            }
        }
        
        if (Redirect.Redirects.Back_TPortBack.isEnabled() && e.getMessage().matches("/back")) {
            e.setCancelled(true);
            TPortCommand.executeTPortCommand(e.getPlayer(), "back");
            if (Redirect.Redirects.ConsoleFeedback.isEnabled()) {
                Main.getInstance().getLogger().log(Level.INFO, "Redirected the command '" + e.getMessage() + "' to '/tport back'");
            }
        }
        
    }
}
