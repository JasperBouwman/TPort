package com.spaceman.tport.tport;

import com.spaceman.tport.OfflineLocationManager;
import com.spaceman.tport.Pair;
import com.spaceman.tport.colorFormatter.ColorTheme;
import com.spaceman.tport.commands.tport.Delay;
import com.spaceman.tport.cooldown.CooldownManager;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.playerUUID.PlayerUUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.NumberConversions;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

import static com.spaceman.tport.colorFormatter.ColorTheme.ColorType.infoColor;
import static com.spaceman.tport.colorFormatter.ColorTheme.ColorType.varInfoColor;
import static com.spaceman.tport.colorFormatter.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.events.InventoryClick.tpPlayerToTPort;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.events.ClickEvent.runCommand;

@SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
public class TPort implements ConfigurationSerializable {
    
    public static int maxLogBookSize = 50;
    
    private UUID tportID;
    private UUID owner;
    private ItemStack item;
    private Location location;
    private String name;
    private int range = 0;
    private String description = "";
    private PrivateStatement privateStatement = PrivateStatement.OFF;
    private ArrayList<UUID> whitelist = new ArrayList<>();
    private int slot = -1;
    private boolean publicTPort = false;
    private UUID offeredTo = null;
    private ArrayList<Pair<Calendar, UUID>> logBook = new ArrayList<>();
    private HashMap<UUID, LogMode> logged = new HashMap<>();
    private LogMode defaultLogMode = LogMode.NONE;
    private NotifyMode notifyMode = NotifyMode.NONE;
    
    private boolean active;
    private String inactiveWorldName = null;
    
    public TPort(UUID owner, String name, Location location, ItemStack item) {
        this.owner = owner;
        this.name = name;
        this.location = location;
        this.item = new ItemStack(item);
        this.active = true;
    }
    
    public TPort(UUID owner, String name, Location location, ItemStack item, String description) {
        this(owner, name, location, item);
        this.description = description;
    }
    
