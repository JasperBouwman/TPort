package com.spaceman.tport.biomeTP;

import com.google.common.collect.Iterables;
import com.google.gson.JsonObject;
import com.spaceman.tport.Main;
import com.spaceman.tport.Pair;
import com.spaceman.tport.commands.tport.BiomeTP;
import com.spaceman.tport.commands.tport.Features;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.MessageUtils;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fancyMessage.encapsulation.BiomeEncapsulation;
import com.spaceman.tport.fancyMessage.inventories.FancyClickEvent;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.stream.Collectors;

import static com.spaceman.tport.advancements.TPortAdvancement.Advancement_BiomeTP_OneIsNotEnough;
import static com.spaceman.tport.commands.tport.BiomeTP.availableBiomes;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.language.Language.getPlayerLang;
import static com.spaceman.tport.inventories.TPortInventories.biomeSelectionDataName;
import static com.spaceman.tport.inventories.TPortInventories.openBiomeTP;
import static org.bukkit.event.inventory.ClickType.LEFT;
import static org.bukkit.event.inventory.ClickType.SHIFT_LEFT;

public record BiomePreset(String name, List<String> biomes, boolean whitelist, Material material, boolean fromMC) {
    
    private static final ArrayList<BiomePreset> biomePresets = new ArrayList<>();
    private static final HashMap<String/*world name*/, ArrayList<BiomePreset>> tagPresets = new HashMap<>();
    
    public static ArrayList<BiomePreset> getBiomePresets() {
        return new ArrayList<>(biomePresets);
    }
    public static ArrayList<BiomePreset> getTagPresets(World world) {
        ArrayList<BiomePreset> list = new ArrayList<>();
        
        ArrayList<BiomePreset> tagPreset = tagPresets.getOrDefault(world.getName(), null);
        if (tagPreset == null) {
            tagPreset = Main.getOrDefault(loadPresetsFromWorld(world), new ArrayList<>());
        }
        
        for (BiomePreset preset : tagPreset) {
            if (list.stream().noneMatch(p -> p.name().equalsIgnoreCase(preset.name()))) {
                list.add(preset);
            }
        }
        return list;
    }
    
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
    
    public static List<String> getNames(World world) {
        List<String> list = biomePresets.stream().map(BiomePreset::name).collect(Collectors.toList());
        ArrayList<BiomePreset> tagPreset = tagPresets.getOrDefault(world.getName(), null);
        if (tagPreset == null) {
            tagPreset = Main.getOrDefault(loadPresetsFromWorld(world), new ArrayList<>());
        }
        tagPreset.stream().map(BiomePreset::name).forEach(list::add);
        return list;
    }
    
    public static List<ItemStack> getItems(Player player) {
        ArrayList<ItemStack> items = new ArrayList<>();
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        
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
            
            if (preset.fromMC()) {
                lore.add(new Message());
                lore.add(formatInfoTranslation("tport.commands.tport.biomeTP.biomeTPPresets.getItems.fromMC"));
                lore.add(new Message(textComponent(preset.name(), varInfoColor)));
            }
            lore.add(new Message());
            lore.add(formatInfoTranslation("tport.commands.tport.biomeTP.biomeTPPresets.getItems.selectBiomes.additive." + ((preset.biomes().size() == 1) ? "singular" : "multiple"), LEFT));
            lore.add(formatInfoTranslation("tport.commands.tport.biomeTP.biomeTPPresets.getItems.selectBiomes.overwrite." + ((preset.biomes().size() == 1) ? "singular" : "multiple"), ClickType.SHIFT_LEFT));
            lore.add(formatInfoTranslation("tport.commands.tport.biomeTP.biomeTPPresets.getItems.runPreset", ClickType.RIGHT));
            lore = MessageUtils.translateMessage(lore, playerLang);
            ColorTheme.ColorType titleColor = (preset.fromMC()) ? varInfo2Color : varInfoColor;
            Message biomeTitle = formatTranslation(titleColor, titleColor, "%s", preset.name());
            MessageUtils.setCustomItemData(is, colorTheme, biomeTitle, lore);
            
            ItemMeta im = is.getItemMeta();
            if (im == null) {
                continue;
            }
            
            FancyClickEvent.addCommand(im, ClickType.RIGHT, "tport biomeTP preset " + preset.name());
            im.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "biomePreset/name"), PersistentDataType.STRING, preset.name());
            im.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "biomePreset/whitelist"), PersistentDataType.INTEGER, preset.whitelist() ? 1 : 0);
            im.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "biomePreset/biomesFromPreset"), PersistentDataType.LIST.strings(), preset.biomes());
            FancyClickEvent.addFunction(im, ((whoClicked, clickType, pdc, fancyInventory) -> {
                NamespacedKey whitelistKey = new NamespacedKey(Main.getInstance(), "biomePreset/whitelist");
                NamespacedKey biomesFromPresetKey = new NamespacedKey(Main.getInstance(), "biomePreset/biomesFromPreset");
                
                if (pdc.has(biomesFromPresetKey, PersistentDataType.LIST.strings()) && pdc.has(whitelistKey, PersistentDataType.INTEGER)) {
                    List<String> biomesFromPreset = pdc.getOrDefault(biomesFromPresetKey, PersistentDataType.LIST.strings(), new ArrayList<>());
                    Integer isWhitelist = Main.getOrDefault(pdc.get(whitelistKey, PersistentDataType.INTEGER), 1);
                    Set<String> biomeSelection = clickType == LEFT ? fancyInventory.getData(biomeSelectionDataName) : new HashSet<>();
                    int originalSelectionSize = biomeSelection.size();
                    List<String> availableBiomes = availableBiomes(whoClicked.getWorld());
                    if (isWhitelist == 1) {
                        for (String biome : biomesFromPreset) {
                            if (availableBiomes.contains(biome)) {
                                biomeSelection.add(biome);
                            }
                        }
                    } else { //is blacklist
                        for (String availableBiome : availableBiomes) {
                            if (!biomesFromPreset.contains(availableBiome)) {
                                biomeSelection.add(availableBiome);
                            }
                        }
                    }
                    if (originalSelectionSize == biomeSelection.size()) {
                        sendErrorTranslation(whoClicked, "tport.commands.tport.biomeTP.biomeTPPresets.getItems.noBiomesLeft");
                    }
                    fancyInventory.setData(biomeSelectionDataName, biomeSelection);
                    openBiomeTP(whoClicked, 0, fancyInventory);
                    
                    Advancement_BiomeTP_OneIsNotEnough.grant(whoClicked);
                }
            }), LEFT, SHIFT_LEFT);
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
            case "spawns_warm_variant_farm_animals" -> "COW_SPAWN_EGG";
            case "spawns_cold_variant_farm_animals" -> "CHICKEN_SPAWN_EGG";
            
            default -> "DIAMOND_BLOCK";
        };
    }
    
    public static ArrayList<BiomePreset> loadPresetsFromWorld(World world) {
        ArrayList<BiomePreset> returnList = null;
        
        if (!BiomeTP.legacyBiomeTP) {
            try {
                returnList = Main.getInstance().adapter.loadPresetsFromWorld(world);
            } catch (Throwable ex) {
                Features.Feature.printSmallNMSErrorInConsole("BiomeTP Minecraft tags", false);
                if (Features.Feature.PrintErrorsInConsole.isEnabled()) ex.printStackTrace();
                BiomeTP.legacyBiomeTP = true;
            }
            if (returnList != null) {
                tagPresets.put(world.getName(), returnList);
            }
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
    
}
