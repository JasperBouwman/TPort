package com.spaceman.tport.commands.tport;

import com.spaceman.tport.Main;
import com.spaceman.tport.Pair;
import com.spaceman.tport.biomeTP.BiomePreset;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.CommandTemplate;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.biomeTP.*;
import com.spaceman.tport.cooldown.CooldownManager;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.encapsulation.BiomeEncapsulation;
import com.spaceman.tport.history.TeleportHistory;
import com.spaceman.tport.metrics.BiomeSearchCounter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.spaceman.tport.commandHandler.CommandTemplate.runCommands;
import static com.spaceman.tport.commands.tport.Back.prevTPorts;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.inventories.TPortInventories.openBiomeTP;
import static com.spaceman.tport.tpEvents.TPEManager.requestTeleportPlayer;

public class BiomeTP extends SubCommand {
    
    public static boolean legacyBiomeTP = false;
    
    private final EmptyCommand empty;
    
    public BiomeTP() {
        empty = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return "";
            }
        };
        empty.setCommandName("", ArgumentType.FIXED);
        empty.setCommandDescription(formatInfoTranslation("tport.command.biomeTP.commandDescription"));
        empty.setPermissions("TPort.biomeTP.open");
        
        addAction(empty);
        addAction(new Whitelist());
        addAction(new Blacklist());
        addAction(Preset.getInstance());
        addAction(com.spaceman.tport.commands.tport.biomeTP.Random.getInstance());
        addAction(new Accuracy());
        addAction(new Mode());
        
        registerBiomeTPPresets();
    }
    
    public static List<String> availableBiomes() {
        if (!legacyBiomeTP) {
            try {
                return Main.getInstance().adapter.availableBiomes();
            } catch (Throwable ex) {
                Features.Feature.printSmallNMSErrorInConsole("BiomeTP biome list", true);
                if (Features.Feature.PrintErrorsInConsole.isEnabled()) ex.printStackTrace();
                legacyBiomeTP = true;
            }
        }
        return Arrays.stream(Biome.values()).map(Enum::name).map(String::toLowerCase).collect(Collectors.toList());
    }
    
    public static List<String> availableBiomes(World world) {
        if (!legacyBiomeTP) {
            try {
                return Main.getInstance().adapter.availableBiomes(world);
            } catch (Throwable ex) {
                Features.Feature.printSmallNMSErrorInConsole("BiomeTP per world biome list", true);
                if (Features.Feature.PrintErrorsInConsole.isEnabled()) ex.printStackTrace();
                legacyBiomeTP = true;
            }
        }
        return availableBiomes();
    }
    
    @Nullable
    public static Pair<Location, String> biomeFinder(Player player, List<String> biomes, @Nonnull Location startLocation, Accuracy.AccuracySettings accuracy) {
        if (!legacyBiomeTP) {
            try {
                return Main.getInstance().adapter.biomeFinder(player, biomes, startLocation, accuracy);
            } catch (Throwable ex) {
                Features.Feature.printSmallNMSErrorInConsole("BiomeTP biome finder", true);
                if (Features.Feature.PrintErrorsInConsole.isEnabled()) ex.printStackTrace();
                legacyBiomeTP = true;
            }
        }
        
        return legacyBiomeTP(player, biomes, startLocation, accuracy);
    }
    private static Pair<Location, String> legacyBiomeTP(Player player, List<String> biomes, @Nonnull Location startLocation, Accuracy.AccuracySettings accuracy) {
        int startX = startLocation.getBlockX();
        int startZ = startLocation.getBlockZ();
        World world = startLocation.getWorld();
        if (world == null) return null;
        Rectangle searchArea = Main.getSearchArea(player);
        
        int size = accuracy.getRange();
        List<Integer> yLevels = accuracy.getYLevels();
        int increment = accuracy.getIncrement();
        
        int quartSize = size >> 2;
        int quartX = startX >> 2;
        int quartZ = startZ >> 2;
        
        for (int squareSize = 0; squareSize <= quartSize; squareSize += increment) {
            for (int zOffset = -squareSize; zOffset <= squareSize; zOffset += increment) {
                boolean zEnd = Math.abs(zOffset) == squareSize;
                
                for (int xOffset = -squareSize; xOffset <= squareSize; xOffset += increment) {
                    boolean xEnd = Math.abs(zOffset) == squareSize;
                    if (!zEnd && !xEnd) continue;
                    
                    int newX = (quartX + xOffset) << 2;
                    int newZ = (quartZ + zOffset) << 2;
                    
                    if (!searchArea.contains(newX, newZ)) {
                        continue;
                    }
                    
                    for (int y : yLevels) {
                        Location testLocation = new Location(world, newX, y, newZ);
                        Biome biome = world.getBiome(testLocation);
                        
                        if (biomes.stream().anyMatch(b -> biome.name().equalsIgnoreCase(b))) {
                            return new Pair<>(testLocation, biome.name());
                        }
                    }
                }
            }
        }
        return null;
    }
    
    public static int randomTPTries = 100;
    public static void randomTP(Player player) {
        Location location;
        
        for (int i = 0; i < randomTPTries; i++) {
            location = Main.getRandomLocation(player);
            
            location = FeatureTP.setSafeY(player.getWorld(), location.getBlockX(), location.getBlockZ());
            if (location == null) {
                continue;
            }
            
            location.add(0.5, 0.1, 0.5);
            location.setPitch(player.getLocation().getPitch());
            location.setYaw(player.getLocation().getYaw());
            
            prevTPorts.put(player.getUniqueId(), new Back.PrevTPort(Back.PrevType.BIOME, "biomeLoc", location,
                    "prevLoc", player.getLocation(), "biomeName", "Random"));
            TeleportHistory.setLocationSource(player.getUniqueId(), new BiomeEncapsulation("random"));
            
            requestTeleportPlayer(player, location, () -> sendSuccessTranslation(Bukkit.getPlayer(player.getUniqueId()), "tport.command.biomeTP.random.succeeded"),
                    (p, delay, tickMessage, seconds, secondMessage) -> sendSuccessTranslation(p, "tport.command.biomeTP.randomTP.succeededRequested", delay, tickMessage, seconds, secondMessage));
            CooldownManager.BiomeTP.update(player);
            return;
        }
        sendErrorTranslation(player, "tport.command.biomeTP.random.couldNotFindLocation", randomTPTries);
    }
    
    public static void biomeTP(Player player, com.spaceman.tport.commands.tport.featureTP.Mode.WorldSearchMode mode, List<String> biomes) {
        BiomeSearchCounter.add(biomes);
        Accuracy.AccuracySettings accuracySettings = Accuracy.getDefaultAccuracySettings();
        
        if (biomes.size() == 1) {
            sendInfoTranslation(player, "tport.command.biomeTP.searchingForBiome", biomesToStringSearch(biomes), accuracySettings);
        } else {
            sendInfoTranslation(player, "tport.command.biomeTP.searchingForBiomes", biomesToStringSearch(biomes), accuracySettings);
        }
        
        Pair<Location, String> biomeLocation = biomeFinder(player, biomes, mode.getLoc(player), accuracySettings);
        
        CooldownManager.BiomeTP.update(player);
        
        if (biomeLocation == null) {
            if (biomes.size() == 1) {
                sendErrorTranslation(player, "tport.command.biomeTP.couldNotFindBiome", biomesToStringError(biomes), accuracySettings);
            } else {
                sendErrorTranslation(player, "tport.command.biomeTP.couldNotFindBiomes", biomesToStringError(biomes), accuracySettings);
            }
        } else {
            Location biomeLoc = biomeLocation.getLeft();
            
            biomeLoc = FeatureTP.setSafeY(player.getWorld(), biomeLoc.getBlockX(), biomeLoc.getBlockZ());
            if (biomeLoc == null) {
                sendErrorTranslation(player, "tport.command.biomeTP.noSafeLocationFound");
                return;
            }
            
            biomeLoc.add(0.5, 0.1, 0.5);
            biomeLoc.setPitch(player.getLocation().getPitch());
            biomeLoc.setYaw(player.getLocation().getYaw());
            
            prevTPorts.put(player.getUniqueId(), new Back.PrevTPort(Back.PrevType.BIOME, "biomeLoc", biomeLoc,
                    "prevLoc", player.getLocation(), "biomeName", biomeLocation.getRight()));
            TeleportHistory.setLocationSource(player.getUniqueId(), new BiomeEncapsulation(biomeLocation.getRight()));
            
            Message biomeName = formatTranslation(varInfoColor, varInfoColor, "%s", new BiomeEncapsulation(biomeLocation.getRight()));
            requestTeleportPlayer(player, biomeLoc, () -> sendSuccessTranslation(Bukkit.getPlayer(player.getUniqueId()), "tport.command.biomeTP.succeeded", biomeName),
                    (p, delay, tickMessage, seconds, secondMessage) -> sendSuccessTranslation(p, "tport.command.biomeTP.succeededRequested", biomeName, delay, tickMessage, seconds, secondMessage));
        }
    }
    
    private static Message biomesToStringError(List<String> biomes) {
        Message biomeList = new Message();
        int listSize = biomes.size();
        boolean color = true;
        
        for (int i = 0; i < listSize; i++) {
            String biome = biomes.get(i).toLowerCase();
            if (color) {
                biomeList.addMessage(formatTranslation(varErrorColor, varErrorColor, "%s", new BiomeEncapsulation(biome)));
            } else {
                biomeList.addMessage(formatTranslation(varError2Color, varError2Color, "%s", new BiomeEncapsulation(biome)));
            }
            
            if (i + 2 == listSize)
                biomeList.addMessage(formatInfoTranslation("tport.command.biomeTP.listBiomes.error.lastDelimiter"));
            else biomeList.addMessage(formatInfoTranslation("tport.command.biomeTP.listBiomes.error.delimiter"));
            
            color = !color;
        }
        biomeList.removeLast();
        return biomeList;
    }
    
    private static Message biomesToStringSearch(List<String> biomes) {
        Message biomeList = new Message();
        biomeList.addText("");
        int listSize = biomes.size();
        boolean color = true;
        
        for (int i = 0; i < listSize; i++) {
            String biome = biomes.get(i).toLowerCase();
            if (color) {
                biomeList.addMessage(formatTranslation(varInfoColor, varInfoColor, "%s", new BiomeEncapsulation(biome)));
            } else {
                biomeList.addMessage(formatTranslation(varInfo2Color, varInfo2Color, "%s", new BiomeEncapsulation(biome)));
            }
            
            if (i + 2 == listSize)
                biomeList.addMessage(formatInfoTranslation("tport.command.biomeTP.listBiomes.info.lastDelimiter"));
            else biomeList.addMessage(formatInfoTranslation("tport.command.biomeTP.listBiomes.info.delimiter"));
            
            color = !color;
        }
        biomeList.removeLast();
        return biomeList;
    }
    
    @SuppressWarnings("CommentedOutCode")
    private void registerBiomeTPPresets() {
        BiomePreset.registerPreset("Land", Arrays.asList(
                "OCEAN", "WARM_OCEAN", "LUKEWARM_OCEAN",
                "COLD_OCEAN", "FROZEN_OCEAN", "DEEP_OCEAN",
                "DEEP_LUKEWARM_OCEAN",
                "DEEP_COLD_OCEAN", "DEEP_FROZEN_OCEAN",
                "RIVER", "FROZEN_RIVER"
        ), false, Material.GRASS_BLOCK);
        
        BiomePreset.registerPreset("Water", Arrays.asList(
                "OCEAN", "WARM_OCEAN", "LUKEWARM_OCEAN",
                "COLD_OCEAN", "FROZEN_OCEAN", "DEEP_OCEAN",
                "DEEP_LUKEWARM_OCEAN",
                "DEEP_COLD_OCEAN", "DEEP_FROZEN_OCEAN",
                "RIVER", "FROZEN_RIVER"
        ), true, Material.WATER_BUCKET);
        
        BiomePreset.registerPreset("The_End", Arrays.asList(
                        "END_BARRENS", "END_HIGHLANDS", "END_MIDLANDS", "THE_END", "SMALL_END_ISLANDS"),
                true, Material.END_STONE);
        
        BiomePreset.registerPreset("Nether", Arrays.asList(
                        "NETHER_WASTES", "BASALT_DELTAS",
                        "CRIMSON_FOREST", "WARPED_FOREST", "SOUL_SAND_VALLEY"),
                true, Material.NETHERRACK);
        
        BiomePreset.registerPreset("Trees", Arrays.asList(
                        "FOREST", "CHERRY_GROVE", "WINDSWEPT_FOREST", "DARK_FOREST", "SAVANNA", "SAVANNA_PLATEAU", "WINDSWEPT_SAVANNA", "JUNGLE", "SPARSE_JUNGLE", "BIRCH_FOREST", "OLD_GROWTH_BIRCH_FOREST", "TAIGA", "OLD_GROWTH_SPRUCE_TAIGA", "OLD_GROWTH_PINE_TAIGA"),
                true, Material.OAK_WOOD);

//        BiomePreset.registerPreset("name", Arrays.asList(
//                "biome names"),
//                true, Material.STONE);
    }
    
    public static Material getMaterial(String biome) {
        String materialName = switch (biome.toUpperCase()) {
            case "OCEAN", "RIVER", "DEEP_OCEAN", "LUKEWARM_OCEAN", "COLD_OCEAN", "DEEP_LUKEWARM_OCEAN", "DEEP_COLD_OCEAN" -> "WATER_BUCKET";
            case "PLAINS", "WINDSWEPT_HILLS", "MEADOW" -> "GRASS_BLOCK";
            case "DESERT", "BEACH" -> "SAND";
            case "FOREST", "WINDSWEPT_FOREST" -> "OAK_LOG";
            case "TAIGA", "OLD_GROWTH_PINE_TAIGA", "OLD_GROWTH_SPRUCE_TAIGA" -> "SPRUCE_LOG";
            case "SWAMP" -> "LILY_PAD";
            case "NETHER_WASTES" -> "NETHERRACK";
            case "THE_END", "SMALL_END_ISLANDS", "END_MIDLANDS", "END_HIGHLANDS", "END_BARRENS" -> "END_STONE";
            case "FROZEN_OCEAN", "FROZEN_RIVER", "DEEP_FROZEN_OCEAN" -> "ICE";
            case "ICE_SPIKES", "FROZEN_PEAKS" -> "PACKED_ICE";
            case "SNOWY_PLAINS", "SNOWY_BEACH", "SNOWY_TAIGA" -> "SNOW";
            case "MUSHROOM_FIELDS" -> "RED_MUSHROOM_BLOCK";
            case "JUNGLE", "SPARSE_JUNGLE" -> "JUNGLE_LOG";
            case "BAMBOO_JUNGLE" -> "BAMBOO";
            case "STONY_SHORE", "STONY_PEAKS" -> "STONE";
            case "BIRCH_FOREST", "OLD_GROWTH_BIRCH_FOREST" -> "BIRCH_LOG";
            case "DARK_FOREST" -> "DARK_OAK_LOG";
            case "SAVANNA", "SAVANNA_PLATEAU", "WINDSWEPT_SAVANNA" -> "ACACIA_LOG";
            case "BADLANDS", "WOODED_BADLANDS", "ERODED_BADLANDS" -> "TERRACOTTA";
            case "WARM_OCEAN" -> "BRAIN_CORAL_BLOCK";
            case "THE_VOID" -> "BARRIER";
            case "SUNFLOWER_PLAINS" -> "SUNFLOWER";
            case "WINDSWEPT_GRAVELLY_HILLS" -> "GRAVEL";
            case "FLOWER_FOREST" -> "ROSE_BUSH";
            case "SOUL_SAND_VALLEY" -> "SOUL_SAND";
            case "CRIMSON_FOREST" -> "CRIMSON_NYLIUM";
            case "WARPED_FOREST" -> "WARPED_NYLIUM";
            case "BASALT_DELTAS" -> "BASALT";
            case "DRIPSTONE_CAVES" -> "POINTED_DRIPSTONE";
            case "LUSH_CAVES" -> "GLOW_BERRIES";
            case "GROVE", "JAGGED_PEAKS" -> "SNOW_BLOCK";
            case "SNOWY_SLOPES" -> "POWDER_SNOW_BUCKET";
            case "MANGROVE_SWAMP" -> "MANGROVE_LOG";
            case "DEEP_DARK" -> "SCULK";
            case "CHERRY_GROVE" -> "CHERRY_LOG";
            
            default -> "DIAMOND_BLOCK";
        };
        return Main.getOrDefault(Material.getMaterial(materialName), Material.DIAMOND_BLOCK);
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport biomeTP
        // tport biomeTP accuracy [accuracy]
        // tport biomeTP whitelist <biome...>
        // tport biomeTP blacklist <biome...>
        // tport biomeTP preset [preset]
        // tport biomeTP random
        // tport biomeTP searchTries [tries]
        // tport biomeTP mode [mode]
        
        if (Features.Feature.BiomeTP.isDisabled())  {
            Features.Feature.BiomeTP.sendDisabledMessage(player);
            return;
        }
        
        if (args.length == 1) {
            if (!empty.hasPermissionToRun(player, true)) {
                return;
            }
            openBiomeTP(player);
        } else {
            if (!runCommands(getActions(), args[1], args, player)) {
                sendErrorTranslation(player, "tport.command.wrongUsage", "/tport biomeTP " + CommandTemplate.convertToArgs(getActions(), true));
            }
        }
    }
}
