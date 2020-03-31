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

public class Blacklist extends SubCommand {
    
    public Blacklist() {
        EmptyCommand emptyBlacklist = new EmptyCommand();
        emptyBlacklist.setCommandName("blacklist", ArgumentType.REQUIRED);
        emptyBlacklist.setCommandDescription(textComponent("This command is used to teleport to a random biome in the given blacklist", infoColor),
                textComponent("\n\nPermissions: (", infoColor), textComponent("TPort.biomeTP.blacklist", varInfoColor),
                textComponent(" and ", infoColor), textComponent("TPort.biomeTP.biome.<biome...>", varInfoColor),
                textComponent(") or ", infoColor), textComponent("TPort.biomeTP.all", varInfoColor));
        emptyBlacklist.setTabRunnable(((args, player) -> {
            List<String> biomeList = Arrays.asList(args).subList(2, args.length).stream().map(String::toUpperCase).collect(Collectors.toList());
            return Arrays.stream(Biome.values()).filter(biome -> !biomeList.contains(biome.name())).map(Enum::name).collect(Collectors.toList());
        }));
        emptyBlacklist.setLooped(true);
        addAction(emptyBlacklist);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        List<String> biomeList = Arrays.asList(args).subList(2, args.length).stream().map(String::toUpperCase).collect(Collectors.toList());
        return Arrays.stream(Biome.values()).filter(biome -> !biomeList.contains(biome.name())).map(Enum::name).collect(Collectors.toList());
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport biomeTP blacklist <biome...>
        
        if (args.length == 2) {
            sendErrorTheme(player, "Usage: %s", "/tport biomeTP blacklist <biome...>");
        } else {
            if (!hasPermission(player, true, true, "TPort.biomeTP.blacklist", "TPort.biomeTP.all")) {
                return;
            }
            if (!CooldownManager.BiomeTP.hasCooled(player)) {
                return;
            }

            List<Biome> blacklist = new ArrayList<>();
            for (int i = 2; i < args.length; i++) {
                String biomeName = args[i].toUpperCase();
                Biome biome;
                try {
                    biome = Biome.valueOf(biomeName);
                } catch (IllegalArgumentException iae) {
                    sendErrorTheme(player, "Biome %s does not exist", biomeName);
                    return;
                }

                blacklist.add(biome);
            }

            List<Biome> newList = new ArrayList<>();
            for (Biome biome : Biome.values()) {
                if (!blacklist.contains(biome)) {
                    if (!hasPermission(player, true, true, "TPort.biomeTP.biome." + biome.name(), "TPort.biomeTP.all")) {
                        return;
                    }
                    newList.add(biome);
                }
            }

            biomeTP(player, newList);
        }
    }
}
