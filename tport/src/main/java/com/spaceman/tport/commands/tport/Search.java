package com.spaceman.tport.commands.tport;

import com.google.common.collect.Iterables;
import com.google.gson.JsonObject;
import com.spaceman.tport.Main;
import com.spaceman.tport.biomeTP.BiomePreset;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.cooldown.CooldownManager;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fancyMessage.encapsulation.BiomeEncapsulation;
import com.spaceman.tport.fancyMessage.language.Language;
import com.spaceman.tport.inventories.ItemFactory;
import com.spaceman.tport.inventories.SettingsInventories;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.search.SearchType;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.spaceman.tport.biomeTP.BiomePreset.getBiomePresets;
import static com.spaceman.tport.biomeTP.BiomePreset.getTagPresets;
import static com.spaceman.tport.fancyMessage.MessageUtils.setCustomItemData;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.varInfoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.inventories.FancyClickEvent.setStringData;
import static com.spaceman.tport.fileHander.Files.tportData;
import static com.spaceman.tport.inventories.ItemFactory.HeadAttributes.CLICK_EVENTS;
import static com.spaceman.tport.inventories.ItemFactory.HeadAttributes.TPORT_AMOUNT;
import static com.spaceman.tport.inventories.ItemFactory.TPortItemAttributes.ADD_OWNER;
import static com.spaceman.tport.inventories.ItemFactory.TPortItemAttributes.CLICK_TO_OPEN;
import static com.spaceman.tport.inventories.ItemFactory.getHead;

public class Search extends SubCommand {
    
