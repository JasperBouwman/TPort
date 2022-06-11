package com.spaceman.tport.commands.tport.biomeTP;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.BiomeTP;
import com.spaceman.tport.cooldown.CooldownManager;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.encapsulation.BiomeEncapsulation;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.spaceman.tport.commands.tport.BiomeTP.biomeTP;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.infoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;

public class Blacklist extends SubCommand {
    
    public Blacklist() {
        EmptyCommand emptyBlacklist = new EmptyCommand();
        emptyBlacklist.setCommandName("biome", ArgumentType.REQUIRED);
        emptyBlacklist.setCommandDescription(formatInfoTranslation("tport.command.biomeTP.blacklist.biome.commandDescription", infoColor));
        emptyBlacklist.setTabRunnable(((args, player) -> {
            List<String> biomeList = Arrays.asList(args).subList(2, args.length).stream().map(String::toLowerCase).toList();
            return BiomeTP.availableBiomes(player.getWorld()).stream().filter(name -> biomeList.stream().noneMatch(name::equals)).toList();
        }));
        emptyBlacklist.setLooped(true);
        emptyBlacklist.setPermissions("TPort.biomeTP.blacklist", "TPort.biomeTP.biome.<biome...>");
        emptyBlacklist.permissionsOR(false);
        addAction(emptyBlacklist);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        List<String> biomeList = Arrays.asList(args).subList(2, args.length).stream().map(String::toLowerCase).toList();
        return BiomeTP.availableBiomes(player.getWorld()).stream().filter(name -> biomeList.stream().noneMatch(name::equals)).toList();
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport biomeTP blacklist <biome...>
        
        if (args.length == 2) {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport biomeTP blacklist <biome...>");
        } else {
            if (!hasPermission(player, true, true, "TPort.biomeTP.blacklist")) {
                return;
            }
            if (!CooldownManager.BiomeTP.hasCooled(player)) {
                return;
            }
            
            List<String> possibleBiomes = BiomeTP.availableBiomes(player.getWorld());
            List<String> blacklist = new ArrayList<>();
            for (int i = 2; i < args.length; i++) {
                String biomeName = args[i].toLowerCase();
                
                if (!possibleBiomes.contains(biomeName)) {
                    sendErrorTranslation(player, "tport.command.biomeTP.blacklist.biome.worldNotGenerateBiome", biomeName);
                    continue;
                }
                
                if (blacklist.contains(biomeName)) {
                    Message biomeMessage = formatTranslation(varInfoColor, varInfoColor, "%s", new BiomeEncapsulation(biomeName));
                    sendErrorTranslation(player, "tport.command.biomeTP.blacklist.biome.biomeAlreadyInList", biomeMessage);
                    continue;
                }
                
                blacklist.add(biomeName);
            }
            
            List<String> whitelist = new ArrayList<>();
            for (String biome : possibleBiomes) {
                if (!blacklist.contains(biome)) {
                    if (hasPermission(player, true, true, "TPort.biomeTP.biome." + biome)) {
                        whitelist.add(biome);
                    }
                }
            }
            
            if (whitelist.isEmpty()) {
                sendErrorTranslation(player, "tport.command.biomeTP.blacklist.biome.noBiomesLeft");
                return;
            }
            
            biomeTP(player, Mode.getDefMode(player.getUniqueId()), whitelist);
        }
    }
}
