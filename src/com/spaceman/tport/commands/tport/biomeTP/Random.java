package com.spaceman.tport.commands.tport.biomeTP;

import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.BiomeTP;
import com.spaceman.tport.cooldown.CooldownManager;
import com.spaceman.tport.fancyMessage.Message;
import org.bukkit.entity.Player;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatInfoTranslation;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;

public class Random extends SubCommand {
    
    public Random() {
        setPermissions("TPort.biomeTP.random");
    }
    
    @Override
    public Message getCommandDescription() {
        return formatInfoTranslation("tport.command.biomeTP.random.commandDescription");
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport biomeTP random
        
        if (args.length == 2) {
            if (!hasPermissionToRun(player, true)) {
                return;
            }
            if (!CooldownManager.BiomeTP.hasCooled(player)) {
                return;
            }
            
            BiomeTP.randomTP(player);
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport biomeTP random");
        }
    }
}
