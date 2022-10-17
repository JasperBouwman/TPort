package com.spaceman.tport;

import com.spaceman.tport.commandHandler.ArgumentType;
import com.spaceman.tport.commandHandler.EmptyCommand;
import com.spaceman.tport.commands.TPortCommand;
import com.spaceman.tport.commands.tport.Tag;
import com.spaceman.tport.commands.tport.*;
import com.spaceman.tport.commands.tport.backup.Auto;
import com.spaceman.tport.commands.tport.biomeTP.Accuracy;
import com.spaceman.tport.dynmap.DynmapHandler;
import com.spaceman.tport.events.*;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fancyMessage.colorTheme.MultiColor;
import com.spaceman.tport.fancyMessage.inventories.FancyClickEvent;
import com.spaceman.tport.fancyMessage.inventories.FancyInventory;
import com.spaceman.tport.metrics.BiomeSearchCounter;
import com.spaceman.tport.metrics.CommandCounter;
import com.spaceman.tport.metrics.FeatureSearchCounter;
import com.spaceman.tport.metrics.Metrics;
import com.spaceman.tport.permissions.PermissionHandler;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tpEvents.ParticleAnimation;
import com.spaceman.tport.tpEvents.TPEManager;
import com.spaceman.tport.tpEvents.TPRestriction;
import com.spaceman.tport.tpEvents.animations.ExplosionAnimation;
import com.spaceman.tport.tpEvents.animations.SimpleAnimation;
import com.spaceman.tport.tpEvents.restrictions.DoSneakRestriction;
import com.spaceman.tport.tpEvents.restrictions.InteractRestriction;
import com.spaceman.tport.tpEvents.restrictions.NoneRestriction;
import com.spaceman.tport.tpEvents.restrictions.WalkRestriction;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fileHander.Files.tportConfig;
import static com.spaceman.tport.fileHander.Files.tportData;

public class Main extends JavaPlugin {
    
    public static <O> O getOrDefault(@Nullable O object, O def) {
        return object == null ? def : object;
    }
    
    public static Object getOrDefaults(Object... objects) {
        for (Object o : objects) {
            if (o != null) {
                return o;
            }
        }
        return null;
    }
    
    public static Main getInstance() {
        return JavaPlugin.getPlugin(Main.class);
    }
    
    public static ArrayList<ItemStack> giveItems(Player player, List<ItemStack> items) {
        ArrayList<ItemStack> returnList = new ArrayList<>();
        for (ItemStack item : player.getInventory().addItem(items.toArray(new ItemStack[0])).values()) {
            player.getWorld().dropItem(player.getLocation(), item);
            returnList.add(item);
            sendErrorTranslation(player, "tport.main.dropItem", item.getType().name());
        }
        return returnList;
    }
    
    public static ArrayList<ItemStack> giveItems(Player player, ItemStack... items) {
        return giveItems(player, Arrays.asList(items));
    }
    
