package com.spaceman.tport.commands.tport.biomeTP;

import com.spaceman.tport.biomeTP.BiomePreset;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.TPortCommand;
import com.spaceman.tport.commands.tport.BiomeTP;
import com.spaceman.tport.inventories.TPortInventories;
import org.bukkit.entity.Player;

import java.util.*;

import static com.spaceman.tport.advancements.TPortAdvancement.Advancement_BiomeTP_OneIsNotEnough;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatInfoTranslation;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;

public class Preset extends SubCommand {
    
    private static final Preset instance = new Preset();
    
    public static Preset getInstance() {
        return instance;
    }
    
    private Preset() {
        setPermissions("TPort.biomeTP.preset");
        
        EmptyCommand emptyPreset = new EmptyCommand();
        emptyPreset.setCommandName("preset", ArgumentType.OPTIONAL);
        emptyPreset.setCommandDescription(formatInfoTranslation("tport.command.biomeTP.preset.preset.commandDescription", "/tport biomeTP whitelist/blacklist <biome...>"));
        emptyPreset.setPermissions(getPermissions());
        
        addAction(emptyPreset);
        
        setCommandDescription(formatInfoTranslation("tport.command.biomeTP.preset.commandDescription"));
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        if (!hasPermissionToRun(player, false)) {
            return Collections.emptyList();
        }
        return BiomePreset.getNames(player.getWorld());
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport biomeTP preset [preset]
        
        if (args.length == 2) {
            if (hasPermissionToRun(player, true)) {
                TPortInventories.openBiomeTPPreset(player, 0, null);
            }
        } else if (args.length == 3) {
            if (!hasPermissionToRun(player, true)) {
                return;
            }
            String biomePreset = args[2];
            if (biomePreset.startsWith("#minecraft:")) biomePreset = "#" + biomePreset.substring(11);
            BiomePreset preset = BiomePreset.getPreset(biomePreset, player.getWorld());
            if (preset == null) {
                sendErrorTranslation(player, "tport.command.biomeTP.preset.presetNotExist", args[2]);
                return;
            }
            List<String> command = new ArrayList<>(Arrays.asList("biomeTP", preset.whitelist() ? "whitelist" : "blacklist"));
            List<String> generatedBiomes = BiomeTP.availableBiomes(player.getWorld());
            for (String biome : preset.biomes()) {
                if (!generatedBiomes.contains(biome)) {
                    sendErrorTranslation(player, "tport.command.biomeTP.preset.biome.worldNotGenerateBiome", biome);
                    continue;
                }
                command.add(biome);
            }
            if (command.size() == 2) {
                sendErrorTranslation(player, "tport.command.biomeTP.preset.biome.noBiomesLeft");
                return;
            }
            TPortCommand.executeTPortCommand(player, command.toArray(new String[0]));
            
            Advancement_BiomeTP_OneIsNotEnough.grant(player);
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport biomeTP preset [preset]");
        }
    }
}
