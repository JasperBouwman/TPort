package com.spaceman.tport.commands.tport.featureTP;

import com.google.common.collect.BiMap;
import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commandHander.SubCommand;
import com.spaceman.tport.commands.tport.Back;
import com.spaceman.tport.commands.tport.Delay;
import com.spaceman.tport.commands.tport.FeatureTP;
import com.spaceman.tport.cooldown.CooldownManager;
import com.spaceman.tport.metrics.FeatureSearchCounter;
import com.spaceman.tport.searchAreaHander.SearchAreaHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

import static com.spaceman.tport.commands.tport.Back.prevTPorts;
import static com.spaceman.tport.commands.tport.FeatureTP.getDefMode;
import static com.spaceman.tport.events.InventoryClick.requestTeleportPlayer;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.infoColor;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;

public class Search extends SubCommand {
    
    public Search() {
        EmptyCommand emptySearchFeatureMode = new EmptyCommand();
        EmptyCommand emptySearchFeature = new EmptyCommand();
        emptySearchFeature.setPermissions("TPort.featureTP.type.<feature>");
        
        emptySearchFeatureMode.setCommandName("mode", ArgumentType.OPTIONAL);
        emptySearchFeatureMode.setCommandDescription(textComponent("This command is used to teleport to the given feature using the given mode", infoColor));
        List<String> permissions = new ArrayList<>(emptySearchFeature.getPermissions());
        permissions.addAll(Mode.emptyModeMode.getPermissions());
        emptySearchFeatureMode.setPermissions(permissions);
    
        emptySearchFeature.setCommandName("feature", ArgumentType.REQUIRED);
        emptySearchFeature.setCommandDescription(textComponent("This command is used to teleport to the given feature using your default FeatureTP Mode", infoColor));
        emptySearchFeature.setTabRunnable((args, player) -> Arrays.stream(FeatureTP.FeatureTPMode.values()).filter(mode -> hasPermission(player, false, true, mode.getPerm())).map(Enum::name).collect(Collectors.toList()));
        emptySearchFeature.addAction(emptySearchFeatureMode);
        
        addAction(emptySearchFeature);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return Arrays.stream(FeatureTP.FeatureType.values()).map(FeatureTP.FeatureType::name).collect(Collectors.toList());
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport featureTP search <feature> [mode]
        
        if (args.length == 3 || args.length == 4) {
            
            FeatureTP.FeatureType featureType;
            FeatureTP.FeatureTPMode mode = getDefMode(player.getUniqueId());
            try {
                featureType = FeatureTP.FeatureType.get(args[2]);
            } catch (IllegalArgumentException iae) {
                sendErrorTheme(player, "Feature %s does not exist", args[2]);
                return;
            }
            if (!hasPermission(player, true, "TPort.featureTP.type." + featureType.name())) {
                return;
            }
            if (args.length == 4) {
                try {
                    mode = FeatureTP.FeatureTPMode.valueOf(args[3].toUpperCase());
                    if (!hasPermission(player, true, "TPort.featureTP.mode." + mode.name())) {
                        return;
                    }
                } catch (IllegalArgumentException iae) {
                    sendErrorTheme(player, "FeatureTP mode %s does not exist", args[3]);
                    return;
                }
            }
            
            if (!CooldownManager.FeatureTP.hasCooled(player)) {
                return;
            }
            
            featureTP(player, mode, featureType);
            
        } else {
            sendErrorTheme(player, "Usage %s", "/tport featureTP search <feature> [mode]");
        }
    }
    
    public static void featureTP(Player player, FeatureTP.FeatureTPMode mode, FeatureTP.FeatureType featureType) {
        FeatureSearchCounter.add(featureType);
        Location l = mode.getLoc(player);
        
        if (l == null) {
            sendErrorTheme(player, "Could not find a location in your search area, you could try again");
            return;
        }
        sendInfoTheme(player, "Searching for feature " + featureType.name());
        Location featureLoc = featureFinder(player, l, featureType);
        if (featureLoc != null) {
            featureLoc = featureType.setY(player.getWorld(), featureLoc.getBlockX(), featureLoc.getBlockZ());
        
            if (featureLoc != null) {
                featureLoc.add(0.5, 0.1, 0.5);
                featureLoc.setPitch(player.getLocation().getPitch());
                featureLoc.setYaw(player.getLocation().getYaw());
            
                prevTPorts.put(player.getUniqueId(), new Back.PrevTPort(Back.PrevType.FEATURE, "featureLoc", featureLoc,
                        "prevLoc", player.getLocation(), "featureName", featureType.name()));

                requestTeleportPlayer(player, featureLoc,
                        () -> sendSuccessTheme(Bukkit.getPlayer(player.getUniqueId()), "Successfully teleported to feature %s", featureType.name()));
                int delay = Delay.delayTime(player);
                if (delay == 0) {
                    sendSuccessTheme(player, "Successfully teleported to feature %s", featureType.name());
                } else {
                    sendSuccessTheme(player, "Successfully requested teleportation to feature %s, delay time is %s ticks", featureType.name(), delay);
                }
            } else {
                sendErrorTheme(player, "Could not find a place to teleport to, try again");
            }
            CooldownManager.FeatureTP.update(player);
        }
    }
    
