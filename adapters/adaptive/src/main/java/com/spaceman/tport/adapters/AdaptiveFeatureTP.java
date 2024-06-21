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
import java.util.*;
import java.util.stream.Stream;

import static com.spaceman.tport.adapters.AdaptiveReflectionManager.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;

public abstract class AdaptiveFeatureTP extends TPortAdapter {
    
    @Override
    public Pair<Location, String> searchFeature(Player player, Location startLocation, List<String> features) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, ClassNotFoundException {
        List<Holder<Structure>> featureList = new ArrayList<>();
        
        WorldServer worldServer = (WorldServer) getWorldServer(player.getWorld());
        IRegistry<Structure> structureRegistry = getStructureRegistry(worldServer);
        for (String feature : features) {
            Structure structure = getFromRegistry(structureRegistry, feature);
            Optional<ResourceKey<Structure>> optional = getResourceKey(structureRegistry, structure);
            if (optional.isPresent()) {
                Holder<Structure> holder = wrapAsHolder(structureRegistry, structure);
                featureList.add(holder);
            }
        }
        
        return featureFinder(player, startLocation, featureList);
    }
    
    private double distToLowCornerSqr(BlockPosition b1, BlockPosition b2) {
        int[] loc1 = getPosition(b1);
        int[] loc2 = getPosition(b2);
        double deltaX = loc1[0] - loc2[0];
        double deltaY = loc1[1] - loc2[1];
        double deltaZ = loc1[2] - loc2[2];
        return deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
    }
    
    private Pair<Location, String> featureFinder(@Nullable Player player, Location startLocation, List<Holder<Structure>> featureList) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, ClassNotFoundException {
        BlockPosition startPosition = new BlockPosition(startLocation.getBlockX(), startLocation.getBlockY(), startLocation.getBlockZ());
        
        WorldServer worldServer = (WorldServer) getWorldServer(startLocation.getWorld());
        IRegistry<Structure> structureRegistry = getStructureRegistry(worldServer);
        
        Set<Holder<BiomeBase>> generateInBiomesList = generateBiomesInListHolder(featureList);
        
        if (generateInBiomesList.isEmpty()) {
            sendErrorTranslation(player, "tport.command.featureTP.search.feature.featuresNotGenerating");
            return null; //does not generate at all
        }
        
        ChunkGenerator chunkGenerator = getChunkGenerator(worldServer);
        WorldChunkManager worldChunkManager = getWorldChunkManager(chunkGenerator);
        
        Set<Holder<BiomeBase>> generatedBiomes = getGeneratedBiomes(worldChunkManager);
        if (Collections.disjoint(generatedBiomes, generateInBiomesList)) {
            sendErrorTranslation(player, "tport.command.featureTP.search.feature.featuresNotGeneratingInWorld");
            return null; //does not generate in world
        }
        
        Map<StructurePlacement, Set<Holder<Structure>>> placementMap = new Object2ObjectArrayMap<>();
        ChunkGeneratorStructureState chunkGeneratorStructureState = getChunkGeneratorStructureState(worldServer);
        
        //this for loop collects all structure placements and their structures
        for (Holder<Structure> structureHolder : featureList) {
            HolderSet<BiomeBase> generateInBiomes = getGenerateInBiomes(structureHolder);
            if (generatedBiomes.stream().anyMatch(biomeHolder -> contains(generateInBiomes, biomeHolder))) {
                
                List<StructurePlacement> structurePlacements = getPlacementsForStructure(chunkGeneratorStructureState, structureHolder);
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
                
                Pair<BlockPosition, Holder<Structure>> pairCandidate = getNearestGeneratedStructure(chunkGenerator,
                        entry.getValue(), worldServer, startPosition, concentricRingsStructurePlacement);
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
            long worldSeed = getWorldSeed(chunkGeneratorStructureState);
            
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
                    
                    int[] loc = getPosition(closestPair.getLeft());
                    return new Pair<>(new Location(startLocation.getWorld(), loc[0], 200, loc[2]),
                            getPathFromMinecraftKey(getKeyFromRegistry(structureRegistry, closestPair.getRight())));
                }
            }
        }
        
        if (closestPair == null) {
            return null;
        }
        int[] loc = getPosition(closestPair.getLeft());
        return new Pair<>(new Location(startLocation.getWorld(), loc[0], 200, loc[2]),
                getPathFromMinecraftKey(getKeyFromRegistry(structureRegistry, closestPair.getRight())));
    }
    
    @Override
    public List<String> availableFeatures() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, ClassNotFoundException {
        World world = Bukkit.getWorlds().get(0);
        WorldServer worldServer = (WorldServer) getWorldServer(world);
        IRegistry<Structure> structureRegistry = getStructureRegistry(worldServer);
        
        List<String> list = new ArrayList<>();
        for (MinecraftKey minecraftKey : keySet_fromRegistry(structureRegistry)) {
            String lowerCase = getPathFromMinecraftKey(minecraftKey).toLowerCase();
            list.add(lowerCase);
        }
        return list;
    }
    
    @Override
    public List<String> availableFeatures(World world) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        List<String> returnList = new ArrayList<>();
        
        WorldServer worldServer = (WorldServer) getWorldServer(world);
        IRegistry<Structure> structureRegistry = getStructureRegistry(worldServer);
        
        ChunkGenerator chunkGenerator = getChunkGenerator(worldServer);
        WorldChunkManager worldChunkManager = getWorldChunkManager(chunkGenerator);
        Set<Holder<BiomeBase>> generatedBiomes = getGeneratedBiomes(worldChunkManager);
        
        for (MinecraftKey minecraftKey : keySet_fromRegistry(structureRegistry)) {
            Structure structure = getFromRegistry(structureRegistry, minecraftKey);
            
            Set<Holder<BiomeBase>> generateInBiomesList = getGenerateBiomesAsSet(structure);
            
            if (generateInBiomesList.isEmpty()) {
                continue; //does not generate at all
            }
            
            if (Collections.disjoint(generatedBiomes, generateInBiomesList)) {
                continue; //does not generate in world
            }
            returnList.add(getPathFromMinecraftKey(minecraftKey));
        }
        
        return returnList;
    }
    
    @Override
    public List<com.spaceman.tport.Pair<String, List<String>>> getFeatureTags(World world) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, ClassNotFoundException {
        List<com.spaceman.tport.Pair<String, List<String>>> list = new ArrayList<>();
        
        WorldServer worldServer = (WorldServer) getWorldServer(world);
        IRegistry<Structure> structureRegistry = getStructureRegistry(worldServer);
        
        List<String> tags = getTags(structureRegistry).map((tagKey) -> {
            try {
                return getPathFromMinecraftKey(getMinecraftKeyFromTag(tagKey.getFirst()));
            } catch (Throwable e) {
                return null;
            }
        }).filter(Objects::nonNull).toList();
        
        for (String tagKeyName : tags) {
            TagKey<Structure> tagKey = TagKey.a(getStructureResourceKey(), AdaptiveReflectionManager.getMinecraftKey(tagKeyName)); //todo finish reflection
            
            Optional<HolderSet.Named<Structure>> optional = getTag(structureRegistry, tagKey);
            if (optional.isPresent()) {
                HolderSet.Named<Structure> named = optional.get();
                Stream<Holder<Structure>> values = ReflectionManager.get(Stream.class, named);
                
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
