package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.cooldown.CooldownManager;
import com.spaceman.tport.fancyMessage.Message;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import static com.spaceman.tport.TPortInventories.openFeatureTP;
import static com.spaceman.tport.colorFormatter.ColorTheme.ColorType.infoColor;
import static com.spaceman.tport.colorFormatter.ColorTheme.ColorType.varInfoColor;
import static com.spaceman.tport.colorFormatter.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.colorFormatter.ColorTheme.sendSuccessTheme;
import static com.spaceman.tport.commands.tport.Back.prevTPorts;
import static com.spaceman.tport.events.InventoryClick.requestTeleportPlayer;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;

public class FeatureTP extends SubCommand {
    
    
    public enum FeatureType {
        Buried_Treasure(new ItemStack(Material.CHEST)),
        Desert_Pyramid(new ItemStack(Material.SAND)),
        EndCity(new ItemStack(Material.END_STONE)),
        Fortress(new ItemStack(Material.NETHER_BRICK), ((world, x, z) -> {
            int originalX = x;
            int originalZ = z;
            int searches = 0;
            Random random = new Random();
            
            for (int y = 1; y < world.getMaxHeight(); y++) {
                Location tempFeet = new Location(world, x, y, z);
                Location tempHead = new Location(world, x, y + 1, z);
                Location tempLava = new Location(world, x, y - 1, z);
                
                if (!tempLava.getBlock().getType().equals(Material.AIR) &&
                        !tempLava.getBlock().getType().equals(Material.LAVA) &&
                        !tempLava.getBlock().getType().equals(Material.FIRE) &&
                        (tempFeet.getBlock().getType().equals(Material.AIR) || tempFeet.getBlock().getType().equals(Material.CAVE_AIR)) &&
                        (tempHead.getBlock().getType().equals(Material.AIR) || tempHead.getBlock().getType().equals(Material.CAVE_AIR))) {
                    return tempFeet;
                } else if (tempFeet.getBlock().getType().equals(Material.BEDROCK) && y > 10) {
                    //spread location, reset search
                    if (searches == 20) {
                        return null;
                    }
                    int spread = 10;
                    x += random.nextInt(spread) - spread * 0.5;
                    z += random.nextInt(spread) - spread * 0.5;
                    y = 1;
                    searches++;
                }
            }
            return new Location(world, originalX, world.getHighestBlockYAt(originalX, originalZ), originalZ);
        })),
        Igloo(new ItemStack(Material.SNOW_BLOCK)),
        Jungle_Pyramid(new ItemStack(Material.MOSSY_COBBLESTONE)),
        Mansion(new ItemStack(Material.DARK_OAK_WOOD)),
        Mineshaft(new ItemStack(Material.STONE)),
        Monument(new ItemStack(Material.PRISMARINE_BRICKS)),
        Ocean_Ruin(new ItemStack(Material.WATER_BUCKET)),
        Pillager_Outpost(new ItemStack(Material.CROSSBOW)),
        Shipwreck(new ItemStack(Material.OAK_BOAT)),
        Stronghold(new ItemStack(Material.END_PORTAL_FRAME)),
        Swamp_Hut(new ItemStack(Material.VINE)),
        Village(new ItemStack(Material.OAK_PLANKS));
        
        private ItemStack itemStack;
        private YSetter yGetter;
        
        FeatureType(ItemStack itemStack) {
            this(itemStack, ((world, x, z) -> new Location(world, x, world.getHighestBlockYAt(x, z), z)));
        }
        
        FeatureType(ItemStack itemStack, YSetter yGetter) {
            this.itemStack = itemStack;
            this.yGetter = yGetter;
        }
        
        public ItemStack getItemStack() {
            return itemStack;
        }
        
        public Location setY(World world, int x, int z) {
            return yGetter.setY(world, x, z);
        }
        
        @FunctionalInterface
        private interface YSetter {
            Location setY(World world, int x, int z);
        }
    }
    
    public enum FeatureTPMode {
        RANDOM(player -> {
            Random r = new Random();
            return new Location(player.getWorld(), r.nextInt(6000000) - 3000000, r.nextDouble(), r.nextInt(6000000) - 3000000);
        }),
        CLOSEST(Entity::getLocation);
        
        private LocationGetter locationGetter;
        
        FeatureTPMode(LocationGetter locationGetter) {
            this.locationGetter = locationGetter;
        }
        
        public Location getLoc(Player player) {
            return locationGetter.getLoc(player);
        }
    
