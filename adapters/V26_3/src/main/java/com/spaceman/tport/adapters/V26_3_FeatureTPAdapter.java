package com.spaceman.tport.adapters;

import com.spaceman.tport.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
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

public abstract class V26_3_FeatureTPAdapter extends TPortAdapter {
    
    private Registry<Structure> getStructureRegistry(ServerLevel worldServer) {
        return worldServer.registryAccess().lookupOrThrow(Registries.STRUCTURE);
    }
    
    @Override
    public Pair<Location, String> searchFeature(Player player, Location startLocation, List<String> features) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        List<Holder<Structure>> featureList = new ArrayList<>();
        
        ServerLevel worldServer = (ServerLevel) getWorldServer(player.getWorld());
        Registry<Structure> structureRegistry = getStructureRegistry(worldServer);
        for (String feature : features) {
            Structure structure = structureRegistry.getValue(Identifier.withDefaultNamespace(feature));
            Optional<ResourceKey<Structure>> optional = structureRegistry.getResourceKey(structure);
            if (optional.isPresent()) {
                Holder<Structure> holder = structureRegistry.wrapAsHolder(structure);
                featureList.add(holder);
            }
        }
        
        return featureFinder(player, startLocation, featureList);
    }
    
    private double distToLowCornerSqr(BlockPos b1, BlockPos b2) {
        double deltaX = b1.getX() - b2.getX();
        double deltaY = b1.getY() - b2.getY();
        double deltaZ = b1.getZ() - b2.getZ();
        return deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
    }
    
    private Pair<Location, String> featureFinder(@Nullable Player player, Location startLocation, List<Holder<Structure>> featureList) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        BlockPos startPosition = new BlockPos(startLocation.getBlockX(), startLocation.getBlockY(), startLocation.getBlockZ());
        
        ServerLevel worldServer = (ServerLevel) getWorldServer(startLocation.getWorld());
        Registry<Structure> structureRegistry = getStructureRegistry(worldServer);
        
        Set<Holder<Biome>> generateInBiomesList = featureList.stream().flatMap((holder) -> holder.value().biomes().stream()).collect(Collectors.toSet());
        
        if (generateInBiomesList.isEmpty()) {
            sendErrorTranslation(player, "tport.command.featureTP.search.feature.featuresNotGenerating");
            return null; //does not generate at all
        }
        
        ChunkGenerator chunkGenerator = worldServer.getChunkSource().getGenerator();
        BiomeSource worldChunkManager = chunkGenerator.getBiomeSource();
        
        Set<Holder<Biome>> generatedBiomes = worldChunkManager.possibleBiomes();
        if (Collections.disjoint(generatedBiomes, generateInBiomesList)) {
            sendErrorTranslation(player, "tport.command.featureTP.search.feature.featuresNotGeneratingInWorld");
            return null; //does not generate in world
        }
        
        Map<StructurePlacement, Set<Holder<Structure>>> placementMap = new Object2ObjectArrayMap<>();
        ChunkGeneratorStructureState chunkGeneratorStructureState = worldServer.getChunkSource().getGeneratorState();
        
        //this for loop collects all structure placements and their structures
        for (Holder<Structure> structureHolder : featureList) {
            HolderSet<Biome> generateInBiomes = structureHolder.value().biomes();
            if (generatedBiomes.stream().anyMatch(generateInBiomes::contains)) {
                
                List<StructurePlacement> structurePlacements = chunkGeneratorStructureState.getPlacementsForStructure(structureHolder);
                for (StructurePlacement structurePlacement : structurePlacements) {
                    placementMap.computeIfAbsent(structurePlacement, (_) -> new ObjectArraySet<>()).add(structureHolder);
                }
            }
        }
        
        List<Map.Entry<StructurePlacement, Set<Holder<Structure>>>> placementList = new ArrayList<>(placementMap.size());
        double closestDistance = Double.MAX_VALUE;
        Pair<BlockPos, Holder<Structure>> closestPair = null;
        //this for loop re-collects all structure placements, and only the closest concentric ring structure placements (strongholds)
        for (Map.Entry<StructurePlacement, Set<Holder<Structure>>> entry : placementMap.entrySet()) {
            StructurePlacement structureplacement = entry.getKey();
            if (structureplacement instanceof ConcentricRingsStructurePlacement concentricRingsStructurePlacement) {
                
                Pair<BlockPos, Holder<Structure>> pairCandidate = getNearestGeneratedStructure(
                        chunkGenerator, entry.getValue(), worldServer, startPosition, concentricRingsStructurePlacement);
                if (pairCandidate == null) {
                    continue;
                }
                BlockPos blockPos = pairCandidate.getLeft();
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
            long worldSeed = chunkGeneratorStructureState.getLevelSeed();
            
            //this for loop checks for the closest structure placement
            for (int squareSize = 0; squareSize <= 100; ++squareSize) {
                boolean foundThisRound = false;
                
                for (Map.Entry<StructurePlacement, Set<Holder<Structure>>> entry : placementList) {
                    RandomSpreadStructurePlacement randomspreadstructureplacement = (RandomSpreadStructurePlacement) entry.getKey();
                    
                    Pair<BlockPos, Holder<Structure>> pairCandidate = getNearestGeneratedStructure(chunkGenerator, entry.getValue(), worldServer, sectionX, sectionZ, squareSize, worldSeed, randomspreadstructureplacement);
                    if (pairCandidate != null) {
                        foundThisRound = true;
                        BlockPos blockPos = pairCandidate.getLeft();
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
                    
                    BlockPos blockPos = closestPair.getLeft();
                    return new Pair<>(new Location(startLocation.getWorld(), blockPos.getX(), 200, blockPos.getZ()),
                            structureRegistry.getKey(closestPair.getRight().value()).getPath());
                }
            }
        }
        
        if (closestPair == null) {
            return null;
        }
        BlockPos blockPos = closestPair.getLeft();
        return new Pair<>(new Location(startLocation.getWorld(), blockPos.getX(), 200, blockPos.getZ()),
                structureRegistry.getKey(closestPair.getRight().value()).getPath());
    }
    
    @Nullable
    private com.spaceman.tport.Pair<BlockPos, Holder<Structure>> getNearestGeneratedStructure(
            ChunkGenerator chunkGenerator, Set<Holder<Structure>> structureSet, ServerLevel worldServer, BlockPos startPosition, ConcentricRingsStructurePlacement concentricRingsStructurePlacement)
            throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        
        Method m = ChunkGenerator.class.getDeclaredMethod("getNearestGeneratedStructure", Set.class, ServerLevel.class, StructureManager.class, BlockPos.class, boolean.class, ConcentricRingsStructurePlacement.class);
        m.setAccessible(true);
        //noinspection unchecked
        com.mojang.datafixers.util.Pair<BlockPos, Holder<Structure>> p = (com.mojang.datafixers.util.Pair<BlockPos, Holder<Structure>>)
                m.invoke(chunkGenerator, structureSet, worldServer, worldServer.structureManager(), startPosition, false, concentricRingsStructurePlacement);
        if (p == null) return null;
        return new com.spaceman.tport.Pair<>(p.getFirst(), p.getSecond());
    }
    @Nullable
    private com.spaceman.tport.Pair<BlockPos, Holder<Structure>> getNearestGeneratedStructure(
            ChunkGenerator chunkGenerator, Set<Holder<Structure>> structureSet, ServerLevel worldServer, int sectionX, int sectionZ, int squareSize, long seed, RandomSpreadStructurePlacement randomSpreadStructurePlacement)
            throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        
        Method m = ChunkGenerator.class.getDeclaredMethod("getNearestGeneratedStructure", Set.class, LevelReader.class, StructureManager.class, int.class, int.class, int.class, boolean.class, long.class, RandomSpreadStructurePlacement.class);
        m.setAccessible(true);
        //noinspection unchecked
        com.mojang.datafixers.util.Pair<BlockPos, Holder<Structure>> p = (com.mojang.datafixers.util.Pair<BlockPos, Holder<Structure>>)
                m.invoke(chunkGenerator, structureSet, worldServer, worldServer.structureManager(), sectionX, sectionZ, squareSize, false, seed, randomSpreadStructurePlacement);
        if (p == null) return null;
        return new com.spaceman.tport.Pair<>(p.getFirst(), p.getSecond());
    }
    
    @Override
    public List<String> availableFeatures() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        World world = Bukkit.getWorlds().getFirst();
        ServerLevel worldServer = (ServerLevel) getWorldServer(world);
        Registry<Structure> structureRegistry = getStructureRegistry(worldServer);
        
        List<String> list = new ArrayList<>();
        for (Identifier minecraftKey : structureRegistry.keySet()) {
            String lowerCase = minecraftKey.getPath().toLowerCase();
            list.add(lowerCase);
        }
        return list;
    }
    
    @Override
    public List<String> availableFeatures(World world) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        List<String> returnList = new ArrayList<>();
        
        ServerLevel worldServer = (ServerLevel) getWorldServer(world);
        Registry<Structure> structureRegistry = getStructureRegistry(worldServer);
        
        ChunkGenerator chunkGenerator = worldServer.getChunkSource().getGenerator();
        BiomeSource worldChunkManager = chunkGenerator.getBiomeSource();
        Set<Holder<Biome>> generatedBiomes = worldChunkManager.possibleBiomes();
        
        for (Identifier minecraftKey : structureRegistry.keySet()) {
            Structure structure = structureRegistry.getValue(minecraftKey);
            
            Set<Holder<Biome>> generateInBiomesList = new HashSet<>();
            HolderSet<Biome> biomes = structure.biomes();
            Stream<Holder<Biome>> biomeStream = biomes.stream();
            biomeStream.forEach(generateInBiomesList::add);
            
            if (generateInBiomesList.isEmpty()) {
                continue; //does not generate at all
            }
            
            if (Collections.disjoint(generatedBiomes, generateInBiomesList)) {
                continue; //does not generate in world
            }
            returnList.add(minecraftKey.getPath());
        }
        
        return returnList;
    }
    
    @Override
    public List<com.spaceman.tport.Pair<String, List<String>>> getFeatureTags(World world) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        List<com.spaceman.tport.Pair<String, List<String>>> list = new ArrayList<>();
        
        ServerLevel worldServer = (ServerLevel) getWorldServer(world);
        Registry<Structure> structureRegistry = getStructureRegistry(worldServer);
        
        structureRegistry.getTags().forEach( (named) -> {
            Stream<Holder<Structure>> values = named.stream();
            
            List<String> features = values
                    .map(Holder::value)
                    .map(structureRegistry::getKey)
                    .filter(Objects::nonNull)
                    .map((key) -> key.getPath().toLowerCase())
                    .toList();
            
            String tagKeyName = named.key().location().getPath().toLowerCase();
            
            list.add(new com.spaceman.tport.Pair<>("#" + tagKeyName, features));
        });
        
        return list;
    }
    
}