    private static Location featureFinder(Player player, Location startLocation, FeatureTP.FeatureType feature) {
        
        int x = startLocation.getBlockX() >> 4;
        int z = startLocation.getBlockZ() >> 4;
        World world = startLocation.getWorld();
        
        if (feature == FeatureTP.FeatureType.Stronghold) {
            
            try {
                Object nmsWorld = Objects.requireNonNull(world).getClass().getMethod("getHandle").invoke(world);
                String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
    
                Field minecraftServerField = nmsWorld.getClass().getDeclaredField("server");
                minecraftServerField.setAccessible(true);
                Object minecraftServer = minecraftServerField.get(nmsWorld);
                Object saveData = minecraftServer.getClass().getMethod("getSaveData").invoke(minecraftServer);
                Object generatorSettings = saveData.getClass().getMethod("getGeneratorSettings").invoke(saveData);
                if (!(boolean) generatorSettings.getClass().getMethod("shouldGenerateMapFeatures").invoke(generatorSettings)) {
                    sendErrorTheme(player, "This world does not generate features");
                    return null;
                }
                
                Object chunkProvider = nmsWorld.getClass().getMethod("getChunkProvider").invoke(nmsWorld);
                Object chunkGenerator = chunkProvider.getClass().getMethod("getChunkGenerator").invoke(chunkProvider);
                
                Method g = chunkGenerator.getClass().getSuperclass().getDeclaredMethod("g");
                g.setAccessible(true);
                g.invoke(chunkGenerator);
                
                Field iteratorField = chunkGenerator.getClass().getSuperclass().getDeclaredField("f");
                iteratorField.setAccessible(true);
                List list = (List) iteratorField.get(chunkGenerator);
                Iterator iterator = (Iterator) list.getClass().getMethod("iterator").invoke(list);
    
                Object currentPosition = Class.forName("net.minecraft.server." + version + ".BaseBlockPosition")
                        .getConstructor(int.class, int.class, int.class).newInstance(startLocation.getBlockX(), startLocation.getBlockY(), startLocation.getBlockZ());
                
                Location positionCandidate = null;
                double d0 = 1.7976931348623157E308D;
                Object mutableBlockPosition = Class.forName("net.minecraft.server." + version + ".BlockPosition$MutableBlockPosition").getConstructor().newInstance();
                
                Method mutableBlockPosition_d = mutableBlockPosition.getClass().getMethod("d", int.class, int.class, int.class); //set position method
                Method mutableBlockPosition_j = mutableBlockPosition.getClass().getMethod("j", currentPosition.getClass()); //get distance
    
                Polygon searchArea = SearchAreaHandler.getSearchAreaHandler().getSearchArea(player);
                
                while (iterator.hasNext()) {
                    Object chunkCoordIntPair = iterator.next();
                    int tmpX = (int) chunkCoordIntPair.getClass().getField("x").get(chunkCoordIntPair);
                    int tmpZ = (int) chunkCoordIntPair.getClass().getField("z").get(chunkCoordIntPair);
                    mutableBlockPosition_d.invoke(mutableBlockPosition, (tmpX << 4) + 8, 64, (tmpZ << 4) + 8);
                    double distance = (double) mutableBlockPosition_j.invoke(mutableBlockPosition, currentPosition);
                    
                    if (positionCandidate == null || distance < d0) {
                        int newX = (int) mutableBlockPosition.getClass().getMethod("getX").invoke(mutableBlockPosition);
                        int newZ = (int) mutableBlockPosition.getClass().getMethod("getZ").invoke(mutableBlockPosition);
                        if (searchArea.contains(newX, newZ)) {
                            positionCandidate = new Location(startLocation.getWorld(), newX, 64, newZ);
                            d0 = distance;
                        }
                    }
                }
                
                return positionCandidate;
                
            } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | NoSuchFieldException | ClassNotFoundException | InstantiationException e) {
                e.printStackTrace();
            }
    
    
            return null;
        }
        