    @SuppressWarnings("unused")
    public static TPort deserialize(Map<String, Object> args) {
        boolean active = true;
        String inactiveWorldName = null;
        World world;
        String name = (String) args.get("name");
        try {
            world = Bukkit.getWorld((String) args.get("world"));
            if (world == null) {
                active = false;
                inactiveWorldName = (String) args.get("world");
                if (inactiveWorldName != null) {
                    Bukkit.getLogger().info(String.format("[TPort] World '%s' was not found, TPort '%s' is now set to %s. When using this TPort it will recheck for the world," +
                            " when found it will be set to active", inactiveWorldName, name, "inactive"));
                }
            }
        } catch (Exception e) {
            world = Bukkit.getWorlds().get(0);
            active = false;
        }
        double x = NumberConversions.toDouble(args.get("x"));
        double y = NumberConversions.toDouble(args.get("y"));
        double z = NumberConversions.toDouble(args.get("z"));
        float yaw = NumberConversions.toFloat(args.get("yaw"));
        float pitch = NumberConversions.toFloat(args.get("pitch"));
        Location location = new Location(world, x, y, z, yaw, pitch);
        
        TPort tport = new TPort(null, name, location, (ItemStack) args.get("item"));
        tport.active = active;
        tport.inactiveWorldName = inactiveWorldName;
        tport.setSlot((Integer) args.getOrDefault("slot", -1));
        tport.setPublicTPort((Boolean) args.getOrDefault("publicTPort", false));
        tport.setPrivateStatement(PrivateStatement.get((String) args.getOrDefault("ps", PrivateStatement.OFF.name())));
        tport.setDescription((String) args.getOrDefault("description", ""));
        //noinspection unchecked
        tport.setWhitelist(((ArrayList<String>) args.getOrDefault("whitelist", new ArrayList<>())).stream().map(UUID::fromString).collect(Collectors.toCollection(ArrayList::new)));
        tport.setRange((Integer) args.getOrDefault("range", 0));
        if (args.containsKey("offeredTo")) {
            tport.offeredTo = UUID.fromString((String) args.get("offeredTo"));
        }
        if (args.containsKey("logBook")) {
            ArrayList<Pair<Calendar, UUID>> log = new ArrayList<>();
            for (Pair<Long, String> pair : ((ArrayList<Pair<Long, String>>) args.get("logBook"))) {
                Calendar c = Calendar.getInstance();
                c.setTime(new Date(pair.getLeft()));
                log.add(new Pair<>(c, UUID.fromString(pair.getRight())));
            }
            tport.setLogBook(log);
        }
        if (args.containsKey("logged")) {
            HashMap<String, String> oldLogged = (HashMap<String, String>) args.get("logged");
            for (String uuid : oldLogged.keySet()) {
                tport.addLogged(UUID.fromString(uuid), LogMode.valueOf(oldLogged.get(uuid)));
            }
        }
        tport.setDefaultLogMode(LogMode.get((String) args.get("defaultLogMode")));
        tport.setNotifyMode(NotifyMode.get((String) args.get("notifyMode")));
        return tport;
    }
    
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("x", location.getX());
        map.put("y", location.getY());
        map.put("z", location.getZ());
        if (this.inactiveWorldName == null && location.getWorld() != null) {
            map.put("world", location.getWorld().getName());
        } else {
            map.put("world", inactiveWorldName);
        }
        map.put("pitch", location.getPitch());
        map.put("yaw", location.getYaw());
        map.put("item", item);
        map.put("slot", slot);
        if (isOffered()) {
            map.put("offeredTo", offeredTo.toString());
        }
        if (publicTPort) map.put("publicTPort", true);
        if (privateStatement != PrivateStatement.OFF) map.put("ps", privateStatement.name());
        if (!whitelist.isEmpty()) map.put("whitelist", whitelist.stream().map(UUID::toString).collect(Collectors.toList()));
        if (!description.isEmpty()) map.put("description", description);
        if (range != 0) map.put("range", range);
        if (!logBook.isEmpty()) map.put("logBook", logBook.stream().map(p -> new Pair(p.getLeft().getTime().getTime(), p.getRight().toString())).collect(Collectors.toList()));
        if (!logged.isEmpty()) {
            HashMap<String, String> newLogged = new HashMap<>();
            for (UUID uuid : logged.keySet()) {
                newLogged.put(uuid.toString(), logged.get(uuid).name());
            }
            map.put("logged", newLogged);
        }
        if (defaultLogMode != LogMode.NONE) map.put("defaultLogMode", defaultLogMode.name());
        if (notifyMode != NotifyMode.NONE) map.put("notifyMode", notifyMode.name());
        return map;
    }
    
    public ItemStack getItem() {
        return new ItemStack(item);
    }
    
    public ItemStack setItem(ItemStack newItem) {
        ItemStack is = item;
        this.item = new ItemStack(newItem);
        return is;
    }
    
    public Location getLocation() {
        return location;
    }
    
    public void setLocation(Location location) {
        this.location = location;
    }
    
    public PrivateStatement getPrivateStatement() {
        return privateStatement;
    }
    
    public void setPrivateStatement(PrivateStatement privateStatement) {
        this.privateStatement = privateStatement;
    }
    
    public boolean hasAccess(Player player) {
        return hasAccess(player.getUniqueId());
    }
    
    public boolean hasAccess(UUID uuid) {
        return this.getPrivateStatement().hasAccess(uuid, this);
    }
    
    public ArrayList<UUID> getWhitelist() {
        return whitelist;
    }
    
    public void setWhitelist(ArrayList<UUID> whitelist) {
        this.whitelist = whitelist;
    }
    
    public boolean addWhitelist(UUID uuid) {
        return this.whitelist.add(uuid);
    }
    
    public boolean removeWhitelist(UUID uuid) {
        return this.whitelist.remove(uuid);
    }
    
    public UUID getOwner() {
        return owner;
    }
    
    public void setOwner(UUID owner) {
        this.owner = owner;
    }
    
    public boolean hasClaimedOwner() {
        return owner != null;
    }
    
    public boolean hasDescription() {
        return description != null && !description.isEmpty();
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(@Nullable String description) {
        if (description == null) {
            description = "";
        }
        this.description = ChatColor.translateAlternateColorCodes('&', description);
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getRange() {
        return range;
    }
    
    public void setRange(int range) {
        this.range = Math.max(range, 0);
    }
    
    public boolean hasRange() {
        return range != 0;
    }
    
    public UUID getTportID() {
        return tportID;
    }
    
    public void setTportID(UUID tportID) {
        this.tportID = tportID;
    }
    
    public int getSlot() {
        return slot;
    }
    
    public void setSlot(int slot) {
        this.slot = slot;
    }
    
    public boolean isPublicTPort() {
        return publicTPort;
    }
    
    public boolean isOffered() {
        return offeredTo != null;
    }
    
    public void setOfferedTo(@Nullable UUID receiver) {
        this.offeredTo = receiver;
    }
    
    public UUID getOfferedTo() {
        return offeredTo;
    }
    
    public void clearLogBook() {
        this.logBook = new ArrayList<>();
    }
    
    public void setLogBook(ArrayList<Pair<Calendar, UUID>> log) {
        this.logBook = log;
    }
    
    public ArrayList<Pair<Calendar, UUID>> getLogBook() {
//        logBook.sort(Comparator.comparing(pair -> pair.getLeft().getTime()));
        return logBook;
    }
    
    public boolean isLogBookEmpty() {
        return logBook.isEmpty();
    }
    
    public void log(UUID consumer) {
        if (!consumer.equals(owner) && getLogMode(consumer).shouldLog(consumer, this)) {
            logBook.add(new Pair<>(Calendar.getInstance(), consumer));
            while (logBook.size() > maxLogBookSize) {
                logBook.remove(logBook.size() - 1);
            }
        }
    }
    
    public List<UUID> getLogged() {
        return new ArrayList<>(logged.keySet());
    }
    
    public LogMode getLogMode(UUID uuid) {
        return logged.getOrDefault(uuid, defaultLogMode);
    }
    
    public LogMode getDefaultLogMode() {
        return defaultLogMode;
    }
    
    public void setDefaultLogMode(LogMode defaultLogMode) {
        this.defaultLogMode = defaultLogMode;
    }
    
    public void addLogged(UUID uuid, LogMode logMode) {
        logged.put(uuid, logMode);
    }
    
    public boolean removeLogged(UUID uuid) {
        return logged.remove(uuid) != null;
    }
    
    public boolean hasLoggedPlayers() {
        return !logged.isEmpty();
    }
    
    public boolean isLogged() {
        if (defaultLogMode != LogMode.NONE) {
            return true;
        }
        for (UUID uuid : getLogged()) {
            if (getLogMode(uuid) != LogMode.NONE) {
                return true;
            }
        }
        return false;
    }
    
    public boolean setPublicTPort(boolean publicTPort) {
        return setPublicTPort(publicTPort, null);
    }
    
    public boolean setPublicTPort(boolean publicTPort, @Nullable Player player) {
        boolean b = this.publicTPort != publicTPort;
        this.publicTPort = publicTPort;
        if (publicTPort) {
            if (!privateStatement.canGoPublic()) {
                privateStatement = PrivateStatement.OFF;
                if (player != null) {
                    sendErrorTheme(player, "Private Statement of TPort %s is now set to %s, because this TPort is now a Public TPort", this.getName(), privateStatement.getDisplayName());
                }
            }
        }
        return b;
    }
    
    public boolean isActive() {
        if (!active && inactiveWorldName != null) {
            World testWorld = Bukkit.getWorld(inactiveWorldName);
            if (testWorld != null) {
                active = true;
                location.setWorld(testWorld);
            }
        }
        return active;
    }
    
    public boolean shouldNotify(UUID uuid) {
        return !uuid.equals(owner) && notifyMode.shouldNotify(uuid, this);
    }
    
    public void setNotifyMode(NotifyMode notifyMode) {
        this.notifyMode = notifyMode;
    }
    
    public NotifyMode getNotifyMode() {
        return notifyMode;
    }
    
    public void notifyOwner(Player teleporter) {
        if (shouldNotify(teleporter.getUniqueId())) {
            Player player = Bukkit.getPlayer(owner);
            if (player != null) {
                Message message = new Message();
                message.addText(textComponent("Player ", infoColor));
                message.addText(textComponent(teleporter.getName(), varInfoColor));
                message.addText(textComponent(" just teleported to TPort ", infoColor));
                message.addText(textComponent(getName(), varInfoColor, runCommand("/tport own " + getName())));
                message.sendMessage(player);
            }
        }
    }
    
    public void save() {
        TPortManager.saveTPort(this);
    }
    
    public void setInactiveWorldName(String inactiveWorldName) {
        this.inactiveWorldName = inactiveWorldName;
    }
    
    public boolean teleport(Player player) {
        return teleport(player, true);
    }
    
    public boolean teleport(Player player, boolean sendMessage) {
        if (!this.isActive()) {
            if (sendMessage)
                sendErrorTheme(player, "TPort %s is not active, it may be inactive because that the world was not found", getName());
            return false;
        }
        if (!CooldownManager.TPortTP.hasCooled(player, sendMessage)) {
            return false;
        }
        
        Location l = this.getLocation();
        
        if (!owner.equals(player.getUniqueId())) {
            
            if (!this.getPrivateStatement().hasAccess(player, this)) {
                this.getPrivateStatement().sendErrorMessage(player, this);
                return false;
            }
            
            if (this.hasRange()) {
                if (l == null) {
                    if (sendMessage) sendErrorTheme(player, "The world for this TPort has not been found");
                    return false;
                }
                Location location = OfflineLocationManager.getLocation(owner);
                if (location == null) {
                    if (sendMessage)
                        sendErrorTheme(player, "Could not get distance of player to TPort, can't teleport you");
                    return false;
                } else {
                    if (location.distance(l) > this.getRange()) {
                        if (sendMessage) sendErrorTheme(player, "The owner is out of his set range of his TPort");
                        return false;
                    }
                }
            }
        }
        
        player.closeInventory();
        
        if (l == null) {
            if (sendMessage) sendErrorTheme(player, "The world for this TPort has not been found");
            return false;
        }
        tpPlayerToTPort(player, l, this.getName(), owner.toString());
        this.notifyOwner(player);
        this.log(player.getUniqueId());
        this.save();
        CooldownManager.TPortTP.update(player);
        
        if (sendMessage) {
            ColorTheme theme = ColorTheme.getTheme(player);
            Message message = new Message();
            if (Delay.delayTime(player) == 0) {
                message.addText("Successfully teleported to ", theme.getSuccessColor());
            } else {
                message.addText("Successfully requested teleportation to ", theme.getSuccessColor());
            }
            message.addText(textComponent(this.getName(), theme.getVarSuccessColor(),
                    runCommand("/tport open " + PlayerUUID.getPlayerName(this.getOwner()) + " " + this.getName())));
            message.sendMessage(player);
        }
        return true;
    }
    
    public enum PrivateStatement {
        OFF(ChatColor.RED + "open", true, "", (player, tport) -> true),
        ON(ChatColor.GREEN + "private", false, "You are not whitelisted to this private TPort", (player, tport) -> tport.getWhitelist().contains(player) || tport.getOwner().equals(player)),
        ONLINE(ChatColor.YELLOW + "online", true, "You can't teleport to this TPort right now, player %s has set this to %s", (player, tport) -> Bukkit.getPlayer(tport.getOwner()) != null),
        PRION(ChatColor.GOLD + "private online", false, "You can't teleport to this TPort right now, player %s has set this to %s", (player, tport) -> ONLINE.hasAccess(player, tport) || ON.hasAccess(player, tport));
        
        private AccessTester tester;
        private String errMessage;
        private String displayName;
        private boolean canGoPublic;
        
        PrivateStatement(String displayName, boolean canGoPublic, String errMessage, AccessTester tester) {
            this.displayName = displayName;
            this.canGoPublic = canGoPublic;
            this.errMessage = errMessage;
            this.tester = tester;
        }
    
        public boolean canGoPublic() {
            return canGoPublic;
        }
    
        public PrivateStatement getNext() {
            boolean next = false;
            for (PrivateStatement statement : values()) {
                if (statement.equals(this)) {
                    next = true;
                } else if (next) {
                    return statement;
                }
            }
            return Arrays.asList(values()).get(0);
        }
        
        public static PrivateStatement get(@Nullable String name) {
            try {
                return PrivateStatement.valueOf(name != null ? name.toUpperCase() : OFF.name());
            } catch (IllegalArgumentException | NullPointerException iae) {
                return OFF;
            }
        }
        
        public void sendErrorMessage(Player player, TPort tport) {
            sendErrorTheme(player, errMessage, PlayerUUID.getPlayerName(tport.getOwner()), this.name().toLowerCase());
        }
    
        public String getDisplayName() {
            return displayName;
        }
    
        public boolean hasAccess(Player player, TPort tport) {
            return hasAccess(player.getUniqueId(), tport);
        }
        
        public boolean hasAccess(UUID uuid, TPort tport) {
            return this.tester.hasAccess(uuid, tport);
        }
        
        @FunctionalInterface
        private interface AccessTester {
            boolean hasAccess(UUID uuid, TPort tport);
        }
    }
    
    public enum LogMode {
        ONLINE((uuid, tport) -> Bukkit.getPlayer(tport.getOwner()) != null),
        OFFLINE((uuid, tport) -> Bukkit.getPlayer(tport.getOwner()) == null),
        ALL((uuid, tport) -> true),
        NONE((uuid, tport) -> false);
        
        private LogTester tester;
        
        LogMode(LogTester tester) {
            this.tester = tester;
        }
        
        public boolean shouldLog(UUID uuid, TPort tport) {
            return tester.shouldLog(uuid, tport);
        }
        
        public static LogMode get(@Nullable String name) {
            return get(name, NONE);
        }
        
        public static LogMode get(@Nullable String name, LogMode def) {
            for (LogMode mode : values()) {
                if (mode.name().equalsIgnoreCase(name)) {
                    return mode;
                }
            }
            return def;
        }
        
        public LogMode getNext() {
            boolean next = false;
            for (LogMode statement : values()) {
                if (statement.equals(this)) {
                    next = true;
                } else if (next) {
                    return statement;
                }
            }
            return Arrays.asList(values()).get(0);
        }
        
        @FunctionalInterface
        private interface LogTester {
            boolean shouldLog(UUID uuid, TPort tport);
        }
    }
    
    public enum NotifyMode {
        ONLINE("When you are online you will be notified", (uuid, tport) -> Bukkit.getPlayer(tport.getOwner()) != null),
        LOG("When the user is logged you will be notified if you are online", (uuid, tport) -> tport.getLogMode(uuid).shouldLog(uuid, tport)),
        NONE("You won't be notified", (uuid, tport) -> false);
    
        private String description;
        private NotifyTester tester;
    
        NotifyMode(String description, NotifyTester tester) {
            this.description = description;
            this.tester = tester;
        }
        
        public boolean shouldNotify(UUID uuid, TPort tport) {
            return tester.shouldNotify(uuid, tport);
        }
    
        public String getDescription() {
            return description;
        }
    
        public static NotifyMode get(@Nullable String name) {
            return get(name, NONE);
        }
        
        public static NotifyMode get(@Nullable String name, NotifyMode def) {
            for (NotifyMode mode : values()) {
                if (mode.name().equalsIgnoreCase(name)) {
                    return mode;
                }
            }
            return def;
        }
        
        public NotifyMode getNext() {
            boolean next = false;
            for (NotifyMode statement : values()) {
                if (statement.equals(this)) {
                    next = true;
                } else if (next) {
                    return statement;
                }
            }
            return Arrays.asList(values()).get(0);
        }
        
        @FunctionalInterface
        private interface NotifyTester {
            boolean shouldNotify(UUID uuid, TPort tport);
        }
    }
}
