package com.spaceman.tport.commands.tport;

import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.CommandTemplate;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.commands.tport.featureTP.Mode;
import com.spaceman.tport.commands.tport.featureTP.Search;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.searchAreaHander.SearchAreaHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

import static com.spaceman.tport.TPortInventories.openFeatureTP;
import static com.spaceman.tport.commandHander.CommandTemplate.runCommands;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.infoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;

public class FeatureTP extends SubCommand {
    
    private final EmptyCommand empty;
    
    public FeatureTP() {
        empty = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return "";
            }
        };
        empty.setCommandName("", ArgumentType.FIXED);
        empty.setCommandDescription(textComponent("This command is used to open the FeatureTP GUI", infoColor));
        empty.setPermissions("TPort.featureTP.open");
        addAction(empty);
        
        addAction(new Mode());
        addAction(new Search());
//        addAction(new SearchTries()); //todo uncomment for SearchArea
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
    
    @Override
    public void run(String[] args, Player player) {
        // tport featureTP
        // tport featureTP search <feature> [mode]
        // tport featureTP mode [mode]
        
        if (args.length == 1) {
            if (!empty.hasPermissionToRun(player, true)) {
                return;
            }
            openFeatureTP(player, 0);
            return;
        }
        if (runCommands(getActions(), args[1], args, player)) {
            return;
        }
        sendErrorTheme(player, "Usage: %s", "/tport featureTP " + CommandTemplate.convertToArgs(getActions(), true));
        
    }
    
    public enum FeatureType {
        Buried_Treasure(new ItemStack(Material.CHEST), safeYSetter(), "buried_treasure", null),
        Desert_Pyramid(new ItemStack(Material.SAND), safeYSetter(), "desert_pyramid", null),
        EndCity(new ItemStack(Material.END_STONE), safeYSetter(), "endcity", null),
        Fortress(new ItemStack(Material.NETHER_BRICKS), safeYSetter(), "fortress", null),
        Igloo(new ItemStack(Material.SNOW_BLOCK), safeYSetter(), "igloo", null),
        Jungle_Pyramid(new ItemStack(Material.MOSSY_COBBLESTONE), safeYSetter(), "jungle_pyramid", null),
        Mansion(new ItemStack(Material.DARK_OAK_WOOD), safeYSetter(), "mansion", null),
        Mineshaft(new ItemStack(Material.CHEST_MINECART), safeYSetter(), "mineshaft", null),
        Monument(new ItemStack(Material.PRISMARINE_BRICKS), safeYSetter(), "monument", null),
        Ocean_Ruin(new ItemStack(Material.WATER_BUCKET), safeYSetter(), "ocean_ruin", null),
        Pillager_Outpost(new ItemStack(Material.CROSSBOW), safeYSetter(), "pillager_outpost", null),
        Shipwreck(new ItemStack(Material.OAK_BOAT), safeYSetter(), "shipwreck", null),
        Stronghold(new ItemStack(Material.END_PORTAL_FRAME), safeYSetter(), "stronghold", null),
        Swamp_Hut(new ItemStack(Material.CAULDRON), safeYSetter(), "swamp_hut", null),
        Village(new ItemStack(Material.EMERALD), safeYSetter(), "village", null),
        Village_Desert(new ItemStack(Material.SANDSTONE), safeYSetter(), "village", "BiomeDesert"),
        Village_Plains(new ItemStack(Material.GRASS_BLOCK), safeYSetter(), "village", "BiomePlains"),
        Village_Savanna(new ItemStack(Material.ACACIA_PLANKS), safeYSetter(), "village", "BiomeSavanna"),
        Village_Taiga(new ItemStack(Material.SPRUCE_PLANKS), safeYSetter(), "village", "BiomeTaiga"),
        Village_Snowy(new ItemStack(Material.ICE), safeYSetter(), "village", "BiomeIcePlains"),
        Bastion_Remnant(new ItemStack(Material.POLISHED_BLACKSTONE_BRICKS), safeYSetter(), "bastion_remnant", null),
        Nether_Fossil(new ItemStack(Material.BONE_BLOCK), safeYSetter(), "nether_fossil", null),
        Ruined_Portal(new ItemStack(Material.OBSIDIAN), safeYSetter(), "ruined_portal", null);
        
        private final ItemStack itemStack;
        private final YSetter ySetter;
        private final String name;
        private final String biome;
        
        FeatureType(ItemStack itemStack, YSetter ySetter, String mcName, String biome) {
            this.itemStack = itemStack;
            this.ySetter = ySetter;
            this.name = mcName;
            this.biome = biome;
        }
        
        public static YSetter safeYSetter() {
            return ((world, x, z) -> {
                if (world.getEnvironment().equals(World.Environment.NETHER)) {
                    int originalX = x;
                    int originalZ = z;
                    int searches = 0;
                    Random random = new Random();
                    
                    for (int y = 1; y < world.getMaxHeight(); y++) {
                        Location tempFeet = new Location(world, x, y, z);
                        
                        if (SafetyCheck.isSafe(tempFeet)) {
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
                } else {
                    int y = world.getHighestBlockYAt(x, z);
                    Location l = new Location(world, x, y + 1, z);
                    Random random = new Random();
                    int spread = 10;
                    int searches = 0;
    
                    while (y <= 1 || !SafetyCheck.isSafe(l)) {
                        if (searches == 20) {
                            return null;
                        }
                        x += random.nextInt(spread) - spread * 0.5;
                        z += random.nextInt(spread) - spread * 0.5;
                        y = world.getHighestBlockYAt(x, z);
                        l = new Location(world, x, y + 1, z);
                        searches++;
                    }
                    return l;
                }
            });
        }
        
        public String getMCName() {
            return name;
        }
        
        public String getBiome() {
            return biome;
        }
        
        public static FeatureType get(String name) {
            for (FeatureType type : values()) {
                if (type.name().equalsIgnoreCase(name)) {
                    return type;
                }
            }
            throw new IllegalArgumentException(name + " does not exist");
        }
        
        public ItemStack getItemStack() {
            return itemStack;
        }
        
        public Location setY(World world, int x, int z) {
            return ySetter.setY(world, x, z);
        }
        
        @FunctionalInterface
        public interface YSetter {
            Location setY(World world, int x, int z);
        }
    }
    
    public enum FeatureTPMode {
        RANDOM(SearchAreaHandler.getSearchAreaHandler()::getRandomLocation),
        CLOSEST(SearchAreaHandler.getSearchAreaHandler()::getClosestLocation);
        
        private final LocationGetter locationGetter;
        
        FeatureTPMode(LocationGetter locationGetter) {
            this.locationGetter = locationGetter;
        }
        
        @Nullable
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
