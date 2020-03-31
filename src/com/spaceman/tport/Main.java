package com.spaceman.tport;

import com.spaceman.tport.colorFormatter.ColorTheme;
import com.spaceman.tport.commands.TPortCommand;
import com.spaceman.tport.commands.tport.BiomeTP;
import com.spaceman.tport.commands.tport.Redirect;
import com.spaceman.tport.commands.tport.Reload;
import com.spaceman.tport.commands.tport.backup.Auto;
import com.spaceman.tport.events.*;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import com.spaceman.tport.tpEvents.ParticleAnimation;
import com.spaceman.tport.tpEvents.TPEManager;
import com.spaceman.tport.tpEvents.TPRestriction;
import com.spaceman.tport.tpEvents.animations.ExplosionAnimation;
import com.spaceman.tport.tpEvents.animations.SimpleAnimation;
import com.spaceman.tport.tpEvents.restrictions.NoneRestriction;
import com.spaceman.tport.tpEvents.restrictions.WalkRestriction;
import com.spaceman.tport.tport.TPort;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.spaceman.tport.colorFormatter.ColorTheme.sendErrorTheme;

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
    
    public static <I, J> HashMap<I, J> asMap(Pair<I, J>... pairs) {
        HashMap<I, J> map = new HashMap<>();
        for (Pair<I, J> pair : pairs) {
            map.put(pair.getLeft(), pair.getRight());
        }
        return map;
    }
    
    public static ArrayList<ItemStack> giveItems(Player player, ItemStack... items) {
        return giveItems(player, Arrays.asList(items));
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
         * changelog 1.15.4 update:
         * added
         *  /tport biomeTP searchTries [tries]
         *  /tport log logSize [size]
         *  /tport public listSize [size]
         * changed/added
         *  /tport featureTP
         *  /tport featureTP search <feature> [mode]
         *  /tport featureTP mode [mode]
         *  shortcut from FeatureTP GUI to '/tport featureTP mode <mode>'
         *  when using /tport biomeTP whitelist|blacklist <biome...> it will give you a confirmation that its searching before the lag-spike
         * fixed minor bugs
         * added /back to TPort Redirects
         * updated the permission 'TPort.admin' from command '/tport redirect <redirect> [state]' to 'TPort.admin.redirect'
         */
        
        /*
         * todo
         * remove converting methods
         * add QuickGuide command
         *
         * create tutorial for creating your own Particle Animations and TP Restrictions
         * */
        
        ConfigurationSerialization.registerClass(ColorTheme.class, "ColorTheme");
        ConfigurationSerialization.registerClass(TPort.class, "TPort");
        ConfigurationSerialization.registerClass(Pair.class, "Pair");
        
        ParticleAnimation.registerAnimation(SimpleAnimation::new);
        ParticleAnimation.registerAnimation(ExplosionAnimation::new);
        TPRestriction.registerRestriction(NoneRestriction::new);
        TPRestriction.registerRestriction(WalkRestriction::new);
        
        registerBiomeTPPresets();
        
        Reload.reloadTPort();
        
        TPEManager.loadTPE(GettingFiles.getFile("TPortConfig"));
        
        ColorTheme.loadThemes(GettingFiles.getFile("TPortConfig"));
    
        Redirect.Redirects.loadRedirects();
    
        Glow.registerGlow();
        
        new TPortCommand();
        
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new InventoryClick(), this);
        pm.registerEvents(new TeleporterEvents(), this);
        pm.registerEvents(new JoinEvent(this), this);
        pm.registerEvents(new RespawnEvent(), this);
        pm.registerEvents(new OfflineLocationManager(), this);
        pm.registerEvents(new CommandEvent(), this);
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            JoinEvent.setData(this, player);
        }
        
        Files tportConfig = GettingFiles.getFile("TPortConfig");
        if (!tportConfig.getConfig().contains("biomeTP.searches")) {
            tportConfig.getConfig().set("biomeTP.searches", 100);
            tportConfig.saveConfig();
        }
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

//        BiomeTP.BiomeTPPresets.registerPreset("name", Arrays.asList(
//                null),
//                true, Material.STONE);
    }
    
    @Override
    public void onDisable() {
        TPEManager.saveTPE(GettingFiles.getFile("TPortConfig"));
        ColorTheme.saveThemes(GettingFiles.getFile("TPortConfig"));
        Redirect.Redirects.saveRedirects();
        Auto.save();
    }
}
