package com.spaceman.tport;

import com.spaceman.tport.colorFormatter.ColorTheme;
import com.spaceman.tport.commands.TPortCommand;
import com.spaceman.tport.commands.tport.Reload;
import com.spaceman.tport.commands.tport.backup.Auto;
import com.spaceman.tport.events.CompassEvents;
import com.spaceman.tport.events.DeathEvent;
import com.spaceman.tport.events.InventoryClick;
import com.spaceman.tport.events.JoinEvent;
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
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.util.*;

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
    
    public void onEnable() {
        
        /*
         * changelog 1.15 update:
         *
         * /tport edit <TPort> move <slot> can now also swap TPorts, in stead of only moving to an empty slot,
         *  when giving a TPort name as slot it swaps (so is now: /tport edit <TPort name> move <TPort name|slot>)
         * tab list of '/tport back' shows back location
         * you need to hold a compass to use the command /tport compass to turn it into a TPort Compass (no free items)
         * minor chat improvements
         * removed name duplication bug with renaming TPorts (did not check if name is already in use)
         * rename TPort lore to TPort description, '/tport edit <TPort name> lore' is now '/tport edit <TPort name> description'
         * added:
         *  /tport colorTheme
         *  /tport colorTheme set <theme>
         *  /tport colorTheme set <type> <color>
         *  /tport colorTheme get <type>
         *  /tport home
         *  /tport setHome <player> <TPort name>
         *  /tport edit <TPort name> range [range] (you have to be in your set range of that TPort so other players can teleport to it)
         *  /tport edit <TPort name> description get
         *  /tport edit <TPort name> whitelist clone <TPort name>
         *  public TPorts (right-click to select a Public TPort to swap)
         *  /tport public
         *  /tport public open <TPort name|page>
         *  /tport public move <TPort name> <slot|TPort name>
         *  /tport public add <TPort name>
         *  /tport public remove <TPort name>
         *  /tport public list [own|all]
         * added these commands (transfer) to give someone one of your TPorts
         *  /tport transfer offer <player> <TPort name>
         *  /tport transfer revoke <TPort name>
         *  /tport transfer accept <player> <TPort name>
         *  /tport transfer reject <player> <TPort name>
         *  /tport transfer list
         *  /tport version
         *  /tport log read <TPort name> [player]
         *  /tport log TimeZone [TimeZone]
         *  /tport log timeFormat [format...]
         *  /tport log clear <TPort name...>
         *  /tport log logData [TPort name] [player]
         *  /tport log add <TPort name> <player[:LogMode]...>
         *  /tport log remove <TPort name> <player...>
         *  /tport log default <TPort name> [default LogMode]
         *  /tport log notify <TPort name> [state]
         *  /tport backup save <name>
         *  /tport backup load <name>
         *  /tport backup auto [state|count]
         *  /tport PLTP state
         *  /tport PLTP state <state>
         *  /tport PLTP consent
         *  /tport PLTP consent <state>
         *  /tport PLTP accept [player...]
         *  /tport PLTP reject <player>
         *  /tport PLTP revoke <player>
         * added Particle animations, the new is for the new location (where you are going), old is for the old location (where you came from)
         *  /tport particleAnimation new set <particleAnimation> [data...]
         *  /tport particleAnimation new edit <data...>
         *  /tport particleAnimation new test
         *  /tport particleAnimation new enable [state]
         *  /tport particleAnimation old set <particleAnimation> [data...]
         *  /tport particleAnimation old edit <data...>
         *  /tport particleAnimation old test
         *  /tport particleAnimation old enable [state]
         * added a customizable delay to your teleportation
         *  /tport delay permission [state]
         *  /tport delay set <player> <delay> (works only when '/tport delay permission' is set to false)
         *  /tport delay get [player]
         * added a teleport restriction to your teleportation, these are customizable. During your delay time
         * the restriction is started, when you fail your restriction you wont be teleported (now available: none and walking)
         *  /tport restriction permission [state]
         *  /tport restriction set <player> <type>
         *  /tport restriction get [player]
         *  /tport cancel (to cancel your teleport request during the delay time)
         * removed /tport PLTP <on|off>, is now /tport PLTP state [state]
         * While a TPort is offered you can't edit it, remove it, make it public and make it not public
         *  TPort range:
         *      Owner must be in range to teleport to.
         *      Private statement is also active.
         *      When set to 0 its turned off.
         *      When owner is offline the last known location is used
         * tport lore supports colors, use char & and a color code to add colors
         * improved /tport help
         * improved Quick Edit in TPort GUI. Middle-click to change edit function (if you have remapped your middle-click button to something else, use that!),
         *      right-click to edit. Already in Quick Edit: Private statement (on, off, online, prion) and Range (0, 50, 100), Swap, Log (ONLINE, OFFLINE, ALL, NONE).
         *      Only works in you are in your own TPort GUI, can't edit others TPorts
         * add private statement online whitelist (PRION)
         *  like online, but when your are offline players in your TPort whitelist can teleport
         * in some cases TPorts are rename friendly, friendly at:
         *      /tport home
         *      public TPorts
         *  not friendly at:
         *      TPort Compass, why not? When you look at your compass it shows the name. And its not handy to change all the made compasses the server has
         * TPort names now can't be a number, but it can contain a number like 'TPort3'
         * added more options to the back buttons
         * removed permissions for /tport remove <TPort>, you should always be able to remove a TPort
         * removed PermissionStrip, found out that Bukkit already has this. Use the '*' as 'everything' wildcard
         * edited a lot of permissions, check 'permissions.txt' in the plugin folder to see all permissions
         * added the permissions to the help command
         * fixed item duplication bug where pressing 1 while hovering on an item in a TPort gui or BiomeTP/FeatureTP gui you got the item
         */
        
        /*
         * todo
         * remove converting methods
         * improve TPort back code
         * */
        
        ConfigurationSerialization.registerClass(ColorTheme.class, "ColorTheme");
        ConfigurationSerialization.registerClass(TPort.class, "TPort");
        ConfigurationSerialization.registerClass(Pair.class, "Pair");
        
        ParticleAnimation.registerAnimation(SimpleAnimation::new);
        ParticleAnimation.registerAnimation(ExplosionAnimation::new);
        TPRestriction.registerRestriction(NoneRestriction::new);
        TPRestriction.registerRestriction(WalkRestriction::new);
        
        Reload.reloadTPort();
        
        TPEManager.loadTPE(GettingFiles.getFile("TPortConfig"));
        
        ColorTheme.loadThemes(GettingFiles.getFile("TPortConfig"));
        
        
        Glow.registerGlow();
        
        new TPortCommand();
        
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new InventoryClick(), this);
        pm.registerEvents(new CompassEvents(), this);
        pm.registerEvents(new JoinEvent(this), this);
        pm.registerEvents(new DeathEvent(), this);
        pm.registerEvents(new OfflineLocationManager(), this);
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            JoinEvent.setData(this, player);
        }
    }
    
    @Override
    public void onDisable() {
        TPEManager.saveTPE(GettingFiles.getFile("TPortConfig"));
        ColorTheme.saveThemes(GettingFiles.getFile("TPortConfig"));
        Auto.save();
    }
}
