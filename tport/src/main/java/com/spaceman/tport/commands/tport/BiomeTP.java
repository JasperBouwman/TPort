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
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

import static com.spaceman.tport.commandHandler.CommandTemplate.runCommands;
import static com.spaceman.tport.commands.tport.Back.prevTPorts;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.*;
import static com.spaceman.tport.fancyMessage.language.Language.getPlayerLang;
import static com.spaceman.tport.inventories.TPortInventories.openBiomeTP;
import static com.spaceman.tport.tpEvents.TPEManager.requestTeleportPlayer;

public class BiomeTP extends SubCommand {
    
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
        
        registerBiomeTPPresets();
    }
    
    public static List<String> availableBiomes() {
        return Main.getInstance().adapter.availableBiomes();
    }
    
    public static List<String> availableBiomes(World world) {
        return Main.getInstance().adapter.availableBiomes(world);
    }
    
    @Nullable
    public static Pair<Location, String> biomeFinder(Player player, List<String> biomes, @Nonnull Location startLocation, Accuracy.AccuracySettings accuracy) {
        return Main.getInstance().adapter.biomeFinder(player, biomes, startLocation, accuracy);
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
    
    @SuppressWarnings("CommentedOutCode")
    private void registerBiomeTPPresets() {
        BiomeTP.BiomeTPPresets.registerPreset("Land", Arrays.asList(
                "OCEAN", "WARM_OCEAN", "LUKEWARM_OCEAN",
                "COLD_OCEAN", "FROZEN_OCEAN", "DEEP_OCEAN",
                "DEEP_LUKEWARM_OCEAN",
                "DEEP_COLD_OCEAN", "DEEP_FROZEN_OCEAN",
                "RIVER", "FROZEN_RIVER"
        ), false, Material.GRASS_BLOCK);
        
        BiomeTP.BiomeTPPresets.registerPreset("Water", Arrays.asList(
                "OCEAN", "WARM_OCEAN", "LUKEWARM_OCEAN",
                "COLD_OCEAN", "FROZEN_OCEAN", "DEEP_OCEAN",
                "DEEP_LUKEWARM_OCEAN",
                "DEEP_COLD_OCEAN", "DEEP_FROZEN_OCEAN",
                "RIVER", "FROZEN_RIVER"
        ), true, Material.WATER_BUCKET);
        
        BiomeTP.BiomeTPPresets.registerPreset("The_End", Arrays.asList(
                        "END_BARRENS", "END_HIGHLANDS", "END_MIDLANDS", "THE_END", "SMALL_END_ISLANDS"),
                true, Material.END_STONE);
        
        BiomeTP.BiomeTPPresets.registerPreset("Nether", Arrays.asList(
                        "NETHER_WASTES", "BASALT_DELTAS",
                        "CRIMSON_FOREST", "WARPED_FOREST", "SOUL_SAND_VALLEY"),
                true, Material.NETHERRACK);
        
        BiomeTP.BiomeTPPresets.registerPreset("Trees", Arrays.asList(
                        "FOREST", "CHERRY_GROVE", "WINDSWEPT_FOREST", "DARK_FOREST", "SAVANNA", "SAVANNA_PLATEAU", "WINDSWEPT_SAVANNA", "JUNGLE", "SPARSE_JUNGLE", "BIRCH_FOREST", "OLD_GROWTH_BIRCH_FOREST", "TAIGA", "OLD_GROWTH_SPRUCE_TAIGA", "OLD_GROWTH_PINE_TAIGA"),
                true, Material.OAK_WOOD);

//        BiomeTP.BiomeTPPresets.registerPreset("name", Arrays.asList(
//                "biome names"),
//                true, Material.STONE);
    }
    
    public static Material getMaterial(String biome) {
        String materialName = switch (biome.toUpperCase()) {
            case "OCEAN", "RIVER", "DEEP_OCEAN", "LUKEWARM_OCEAN", "COLD_OCEAN", "DEEP_LUKEWARM_OCEAN", "DEEP_COLD_OCEAN" -> "WATER_BUCKET";
            case "PLAINS", "WINDSWEPT_HILLS", "MEADOW" -> "GRASS_BLOCK";
            case "DESERT", "BEACH" -> "SAND";
            case "FOREST", "WINDSWEPT_FOREST" -> "OAK_LOG";
            case "TAIGA", "OLD_GROWTH_PINE_TAIGA", "OLD_GROWTH_SPRUCE_TAIGA" -> "SPRUCE_LOG";
            case "SWAMP" -> "LILY_PAD";
            case "NETHER_WASTES" -> "NETHERRACK";
            case "THE_END", "SMALL_END_ISLANDS", "END_MIDLANDS", "END_HIGHLANDS", "END_BARRENS" -> "END_STONE";
            case "FROZEN_OCEAN", "FROZEN_RIVER", "DEEP_FROZEN_OCEAN" -> "ICE";
            case "ICE_SPIKES", "FROZEN_PEAKS" -> "PACKED_ICE";
            case "SNOWY_PLAINS", "SNOWY_BEACH", "SNOWY_TAIGA" -> "SNOW";
            case "MUSHROOM_FIELDS" -> "RED_MUSHROOM_BLOCK";
            case "JUNGLE", "SPARSE_JUNGLE" -> "JUNGLE_LOG";
            case "BAMBOO_JUNGLE" -> "BAMBOO";
            case "STONY_SHORE", "STONY_PEAKS" -> "STONE";
            case "BIRCH_FOREST", "OLD_GROWTH_BIRCH_FOREST" -> "BIRCH_LOG";
            case "DARK_FOREST" -> "DARK_OAK_LOG";
            case "SAVANNA", "SAVANNA_PLATEAU", "WINDSWEPT_SAVANNA" -> "ACACIA_LOG";
            case "BADLANDS", "WOODED_BADLANDS", "ERODED_BADLANDS" -> "TERRACOTTA";
            case "WARM_OCEAN" -> "BRAIN_CORAL_BLOCK";
            case "THE_VOID" -> "BARRIER";
            case "SUNFLOWER_PLAINS" -> "SUNFLOWER";
            case "WINDSWEPT_GRAVELLY_HILLS" -> "GRAVEL";
            case "FLOWER_FOREST" -> "ROSE_BUSH";
            case "SOUL_SAND_VALLEY" -> "SOUL_SAND";
            case "CRIMSON_FOREST" -> "CRIMSON_NYLIUM";
            case "WARPED_FOREST" -> "WARPED_NYLIUM";
            case "BASALT_DELTAS" -> "BASALT";
            case "DRIPSTONE_CAVES" -> "POINTED_DRIPSTONE";
            case "LUSH_CAVES" -> "GLOW_BERRIES";
            case "GROVE", "JAGGED_PEAKS" -> "SNOW_BLOCK";
            case "SNOWY_SLOPES" -> "POWDER_SNOW_BUCKET";
            case "MANGROVE_SWAMP" -> "MANGROVE_LOG";
            case "DEEP_DARK" -> "SCULK";
            case "CHERRY_GROVE" -> "CHERRY_LOG";
            
            default -> "DIAMOND_BLOCK";
        };
        return Main.getOrDefault(Material.getMaterial(materialName), Material.DIAMOND_BLOCK);
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
        
        if (Features.Feature.BiomeTP.isDisabled())  {
            Features.Feature.BiomeTP.sendDisabledMessage(player);
            return;
        }
        
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
         * @return {@link Pair} with {@link Boolean} (true when registered, false when not registered), and a {@link List<String>} with all unregistered biomes
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
                lore = MessageUtils.translateMessage(lore, playerLang);
                ColorType titleColor = (preset.fromMC) ? varInfo2Color : varInfoColor;
                Message biomeTitle = formatTranslation(titleColor, titleColor, "%s", preset.name);
                MessageUtils.setCustomItemData(is, ct, biomeTitle, lore);
                
                ItemMeta im = is.getItemMeta();
                if (im == null) {
                    continue;
                }
                
                String biomesAsString = String.join("|", preset.biomes());
                
                FancyClickEvent.addCommand(im, ClickType.RIGHT, "tport biomeTP preset " + preset.name());
                im.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "biomePreset/name"), PersistentDataType.STRING, preset.name());
                im.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "biomePreset/biomes"), PersistentDataType.STRING, biomesAsString);
                im.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "biomePreset/whitelist"), PersistentDataType.INTEGER, preset.whitelist ? 1 : 0);
                FancyClickEvent.addFunction(im, ClickType.LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
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
                FancyClickEvent.addFunction(im, ClickType.SHIFT_LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
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
        
        public static String getMaterialName(String tagKeyName) {
            return switch (tagKeyName) {
                case "is_deep_ocean", "is_river", "is_ocean", "reduce_water_ambient_spawns", "water_on_map_outlines", "has_closer_water_fog" -> "WATER_BUCKET";
                case "is_mountain" -> "STONE";
                case "is_hill", "is_overworld" -> "GRASS_BLOCK";
                case "is_taiga" -> "SPRUCE_LOG";
                case "is_beach" -> "SAND";
                case "is_badlands" -> "TERRACOTTA";
                case "is_nether" -> "NETHERRACK";
                case "is_jungle" -> "JUNGLE_LOG";
                case "is_forest" -> "OAK_SAPLING";
                case "without_zombie_sieges" -> "ZOMBIE_HEAD";
                case "required_ocean_monument_surrounding" -> "DARK_PRISMARINE";
                case "spawns_cold_variant_frogs" -> "VERDANT_FROGLIGHT";
                case "spawns_temperate_variant_frogs" -> "OCHRE_FROGLIGHT";
                case "spawns_warm_variant_frogs" -> "PEARLESCENT_FROGLIGHT";
                case "without_patrol_spawns" -> "RAVAGER_SPAWN_EGG";
                case "allows_surface_slime_spawns" -> "SLIME_BALL";
                case "without_wandering_trader_spawns" -> "WANDERING_TRADER_SPAWN_EGG";
                case "polar_bears_spawn_on_alternate_blocks" -> "POLAR_BEAR_SPAWN_EGG";
                case "is_end" -> "END_STONE";
                case "plays_underwater_music" -> "MUSIC_DISC_MALL";
                case "only_allows_snow_and_gold_rabbits", "spawns_gold_rabbits", "spawns_white_rabbits" -> "RABBIT_SPAWN_EGG";
                case "more_frequent_drowned_spawns" -> "DROWNED_SPAWN_EGG";
                case "allows_tropical_fish_spawns_at_any_height" -> "TROPICAL_FISH_SPAWN_EGG";
                case "produces_corals_from_bonemeal" -> "BRAIN_CORAL";
                case "is_savanna" -> "ACACIA_LOG";
                case "stronghold_biased_to" -> "END_PORTAL_FRAME";
                case "mineshaft_blocking" -> "CHEST_MINECART";
                case "snow_golem_melts" -> "CARVED_PUMPKIN";
                case "spawns_snow_foxes" -> "FOX_SPAWN_EGG";
                case "increased_fire_burnout" -> "FLINT_AND_STEEL";
                
                default -> "DIAMOND_BLOCK";
            };
        }
        
        private static ArrayList<BiomePreset> loadPresetsFromWorld(World world) {
            ArrayList<BiomePreset> returnList =  Main.getInstance().adapter.loadPresetsFromWorld(world);
            if (returnList != null) {
                tagPresets.put(world.getName(), returnList);
            }
            return returnList;
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
