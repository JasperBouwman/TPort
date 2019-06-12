package com.spaceman.tport.commands.tport;

import com.spaceman.tport.Permissions;
import com.spaceman.tport.TPortInventories;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.cooldown.CooldownManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.spaceman.tport.TPortInventories.openFeatureTP;
import static com.spaceman.tport.events.InventoryClick.teleportPlayer;

public class FeatureTP extends SubCommand {

    @Override
    public List<String> tabList(Player player, String[] args) {
        ArrayList<String> list = new ArrayList<>();
        Arrays.stream(TPortInventories.FeatureTypes.values()).map(TPortInventories.FeatureTypes::name).forEach(list::add);
//        for (TPortInventories.FeatureTypes feature : TPortInventories.FeatureTypes.values()) {
//            list.add(feature.name());
//        }
        return list;
    }

    public static void featureTP(Player player, TPortInventories.FeatureTypes featuresType) {
        Location featureLoc = featureFinder(player.getLocation(), featuresType);
        if (featureLoc == null) {
            player.sendMessage(ChatColor.RED + "Could not find a " + ChatColor.DARK_RED + featuresType.name() + ChatColor.RED + " nearby");
        } else {
            featureLoc.setY(player.getWorld().getHighestBlockYAt(featureLoc.getBlockX(), featureLoc.getBlockZ()));
            teleportPlayer(player, featureLoc);
            CooldownManager.FeatureTP.update(player);
        }
    }

    private static Location featureFinder(Location startLocation, TPortInventories.FeatureTypes feature) {
        try {
            String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
            Object nmsWorld = startLocation.getWorld().getClass().getMethod("getHandle").invoke(startLocation.getWorld());
            Object blockPos = Class.forName("net.minecraft.server." + version + ".BlockPosition").getConstructor(int.class, int.class, int.class)
                    .newInstance(startLocation.getBlockX(), startLocation.getBlockY(), startLocation.getBlockZ());
            Object finalBlockPos = nmsWorld.getClass().getMethod("a", String.class, blockPos.getClass(), int.class, boolean.class).invoke(nmsWorld, feature.name(), blockPos, 100, false);
            Object x = finalBlockPos.getClass().getMethod("getX").invoke(finalBlockPos);
            Object z = finalBlockPos.getClass().getMethod("getZ").invoke(finalBlockPos);
            return new Location(startLocation.getWorld(), (int) x, 0, (int) z);

        } catch (Exception ignore) {
            return null;
        }
    }

    @Override
    public void run(String[] args, Player player) {
        //tport featureTP [featureType]


        if (args.length == 1) {
            if (!Permissions.hasPermission(player, "TPort.command.featureTP")) {
                return;
            }
            openFeatureTP(player, 0);
        } else {
            TPortInventories.FeatureTypes featuresType;
            try {
                featuresType = TPortInventories.FeatureTypes.valueOf(args[1]);
            } catch (IllegalArgumentException iae) {
                player.sendMessage(ChatColor.RED + "Feature " + args[1] + " does not exist");
                return;
            }

            if (!Permissions.hasPermission(player, "TPort.command.featureTP." + featuresType.name())) {
                return;
            }
            long cooldown = CooldownManager.FeatureTP.getTime(player);
            if (cooldown / 1000 > 0) {
                player.sendMessage(ChatColor.RED + "You must wait another " + (cooldown / 1000) + " second" + ((cooldown / 1000) == 1 ? "" : "s") + " to use this again");
                return;
            }

            featureTP(player, featuresType);
        }
    }

}
