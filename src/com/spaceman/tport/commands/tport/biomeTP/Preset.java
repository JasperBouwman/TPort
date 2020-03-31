package com.spaceman.tport.commands.tport.biomeTP;

import com.spaceman.tport.TPortInventories;
import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.commands.tport.BiomeTP;
import com.spaceman.tport.fancyMessage.Message;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.spaceman.tport.colorFormatter.ColorTheme.ColorType.infoColor;
import static com.spaceman.tport.colorFormatter.ColorTheme.ColorType.varInfoColor;
import static com.spaceman.tport.colorFormatter.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.commands.TPortCommand.executeInternal;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;

public class Preset extends SubCommand {
    
    public Preset() {
        EmptyCommand emptyPreset = new EmptyCommand();
        emptyPreset.setCommandName("preset", ArgumentType.OPTIONAL);
        emptyPreset.setCommandDescription(textComponent("This command is used to use a biomeTP preset", infoColor),
                textComponent("\n\nPermissions: (", infoColor), textComponent("TPort.biomeTP.preset", varInfoColor),
                textComponent(" and ", infoColor), textComponent("permissions of '/tport biomeTP whitelist/blacklist <biome...>'", varInfoColor),
                textComponent(") or ", infoColor), textComponent("TPort.biomeTP.all", varInfoColor));
        
        addAction(emptyPreset);
    }
    
    @Override
    public Message getCommandDescription() {
        return new Message(textComponent("This command is used to open the biomeTP preset list GUI", infoColor),
                textComponent("\n\nPermissions: ", infoColor), textComponent("TPort.biomeTP.preset", varInfoColor),
                textComponent(" or ", infoColor), textComponent("TPort.biomeTP.all", varInfoColor));
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return BiomeTP.BiomeTPPresets.getNames();
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport biomeTP preset [preset]
        
        if (args.length == 2) {
            if (hasPermission(player, true, true, "TPort.biomeTP.preset", "TPort.biomeTP.all")) {
                TPortInventories.openBiomeTPPreset(player, 0);
            }
        } else if (args.length == 3) {
            if (!hasPermission(player, true, true, "TPort.biomeTP.preset", "TPort.biomeTP.all")) {
                return;
            }
            BiomeTP.BiomeTPPresets.Preset preset = BiomeTP.BiomeTPPresets.getPreset(args[2]);
            if (preset != null) {
                List<String> command = new ArrayList<>(Arrays.asList("biomeTP", preset.isWhitelist() ? "whitelist" : "blacklist"));
                preset.getBiomes().stream().map(Enum::name).forEach(command::add);
                executeInternal(player, command.toArray(new String[0]));
            } else {
                sendErrorTheme(player, "Preset %s does not exist", args[2]);
            }
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport biomeTP preset [preset]");
        }
    }
}