    private Search() { }
    private static final Search instance = new Search();
    public static Search getInstance() {
        EmptyCommand empty = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return "";
            }
        };
        empty.setCommandName("", ArgumentType.FIXED);
        empty.setCommandDescription(formatInfoTranslation("tport.command.search.commandDescription"));
        instance.addAction(empty);
        
        instance.registerSearchers();
        return instance;
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport search <type>
        // tport search <type> <searched...>
        // tport search <type> <mode> <searched...>
        
        /*
         * type:
         * canTP
         * player <mode> <player>
         * TPort <mode> <TPort name>
         * description <mode> <TPort description...>
         * dimension <dimension>
         * biome <biome...>
         * biomePreset <preset>
         * tag <tag>
         */
        
        /*
         * mode:
         * equals
         * contains
         * starts
         */
        
        if (args.length == 1) {
            SettingsInventories.openMainSearchGUI(player, 0);
            return;
        } else if (args.length > 1) {
            for (SubCommand action : getActions()) {
                if (action.getName(args[1]).equalsIgnoreCase(args[1]) || action.getAliases().stream().anyMatch(alias -> alias.equalsIgnoreCase(args[1]))) {
                    // permission will be checked in the TPortInventories.openSearchGUI
                    if (!CooldownManager.Search.hasCooled(player, true)) {
                        return;
                    }
                    action.run(args, player);
                    return;
                }
            }
        }
        sendErrorTranslation(player, "tport.command.wrongUsage", "/tport search <type> [mode] <searched...>");
    }
    
    private boolean alreadyRegistered = false;
    private void registerSearchers() { //todo remove 'query' naming
        if (alreadyRegistered) return;
        else alreadyRegistered = true;
        
        {
            SearchType searchType = new SearchType("TPort");
            searchType.setInventoryModel(SettingsInventories.settings_search_tport_model);
            
            searchType.setQuery("TPort name", false, false, null, null);
            searchType.hasSearchMode(true);
            
            searchType.setSearcher((searchMode, searched, player) -> {
                ArrayList<ItemStack> list = new ArrayList<>();
                for (String uuid : tportData.getKeys("tport")) {
                    for (TPort tport : TPortManager.getTPortList(UUID.fromString(uuid))) {
                        if (searchMode.fits(tport.getName(), searched)) {
                            list.add(ItemFactory.toTPortItem(tport, player, List.of(ADD_OWNER, CLICK_TO_OPEN)));
                        }
                    }
                }
                return list;
            });
            
            Search.Searchers.addSearcher(searchType);
        } //TPort
        
        {
            SearchType searchType = new SearchType("description");
            searchType.setInventoryModel(SettingsInventories.settings_search_description_model);
            
            searchType.setQuery("description", true, false, null, null);
            searchType.hasSearchMode(true);
            
            searchType.setSearcher((searchMode, searched, player) -> {
                ArrayList<ItemStack> list = new ArrayList<>();
                for (String uuid : tportData.getKeys("tport")) {
                    for (TPort tport : TPortManager.getTPortList(UUID.fromString(uuid))) {
                        if (searchMode.fits(tport.getTextDescription().replace("\n", ""), searched)) {
                            list.add(ItemFactory.toTPortItem(tport, player, List.of(ADD_OWNER, CLICK_TO_OPEN)));
                        }
                    }
                }
                return list;
            });
            
            Search.Searchers.addSearcher(searchType);
        } //description
        
        {
            SearchType searchType = new SearchType("player");
            searchType.setInventoryModel(SettingsInventories.settings_search_player_model);
            
            searchType.setQuery("player", false, false,
                    ((args, player) -> PlayerUUID.getPlayerNames()),
                    null);
            searchType.hasSearchMode(true);
            
            searchType.setSearcher((searchMode, searched, player) -> {
                ArrayList<ItemStack> list = new ArrayList<>();
                for (String uuid : tportData.getKeys("tport")) {
                    if (searchMode.fits(PlayerUUID.getPlayerName(uuid), searched)) {
                        list.add(Main.getOrDefault(getHead(UUID.fromString(uuid), player, List.of(TPORT_AMOUNT, CLICK_EVENTS), null), new ItemStack(Material.AIR)));
                    }
                }
                return list;
            });
            
            Search.Searchers.addSearcher(searchType);
        } //player
        
        {
            SearchType searchType = new SearchType("ownedTPorts");
            searchType.setInventoryModel(SettingsInventories.settings_search_owned_tports_model);
            
            searchType.setQuery("amount", false, true,
                    ((args, player) -> IntStream.range(0, 24).mapToObj(String::valueOf).toList()),
                    null);
            searchType.hasSearchMode(true);
            
            searchType.setSearcher((searchMode, searched, player) -> {
                ArrayList<ItemStack> list = new ArrayList<>();
                int i = Integer.parseInt(searched);
                for (String uuidString : tportData.getKeys("tport")) {
                    UUID uuid = UUID.fromString(uuidString);
                    if (searchMode.fits(TPortManager.getTPortList(uuid).size(), i)) {
                        list.add(Main.getOrDefault(getHead(uuid, player, List.of(TPORT_AMOUNT, CLICK_EVENTS), null), new ItemStack(Material.AIR)));
                    }
                }
                return list;
            });
            
            Search.Searchers.addSearcher(searchType);
        } //ownedTPorts
        
        {
            SearchType searchType = new SearchType("canTP");
            searchType.setInventoryModel(SettingsInventories.settings_search_can_tp_model);
            
            searchType.removeQuery();
            searchType.hasSearchMode(false);
            
            searchType.setSearcher((searchMode, searched, player) -> {
                ArrayList<ItemStack> list = new ArrayList<>();
                for (String uuid : tportData.getKeys("tport")) {
                    for (TPort tport : TPortManager.getTPortList(UUID.fromString(uuid))) {
                        if (tport.canTeleport(player, false, false, false)) {
                            list.add(ItemFactory.toTPortItem(tport, player, List.of(ADD_OWNER, CLICK_TO_OPEN)));
                        }
                    }
                }
                return list;
            });
            
            Search.Searchers.addSearcher(searchType);
        } //canTP
        
        {
            SearchType searchType = new SearchType("biome");
            searchType.setInventoryModel(SettingsInventories.settings_search_biome_model);
            
            searchType.setQuery("biome", true, false,
                    ((args, player) -> {
                        List<String> biomeList = Arrays.asList(args).subList(2, args.length).stream().map(String::toUpperCase).toList();
                        return Arrays.stream(Biome.values()).map(Biome::name).filter(name -> !biomeList.contains(name)).collect(Collectors.toList());
                    }),
                    ((player) -> {
                        ArrayList<ItemStack> items = new ArrayList<>();
                        ColorTheme colorTheme = ColorTheme.getTheme(player);
                        JsonObject playerLang = Language.getPlayerLang(player);
                        for (String biome : BiomeTP.availableBiomes()) {
                            ItemStack is = new ItemStack(BiomeTP.getMaterial(biome));
                            setStringData(is, new NamespacedKey(Main.getInstance(), "query"), biome);
                            Message biomeTitle = formatTranslation(varInfoColor, varInfoColor, "%s", new BiomeEncapsulation(biome)).translateMessage(playerLang);
                            setCustomItemData(is, colorTheme, biomeTitle, null);
                            items.add(is);
                        }
                        return items;
                    }));
            searchType.hasSearchMode(false);
            
            searchType.setSearcher((searchMode, searched, player) -> {
                ArrayList<ItemStack> list = new ArrayList<>();
                String[] split = searched.split(" ");
                for (String uuid : tportData.getKeys("tport")) {
                    for (TPort tport : TPortManager.getTPortList(UUID.fromString(uuid))) {
                        if (Arrays.stream(split).anyMatch(s -> s.equalsIgnoreCase(tport.getBiome().name()))) {
                            list.add(ItemFactory.toTPortItem(tport, player, List.of(ADD_OWNER, CLICK_TO_OPEN)));
                        }
                    }
                }
                return list;
            });
            
            Search.Searchers.addSearcher(searchType);
        } //biome
        
        {
            SearchType searchType = new SearchType("biomePreset");
            searchType.setInventoryModel(SettingsInventories.settings_search_biome_preset_model);
            
            searchType.setQuery("preset", false, false,
                    ((args, player) -> BiomePreset.getNames(player.getWorld())),
                    ((player) -> {
                        ArrayList<ItemStack> items = new ArrayList<>();
                        for (BiomePreset preset : Iterables.concat(getBiomePresets(), getTagPresets(player.getWorld()))) {
                            ItemStack is = new ItemStack(preset.material());
                            setStringData(is, new NamespacedKey(Main.getInstance(), "query"), preset.name());
                            items.add(is);
                        }
                        return items;
                    }));
            searchType.hasSearchMode(false);
            
            searchType.setSearcher((searchMode, searched, player) -> {
                ArrayList<ItemStack> list = new ArrayList<>();
                BiomePreset preset = BiomePreset.getPreset(searched, player.getLocation().getWorld());
                //noinspection ConstantConditions, command checks if available
                List<String> biomes = preset.biomes();
                for (String uuid : tportData.getKeys("tport")) {
                    for (TPort tport : TPortManager.getTPortList(UUID.fromString(uuid))) {
                        if (preset.whitelist()) {
                            if (biomes.stream().anyMatch(b -> b.equalsIgnoreCase(tport.getBiome().name()))) {
                                list.add(ItemFactory.toTPortItem(tport, player, List.of(ADD_OWNER, CLICK_TO_OPEN)));
                            }
                        } else {
                            if (biomes.stream().noneMatch(b -> b.equalsIgnoreCase(tport.getBiome().name()))) {
                                list.add(ItemFactory.toTPortItem(tport, player, List.of(ADD_OWNER, CLICK_TO_OPEN)));
                            }
                        }
                    }
                }
                return list;
            });
            
            Search.Searchers.addSearcher(searchType);
        } //biomePreset
        
        {
            SearchType searchType = new SearchType("dimension");
            searchType.setInventoryModel(SettingsInventories.settings_search_dimension_model);
            
            searchType.setQuery("dimension", false, false,
                    ((args, player) -> Arrays.stream(World.Environment.values()).map(Enum::name).collect(Collectors.toList())),
                    ((player) -> {
                        ArrayList<ItemStack> items = new ArrayList<>();
                        for (World.Environment environment : World.Environment.values()) {
                            ItemStack is = (switch (environment) {
                                case NORMAL -> SettingsInventories.settings_search_dimension_overworld_model;
                                case NETHER -> SettingsInventories.settings_search_dimension_nether_model;
                                case THE_END -> SettingsInventories.settings_search_dimension_the_end_model;
                                default -> SettingsInventories.settings_search_dimension_other_environments_model;
                            }).getItem(player);
                            
                            setStringData(is, new NamespacedKey(Main.getInstance(), "query"), environment.name());
                            items.add(is);
                        }
                        return items;
                    }));
            searchType.hasSearchMode(false);
            
            searchType.setSearcher((searchMode, searched, player) -> {
                ArrayList<ItemStack> list = new ArrayList<>();
                for (String uuid : tportData.getKeys("tport")) {
                    for (TPort tport : TPortManager.getTPortList(UUID.fromString(uuid))) {
                        if (tport.getDimension().name().equalsIgnoreCase(searched)) {
                            list.add(ItemFactory.toTPortItem(tport, player, List.of(ADD_OWNER, CLICK_TO_OPEN)));
                        }
                    }
                }
                return list;
            });
            
            Search.Searchers.addSearcher(searchType);
        } //dimension
        
        {
            SearchType searchType = new SearchType("world");
            searchType.setInventoryModel(SettingsInventories.settings_search_world_model);
            
            searchType.setQuery("world", false, false,
                    ((args, player) -> Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList())),
                    ((player) -> {
                        ArrayList<ItemStack> items = new ArrayList<>();
                        for (World world : Bukkit.getWorlds()) {
                            ItemStack is = (switch (world.getEnvironment()) {
                                case NORMAL -> SettingsInventories.settings_search_world_overworld_model;
                                case NETHER -> SettingsInventories.settings_search_world_nether_model;
                                case THE_END -> SettingsInventories.settings_search_world_the_end_model;
                                default -> SettingsInventories.settings_search_world_other_worlds_model;
                            }).getItem(player);
                            
                            setStringData(is, new NamespacedKey(Main.getInstance(), "query"), world.getName());
                            items.add(is);
                        }
                        return items;
                    }));
            searchType.hasSearchMode(false);
            
            searchType.setSearcher((searchMode, searched, player) -> {
                ArrayList<ItemStack> list = new ArrayList<>();
                for (String uuid : tportData.getKeys("tport")) {
                    for (TPort tport : TPortManager.getTPortList(UUID.fromString(uuid))) {
                        if (tport.getWorld().getName().equalsIgnoreCase(searched)) {
                            list.add(ItemFactory.toTPortItem(tport, player, List.of(ADD_OWNER, CLICK_TO_OPEN)));
                        }
                    }
                }
                return list;
            });
            
            Search.Searchers.addSearcher(searchType);
        } //world
        
        {
            SearchType searchType = new SearchType("tag");
            searchType.setInventoryModel(SettingsInventories.settings_search_tag_model);
            
            searchType.setQuery("tag", false, false,
                    ((args, player) -> Tag.getTags()),
                    ((player) -> {
                        ArrayList<ItemStack> items = new ArrayList<>();
                        for (String tag : Tag.getTags()) {
                            ItemStack is = SettingsInventories.settings_search_tag_tags_model.getItem(player);
                            setStringData(is, new NamespacedKey(Main.getInstance(), "query"), tag);
                            items.add(is);
                        }
                        return items;
                    }));
            searchType.hasSearchMode(false);
            
            searchType.setSearcher((searchMode, searched, player) -> {
                ArrayList<ItemStack> list = new ArrayList<>();
                for (String uuid : tportData.getKeys("tport")) {
                    for (TPort tport : TPortManager.getTPortList(UUID.fromString(uuid))) {
                        if (tport.getTags().contains(searched)) {
                            list.add(ItemFactory.toTPortItem(tport, player, List.of(ADD_OWNER, CLICK_TO_OPEN)));
                        }
                    }
                }
                return list;
            });
            
            Search.Searchers.addSearcher(searchType);
        } //tag
    }
    
    public static class Searchers {
        
        private static final HashMap<String, SearchType> searchers = new HashMap<>();
        
        public static boolean addSearcher(SearchType searchType) {
            if (searchType.hasSearcher() && !searchers.containsKey(searchType.getSearchTypeName())) {
                searchers.put(searchType.getSearchTypeName(), searchType);
                instance.addAction(searchType.buildCommand());
                return true;
            }
            return false;
        }
        
        public static Collection<SearchType> getSearchers() {
            return searchers.values();
        }
        
        public static SearchType getSearcher(String name) {
            return searchers.getOrDefault(name, null);
        }
    }
}
