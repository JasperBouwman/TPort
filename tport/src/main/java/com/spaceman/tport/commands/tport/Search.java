package com.spaceman.tport.commands.tport;

import com.spaceman.tport.Main;
import com.spaceman.tport.Pair;
import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.cooldown.CooldownManager;
import com.spaceman.tport.inventories.ItemFactory;
import com.spaceman.tport.inventories.TPortInventories;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatInfoTranslation;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;
import static com.spaceman.tport.fileHander.Files.tportData;
import static com.spaceman.tport.inventories.ItemFactory.HeadAttributes.CLICK_EVENTS;
import static com.spaceman.tport.inventories.ItemFactory.HeadAttributes.TPORT_AMOUNT;
import static com.spaceman.tport.inventories.ItemFactory.TPortItemAttributes.ADD_OWNER;
import static com.spaceman.tport.inventories.ItemFactory.TPortItemAttributes.CLICK_TO_OPEN;
import static com.spaceman.tport.inventories.ItemFactory.getHead;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;

public class Search extends SubCommand {
    
    private Search() { }
    private static final Search instance = new Search();
    public static Search getInstance() {
        instance.registerSearchers();
        return instance;
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport search <type>
        // tport search <type> <query...>
        // tport search <type> <mode> <query...>
        
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
        
        if (args.length > 1) {
            for (SubCommand action : getActions()) {
                if (action.getName(args[1]).equalsIgnoreCase(args[1]) || action.getAliases().stream().anyMatch(alias -> alias.equalsIgnoreCase(args[1]))) {
                    if (hasPermission(player, true, "TPort.search." + action.getName(args[1]))) {
                        if (!CooldownManager.Search.hasCooled(player, true)) {
                            return;
                        }
                        action.run(args, player);
                    }
                    return;
                }
            }
        }
        sendErrorTranslation(player, "tport.command.wrongUsage", "/tport search <type> [mode] <query...>");
        
    }
    
