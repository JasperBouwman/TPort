package com.spaceman.tport.commands.tport;

import com.google.common.collect.Iterables;
import com.google.gson.JsonObject;
import com.spaceman.tport.Main;
import com.spaceman.tport.Pair;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.CommandTemplate;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.commands.tport.biomeTP.*;
import com.spaceman.tport.cooldown.CooldownManager;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.MessageUtils;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fancyMessage.encapsulation.BiomeEncapsulation;
import com.spaceman.tport.fancyMessage.inventories.FancyClickEvent;
import com.spaceman.tport.metrics.BiomeSearchCounter;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.WorldChunkManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.spaceman.tport.TPortInventories.openBiomeTP;
import static com.spaceman.tport.commandHandler.CommandTemplate.runCommands;
import static com.spaceman.tport.commands.tport.Back.prevTPorts;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.language.Language.getPlayerLang;
import static com.spaceman.tport.tpEvents.TPEManager.requestTeleportPlayer;

public class BiomeTP extends SubCommand {
    
    public static boolean legacyBiomeTP = false;
    
    private final EmptyCommand empty;
    
    public BiomeTP() {
        empty = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return "";
            }
        };
        empty.setCommandName("", ArgumentType.FIXED);
        empty.setCommandDescription(formatInfoTranslation("tport.command.biomeTP.commandDescription"));
        empty.setPermissions("TPort.biomeTP.open");
        
        addAction(empty);
        addAction(new Whitelist());
        addAction(new Blacklist());
        addAction(Preset.getInstance());
        addAction(com.spaceman.tport.commands.tport.biomeTP.Random.getInstance());
        addAction(new Accuracy());
        addAction(new Mode());
    }
    
    public static List<String> availableBiomes() {
        if (!legacyBiomeTP) {
            try {
                World world = Bukkit.getWorlds().get(0);
                Object nmsWorld = Objects.requireNonNull(world).getClass().getMethod("getHandle").invoke(world);
                WorldServer worldServer = (WorldServer) nmsWorld;

//              IRegistry<BiomeBase> biomeRegistry = worldServer.t().d(IRegistry.aR); //1.18.1
//              IRegistry<BiomeBase> biomeRegistry = worldServer.s().d(IRegistry.aP); //1.18.2
//              IRegistry<BiomeBase> biomeRegistry = worldServer.s().d(IRegistry.aR); //1.19
                IRegistry<BiomeBase> biomeRegistry = worldServer.s().d(Registries.al); //1.19.3
                return biomeRegistry.e().stream().map(MinecraftKey::a).map(String::toLowerCase).collect(Collectors.toList());
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                Main.getInstance().getLogger().log(Level.WARNING, "Can't use NMS (try updating TPort), using legacy mode for BiomeTP");
                legacyBiomeTP = true;
                e.printStackTrace();
            }
        }
        return Arrays.stream(Biome.values()).map(Enum::name).map(String::toLowerCase).collect(Collectors.toList());
    }
    
    public static List<String> availableBiomes(World world) {
        if (!legacyBiomeTP) {
            try {
                Object nmsWorld = Objects.requireNonNull(world).getClass().getMethod("getHandle").invoke(world);
                WorldServer worldServer = (WorldServer) nmsWorld;
                
                ChunkGenerator chunkGenerator = worldServer.k().g();
//              IRegistry<BiomeBase> biomeRegistry = worldServer.t().d(IRegistry.aR); //1.18.1
//              IRegistry<BiomeBase> biomeRegistry = worldServer.s().d(IRegistry.aP); //1.18.2
//              IRegistry<BiomeBase> biomeRegistry = worldServer.s().d(IRegistry.aR); //1.19
                IRegistry<BiomeBase> biomeRegistry = worldServer.s().d(Registries.al); //1.19.3

//              Field f = ChunkGenerator.class.getDeclaredField("b"); //1.18.1
//              Field f = ChunkGenerator.class.getDeclaredField("c"); //1.18.2
                Field f = ChunkGenerator.class.getDeclaredField("b"); //1.19.3
                
                f.setAccessible(true);
                WorldChunkManager worldChunkManager = (WorldChunkManager) f.get(chunkGenerator);
                
                return worldChunkManager.b().stream()
                        .map((b) -> biomeRegistry.b(b.a()))
                        .filter(Objects::nonNull)
                        .map(MinecraftKey::a)
                        .map(String::toLowerCase)
                        .collect(Collectors.toList());
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | NoSuchFieldException | NoSuchMethodError | ClassCastException e) {
                Main.getInstance().getLogger().log(Level.WARNING, "Can't use NMS (try updating TPort), using legacy mode for BiomeTP");
                legacyBiomeTP = true;
                e.printStackTrace();
            }
        }
        
        return availableBiomes();
    }
    
    @Nullable
    public static Pair<Location, String> biomeFinder(Player player, List<String> biomes, @Nonnull Location startLocation, Accuracy.AccuracySettings accuracy) {
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
                
//              WorldChunkManager worldChunkManager = chunkGenerator.e(); //1.18.2
//                WorldChunkManager worldChunkManager = chunkGenerator.d(); //1.19
                WorldChunkManager worldChunkManager = chunkGenerator.c(); //1.19.3
                
//              IRegistry<BiomeBase> biomeRegistry = worldServer.t().d(IRegistry.aR); //1.18.1
//              IRegistry<BiomeBase> biomeRegistry = worldServer.s().d(IRegistry.aP); //1.18.2
//              IRegistry<BiomeBase> biomeRegistry = worldServer.s().d(IRegistry.aR); //1.19
                IRegistry<BiomeBase> biomeRegistry = worldServer.s().d(Registries.al); //1.19.3
                List<BiomeBase> baseList = biomes.stream().map(biome -> biomeRegistry.a(new MinecraftKey(biome.toLowerCase()))).filter(Objects::nonNull).toList();
                
                Predicate<Holder<BiomeBase>> predicate = (holder) -> baseList.stream().anyMatch((biomeBase) -> biomeBase.equals(holder.a())); //1.18.2
                
                Location blockPos;
//              Climate.Sampler climateSampler = worldServer.k().g().c(); //1.18.1
//              Climate.Sampler climateSampler = worldServer.k().g().d(); //1.18.2
//              Climate.Sampler climateSampler = worldServer.k().h().c(); //1.19
                Climate.Sampler climateSampler = worldServer.k().h().c().b(); //1.19.3
                
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
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                Main.getInstance().getLogger().log(Level.WARNING, "Can't use NMS (try updating TPort), using legacy mode for BiomeTP");
                legacyBiomeTP = true;
            }
        }
        
        //legacy biomeTP
        for (int squareSize = 0; squareSize <= quartSize; squareSize += increment) {
            for (int zOffset = -squareSize; zOffset <= squareSize; zOffset += increment) {
                boolean zEnd = Math.abs(zOffset) == squareSize;
                
                for (int xOffset = -squareSize; xOffset <= squareSize; xOffset += increment) {
                    boolean xEnd = Math.abs(zOffset) == squareSize;
                    if (!zEnd && !xEnd) continue;
                    
                    int newX = (quartX + xOffset) << 2;
                    int newZ = (quartZ + zOffset) << 2;
    
                    if (!searchArea.contains(newX, newZ)) {
                        continue;
                    }
                    
                    for (int y : yLevels) {
                        Location testLocation = new Location(world, newX, y, newZ);
                        Biome biome = world.getBiome(testLocation);
                        
                        if (biomes.stream().anyMatch(b -> biome.name().equalsIgnoreCase(b))) {
                            return new Pair<>(testLocation, biome.name());
                        }
                    }
                }
            }
        }
        
        //error could not find biome
        return null;
    }
    
    public static int randomTPTries = 100;
    
    public static void randomTP(Player player) {
        Location l;
        
        for (int i = 0; i < randomTPTries; i++) {
            l = Main.getRandomLocation(player);
            
            l = FeatureTP.setSafeY(player.getWorld(), l.getBlockX(), l.getBlockZ());
            if (l == null) {
                continue;
            }
            
            l.add(0.5, 0.1, 0.5);
            l.setPitch(player.getLocation().getPitch());
            l.setYaw(player.getLocation().getYaw());
            
            prevTPorts.put(player.getUniqueId(), new Back.PrevTPort(Back.PrevType.BIOME, "biomeLoc", l,
                    "prevLoc", player.getLocation(), "biomeName", "Random"));
            
            requestTeleportPlayer(player, l, () -> sendSuccessTranslation(Bukkit.getPlayer(player.getUniqueId()), "tport.command.biomeTP.random.succeeded"),
                    (p, delay, tickMessage, seconds, secondMessage) -> sendSuccessTranslation(p, "tport.command.biomeTP.randomTP.succeededRequested", delay, tickMessage, seconds, secondMessage));
            CooldownManager.BiomeTP.update(player);
            return;
        }
        sendErrorTranslation(player, "tport.command.biomeTP.random.couldNotFindLocation", randomTPTries);
    }
    
    public static void biomeTP(Player player, com.spaceman.tport.commands.tport.featureTP.Mode.WorldSearchMode mode, List<String> biomes) {
        BiomeSearchCounter.add(biomes);
        Accuracy.AccuracySettings accuracySettings = Accuracy.getDefaultAccuracySettings();
        
        if (biomes.size() == 1) {
            sendInfoTranslation(player, "tport.command.biomeTP.searchingForBiome", biomesToStringSearch(biomes), accuracySettings);
        } else {
            sendInfoTranslation(player, "tport.command.biomeTP.searchingForBiomes", biomesToStringSearch(biomes), accuracySettings);
        }
        
        Pair<Location, String> biomeLocation = biomeFinder(player, biomes, mode.getLoc(player), accuracySettings);
        
        CooldownManager.BiomeTP.update(player);
        
        if (biomeLocation == null) {
            if (biomes.size() == 1) {
                sendErrorTranslation(player, "tport.command.biomeTP.couldNotFindBiome", biomesToStringError(biomes), accuracySettings);
            } else {
                sendErrorTranslation(player, "tport.command.biomeTP.couldNotFindBiomes", biomesToStringError(biomes), accuracySettings);
            }
        } else {
            Location l = biomeLocation.getLeft();
            
            l = FeatureTP.setSafeY(player.getWorld(), l.getBlockX(), l.getBlockZ());
            if (l == null) {
                sendErrorTranslation(player, "tport.command.biomeTP.noSafeLocationFound");
                return;
            }
            
            l.add(0.5, 0.1, 0.5);
            l.setPitch(player.getLocation().getPitch());
            l.setYaw(player.getLocation().getYaw());
            
            prevTPorts.put(player.getUniqueId(), new Back.PrevTPort(Back.PrevType.BIOME, "biomeLoc", l,
                    "prevLoc", player.getLocation(), "biomeName", biomeLocation.getRight()));
            
            Message biomeName = formatTranslation(varInfoColor, varInfoColor, "%s", new BiomeEncapsulation(biomeLocation.getRight()));
            requestTeleportPlayer(player, l, () -> sendSuccessTranslation(Bukkit.getPlayer(player.getUniqueId()), "tport.command.biomeTP.succeeded", biomeName),
                    (p, delay, tickMessage, seconds, secondMessage) -> sendSuccessTranslation(p, "tport.command.biomeTP.succeededRequested", biomeName, delay, tickMessage, seconds, secondMessage));
        }
    }
    
    private static Message biomesToStringError(List<String> biomes) {
        Message biomeList = new Message();
        int listSize = biomes.size();
        boolean color = true;
        
        for (int i = 0; i < listSize; i++) {
            String biome = biomes.get(i).toLowerCase();
            if (color) {
                biomeList.addMessage(formatTranslation(varErrorColor, varErrorColor, "%s", new BiomeEncapsulation(biome)));
            } else {
                biomeList.addMessage(formatTranslation(varError2Color, varError2Color, "%s", new BiomeEncapsulation(biome)));
            }
            
            if (i + 2 == listSize)
                biomeList.addMessage(formatInfoTranslation("tport.command.biomeTP.listBiomes.error.lastDelimiter"));
            else biomeList.addMessage(formatInfoTranslation("tport.command.biomeTP.listBiomes.error.delimiter"));
            
            color = !color;
        }
        biomeList.removeLast();
        return biomeList;
    }
    
    private static Message biomesToStringSearch(List<String> biomes) {
        Message biomeList = new Message();
        biomeList.addText("");
        int listSize = biomes.size();
        boolean color = true;
        
        for (int i = 0; i < listSize; i++) {
            String biome = biomes.get(i).toLowerCase();
            if (color) {
                biomeList.addMessage(formatTranslation(varInfoColor, varInfoColor, "%s", new BiomeEncapsulation(biome)));
            } else {
                biomeList.addMessage(formatTranslation(varInfo2Color, varInfo2Color, "%s", new BiomeEncapsulation(biome)));
            }
            
            if (i + 2 == listSize)
                biomeList.addMessage(formatInfoTranslation("tport.command.biomeTP.listBiomes.info.lastDelimiter"));
            else biomeList.addMessage(formatInfoTranslation("tport.command.biomeTP.listBiomes.info.delimiter"));
            
            color = !color;
        }
        biomeList.removeLast();
        return biomeList;
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport biomeTP
        // tport biomeTP accuracy [accuracy]
        // tport biomeTP whitelist <biome...>
        // tport biomeTP blacklist <biome...>
        // tport biomeTP preset [preset]
        // tport biomeTP random
        // tport biomeTP searchTries [tries]
        // tport biomeTP mode [mode]
        
        if (args.length == 1) {
            if (!empty.hasPermissionToRun(player, true)) {
                return;
            }
            openBiomeTP(player);
        } else {
            if (!runCommands(getActions(), args[1], args, player)) {
                sendErrorTranslation(player, "tport.command.wrongUsage", "/tport biomeTP " + CommandTemplate.convertToArgs(getActions(), true));
            }
        }
    }
    
    public static class BiomeTPPresets {
        private static final ArrayList<BiomePreset> biomePresets = new ArrayList<>();
        private static final HashMap<String, ArrayList<BiomePreset>> tagPresets = new HashMap<>();
        
        /**
         * @return {@link Pair} with {@link Boolean} (true when registered, false when not registered), and a {@link List<String>} will all unregistered biomes
         */
        @SuppressWarnings("UnusedReturnValue")
        public static Pair<Boolean, List<String>> registerPreset(String name, List<String> biomes, boolean whitelist, Material material) {
            if (Main.containsSpecialCharacter(name)) {
                return new Pair<>(false, biomes);
            }
            if (biomePresets.stream().map(BiomePreset::name).anyMatch(n -> n.equalsIgnoreCase(name))) { //name check
                return new Pair<>(false, biomes);
            }
            
            List<String> availableBiomes = availableBiomes();
            ArrayList<String> biomeList = new ArrayList<>();
            ArrayList<String> errorList = new ArrayList<>();
            for (String biome : biomes) {
                String b = biome.toLowerCase();
                if (availableBiomes.stream().anyMatch(b::equals)) {
                    biomeList.add(b);
                } else {
                    errorList.add(biome);
                }
            }
            
            if (biomeList.isEmpty()) {
                return new Pair<>(false, errorList);
            } else {
                biomePresets.add(new BiomePreset(name, biomeList, whitelist, material, false));
                return new Pair<>(true, errorList);
            }
        }
        
        public static List<String> getNames() {
            List<String> list = biomePresets.stream().map(BiomePreset::name).collect(Collectors.toList());
            ArrayList<BiomePreset> tagPreset = tagPresets.getOrDefault("world", null);
            if (tagPreset == null) {
                tagPreset = Main.getOrDefault(loadPresetsFromWorld(Bukkit.getWorld("world")), new ArrayList<>());
            }
            tagPreset.stream().map(BiomePreset::name).forEach(list::add);
            return list;
        }
        
        public static List<ItemStack> getItems(Player player) {
            ArrayList<ItemStack> items = new ArrayList<>();
            ColorTheme ct = ColorTheme.getTheme(player);
            
            ArrayList<BiomePreset> tagPreset = tagPresets.getOrDefault(player.getWorld().getName(), null);
            if (tagPreset == null) {
                tagPreset = Main.getOrDefault(loadPresetsFromWorld(player.getWorld()), new ArrayList<>());
            }
            
            for (BiomePreset preset : Iterables.concat(biomePresets, tagPreset)) {
                ItemStack is = new ItemStack(preset.material());
                
                JsonObject playerLang = getPlayerLang(player.getUniqueId());
                
                List<Message> lore = new LinkedList<>();
                lore.add(new Message());
                
                String baseType = "tport.commands.tport.biomeTP.biomeTPPresets.getItems.type";
                if (preset.biomes().size() == 1) baseType += ".singular";
                else baseType += ".multiple";
                if (preset.whitelist()) {
                    lore.add(formatInfoTranslation(baseType,
                            formatTranslation(varInfoColor, varInfo2Color, "tport.commands.tport.biomeTP.biomeTPPresets.getItems.whitelist")));
                } else {
                    lore.add(formatInfoTranslation(baseType,
                            formatTranslation(varInfoColor, varInfo2Color, "tport.commands.tport.biomeTP.biomeTPPresets.getItems.blacklist")));
                }
                
                Message lorePiece = new Message();
                boolean color = true;
                
                for (int i = 0; i < preset.biomes().size(); i++) {
                    if ((i % 2) == 0 && i != 0) {
                        lore.add(lorePiece);
                        lorePiece = new Message();
                    }
                    if (color) {
                        lorePiece.addMessage(formatTranslation(varInfoColor, varInfoColor, "%s", new BiomeEncapsulation(preset.biomes().get(i))));
                    } else {
                        lorePiece.addMessage(formatTranslation(varInfo2Color, varInfo2Color, "%s", new BiomeEncapsulation(preset.biomes().get(i))));
                    }
                    lorePiece.addText(textComponent(", ", infoColor));
                    color = !color;
                }
                lorePiece.removeLast();
                lore.add(lorePiece);
                
                if (preset.fromMC) {
                    lore.add(new Message());
                    lore.add(formatInfoTranslation("tport.commands.tport.biomeTP.biomeTPPresets.getItems.fromMC"));
                    lore.add(new Message(textComponent(preset.name(), varInfoColor)));
                }
                lore.add(new Message());
                lore.add(formatInfoTranslation("tport.commands.tport.biomeTP.biomeTPPresets.getItems.selectBiomes.additive." + ((preset.biomes.size() == 1) ? "singular" : "multiple"), ClickType.LEFT));
                lore.add(formatInfoTranslation("tport.commands.tport.biomeTP.biomeTPPresets.getItems.selectBiomes.overwrite." + ((preset.biomes.size() == 1) ? "singular" : "multiple"), ClickType.SHIFT_LEFT));
                lore.add(formatInfoTranslation("tport.commands.tport.biomeTP.biomeTPPresets.getItems.runPreset", ClickType.RIGHT));
                
                if (playerLang != null) { //if player has no custom language, translate it
                    lore = MessageUtils.translateMessage(lore, playerLang);
                }
                MessageUtils.setCustomItemData(is, ct, null, lore);
                
                ItemMeta im = is.getItemMeta();
                if (im == null) {
                    continue;
                }
                if (preset.fromMC) im.setDisplayName(ct.getVarInfo2Color() + preset.name());
                else im.setDisplayName(ct.getVarInfoColor() + preset.name());
                
                String biomesAsString = String.join("|", preset.biomes());
                
                FancyClickEvent.addCommand(im, ClickType.RIGHT, "tport biomeTP preset " + preset.name());
                im.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "biomePreset/name"), PersistentDataType.STRING, preset.name());
                im.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "biomePreset/biomes"), PersistentDataType.STRING, biomesAsString);
                im.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "biomePreset/whitelist"), PersistentDataType.INTEGER, preset.whitelist ? 1 : 0);
                FancyClickEvent.addFunction(im, ClickType.LEFT, "biomeTP_selectAdditive", ((whoClicked, clickType, pdc, fancyInventory) -> {
                    NamespacedKey biomeKey = new NamespacedKey(Main.getInstance(), "biomePreset/biomes");
                    NamespacedKey whitelistKey = new NamespacedKey(Main.getInstance(), "biomePreset/whitelist");
                    
                    if (pdc.has(biomeKey, PersistentDataType.STRING) && pdc.has(whitelistKey, PersistentDataType.INTEGER)) {
                        String biomes = pdc.get(biomeKey, PersistentDataType.STRING);
                        String[] biomesArray = biomes.split("\\|");
                        Integer whitelist = Main.getOrDefault(pdc.get(whitelistKey, PersistentDataType.INTEGER), 1);
                        ArrayList<String> biomeSelection = fancyInventory.getData("biomeSelection", ArrayList.class, new ArrayList<String>());
                        if (whitelist == 1) {
                            Arrays.stream(biomesArray).filter(s -> !biomeSelection.contains(s)).forEach(biomeSelection::add);
                        } else {
                            for (Biome biome : Biome.values()) {
                                String biomeName = biome.name().toLowerCase();
                                if (Arrays.stream(biomesArray).noneMatch(b -> b.equals(biomeName))) {
                                    biomeSelection.add(biomeName);
                                }
                            }
                        }
                        fancyInventory.setData("biomeSelection", biomeSelection);
                        openBiomeTP(whoClicked, 0, fancyInventory);
                    }
                }));
                FancyClickEvent.addFunction(im, ClickType.SHIFT_LEFT, "biomeTP_selectOverwrite", ((whoClicked, clickType, pdc, fancyInventory) -> {
                    NamespacedKey biomeKey = new NamespacedKey(Main.getInstance(), "biomePreset/biomes");
                    NamespacedKey whitelistKey = new NamespacedKey(Main.getInstance(), "biomePreset/whitelist");
                    
                    if (pdc.has(biomeKey, PersistentDataType.STRING) && pdc.has(whitelistKey, PersistentDataType.INTEGER)) {
                        String biomes = pdc.get(biomeKey, PersistentDataType.STRING);
                        String[] biomesArray = biomes.split("\\|");
                        Integer whitelist = Main.getOrDefault(pdc.get(whitelistKey, PersistentDataType.INTEGER), 1);
                        ArrayList<String> biomeSelection = new ArrayList<>();
                        if (whitelist == 1) {
                            biomeSelection = Arrays.stream(biomesArray).collect(Collectors.toCollection(ArrayList::new));
                        } else {
                            for (Biome biome : Biome.values()) {
                                if (Arrays.stream(biomesArray).noneMatch(b -> b.equals(biome.name().toLowerCase()))) {
                                    biomeSelection.add(biome.name().toLowerCase());
                                }
                            }
                        }
                        fancyInventory.setData("biomeSelection", biomeSelection);
                        openBiomeTP(whoClicked, 0, fancyInventory);
                    }
                }));
                is.setItemMeta(im);
                items.add(is);
            }
            
            return items;
        }
        
        private static ArrayList<BiomePreset> loadPresetsFromWorld(World world) {
            if (legacyBiomeTP) return null;
            
            try {
                Object nmsWorld = Objects.requireNonNull(world).getClass().getMethod("getHandle").invoke(world);
                WorldServer worldServer = (WorldServer) nmsWorld;
                
                IRegistryCustom registry = worldServer.s();
//              IRegistry<BiomeBase> biomeRegistry = worldServer.s().d(IRegistry.aR); //1.19
                IRegistry<BiomeBase> biomeRegistry = worldServer.s().d(Registries.al); //1.19.3
                
                ArrayList<BiomePreset> presets = new ArrayList<>();
                
                for (String tagKeyName : biomeRegistry.i().map((tagKey) -> tagKey.getFirst().b().a()).toList()) {
                    TagKey<BiomeBase> tagKey = TagKey.a(Registries.al, new MinecraftKey(tagKeyName));
                    
//                    Optional<HolderSet.Named<BiomeBase>> optional = biomeRegistry.c(tagKey);
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
                            material = switch (tagKeyName) {
                                case "is_deep_ocean", "is_river", "is_ocean", "reduce_water_ambient_spawns", "water_on_map_outlines", "has_closer_water_fog" -> Material.WATER_BUCKET;
                                case "is_mountain" -> Material.STONE;
                                case "is_hill", "is_overworld" -> Material.GRASS_BLOCK;
                                case "is_taiga" -> Material.SPRUCE_LOG;
                                case "is_beach" -> Material.SAND;
                                case "is_badlands" -> Material.TERRACOTTA;
                                case "is_nether" -> Material.NETHERRACK;
                                case "is_jungle" -> Material.JUNGLE_LOG;
                                case "is_forest" -> Material.OAK_SAPLING;
                                case "without_zombie_sieges" -> Material.ZOMBIE_HEAD;
                                case "required_ocean_monument_surrounding" -> Material.DARK_PRISMARINE;
                                case "spawns_cold_variant_frogs" -> Material.VERDANT_FROGLIGHT;
                                case "spawns_temperate_variant_frogs" -> Material.OCHRE_FROGLIGHT;
                                case "spawns_warm_variant_frogs" -> Material.PEARLESCENT_FROGLIGHT;
                                case "without_patrol_spawns" -> Material.RAVAGER_SPAWN_EGG;
                                case "allows_surface_slime_spawns" -> Material.SLIME_BALL;
                                case "without_wandering_trader_spawns" -> Material.WANDERING_TRADER_SPAWN_EGG;
                                case "polar_bears_spawn_on_alternate_blocks" -> Material.POLAR_BEAR_SPAWN_EGG;
                                case "is_end" -> Material.END_STONE;
                                case "plays_underwater_music" -> Material.MUSIC_DISC_MALL;
                                case "only_allows_snow_and_gold_rabbits" -> Material.RABBIT_SPAWN_EGG;
                                case "more_frequent_drowned_spawns" -> Material.DROWNED_SPAWN_EGG;
                                case "allows_tropical_fish_spawns_at_any_height" -> Material.TROPICAL_FISH_SPAWN_EGG;
                                case "produces_corals_from_bonemeal" -> Material.BRAIN_CORAL;
                                case "is_savanna" -> Material.ACACIA_LOG;
                                case "stronghold_biased_to" -> Material.END_PORTAL_FRAME;
                                case "mineshaft_blocking" -> Material.CHEST_MINECART;

                                default -> Material.DIAMOND_BLOCK;
                            };
                        }

                        presets.add(new BiomePreset("#" + tagKeyName, biomes, true, material, true));
                    }
                }

                tagPresets.put(world.getName(), presets);
                return presets;
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | NoSuchMethodError e) {
                Main.getInstance().getLogger().log(Level.WARNING, "Can't use NMS (try updating TPort), using legacy mode for BiomeTP");
                legacyBiomeTP = true;
            }
            return null;
        }
        
        public static BiomePreset getPreset(String name, World world) {
            for (BiomePreset preset : biomePresets) {
                if (preset.name().equalsIgnoreCase(name)) {
                    return preset;
                }
            }
            ArrayList<BiomePreset> tagPreset = tagPresets.getOrDefault(world.getName(), null);
            if (tagPreset == null) {
                tagPreset = Main.getOrDefault(loadPresetsFromWorld(world), new ArrayList<>());
            }
            for (BiomePreset preset : tagPreset) {
                if (preset.name().equalsIgnoreCase(name)) {
                    return preset;
                }
            }
            return null;
        }
        
        public record BiomePreset(String name, List<String> biomes, boolean whitelist, Material material, boolean fromMC) { }
    }
}
