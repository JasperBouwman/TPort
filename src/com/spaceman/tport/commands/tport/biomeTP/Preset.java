package com.spaceman.tport.commands.tport.biomeTP;

import com.spaceman.tport.TPortInventories;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.BiomeTP;
import com.spaceman.tport.fancyMessage.Message;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.spaceman.tport.commands.TPortCommand.executeInternal;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatInfoTranslation;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;

public class Preset extends SubCommand {
    
    public Preset() {
        setPermissions("TPort.biomeTP.preset");
        
        EmptyCommand emptyPreset = new EmptyCommand();
        emptyPreset.setCommandName("preset", ArgumentType.OPTIONAL);
        emptyPreset.setCommandDescription(formatInfoTranslation("tport.command.biomeTP.preset.preset.commandDescription", "/tport biomeTP whitelist/blacklist <biome...>"));
        emptyPreset.setPermissions(getPermissions());
        
        addAction(emptyPreset);
    }
    
    @Override
    public Message getCommandDescription() {
        return formatInfoTranslation("tport.command.biomeTP.preset.commandDescription");
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return BiomeTP.BiomeTPPresets.getNames();
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
            BiomeTP.BiomeTPPresets.BiomePreset preset = BiomeTP.BiomeTPPresets.getPreset(args[2], player.getWorld());
            if (preset != null) {
                List<String> command = new ArrayList<>(Arrays.asList("biomeTP", preset.whitelist() ? "whitelist" : "blacklist"));
                command.addAll(preset.biomes());
                executeInternal(player, command.toArray(new String[0]));
            } else {
                sendErrorTranslation(player, "tport.command.biomeTP.preset.presetNotExist", args[2]);
            }
        } else {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport biomeTP preset [preset]");
        }
    }
}
