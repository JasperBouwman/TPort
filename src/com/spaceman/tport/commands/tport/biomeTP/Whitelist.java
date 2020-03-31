package com.spaceman.tport.commands.tport.biomeTP;

import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.cooldown.CooldownManager;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.spaceman.tport.colorFormatter.ColorTheme.ColorType.infoColor;
import static com.spaceman.tport.colorFormatter.ColorTheme.ColorType.varInfoColor;
import static com.spaceman.tport.colorFormatter.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.commands.tport.BiomeTP.biomeTP;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;

public class Whitelist extends SubCommand {
    
    public Whitelist() {
        EmptyCommand emptyWhitelist = new EmptyCommand();
        emptyWhitelist.setCommandName("whitelist", ArgumentType.REQUIRED);
        emptyWhitelist.setCommandDescription(textComponent("This command is used to teleport to a random biome in the given whitelist", infoColor),
                textComponent("\n\nPermissions: (", infoColor), textComponent("TPort.biomeTP.whitelist", varInfoColor),
                textComponent(" and ", infoColor), textComponent("TPort.biomeTP.biome.<biome...>", varInfoColor),
                textComponent(") or ", infoColor), textComponent("TPort.biomeTP.all", varInfoColor));
        emptyWhitelist.setTabRunnable(((args, player) -> {
            List<String> biomeList = Arrays.asList(args).subList(2, args.length).stream().map(String::toUpperCase).collect(Collectors.toList());
            return Arrays.stream(Biome.values()).filter(biome -> !biomeList.contains(biome.name())).map(Enum::name).collect(Collectors.toList());
        }));
        emptyWhitelist.setLooped(true);
        addAction(emptyWhitelist);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        List<String> biomeList = Arrays.asList(args).subList(2, args.length).stream().map(String::toUpperCase).collect(Collectors.toList());
        return Arrays.stream(Biome.values()).filter(biome -> !biomeList.contains(biome.name())).map(Enum::name).collect(Collectors.toList());
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport biomeTP whitelist <biome...>
        
        if (args.length == 2) {
            sendErrorTheme(player, "Usage: %s", "/tport biomeTP whitelist <biome...>");
        } else {
            if (!hasPermission(player, true, true, "TPort.biomeTP.whitelist", "TPort.biomeTP.all")) {
                return;
            }
            if (!CooldownManager.BiomeTP.hasCooled(player)) {
                return;
            }
            
            List<Biome> whitelist = new ArrayList<>();
            for (int i = 2; i < args.length; i++) {
                
                String biomeName = args[i].toUpperCase();
                Biome biome;
                try {
                    biome = Biome.valueOf(biomeName);
                } catch (IllegalArgumentException iae) {
                    sendErrorTheme(player, "Biome %s does not exist", biomeName);
                    return;
                }
                
                if (!hasPermission(player, true, true, "TPort.biomeTP.biome." + biome.name(), "TPort.biomeTP.all")) {
                    return;
                }
                whitelist.add(biome);
            }
            biomeTP(player, whitelist);
        }
    }
}
