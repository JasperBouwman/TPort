package com.spaceman.tport.adapters;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.*;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.placement.ConcentricRingsStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;

import javax.annotation.Nullable;
import java.lang.reflect.*;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class AdaptiveReflectionManager {
    
    private static <c> ResourceKey<Registry<c>> getResourceKey(Class<c> c) throws IllegalAccessException, ClassNotFoundException {
        for (Field f : getRegistryClass().getDeclaredFields()) {
            if (f.getType().equals(ResourceKey.class)) {
                Type resourceKeyType = f.getGenericType();
                ParameterizedType p = (ParameterizedType) resourceKeyType;
                
                Type iRegistryType = p.getActualTypeArguments()[0];
                ParameterizedType pp = (ParameterizedType) iRegistryType;
                
                Type type = pp.getActualTypeArguments()[0];
                
                if (type.getTypeName().equals(c.getName())) {
                    return (ResourceKey<Registry<c>>) f.get(null);
                }
            }
        }
        return null;
    }
    
    public static ResourceKey<Registry<Biome>> getBiomeResourceKey() throws IllegalAccessException, ClassNotFoundException {
//      return IRegistry.aR;  //1.18.1
//      return IRegistry.aP;  //1.18.2
//      return IRegistry.aR;  //1.19
//      return Registries.al; //1.19.3
//      return Registries.an; //1.19.4
//      return Registries.ap; //1.20
        return getResourceKey(Biome.class);
    }
    
    public static Registry<Biome> getBiomeRegistry(ServerLevel worldServer) throws InvocationTargetException, IllegalAccessException, ClassNotFoundException {
//      return worldServer.t().d(getBiomeResourceKey()); //1.18.1
//      return worldServer.s().d(getBiomeResourceKey()); //1.18.2
//      return worldServer.u_().d(getBiomeResourceKey()); //1.19.4
//      return worldServer.B_().d(getBiomeResourceKey()); //1.20
        
        RegistryAccess i = ReflectionManager.get(RegistryAccess.class, worldServer);
        return ReflectionManager.get(Registry.class, i, getBiomeResourceKey());
    }
    public static BiomeSource getWorldChunkManager(ChunkGenerator chunkGenerator) throws InvocationTargetException, IllegalAccessException {
//      return chunkGenerator.e(); //1.18.2
//      return chunkGenerator.d(); //1.19
//      return chunkGenerator.c(); //1.19.3
        return ReflectionManager.get(BiomeSource.class, chunkGenerator);
    }
    public static Climate.Sampler getClimateSampler(ServerLevel worldServer) throws InvocationTargetException, IllegalAccessException {
//      return worldServer.k().g().c();     //1.18.1
//      return worldServer.k().g().d();     //1.18.2
        //worldServer->ChunkProviderServer->ChunkGenerator->Climate.Sampler
//      return worldServer.k().h().c();     //1.19
//      return worldServer.k().h().c().b(); //1.19.3
        // worldServer->ChunkProviderServer->ChunkGeneratorStructureState->RandomState->Climate.Sampler
        
        ServerChunkCache cps = ReflectionManager.get(ServerChunkCache.class, worldServer);
        try {
            Object cgst = ReflectionManager.get(Class.forName("net.minecraft.world.level.chunk.ChunkGeneratorStructureState"), cps);
            RandomState randomState = ReflectionManager.get(RandomState.class, cgst);
            return ReflectionManager.get(Climate.Sampler.class, randomState);
        } catch (ClassNotFoundException e) {
            ChunkGenerator cg = ReflectionManager.get(ChunkGenerator.class, cps);
            return ReflectionManager.get(Climate.Sampler.class, cg);
        }
    }
    public static RandomState getRandomState(ServerLevel worldServer) throws InvocationTargetException, IllegalAccessException, ClassNotFoundException {
//      return worldServer.k().g().c();     //1.18.1
//      return worldServer.k().g().d();     //1.18.2
        //worldServer->ChunkProviderServer->ChunkGenerator->Climate.Sampler
//      return worldServer.k().h().c();     //1.19
//      return worldServer.k().h().c().b(); //1.19.3
        // worldServer->ChunkProviderServer->ChunkGeneratorStructureState->RandomState->Climate.Sampler
        
        ServerChunkCache cps = ReflectionManager.get(ServerChunkCache.class, worldServer);
        Object cgst = ReflectionManager.get(Class.forName("net.minecraft.world.level.chunk.ChunkGeneratorStructureState"), cps);
        return ReflectionManager.get(RandomState.class, cgst);
    }
    public static ChunkGenerator getChunkGenerator(ServerLevel worldServer) throws InvocationTargetException, IllegalAccessException {
        ServerChunkCache cps = ReflectionManager.get(ServerChunkCache.class, worldServer);
        return ReflectionManager.get(ChunkGenerator.class, cps);
    }
    public static Set<Holder<Biome>> getGeneratedBiomes(BiomeSource worldChunkManager) throws InvocationTargetException, IllegalAccessException {
        return ReflectionManager.get(Set.class, worldChunkManager);
    }
    public static <T> Stream<HolderSet.Named<T>> l(Registry<T> registry) throws InvocationTargetException, IllegalAccessException {
        for (Method m : registry.getClass().getMethods()) {
            if (!m.getReturnType().equals(Stream.class)) continue;
            if (!m.getGenericReturnType().getTypeName().contains("HolderSet")) continue;
            
            if (m.getParameterCount() == 0) {
                return (Stream<HolderSet.Named<T>>) m.invoke(registry);
            }
        }
        return Stream.empty();
    }
    public static <T> TagKey<T> key(HolderSet.Named<T> named) throws InvocationTargetException, IllegalAccessException {
        return ReflectionManager.get(TagKey.class, named);
    }
    public static <T> Identifier minecraftKey(TagKey<T> tagKey) throws InvocationTargetException, IllegalAccessException {
        return ReflectionManager.get(Identifier.class, tagKey);
    }
    public static <T> Stream<Pair<TagKey<T>, HolderSet.Named<T>>> getTags(Registry<T> registry) throws InvocationTargetException, IllegalAccessException {
        for (Method m : registry.getClass().getMethods()) {
            if (!m.getReturnType().equals(Stream.class)) continue;
            if (!m.getGenericReturnType().getTypeName().contains("Pair")) continue;
            
            if (m.getParameterCount() == 0) {
                return (Stream<Pair<TagKey<T>, HolderSet.Named<T>>>) m.invoke(registry);
            }
        }
        return Stream.empty();
    }
    public static <T> Optional<HolderSet.Named<T>> getTag(Registry<T> biomeRegistry, TagKey<T> tagKey) throws InvocationTargetException, IllegalAccessException {
        //searching for IRegistry#getTag(TagKey<T>)
        return ReflectionManager.get(Optional.class, biomeRegistry, tagKey);
    }
    public static <T> Optional<ResourceKey<T>> getResourceKey(Registry<T> registry, T t) throws InvocationTargetException, IllegalAccessException {
        //searching for IRegistry#getResourceKey(T)
        return ReflectionManager.get(Optional.class, registry, t, Object.class);
    }
    public static Identifier getMinecraftKeyFromTag(TagKey<?> tagKey) throws InvocationTargetException, IllegalAccessException {
        return ReflectionManager.get(Identifier.class, tagKey);
    }
    public static <T> Holder<T> wrapAsHolder(Registry<T> registry, T t) throws InvocationTargetException, IllegalAccessException {
        return ReflectionManager.get(Holder.class, registry, t, Object.class);
    }
    
    public static <T> T getValueFromHolder(Holder<T> holder) throws InvocationTargetException, IllegalAccessException {
        //searching for Holder#value();
        return (T) ReflectionManager.get(Object.class, holder);
    }
    public static <T> Identifier getKeyFromRegistry(Registry<T> registry, Holder<T> holder) throws InvocationTargetException, IllegalAccessException {
        //searching for IRegistry#getKey(T)
        return ReflectionManager.get(Identifier.class, registry, getValueFromHolder(holder), Object.class);
    }
    public static <T> T getFromRegistry(Registry<T> registry, String value) throws InvocationTargetException, IllegalAccessException {
        //searching for IRegistry#get(MinecraftKey)
        Identifier key = AdaptiveReflectionManager.getMinecraftKey(value);
        return getFromRegistry(registry, key);
    }
    public static <T> T getFromRegistry(Registry<T> biomeRegistry, Identifier key) throws InvocationTargetException, IllegalAccessException {
        //searching for IRegistry#get(MinecraftKey)
        return (T) ReflectionManager.get(Object.class, biomeRegistry, key);
    }
    public static Set<Identifier> keySet_fromRegistry(Registry<?> registry) throws InvocationTargetException, IllegalAccessException {
        //searching for IRegistry#keySet
        
        for (Method m : registry.getClass().getMethods()) {
            if (!m.getReturnType().equals(Set.class)) continue;
            if (!m.getGenericReturnType().getTypeName().contains("MinecraftKey")) continue;
            
            if (m.getParameterCount() == 0) {
                return (Set<Identifier>) m.invoke(registry);
            }
        }
        return new HashSet<>();
    }
    public static String getPathFromMinecraftKey(Identifier key) {
        return key.getPath();
    }
    
    public static ChunkGeneratorStructureState getChunkGeneratorStructureState(ServerLevel worldServer) throws InvocationTargetException, IllegalAccessException {
        ServerChunkCache cpd = ReflectionManager.get(ServerChunkCache.class, worldServer);
        return ReflectionManager.get(ChunkGeneratorStructureState.class, cpd);
    }
    
    public static ResourceKey<Registry<Structure>> getStructureResourceKey() throws IllegalAccessException, ClassNotFoundException {
//            structureClass = Class.forName("net.minecraft.world.level.levelgen.feature.StructureFeature");
        return getResourceKey(Structure.class);
//        return Registries.az;
    }
    public static Registry<Structure> getStructureRegistry(ServerLevel worldServer) throws InvocationTargetException, IllegalAccessException, ClassNotFoundException {
//      return worldServer.t().d(getBiomeResourceKey()); //1.18.1
//      return worldServer.s().d(getBiomeResourceKey()); //1.18.2
//      return worldServer.u_().d(getBiomeResourceKey()); //1.19.4 u_=IRegistryCustom
//      return worldServer.B_().d(getBiomeResourceKey()); //1.20
        
        RegistryAccess i = ReflectionManager.get(RegistryAccess.class, worldServer);
        return ReflectionManager.get(Registry.class, i, getStructureResourceKey());
    }
    
    public static <T> boolean contains(HolderSet<T> holderSet, Holder<T> holderToSearch) {
        try {
            Object o = ReflectionManager.get(boolean.class, holderSet, holderToSearch, Holder.class);
            return Boolean.TRUE.equals(o);
        } catch (InvocationTargetException | IllegalAccessException e) {
            return false;
        }
    }
    public static HolderSet<Biome> getGenerateInBiomes(Holder<Structure> holder) throws InvocationTargetException, IllegalAccessException {
        return getGenerateInBiomes(getValueFromHolder(holder));
    }
    public static HolderSet<Biome> getGenerateInBiomes(Structure structure) throws InvocationTargetException, IllegalAccessException {
        //searching for Structure#biomes()
        return ReflectionManager.get(HolderSet.class, structure);
    }
    public static Set<Holder<Biome>> generateBiomesInListHolder(List<Holder<Structure>> featureList) throws InvocationTargetException, IllegalAccessException {
//        Set<Holder<Biome>> generateInBiomesList = featureList.stream().flatMap((holder) -> holder.a().a().a()).collect(Collectors.toSet());
        
        Set<Holder<Biome>> returnSet = new HashSet<>();
        for (Holder<Structure> holder : featureList) {
            HolderSet<Biome> biomes = getGenerateInBiomes(holder);
            Stream<Holder<Biome>> biomeStream = ReflectionManager.get(Stream.class, biomes);
            
            biomeStream.forEach(returnSet::add);
        }
        
        return returnSet;
    }
    public static Set<Holder<Biome>> getGenerateBiomesAsSet(Structure structure) throws InvocationTargetException, IllegalAccessException {
//        Set<Holder<Biome>> generateInBiomesList = featureList.stream().flatMap((holder) -> holder.a().a().a()).collect(Collectors.toSet());
        
        Set<Holder<Biome>> returnSet = new HashSet<>();
        HolderSet<Biome> biomes = getGenerateInBiomes(structure);
        Stream<Holder<Biome>> biomeStream = ReflectionManager.get(Stream.class, biomes);
        
        biomeStream.forEach(returnSet::add);
        return returnSet;
    }
    
    public static List<StructurePlacement> getPlacementsForStructure(ChunkGeneratorStructureState chunkGeneratorStructureState, Holder<Structure> structureHolder) throws InvocationTargetException, IllegalAccessException {
        ReflectionManager.get(List.class, chunkGeneratorStructureState, structureHolder); //todo why this?
        return chunkGeneratorStructureState.getPlacementsForStructure(structureHolder);
//        return chunkGeneratorStructureState.a(structureHolder);
    }
    public static StructureManager getStructureManager(ServerLevel worldServer) throws InvocationTargetException, IllegalAccessException {
        //searching for WorldServer#structureManager()
        return ReflectionManager.get(StructureManager.class, worldServer);
    }
    @Nullable
    public static com.spaceman.tport.Pair<BlockPos, Holder<Structure>> getNearestGeneratedStructure(
            ChunkGenerator chunkGenerator, Set<Holder<Structure>> structureSet, ServerLevel worldServer, BlockPos startPosition, ConcentricRingsStructurePlacement concentricRingsStructurePlacement) throws InvocationTargetException, IllegalAccessException {
        
        for (Method m : ChunkGenerator.class.getDeclaredMethods()) {
            Parameter[] parameters = m.getParameters();
            if (parameters.length != 6) continue;
            if (!m.getReturnType().equals(Pair.class)) continue;
            m.setAccessible(true);
            Pair<BlockPos, Holder<Structure>> p = (Pair<BlockPos, Holder<Structure>>)
                    m.invoke(chunkGenerator, structureSet, worldServer, getStructureManager(worldServer), startPosition, false, concentricRingsStructurePlacement);
            if (p == null) return null;
            return new com.spaceman.tport.Pair<>(p.getFirst(), p.getSecond());
        }
        return null;
    }
    @Nullable
    public static com.spaceman.tport.Pair<BlockPos, Holder<Structure>> getNearestGeneratedStructure(
            ChunkGenerator chunkGenerator, Set<Holder<Structure>> structureSet, ServerLevel worldServer, int sectionX, int sectionZ, int squareSize, long seed, RandomSpreadStructurePlacement randomSpreadStructurePlacement) throws InvocationTargetException, IllegalAccessException {
        
        for (Method m : ChunkGenerator.class.getDeclaredMethods()) {
            Parameter[] parameters = m.getParameters();
            if (parameters.length != 9) continue;
            if (!m.getReturnType().equals(Pair.class)) continue;
            m.setAccessible(true);
            Pair<BlockPos, Holder<Structure>> p = (Pair<BlockPos, Holder<Structure>>)
                    m.invoke(chunkGenerator, structureSet, worldServer, getStructureManager(worldServer), sectionX, sectionZ, squareSize, false, seed, randomSpreadStructurePlacement);
            if (p == null) return null;
            return new com.spaceman.tport.Pair<>(p.getFirst(), p.getSecond());
        }
        return null;
    }
    
    public static long getWorldSeed(ChunkGeneratorStructureState chunkGeneratorStructureState) throws InvocationTargetException, IllegalAccessException {
        return ReflectionManager.get(long.class, chunkGeneratorStructureState);
    }
    
    public static int[] getPosition(BlockPos blockPosition) {
        //BlockPosition{x=0, y=0, z=0}
        String[] split = blockPosition.toString().split("[=,} ]");
        int x = Integer.parseInt(split[1]);
        int y = Integer.parseInt(split[4]);
        int z = Integer.parseInt(split[7]);
        
        return new int[]{x, y, z};
    }
    
    public static Identifier getMinecraftKey(String key) {
        return Identifier.withDefaultNamespace(key);
    }
    
    public static Class<?> getRegistryClass() throws ClassNotFoundException {
        String version = ReflectionManager.getServerClassesVersion();
        
        if (version.isEmpty()) {
            return Class.forName("net.minecraft.core.registries.Registries");
        }
        
        int mainVersion = Integer.parseInt(version.split("_")[1]);
        
        if (mainVersion > 19) {
            return Class.forName("net.minecraft.core.registries.Registries");
        } else if (mainVersion == 19) {
            String subVersion = version.split("_")[2];
            if (subVersion.equals("R1") || subVersion.equals("R2")) {
                return Class.forName("net.minecraft.core.IRegistry");
            } else {
                return Class.forName("net.minecraft.core.registries.Registries");
            }
        } else {
            return Class.forName("net.minecraft.core.IRegistry");
        }
        
    }
}
