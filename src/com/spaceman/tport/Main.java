package com.spaceman.tport;

import com.spaceman.tport.commandHander.ArgumentType;
import com.spaceman.tport.commandHander.EmptyCommand;
import com.spaceman.tport.commands.TPortCommand;
import com.spaceman.tport.commands.tport.Tag;
import com.spaceman.tport.commands.tport.*;
import com.spaceman.tport.commands.tport.backup.Auto;
import com.spaceman.tport.dynmap.DynmapHandler;
import com.spaceman.tport.events.*;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fancyMessage.colorTheme.MultiColor;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import com.spaceman.tport.metrics.BiomeSearchCounter;
import com.spaceman.tport.metrics.CommandCounter;
import com.spaceman.tport.metrics.FeatureSearchCounter;
import com.spaceman.tport.metrics.Metrics;
import com.spaceman.tport.permissions.PermissionHandler;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.searchAreaHander.SearchAreaHandler;
import com.spaceman.tport.tpEvents.ParticleAnimation;
import com.spaceman.tport.tpEvents.TPEManager;
import com.spaceman.tport.tpEvents.TPRestriction;
import com.spaceman.tport.tpEvents.animations.ExplosionAnimation;
import com.spaceman.tport.tpEvents.animations.SimpleAnimation;
import com.spaceman.tport.tpEvents.restrictions.NoneRestriction;
import com.spaceman.tport.tpEvents.restrictions.WalkRestriction;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Slab;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.spaceman.tport.commands.TPortCommand.getHead;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.infoColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;

public class Main extends JavaPlugin {
    
    public static <O> O getOrDefault(@Nullable O object, O def) {
        return object == null ? def : object;
    }
    
    public static Main getInstance() {
        return JavaPlugin.getPlugin(Main.class);
    }
    
    public static ArrayList<ItemStack> giveItems(Player player, List<ItemStack> items) {
        ArrayList<ItemStack> returnList = new ArrayList<>();
        for (ItemStack item : player.getInventory().addItem(items.toArray(new ItemStack[0])).values()) {
            player.getWorld().dropItem(player.getLocation(), item);
            returnList.add(item);
            sendErrorTheme(player, "Your inventory is full, dropped item %s on the ground", item.getType().name());
        }
        return returnList;
    }
    
    public static ArrayList<ItemStack> giveItems(Player player, ItemStack... items) {
        return giveItems(player, Arrays.asList(items));
    }
    
    public static <I, J> HashMap<I, J> asMap(Pair<I, J>... pairs) {
        HashMap<I, J> map = new HashMap<>();
        for (Pair<I, J> pair : pairs) {
            map.put(pair.getLeft(), pair.getRight());
        }
        return map;
    }
    
    public static Location getLocation(String path) {
        return getLocation(path, GettingFiles.getFile("TPortData"));
    }
    
    public static Location getLocation(String path, Files file) {
        if (!file.getConfig().contains(path)) {
            return null;
        }
        World world;
        try {
            world = Bukkit.getWorld(file.getConfig().getString(path + ".world"));
        } catch (Exception e) {
            return null;
        }
        if (world == null) {
            return null;
        }
        
        double x = file.getConfig().getDouble(path + ".x");
        double y = file.getConfig().getDouble(path + ".y");
        double z = file.getConfig().getDouble(path + ".z");
        
        float yaw = file.getConfig().getInt(path + ".yaw");
        float pitch = file.getConfig().getInt(path + ".pitch");
        
        return new Location(world, x, y, z, yaw, pitch);
    }
    
    public static void saveLocation(String path, Location location, Files tportData) {
        tportData.getConfig().set(path + ".world", Objects.requireNonNull(location.getWorld()).getName());
        tportData.getConfig().set(path + ".x", location.getX());
        tportData.getConfig().set(path + ".y", location.getY());
        tportData.getConfig().set(path + ".z", location.getZ());
        tportData.getConfig().set(path + ".pitch", location.getPitch());
        tportData.getConfig().set(path + ".yaw", location.getYaw());
        tportData.saveConfig();
    }
    
    public static ArrayList<String> getPlayerNames() {
        ArrayList<String> list = new ArrayList<>();
        Files tportData = GettingFiles.getFile("TPortData");
        for (OfflinePlayer op : Bukkit.getOnlinePlayers()) {
            if (tportData.getConfig().contains("tport." + op.getUniqueId())) {
                list.add(op.getName());
            }
        }
        return list;
    }
    
    public static ArrayList<UUID> getPlayerUUIDs() {
        ArrayList<UUID> list = new ArrayList<>();
        Files tportData = GettingFiles.getFile("TPortData");
        for (OfflinePlayer op : Bukkit.getOnlinePlayers()) {
            if (tportData.getConfig().contains("tport." + op.getUniqueId())) {
                list.add(op.getUniqueId());
            }
        }
        return list;
    }
    
