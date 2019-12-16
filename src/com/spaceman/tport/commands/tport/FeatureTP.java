package com.spaceman.tport.commands.tport;

import com.spaceman.tport.TPortInventories;
import com.spaceman.tport.colorFormatter.ColorTheme;
import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.cooldown.CooldownManager;
import com.spaceman.tport.fancyMessage.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.spaceman.tport.TPortInventories.openFeatureTP;
import static com.spaceman.tport.colorFormatter.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.colorFormatter.ColorTheme.sendSuccessTheme;
import static com.spaceman.tport.events.InventoryClick.requestTeleportPlayer;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;

public class FeatureTP extends SubCommand {
    
    public FeatureTP() {
        EmptyCommand emptyFeature = new EmptyCommand();
        emptyFeature.setCommandName("feature", ArgumentType.OPTIONAL);
        emptyFeature.setCommandDescription(textComponent("This command is used to teleport to the given feature", ColorTheme.ColorType.infoColor),
                textComponent("\n\nPermissions: ", ColorTheme.ColorType.infoColor), textComponent("TPort.featureTP.<feature>", ColorTheme.ColorType.varInfoColor),
                textComponent(" or ", ColorTheme.ColorType.infoColor), textComponent("TPort.featureTP.all", ColorTheme.ColorType.varInfoColor));
        addAction(emptyFeature);
    }
    
    private static void featureTP(Player player, TPortInventories.FeatureType featureType) {
        Location featureLoc = featureFinder(player.getLocation(), featureType);
        if (featureLoc == null) {
            player.sendMessage(ChatColor.RED + "Could not find a " + ChatColor.DARK_RED + featureType.name() + ChatColor.RED + " nearby");
            sendErrorTheme(player, "Could not find the feature %s nearby", featureType.name());
        } else {
            featureLoc.setY(player.getWorld().getHighestBlockYAt(featureLoc.getBlockX(), featureLoc.getBlockZ()));
            requestTeleportPlayer(player, featureLoc);
            if (Delay.delayTime(player) == 0) {
                sendSuccessTheme(player, "Successfully teleported to feature %s", featureType.name());
            } else {
                sendSuccessTheme(player, "Successfully requested teleportation to feature %s", featureType.name());
            }
            CooldownManager.FeatureTP.update(player);
        }
    }
    
    private static Location featureFinder(Location startLocation, TPortInventories.FeatureType feature) {
        try {
            String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
            Object nmsWorld = Objects.requireNonNull(startLocation.getWorld()).getClass().getMethod("getHandle").invoke(startLocation.getWorld());
            Object blockPos = Class.forName("net.minecraft.server." + version + ".BlockPosition").getConstructor(int.class, int.class, int.class)
                    .newInstance(startLocation.getBlockX(), startLocation.getBlockY(), startLocation.getBlockZ());
            Object finalBlockPos = nmsWorld.getClass().getMethod("a", String.class, blockPos.getClass(), int.class, boolean.class)
                    .invoke(nmsWorld, feature.name(), blockPos, 100, false);
            Object x = finalBlockPos.getClass().getMethod("getX").invoke(finalBlockPos);
            Object z = finalBlockPos.getClass().getMethod("getZ").invoke(finalBlockPos);
            return new Location(startLocation.getWorld(), (int) x, 0, (int) z);
        } catch (Exception ignore) {
            return null;
        }
    }
    
    @Override
    public Message getCommandDescription() {
        return new Message(textComponent("This command is used to open the FeatureTP GUI", ColorTheme.ColorType.infoColor),
                textComponent("\n\nPermissions: ", ColorTheme.ColorType.infoColor), textComponent("TPort.featureTP.open", ColorTheme.ColorType.varInfoColor));
    }
    
    @Override
    public List<String> tabList(Player player, String[] args) {
        return Arrays.stream(TPortInventories.FeatureType.values()).map(TPortInventories.FeatureType::name).collect(Collectors.toList());
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport featureTP [featureType]
        
        if (args.length == 1) {
            if (!hasPermission(player, "TPort.featureTP.open")) {
                return;
            }
            openFeatureTP(player, 0);
        } else {
            TPortInventories.FeatureType featuresType;
            try {
                featuresType = TPortInventories.FeatureType.valueOf(args[1]);
            } catch (IllegalArgumentException iae) {
                sendErrorTheme(player, "Feature %s does not exist", args[1]);
                return;
            }
            
            if (!hasPermission(player, "TPort.featureTP." + featuresType.name(), "TPort.featureTP.all")) {
                return;
            }
            if (!CooldownManager.FeatureTP.hasCooled(player)) {
                return;
            }
            
            featureTP(player, featuresType);
        }
    }
    
}
