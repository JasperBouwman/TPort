package com.spaceman.tport.adapters;

import com.spaceman.tport.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.WorldChunkManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.placement.ConcentricRingsStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;

public abstract class V1_18_2_FeatureTPAdapter extends TPortAdapter {
    
    private IRegistry<StructureFeature<?, ?>> getStructureRegistry(WorldServer worldServer) {
        return worldServer.s().d(IRegistry.aL);
    }
    
    @Override
    public Pair<Location, String> searchFeature(Player player, Location startLocation, List<String> features) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        List<Holder<StructureFeature<?,?>>> featureList = new ArrayList<>();
        
        WorldServer worldServer = (WorldServer) getWorldServer(player.getWorld());
        IRegistry<StructureFeature<?,?>> structureRegistry = getStructureRegistry(worldServer);
        for (String feature : features) {
            StructureFeature<?,?> structure = structureRegistry.a(new MinecraftKey(feature));
            Optional<ResourceKey<StructureFeature<?,?>>> optional = structureRegistry.c(structure);
            if (optional.isPresent()) {
                Holder<StructureFeature<?,?>> holder = structureRegistry.c(optional.get());
                featureList.add(holder);
            }
        }
        
        return featureFinder(player, startLocation, featureList);
    }
    
    private double distToLowCornerSqr(BlockPosition b1, BlockPosition b2) {
        double deltaX = b1.u() - b2.u();
        double deltaY = b1.v() - b2.v();
        double deltaZ = b1.w() - b2.w();
        return deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
    }
    
    private Pair<Location, String> featureFinder(@Nullable Player player, Location startLocation, List<Holder<StructureFeature<?,?>>> featureList) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        BlockPosition startPosition = new BlockPosition(startLocation.getBlockX(), startLocation.getBlockY(), startLocation.getBlockZ());
        
        WorldServer worldServer = (WorldServer) getWorldServer(startLocation.getWorld());
        IRegistry<StructureFeature<?, ?>> structureRegistry = getStructureRegistry(worldServer);
        
        Set<Holder<BiomeBase>> generateInBiomesList = featureList.stream().flatMap((holder) -> holder.a().a().a()).collect(Collectors.toSet());
        
        if (generateInBiomesList.isEmpty()) {
            sendErrorTranslation(player, "tport.command.featureTP.search.feature.featuresNotGenerating");
            return null; //does not generate at all
        }
        
        ChunkGenerator chunkGenerator = worldServer.k().g();
        WorldChunkManager worldChunkManager = chunkGenerator.e();
        
        Set<Holder<BiomeBase>> generatedBiomes = worldChunkManager.b();
        if (Collections.disjoint(generatedBiomes, generateInBiomesList)) {
            sendErrorTranslation(player, "tport.command.featureTP.search.feature.featuresNotGeneratingInWorld");
            return null; //does not generate in world
        }
        
        com.mojang.datafixers.util.Pair<BlockPosition, Holder<StructureFeature<?, ?>>> closestPair = null;
        Map<StructurePlacement, Set<Holder<StructureFeature<?, ?>>>> placementMap = new Object2ObjectArrayMap<>();
        
        //this for loop collects all structure placements and their structures
        for (Holder<StructureFeature<?, ?>> structureHolder : featureList) {
            HolderSet<BiomeBase> generateInBiomes = structureHolder.a().a();
            if (generatedBiomes.stream().anyMatch(generateInBiomes::a)) {
                
                Method m = ChunkGenerator.class.getDeclaredMethod("b", Holder.class);
                m.setAccessible(true);
                List<StructurePlacement> l = (List<StructurePlacement>) m.invoke(chunkGenerator, structureHolder);
                for (StructurePlacement structureplacement : l) {
                    placementMap.computeIfAbsent(structureplacement, (unusedStructurePlacement) -> new ObjectArraySet()).add(structureHolder);
                }
            }
        }
        
        List<Map.Entry<StructurePlacement, Set<Holder<StructureFeature<?, ?>>>>> placementList = new ArrayList<>(placementMap.size());
        double closestDistance = Double.MAX_VALUE;
        
        //this for loop re-collects all structure placements, and only the closest concentric ring structure placements (strongholds)
        for (Map.Entry<StructurePlacement, Set<Holder<StructureFeature<?, ?>>>> entry : placementMap.entrySet()) {
            StructurePlacement structureplacement = entry.getKey();
            if (structureplacement instanceof ConcentricRingsStructurePlacement concentricringsstructureplacement) {
                Method m = ChunkGenerator.class.getDeclaredMethod("a", BlockPosition.class, ConcentricRingsStructurePlacement.class);
                m.setAccessible(true);
                BlockPosition blockPos = (BlockPosition) m.invoke(chunkGenerator, startPosition, concentricringsstructureplacement);
                double distance = distToLowCornerSqr(startPosition, blockPos);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestPair = com.mojang.datafixers.util.Pair.of(blockPos, entry.getValue().iterator().next());
                }
            } else if (structureplacement instanceof RandomSpreadStructurePlacement) {
                placementList.add(entry);
            }
        }
        
        if (!placementList.isEmpty()) {
            int sectionX = startLocation.getBlockX() >> 4; //block to section coord
            int sectionZ = startLocation.getBlockZ() >> 4; //block to section coord
            
            //this for loop checks for the closest structure placement
            for (int squareSize = 0; squareSize <= 100; ++squareSize) {
                boolean foundThisRound = false;
                
                for (Map.Entry<StructurePlacement, Set<Holder<StructureFeature<?, ?>>>> entry : placementList) {
                    RandomSpreadStructurePlacement randomspreadstructureplacement = (RandomSpreadStructurePlacement) entry.getKey();
                    
                    Method m = ChunkGenerator.class.getDeclaredMethod("a", Set.class, IWorldReader.class, StructureManager.class, int.class, int.class, int.class, boolean.class, long.class, RandomSpreadStructurePlacement.class);
                    m.setAccessible(true);
                    com.mojang.datafixers.util.Pair<BlockPosition, Holder<StructureFeature<?, ?>>> pairCandidate = (com.mojang.datafixers.util.Pair<BlockPosition, Holder<StructureFeature<?, ?>>>) m
                            .invoke(chunkGenerator, entry.getValue(), worldServer, worldServer.a(), sectionX, sectionZ, squareSize, false, worldServer.D(), randomspreadstructureplacement);
                    if (pairCandidate != null) {
                        foundThisRound = true;
                        BlockPosition blockPos = pairCandidate.getFirst();
                        double distance = distToLowCornerSqr(startPosition, blockPos);
                        if (distance < closestDistance) {
                            closestDistance = distance;
                            closestPair = pairCandidate;
                        }
                    }
                }
                
                if (foundThisRound) {
                    //closestPair should never be null, it is: stronghold, or: closest structure.
                    if (closestPair == null) {
                        return null;
                    }
                    
                    BlockPosition blockPos = closestPair.getFirst();
                    return new Pair<>(new Location(startLocation.getWorld(), blockPos.u(), 200, blockPos.w()),
                            structureRegistry.b(closestPair.getSecond().a()).a());
                }
            }
        }
        
        if (closestPair == null) {
            return null;
        }
        BlockPosition blockPos = closestPair.getFirst();
        return new Pair<>(new Location(startLocation.getWorld(), blockPos.u(), 200, blockPos.w()),
                structureRegistry.b(closestPair.getSecond().a()).a());
    }
    
    @Override
    public List<String> availableFeatures() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        World world = Bukkit.getWorlds().get(0);
        WorldServer worldServer = (WorldServer) getWorldServer(world);
        IRegistry<StructureFeature<?,?>> structureRegistry = getStructureRegistry(worldServer);
        
        List<String> list = new ArrayList<>();
        for (MinecraftKey minecraftKey : structureRegistry.d()) {
            String lowerCase = minecraftKey.a().toLowerCase();
            list.add(lowerCase);
        }
        return list;
    }
    
    @Override
    public List<String> availableFeatures(World world) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        List<String> returnList = new ArrayList<>();
        
        WorldServer worldServer = (WorldServer) getWorldServer(world);
        IRegistry<StructureFeature<?,?>> structureRegistry = getStructureRegistry(worldServer);
        
        ChunkGenerator chunkGenerator = worldServer.k().g();
        WorldChunkManager worldChunkManager = chunkGenerator.e();
        Set<Holder<BiomeBase>> generatedBiomes = worldChunkManager.b();
        
        for (MinecraftKey minecraftKey : structureRegistry.d()) {
            StructureFeature<?,?> structure = structureRegistry.a(minecraftKey);
            
            Set<Holder<BiomeBase>> generateInBiomesList = new HashSet<>();
            HolderSet<BiomeBase> biomes = structure.a();
            Stream<Holder<BiomeBase>> biomeStream = biomes.a();
            biomeStream.forEach(generateInBiomesList::add);
            
            if (generateInBiomesList.isEmpty()) {
                continue; //does not generate at all
            }
            
            if (Collections.disjoint(generatedBiomes, generateInBiomesList)) {
                continue; //does not generate in world
            }
            returnList.add(minecraftKey.a());
        }
        
        return returnList;
    }
    
    @Override
    public List<com.spaceman.tport.Pair<String, List<String>>> getFeatureTags(World world) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, ClassNotFoundException {
        List<com.spaceman.tport.Pair<String, List<String>>> list = new ArrayList<>();
        
        WorldServer worldServer = (WorldServer) getWorldServer(world);
        IRegistry<StructureFeature<?,?>> structureRegistry = getStructureRegistry(worldServer);
        
        List<String> tags = structureRegistry.h().map((tagKey) -> tagKey.b().a()).toList();
        
        for (String tagKeyName : tags) {
            TagKey<StructureFeature<?,?>> tagKey = TagKey.a(IRegistry.aL, new MinecraftKey(tagKeyName));
            
            Optional<HolderSet.Named<StructureFeature<?,?>>> optional = structureRegistry.c(tagKey);
            if (optional.isPresent()) {
                HolderSet.Named<StructureFeature<?,?>> named = optional.get();
                Stream<Holder<StructureFeature<?,?>>> values = named.a();
                
                List<String> features = values
                        .map((holder) -> holder.a())
                        .map(structureRegistry::b)
                        .filter(Objects::nonNull)
                        .map((key) -> key.a().toLowerCase())
                        .toList();
                
                list.add(new com.spaceman.tport.Pair<>("#" + tagKeyName.toLowerCase(), features));
            }
        }
        return list;
    }

}