    public static boolean containsSpecialCharacter(String s) {
        if (s == null || s.trim().isEmpty()) {
            return true;
        }
        Pattern p = Pattern.compile("[^A-Za-z0-9_-]");
        Matcher m = p.matcher(s);
        
        return m.find();
    }
    
    public static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
    }
    
    public void onEnable() {
        
        /*
         * changelog 1.16.1 update:
         * added:
         *  /tport log notify
         *  /tport search <type>
         *  /tport edit <TPort name> tag add <tag>
         *  /tport edit <TPort name> tag remove <tag>
         *  /tport tag create <tag> <permission>
         *  /tport tag delete <tag>
         *  /tport tag list
         *  /tport tag reset
         *  /tport sort [sorter]
         *  /tport safetyCheck [default state]
         *  /tport open <player> <TPort name> [safetyCheck]
         *  /tport own <TPort name> [safetyCheck]
         *  /tport back [safetyCheck]
         *  /tport permissions enabled [state]
         *  /tport mainLayout players [state]
         *  /tport mainLayout TPorts [state]
         *  /tport world <world>   (used to teleport to the spawn of the given world)
         *  '/tport search' cooldown, default value is 10 seconds
         * fixed:
         *  tport back from '/tport biomeTP random'
         * Teleporter items (old name compass) are now TPort rename friendly
         * new buttons in the main TPort GUI to go to BiomeTP, FeatureTP and Public TPorts and to teleport back (/tport back)
         * added the permission 'TPort.featureTP.open' to open the FeatureTP gui (/tport featureTP)
         *
         * players in your PLTP whitelist don't have to ask for consent anymore (when PLTP consent is set to true)
         *
         * added metrics (powered by bStats)
         *  /tport metrics enable [state]
         *  /tport metrics viewStats
         *
         * added Dynmap support.
         * new commands:
         *  /tport dynmap
         *  /tport dynmap enable [state]
         *  /tport dynmap show <player> <tport name>
         *  /tport dynmap IP [IP]
         *  /tport edit <TPort name> dynmap show [state]
         *  /tport edit <TPort name> dynmap icon [icon]
         *
         * improved PLTP Offset BEHIND
         *
         * if a TPort is public (/tport public add <TPort name>) it can't be renamed to a name that contains as a Public TPort
         *
         * when a player gets offline, his location won't be stored to use for TPort range. When he is offline he is just out of range.
         * fixed error when owner is in different dimension than TPort, when trying to measure the distance between owner and TPort
         *
         * updated '/tport teleporter create' command descriptions
         *
         * ColorTheme now supports HEX colors (#123456), to use '/tport colorTheme set <type> <hex color>'
         * added some more color themes, if you have made any for yourself you should share them with me. If I like it I will add it to TPort
         *
         * updated the permissions system for the commands. There may be some bugs that the permission does not properly works for that command.
         * If you found any leave a command at https://dev.bukkit.org/projects/tport
         *
         * you can now search for the different village types (Village_Desert, Village_Plains, Village_Savanna, Village_Snowy, Village_Taiga),
         * it won't always gets the closest village when the FeatureTP mode is set to CLOSEST
         * Updated feature display items in GUI
         *
         * added a SafetyCheck, to use this use the command '/tport safetyCheck [default state]'
         * when true it will preform a safetyCheck before you teleport. It checks if the location does not exist of solid blocks, and the block you are standing on is not lava/fire
         *
         * the walkRestriction now cancels your TP request when you walk. Before it canceled your teleportation, but it forgot to remove the request.
         * TPort restriction can now be used with permissions, permission: TPort.restriction.type.<restriction name>
         * To enable use '/tport restriction permission true'
         *
         * TPort back form TPorts is now TPort rename friendly
         *
         * permission 'TPort.biomeTP.all' is replaced by 'TPort.biomeTP.*'
         */
        
        /*
         * todo
         * remove converting methods (next update)
         *
         * update biome search system
         *
         * /tport sort [popularity]
         *  create popularity system for /tport sort popularity
         *
         * /tport featureTP searchTries [tries]
         * /tport biomeTP mode [mode]
         *
         * /tport searchArea set <type>
         * /tport searchArea get
         * /tport searchArea configure [data...]
         * /tport searchArea show <world>
         * /tport searchArea description <type>
         *
         * polygon:     tport searchArea configure <world> <corner> set <x> <z>
         *              tport searchArea configure <world> <corner> get
         *              tport searchArea configure <world> <corner> remove
         * permission:  tport searchArea configure
         * worldBorder: tport searchArea configure
         *
         * Permissions
         * TPort.searchArea.subType.<type> {polygon or worldBorder}
         *
         * Polygon permissions:
         * TPort.searchArea.data.<world>.<corner>.x.<x>
         * TPort.searchArea.data.<world>.<corner>.z.<z>
         *
         * worldBorder permissions:
         * N/A
         *
         *
         *
         * fix /tport reload
         *
         *
         *
         * update /tport version compatible Bukkit versions
         * create tutorial for creating your own Particle Animations and TP Restrictions
         * */
        ConfigurationSerialization.registerClass(ColorTheme.class, "ColorTheme");
        ConfigurationSerialization.registerClass(com.spaceman.tport.colorFormatter.ColorTheme.class, "ColorTheme");
        ConfigurationSerialization.registerClass(TPort.class, "TPort");
        ConfigurationSerialization.registerClass(Pair.class, "Pair");
        ConfigurationSerialization.registerClass(MultiColor.class, "MultiColor");
        
        Reload.reloadTPort();
        
        ParticleAnimation.registerAnimation(SimpleAnimation::new);
        ParticleAnimation.registerAnimation(ExplosionAnimation::new);
        TPRestriction.registerRestriction(NoneRestriction::new);
        TPRestriction.registerRestriction(WalkRestriction::new);
        
        SafetyCheck.setSafetyCheck((feet) -> {
            
            Location head = feet.clone().add(0, 1, 0);
            Location ground = feet.clone().add(0, -1, 0);
            
            return !ground.getBlock().getType().equals(Material.LAVA) &&
                    !ground.getBlock().getType().equals(Material.CAMPFIRE) &&
                    !ground.getBlock().getType().equals(Material.SOUL_CAMPFIRE) &&
                    !ground.getBlock().getType().equals(Material.FIRE) && //todo add other damageable blocks
                    !ground.getBlock().getType().equals(Material.SOUL_FIRE) &&
                    !ground.getBlock().getType().equals(Material.MAGMA_BLOCK) &&
                    (ground.getBlock().getType().isSolid() || ground.getBlock().getType().equals(Material.WATER)) &&
                    !head.getBlock().getType().isSolid() &&
                    !head.getBlock().getType().equals(Material.LAVA) &&
                    !head.getBlock().getType().equals(Material.FIRE) &&
                    !head.getBlock().getType().equals(Material.SOUL_FIRE) &&
                    !head.getBlock().getType().equals(Material.MAGMA_BLOCK) &&
                    !feet.getBlock().getType().isSolid() &&
                    !feet.getBlock().getType().equals(Material.LAVA) &&
                    !feet.getBlock().getType().equals(Material.FIRE) &&
                    !feet.getBlock().getType().equals(Material.WITHER_ROSE) &&
                    !feet.getBlock().getType().equals(Material.SOUL_FIRE) &&
                    !feet.getBlock().getType().equals(Material.MAGMA_BLOCK);
        });
        
        registerBiomeTPPresets();
        
        TPEManager.loadTPE(GettingFiles.getFile("TPortConfig"));
        
        ColorTheme.loadThemes(GettingFiles.getFile("TPortConfig"));
        
        Redirect.Redirects.loadRedirects();
        
        Glow.registerGlow();
        
        Tag.loadTags();
        
        new TPortCommand();
        
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new InventoryClick(), this);
        pm.registerEvents(new TeleporterEvents(), this);
        pm.registerEvents(new JoinEvent(), this);
        pm.registerEvents(new RespawnEvent(), this);
        pm.registerEvents(new CommandEvent(), this);
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            JoinEvent.setData(this, player);
        }
        
        Files tportConfig = GettingFiles.getFile("TPortConfig");
        if (!tportConfig.getConfig().contains("biomeTP.searches")) {
            tportConfig.getConfig().set("biomeTP.searches", 100);
            tportConfig.saveConfig();
        }
        if (!tportConfig.getConfig().contains("tags.list")) {
            Tag.resetTags();
        }
        
        registerSearchers();
        registerSorters();
        registerSearchAreas();
        
        if (MetricsCommand.isEnabled()) {
            Main.getInstance().getLogger().log(Level.INFO, "Enabling metrics. Thank you for enabling metrics, powered by bStats. To view stats use '/tport metrics viewStats'");
            Metrics m = new Metrics(this, 8061);
            m.addCustomChart(new Metrics.AdvancedPie("command_usage", CommandCounter::getData));
            m.addCustomChart(new Metrics.AdvancedPie("biome_searches", BiomeSearchCounter::getData));
            m.addCustomChart(new Metrics.AdvancedPie("feature_searches", FeatureSearchCounter::getData));
            m.addCustomChart(new Metrics.SimplePie("tport_version", () -> this.getDescription().getVersion()));
            m.addCustomChart(new Metrics.SimplePie("public_enabled", () -> (Public.isEnabled() ? "True" : "False")));
            m.addCustomChart(new Metrics.SimplePie("permissions_enabled", () -> (PermissionHandler.isPermissionEnabled() ? "True" : "False")));
            m.addCustomChart(new Metrics.SimplePie("dynmap_enabled", () -> (DynmapHandler.isEnabled() ? "True" : "False")));
        }
        
        DynmapHandler.enable();
    }
    
    @Override
    public void onDisable() {
        TPEManager.saveTPE(GettingFiles.getFile("TPortConfig"));
        ColorTheme.saveThemes(GettingFiles.getFile("TPortConfig"));
        Redirect.Redirects.saveRedirects();
        Tag.saveTags();
        Auto.save();
        DynmapHandler.disable();
    }
    
    private void registerBiomeTPPresets() {
        BiomeTP.BiomeTPPresets.registerPreset("Land", Arrays.asList(
                Biome.OCEAN, Biome.WARM_OCEAN, Biome.LUKEWARM_OCEAN,
                Biome.COLD_OCEAN, Biome.FROZEN_OCEAN, Biome.DEEP_OCEAN,
                Biome.DEEP_WARM_OCEAN, Biome.DEEP_LUKEWARM_OCEAN,
                Biome.DEEP_COLD_OCEAN, Biome.DEEP_FROZEN_OCEAN,
                Biome.RIVER, Biome.FROZEN_RIVER
        ), false, Material.DIRT);
        
        BiomeTP.BiomeTPPresets.registerPreset("Water", Arrays.asList(
                Biome.OCEAN, Biome.WARM_OCEAN, Biome.LUKEWARM_OCEAN,
                Biome.COLD_OCEAN, Biome.FROZEN_OCEAN, Biome.DEEP_OCEAN,
                Biome.DEEP_WARM_OCEAN, Biome.DEEP_LUKEWARM_OCEAN,
                Biome.DEEP_COLD_OCEAN, Biome.DEEP_FROZEN_OCEAN,
                Biome.RIVER, Biome.FROZEN_RIVER
        ), true, Material.WATER_BUCKET);
        
        BiomeTP.BiomeTPPresets.registerPreset("The_End", Arrays.asList(
                Biome.END_BARRENS, Biome.END_HIGHLANDS, Biome.END_MIDLANDS, Biome.THE_END, Biome.SMALL_END_ISLANDS),
                true, Material.END_STONE);
        
        BiomeTP.BiomeTPPresets.registerPreset("Savannah", Arrays.asList(
                Biome.SAVANNA, Biome.SAVANNA_PLATEAU, Biome.SHATTERED_SAVANNA, Biome.SHATTERED_SAVANNA_PLATEAU),
                true, Material.ACACIA_PLANKS);
        
        BiomeTP.BiomeTPPresets.registerPreset("Taiga", Arrays.asList(
                Biome.TAIGA,
                Biome.TAIGA_HILLS,
                Biome.TAIGA_MOUNTAINS,
                Biome.SNOWY_TAIGA,
                Biome.SNOWY_TAIGA_MOUNTAINS
                ),
                true, Material.SPRUCE_PLANKS);
        
        BiomeTP.BiomeTPPresets.registerPreset("Giant_Taiga", Arrays.asList(
                Biome.GIANT_TREE_TAIGA,
                Biome.GIANT_TREE_TAIGA_HILLS,
                Biome.GIANT_SPRUCE_TAIGA,
                Biome.GIANT_SPRUCE_TAIGA_HILLS
                ),
                true, Material.SPRUCE_LOG);
        
        BiomeTP.BiomeTPPresets.registerPreset("Mushroom", Arrays.asList(
                Biome.MUSHROOM_FIELDS, Biome.MUSHROOM_FIELD_SHORE),
                true, Material.RED_MUSHROOM_BLOCK);
        
        BiomeTP.BiomeTPPresets.registerPreset("Jungle", Arrays.asList(
                Biome.JUNGLE, Biome.JUNGLE_EDGE, Biome.JUNGLE_HILLS,
                Biome.BAMBOO_JUNGLE, Biome.MODIFIED_JUNGLE, Biome.BAMBOO_JUNGLE_HILLS,
                Biome.MODIFIED_JUNGLE_EDGE),
                true, Material.JUNGLE_PLANKS);
        
        BiomeTP.BiomeTPPresets.registerPreset("Badlands", Arrays.asList(
                Biome.BADLANDS, Biome.BADLANDS_PLATEAU, Biome.ERODED_BADLANDS,
                Biome.MODIFIED_BADLANDS_PLATEAU, Biome.MODIFIED_WOODED_BADLANDS_PLATEAU,
                Biome.WOODED_BADLANDS_PLATEAU),
                true, Material.TERRACOTTA);
        
        BiomeTP.BiomeTPPresets.registerPreset("Birch", Arrays.asList(
                Biome.BIRCH_FOREST, Biome.BIRCH_FOREST_HILLS,
                Biome.TALL_BIRCH_FOREST, Biome.TALL_BIRCH_HILLS),
                true, Material.BIRCH_PLANKS);
        
        BiomeTP.BiomeTPPresets.registerPreset("Nether", Arrays.asList(
                Biome.NETHER_WASTES, Biome.BASALT_DELTAS,
                Biome.CRIMSON_FOREST, Biome.WARPED_FOREST, Biome.SOUL_SAND_VALLEY),
                true, Material.NETHERRACK);
        
        BiomeTP.BiomeTPPresets.registerPreset("Mountains", Arrays.asList(
                Biome.MOUNTAINS, Biome.MOUNTAIN_EDGE,
                Biome.MODIFIED_GRAVELLY_MOUNTAINS, Biome.GRAVELLY_MOUNTAINS),
                true, Material.STONE);
        
        BiomeTP.BiomeTPPresets.registerPreset("Swamp", Arrays.asList(
                Biome.SWAMP, Biome.SWAMP_HILLS),
                true, Material.LILY_PAD);
        
        BiomeTP.BiomeTPPresets.registerPreset("Desert", Arrays.asList(
                Biome.DESERT, Biome.DESERT, Biome.DESERT_LAKES),
                true, Material.SAND);

//        BiomeTP.BiomeTPPresets.registerPreset("name", Arrays.asList(
//                null),
//                true, Material.STONE);
    }
    
    private void registerSearchers() {
        {
            EmptyCommand emptyTPortModeQuery = new EmptyCommand();
            emptyTPortModeQuery.setCommandName("query", ArgumentType.REQUIRED);
            emptyTPortModeQuery.setCommandDescription(textComponent("This command is used to search TPorts by their name", infoColor));
            
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
                        sendErrorTheme(player, "Search mode %s does not exist", args[2]);
                        return;
                    }
                    TPortInventories.openSearchGUI(player, 0, searchMode, "TPort", args[3]);
                } else {
                    sendErrorTheme(player, "Usage: %s", "/tport search TPort <mode> <TPort name>");
                }
            }));
            emptyTPort.addAction(emptyTPortMode);
            
            Search.Searchers.addSearcher((searchMode, query, player) -> {
                ArrayList<ItemStack> list = new ArrayList<>();
                for (String uuid : GettingFiles.getFile("TPortData").getKeys("tport")) {
                    for (TPort tport : TPortManager.getTPortList(UUID.fromString(uuid))) {
                        if (searchMode.fits(tport.getName(), query)) {
                            list.add(TPortInventories.toTPortItem(tport, player, true));
                        }
                    }
                }
                return list;
            }, emptyTPort);
        } //TPort
        
        {
            EmptyCommand emptyDescriptionModeQuery = new EmptyCommand();
            emptyDescriptionModeQuery.setCommandName("query", ArgumentType.REQUIRED);
            emptyDescriptionModeQuery.setCommandDescription(textComponent("This command is used to search TPorts by their description", infoColor));
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
                        sendErrorTheme(player, "Search mode %s does not exist", args[2]);
                        return;
                    }
                    TPortInventories.openSearchGUI(player, 0, searchMode, "description", StringUtils.join(args, " ", 3, args.length));
                } else {
                    sendErrorTheme(player, "Usage: %s", "/tport search description <mode> <TPort description...>");
                }
            }));
            emptyDescription.addAction(emptyDescriptionMode);
            
            Search.Searchers.addSearcher((searchMode, query, player) -> {
                ArrayList<ItemStack> list = new ArrayList<>();
                for (String uuid : GettingFiles.getFile("TPortData").getKeys("tport")) {
                    for (TPort tport : TPortManager.getTPortList(UUID.fromString(uuid))) {
                        if (searchMode.fits(tport.getDescription().replace("\n", ""), query)) {
                            list.add(TPortInventories.toTPortItem(tport, player, true));
                        }
                    }
                }
                return list;
            }, emptyDescription);
        } //description
        
        {
            EmptyCommand emptyPlayerModeQuery = new EmptyCommand();
            emptyPlayerModeQuery.setCommandName("query", ArgumentType.REQUIRED);
            emptyPlayerModeQuery.setCommandDescription(textComponent("This command is used to search players by their name", infoColor));
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
                        sendErrorTheme(player, "Search mode %s does not exist", args[2]);
                        return;
                    }
                    TPortInventories.openSearchGUI(player, 0, searchMode, "player", args[3]);
                } else {
                    sendErrorTheme(player, "Usage: %s", "/tport search player <mode> <player>");
                }
            }));
            emptyPlayer.addAction(emptyPlayerMode);
            
            Search.Searchers.addSearcher((searchMode, query, player) -> {
                ArrayList<ItemStack> list = new ArrayList<>();
                for (String uuid : GettingFiles.getFile("TPortData").getKeys("tport")) {
                    if (searchMode.fits(PlayerUUID.getPlayerName(uuid), query)) {
                        list.add(getOrDefault(getHead(UUID.fromString(uuid), player), new ItemStack(Material.AIR)));
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
            emptyCanTP.setCommandDescription(textComponent("This command is used to search TPorts you can teleport to", infoColor));
            emptyCanTP.setRunnable(((args, player) -> {
                if (args.length == 2) {
                    TPortInventories.openSearchGUI(player, 0, null, "canTP", "");
                } else {
                    sendErrorTheme(player, "Usage: %s", "/tport search canTP");
                }
            }));
            
            Search.Searchers.addSearcher((searchMode, query, player) -> {
                ArrayList<ItemStack> list = new ArrayList<>();
                for (String uuid : GettingFiles.getFile("TPortData").getKeys("tport")) {
                    for (TPort tport : TPortManager.getTPortList(UUID.fromString(uuid))) {
                        if (tport.canTeleport(player, false, false)) {
                            list.add(TPortInventories.toTPortItem(tport, player, true));
                        }
                    }
                }
                return list;
            }, emptyCanTP);
        } //canTP
        
        {
            EmptyCommand emptyBiomeBiome = new EmptyCommand();
            emptyBiomeBiome.setCommandName("biome", ArgumentType.REQUIRED);
            emptyBiomeBiome.setCommandDescription(textComponent("This command is used to search TPorts by their biome", infoColor));
            emptyBiomeBiome.setTabRunnable(((args, player) -> {
                List<String> biomeList = Arrays.asList(args).subList(2, args.length).stream().map(String::toUpperCase).collect(Collectors.toList());
                return Arrays.stream(Biome.values()).filter(biome -> !biomeList.contains(biome.name())).map(Enum::name).collect(Collectors.toList());
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
                            sendErrorTheme(player, "Biome %s does not exist", args[i].toUpperCase());
                            return;
                        }
                    }
                    TPortInventories.openSearchGUI(player, 0, null, "biome", str.toString().trim());
                } else {
                    sendErrorTheme(player, "Usage: %s", "/tport search biome <biome...>");
                }
            }));
            emptyBiome.addAction(emptyBiomeBiome);
            
            Search.Searchers.addSearcher((searchMode, query, player) -> {
                ArrayList<ItemStack> list = new ArrayList<>();
                for (String uuid : GettingFiles.getFile("TPortData").getKeys("tport")) {
                    for (TPort tport : TPortManager.getTPortList(UUID.fromString(uuid))) {
                        if (Arrays.stream(query.split(" ")).anyMatch(s -> s.equalsIgnoreCase(tport.getBiome().name()))) {
                            list.add(TPortInventories.toTPortItem(tport, player, true));
                        }
                    }
                }
                return list;
            }, emptyBiome);
        } //biome
        
        {
            EmptyCommand emptyBiomeBiome = new EmptyCommand();
            emptyBiomeBiome.setCommandName("preset", ArgumentType.REQUIRED);
            emptyBiomeBiome.setCommandDescription(textComponent("This command is used to search TPorts by their biome", infoColor));
            
            EmptyCommand emptyDimension = new EmptyCommand() {
                @Override
                public String getName(String argument) {
                    return getCommandName();
                }
            };
            emptyDimension.setCommandName("biomePreset", ArgumentType.FIXED);
            emptyDimension.setTabRunnable(((args, player) -> BiomeTP.BiomeTPPresets.getNames()));
            emptyDimension.setRunnable(((args, player) -> {
                if (args.length == 3) {
                    BiomeTP.BiomeTPPresets.Preset preset = BiomeTP.BiomeTPPresets.getPreset(args[2]);
                    if (preset != null) {
                        TPortInventories.openSearchGUI(player, 0, null, "biomePreset", args[2]);
                    } else {
                        sendErrorTheme(player, "Preset %s does not exist", args[2]);
                    }
                } else {
                    sendErrorTheme(player, "Usage: %s", "/tport search biomePreset <preset>");
                }
            }));
            emptyDimension.addAction(emptyBiomeBiome);
            
            Search.Searchers.addSearcher((searchMode, query, player) -> {
                ArrayList<ItemStack> list = new ArrayList<>();
                for (String uuid : GettingFiles.getFile("TPortData").getKeys("tport")) {
                    for (TPort tport : TPortManager.getTPortList(UUID.fromString(uuid))) {
                        //noinspection ConstantConditions, command checks if available
                        if (BiomeTP.BiomeTPPresets.getPreset(query).getBiomes().contains(tport.getBiome())) {
                            list.add(TPortInventories.toTPortItem(tport, player, true));
                        }
                    }
                }
                return list;
            }, emptyDimension);
        } //biomePreset
        
        {
            EmptyCommand emptyDimensionDimension = new EmptyCommand();
            emptyDimensionDimension.setCommandName("dimension", ArgumentType.REQUIRED);
            emptyDimensionDimension.setCommandDescription(textComponent("This command is used to search TPorts by their dimension", infoColor));
            
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
                        sendErrorTheme(player, "Environment %s does not exist", args[2].toUpperCase());
                    }
                } else {
                    sendErrorTheme(player, "Usage: %s", "/tport search dimension <dimension>");
                }
            }));
            emptyDimension.addAction(emptyDimensionDimension);
            
            Search.Searchers.addSearcher((searchMode, query, player) -> {
                ArrayList<ItemStack> list = new ArrayList<>();
                for (String uuid : GettingFiles.getFile("TPortData").getKeys("tport")) {
                    for (TPort tport : TPortManager.getTPortList(UUID.fromString(uuid))) {
                        if (tport.getDimension().name().equalsIgnoreCase(query)) {
                            list.add(TPortInventories.toTPortItem(tport, player, true));
                        }
                    }
                }
                return list;
            }, emptyDimension);
        } //dimension
        
        {
            EmptyCommand emptyTagTag = new EmptyCommand();
            emptyTagTag.setCommandName("tag", ArgumentType.REQUIRED);
            emptyTagTag.setCommandDescription(textComponent("This command is used to search TPorts by their tag", infoColor));
            
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
                        sendErrorTheme(player, "Tag %s does not exist", args[2]);
                        return;
                    }
                    
                    TPortInventories.openSearchGUI(player, 0, null, emptyTag.getCommandName(), tag);
                } else {
                    sendErrorTheme(player, "Usage: %s", "/tport search tag <tag>");
                }
            }));
            emptyTag.addAction(emptyTagTag);
            
            Search.Searchers.addSearcher((searchMode, query, player) -> {
                ArrayList<ItemStack> list = new ArrayList<>();
                for (String uuid : GettingFiles.getFile("TPortData").getKeys("tport")) {
                    for (TPort tport : TPortManager.getTPortList(UUID.fromString(uuid))) {
                        if (tport.getTags().contains(query)) {
                            list.add(TPortInventories.toTPortItem(tport, player, true));
                        }
                    }
                }
                return list;
            }, emptyTag);
        } //tag
    }
    
    private void registerSorters() {
        Sort.addSorter("alphabet", (player) -> {
            Files tportData = GettingFiles.getFile("TPortData");
            ArrayList<String> playerList = new ArrayList<>(tportData.getKeys("tport"));
            return playerList.stream().map(playerUUID -> getOrDefault(getHead(UUID.fromString(playerUUID), player), new ItemStack(Material.AIR))).sorted((item1, item2) -> {
                //noinspection ConstantConditions
                return item1.getItemMeta().getDisplayName().compareToIgnoreCase(item2.getItemMeta().getDisplayName());
            }).collect(Collectors.toList());
            
        }, new Message(textComponent("This sorter sorts all players alphabetically according to their name", infoColor)));
        
        Sort.addSorter("oldest", (player) -> {
            Files tportData = GettingFiles.getFile("TPortData");
            ArrayList<String> playerList = new ArrayList<>(tportData.getKeys("tport"));
            return playerList.stream().map(playerUUID -> getOrDefault(getHead(UUID.fromString(playerUUID), player), new ItemStack(Material.AIR))).collect(Collectors.toList());
        }, new Message(textComponent("This sorter sorts the oldest players first", infoColor)));
        
        Sort.addSorter("newest", (player) -> {
            Files tportData = GettingFiles.getFile("TPortData");
            ArrayList<String> playerList = new ArrayList<>(tportData.getKeys("tport"));
            Collections.reverse(playerList);
            return playerList.stream().map(playerUUID -> getOrDefault(getHead(UUID.fromString(playerUUID), player), new ItemStack(Material.AIR))).collect(Collectors.toList());
        }, new Message(textComponent("This sorter sorts the newest players first", infoColor)));
    }
    
    private void registerSearchAreas() {
        {
            EmptyCommand emptyWorldborder = new EmptyCommand() {
                @Override
                public String getName(String argument) {
                    return getCommandName();
                }
            };
            emptyWorldborder.setCommandName("worldborder", ArgumentType.FIXED);
            emptyWorldborder.setRunnable(((args, player) -> {
//                SearchArea.SearchAreaHandler.getArea("worldborder").get(player);
            }));
            
            SearchAreaHandler.addAreaHandler(((player) -> {
                World world = player.getWorld();
                Location center = world.getWorldBorder().getCenter();
                int size = (int) (world.getWorldBorder().getSize() / 2);
                
                Polygon polygon = new Polygon();
                
                polygon.addPoint(center.getBlockX() - size, center.getBlockZ() + size);
                polygon.addPoint(center.getBlockX() + size, center.getBlockZ() + size);
                polygon.addPoint(center.getBlockX() + size, center.getBlockZ() - size);
                polygon.addPoint(center.getBlockX() - size, center.getBlockZ() - size);
                
                return polygon;
            }), emptyWorldborder);
        } //world border
        
        {
            EmptyCommand emptyPermission = new EmptyCommand() {
                @Override
                public String getName(String argument) {
                    return getCommandName();
                }
            };
            emptyPermission.setCommandName("permission", ArgumentType.FIXED);
            emptyPermission.setRunnable(((args, player) -> {
//                SearchArea.SearchAreaHandler.getArea("worldborder").get(player);
            }));
            
            SearchAreaHandler.addAreaHandler(((player) -> {
                World world = player.getWorld();
                String worldName = world.getName();
                
                label:
                if (player.getEffectivePermissions().stream().anyMatch(p -> p.getPermission().equalsIgnoreCase("TPort.searchArea.subType.polygon"))) {
                    HashMap<Integer, Pair<Integer, Integer>> prePolygon = new HashMap<>();
                    
                    for (PermissionAttachmentInfo permission : player.getEffectivePermissions()) {
                        if (permission.getPermission().toLowerCase().matches("(?i)tport\\.searcharea\\.data\\." + worldName + "\\.\\d+\\.x\\.[-+]?\\d+")) {
                            int corner = Integer.parseInt(permission.getPermission().substring(23 + worldName.length()).split("\\.x")[0]);
                            int x = Integer.parseInt(permission.getPermission().split("(?i)tport\\.searcharea\\.data\\." + worldName + "\\.\\d+\\.x\\.")[1]);
                            
                            if (prePolygon.containsKey(corner)) {
                                Pair<Integer, Integer> preCorner = prePolygon.get(corner);
                                preCorner.setLeft(x);
                                prePolygon.put(corner, preCorner);
                            } else {
                                prePolygon.put(corner, new Pair<>(x, null));
                            }
                        }
                        if (permission.getPermission().toLowerCase().matches("(?i)tport\\.searcharea\\.data\\." + worldName + "\\.\\d+\\.z\\.[-+]?\\d+")) {
                            int corner = Integer.parseInt(permission.getPermission().substring(23 + worldName.length()).split("\\.z")[0]);
                            int z = Integer.parseInt(permission.getPermission().split("(?i)tport\\.searcharea\\.data\\." + worldName + "\\.\\d+\\.z\\.")[1]);
                            
                            if (prePolygon.containsKey(corner)) {
                                Pair<Integer, Integer> preCorner = prePolygon.get(corner);
                                preCorner.setRight(z);
                                prePolygon.put(corner, preCorner);
                            } else {
                                prePolygon.put(corner, new Pair<>(null, z));
                            }
                        }
                    }
                    
                    Polygon polygon = new Polygon();
                    for (int corner = 1; corner <= prePolygon.size(); corner++) {
                        if (prePolygon.containsKey(corner)) {
                            Pair<Integer, Integer> loc = prePolygon.get(corner);
                            if (loc.getLeft() != null && loc.getRight() != null) {
                                polygon.addPoint(loc.getLeft(), loc.getRight());
                            } else {
                                //error, polygon point not complete
                                break label;
                            }
                        } else {
                            //error, polygon corners not complete
                            break label;
                        }
                    }
                    return polygon;
                }
                
                Location center = world.getWorldBorder().getCenter();
                int size = (int) (world.getWorldBorder().getSize() / 2);
                Polygon polygon = new Polygon();
                
                polygon.addPoint(center.getBlockX() - size, center.getBlockZ() + size);
                polygon.addPoint(center.getBlockX() + size, center.getBlockZ() + size);
                polygon.addPoint(center.getBlockX() + size, center.getBlockZ() - size);
                polygon.addPoint(center.getBlockX() - size, center.getBlockZ() - size);
                
                return polygon;
            }), emptyPermission);
        } //permission
        
        {
            EmptyCommand emptyWorldCornerSetXY = new EmptyCommand();
            emptyWorldCornerSetXY.setCommandName("y", ArgumentType.REQUIRED);
            emptyWorldCornerSetXY.setLinked();
            EmptyCommand emptyWorldCornerSetX = new EmptyCommand();
            emptyWorldCornerSetX.setCommandName("x", ArgumentType.REQUIRED);
            emptyWorldCornerSetX.addAction(emptyWorldCornerSetXY);
            emptyWorldCornerSetX.setLinked();
            EmptyCommand emptyWorldCornerSet = new EmptyCommand();
            emptyWorldCornerSet.setCommandName("set", ArgumentType.OPTIONAL);
            
            emptyWorldCornerSet.addAction(emptyWorldCornerSetX);
            EmptyCommand emptyWorldCornerGet = new EmptyCommand();
            emptyWorldCornerGet.setCommandName("get", ArgumentType.OPTIONAL);
            EmptyCommand emptyWorldCornerRemove = new EmptyCommand();
            emptyWorldCornerRemove.setCommandName("remove", ArgumentType.OPTIONAL);
            
            EmptyCommand emptyWorldCorner = new EmptyCommand();
            emptyWorldCorner.setCommandName("corner", ArgumentType.REQUIRED);
            emptyWorldCorner.addAction(emptyWorldCornerSet);
            emptyWorldCorner.addAction(emptyWorldCornerGet);
            emptyWorldCorner.addAction(emptyWorldCornerRemove);
            
            EmptyCommand emptyWorld = new EmptyCommand();
            emptyWorld.setCommandName("world", ArgumentType.REQUIRED);
            emptyWorld.addAction(emptyWorldCorner);
            
            EmptyCommand emptyPolygon = new EmptyCommand() {
                @Override
                public String getName(String argument) {
                    return getCommandName();
                }
            };
            emptyPolygon.setCommandName("polygon", ArgumentType.FIXED);
            emptyPolygon.addAction(emptyWorld);
            
            SearchAreaHandler.addAreaHandler(((player) -> { //todo
                Polygon polygon = new Polygon();
                polygon.addPoint(2999808, -9000448);
                polygon.addPoint(9000447, -9000448);
                polygon.addPoint(9000447, 9000447);
                polygon.addPoint(2999808, 9000447);
                return polygon;
            }), emptyPolygon);
        } //polygon
    }
}
