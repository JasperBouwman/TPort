package com.spaceman.tport.adapters;

import com.spaceman.tport.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.WorldChunkManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.Structure;
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

public abstract class V1_21_4_FeatureTPAdapter extends TPortAdapter {
    
    private IRegistry<Structure> getStructureRegistry(WorldServer worldServer) {
        return worldServer.K_().e(Registries.aU);
    }
    
    @Override
    public Pair<Location, String> searchFeature(Player player, Location startLocation, List<String> features) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        List<Holder<Structure>> featureList = new ArrayList<>();
        
        WorldServer worldServer = (WorldServer) getWorldServer(player.getWorld());
        IRegistry<Structure> structureRegistry = getStructureRegistry(worldServer);
        for (String feature : features) {
            Structure structure = structureRegistry.a(MinecraftKey.b(feature));
            Optional<ResourceKey<Structure>> optional = structureRegistry.d(structure);
            if (optional.isPresent()) {
                Holder<Structure> holder = structureRegistry.e(structure);
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
    
    private Pair<Location, String> featureFinder(@Nullable Player player, Location startLocation, List<Holder<Structure>> featureList) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        BlockPosition startPosition = new BlockPosition(startLocation.getBlockX(), startLocation.getBlockY(), startLocation.getBlockZ());
        
        WorldServer worldServer = (WorldServer) getWorldServer(startLocation.getWorld());
        IRegistry<Structure> structureRegistry = getStructureRegistry(worldServer);
        
        Set<Holder<BiomeBase>> generateInBiomesList = featureList.stream().flatMap((holder) -> holder.a().a().a()).collect(Collectors.toSet());
        
        if (generateInBiomesList.isEmpty()) {
            sendErrorTranslation(player, "tport.command.featureTP.search.feature.featuresNotGenerating");
            return null; //does not generate at all
        }
        
        ChunkGenerator chunkGenerator = worldServer.m().g();
        WorldChunkManager worldChunkManager = chunkGenerator.d();
        
        Set<Holder<BiomeBase>> generatedBiomes = worldChunkManager.c();
        if (Collections.disjoint(generatedBiomes, generateInBiomesList)) {
            sendErrorTranslation(player, "tport.command.featureTP.search.feature.featuresNotGeneratingInWorld");
            return null; //does not generate in world
        }
        
        Map<StructurePlacement, Set<Holder<Structure>>> placementMap = new Object2ObjectArrayMap<>();
        ChunkGeneratorStructureState chunkGeneratorStructureState = worldServer.m().h();
        
        //this for loop collects all structure placements and their structures
        for (Holder<Structure> structureHolder : featureList) {
            HolderSet<BiomeBase> generateInBiomes = structureHolder.a().a();
            if (generatedBiomes.stream().anyMatch(generateInBiomes::a)) {
                
                List<StructurePlacement> structurePlacements = chunkGeneratorStructureState.a(structureHolder);
                for (StructurePlacement structurePlacement : structurePlacements) {
                    placementMap.computeIfAbsent(structurePlacement, (unusedStructurePlacement) -> new ObjectArraySet<>()).add(structureHolder);
                }
            }
        }
        
        List<Map.Entry<StructurePlacement, Set<Holder<Structure>>>> placementList = new ArrayList<>(placementMap.size());
        double closestDistance = Double.MAX_VALUE;
        Pair<BlockPosition, Holder<Structure>> closestPair = null;
        //this for loop re-collects all structure placements, and only the closest concentric ring structure placements (strongholds)
        for (Map.Entry<StructurePlacement, Set<Holder<Structure>>> entry : placementMap.entrySet()) {
            StructurePlacement structureplacement = entry.getKey();
            if (structureplacement instanceof ConcentricRingsStructurePlacement concentricRingsStructurePlacement) {
                
                Pair<BlockPosition, Holder<Structure>> pairCandidate = getNearestGeneratedStructure(
                        chunkGenerator, entry.getValue(), worldServer, startPosition, concentricRingsStructurePlacement);
                if (pairCandidate == null) {
                    continue;
                }
                BlockPosition blockPos = pairCandidate.getLeft();
                double distance = distToLowCornerSqr(startPosition, blockPos);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestPair = pairCandidate;
                }
            } else if (structureplacement instanceof RandomSpreadStructurePlacement) {
                placementList.add(entry);
            }
        }
        
        if (!placementList.isEmpty()) {
            int sectionX = startLocation.getBlockX() >> 4; //block to section coord
            int sectionZ = startLocation.getBlockZ() >> 4; //block to section coord
            long worldSeed = chunkGeneratorStructureState.d();
            
            //this for loop checks for the closest structure placement
            for (int squareSize = 0; squareSize <= 100; ++squareSize) {
                boolean foundThisRound = false;
                
                for (Map.Entry<StructurePlacement, Set<Holder<Structure>>> entry : placementList) {
                    RandomSpreadStructurePlacement randomspreadstructureplacement = (RandomSpreadStructurePlacement) entry.getKey();
                    
                    Pair<BlockPosition, Holder<Structure>> pairCandidate = getNearestGeneratedStructure(chunkGenerator, entry.getValue(), worldServer, sectionX, sectionZ, squareSize, worldSeed, randomspreadstructureplacement);
                    if (pairCandidate != null) {
                        foundThisRound = true;
                        BlockPosition blockPos = pairCandidate.getLeft();
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
                    
                    BlockPosition blockPos = closestPair.getLeft();
                    return new Pair<>(new Location(startLocation.getWorld(), blockPos.u(), 200, blockPos.w()),
                            structureRegistry.b(closestPair.getRight().a()).a());
                }
            }
        }
        
        if (closestPair == null) {
            return null;
        }
        BlockPosition blockPos = closestPair.getLeft();
        return new Pair<>(new Location(startLocation.getWorld(), blockPos.u(), 200, blockPos.w()),
                structureRegistry.b(closestPair.getRight().a()).a());
    }
    
    @Nullable
    private com.spaceman.tport.Pair<BlockPosition, Holder<Structure>> getNearestGeneratedStructure(
            ChunkGenerator chunkGenerator, Set<Holder<Structure>> structureSet, WorldServer worldServer, BlockPosition startPosition, ConcentricRingsStructurePlacement concentricRingsStructurePlacement)
            throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        
        Method m = ChunkGenerator.class.getDeclaredMethod("a", Set.class, WorldServer.class, StructureManager.class, BlockPosition.class, boolean.class, ConcentricRingsStructurePlacement.class);
        m.setAccessible(true);
        //noinspection unchecked
        com.mojang.datafixers.util.Pair<BlockPosition, Holder<Structure>> p = (com.mojang.datafixers.util.Pair<BlockPosition, Holder<Structure>>)
                m.invoke(chunkGenerator, structureSet, worldServer, worldServer.b(), startPosition, false, concentricRingsStructurePlacement);
        if (p == null) return null;
        return new com.spaceman.tport.Pair<>(p.getFirst(), p.getSecond());
    }
    @Nullable
    private com.spaceman.tport.Pair<BlockPosition, Holder<Structure>> getNearestGeneratedStructure(
            ChunkGenerator chunkGenerator, Set<Holder<Structure>> structureSet, WorldServer worldServer, int sectionX, int sectionZ, int squareSize, long seed, RandomSpreadStructurePlacement randomSpreadStructurePlacement)
            throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        
        Method m = ChunkGenerator.class.getDeclaredMethod("a", Set.class, IWorldReader.class, StructureManager.class, int.class, int.class, int.class, boolean.class, long.class, RandomSpreadStructurePlacement.class);
        m.setAccessible(true);
        //noinspection unchecked
        com.mojang.datafixers.util.Pair<BlockPosition, Holder<Structure>> p = (com.mojang.datafixers.util.Pair<BlockPosition, Holder<Structure>>)
                m.invoke(chunkGenerator, structureSet, worldServer, worldServer.b(), sectionX, sectionZ, squareSize, false, seed, randomSpreadStructurePlacement);
        if (p == null) return null;
        return new com.spaceman.tport.Pair<>(p.getFirst(), p.getSecond());
    }
    
    @Override
    public List<String> availableFeatures() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        World world = Bukkit.getWorlds().get(0);
        WorldServer worldServer = (WorldServer) getWorldServer(world);
        IRegistry<Structure> structureRegistry = getStructureRegistry(worldServer);
        
        List<String> list = new ArrayList<>();
        for (MinecraftKey minecraftKey : structureRegistry.i()) {
            String lowerCase = minecraftKey.a().toLowerCase();
            list.add(lowerCase);
        }
        return list;
    }
    
    @Override
    public List<String> availableFeatures(World world) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        List<String> returnList = new ArrayList<>();
        
        WorldServer worldServer = (WorldServer) getWorldServer(world);
        IRegistry<Structure> structureRegistry = getStructureRegistry(worldServer);
        
        ChunkGenerator chunkGenerator = worldServer.m().g();
        WorldChunkManager worldChunkManager = chunkGenerator.d();
        Set<Holder<BiomeBase>> generatedBiomes = worldChunkManager.c();
        
        for (MinecraftKey minecraftKey : structureRegistry.i()) {
            Structure structure = structureRegistry.a(minecraftKey);
            
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
    public List<com.spaceman.tport.Pair<String, List<String>>> getFeatureTags(World world) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        List<com.spaceman.tport.Pair<String, List<String>>> list = new ArrayList<>();
        
        WorldServer worldServer = (WorldServer) getWorldServer(world);
        IRegistry<Structure> structureRegistry = getStructureRegistry(worldServer);
        
        structureRegistry.l().forEach( (named) -> {
            Stream<Holder<Structure>> values = named.a();
            
            List<String> features = values
                    .map((holder) -> holder.a())
                    .map(structureRegistry::b)
                    .filter(Objects::nonNull)
                    .map((key) -> key.a().toLowerCase())
                    .toList();
            
            String tagKeyName = named.h().b().a().toLowerCase();
            
            list.add(new com.spaceman.tport.Pair<>("#" + tagKeyName, features));
        });
        
        return list;
    }
    
}
