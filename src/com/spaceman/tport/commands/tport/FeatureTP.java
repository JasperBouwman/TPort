package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.cooldown.CooldownManager;
import com.spaceman.tport.fileHander.Files;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.spaceman.tport.TPortInventories.openFeatureTP;
import static com.spaceman.tport.colorFormatter.ColorTheme.*;
import static com.spaceman.tport.colorFormatter.ColorTheme.ColorType.infoColor;
import static com.spaceman.tport.colorFormatter.ColorTheme.ColorType.varInfoColor;
import static com.spaceman.tport.commandHander.CommandTemplate.runCommands;
import static com.spaceman.tport.commands.tport.Back.prevTPorts;
import static com.spaceman.tport.events.InventoryClick.requestTeleportPlayer;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;

public class FeatureTP extends SubCommand {
    
    public FeatureTP() {
        EmptyCommand empty = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return "";
            }
        };
        empty.setCommandName("", ArgumentType.FIXED);
        empty.setCommandDescription(textComponent("This command is used to open the FeatureTP GUI", infoColor));
        
        
        EmptyCommand emptySearchFeatureMode = new EmptyCommand();
        emptySearchFeatureMode.setCommandName("mode", ArgumentType.OPTIONAL);
        emptySearchFeatureMode.setCommandDescription(textComponent("This command is used to teleport to the given feature using the given mode", infoColor),
                textComponent("\n\nPermissions: ", infoColor), textComponent("TPort.featureTP.type.<feature>", varInfoColor),
                textComponent(" and ", infoColor), textComponent("TPort.featureTP.mode.<mode>", varInfoColor));
        emptySearchFeatureMode.setRunnable(((args, player) -> {
            if (args.length == 3 || args.length == 4) {
    
                FeatureType featureType;
                FeatureTPMode mode = getDefMode(player.getUniqueId());
                try {
                    featureType = FeatureType.valueOf(args[2]);
                } catch (IllegalArgumentException iae) {
                    sendErrorTheme(player, "Feature %s does not exist", args[2]);
                    return;
                }
                if (args.length == 4) {
                    try {
                        mode = FeatureTPMode.valueOf(args[3].toUpperCase());
                        if (!hasPermission(player, true, "TPort.featureTP.mode." + mode.name())) {
                            return;
                        }
                    } catch (IllegalArgumentException iae) {
                        sendErrorTheme(player, "FeatureTP mode %s does not exist", args[3]);
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
                sendErrorTheme(player, "Usage %s", "/tport featureTP search <feature> [mode]");
            }
        }));
        EmptyCommand emptySearchFeature = new EmptyCommand();
        emptySearchFeature.setCommandName("feature", ArgumentType.REQUIRED);
        emptySearchFeature.setCommandDescription(textComponent("This command is used to teleport to the given feature using your default FeatureTPMode", infoColor),
                textComponent("\n\nPermission: ", infoColor), textComponent("TPort.featureTP.type.<feature>", varInfoColor));
        emptySearchFeature.setRunnable((emptySearchFeatureMode::run));
        emptySearchFeature.setTabRunnable((args, player) -> Arrays.stream(FeatureTPMode.values()).filter(mode -> hasPermission(player, false, true, mode.getPerm())).map(Enum::name).collect(Collectors.toList()));
        emptySearchFeature.addAction(emptySearchFeatureMode);
        EmptyCommand emptySearch = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptySearch.setCommandName("search", ArgumentType.FIXED);
        emptySearch.setTabRunnable(((args, player) -> Arrays.stream(FeatureType.values()).map(FeatureType::name).collect(Collectors.toList())));
        emptySearch.setRunnable(emptySearchFeatureMode::run);
        emptySearch.addAction(emptySearchFeature);
        
        
        EmptyCommand emptyModeMode = new EmptyCommand();
        emptyModeMode.setCommandName("mode", ArgumentType.OPTIONAL);
        emptyModeMode.setCommandDescription(textComponent("This command is used to set your default FeatureTPMode", infoColor),
                textComponent("\n\nPermission: ", infoColor), textComponent("TPort.featureTP.mode.<mode>", varInfoColor));
        emptyModeMode.setRunnable(((args, player) -> {
            if (args.length == 2) {
                sendInfoTheme(player, "Your default FeatureTPMode is set to %s", getDefMode(player.getUniqueId()).name());
            } else if (args.length == 3) {
                try {
                    FeatureTPMode mode = FeatureTPMode.valueOf(args[2].toUpperCase());
                    if (!hasPermission(player, true, "TPort.featureTP.mode." + mode.name())) {
                        return;
                    }
                    setDefMode(player.getUniqueId(), mode);
                    sendSuccessTheme(player, "Successfully set you default FeatureTPMode to %s", mode.name());
                } catch (IllegalArgumentException iae) {
                    sendErrorTheme(player, "FeatureTP mode %s does not exist", args[2]);
                }
            } else {
                sendErrorTheme(player, "Usage: %s", "/tport featureTP mode [mode]");
            }
        }));
        EmptyCommand emptyMode = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptyMode.setCommandName("mode", ArgumentType.FIXED);
        emptyMode.setCommandDescription(textComponent("This command is used to get your default FeatureTPMode", infoColor));
        emptyMode.setRunnable(emptyModeMode::run);
        emptyMode.setTabRunnable(((args, player) -> Arrays.stream(FeatureTPMode.values()).map(Enum::name).collect(Collectors.toList())));
        emptyMode.addAction(emptyModeMode);
        
        
        addAction(empty);
        addAction(emptySearch);
        addAction(emptyMode);
    }
    
    public static FeatureTPMode getDefMode(UUID uuid) {
        Files tportConfig = getFile("TPortConfig");
        return FeatureTPMode.valueOf(tportConfig.getConfig().getString("featureTP.defaultMode." + uuid.toString(), "CLOSEST"));
    }
    
    public static void setDefMode(UUID uuid, FeatureTPMode mode) {
        Files tportConfig = getFile("TPortConfig");
        tportConfig.getConfig().set("featureTP.defaultMode." + uuid.toString(), mode.name());
        tportConfig.saveConfig();
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
    public void run(String[] args, Player player) {
        // tport featureTP
        // tport featureTP search <feature> [mode]
        // tport featureTP mode [mode]
        
        if (args.length == 1) {
            openFeatureTP(player, 0);
            return;
        }
        if (runCommands(getActions(), args[1], args, player)) {
            return;
        }
        sendErrorTheme(player, "Usage: %s", "/tport featureTP [search|mode]");
        
    }
    
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
        
        public FeatureTPMode getNext() {
            boolean next = false;
            for (FeatureTPMode mode : values()) {
                if (mode.equals(this)) {
                    next = true;
                } else if (next) {
                    return mode;
                }
            }
            return Arrays.asList(values()).get(0);
        }
        
        @FunctionalInterface
        private interface LocationGetter {
            Location getLoc(Player player);
        }
    }
    
}
