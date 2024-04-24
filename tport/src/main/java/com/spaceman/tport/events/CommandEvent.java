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
        if (Redirect.Redirects.TP_PLTP.isEnabled() && e.getMessage().matches("(?i)/tp [A-Z_0-9]{3,16}")) {
            e.setCancelled(true);
            TPortCommand.executeTPortCommand(e.getPlayer(), "PLTP tp " + e.getMessage().substring(4));
            if (Redirect.Redirects.ConsoleFeedback.isEnabled()) {
                Main.getInstance().getLogger().log(Level.INFO, "Redirected the command '" + e.getMessage() + "' to '/tport " + "PLTP tp" + e.getMessage().substring(3) + "'");
            }
        }
        
        if (Redirect.Redirects.Locate_FeatureTP.isEnabled()) {
            if (e.getMessage().matches("(?i)/locate structure .+")) {
                e.setCancelled(true);
                String structureQuery = e.getMessage().substring(18);
                TPortCommand.executeTPortCommand(e.getPlayer(), "FeatureTP search " + structureQuery);
                if (Redirect.Redirects.ConsoleFeedback.isEnabled()) {
                    Main.getInstance().getLogger().log(Level.INFO, "Redirected the command '" + e.getMessage() + "' to '/tport " + "FeatureTP search " + structureQuery + "'");
                }
            } else if (e.getMessage().matches("(?i)/locate .+")) {
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
            String biomeQuery = null;
            if (e.getMessage().matches("(?i)/locate biome .+")) {
                e.setCancelled(true);
                biomeQuery = e.getMessage().substring(14);
            } else if (e.getMessage().matches("(?i)/locatebiome .+")) {
                e.setCancelled(true);
                biomeQuery = e.getMessage().substring(13);
            }
            
            if (biomeQuery != null) {
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
        
        if (Redirect.Redirects.Home_TPortHome.isEnabled() && e.getMessage().equalsIgnoreCase("/home")) {
            e.setCancelled(true);
            TPortCommand.executeTPortCommand(e.getPlayer(), "home");
            if (Redirect.Redirects.ConsoleFeedback.isEnabled()) {
                Main.getInstance().getLogger().log(Level.INFO, "Redirected the command '" + e.getMessage() + "' to '/tport home'");
            }
        }
        
        if (Redirect.Redirects.Back_TPortBack.isEnabled() && e.getMessage().equalsIgnoreCase("/back")) {
            e.setCancelled(true);
            TPortCommand.executeTPortCommand(e.getPlayer(), "back");
            if (Redirect.Redirects.ConsoleFeedback.isEnabled()) {
                Main.getInstance().getLogger().log(Level.INFO, "Redirected the command '" + e.getMessage() + "' to '/tport back'");
            }
        }
        
        if (Redirect.Redirects.TPA_PLTP_TP.isEnabled() && e.getMessage().matches("(?i)/tpa [A-Z_0-9]{3,16}")) {
            e.setCancelled(true);
            TPortCommand.executeTPortCommand(e.getPlayer(), "PLTP tp " + e.getMessage().substring(5));
            if (Redirect.Redirects.ConsoleFeedback.isEnabled()) {
                Main.getInstance().getLogger().log(Level.INFO, "Redirected the command '" + e.getMessage() + "' to '/tport " + "PLTP tp " + e.getMessage().substring(5) + "'");
            }
        }
        if (Redirect.Redirects.TPAccept_Requests_accept.isEnabled())
            if (e.getMessage().matches("(?i)/tpaccept [A-Z_0-9]{3,16}")) {
                e.setCancelled(true);
                TPortCommand.executeTPortCommand(e.getPlayer(), "requests accept " + e.getMessage().substring(10));
                if (Redirect.Redirects.ConsoleFeedback.isEnabled()) {
                    Main.getInstance().getLogger().log(Level.INFO, "Redirected the command '" + e.getMessage() + "' to '/tport requests accept " + e.getMessage().substring(10) + "'");
                }
            } else if (e.getMessage().equalsIgnoreCase("/tpaccept")) {
                e.setCancelled(true);
                TPortCommand.executeTPortCommand(e.getPlayer(), "requests accept");
                if (Redirect.Redirects.ConsoleFeedback.isEnabled()) {
                    Main.getInstance().getLogger().log(Level.INFO, "Redirected the command '" + e.getMessage() + "' to '/tport request accept'");
                }
            }
        if (Redirect.Redirects.TPDeny_Requests_reject.isEnabled()) {
            if (e.getMessage().matches("(?i)/tpdeny [A-Z_0-9]{3,16}")) {
                e.setCancelled(true);
                TPortCommand.executeTPortCommand(e.getPlayer(), "requests reject " + e.getMessage().substring(8));
                if (Redirect.Redirects.ConsoleFeedback.isEnabled()) {
                    Main.getInstance().getLogger().log(Level.INFO, "Redirected the command '" + e.getMessage() + "' to '/tport reject reject " + e.getMessage().substring(8) + "'");
                }
            } else if (e.getMessage().equalsIgnoreCase("/tpdeny")) {
                e.setCancelled(true);
                TPortCommand.executeTPortCommand(e.getPlayer(), "requests reject");
                if (Redirect.Redirects.ConsoleFeedback.isEnabled()) {
                    Main.getInstance().getLogger().log(Level.INFO, "Redirected the command '" + e.getMessage() + "' to '/tport request reject'");
                }
            }
        }
        if (Redirect.Redirects.TPRevoke_Requests_revoke.isEnabled() && e.getMessage().equalsIgnoreCase("/tprevoke")) {
            e.setCancelled(true);
            TPortCommand.executeTPortCommand(e.getPlayer(), "requests revoke");
            if (Redirect.Redirects.ConsoleFeedback.isEnabled()) {
                Main.getInstance().getLogger().log(Level.INFO, "Redirected the command '" + e.getMessage() + "' to '/tport requests revoke'");
            }
        }
        
        if (Redirect.Redirects.TPRandom_BiomeTP_random.isEnabled())
            if (e.getMessage().equalsIgnoreCase("/tprandom") || e.getMessage().equalsIgnoreCase("/randomtp") || e.getMessage().equalsIgnoreCase("/rtp")) {
                e.setCancelled(true);
                TPortCommand.executeTPortCommand(e.getPlayer(), "biomeTP random");
                if (Redirect.Redirects.ConsoleFeedback.isEnabled()) {
                    Main.getInstance().getLogger().log(Level.INFO, "Redirected the command '" + e.getMessage() + "' to '/tport biomeTP random'");
                }
            }
        
    }
}
