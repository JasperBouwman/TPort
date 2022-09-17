package com.spaceman.tport.commands.tport.featureTP;

import com.spaceman.tport.Pair;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.Back;
import com.spaceman.tport.commands.tport.FeatureTP;
import com.spaceman.tport.cooldown.CooldownManager;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.encapsulation.FeatureEncapsulation;
import com.spaceman.tport.metrics.FeatureSearchCounter;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.core.*;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.WorldChunkManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.placement.ConcentricRingsStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static com.spaceman.tport.commands.tport.Back.prevTPorts;
import static com.spaceman.tport.commands.tport.featureTP.Mode.getDefMode;
import static com.spaceman.tport.commands.tport.featureTP.Mode.worldSearchString;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfo2Color;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;
import static com.spaceman.tport.tpEvents.TPEManager.requestTeleportPlayer;

public class Search extends SubCommand {
    
    public Search() {
        EmptyCommand emptySearchFeature = new EmptyCommand();
        EmptyCommand emptySearchModeFeature = new EmptyCommand();
        emptySearchFeature.setPermissions("TPort.featureTP.type.<feature>");
        
        emptySearchModeFeature.setCommandName("feature", ArgumentType.REQUIRED);
        emptySearchModeFeature.setCommandDescription(formatInfoTranslation("tport.command.featureTP.search.mode.feature.commandDescription"));
        List<String> permissions = new ArrayList<>(emptySearchFeature.getPermissions());
        permissions.add(worldSearchString);
        emptySearchModeFeature.setPermissions(permissions);
        emptySearchModeFeature.permissionsOR(false);
        emptySearchModeFeature.setTabRunnable((args, player) -> {
            ArrayList<String> list = new ArrayList<>(FeatureTP.getFeatures(player.getWorld()));
            FeatureTP.getTags(player.getWorld()).stream().map(Pair::getLeft).forEach(list::add);
            List<String> featureList = Arrays.asList(args).subList(2, args.length).stream().map(String::toLowerCase).toList();
            return list.stream().filter(name -> featureList.stream().noneMatch(name::equalsIgnoreCase)).toList();
        });
        emptySearchModeFeature.setLooped(true);
        
        EmptyCommand emptySearchMode = new EmptyCommand();
        emptySearchMode.setCommandName("mode", ArgumentType.REQUIRED);
        emptySearchMode.addAction(emptySearchModeFeature);
        
        emptySearchFeature.setCommandName("feature", ArgumentType.REQUIRED);
        emptySearchFeature.setCommandDescription(formatInfoTranslation("tport.command.featureTP.search.feature.commandDescription"));
        emptySearchFeature.setTabRunnable(emptySearchModeFeature.getTabRunnable());
        emptySearchFeature.setLooped(true);
        
        addAction(emptySearchMode);
        addAction(emptySearchFeature);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        ArrayList<String> list = new ArrayList<>(FeatureTP.getFeatures(player.getWorld()));
        FeatureTP.getTags(player.getWorld()).stream().map(Pair::getLeft).forEach(list::add);
//        list.addAll(FeatureTP.getPOIs(player.getWorld()));
//        FeatureTP.getPOITags(player.getWorld()).stream().map(Pair::getLeft).forEach(list::add);
        Arrays.stream(Mode.WorldSearchMode.values()).map(Enum::name).forEach(list::add);
        return list;
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport featureTP search [mode] <feature...>
        // ->
        // tport featureTP search <mode> <feature...>
        // tport featureTP search <feature...>
        
        if (args.length <= 2) {
            sendErrorTranslation(player, "tport.command.wrongUsage", "/tport featureTP search [mode] <feature...>");
            return;
        }
        
        int startArgumentAt = 3;
        Mode.WorldSearchMode mode = Mode.WorldSearchMode.getForPlayer(args[2].toUpperCase(), player, true);
        if (mode == null) {
            mode = getDefMode(player.getUniqueId());
            startArgumentAt--;
        }
        
        List<String> features = FeatureTP.getFeatures(player.getWorld());
        List<Pair<String, List<String>>> tags = FeatureTP.getTags(player.getWorld());
        
        ArrayList<String> selectedFeatures = new ArrayList<>(args.length - 2);
        label:
        for (int i = startArgumentAt; i < args.length; i++) {
            String argument = args[i].toLowerCase();
            
            if (argument.charAt(0) == '#') { //tag list
                for (Pair<String, List<String>> tag : tags) {
                    if (!tag.getLeft().equals(argument)) {
                        continue;
                    }
                    for (String feature : tag.getRight()) {
                        if (selectedFeatures.contains(feature)) {
                            sendInfoTranslation(player, "tport.command.featureTP.search.tag.featureAlreadySelected", tag.getLeft(), feature);
                        } else {
                            if (hasPermission(player, true, "TPort.featureTP.type." + feature)) {
                                selectedFeatures.add(feature);
                            }
                        }
                    }
                    continue label;
                }
                sendErrorTranslation(player, "tport.command.featureTP.search.tag.tagNotExist", argument);
            } else { //feature
                for (String feature : features) {
                    if (!feature.equals(argument)) {
                        continue;
                    }
                    if (selectedFeatures.contains(feature)) {
                        sendErrorTranslation(player, "tport.command.featureTP.search.feature.featureAlreadySelected", feature);
                    } else {
                        if (hasPermission(player, true, "TPort.featureTP.type." + feature)) {
                            selectedFeatures.add(feature);
                        }
                    }
                    continue label;
                }
                sendErrorTranslation(player, "tport.command.featureTP.search.feature.featureNotExist", argument);
            }
        }
        
        featureTP(player, mode, selectedFeatures);
    }
    
