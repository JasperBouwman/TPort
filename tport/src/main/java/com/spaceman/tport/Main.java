package com.spaceman.tport;

import com.spaceman.tport.adapters.TPortAdapter;
import com.spaceman.tport.commands.TPortCommand;
import com.spaceman.tport.commands.tport.*;
import com.spaceman.tport.commands.tport.backup.Auto;
import com.spaceman.tport.commands.tport.resourcePack.ResolutionCommand;
import com.spaceman.tport.dynmap.DynmapHandler;
import com.spaceman.tport.events.*;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fancyMessage.colorTheme.MultiColor;
import com.spaceman.tport.fancyMessage.inventories.FancyClickEvent;
import com.spaceman.tport.fancyMessage.inventories.FancyInventory;
import com.spaceman.tport.fancyMessage.inventories.keyboard.QuickType;
import com.spaceman.tport.history.HistoryEvents;
import com.spaceman.tport.metrics.BiomeSearchCounter;
import com.spaceman.tport.metrics.CommandCounter;
import com.spaceman.tport.metrics.FeatureSearchCounter;
import com.spaceman.tport.metrics.Metrics;
import com.spaceman.tport.permissions.PermissionHandler;
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
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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
         * changelog 1.20.4 update:
         *
         * updated the resource pack format to 22
         *
         * Finished the Logging setting
         * Auto backups are working again
         *
         * Added Item Selection in the Quick Edit menu
         * Added whitelist clone to the Quick Edit menu
         *
         * Full update for Search
         *  - Added World search type. Search for TPorts that are in the given world
         *  - Added OwnedTPorts search type. Search for players owned TPorts
         *  - Search can now be run with inventories, use '/tport search' or find it in the settings screen
         *  - Added new search modes: not equals, ends with, not contains
         *
         * Added full adapter support
         *  - server admins now can select the best adapter for their server
         *  - The default is set to automatic, this chooses based upon the server version the best adapter
         *  - The end goal is that each Minecraft version has their own adapter
         *  - The adaptive adapter should mainly be used if your version does not exist (example: using TPort for a new Minecraft version that TPort does not yet have a adapter for)
         *  - If the selected adapter does not load the adaptive is used as a backup
         *  - command: /tport adapter [adapter]
         *  - permissions: tport.adapter or tport.admin for setting the adapter
         *  - selecting the adapter can also be done via the setting menu
         *
         * Added beta support for BlueMap
         *  - Works the same as the Dynmap support
         *  - to enable: /tport features blueMap state true, and make sure that BlueMap is successfully loaded into your server
         *  - For now only a toggle for showing state on the map is available using: /tport edit <TPort name> blueMap show [state]
         *
         * Added a safety check to PLTP
         *  - /tport PLTP tp <player> [safetyCheck]
         *  - when teleport needs to be requested, the check is preformed after the request is accepted
         * Another safety check is preformed for teleporting to a TPort
         *  - the owner of the TPort can move the TPort to a dangerous location in between the request and accepting
         *
         * More features for the Keyboard:
         *  - you can now delete a color
         *  - in the color selector, you can select a color from Minecraft (Chat Colors & Dye Colors)
         *    This built-in color selector is also used for creating your own color theme
         *
         * Renamed TPort private state 'prion' to PRIVATE_ONLINE
         *
         * New icons for the transfer system. Check them out in: settings, transfer window offered/offers filter and in the Quick Edit window
         *
         * Added a command to fully stop logging a TPort
         *  /tport log delete <TPort name>
         *  This removes all players from the logged players list, and sets the default log mode to NONE
         *
         * Added LookTP to the features list. Default value is on
         * Added EnsureUniqueUUID to the features list. Default value is off.
         *  - This feature was already in TPort, but its now changeable.
         *  - When enabled, TPort will look at all existing TPorts and check if the new UUID for the new TPort is truly unique.
         *  - The changes of it randomly creating a UUID that is already in use is animatronic low.
         *
         * When Permissions are disabled, they won't show anymore in the help page (/tport help <command>)
         *
         * Added '/tport language repair <language> [repair with] [dump]'.
         * When dump is set to true, it prints all repaired ID's in the console
         *
         * '/tport back <safetyCheck>' now uses the correct safetyCheck permissions.
         *  - old: (TPort.Back and TPort.safetyCheck.TPORT_PUBLIC) or TPort.basic
         *  - new: (TPort.Back and TPort.safetyCheck.TPORT_BACK) or TPort.basic
         *
         * fixed some minor bugs
         */
        
        /*
         * //todo
         *
         * settings_features_permissions
         * settings_features_permissions_grayed
         * settings_features_tport_takes_item
         * settings_features_tport_takes_item_grayed
         * settings_search_owned_tports
         *
         * disabling biomes per player now is only by permissions
         * add server wide biome selection
         * add per player biome selection
         * do this also for FeatureTP
         *
         * add preview state: whitelist
         *
         * create a friend list. use this friend list as option for all whitelists
         * /tport friends [add|remove|list]
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
         * /tport history
         * /tport history back
         * /tport back -> tport history back ?
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
        HistoryEvents.load();
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            JoinEvent.setData(player);
        }
        
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
            QuickType.removeQuickTypeSignHandler(player);
            
            if (player.getOpenInventory().getTopInventory().getHolder() instanceof FancyInventory) {
                player.closeInventory();
                sendInfoTranslation(player, "tport.Main.inventoryCloseByReload");
            }
        }
    }
}
