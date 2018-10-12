package com.spaceman.tport.commands.tport;

import com.spaceman.tport.TPortInventories;
import com.spaceman.tport.commands.CmdHandler;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import static com.spaceman.tport.Main.Cooldown.cooldownFeatureTP;
import static com.spaceman.tport.Main.Cooldown.updateFeatureTPCooldown;
import static com.spaceman.tport.TPortInventories.openFeatureTP;
import static com.spaceman.tport.events.InventoryClick.teleportPlayer;

public class FeatureTP extends CmdHandler {

    @Override
    public void run(String[] args, Player player) {
        if (args.length == 1) {
            openFeatureTP(player, 0);
        } else {

            long cooldown = cooldownFeatureTP(player);
            if (cooldown / 1000 > 0) {
                player.sendMessage(ChatColor.RED + "You must wait another " + (cooldown / 1000) + " second" + ((cooldown / 1000) == 1 ? "" : "s") + " to use this again");
                return;
            }

            TPortInventories.FeaturesTypes featuresType;
            try {
                featuresType = TPortInventories.FeaturesTypes.valueOf(args[1]);
            } catch (IllegalArgumentException iae) {
                player.sendMessage(ChatColor.RED + "Feature " + args[1] + " does not exist");
                return;
            }
            featureTP(player, featuresType);
        }
    }

    public static void featureTP(Player player, TPortInventories.FeaturesTypes featuresType) {
        Location featureLoc = featureFinder(player.getLocation(), featuresType);
        if (featureLoc == null) {
            player.sendMessage(ChatColor.RED + "Could not find a " + ChatColor.DARK_RED + featuresType.name() + ChatColor.RED + " nearby");
        } else {
            featureLoc.setY(player.getWorld().getHighestBlockYAt(featureLoc.getBlockX(), featureLoc.getBlockZ()));
            teleportPlayer(player, featureLoc);
            updateFeatureTPCooldown(player);
        }
    }

    private static Location featureFinder(Location startLocation, TPortInventories.FeaturesTypes feature) {
        try {
            Object nmsWorld = startLocation.getWorld().getClass().getMethod("getHandle").invoke(startLocation.getWorld());
            Object blockPos = Class.forName("net.minecraft.server.v1_13_R2.BlockPosition").getConstructor(int.class, int.class, int.class)
                    .newInstance(startLocation.getBlockX(), startLocation.getBlockY(), startLocation.getBlockZ());
            Object finalBlockPos = nmsWorld.getClass().getMethod("a", String.class, blockPos.getClass(), int.class, boolean.class).invoke(nmsWorld, feature.name(), blockPos, 100, false);
            Object x = finalBlockPos.getClass().getMethod("getX").invoke(finalBlockPos);
            Object z = finalBlockPos.getClass().getMethod("getZ").invoke(finalBlockPos);
            return new Location(startLocation.getWorld(), (int) x, 0, (int) z);

        } catch (Exception ignore) {
            return null;
        }
    }

}