    private static Message featuresToMessageInfo(List<String> features) {
        Message featureList = new Message();
        int listSize = features.size();
        boolean color = true;
        
        for (int i = 0; i < listSize; i++) {
            String feature = features.get(i).toLowerCase();
            if (color) {
                featureList.addMessage(formatTranslation(varInfoColor, varInfoColor, "%s", new FeatureEncapsulation(feature)));
            } else {
                featureList.addMessage(formatTranslation(varInfo2Color, varInfo2Color, "%s", new FeatureEncapsulation(feature)));
            }
            
            if (i + 2 == listSize)
                featureList.addMessage(formatInfoTranslation("tport.command.featureTP.listFeatures.info.lastDelimiter"));
            else featureList.addMessage(formatInfoTranslation("tport.command.featureTP.listFeatures.info.delimiter"));
        
            color = !color;
        }
        featureList.removeLast();
        return featureList;
    }
    private static Message featuresToMessageError(List<String> features) {
        Message featureList = new Message();
        int listSize = features.size();
        boolean color = true;
        
        for (int i = 0; i < listSize; i++) {
            String feature = features.get(i).toLowerCase();
            if (color) {
                featureList.addMessage(formatTranslation(varInfoColor, varInfoColor, "%s", new FeatureEncapsulation(feature)));
            } else {
                featureList.addMessage(formatTranslation(varInfo2Color, varInfo2Color, "%s", new FeatureEncapsulation(feature)));
            }
            
            if (i + 2 == listSize)
                featureList.addMessage(formatInfoTranslation("tport.command.featureTP.listFeatures.error.lastDelimiter"));
            else featureList.addMessage(formatInfoTranslation("tport.command.featureTP.listFeatures.error.delimiter"));
        
            color = !color;
        }
        featureList.removeLast();
        return featureList;
    }
    
    public static void featureTP(Player player, Mode.WorldSearchMode mode, List<String> features) {
        FeatureSearchCounter.add(features);
        Location startLocation = mode.getLoc(player);
        
        if (features.size() == 1) sendInfoTranslation(player, "tport.command.featureTP.search.feature.starting.singular", featuresToMessageInfo(features));
        else                      sendInfoTranslation(player, "tport.command.featureTP.search.feature.starting.multiple", featuresToMessageInfo(features));
        Pair<Location, String> featureLoc = searchFeature(player, startLocation, features);
        if (featureLoc != null) {
            Location loc = featureLoc.getLeft();
            loc = FeatureTP.setSafeY(player.getWorld(), loc.getBlockX(), loc.getBlockZ());
            
            if (loc != null) {
                loc.add(0.5, 0.1, 0.5);
                loc.setPitch(player.getLocation().getPitch());
                loc.setYaw(player.getLocation().getYaw());
                
                prevTPorts.put(player.getUniqueId(), new Back.PrevTPort(Back.PrevType.FEATURE, "featureLoc", loc,
                        "prevLoc", player.getLocation(), "featureName", featureLoc.getRight()));
                
                requestTeleportPlayer(player, loc,
                        () -> sendSuccessTranslation(player, "tport.command.featureTP.search.feature.succeeded", new FeatureEncapsulation(featureLoc.getRight())),
                        (p, delay, tickMessage, seconds, secondMessage) -> sendSuccessTranslation(p, "tport.command.featureTP.search.feature.tpRequested", new FeatureEncapsulation(featureLoc.getRight()), delay, tickMessage, seconds, secondMessage));
            } else {
                sendErrorTranslation(player, "tport.command.featureTP.search.feature.noSafeLocation");
            }
            CooldownManager.FeatureTP.update(player);
        }
    }
    
