package com.spaceman.tport;

import com.spaceman.tport.adapters.TPortAdapter;
import com.spaceman.tport.commands.TPortCommand;
import com.spaceman.tport.commands.tport.*;
import com.spaceman.tport.commands.tport.Tag;
import com.spaceman.tport.commands.tport.backup.Auto;
import com.spaceman.tport.commands.tport.resourcePack.ResolutionCommand;
import com.spaceman.tport.events.*;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fancyMessage.colorTheme.MultiColor;
import com.spaceman.tport.fancyMessage.inventories.FancyClickEvent;
import com.spaceman.tport.fancyMessage.inventories.FancyInventory;
import com.spaceman.tport.fancyMessage.inventories.keyboard.QuickType;
import com.spaceman.tport.history.HistoryEvents;
import com.spaceman.tport.history.TeleportHistory;
import com.spaceman.tport.inventories.ItemFactory;
import com.spaceman.tport.inventories.TPortInventories;
import com.spaceman.tport.metrics.BiomeSearchCounter;
import com.spaceman.tport.metrics.CommandCounter;
import com.spaceman.tport.metrics.FeatureSearchCounter;
import com.spaceman.tport.metrics.Metrics;
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
import com.spaceman.tport.waypoint.WaypointManager;
import com.spaceman.tport.webMaps.BlueMapHandler;
import com.spaceman.tport.webMaps.DynmapHandler;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.awt.*;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendInfoTranslation;
import static com.spaceman.tport.fileHander.Files.tportConfig;
import static com.spaceman.tport.fileHander.Files.tportData;
import static com.spaceman.tport.inventories.ItemFactory.TPortItemAttributes.ADD_OWNER;
import static com.spaceman.tport.inventories.ItemFactory.TPortItemAttributes.CLICK_TO_OPEN;

public class Main extends JavaPlugin {
    
