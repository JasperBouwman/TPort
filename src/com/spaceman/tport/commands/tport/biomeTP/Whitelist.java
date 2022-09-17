package com.spaceman.tport.commands.tport.biomeTP;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.BiomeTP;
import com.spaceman.tport.cooldown.CooldownManager;
import com.spaceman.tport.fancyMessage.encapsulation.BiomeEncapsulation;
import org.bukkit.entity.Player;

import java.util.*;

import static com.spaceman.tport.commands.tport.BiomeTP.biomeTP;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatInfoTranslation;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;

public class Whitelist extends SubCommand {
    
    public Whitelist() {
        EmptyCommand emptyWhitelist = new EmptyCommand();
        emptyWhitelist.setCommandName("biome", ArgumentType.REQUIRED);
        emptyWhitelist.setCommandDescription(formatInfoTranslation("tport.command.biomeTP.whitelist.biome.commandDescription"));
        emptyWhitelist.setTabRunnable(((args, player) -> {
            if (!hasPermission(player, false, true, "TPort.biomeTP.whitelist")) {
                return Collections.emptyList();
            }
            List<String> biomeList = Arrays.asList(args).subList(2, args.length).stream().map(String::toLowerCase).toList();
            return BiomeTP.availableBiomes(player.getWorld()).stream().filter(name -> biomeList.stream().noneMatch(name::equals)).toList();
        }));
        emptyWhitelist.setLooped(true);
        emptyWhitelist.setPermissions("TPort.biomeTP.whitelist", "TPort.biomeTP.biome.<biome...>");
        emptyWhitelist.permissionsOR(false);
        addAction(emptyWhitelist);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        if (!hasPermission(player, false, true, "TPort.biomeTP.whitelist")) {
            return Collections.emptyList();
        }
        List<String> biomeList = Arrays.asList(args).subList(2, args.length).stream().map(String::toLowerCase).toList();
        return BiomeTP.availableBiomes(player.getWorld()).stream().filter(name -> biomeList.stream().noneMatch(name::equals)).toList();
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport biomeTP whitelist <biome...>
        
        if (args.length == 2) {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport biomeTP whitelist <biome...>");
        } else {
            if (!hasPermission(player, true, true, "TPort.biomeTP.whitelist")) {
                return;
            }
            if (!CooldownManager.BiomeTP.hasCooled(player)) {
                return;
            }
            
            List<String> possibleBiomes = BiomeTP.availableBiomes(player.getWorld());
            List<String> whitelist = new ArrayList<>();
            for (int i = 2; i < args.length; i++) {
                String biomeName = args[i].toLowerCase();
                
                if (!possibleBiomes.contains(biomeName)) {
                    sendErrorTranslation(player, "tport.command.biomeTP.whitelist.biome.worldNotGenerateBiome", biomeName);
                    continue;
                }
                
                if (!hasPermission(player, true, true, "TPort.biomeTP.biome." + biomeName)) {
                    continue;
                }
                
                if (whitelist.contains(biomeName)) {
                    sendErrorTranslation(player, "tport.command.biomeTP.blacklist.biome.biomeAlreadyInList", new BiomeEncapsulation(biomeName));
                    continue;
                }
                
                whitelist.add(biomeName);
            }
            if (whitelist.isEmpty()) {
                sendErrorTranslation(player, "tport.command.biomeTP.whitelist.biome.noBiomesLeft");
                return;
            }
            
            biomeTP(player, Mode.getDefMode(player.getUniqueId()), whitelist);
        }
    }
}