        try {
            Object nmsWorld = Objects.requireNonNull(world).getClass().getMethod("getHandle").invoke(world);
            String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
            
            Field minecraftServerField = nmsWorld.getClass().getDeclaredField("server");
            minecraftServerField.setAccessible(true);
            Object minecraftServer = minecraftServerField.get(nmsWorld);
            Object saveData = minecraftServer.getClass().getMethod("getSaveData").invoke(minecraftServer);
            Object generatorSettings = saveData.getClass().getMethod("getGeneratorSettings").invoke(saveData);
            if (!(boolean) generatorSettings.getClass().getMethod("shouldGenerateMapFeatures").invoke(generatorSettings)) {
                sendErrorTheme(player, "This world does not generate features");
                return null;
            }
            
            Class<?> structureGeneratorClass = Class.forName("net.minecraft.server." + version + ".StructureGenerator");
            Field a = structureGeneratorClass.getField("a");
            //noinspection rawtypes
            Object structureGenerator = ((BiMap) a.get(null)).get(feature.getMCName());
            
            Object chunkProvider = nmsWorld.getClass().getMethod("getChunkProvider").invoke(nmsWorld);
            Object chunkGenerator = chunkProvider.getClass().getMethod("getChunkGenerator").invoke(chunkProvider);
            
            Field worldChunkManagerField = chunkGenerator.getClass().getSuperclass().getDeclaredField("b");
            worldChunkManagerField.setAccessible(true);
            Object worldChunkManager = worldChunkManagerField.get(chunkGenerator);
            if (!(boolean) worldChunkManager.getClass().getMethod("a", structureGeneratorClass).invoke(worldChunkManager, structureGenerator)) {
                sendErrorTheme(player, "Feature %s does not generate in your world", feature.name());
                return null;
            }
            
            Object structureSettings = chunkGenerator.getClass().getMethod("getSettings").invoke(chunkGenerator);
    
            Method m = chunkGenerator.getClass().getSuperclass().getDeclaredMethod("updateStructureSettings", nmsWorld.getClass().getSuperclass(), structureSettings.getClass());
            m.setAccessible(true);
            m.invoke(chunkGenerator, nmsWorld, structureSettings);
    
            long worldSeed = (long) nmsWorld.getClass().getMethod("getSeed").invoke(nmsWorld);
            Object structureManager = nmsWorld.getClass().getMethod("getStructureManager").invoke(nmsWorld);
            
            Object structureSettingsFeature = structureSettings.getClass().getMethod("a", structureGeneratorClass).invoke(structureSettings, structureGenerator);
            int spacing = (int) structureSettingsFeature.getClass().getMethod("a").invoke(structureSettingsFeature);
    
            Random seededRandom = (Random) Class.forName("net.minecraft.server." + version + ".SeededRandom").getConstructor().newInstance();
    
            Method chunkCoordIntPairMethod = structureGenerator.getClass().getMethod("a", structureSettingsFeature.getClass(), long.class, seededRandom.getClass(), int.class, int.class);
    
            Object chunkStatus = Class.forName("net.minecraft.server." + version + ".ChunkStatus").getField((feature.getBiome() == null ? "STRUCTURE_STARTS" : "BIOMES")).get(null);
    
            Method sectionPositionMethod = Class.forName("net.minecraft.server." + version + ".SectionPosition")
                    .getMethod("a", Class.forName("net.minecraft.server." + version + ".ChunkCoordIntPair"), int.class);
            
            Class<?> chunkClass = Class.forName("net.minecraft.server." + version + ".IStructureAccess");
            
            Polygon searchArea = SearchAreaHandler.getSearchAreaHandler().getSearchArea(player);
            
            for (int size = 0; size <= 100; ++size) {
                for (int xOffset = -size; xOffset <= size; ++xOffset) {
                    boolean xEnd = xOffset == -size || xOffset == size;
                    
                    for (int zOffset = -size; zOffset <= size; ++zOffset) {
                        
                        int newX = x + spacing * xOffset;
                        int newZ = z + spacing * zOffset;
                        
                        Object chunkCoordIntPair = chunkCoordIntPairMethod.invoke(structureGenerator, structureSettingsFeature, worldSeed, seededRandom, newX, newZ);
                        Field fieldX = chunkCoordIntPair.getClass().getField("x");
                        Field fieldZ = chunkCoordIntPair.getClass().getField("z");
                        int chunkCoordIntPairX = (Integer) fieldX.get(chunkCoordIntPair);
                        int chunkCoordIntPairZ = (Integer) fieldZ.get(chunkCoordIntPair);
                        
                        Object chunk = nmsWorld.getClass().getMethod("getChunkAt", int.class, int.class, chunkStatus.getClass())
                                .invoke(nmsWorld, chunkCoordIntPairX, chunkCoordIntPairZ, chunkStatus);
                        
                        if (feature.getBiome() != null) {
                            Object biomeIndex = chunk.getClass().getMethod("getBiomeIndex").invoke(chunk);
                            Object biome = biomeIndex.getClass().getMethod("getBiome", int.class, int.class, int.class).invoke(biomeIndex, newX, 0, newZ);
                            if (!biome.getClass().getSimpleName().equals(feature.getBiome())) {
                                continue;
                            }
                        }
                        
                        Object chunkPosPair = chunk.getClass().getMethod("getPos").invoke(chunk);
                        Object sectionPosition = sectionPositionMethod.invoke(null, chunkPosPair, 0);
                        Object structureStart = structureManager.getClass()
                                .getMethod("a", sectionPosition.getClass(), structureGeneratorClass, chunkClass)
                                .invoke(structureManager, sectionPosition, structureGenerator, chunk);
                        
                        if (structureStart != null && (boolean) structureStart.getClass().getMethod("e").invoke(structureStart)) {
                            Object pos = structureStart.getClass().getMethod("a").invoke(structureStart);
                            Integer finalX = (Integer) pos.getClass().getMethod("getX").invoke(pos);
                            Integer finalZ = (Integer) pos.getClass().getMethod("getZ").invoke(pos);
                            
                            if (searchArea.contains(finalX, finalZ)) {
                                return new Location(null, finalX, 0, finalZ);
                            }
                        }
                        
                        if (!xEnd && zOffset == -size) {
                            zOffset = size - 1;
                        }
                    }
                    
                    if (size == 0) {
                        break;
                    }
                }
            }
            
        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException | ClassNotFoundException | InstantiationException e) {
            sendErrorTheme(player, "Could not find feature %s, error %s", feature.name(), e.getMessage());
            e.printStackTrace();
            return null;
        }
        