    private boolean alreadyRegistered = false;
    private void registerSearchers() {
        if (alreadyRegistered) return;
        else alreadyRegistered = true;
        
        {
            EmptyCommand emptyTPortModeQuery = new EmptyCommand();
            emptyTPortModeQuery.setCommandName("query", ArgumentType.REQUIRED);
            emptyTPortModeQuery.setCommandDescription(formatInfoTranslation("tport.command.search.tport.commandDescription"));
            
            EmptyCommand emptyTPortMode = new EmptyCommand();
            emptyTPortMode.setCommandName("mode", ArgumentType.REQUIRED);
            emptyTPortMode.addAction(emptyTPortModeQuery);
            
            EmptyCommand emptyTPort = new EmptyCommand() {
                @Override
                public String getName(String argument) {
                    return getCommandName();
                }
            };
            emptyTPort.setCommandName("TPort", ArgumentType.FIXED);
            emptyTPort.setTabRunnable(((args, player) -> Arrays.stream(Search.SearchMode.values()).map(Enum::name).collect(Collectors.toList())));
            emptyTPort.setRunnable(((args, player) -> {
                if (args.length == 4) {
                    Search.SearchMode searchMode = Search.SearchMode.get(args[2]);
                    if (searchMode == null) {
                        sendErrorTranslation(player, "tport.command.search.tport.modeNotExist", args[2]);
                        return;
                    }
                    TPortInventories.openSearchGUI(player, 0, searchMode, "TPort", args[3]);
                } else {
                    sendErrorTranslation(player, "tport.command.wrongUsage","/tport search TPort <mode> <TPort name>");
                }
            }));
            emptyTPort.addAction(emptyTPortMode);
            
            Search.Searchers.addSearcher((searchMode, query, player) -> {
                ArrayList<ItemStack> list = new ArrayList<>();
                for (String uuid : tportData.getKeys("tport")) {
                    for (TPort tport : TPortManager.getTPortList(UUID.fromString(uuid))) {
                        if (searchMode.fits(tport.getName(), query)) {
                            list.add(ItemFactory.toTPortItem(tport, player, List.of(ADD_OWNER, CLICK_TO_OPEN)));
                        }
                    }
                }
                return list;
            }, emptyTPort);
        } //TPort
        
        {
            EmptyCommand emptyDescriptionModeQuery = new EmptyCommand();
            emptyDescriptionModeQuery.setCommandName("query", ArgumentType.REQUIRED);
            emptyDescriptionModeQuery.setCommandDescription(formatInfoTranslation("tport.command.search.description.commandDescription"));
            emptyDescriptionModeQuery.setLooped(true);
            
            EmptyCommand emptyDescriptionMode = new EmptyCommand();
            emptyDescriptionMode.setCommandName("mode", ArgumentType.REQUIRED);
            emptyDescriptionMode.addAction(emptyDescriptionModeQuery);
            
            EmptyCommand emptyDescription = new EmptyCommand() {
                @Override
                public String getName(String argument) {
                    return getCommandName();
                }
            };
            emptyDescription.setCommandName("description", ArgumentType.FIXED);
            emptyDescription.setTabRunnable(((args, player) -> Arrays.stream(Search.SearchMode.values()).map(Enum::name).collect(Collectors.toList())));
            emptyDescription.setRunnable(((args, player) -> {
                if (args.length >= 4) {
                    Search.SearchMode searchMode = Search.SearchMode.get(args[2]);
                    if (searchMode == null) {
                        sendErrorTranslation(player, "tport.command.search.description.modeNotExist", args[2]);
                        return;
                    }
                    TPortInventories.openSearchGUI(player, 0, searchMode, "description", StringUtils.join(args, " ", 3, args.length));
                } else {
                    sendErrorTranslation(player, "tport.command.wrongUsage","/tport search description <mode> <TPort description...>");
                }
            }));
            emptyDescription.addAction(emptyDescriptionMode);
            
            Search.Searchers.addSearcher((searchMode, query, player) -> {
                ArrayList<ItemStack> list = new ArrayList<>();
                for (String uuid : tportData.getKeys("tport")) {
                    for (TPort tport : TPortManager.getTPortList(UUID.fromString(uuid))) {
                        if (searchMode.fits(tport.getTextDescription().replace("\n", ""), query)) {
                            list.add(ItemFactory.toTPortItem(tport, player, List.of(ADD_OWNER, CLICK_TO_OPEN)));
                        }
                    }
                }
                return list;
            }, emptyDescription);
        } //description
        
        {
            EmptyCommand emptyPlayerModeQuery = new EmptyCommand();
            emptyPlayerModeQuery.setCommandName("query", ArgumentType.REQUIRED);
            emptyPlayerModeQuery.setCommandDescription(formatInfoTranslation("tport.command.search.player.commandDescription"));
            emptyPlayerModeQuery.setLooped(true);
            
            EmptyCommand emptyPlayerMode = new EmptyCommand();
            emptyPlayerMode.setCommandName("mode", ArgumentType.REQUIRED);
            emptyPlayerMode.addAction(emptyPlayerModeQuery);
            
            EmptyCommand emptyPlayer = new EmptyCommand() {
                @Override
                public String getName(String argument) {
                    return getCommandName();
                }
            };
            emptyPlayer.setCommandName("player", ArgumentType.FIXED);
            emptyPlayer.setTabRunnable(((args, player) -> Arrays.stream(Search.SearchMode.values()).map(Enum::name).collect(Collectors.toList())));
            emptyPlayer.setRunnable(((args, player) -> {
                if (args.length == 4) {
                    Search.SearchMode searchMode = Search.SearchMode.get(args[2]);
                    if (searchMode == null) {
                        sendErrorTranslation(player, "tport.command.search.player.modeNotExist", args[2]);
                        return;
                    }
                    TPortInventories.openSearchGUI(player, 0, searchMode, "player", args[3]);
                } else {
                    sendErrorTranslation(player, "tport.command.wrongUsage","/tport search player <mode> <player>");
                }
            }));
            emptyPlayer.addAction(emptyPlayerMode);
            
            Search.Searchers.addSearcher((searchMode, query, player) -> {
                ArrayList<ItemStack> list = new ArrayList<>();
                for (String uuid : tportData.getKeys("tport")) {
                    if (searchMode.fits(PlayerUUID.getPlayerName(uuid), query)) {
                        list.add(Main.getOrDefault(getHead(UUID.fromString(uuid), player, List.of(TPORT_AMOUNT, CLICK_EVENTS), null), new ItemStack(Material.AIR)));
                    }
                }
                return list;
            }, emptyPlayer);
        } //player
        
        {
            EmptyCommand emptyCanTP = new EmptyCommand() {
                @Override
                public String getName(String argument) {
                    return getCommandName();
                }
            };
            emptyCanTP.setCommandName("canTP", ArgumentType.FIXED);
            emptyCanTP.setCommandDescription(formatInfoTranslation("tport.command.search.canTP.commandDescription"));
            emptyCanTP.setRunnable(((args, player) -> {
                if (args.length == 2) {
                    TPortInventories.openSearchGUI(player, 0, null, "canTP", "");
                } else {
                    sendErrorTranslation(player, "tport.command.wrongUsage","/tport search canTP");
                }
            }));
            
            Search.Searchers.addSearcher((searchMode, query, player) -> {
                ArrayList<ItemStack> list = new ArrayList<>();
                for (String uuid : tportData.getKeys("tport")) {
                    for (TPort tport : TPortManager.getTPortList(UUID.fromString(uuid))) {
                        if (tport.canTeleport(player, false, false, false)) {
                            list.add(ItemFactory.toTPortItem(tport, player, List.of(ADD_OWNER, CLICK_TO_OPEN)));
                        }
                    }
                }
                return list;
            }, emptyCanTP);
        } //canTP
        
        {
            EmptyCommand emptyBiomeBiome = new EmptyCommand();
            emptyBiomeBiome.setCommandName("biome", ArgumentType.REQUIRED);
            emptyBiomeBiome.setCommandDescription(formatInfoTranslation("tport.command.search.biome.commandDescription"));
            emptyBiomeBiome.setTabRunnable(((args, player) -> {
                List<String> biomeList = Arrays.asList(args).subList(2, args.length).stream().map(String::toUpperCase).toList();
                return Arrays.stream(Biome.values()).map(Enum::name).filter(name -> !biomeList.contains(name)).collect(Collectors.toList());
            }));
            emptyBiomeBiome.setLooped(true);
            
            EmptyCommand emptyBiome = new EmptyCommand() {
                @Override
                public String getName(String argument) {
                    return getCommandName();
                }
            };
            emptyBiome.setCommandName("biome", ArgumentType.FIXED);
            emptyBiome.setTabRunnable(((args, player) -> emptyBiomeBiome.tabList(player, args)));
            emptyBiome.setRunnable(((args, player) -> {
                if (args.length >= 3) {
                    StringBuilder str = new StringBuilder();
                    for (int i = 2; i < args.length; i++) {
                        try {
                            Biome.valueOf(args[i].toUpperCase());
                            str.append(args[i]).append(" ");
                        } catch (IllegalArgumentException iae) {
                            sendErrorTranslation(player, "tport.command.search.biome.biomeNotExist", args[i].toUpperCase());
                            return;
                        }
                    }
                    TPortInventories.openSearchGUI(player, 0, null, "biome", str.toString().trim());
                } else {
                    sendErrorTranslation(player, "tport.command.wrongUsage","/tport search biome <biome...>");
                }
            }));
            emptyBiome.addAction(emptyBiomeBiome);
            
            Search.Searchers.addSearcher((searchMode, query, player) -> {
                ArrayList<ItemStack> list = new ArrayList<>();
                for (String uuid : tportData.getKeys("tport")) {
                    for (TPort tport : TPortManager.getTPortList(UUID.fromString(uuid))) {
                        if (Arrays.stream(query.split(" ")).anyMatch(s -> s.equalsIgnoreCase(tport.getBiome().name()))) {
                            list.add(ItemFactory.toTPortItem(tport, player, List.of(ADD_OWNER, CLICK_TO_OPEN)));
                        }
                    }
                }
                return list;
            }, emptyBiome);
        } //biome
        
        {
            EmptyCommand emptyPresetPreset = new EmptyCommand();
            emptyPresetPreset.setCommandName("preset", ArgumentType.REQUIRED);
            emptyPresetPreset.setCommandDescription(formatInfoTranslation("tport.command.search.biomePreset.commandDescription"));
            
            EmptyCommand emptyPreset = new EmptyCommand() {
                @Override
                public String getName(String argument) {
                    return getCommandName();
                }
            };
            emptyPreset.setCommandName("biomePreset", ArgumentType.FIXED);
            emptyPreset.setTabRunnable(((args, player) -> BiomeTP.BiomeTPPresets.getNames()));
            emptyPreset.setRunnable(((args, player) -> {
                if (args.length == 3) {
                    BiomeTP.BiomeTPPresets.BiomePreset preset = BiomeTP.BiomeTPPresets.getPreset(args[2], player.getWorld());
                    if (preset != null) {
                        TPortInventories.openSearchGUI(player, 0, null, "biomePreset", args[2]);
                    } else {
                        sendErrorTranslation(player, "tport.command.search.biomePreset.presetNotExist", args[2]);
                    }
                } else {
                    sendErrorTranslation(player, "tport.command.wrongUsage","/tport search biomePreset <preset>");
                }
            }));
            emptyPreset.addAction(emptyPresetPreset);
            
            Search.Searchers.addSearcher((searchMode, query, player) -> {
                ArrayList<ItemStack> list = new ArrayList<>();
                for (String uuid : tportData.getKeys("tport")) {
                    for (TPort tport : TPortManager.getTPortList(UUID.fromString(uuid))) {
                        //noinspection ConstantConditions, command checks if available
                        if (BiomeTP.BiomeTPPresets.getPreset(query, tport.getLocation().getWorld()).biomes().stream().anyMatch(b -> b.equalsIgnoreCase(tport.getBiome().name()))) {
                            list.add(ItemFactory.toTPortItem(tport, player, List.of(ADD_OWNER, CLICK_TO_OPEN)));
                        }
                    }
                }
                return list;
            }, emptyPreset);
        } //biomePreset
        
        {
            EmptyCommand emptyDimensionDimension = new EmptyCommand();
            emptyDimensionDimension.setCommandName("dimension", ArgumentType.REQUIRED);
            emptyDimensionDimension.setCommandDescription(formatInfoTranslation("tport.command.search.dimension.commandDescription"));
            
            EmptyCommand emptyDimension = new EmptyCommand() {
                @Override
                public String getName(String argument) {
                    return getCommandName();
                }
            };
            emptyDimension.setCommandName("dimension", ArgumentType.FIXED);
            emptyDimension.setTabRunnable(((args, player) -> Arrays.stream(World.Environment.values()).map(Enum::name).collect(Collectors.toList())));
            emptyDimension.setRunnable(((args, player) -> {
                if (args.length == 3) {
                    try {
                        World.Environment.valueOf(args[2].toUpperCase());
                        TPortInventories.openSearchGUI(player, 0, null, "dimension", args[2]);
                    } catch (IllegalArgumentException iae) {
                        sendErrorTranslation(player, "tport.command.search.dimension.dimensionNotExist", args[2].toUpperCase());
                    }
                } else {
                    sendErrorTranslation(player, "tport.command.wrongUsage","/tport search dimension <dimension>");
                }
            }));
            emptyDimension.addAction(emptyDimensionDimension);
            
            Search.Searchers.addSearcher((searchMode, query, player) -> {
                ArrayList<ItemStack> list = new ArrayList<>();
                for (String uuid : tportData.getKeys("tport")) {
                    for (TPort tport : TPortManager.getTPortList(UUID.fromString(uuid))) {
                        if (tport.getDimension().name().equalsIgnoreCase(query)) {
                            list.add(ItemFactory.toTPortItem(tport, player, List.of(ADD_OWNER, CLICK_TO_OPEN)));
                        }
                    }
                }
                return list;
            }, emptyDimension);
        } //dimension
        
        {
            EmptyCommand emptyTagTag = new EmptyCommand();
            emptyTagTag.setCommandName("tag", ArgumentType.REQUIRED);
            emptyTagTag.setCommandDescription(formatInfoTranslation("tport.command.search.tag.commandDescription"));
            
            EmptyCommand emptyTag = new EmptyCommand() {
                @Override
                public String getName(String argument) {
                    return getCommandName();
                }
            };
            emptyTag.setCommandName("tag", ArgumentType.FIXED);
            emptyTag.setTabRunnable(((args, player) -> Tag.getTags()));
            emptyTag.setRunnable(((args, player) -> {
                if (args.length == 3) {
                    String tag = Tag.getTag(args[2]);
                    
                    if (tag == null) {
                        sendErrorTranslation(player, "tport.command.search.tag.tagNotExist", args[2]);
                        return;
                    }
                    
                    TPortInventories.openSearchGUI(player, 0, null, emptyTag.getCommandName(), tag);
                } else {
                    sendErrorTranslation(player, "tport.command.wrongUsage", "/tport search tag <tag>");
                }
            }));
            emptyTag.addAction(emptyTagTag);
            
            Search.Searchers.addSearcher((searchMode, query, player) -> {
                ArrayList<ItemStack> list = new ArrayList<>();
                for (String uuid : tportData.getKeys("tport")) {
                    for (TPort tport : TPortManager.getTPortList(UUID.fromString(uuid))) {
                        if (tport.getTags().contains(query)) {
                            list.add(ItemFactory.toTPortItem(tport, player, List.of(ADD_OWNER, CLICK_TO_OPEN)));
                        }
                    }
                }
                return list;
            }, emptyTag);
        } //tag
    }
    