    @SafeVarargs
    public static <I, J> HashMap<I, J> asMap(Pair<I, J>... pairs) {
        HashMap<I, J> map = new HashMap<>();
        for (Pair<I, J> pair : pairs) {
            map.put(pair.getLeft(), pair.getRight());
        }
        return map;
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
    
    public static Rectangle getSearchArea(Player player) {
        World world = player.getWorld();
        Location center = world.getWorldBorder().getCenter();
        int size = (int) world.getWorldBorder().getSize();
        int halfSize = size / 2;
        
        return new Rectangle(center.getBlockX() - halfSize, center.getBlockZ() - halfSize, size, size);
    }
    
    public static Location getRandomLocation(Player player) {
        Random random = new Random();
        Rectangle searchArea = getSearchArea(player);
        return new Location(player.getWorld(), random.nextInt(searchArea.width) + searchArea.x, 0, random.nextInt(searchArea.height) + searchArea.y);
    }
    
    public static Location getClosestLocation(Player player) {
        Rectangle searchArea = getSearchArea(player);
        
        if (searchArea.contains(player.getLocation().getX(), player.getLocation().getZ())) {
            return player.getLocation();
        } else {
            int currentX = player.getLocation().getBlockX();
            int currentZ = player.getLocation().getBlockZ();
            int newX = Math.min(Math.max(currentX, searchArea.x), searchArea.x + searchArea.width);
            int newZ = Math.min(Math.max(currentZ, searchArea.y), searchArea.y + searchArea.height);
            
            return new Location(player.getLocation().getWorld(), newX, player.getLocation().getY(), newZ);
        }
    }
    
    public static Boolean toBoolean(String arg) {
        return toBoolean(arg, null);
    }
    public static Boolean toBoolean(String arg, @Nullable Boolean def) {
        boolean t = isTrue(arg), f = isFalse(arg);
        return (!t && !f) ? def : Boolean.valueOf(t);
    }
    
    public static boolean isTrue(String arg) {
        if (arg == null) return false;
        arg = arg.toLowerCase();
        return arg.equals("true") || arg.equals("yes") || arg.equals("y") || arg.equals("enable");
    }
    
    public static boolean isFalse(String arg) {
        if (arg == null) return false;
        arg = arg.toLowerCase();
        return arg.equals("false") || arg.equals("no") || arg.equals("n") || arg.equals("disable");
    }
    
    public static final String discordLink = "https://discord.gg/tq5RTmSbHU";
    public void onEnable() {
        
        /*
         * changelog 1.19.3 update:
         *
         * added a confirmation when using /tport resourcePack resolution <resolution>
         *
         * fixed command /tport public open <TPort name> [safetyCheck]
         * fixed error with command /tport open
         * fixed command redirection of /locate <biome|structure>
         
         * the /tport setHome command has moved into the /tport home command:
         *  /tport home                             teleport to your home
         *  /tport home <safetyCheck>               teleport to your home with selected safety check
         *  /tport home get                         gives the current home
         *  /tport home set <player> <TPort name>   set the selected TPort as your home
         *
         * Dynmap TPort icons now show more information about the TPort
         *
         */
        
        /*
         * //todo
         *
         * unify EmptyCommand names
         *
         * add POI
         *
         * /tport history
         * /tport history back
         * /tport back -> tport history back ?
         *
         * redo the teleporter command
         * - use on items
         * - use om items in itemFrame
         * - use on sign
         *
         * unify getPlayer in commands
         *
         * for the usage of the tab complete the player now need the permission to run the command
         *
         * /tport sort [popularity]
         *  create popularity system for /tport sort popularity
         *
         * update /tport version compatible Bukkit versions
         * create tutorial for creating your own Particle Animations and TP Restrictions
         * */
        
        this.getLogger().log(Level.INFO, "TPort has now a Discord server, for any questions/more go to: " + discordLink);
        
        ConfigurationSerialization.registerClass(ColorTheme.class, "ColorTheme");
        ConfigurationSerialization.registerClass(TPort.class, "TPort");
        ConfigurationSerialization.registerClass(Pair.class, "Pair");
        ConfigurationSerialization.registerClass(MultiColor.class, "MultiColor");
    
        ParticleAnimation.registerAnimation(SimpleAnimation::new);
        ParticleAnimation.registerAnimation(ExplosionAnimation::new);
        TPRestriction.registerRestriction(NoneRestriction::new);
        TPRestriction.registerRestriction(WalkRestriction::new);
        TPRestriction.registerRestriction(InteractRestriction::new);
        TPRestriction.registerRestriction(DoSneakRestriction::new);
        
        Reload.reloadTPort();
        
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
        
        Glow.registerGlow();
        
        Features.convert();
        
        TPortCommand.getInstance().register();
        
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new TeleporterEvents(), this);
        pm.registerEvents(new JoinEvent(), this);
        pm.registerEvents(new RespawnEvent(), this);
        pm.registerEvents(new CommandEvent(), this);
        pm.registerEvents(new FancyClickEvent(), this);
//        pm.registerEvents(new TeleportEvents(), this);
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            JoinEvent.setData(player);
        }
        
        registerSearchers();
        registerSorters();
        registerBiomeTPAccuracies();
        
