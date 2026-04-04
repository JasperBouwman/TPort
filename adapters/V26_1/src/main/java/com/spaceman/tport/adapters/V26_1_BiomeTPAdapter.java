package com.spaceman.tport.adapters;

import com.spaceman.tport.Main;
import com.spaceman.tport.Pair;
import com.spaceman.tport.biomeTP.BiomePreset;
import com.spaceman.tport.commands.tport.FeatureTP;
import com.spaceman.tport.commands.tport.biomeTP.Accuracy;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
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
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class V26_1_BiomeTPAdapter extends V26_1_FeatureTPAdapter {
    
    private Registry<Biome> getBiomeRegistry(ServerLevel worldServer) {
        return worldServer.registryAccess().lookupOrThrow(Registries.BIOME);
    }
    
    @Override
    public List<String> availableBiomes() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        World world = Bukkit.getWorlds().getFirst();
        ServerLevel worldServer = (ServerLevel) getWorldServer(world);
        Registry<Biome> biomeRegistry = getBiomeRegistry(worldServer);
        return biomeRegistry.keySet().stream().map(Identifier::getPath).map(String::toLowerCase).collect(Collectors.toList());
    }
    
    @Override
    public List<String> availableBiomes(World world) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ServerLevel worldServer = (ServerLevel) getWorldServer(world);
        
        ChunkGenerator chunkGenerator = worldServer.getChunkSource().getGenerator();
        Registry<Biome> biomeRegistry = getBiomeRegistry(worldServer);
        BiomeSource worldChunkManager = chunkGenerator.getBiomeSource();
        
        return worldChunkManager.possibleBiomes().stream()
                .map((biomeHolder) -> biomeRegistry.getKey(biomeHolder.value()))
                .filter(Objects::nonNull)
                .map(Identifier::getPath)
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }
    
    @Override
    public Pair<Location, String> biomeFinder(Player player, List<String> biomes, @Nonnull Location startLocation, Accuracy.AccuracySettings accuracy) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
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
        
        ServerLevel worldServer = (ServerLevel) getWorldServer(world);
        
        ChunkGenerator chunkGenerator = worldServer.getChunkSource().getGenerator();
        
        BiomeSource worldChunkManager = chunkGenerator.getBiomeSource();
        
        Registry<Biome> biomeRegistry = getBiomeRegistry(worldServer);
        List<Biome> baseList = biomes.stream().map(biome -> biomeRegistry.getValue(Identifier.withDefaultNamespace(biome.toLowerCase()))).filter(Objects::nonNull).toList();
        
        Predicate<Holder<Biome>> predicate = (biomeHolder) -> baseList.stream().anyMatch((biomeBase) -> biomeBase.equals(biomeHolder.value()));
        
        Location blockPos;
        Climate.Sampler climateSampler = worldServer.getChunkSource().getGeneratorState().randomState().sampler();
        
        for (int squareSize = 0; squareSize <= quartSize; squareSize += increment) {
            for (int zOffset = -squareSize; zOffset <= squareSize; zOffset += increment) {
                boolean zEnd = Math.abs(zOffset) == squareSize;
                
                for (int xOffset = -squareSize; xOffset <= squareSize; xOffset += increment) {
                    boolean xEnd = Math.abs(zOffset) == squareSize;
                    if (!zEnd && !xEnd) continue;
                    
                    int newX = quartX + xOffset;
                    int newZ = quartZ + zOffset;
                    
                    if (!searchArea.contains(QuartPos.toBlock(newX), QuartPos.toBlock(newZ))) {
                        continue;
                    }
                    
                    for (int y : yLevels) {
                        int newY = QuartPos.fromBlock(y);
                        blockPos = new Location(player.getWorld(), QuartPos.toBlock(newX), startY, QuartPos.toBlock(newZ));
                        
                        Holder<Biome> currentBiome = worldChunkManager.getNoiseBiome(newX, newY, newZ, climateSampler);
                        
                        if (predicate.test(currentBiome)) {
                            return new Pair<>(blockPos, biomeRegistry.getKey(currentBiome.value()).getPath());
                        }
                    }
                }
            }
        }
        return null;
    }
    
    @Override
    public ArrayList<BiomePreset> loadPresetsFromWorld(World world) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ServerLevel worldServer = (ServerLevel) getWorldServer(world);
        
        Registry<Biome> biomeRegistry = getBiomeRegistry(worldServer);
        
        ArrayList<BiomePreset> presets = new ArrayList<>();
        
        biomeRegistry.getTags().forEach( (named) -> {
            Stream<Holder<Biome>> values = named.stream();
            
            List<String> biomes = values
                    .map(Holder::value)
                    .map(biomeRegistry::getKey)
                    .filter(Objects::nonNull)
                    .map((key) -> key.getPath().toLowerCase())
                    .toList();
            
            String tagKeyName = named.key().location().getPath().toLowerCase();
            
            Material material;
            if (tagKeyName.startsWith("has_structure/")) {
                material = FeatureTP.getMaterial(tagKeyName.substring(14));
            } else {
                String materialName = BiomePreset.getMaterialName(tagKeyName);
                material = Main.getOrDefault(Material.getMaterial(materialName), Material.DIAMOND_BLOCK);
            }
            
            presets.add(new BiomePreset("#" + tagKeyName, biomes, true, material, true));
            
        });
        
        return presets;
    }
}
