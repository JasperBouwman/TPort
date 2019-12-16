package com.spaceman.tport.commands.tport;

import com.spaceman.tport.colorFormatter.ColorTheme;
import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.cooldown.CooldownManager;
import com.spaceman.tport.fancyMessage.Message;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.spaceman.tport.TPortInventories.openBiomeTP;
import static com.spaceman.tport.colorFormatter.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.colorFormatter.ColorTheme.sendSuccessTheme;
import static com.spaceman.tport.events.InventoryClick.requestTeleportPlayer;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;

public class BiomeTP extends SubCommand {
    
    public BiomeTP() {
        EmptyCommand emptyBiome = new EmptyCommand();
        emptyBiome.setCommandName("biome", ArgumentType.OPTIONAL);
        emptyBiome.setCommandDescription(textComponent("This command is used to teleport to the given biome", ColorTheme.ColorType.infoColor),
                textComponent("\n\nPermissions: ", ColorTheme.ColorType.infoColor), textComponent("TPort.biomeTP.<biome>", ColorTheme.ColorType.varInfoColor),
                textComponent(" or ", ColorTheme.ColorType.infoColor), textComponent("TPort.biomeTP.all", ColorTheme.ColorType.varInfoColor));
        EmptyCommand emptyRandom = new EmptyCommand();
        emptyRandom.setCommandName("random", ArgumentType.OPTIONAL);
        emptyRandom.setCommandDescription(textComponent("This command is used to teleport to a random biome", ColorTheme.ColorType.infoColor),
                textComponent("\n\nPermissions: ", ColorTheme.ColorType.infoColor), textComponent("TPort.biomeTP.random", ColorTheme.ColorType.varInfoColor),
                textComponent(" or ", ColorTheme.ColorType.infoColor), textComponent("TPort.biomeTP.all", ColorTheme.ColorType.varInfoColor));
        addAction(emptyBiome);
        addAction(emptyRandom);
    }
    
    public static int biomeSearches = 100;

    private static void biomeTP(Player player, Biome biome) {

        Random random = new Random();
        int x = random.nextInt(6000000) - 3000000;
        int z = random.nextInt(6000000) - 3000000;
        Block b = player.getWorld().getBlockAt(x, 64, z);

        for (int i = 0; i < biomeSearches; i++) {
            if (b.getBiome().equals(biome)) {
                requestTeleportPlayer(player, player.getWorld().getHighestBlockAt(x, z).getLocation().add(0.5, 0.1, 0.5));
                if (Delay.delayTime(player) == 0) {
                    sendSuccessTheme(player, "Successfully teleported to biome %s", b.getBiome().name());
                } else {
                    sendSuccessTheme(player, "Successfully requested teleportation to biome %s", b.getBiome().name());
                }
                CooldownManager.BiomeTP.update(player);
                return;
            } else {
                x = random.nextInt(6000000) - 3000000;
                z = random.nextInt(6000000) - 3000000;
                b = player.getWorld().getBlockAt(x, 64, z);
            }
        }
        sendErrorTheme(player, "Could not find the biome %s in a %s tries, you can try again", biome.name(), String.valueOf(biomeSearches));
    }
    
    @Override
    public Message getCommandDescription() {
        return new Message(textComponent("This command is used to open the BiomeTP GUI", ColorTheme.ColorType.infoColor),
                textComponent("\n\nPermission: ", ColorTheme.ColorType.infoColor), textComponent("TPort.biomeTP.open", ColorTheme.ColorType.varInfoColor));
    }
    
    @Override
    public List<String> tabList(Player player, String[] args) {
        ArrayList<String> list = new ArrayList<>();
        for (Biome biome : Biome.values()) {
            list.add(biome.name());
        }
        list.add("random");
        return list;
    }

    @Override
    public void run(String[] args, Player player) {
        //tport biomeTP [biome]

        if (args.length == 1) {
            if (!hasPermission(player, "TPort.biomeTP.open")) {
                return;
            }
            openBiomeTP(player, 0);
        } else {

            if (args[1].equalsIgnoreCase("random")) {
                if (!hasPermission(player, "TPort.biomeTP.random", "TPort.biomeTP.all")) {
                    return;
                }
                if (!CooldownManager.BiomeTP.hasCooled(player)) {
                    return;
                }
                Random random = new Random();
                int x = random.nextInt(6000000) - 3000000;
                int z = random.nextInt(6000000) - 3000000;
                requestTeleportPlayer(player, player.getWorld().getHighestBlockAt(x, z).getLocation().add(0.5, 0, 0.5));
                if (Delay.delayTime(player) == 0) {
                    sendSuccessTheme(player, "Successfully teleported to a random location");
                } else {
                    sendSuccessTheme(player, "Successfully requested teleportation to a random location");
                }
                CooldownManager.BiomeTP.update(player);
                return;
            }

            Biome biome;
            try {
                biome = Biome.valueOf(args[1].toUpperCase());
            } catch (IllegalArgumentException iae) {
                sendErrorTheme(player, "Biome %s does not exist", args[1].toUpperCase());
                return;
            }

            if (!hasPermission(player, "TPort.biomeTP." + biome, "TPort.biomeTP.all")) {
                return;
            }
            if (!CooldownManager.BiomeTP.hasCooled(player)) {
                return;
            }
            biomeTP(player, biome);
        }

    }
}
