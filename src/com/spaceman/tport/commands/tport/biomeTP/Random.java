package com.spaceman.tport.commands.tport.biomeTP;

import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.commands.tport.BiomeTP;
import com.spaceman.tport.cooldown.CooldownManager;
import com.spaceman.tport.fancyMessage.Message;
import org.bukkit.entity.Player;

import java.util.Collections;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.infoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;

public class Random extends SubCommand {
    
    public Random() {
        setPermissions("TPort.biomeTP.random");
    }
    
    @Override
    public Message getCommandDescription() {
        return new Message(textComponent("This command is used to teleport to a random biome", infoColor));
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
    
            BiomeTP.biomeTP(player, Collections.emptyList(), true);
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport biomeTP random");
        }
    }
}
