package com.spaceman.tport.adapters;

import com.spaceman.tport.Main;
import com.spaceman.tport.Pair;
import com.spaceman.tport.biomeTP.BiomePreset;
import com.spaceman.tport.commands.tport.FeatureTP;
import com.spaceman.tport.commands.tport.biomeTP.Accuracy;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.core.QuartPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.WorldServer;
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
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class V1_21_4_BiomeTPAdapter extends V1_21_4_FeatureTPAdapter {
    
    private IRegistry<BiomeBase> getBiomeRegistry(WorldServer worldServer) {
        return worldServer.K_().e(Registries.aI);
    }
    
    @Override
    public List<String> availableBiomes() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        World world = Bukkit.getWorlds().get(0);
        WorldServer worldServer = (WorldServer) getWorldServer(world);
        IRegistry<BiomeBase> biomeRegistry = getBiomeRegistry(worldServer);
        return biomeRegistry.i().stream().map(MinecraftKey::a).map(String::toLowerCase).collect(Collectors.toList());
    }
    
    @Override
    public List<String> availableBiomes(World world) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        WorldServer worldServer = (WorldServer) getWorldServer(world);
        
        ChunkGenerator chunkGenerator = worldServer.m().g();
        IRegistry<BiomeBase> biomeRegistry = getBiomeRegistry(worldServer);
        WorldChunkManager worldChunkManager = chunkGenerator.d();
        
        return worldChunkManager.c().stream()
                .map((b) -> biomeRegistry.b(b.a()))
                .filter(Objects::nonNull)
                .map(MinecraftKey::a)
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
        
        WorldServer worldServer = (WorldServer) getWorldServer(world);
        
        ChunkGenerator chunkGenerator = worldServer.m().g();
        
        WorldChunkManager worldChunkManager = chunkGenerator.d();
        
        IRegistry<BiomeBase> biomeRegistry = getBiomeRegistry(worldServer);
        List<BiomeBase> baseList = biomes.stream().map(biome -> biomeRegistry.a(MinecraftKey.b(biome.toLowerCase()))).filter(Objects::nonNull).toList();
        
        Predicate<Holder<BiomeBase>> predicate = (holder) -> baseList.stream().anyMatch((biomeBase) -> biomeBase.equals(holder.a()));
        
        Location blockPos;
        Climate.Sampler climateSampler = worldServer.m().h().c().b();
        
        for (int squareSize = 0; squareSize <= quartSize; squareSize += increment) {
            for (int zOffset = -squareSize; zOffset <= squareSize; zOffset += increment) {
                boolean zEnd = Math.abs(zOffset) == squareSize;
                
                for (int xOffset = -squareSize; xOffset <= squareSize; xOffset += increment) {
                    boolean xEnd = Math.abs(zOffset) == squareSize;
                    if (!zEnd && !xEnd) continue;
                    
                    int newX = quartX + xOffset;
                    int newZ = quartZ + zOffset;
                    
                    if (!searchArea.contains(QuartPos.c(newX), QuartPos.c(newZ))) {
                        continue;
                    }
                    
                    for (int y : yLevels) {
                        int newY = QuartPos.a(y);
                        blockPos = new Location(player.getWorld(), QuartPos.c(newX), startY, QuartPos.c(newZ));
                        
                        Holder<BiomeBase> currentBiome = worldChunkManager.getNoiseBiome(newX, newY, newZ, climateSampler);
                        
                        if (predicate.test(currentBiome)) {
                            return new Pair<>(blockPos, biomeRegistry.b(currentBiome.a()).a());
                        }
                    }
                }
            }
        }
        return null;
    }
    
    @Override
    public ArrayList<BiomePreset> loadPresetsFromWorld(World world) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        WorldServer worldServer = (WorldServer) getWorldServer(world);
        
        IRegistry<BiomeBase> biomeRegistry = getBiomeRegistry(worldServer);
        
        ArrayList<BiomePreset> presets = new ArrayList<>();
        
        biomeRegistry.l().forEach( (named) -> {
            Stream<Holder<BiomeBase>> values = named.a();
            
            List<String> biomes = values
                    .map((holder) -> holder.a())
                    .map(biomeRegistry::b)
                    .filter(Objects::nonNull)
                    .map((key) -> key.a().toLowerCase())
                    .toList();
            
            String tagKeyName = named.h().b().a().toLowerCase();
            
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