        sendErrorTheme(player, "Could not find the feature %s nearby", feature.name());
        return null;
    }
    

//    private static Location featureFinder(Location startLocation, FeatureTP.FeatureType feature) {
//        try {
//            String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
//            Object nmsWorld = Objects.requireNonNull(startLocation.getWorld()).getClass().getMethod("getHandle").invoke(startLocation.getWorld());
//            Object blockPos = Class.forName("net.minecraft.server." + version + ".BlockPosition").getConstructor(int.class, int.class, int.class)
//                    .newInstance(startLocation.getBlockX(), startLocation.getBlockY(), startLocation.getBlockZ());
//            Object finalBlockPos = nmsWorld.getClass().getMethod("a", String.class, blockPos.getClass(), int.class, boolean.class)
//                    .invoke(nmsWorld, feature.name(), blockPos, 100, false);
//            Object x = finalBlockPos.getClass().getMethod("getX").invoke(finalBlockPos);
//            Object z = finalBlockPos.getClass().getMethod("getZ").invoke(finalBlockPos);
//            return new Location(startLocation.getWorld(), (int) x, 0, (int) z);
//        } catch (Exception ignore) {
//            return null;
//        }
//    }

//    private static Location featureFinder(Location startLocation, FeatureTP.FeatureType feature) {
//
//        int x = startLocation.getBlockX() >> 4;
//        int z = startLocation.getBlockZ() >> 4;
//        World world = startLocation.getWorld();
//
//        try {
//            Object nmsWorld = Objects.requireNonNull(world).getClass().getMethod("getHandle").invoke(world);
//            Object chunkProvider = nmsWorld.getClass().getMethod("getChunkProvider").invoke(nmsWorld);
//            Object chunkGenerator = chunkProvider.getClass().getMethod("getChunkGenerator").invoke(chunkProvider);
//
//            String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
//            Field ao = Class.forName("net.minecraft.server." + version + ".WorldGenerator").getField("ao");
//
//
//            ao.setAccessible(true);
//            //noinspection rawtypes
//            Object structureGenerator = ((BiMap) ao.get(null)).get(feature.getMCName().toLowerCase());
//
//            Object worldChunkManager = chunkGenerator.getClass().getMethod("getWorldChunkManager").invoke(chunkGenerator);
//            Class<?> structureGeneratorClass = Class.forName("net.minecraft.server.v1_15_R1.StructureGenerator");
//
//            if (!(boolean) worldChunkManager.getClass().getMethod("a", structureGeneratorClass).invoke(worldChunkManager, structureGenerator)) {
//                return null;
//            }
//
//            Method m;
//            try {
//                Class<?> chunkGeneratorClass = Class.forName("net.minecraft.server." + version + ".ChunkGenerator");
//                m = structureGenerator.getClass().getSuperclass().getDeclaredMethod("a", chunkGeneratorClass, Random.class, int.class, int.class, int.class, int.class);
//                m.setAccessible(true);
//            } catch (NoSuchMethodException e) {
//                e.printStackTrace();
//                return null;
//            }
//
//            Random seededRandom = (Random) Class.forName("net.minecraft.server." + version + ".SeededRandom").getConstructor().newInstance();
//
//            for (int size = 0; size <= 100; ++size) {
//
//                for (int xOffset = -size; xOffset <= size; ++xOffset) {
//                    boolean xEnd = xOffset == -size || xOffset == size;
//
//                    for (int zOffset = -size; zOffset <= size; ++zOffset) {
//                        Pair<Integer, Integer> chunkCoordIntPair = getChunkCoords(m, structureGenerator, chunkGenerator, seededRandom, x, z, xOffset, zOffset);
//
//                        Location loc = hasFeature(nmsWorld, chunkCoordIntPair.getLeft(), chunkCoordIntPair.getRight(), feature);
//                        if (loc != null) {
//                            loc.setWorld(world);
//                            return loc;
//                        }
//
//                        if (!xEnd && zOffset == -size) {
//                            zOffset = size - 1;
//                        }
//                    }
//
//                    if (size == 0) {
//                        break;
//                    }
//                }
//            }
//
//        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException | InstantiationException | NoSuchFieldException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    private static Pair<Integer, Integer> getChunkCoords(Method m, Object structureGenerator, Object chunkGenerator, Random r, int x, int z, int xOffset, int zOffset) {
//        try {
//            Object chunkCoordIntPair = m.invoke(structureGenerator, chunkGenerator, r, x, z, xOffset, zOffset);
//            Field fieldX = chunkCoordIntPair.getClass().getField("x");
//            Field fieldZ = chunkCoordIntPair.getClass().getField("z");
//            fieldX.setAccessible(true);
//            fieldZ.setAccessible(true);
//            Integer newX = (Integer) fieldX.get(chunkCoordIntPair);
//            Integer newZ = (Integer) fieldZ.get(chunkCoordIntPair);
//            return new Pair<>(newX, newZ);
//        } catch (IllegalAccessException | InvocationTargetException | NoSuchFieldException ignore) {
//            return new Pair<>(x + xOffset, z + zOffset);
//        }
//    }
//
//    private static Location hasFeature(Object nmsWorld, int chunkX, int chunkZ, FeatureTP.FeatureType feature) {
//        try {
//            String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
//            Field chunkStatus = Class.forName("net.minecraft.server." + version + ".ChunkStatus").getField((feature.getBiome() == null ? "STRUCTURE_STARTS" : "BIOMES"));
//            chunkStatus.setAccessible(true);
//
//            Object chunk = nmsWorld.getClass().getMethod("getChunkAt", int.class, int.class, chunkStatus.getDeclaringClass())
//                    .invoke(nmsWorld, chunkX, chunkZ, chunkStatus.get(null));
//
//            Object structureStart = chunk.getClass().getMethod("a", String.class).invoke(chunk, feature.getMCName());
//
//            if (structureStart != null && (boolean) structureStart.getClass().getMethod("e").invoke(structureStart)) {
//                Object pos = structureStart.getClass().getMethod("a").invoke(structureStart);
//                Integer newX = (Integer) pos.getClass().getMethod("getX").invoke(pos);
//                Integer newZ = (Integer) pos.getClass().getMethod("getZ").invoke(pos);
//
//                if (feature.getBiome() != null) {
//                    Object biomeIndex = chunk.getClass().getMethod("getBiomeIndex").invoke(chunk);
//                    Object biome = biomeIndex.getClass().getMethod("getBiome", int.class, int.class, int.class).invoke(biomeIndex, newX, 0, newZ);
//
//                    if (!biome.getClass().getSimpleName().equals(feature.getBiome())) {
//                        return null;
//                    }
//
//                }
//
//                return new Location(null, newX, 0, newZ);
//            } else {
//                return null;
//            }
//
//        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException | NoSuchFieldException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
}