        public String getPerm() {
            return "TPort.featureTP.mode." + name();
        }
        
        @FunctionalInterface
        private interface LocationGetter {
            Location getLoc(Player player);
        }
    }
    
    public FeatureTP() {
        EmptyCommand emptyFeatureMode = new EmptyCommand();
        emptyFeatureMode.setCommandName("mode", ArgumentType.OPTIONAL);
        emptyFeatureMode.setCommandDescription(textComponent("This command is used to teleport to the given feature using the given mode", infoColor),
                textComponent("\n\nPermissions: ", infoColor), textComponent("TPort.featureTP.type.<feature>", varInfoColor),
                textComponent(" and ", infoColor), textComponent("TPort.featureTP.mode.<mode>", varInfoColor));
        
        EmptyCommand emptyFeature = new EmptyCommand();
        emptyFeature.setCommandName("feature", ArgumentType.OPTIONAL);
        emptyFeature.setCommandDescription(textComponent("This command is used to teleport to the given feature using the CLOSEST mode", infoColor),
                textComponent("\n\nPermissions: ", infoColor), textComponent("TPort.featureTP.type.<feature>", varInfoColor));
        emptyFeature.setTabRunnable((args, player) -> Arrays.stream(FeatureTPMode.values()).filter(mode -> hasPermission(player, false, true, mode.getPerm())).map(Enum::name).collect(Collectors.toList()));
        emptyFeature.addAction(emptyFeatureMode);
        
        addAction(emptyFeature);
    }
    
    private static void featureTP(Player player, FeatureTPMode mode, FeatureType featureType) {
        Location l = mode.getLoc(player);
        Location featureLoc = featureFinder(l, featureType);
        if (featureLoc == null) {
            sendErrorTheme(player, "Could not find the feature %s nearby", featureType.name());
        } else {
            featureLoc = featureType.setY(player.getWorld(), featureLoc.getBlockX(), featureLoc.getBlockZ());
            
            if (featureLoc != null) {
                featureLoc.add(0.5, 0.1, 0.5);
                featureLoc.setPitch(player.getLocation().getPitch());
                featureLoc.setYaw(player.getLocation().getYaw());

                prevTPorts.put(player.getUniqueId(), new Back.PrevTPort(Back.PrevType.FEATURE, "featureLoc", featureLoc,
                        "prevLoc", player.getLocation(), "featureName", featureType.name()));
                
                requestTeleportPlayer(player, featureLoc);
                if (Delay.delayTime(player) == 0) {
                    sendSuccessTheme(player, "Successfully teleported to feature %s", featureType.name());
                } else {
                    sendSuccessTheme(player, "Successfully requested teleportation to feature %s", featureType.name());
                }
            } else {
                sendErrorTheme(player, "Could not find a place to teleport to, try again");
            }
            CooldownManager.FeatureTP.update(player);
        }
    }
    
    private static Location featureFinder(Location startLocation, FeatureType feature) {
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
        return new Message(textComponent("This command is used to open the FeatureTP GUI", infoColor));
    }
    
    @Override
    public List<String> tabList(Player player, String[] args) {
        return Arrays.stream(FeatureType.values()).map(FeatureType::name).collect(Collectors.toList());
    }
    
    @Override
    public void run(String[] args, Player player) {
        //tport featureTP [feature] [mode]
        
        if (args.length == 1) {
            openFeatureTP(player, 0);
        } else if (args.length == 3 || args.length == 2) {
            FeatureType featureType;
            FeatureTPMode mode = FeatureTPMode.CLOSEST;
            try {
                featureType = FeatureType.valueOf(args[1]);
            } catch (IllegalArgumentException iae) {
                sendErrorTheme(player, "Feature %s does not exist", args[1]);
                return;
            }
            if (args.length == 3) {
                try {
                    mode = FeatureTPMode.valueOf(args[2].toUpperCase());
                    if (!hasPermission(player, true, "TPort.featureTP.mode." + mode.name())) {
                        return;
                    }
                } catch (IllegalArgumentException iae) {
                    sendErrorTheme(player, "FeatureTP mode %s does not exist", args[2]);
                    return;
                }
            }
    
            if (!hasPermission(player, true, "TPort.featureTP.type." + featureType.name())) {
                return;
            }
            if (!CooldownManager.FeatureTP.hasCooled(player)) {
                return;
            }
    
            featureTP(player, mode, featureType);
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport featureTP [feature] [mode]");
        }
    }
    
}