    public enum SearchMode {
        EQUALS(String::contentEquals),
        CONTAINS(String::contains),
        STARTS(String::startsWith);
        
        private final Fitter fitter;
        
        SearchMode(Fitter fitter) {
            this.fitter = fitter;
        }
        
        public static SearchMode get(String s) {
            try {
                return valueOf(s.toUpperCase());
            } catch (IllegalArgumentException iae) {
                return null;
            }
        }
        
        public boolean fits(String s, String query) {
            return this.fitter.fit(s.toLowerCase(), query.toLowerCase());
        }
        
        @FunctionalInterface
        private interface Fitter {
            boolean fit(String s, String a);
        }
    }
    
    public static class Searchers {
        
        private static final HashMap<String, Pair<Searcher, SubCommand>> searchers = new HashMap<>();
        
        public static boolean addSearcher(Searcher searcher, SubCommand subCommand) {
            if (searcher != null && !searchers.containsKey(subCommand.getCommandName())) {
                searchers.put(subCommand.getCommandName(), new Pair<>(searcher, subCommand));
                setPermission(subCommand.getCommandName(), subCommand);
                instance.addAction(subCommand);
                return true;
            }
            return false;
        }
        
        private static void setPermission(String name, SubCommand subCommand) {
            subCommand.setPermissions("TPort.search." + name);
            for (SubCommand sub : subCommand.getActions()) {
                if (!(sub instanceof EmptyCommand) || !((EmptyCommand) sub).isLooped()) {
                    setPermission(name, sub);
                } else {
                    sub.setPermissions("TPort.search." + name);
                }
            }
        }
        
        public static Set<String> getSearchers() {
            return searchers.keySet();
        }
        
        public static Searcher getSearcher(String name) {
            Pair<Searcher, SubCommand> s = searchers.getOrDefault(name, null);
            return s == null ? null : s.getLeft();
        }
        
        public static SubCommand getSubCommand(String name) {
            Pair<Searcher, SubCommand> s = searchers.getOrDefault(name, null);
            return s == null ? null : s.getRight();
        }
        
        @FunctionalInterface
        public interface Searcher {
            List<ItemStack> search(SearchMode searchMode, String query, Player player);
        }
    }
}