        if (Features.Feature.Metrics.isEnabled()) {
            Main.getInstance().getLogger().log(Level.INFO, "Enabling metrics. Thank you for enabling metrics, powered by bStats. To view stats use '/tport metrics viewStats'");
            Metrics m = new Metrics(this, 8061);
            m.addCustomChart(new Metrics.AdvancedPie("command_usage", CommandCounter::getData));
            m.addCustomChart(new Metrics.AdvancedPie("biome_searches", BiomeSearchCounter::getData));
            m.addCustomChart(new Metrics.AdvancedPie("feature_searches", FeatureSearchCounter::getData));
            m.addCustomChart(new Metrics.SimplePie("tport_version", () -> this.getDescription().getVersion()));
            m.addCustomChart(new Metrics.SimplePie("public_enabled", () -> (Features.Feature.PublicTP.isEnabled() ? "True" : "False")));
            m.addCustomChart(new Metrics.SimplePie("permissions_enabled", () -> (PermissionHandler.isPermissionEnabled() ? "True" : "False")));
            m.addCustomChart(new Metrics.SimplePie("dynmap_enabled", () -> (DynmapHandler.isEnabled() ? "True" : "False")));
        }
    }
    
    @Override
    public void onDisable() {
        TPEManager.saveTPE(tportConfig);
        ColorTheme.saveThemes(tportConfig);
        Redirect.Redirects.saveRedirects();
        Tag.saveTags();
        Auto.save();
        DynmapHandler.disable();
        PreviewEvents.cancelAllPreviews();
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getOpenInventory().getTopInventory().getHolder() instanceof FancyInventory) {
                player.closeInventory();
                sendInfoTranslation(player, "tport.Main.inventoryCloseByReload");
            }
        }
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
                "FOREST", "WINDSWEPT_FOREST", "DARK_FOREST", "SAVANNA", "SAVANNA_PLATEAU", "WINDSWEPT_SAVANNA", "JUNGLE", "SPARSE_JUNGLE", "BIRCH_FOREST", "OLD_GROWTH_BIRCH_FOREST", "TAIGA", "OLD_GROWTH_SPRUCE_TAIGA", "OLD_GROWTH_PINE_TAIGA"),
                true, Material.OAK_WOOD);
        
//        BiomeTP.BiomeTPPresets.registerPreset("name", Arrays.asList(
//                "biome names"),
//                true, Material.STONE);
    }
    
    private void registerSearchers() {
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
                            list.add(TPortInventories.toTPortItem(tport, player, true));
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
                            list.add(TPortInventories.toTPortItem(tport, player, true));
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
            ArrayList<String> playerList = new ArrayList<>(tportData.getKeys("tport"));
            return playerList.stream().map(playerUUID -> getOrDefault(getHead(UUID.fromString(playerUUID), player), new ItemStack(Material.AIR))).sorted((item1, item2) -> {
                //noinspection ConstantConditions
                return item1.getItemMeta().getDisplayName().compareToIgnoreCase(item2.getItemMeta().getDisplayName());
            }).collect(Collectors.toList());
            
        }, formatInfoTranslation("tport.main.sorter.alphabet.description"));
        
        Sort.addSorter("oldest", (player) -> {
            ArrayList<String> playerList = new ArrayList<>(tportData.getKeys("tport"));
            return playerList.stream().map(playerUUID -> getOrDefault(getHead(UUID.fromString(playerUUID), player), new ItemStack(Material.AIR))).collect(Collectors.toList());
        }, formatInfoTranslation("tport.main.sorter.oldest.description"));
        
        Sort.addSorter("newest", (player) -> {
            ArrayList<String> playerList = new ArrayList<>(tportData.getKeys("tport"));
            Collections.reverse(playerList);
            return playerList.stream().map(playerUUID -> getOrDefault(getHead(UUID.fromString(playerUUID), player), new ItemStack(Material.AIR))).collect(Collectors.toList());
        }, formatInfoTranslation("tport.main.sorter.newest.description"));
    }
    
    private void registerBiomeTPAccuracies() {
        Accuracy.createAccuracy("default", 6400, 8, Arrays.asList(200, 0));
        Accuracy.createAccuracy("fine", 4800, 6, Arrays.asList(200, 100, 0, -30));
        Accuracy.createAccuracy("fast", 6400, 8, List.of(100));
    }
}
