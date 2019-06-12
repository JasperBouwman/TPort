package com.spaceman.tport.commands.tport;

import com.spaceman.tport.Permissions;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.cooldown.CooldownManager;
import org.bukkit.ChatColor;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.spaceman.tport.TPortInventories.openBiomeTP;
import static com.spaceman.tport.events.InventoryClick.teleportPlayer;

public class BiomeTP extends SubCommand {

    public static void biomeTP(Player player, Biome biome) {

        Random random = new Random();
        int x = random.nextInt(6000000) - 3000000;
        int z = random.nextInt(6000000) - 3000000;
        Block b = player.getWorld().getBlockAt(x, 64, z);

        for (int i = 0; i < 100; i++) {
            if (b.getBiome().equals(biome)) {
//                player.teleport(player.getWorld().getHighestBlockAt(x, z).getLocation().add(0.5, 0, 0.5));
                teleportPlayer(player, player.getWorld().getHighestBlockAt(x, z).getLocation().add(0.5, 0, 0.5));
                player.sendMessage("§3Teleport to biome: " + b.getBiome());
                CooldownManager.BiomeTP.update(player);
                return;
            } else {
                x = random.nextInt(6000000) - 3000000;
                z = random.nextInt(6000000) - 3000000;
                b = player.getWorld().getBlockAt(x, 64, z);
            }
        }
        player.sendMessage(ChatColor.RED + "Could not find biome " + biome + " in 100 tries, try again");

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
            if (!Permissions.hasPermission(player, "TPort.command.biomeTP")) {
                return;
            }
            openBiomeTP(player, 0);
        } else {

            if (args[1].equalsIgnoreCase("random")) {
                if (!Permissions.hasPermission(player, "TPort.command.biomeTP")) {
                    return;
                }
                long cooldown = CooldownManager.BiomeTP.getTime(player);
                if (cooldown / 1000 > 0) {
                    player.sendMessage(ChatColor.RED + "You must wait another " + (cooldown / 1000) + " second" + ((cooldown / 1000) == 1 ? "" : "s") + " to use this again");
                    return;
                }
                Random random = new Random();
                int x = random.nextInt(6000000) - 3000000;
                int z = random.nextInt(6000000) - 3000000;
                teleportPlayer(player, player.getWorld().getHighestBlockAt(x, z).getLocation().add(0.5, 0, 0.5));
                player.sendMessage("§3Teleported to a random location");
                CooldownManager.BiomeTP.update(player);
                return;
            }

            Biome biome;
            try {
                biome = Biome.valueOf(args[1].toUpperCase());
            } catch (IllegalArgumentException iae) {
                player.sendMessage(ChatColor.RED + "Biome " + args[1].toUpperCase() + " does not exist");
                return;
            }

            if (!Permissions.hasPermission(player, "TPort.command.biomeTP." + biome)) {
                return;
            }

            long cooldown = CooldownManager.BiomeTP.getTime(player);
            if (cooldown / 1000 > 0) {
                player.sendMessage(ChatColor.RED + "You must wait another " + (cooldown / 1000) + " second" + ((cooldown / 1000) == 1 ? "" : "s") + " to use this again");
                return;
            }
            biomeTP(player, biome);
        }

    }
}
