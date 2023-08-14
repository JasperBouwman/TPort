package com.spaceman.tport.reflection;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ChunkProviderServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.WorldChunkManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Stream;

public class ReflectionManager {
    
    public static String getServerVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
    }
    
    public static WorldServer getWorldServer(World world) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Object nmsWorld = Objects.requireNonNull(world).getClass().getMethod("getHandle").invoke(world);
        return (WorldServer) nmsWorld;
    }
    
    public static EntityPlayer getEntityPlayer(Player player) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return (EntityPlayer) player.getClass().getMethod("getHandle").invoke(player);
    }
    public static PlayerConnection getPlayerConnection(EntityPlayer entityPlayer) throws InvocationTargetException, IllegalAccessException {
        return getField(PlayerConnection.class, entityPlayer);
    }
    
    public static void sendPlayerPacket(Player player, Packet<?> packet) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        PlayerConnection pc = getPlayerConnection(getEntityPlayer(player));
        
        for (Method m : pc.getClass().getMethods()) {
            if (m.getParameterCount() != 1) continue;
            Parameter parameter = m.getParameters()[0];
            if (!parameter.getType().equals(Packet.class)) continue;
            m.invoke(pc, packet);
            break;
        }
    }
    
    public static <R, I> R getField(Class<R> r, I invoked) throws IllegalAccessException {
        if (invoked == null) return null;
        for (Field f : invoked.getClass().getFields()) {
            if (f.getType().equals(r)) return (R) f.get(invoked);
        }
        return null;
    }
    public static <R, I> R get(Class<R> r, I invoked) throws InvocationTargetException, IllegalAccessException {
        if (invoked == null) return null;
        for (Method m : invoked.getClass().getMethods()) {
            if (!m.getReturnType().equals(r)) continue;
            if (m.getParameterCount() == 0) {
                return (R) m.invoke(invoked);
            }
        }
        return null;
    }
    public static <R, I, P> R get(Class<R> returnClass, I invoked, P parameter) throws InvocationTargetException, IllegalAccessException {
        for (Method m : invoked.getClass().getMethods()) {
            if (!m.getReturnType().equals(returnClass)) continue;
            Class<?>[] parameterTypes = m.getParameterTypes();
            if (parameterTypes.length != 1) continue;
            if (!parameterTypes[0].equals(parameter.getClass())) continue;
            
            return (R) m.invoke(invoked, parameter);
        }
        return null;
    }
    public static <R, I, P> R get(Class<R> returnClass, I invoked, P parameter, Class<?> parameterClass) throws InvocationTargetException, IllegalAccessException {
        for (Method m : invoked.getClass().getMethods()) {
            if (!m.getReturnType().equals(returnClass)) continue;
            Class<?>[] parameterTypes = m.getParameterTypes();
            if (parameterTypes.length != 1) continue;
            if (!parameterTypes[0].equals(parameterClass)) continue;
            
            return (R) m.invoke(invoked, parameter);
        }
        return null;
    }
    public static <R, I> R get(Class<R> r, I invoked, @Nullable Annotation annotation) throws InvocationTargetException, IllegalAccessException {
        if (invoked == null) return null;
        for (Method m : invoked.getClass().getMethods()) {
            if (!m.getReturnType().equals(r)) continue;
            if (annotation == null) { if (m.getAnnotations().length != 0) continue;
            } else if (!m.getAnnotations()[0].equals(annotation)) continue;
            if (m.getParameterCount() == 0) return (R) m.invoke(invoked);
        }
        return null;
    }
    
    private static <c> ResourceKey<IRegistry<c>> getResourceKey(Class<c> c) throws IllegalAccessException, ClassNotFoundException {
        for (Field f : getRegistryClass().getDeclaredFields()) {
            if (f.getType().equals(ResourceKey.class)) {
                Type resourceKeyType = f.getGenericType();
                ParameterizedType p = (ParameterizedType) resourceKeyType;
                
                Type iRegistryType = p.getActualTypeArguments()[0];
                ParameterizedType pp = (ParameterizedType) iRegistryType;
                
                Type type = pp.getActualTypeArguments()[0];
                
                if (type.getTypeName().equals(c.getName())) {
                    return (ResourceKey<IRegistry<c>>) f.get(null);
                }
            }
        }
        return null;
    }
    
    public static ResourceKey<IRegistry<BiomeBase>> getBiomeResourceKey() throws IllegalAccessException, ClassNotFoundException {
//      return IRegistry.aR;  //1.18.1
//      return IRegistry.aP;  //1.18.2
//      return IRegistry.aR;  //1.19
//      return Registries.al; //1.19.3
//      return Registries.an; //1.19.4
//      return Registries.ap; //1.20
        return getResourceKey(BiomeBase.class);
    }
    
    public static IRegistry<BiomeBase> getBiomeRegistry(WorldServer worldServer) throws InvocationTargetException, IllegalAccessException, ClassNotFoundException {
//      return worldServer.t().d(getBiomeResourceKey()); //1.18.1
//      return worldServer.s().d(getBiomeResourceKey()); //1.18.2
//      return worldServer.u_().d(getBiomeResourceKey()); //1.19.4
//      return worldServer.B_().d(getBiomeResourceKey()); //1.20
        
        IRegistryCustom i = get(IRegistryCustom.class, worldServer);
        return get(IRegistry.class, i, getBiomeResourceKey());
    }
    public static WorldChunkManager getWorldChunkManager(ChunkGenerator chunkGenerator) throws InvocationTargetException, IllegalAccessException {
//      return chunkGenerator.e(); //1.18.2
//      return chunkGenerator.d(); //1.19
//      return chunkGenerator.c(); //1.19.3
        return get(WorldChunkManager.class, chunkGenerator);
    }
    public static Climate.Sampler getClimateSampler(WorldServer worldServer) throws InvocationTargetException, IllegalAccessException {
//      return worldServer.k().g().c();     //1.18.1
//      return worldServer.k().g().d();     //1.18.2
        //worldServer->ChunkProviderServer->ChunkGenerator->Climate.Sampler
//      return worldServer.k().h().c();     //1.19
//      return worldServer.k().h().c().b(); //1.19.3
        // worldServer->ChunkProviderServer->ChunkGeneratorStructureState->RandomState->Climate.Sampler
        
        ChunkProviderServer cps = get(ChunkProviderServer.class, worldServer);
        try {
            Object cgst = get(Class.forName("net.minecraft.world.level.chunk.ChunkGeneratorStructureState"), cps);
            RandomState randomState = get(RandomState.class, cgst);
            return get(Climate.Sampler.class, randomState);
        } catch (ClassNotFoundException e) {
            ChunkGenerator cg = get(ChunkGenerator.class, cps);
            return get(Climate.Sampler.class, cg);
        }
    }
    public static ChunkGenerator getChunkGenerator(WorldServer worldServer) throws InvocationTargetException, IllegalAccessException {
        ChunkProviderServer cps = get(ChunkProviderServer.class, worldServer);
        return get(ChunkGenerator.class, cps);
    }
    public static Set<Holder<BiomeBase>> getGeneratedBiomes(WorldChunkManager worldChunkManager) throws InvocationTargetException, IllegalAccessException {
        return get(Set.class, worldChunkManager);
    }
    public static <T> Stream<Pair<TagKey<T>, HolderSet.Named<T>>> getTags(IRegistry<T> biomeRegistry) throws InvocationTargetException, IllegalAccessException {
        for (Method m : biomeRegistry.getClass().getMethods()) {
            if (!m.getReturnType().equals(Stream.class)) continue;
            if (!m.getGenericReturnType().getTypeName().contains("Pair")) continue;
            
            if (m.getParameterCount() == 0) {
                return (Stream<Pair<TagKey<T>, HolderSet.Named<T>>>) m.invoke(biomeRegistry);
            }
        }
        return Stream.empty();
    }
    public static Optional<HolderSet.Named<BiomeBase>> getOptional(IRegistry<BiomeBase> biomeRegistry, TagKey<BiomeBase> tagKey) throws InvocationTargetException, IllegalAccessException {
        return get(Optional.class, biomeRegistry, tagKey);
    }
    public static MinecraftKey getMinecraftKeyFromTag(TagKey<BiomeBase> tagKey) throws InvocationTargetException, IllegalAccessException {
        return get(MinecraftKey.class, tagKey);
    }
    
    public static <T> T getValueFromHolder(Holder<T> holder) throws InvocationTargetException, IllegalAccessException {
        //searching for Holder#value();
        return (T) get(Object.class, holder);
    }
    public static MinecraftKey getKeyFromRegistry(IRegistry<BiomeBase> biomeRegistry, Holder<BiomeBase> biomeHolder) throws InvocationTargetException, IllegalAccessException {
        //searching for IRegistry#getKey(T)
        return get(MinecraftKey.class, biomeRegistry, getValueFromHolder(biomeHolder), Object.class);
    }
    public static <T> T getFromRegistry(IRegistry<T> biomeRegistry, String value) throws InvocationTargetException, IllegalAccessException {
        MinecraftKey key = new MinecraftKey(value);
        return (T) get(Object.class, biomeRegistry, key);
    }
    public static Set<MinecraftKey> keySet_fromRegistry(IRegistry<BiomeBase> biomeRegistry) throws InvocationTargetException, IllegalAccessException {
        //searching for IRegistry#keySet
        
        for (Method m : biomeRegistry.getClass().getMethods()) {
            if (!m.getReturnType().equals(Set.class)) continue;
            if (!m.getGenericReturnType().getTypeName().contains("MinecraftKey")) continue;
            
            if (m.getParameterCount() == 0) {
                return (Set<MinecraftKey>) m.invoke(biomeRegistry);
            }
        }
        return new HashSet<>();
    }
    public static String getPathFromMinecraftKey(MinecraftKey key) {
        return key.toString().split(":")[1];
    }
    
    public static ChunkGeneratorStructureState getChunkGeneratorStructureState(WorldServer worldServer) throws InvocationTargetException, IllegalAccessException {
        ChunkProviderServer cpd = get(ChunkProviderServer.class, worldServer);
        return get(ChunkGeneratorStructureState.class, cpd);
    }
    
    public static ResourceKey<IRegistry<Structure>> getStructureResourceKey() throws IllegalAccessException, ClassNotFoundException {
        return getResourceKey(Structure.class);
//        return Registries.az;
    }
    public static IRegistry<Structure> getStructureRegistry(WorldServer worldServer) throws InvocationTargetException, IllegalAccessException, ClassNotFoundException {
//      return worldServer.t().d(getBiomeResourceKey()); //1.18.1
//      return worldServer.s().d(getBiomeResourceKey()); //1.18.2
//      return worldServer.u_().d(getBiomeResourceKey()); //1.19.4 u_=IRegistryCustom
//      return worldServer.B_().d(getBiomeResourceKey()); //1.20
        
        IRegistryCustom i = get(IRegistryCustom.class, worldServer);
        return get(IRegistry.class, i, getStructureResourceKey());
    }

    public static boolean contains(HolderSet<BiomeBase> biomes, Holder<BiomeBase> biomeToSearch) {
        try {
            return Boolean.TRUE.equals(get(boolean.class, biomes, biomeToSearch));
        } catch (InvocationTargetException | IllegalAccessException e) {
            return false;
        }
    }
    public static HolderSet<BiomeBase> getGenerateInBiomes(Holder<Structure> holder) throws InvocationTargetException, IllegalAccessException {
//        Structure structure = (Structure) get(Object.class, holder);
        Structure structure = getValueFromHolder(holder);
        return get(HolderSet.class, structure);
    }
    public static Set<Holder<BiomeBase>> generateBiomesInList(List<Holder<Structure>> featureList) throws InvocationTargetException, IllegalAccessException {
//        Set<Holder<BiomeBase>> generateInBiomesList = featureList.stream().flatMap((holder) -> holder.a().a().a()).collect(Collectors.toSet());
        
        Set<Holder<BiomeBase>> returnSet = new HashSet<>();
        for (Holder<Structure> holder : featureList) {
            HolderSet<BiomeBase> biomes = getGenerateInBiomes(holder);
            Stream<Holder<BiomeBase>> biomeStream = get(Stream.class, biomes);
            
            biomeStream.forEach(returnSet::add);
        }
        
        return returnSet;
    }
    
    public static Class<?> getRegistryClass() throws ClassNotFoundException {
        String version = getServerVersion();
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
