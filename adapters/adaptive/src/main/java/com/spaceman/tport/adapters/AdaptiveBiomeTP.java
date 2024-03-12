package com.spaceman.tport.adapters;

import com.spaceman.tport.Main;
import com.spaceman.tport.Pair;
import com.spaceman.tport.biomeTP.BiomePreset;
import com.spaceman.tport.commands.tport.FeatureTP;
import com.spaceman.tport.commands.tport.biomeTP.Accuracy;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.WorldChunkManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.spaceman.tport.adapters.AdaptiveReflectionManager.*;

public abstract class AdaptiveBiomeTP extends AdaptiveFeatureTP {
    
    @Override
    public List<String> availableBiomes() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, ClassNotFoundException {
        World world = Bukkit.getWorlds().get(0);
        WorldServer worldServer = (WorldServer) getWorldServer(world);
        
        IRegistry<BiomeBase> biomeRegistry = getBiomeRegistry(worldServer);
        List<String> list = new ArrayList<>();
        for (MinecraftKey key : keySet_fromRegistry(biomeRegistry)) {
            String lowerCase = getPathFromMinecraftKey(key).toLowerCase();
            list.add(lowerCase);
        }
        return list;
    }
    
    @Override
    public List<String> availableBiomes(World world) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        WorldServer worldServer = (WorldServer) getWorldServer(world);
        
        IRegistry<BiomeBase> biomeRegistry = getBiomeRegistry(worldServer);
        ChunkGenerator chunkGenerator = getChunkGenerator(worldServer);
        WorldChunkManager worldChunkManager = getWorldChunkManager(chunkGenerator);
        
        List<String> list = new ArrayList<>();
        for (Holder<BiomeBase> biomeHolder : getGeneratedBiomes(worldChunkManager)) {
            MinecraftKey key = getKeyFromRegistry(biomeRegistry, biomeHolder);
            if (key != null) {
                list.add(getPathFromMinecraftKey(key).toLowerCase());
            }
        }
        return list;
    }
    
    @Override
    public Pair<Location, String> biomeFinder(Player player, List<String> biomes, @Nonnull Location startLocation, Accuracy.AccuracySettings accuracy) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, ClassNotFoundException {
        int startX = (startLocation.getBlockX());
        int startY = (startLocation.getBlockY());
        int startZ = (startLocation.getBlockZ());
        World world = startLocation.getWorld();
        if (world == null) return null;
        Rectangle searchArea = Main.getSearchArea(player);
        
        int size = accuracy.getRange();
        List<Integer> yLevels = accuracy.getYLevels();
        int increment = accuracy.getIncrement();
        
        int quartSize = size >> 2;
        int quartX = startX >> 2;
        int quartZ = startZ >> 2;
        
        WorldServer worldServer = (WorldServer) getWorldServer(world);
        ChunkGenerator chunkGenerator = getChunkGenerator(worldServer);
        WorldChunkManager worldChunkManager = getWorldChunkManager(chunkGenerator);
        IRegistry<BiomeBase> biomeRegistry = getBiomeRegistry(worldServer);
        List<BiomeBase> baseList = new ArrayList<>();
        for (String biome : biomes) {
            BiomeBase biomeBase = getFromRegistry(biomeRegistry, biome.toLowerCase());
            if (biomeBase != null) baseList.add(biomeBase);
        }
        
        Predicate<Holder<BiomeBase>> predicate = (holder) -> {
            for (BiomeBase biomeBase : baseList) {
                try {
                    if (biomeBase.equals(getValueFromHolder(holder))) {
                        return true;
                    }
                } catch (InvocationTargetException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            return false;
        };
        
        Location blockPos;
        Climate.Sampler climateSampler = getClimateSampler(worldServer);
        
        for (int squareSize = 0; squareSize <= quartSize; squareSize += increment) {
            for (int zOffset = -squareSize; zOffset <= squareSize; zOffset += increment) {
                boolean zEnd = Math.abs(zOffset) == squareSize;
                
                for (int xOffset = -squareSize; xOffset <= squareSize; xOffset += increment) {
                    boolean xEnd = Math.abs(zOffset) == squareSize;
                    if (!zEnd && !xEnd) continue;
                    
                    int newX = quartX + xOffset;
                    int newZ = quartZ + zOffset;
                    
                    if (!searchArea.contains(newX << 2, newZ << 2)) {
                        continue;
                    }
                    
                    for (int y : yLevels) {
                        int newY = y >> 2;
                        
                        Holder<BiomeBase> currentBiome = worldChunkManager.getNoiseBiome(newX, newY, newZ, climateSampler);
                        
                        if (predicate.test(currentBiome)) {
                            blockPos = new Location(player.getWorld(), newX << 2, startY, newZ << 2);
                            MinecraftKey k = getKeyFromRegistry(biomeRegistry, currentBiome);
                            return new Pair<>(blockPos, getPathFromMinecraftKey(k));
                        }
                    }
                }
            }
        }
        return null;
    }
    
    @Override
    public ArrayList<BiomePreset> loadPresetsFromWorld(World world) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, ClassNotFoundException {
        WorldServer worldServer = (WorldServer) getWorldServer(world);
        IRegistry<BiomeBase> biomeRegistry = getBiomeRegistry(worldServer);
        ArrayList<BiomePreset> presets = new ArrayList<>();
        
        List<String> tags = getTags(biomeRegistry).map((tagKey) -> {
            try {
                return getPathFromMinecraftKey(getMinecraftKeyFromTag(tagKey.getFirst()));
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }).toList();
        
        for (String tagKeyName : tags) {
            TagKey<BiomeBase> tagKey = TagKey.a(getBiomeResourceKey(), new MinecraftKey(tagKeyName)); //todo finish reflection
            
            Optional<HolderSet.Named<BiomeBase>> optional = getTag(biomeRegistry, tagKey);
            if (optional.isPresent()) {
                HolderSet.Named<BiomeBase> named = optional.get();
                Stream<Holder<BiomeBase>> values = ReflectionManager.get(Stream.class, named);
                
                List<String> biomes = values.map(holder -> {
                    MinecraftKey key;
                    try {
                        key = getKeyFromRegistry(biomeRegistry, holder);
                    } catch (InvocationTargetException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                    if (key != null) {
                        return getPathFromMinecraftKey(key).toLowerCase();
                    }
                    return null;
                }).filter(Objects::nonNull).toList();
                
                Material material;
                if (tagKeyName.startsWith("has_structure/")) {
                    material = FeatureTP.getMaterial(tagKeyName.substring(14));
                } else {
                    String materialName = BiomePreset.getMaterialName(tagKeyName);
                    material = Main.getOrDefault(Material.getMaterial(materialName), Material.DIAMOND_BLOCK);
                }
                
                presets.add(new BiomePreset("#" + tagKeyName, biomes, true, material, true));
            }
        }
        return presets;
    }
}
