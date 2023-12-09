package com.spaceman.tport.adapters;

import com.spaceman.tport.Main;
import com.spaceman.tport.Pair;
import com.spaceman.tport.commands.tport.BiomeTP;
import com.spaceman.tport.commands.tport.FeatureTP;
import com.spaceman.tport.commands.tport.Features;
import com.spaceman.tport.commands.tport.biomeTP.Accuracy;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.IRegistry;
import net.minecraft.core.QuartPos;
import net.minecraft.core.registries.Registries;
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
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class V1_19_4_BiomeTPAdapter extends V1_19_4_FeatureTPAdapter {
    
    private boolean legacyBiomeTP = false;
    
    @Override
    public List<String> availableBiomes() {
        if (!legacyBiomeTP) {
            try {
                World world = Bukkit.getWorlds().get(0);
                WorldServer worldServer = (WorldServer) getWorldServer(world);
                
                IRegistry<BiomeBase> biomeRegistry = worldServer.u_().d(Registries.an); //1.19.4
                return biomeRegistry.e().stream().map(MinecraftKey::a).map(String::toLowerCase).collect(Collectors.toList());
            } catch (Throwable ex) {
                Features.Feature.printSmallNMSErrorInConsole("BiomeTP biome list", true);
                if (Features.Feature.PrintErrorsInConsole.isEnabled()) ex.printStackTrace();
                legacyBiomeTP = true;
            }
        }
        return Arrays.stream(Biome.values()).map(Enum::name).map(String::toLowerCase).collect(Collectors.toList());
    }
    
    @Override
    public List<String> availableBiomes(World world) {
        if (!legacyBiomeTP) {
            try {
                WorldServer worldServer = (WorldServer) getWorldServer(world);
                
                ChunkGenerator chunkGenerator = worldServer.k().g();
                IRegistry<BiomeBase> biomeRegistry = worldServer.u_().d(Registries.an); //1.19.4
                
                Field f = ChunkGenerator.class.getDeclaredField("b"); //1.19.3
                
                f.setAccessible(true);
                WorldChunkManager worldChunkManager = (WorldChunkManager) f.get(chunkGenerator);
                
                return worldChunkManager.c().stream()
                        .map((b) -> biomeRegistry.b(b.a()))
                        .filter(Objects::nonNull)
                        .map(MinecraftKey::a)
                        .map(String::toLowerCase)
                        .collect(Collectors.toList());
            } catch (Throwable ex) {
                Features.Feature.printSmallNMSErrorInConsole("BiomeTP per world biome list", true);
                if (Features.Feature.PrintErrorsInConsole.isEnabled()) ex.printStackTrace();
                legacyBiomeTP = true;
            }
        }
        return availableBiomes();
    }
    
    @Override
    public Pair<Location, String> biomeFinder(Player player, List<String> biomes, @Nonnull Location startLocation, Accuracy.AccuracySettings accuracy) {
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
        
        if (!legacyBiomeTP) {
            try {
                Object nmsWorld = Objects.requireNonNull(world).getClass().getMethod("getHandle").invoke(world);
                WorldServer worldServer = (WorldServer) nmsWorld;
                
                ChunkGenerator chunkGenerator = worldServer.k().g();

                WorldChunkManager worldChunkManager = chunkGenerator.c();

                IRegistry<BiomeBase> biomeRegistry = worldServer.u_().d(Registries.an);
                List<BiomeBase> baseList = biomes.stream().map(biome -> biomeRegistry.a(new MinecraftKey(biome.toLowerCase()))).filter(Objects::nonNull).toList();
                
                Predicate<Holder<BiomeBase>> predicate = (holder) -> baseList.stream().anyMatch((biomeBase) -> biomeBase.equals(holder.a()));
                
                Location blockPos;
                Climate.Sampler climateSampler = worldServer.k().h().c().b();
                
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
            } catch (Exception e) {
                Main.getInstance().getLogger().log(Level.WARNING, "Can't use NMS (try updating TPort), using legacy mode for BiomeTP");
                legacyBiomeTP = true;
            }
        }
        
        return this.legacyBiomeTP(quartSize, quartX, quartZ, searchArea, increment, yLevels, world, biomes);
    }
    
    @Override
    public ArrayList<BiomeTP.BiomeTPPresets.BiomePreset> loadPresetsFromWorld(World world) {
        if (legacyBiomeTP) return null;
        
        try {
            Object nmsWorld = Objects.requireNonNull(world).getClass().getMethod("getHandle").invoke(world);
            WorldServer worldServer = (WorldServer) nmsWorld;

            IRegistry<BiomeBase> biomeRegistry = worldServer.u_().d(Registries.an);
            
            ArrayList<BiomeTP.BiomeTPPresets.BiomePreset> presets = new ArrayList<>();
            
            for (String tagKeyName : biomeRegistry.i().map((tagKey) -> tagKey.getFirst().b().a()).toList()) {
                TagKey<BiomeBase> tagKey = TagKey.a(Registries.an, new MinecraftKey(tagKeyName));

                Optional<HolderSet.Named<BiomeBase>> optional = biomeRegistry.b(tagKey);
                if (optional.isPresent()) {
                    HolderSet.Named<BiomeBase> named = optional.get();
                    Stream<Holder<BiomeBase>> values = named.a();
                    
                    List<String> biomes = values.map((holder) -> {
                        return holder.a(); //Holder -> BiomeBase
                    }).map((biomeBase) -> {
                        MinecraftKey key = biomeRegistry.b(biomeBase);
                        if (key != null) {
                            return key.a().toLowerCase();
                        }
                        return null;
                    }).filter(Objects::nonNull).toList();
                    
                    Material material;
                    if (tagKeyName.startsWith("has_structure/")) {
                        material = FeatureTP.getMaterial(tagKeyName.substring(14));
                    } else {
                        String materialName = BiomeTP.BiomeTPPresets.getMaterialName(tagKeyName);
                        material = Main.getOrDefault(Material.getMaterial(materialName), Material.DIAMOND_BLOCK);
                    }
                    
                    presets.add(new BiomeTP.BiomeTPPresets.BiomePreset("#" + tagKeyName, biomes, true, material, true));
                }
            }
            
            return presets;
        } catch (Exception e) {
            Main.getInstance().getLogger().log(Level.WARNING, "Can't use NMS (try updating TPort), using legacy mode for BiomeTP");
            legacyBiomeTP = true;
        }
        return null;
    }
}