    public TPortAdapter adapter = null;
    
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
            if (pair != null) map.put(pair.getLeft(), pair.getRight());
        }
        return map;
    }
    
    public static boolean containsSpecialCharacter(String s) {
        if (s == null || s.isBlank()) {
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
    public static String[] supportedVersions = new String[]{};
    private void setSupportedVersions() {
        InputStream is = this.getClassLoader().getResourceAsStream("plugin.yml");
        if (is == null) {
            this.getLogger().log(Level.INFO, "Could not update the supported versions list");
            return;
        }
        Reader r = new InputStreamReader(is);
        
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(r);
        supportedVersions = yaml.getStringList("supportedVersions").toArray(new String[]{});
    }
    
    @Override
    public void onEnable() {
        
        /*
         * //todo
         *
         * add Defaults.yml
         * In here are all defaults stored, so that admins can change the defaults
         *
         * Feature: waypoints
         * /tport waypoints type [type]
         *   - PublicTP (shows all PublicTP TPorts)
         *   - public (shows all public TPorts, using private state: open, online)
         *   - canTP (shows all TPorts you can teleport to)
         *   - own (shows only own TPorts)
         * / tport edit <TPort> waypoint show [state]
         * / tport edit <TPort> waypoint icon [icon]
         * / tport edit <TPort> waypoint color [chat color]
         * / tport edit <TPort> waypoint color [hex color]
         *
         *
         * /tport location ~ ~ ~
         * /tport location 0 0 0
         * /tport location ~ 0 ~
         * /tport location ^ ^ ^
         *
         * /tport admin
         * /tport admin add <player...>
         * /tport admin remove <player...>
         * /tport admin list
         * let admins teleport to any TPort (even without consent)
         *
         * /tport features display_disabled_features
         * when disabled, disabled features wont show in inventories (or if player has no permission)
         *
         * settings_features_permissions
         * settings_features_permissions_grayed
         * settings_features_companion_tp
         * settings_features_companion_tp_grayed
         * settings_features_tport_takes_item
         * settings_features_tport_takes_item_grayed
         * settings_search_owned_tports
         *
         * disabling biomes per player now is only by permissions
         * add server wide biome selection
         * add per player biome selection
         * Add configurable mappings from Biome->Material
         * do this also for FeatureTP
         *
         * make markdown reader
         * use readme.md and quickEdit.md for in-game wiki
         * extend this wiki type
         *
         * /TPort tipOfTheDay state [state]
         * /TPort tipOfTheDay next
         * When enabled: when a player logs on the server, show a random tip
         *
         * add preview state: whitelist
         *
         * create a friend list. use this friend list as option for all whitelists
         * /tport friends [add|remove|list]
         *
         * /tport party
         * If a player teleports, every other player in that party also teleports
         *
         * unify EmptyCommand names
         *
         * add POI
         * bring featureTP and biomeTP closer together, feature/usage wise
         *
         * add swear word filter to TPort name and description
         *
         * /tport bed
         *
         * Feature: disableRandomWorldSearch
         *
         * make certain command terminal friendly
         *
         * Use World.locateNearestStructure as fallback for FeatureTP
         *
         * redo the teleporter command
         * - use on items
         * - use om items in itemFrame
         * - use on sign
         *
         * inventories:
         *  - particle animation
         *  - language
         *  - biomeTP
         *  - featureTP
         *  - dynmap
         *  - blueMap
         *  - delay
         *  - restriction
         *  - requests
         *
         * unify how get/set commands handle permissions (logSize: tport.log.logSize, Delay: tport.delay.get.all)
         *
         * Add minimalTPortBetweenTPortDistance. This helps owners protect their home, so no other can add a TPort and bypass the private state/range
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
         *
         * create payment system (with elytra pay with fire rockets)
         * earn credits when players teleport to one of your TPorts. use these credits to teleport to other TPorts.
         * */
        
        this.getLogger().log(Level.INFO, "TPort has a Discord server, for any questions/more go to: " + discordLink);
        setSupportedVersions();
        Version.checkForLatestVersion();
        
        Adapter.registerAdapter("adaptive", "com.spaceman.tport.adapters.AdaptiveAdapter");
        Adapter.registerAdapter("1.18.2", "com.spaceman.tport.adapters.V1_18_2_Adapter");
        Adapter.registerAdapter("1.19.4", "com.spaceman.tport.adapters.V1_19_4_Adapter");
        Adapter.registerAdapter("1.20.4", "com.spaceman.tport.adapters.V1_20_4_Adapter");
        Adapter.registerAdapter("1.20.5", "com.spaceman.tport.adapters.V1_20_6_Adapter");
        Adapter.registerAdapter("1.20.6", "com.spaceman.tport.adapters.V1_20_6_Adapter");
        Adapter.registerAdapter("1.21", "com.spaceman.tport.adapters.V1_21_Adapter");
        Adapter.registerAdapter("1.21.1", "com.spaceman.tport.adapters.V1_21_Adapter");
        Adapter.registerAdapter("1.21.3", "com.spaceman.tport.adapters.V1_21_3_Adapter");
        Adapter.registerAdapter("1.21.4", "com.spaceman.tport.adapters.V1_21_4_Adapter");
        Adapter.registerAdapter("1.21.5", "com.spaceman.tport.adapters.V1_21_5_Adapter");
        Adapter.registerAdapter("1.21.6", "com.spaceman.tport.adapters.V1_21_6_Adapter");
        Adapter.registerAdapter("1.21.7", "com.spaceman.tport.adapters.V1_21_6_Adapter");
        Adapter.registerAdapter("1.21.8", "com.spaceman.tport.adapters.V1_21_6_Adapter");
        Adapter.registerAdapter("1.21.9", "com.spaceman.tport.adapters.V1_21_10_Adapter");
        Adapter.registerAdapter("1.21.10", "com.spaceman.tport.adapters.V1_21_10_Adapter");
        
        ConfigurationSerialization.registerClass(ColorTheme.class, "ColorTheme");
        ConfigurationSerialization.registerClass(TPort.class, "TPort");
        ConfigurationSerialization.registerClass(Pair.class, "Pair");
        ConfigurationSerialization.registerClass(MultiColor.class, "MultiColor");
        ConfigurationSerialization.registerClass(TPort.LogEntry.class, "LogEntry");
        
        ParticleAnimation.registerAnimation(SimpleAnimation::new);
        ParticleAnimation.registerAnimation(ExplosionAnimation::new);
        TPRestriction.registerRestriction(NoneRestriction::new);
        TPRestriction.registerRestriction(WalkRestriction::new);
        TPRestriction.registerRestriction(InteractRestriction::new);
        TPRestriction.registerRestriction(DoSneakRestriction::new);
        
        Reload.reloadTPort();
        
        TeleportHistory.registerPluginFilterModel(this, TPortInventories.history_filter_plugin_tport_model);
        
        SafetyCheck.setSafetyCheck((feet, clearFeet) -> {
            
            Location head = feet.clone().add(0, 1, 0);
            Location ground = feet.clone().add(0, -1, 0);
            
            List<Material> damageableMaterials = Arrays.asList //todo add other damageable blocks
                    (Material.LAVA, Material.CAMPFIRE, Material.SOUL_CAMPFIRE, Material.FIRE, Material.SOUL_FIRE, Material.MAGMA_BLOCK);
            
            return !damageableMaterials.contains(ground.getBlock().getType()) &&
                    !damageableMaterials.contains(head.getBlock().getType()) &&
                    !damageableMaterials.contains(feet.getBlock().getType()) &&
                    (ground.getBlock().getType().isSolid() || ground.getBlock().getType().equals(Material.WATER)) &&
                    !head.getBlock().getType().isSolid() &&
                    (!clearFeet || !feet.getBlock().getType().isSolid());
        });
        
        Features.convert();
        
        ResolutionCommand.Resolution.registerResourcePackResolution("x16", "https://github.com/JasperBouwman/TPort/releases/download/TPort-" +
                Main.getInstance().getDescription().getVersion() + "/TPort_16x_dark.zip");
        ResolutionCommand.Resolution.registerResourcePackResolution("x32", "https://github.com/JasperBouwman/TPort/releases/download/TPort-" +
                Main.getInstance().getDescription().getVersion() + "/TPort_32x_dark.zip");
        ResolutionCommand.Resolution.registerResourcePackResolution("x16_light", "https://github.com/JasperBouwman/TPort/releases/download/TPort-" +
                Main.getInstance().getDescription().getVersion() + "/TPort_16x_light.zip");
        ResolutionCommand.Resolution.registerResourcePackResolution("x32_light", "https://github.com/JasperBouwman/TPort/releases/download/TPort-" +
                Main.getInstance().getDescription().getVersion() + "/TPort_32x_light.zip");
        ResolutionCommand.Resolution.registerResourcePackResolution("custom", null);
        
        TPortCommand.getInstance().register();
        
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new TeleporterEvents(), this);
        pm.registerEvents(new JoinEvent(), this);
        pm.registerEvents(new RespawnEvent(), this);
        pm.registerEvents(new CommandEvent(), this);
        pm.registerEvents(new FancyClickEvent(), this);
        pm.registerEvents(new WaypointManager(), this);
        HistoryEvents.load();
        
        // generate waypoint register
        for (String uuid : tportData.getKeys("tport")) {
            for (TPort tport : TPortManager.getTPortList(UUID.fromString(uuid))) {
                WaypointManager.registerTPort(tport);
            }
        }
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            JoinEvent.setData(player);
        }
        
        if (tportConfig.getConfig().contains("tport.onlineState")) {
            if (tportConfig.getConfig().getBoolean("tport.onlineState") != Bukkit.getOnlineMode()) {
                
                if (Bukkit.getOnlineMode()) {
                    this.getLogger().log(Level.WARNING, "When TPort was installed, the server was in offline mode. By changing this mode TPort will have unexpected behaviour. Players will lose their settings/TPorts");
                } else {
                    this.getLogger().log(Level.WARNING, "When TPort was installed, the server was in online mode. By changing this mode TPort will have unexpected behaviour. Players will lose their settings/TPorts");
                }
                
            }
        } else {
            tportConfig.getConfig().set("tport.onlineState", Bukkit.getOnlineMode());
        }
        
        if (Features.Feature.Metrics.isEnabled()) {
            Main.getInstance().getLogger().log(Level.INFO, "Enabling metrics. Thank you for enabling metrics, powered by bStats. To view stats use '/tport metrics viewStats'");
            Metrics m = new Metrics(this, 8061);
            m.addCustomChart(new Metrics.AdvancedPie("command_usage", CommandCounter::getData));
            m.addCustomChart(new Metrics.AdvancedPie("biome_searches", BiomeSearchCounter::getData));
            m.addCustomChart(new Metrics.AdvancedPie("feature_searches", FeatureSearchCounter::getData));
            m.addCustomChart(new Metrics.SimplePie("tport_version", () -> this.getDescription().getVersion()));
            
            m.addCustomChart(new Metrics.AdvancedPie("features", () -> {
                HashMap<String, Integer> map = new HashMap<>();
                for (Features.Feature feature : Features.Feature.values()) {
                    if (feature == Features.Feature.Dynmap) {
                        if (DynmapHandler.isEnabled()) {
                            map.put(feature.name(), 1);
                        } else {
                            map.put(feature.name(), 0);
                        }
                    } else if (feature == Features.Feature.BlueMap) {
                        if (BlueMapHandler.isEnabled()) {
                            map.put(feature.name(), 1);
                        } else {
                            map.put(feature.name(), 0);
                        }
                        
                    } else if (feature.isEnabled()) {
                        map.put(feature.name(), 1);
                    } else {
                        map.put(feature.name(), 0);
                    }
                }
                return map;
            }));
            
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
        try { BlueMapHandler.disable(); } catch (Throwable ignored) { }
        PreviewEvents.cancelAllPreviews();
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            QuickType.removeQuickTypeSignHandler(player);
            
            if (player.getOpenInventory().getTopInventory().getHolder() instanceof FancyInventory) {
                player.closeInventory();
                sendInfoTranslation(player, "tport.Main.inventoryCloseByReload");
            }
        }
    }
}