    public static Pair<Location, String> searchFeature(Player player, Location startLocation, List<String> features) {
        
        List<Holder<Structure>> featureList = new ArrayList<>();
        WorldServer worldServer;
        try {
            Object nmsWorld = Objects.requireNonNull(player.getWorld()).getClass().getMethod("getHandle").invoke(player.getWorld());
            worldServer = (WorldServer) nmsWorld;
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException | NullPointerException e) {
            e.printStackTrace();
            return null;
        }

        IRegistryCustom registry = worldServer.s();
        IRegistry<Structure> structureRegistry = registry.d(IRegistry.aN);

        for (String feature : features) {
            Structure o = structureRegistry.a(new MinecraftKey(feature));
            Optional<ResourceKey<Structure>> optional = structureRegistry.c(o);
            if (optional.isPresent()) {
                Holder<Structure> holder = structureRegistry.c(optional.get());
                featureList.add(holder);
//            System.out.println(structureRegistry.b(holder.a()).a());
            }
        }
        
        return searchFeature_1_18_2(player, startLocation, featureList, features);
    }
    
    private static Pair<Location, String> searchFeature_1_18_2(@Nullable Player player, Location startLocation, List<Holder<Structure>> featureList, List<String> features) {
        try {
            BlockPosition startPosition = new BlockPosition(startLocation.getBlockX(), startLocation.getBlockY(), startLocation.getBlockZ());
            
            Object nmsWorld = Objects.requireNonNull(startLocation.getWorld()).getClass().getMethod("getHandle").invoke(startLocation.getWorld());
            WorldServer worldServer = (WorldServer) nmsWorld;
            
            IRegistryCustom registry = worldServer.s();
            IRegistry<Structure> structureRegistry = registry.d(IRegistry.aN);
            
            Set<Holder<BiomeBase>> generateInBiomesList = featureList.stream().flatMap((holder) -> holder.a().a().a()).collect(Collectors.toSet());
            
            if (generateInBiomesList.isEmpty()) {
                sendErrorTranslation(player, "tport.command.featureTP.search.feature.featuresNotGenerating");
                return null; //does not generate at all
            } else {
                ChunkGenerator chunkGenerator = worldServer.k().g();
//              Field f = ChunkGenerator.class.getDeclaredField("d"); //1.18.2
                Field f = ChunkGenerator.class.getDeclaredField("c"); //1.19
                f.setAccessible(true);
                WorldChunkManager worldChunkManager = (WorldChunkManager) f.get(chunkGenerator);
                
                Set<Holder<BiomeBase>> generatedBiomes = worldChunkManager.b();
                if (Collections.disjoint(generatedBiomes, generateInBiomesList)) {
                    sendErrorTranslation(player, "tport.command.featureTP.search.feature.featuresNotGeneratingInWorld");
                    return null; //does not generate in world
                } else {
                    com.mojang.datafixers.util.Pair<BlockPosition, Holder<Structure>> pair = null;
                    double d0 = Double.MAX_VALUE;
                    Map<StructurePlacement, Set<Holder<Structure>>> map = new Object2ObjectArrayMap<>();
                    
                    for (Holder<Structure> holder : featureList) {
                        if (generatedBiomes.stream().anyMatch(holder.a().a()::a)) {
                            
                            Method m = ChunkGenerator.class.getDeclaredMethod("a", Holder.class, RandomState.class);
                            m.setAccessible(true);
                            List<StructurePlacement> l = (List<StructurePlacement>) m.invoke(chunkGenerator, holder, worldServer.k().h());
                            for (StructurePlacement structureplacement : l) {
                                map.computeIfAbsent(structureplacement, (p_211663_) -> new ObjectArraySet()).add(holder);
                            }
                        }
                    }
                    
                    List<Map.Entry<StructurePlacement, Set<Holder<Structure>>>> list = new ArrayList<>(map.size());
                    
                    for (Map.Entry<StructurePlacement, Set<Holder<Structure>>> entry : map.entrySet()) {
                        StructurePlacement structureplacement1 = entry.getKey();
                        if (structureplacement1 instanceof ConcentricRingsStructurePlacement concentricringsstructureplacement) {
                            Method m = ChunkGenerator.class.getDeclaredMethod("a", Set.class, WorldServer.class, StructureManager.class, BlockPosition.class, boolean.class, ConcentricRingsStructurePlacement.class);
                            m.setAccessible(true);
                            BlockPosition blockpos = (BlockPosition) m.invoke(chunkGenerator, entry.getValue(), worldServer, worldServer.a(), startPosition, false, concentricringsstructureplacement);
                            double d1 = startPosition.j(blockpos);
                            if (d1 < d0) {
                                d0 = d1;
                                pair = com.mojang.datafixers.util.Pair.of(blockpos, entry.getValue().iterator().next());
                            }
                        } else if (structureplacement1 instanceof RandomSpreadStructurePlacement) {
                            list.add(entry);
                        }
                    }
                    
                    if (!list.isEmpty()) {
                        int sectionX = SectionPosition.a(startLocation.getX());
                        int sectionZ = SectionPosition.a(startLocation.getZ());
                        
                        for (int k = 0; k <= 100; ++k) {
                            boolean flag = false;
                            
                            for (Map.Entry<StructurePlacement, Set<Holder<Structure>>> entry1 : list) {
                                RandomSpreadStructurePlacement randomspreadstructureplacement = (RandomSpreadStructurePlacement) entry1.getKey();
                                
                                Method m = ChunkGenerator.class.getDeclaredMethod("a", Set.class, IWorldReader.class, StructureManager.class, int.class, int.class, int.class, boolean.class, long.class, RandomSpreadStructurePlacement.class);
                                m.setAccessible(true);
                                com.mojang.datafixers.util.Pair<BlockPosition, Holder<Structure>> pair1 = (com.mojang.datafixers.util.Pair<BlockPosition, Holder<Structure>>) m
                                        .invoke(chunkGenerator, entry1.getValue(), worldServer, worldServer.a(), sectionX, sectionZ, k, false, worldServer.B(), randomspreadstructureplacement);
                                if (pair1 != null) {
                                    flag = true;
                                    double d2 = startPosition.j(pair1.getFirst());
                                    if (d2 < d0) {
                                        d0 = d2;
                                        pair = pair1;
                                    }
                                }
                            }
                            
                            if (flag) {
                                if (pair == null) {
                                    if (features.size() == 1) sendErrorTranslation(player, "tport.command.featureTP.search.feature.featureNotFound.singular", featuresToMessageError(features));
                                    else                      sendErrorTranslation(player, "tport.command.featureTP.search.feature.featureNotFound.multiple", featuresToMessageError(features));
                                    return null;
                                }
                                return new Pair<>(new Location(startLocation.getWorld(), pair.getFirst().u(), 200, pair.getFirst().w()),
                                        structureRegistry.b(pair.getSecond().a()).a());
                            }
                        }
                    }
                    
                    if (pair == null) {
                        if (features.size() == 1) sendErrorTranslation(player, "tport.command.featureTP.search.feature.featureNotFound.singular", featuresToMessageError(features));
                        else                      sendErrorTranslation(player, "tport.command.featureTP.search.feature.featureNotFound.multiple", featuresToMessageError(features));
                        return null;
                    }
                    return new Pair<>(new Location(startLocation.getWorld(), pair.getFirst().u(), 200, pair.getFirst().w()),
                            structureRegistry.b(pair.getSecond().a()).a());
                }
            }
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        
        if (features.size() == 1) sendErrorTranslation(player, "tport.command.featureTP.search.feature.featureNotFound.singular", featuresToMessageError(features));
        else                      sendErrorTranslation(player, "tport.command.featureTP.search.feature.featureNotFound.multiple", featuresToMessageError(features));
        return null;
    }
    
//    private static Location featureFinder(Player player, Location startLocation, FeatureTP.FeatureType feature) {
//        int x = SectionPosition.a(startLocation.getX());
//        int z = SectionPosition.a(startLocation.getZ());
//        World world = startLocation.getWorld();
//        Rectangle searchArea = getSearchArea(player);
//
//        if (feature == FeatureTP.FeatureType.Stronghold) {
//
//            try {
//                Object nmsWorld = Objects.requireNonNull(world).getClass().getMethod("getHandle").invoke(world);
//                WorldServer worldServer = (WorldServer) nmsWorld;
//
//                //method -> BlockPosition WorldServer.a(StructureGenerator<?> structuregenerator, BlockPosition blockposition, int i, boolean flag)
//
//                if (!worldServer.N.A().b()) {
//                    sendErrorTranslation(player, "tport.command.featureTP.search.feature.worldDoesNotGenerateAnyFeatures");
//                    return null;
//                }
//
//                ChunkGenerator chunkGenerator = worldServer.k().g();
//
//                //method -> ChunkGenerator.findNearestMapFeature(WorldServer worldserver, StructureGenerator<?> structuregenerator, BlockPosition blockposition, int i, boolean flag)
//
//                Class<?> chunkGeneratorClass = chunkGenerator.getClass().getSuperclass();
//
//                BlockPosition startPosition = new BlockPosition(startLocation.getBlockX(), startLocation.getBlockY(), startLocation.getBlockZ());
//
//                Method generateStrongholds = chunkGeneratorClass.getDeclaredMethod("i");
//                generateStrongholds.setAccessible(true);
//                generateStrongholds.invoke(chunkGenerator);
//
//                BlockPosition endPosition = null;
//                double closestDistance = Double.MAX_VALUE;
//                BlockPosition.MutableBlockPosition mutableBlockPosition = new BlockPosition.MutableBlockPosition();
//
//                Field strongholdPositions = chunkGeneratorClass.getDeclaredField("f");
//                strongholdPositions.setAccessible(true);
//
//                //noinspection unchecked
//                for (ChunkCoordIntPair chunkcoordintpair : (List<ChunkCoordIntPair>) strongholdPositions.get(chunkGenerator)) {
//                    mutableBlockPosition.d(SectionPosition.a(chunkcoordintpair.c, 8), 32, SectionPosition.a(chunkcoordintpair.d, 8));
//                    if (searchArea.contains(mutableBlockPosition.u(), mutableBlockPosition.w())) {
//                        double distance = mutableBlockPosition.j(startPosition);
//                        if (endPosition == null) {
//                            endPosition = new BlockPosition(mutableBlockPosition);
//                            closestDistance = distance;
//                        } else if (distance < closestDistance) {
//                            endPosition = new BlockPosition(mutableBlockPosition);
//                            closestDistance = distance;
//                        }
//                    }
//                }
//
//                if (endPosition == null) {
//                    sendErrorTranslation(player, "tport.command.featureTP.search.feature.featureNotFound", feature);
//                    return null;
//                }
//
//                return new Location(world, endPosition.u(), 0, endPosition.w());
//
//            } catch (NoSuchMethodException | NoSuchFieldException | IllegalAccessException | InvocationTargetException e) {
//                e.printStackTrace();
//            }
//
//            sendErrorTranslation(player, "tport.command.featureTP.search.feature.featureNotFound", feature);
//            return null;
//        }
//
//        try {
//            Object nmsWorld = Objects.requireNonNull(world).getClass().getMethod("getHandle").invoke(world);
//            WorldServer worldServer = (WorldServer) nmsWorld;
//
//            //method -> BlockPosition WorldServer.a(StructureGenerator<?> structuregenerator, BlockPosition blockposition, int i, boolean flag)
//
//            if (!worldServer.N.A().b()) {
//                sendErrorTranslation(player, "tport.command.featureTP.search.feature.worldDoesNotGenerateAnyFeatures");
//                return null;
//            }
//
//            ChunkGenerator chunkGenerator = worldServer.k().g();
//
//            //method -> ChunkGenerator.findNearestMapFeature(WorldServer worldserver, StructureGenerator<?> structuregenerator, BlockPosition blockposition, int i, boolean flag)
//
//            StructureGenerator<?> structureGenerator = StructureGenerator.b.get(feature.getMCName());
//
//            Field structureSettingsField = chunkGenerator.getClass().getSuperclass().getDeclaredField("d");
//            structureSettingsField.setAccessible(true);
//            StructureSettings structureSettings = (StructureSettings) structureSettingsField.get(chunkGenerator);
//
////            chunkGenerator.updateStructureSettings(worldServer, structureSettings);
//            Method updateStructureSettings = ChunkGenerator.class.getDeclaredMethod("updateStructureSettings", net.minecraft.world.level.World.class, structureSettings.getClass());
//            updateStructureSettings.setAccessible(true);
//            updateStructureSettings.invoke(chunkGenerator, worldServer, structureSettings);
//
//            StructureSettingsFeature structureSettingsFeature = structureSettings.a(structureGenerator);
//
//            ImmutableMultimap<StructureFeature<?, ?>, ResourceKey<BiomeBase>> immutableMultimap = structureSettings.b(structureGenerator);
//
//            IRegistry<BiomeBase> biomeRegistry = worldServer.t().d(IRegistry.aR);
//
//            if (structureSettingsFeature == null || immutableMultimap.isEmpty()) {
//                System.out.println("error");
//                //error general
//                return null;
//            }
//
//            Set<ResourceKey<BiomeBase>> possibleBiomes = chunkGenerator.e().b().stream().flatMap(biomeBase -> biomeRegistry.c(biomeBase).stream()).collect(Collectors.toSet());
//            if (immutableMultimap.values().stream().noneMatch(possibleBiomes::contains)) {
//                sendErrorTranslation(player, "tport.command.featureTP.search.feature.worldDoesNotGenerateFeature", feature);
//                return null;
//            }
//
//            //method -> StructureGenerator.getNearestGeneratedFeature(IWorldReader var0, StructureManager var1, BlockPosition var2, int var3, boolean var4, long var5, StructureSettingsFeature var7)
//
//            BiomeBase featureBiome = null;
//            if (feature.getBiome() != null) {
//                featureBiome = biomeRegistry.a(new MinecraftKey(feature.getBiome()));
////                MinecraftKey featureBiomeKey = biomeRegistry.b(featureBiome);
//                if (featureBiome == null) {
//                    sendErrorTranslation(player, "tport.command.featureTP.search.feature.biomeNotFound", feature);
//                    return null;
//                }
//            }
//
//            int spacing = structureSettingsFeature.a();
//
//            for (int size = 0; size <= 100; ++size) {
//                for (int xOffset = -size; xOffset <= size; ++xOffset) {
//                    boolean var14 = xOffset == -size || xOffset == size;
//
//                    for (int zOffset = -size; zOffset <= size; ++zOffset) {
//                        boolean var16 = zOffset == -size || zOffset == size;
//                        if (var14 || var16) {
//                            int newX = x + spacing * xOffset;
//                            int newZ = z + spacing * zOffset;
//
//                            ChunkCoordIntPair chunkCoordIntPair = structureGenerator.a(structureSettingsFeature, worldServer.E(), newX, newZ);
//
//                            BiomeBase primaryBiomeAtChunk = worldServer.s_().a(chunkCoordIntPair.d(), 100d, chunkCoordIntPair.e());
//
//                            StructureCheckResult structureCheckResult = worldServer.a().a(chunkCoordIntPair, structureGenerator, false);
//                            if (structureCheckResult != StructureCheckResult.b) {
//                                if (structureCheckResult == StructureCheckResult.a) {
//                                    if (featureBiome != null) {
//                                        if (!primaryBiomeAtChunk.equals(featureBiome)) {
//                                            continue;
//                                        }
//                                    }
//
//                                    Location l = new Location(world, chunkCoordIntPair.d(), 0, chunkCoordIntPair.e());
//                                    if (searchArea.contains(l.getBlockX(), l.getBlockZ())) {
//                                        return l;
//                                    }
//                                }
//
//                                IChunkAccess chunk = worldServer.a(chunkCoordIntPair.c, chunkCoordIntPair.d, ChunkStatus.d);
//
//                                StructureStart<?> structureStart = worldServer.a().a(SectionPosition.a(chunk), structureGenerator, chunk);
//                                if (structureStart != null && structureStart.b()) {
//
//                                    if (featureBiome != null) {
//                                        if (!primaryBiomeAtChunk.equals(featureBiome)) {
//                                            continue;
//                                        }
//                                    }
//
//                                    BlockPosition blockPosition = structureGenerator.a(structureStart.c());
//                                    Location l = new Location(world, blockPosition.u(), 0, blockPosition.w());
//                                    if (searchArea.contains(l.getBlockX(), l.getBlockZ())) {
//                                        return l;
//                                    }
//                                }
//                            }
//
//                            if (size == 0) {
//                                break;
//                            }
//                        }
//                    }
//
//                    if (size == 0) {
//                        break;
//                    }
//                }
//            }
//        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException | NoSuchFieldException | IllegalStateException e) {
//            e.printStackTrace();
//        }
//
//        sendErrorTranslation(player, "tport.command.featureTP.search.feature.featureNotFound", feature);
//        return null;
//    }
    
}
