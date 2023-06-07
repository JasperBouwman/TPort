package com.spaceman.tport.tport;

import com.spaceman.tport.Main;
import com.spaceman.tport.Pair;
import com.spaceman.tport.commands.tport.Features;
import com.spaceman.tport.commands.tport.SafetyCheck;
import com.spaceman.tport.commands.tport.log.LogSize;
import com.spaceman.tport.cooldown.CooldownManager;
import com.spaceman.tport.dynmap.DynmapHandler;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.MessageUtils;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import com.spaceman.tport.fancyMessage.inventories.InventoryModel;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tpEvents.TPRequest;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.NumberConversions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

import static com.spaceman.tport.fancyMessage.MessageUtils.transformColoredTextToMessage;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;
import static com.spaceman.tport.fancyMessage.encapsulation.TPortEncapsulation.asTPort;
import static com.spaceman.tport.inventories.QuickEditInventories.*;
import static com.spaceman.tport.tpEvents.TPEManager.tpPlayerToTPort;

@SuppressWarnings({"WeakerAccess", "UnusedReturnValue", "FieldMayBeFinal"})
public class TPort implements ConfigurationSerializable {
    
    private UUID tportID = null;
    private UUID owner;
    private ItemStack item;
    private Location location;
    private String name;
    private int range = 0;
    private String description = "";
    private PrivateState privateState = PrivateState.OPEN;
    private WhitelistVisibility whitelistVisibility = WhitelistVisibility.ON;
    private ArrayList<UUID> whitelist = new ArrayList<>();
    private int slot = -1;
    private boolean publicTPort = false;
    private UUID offeredTo = null;
    private ArrayList<LogEntry> logBook = new ArrayList<>();
    private HashMap<UUID, LogMode> logged = new HashMap<>();
    private LogMode defaultLogMode = LogMode.NONE;
    private NotifyMode notifyMode = NotifyMode.NONE;
    private PreviewState previewState = PreviewState.ON;
    private ArrayList<String> tags = new ArrayList<>();
    private boolean showOnDynmap = true;
    private String dynmapIconID = "";
    private boolean shouldReturnItem = true;
    
    private boolean active;
    private String inactiveWorldName = null;
    
    public TPort(UUID owner, String name, Location location, ItemStack item) {
        this.owner = owner;
        this.name = name;
        this.setLocation(location);
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
                    Main.getInstance().getLogger().info(String.format("World '%s' was not found, TPort '%s' is now set to %s. When using this TPort it will recheck for the world," +
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
        tport.setPrivateState(PrivateState.get((String) args.getOrDefault("ps", PrivateState.OPEN.name()), PrivateState.OPEN));
        tport.setWhitelistVisibility(WhitelistVisibility.get((String) args.getOrDefault("whitelistVisibility", PrivateState.OPEN.name())));
        tport.setDescription((String) args.getOrDefault("description", ""));
        //noinspection unchecked
        tport.setWhitelist(((ArrayList<String>) args.getOrDefault("whitelist", new ArrayList<>())).stream().map(UUID::fromString).collect(Collectors.toCollection(ArrayList::new)));
        tport.setRange((Integer) args.getOrDefault("range", 0));
        if (args.containsKey("offeredTo")) {
            tport.offeredTo = UUID.fromString((String) args.get("offeredTo"));
        }
        if (args.containsKey("logBook")) {
            ArrayList<LogEntry> log = new ArrayList<>();
            //noinspection unchecked
            for (Pair<Long, String> pair : ((ArrayList<Pair<Long, String>>) args.get("logBook"))) {
                Calendar c = Calendar.getInstance();
                c.setTime(new Date(pair.getLeft()));
                log.add(new LogEntry(UUID.fromString(pair.getRight()), c, null, null));
            }
            tport.setLogBook(log);
        }
        if (args.containsKey("logBook_v2")) {
            ArrayList<LogEntry> log = (ArrayList<LogEntry>) args.get("logBook_v2");
            tport.setLogBook(log);
        }
        if (args.containsKey("logged")) {
            //noinspection unchecked
            HashMap<String, String> oldLogged = (HashMap<String, String>) args.get("logged");
            for (String uuid : oldLogged.keySet()) {
                tport.addLogged(UUID.fromString(uuid), LogMode.valueOf(oldLogged.get(uuid)));
            }
        }
        tport.setDefaultLogMode(LogMode.get((String) args.get("defaultLogMode")));
        tport.setNotifyMode(NotifyMode.get((String) args.get("notifyMode")));
        tport.setPreviewState(PreviewState.get((String) args.get("previewState"), PreviewState.ON));
        if (args.containsKey("tags")) {
            //noinspection unchecked
            for (String tag : (List<String>) args.get("tags")) {
                tport.addTag(tag);
            }
        }
        
        tport.showOnDynmap((Boolean) args.getOrDefault("showOnDynmap", true));
        tport.setDynmapIconID((String) args.getOrDefault("dynmapIconID", ""));
        tport.setShouldReturnItem((Boolean) args.getOrDefault("shouldReturnItem", true));
        
        return tport;
    }
    
    @Override
    @Nonnull
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
        if (privateState != PrivateState.OPEN) map.put("ps", privateState.name());
        if (whitelistVisibility != WhitelistVisibility.ON) map.put("whitelistVisibility", whitelistVisibility.name());
        if (!whitelist.isEmpty())
            map.put("whitelist", whitelist.stream().map(UUID::toString).collect(Collectors.toList()));
        if (!description.isEmpty()) map.put("description", description);
        if (range != 0) map.put("range", range);
        if (!logBook.isEmpty()) map.put("logBook_v2", logBook);
        if (!logged.isEmpty()) {
            HashMap<String, String> newLogged = new HashMap<>();
            for (UUID uuid : logged.keySet()) {
                newLogged.put(uuid.toString(), logged.get(uuid).name());
            }
            map.put("logged", newLogged);
        }
        if (defaultLogMode != LogMode.NONE) map.put("defaultLogMode", defaultLogMode.name());
        if (notifyMode != NotifyMode.NONE) map.put("notifyMode", notifyMode.name());
        if (previewState != PreviewState.ON) map.put("previewState", previewState.name());
        map.put("tags", tags);
        
        map.put("showOnDynmap", showOnDynmap);
        map.put("dynmapIconID", dynmapIconID);
        map.put("shouldReturnItem", shouldReturnItem);
        
        return map;
    }
    
    @Override
    public String toString() {
        return this.getName();
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
    
    public Biome getBiome() {
        return location.getBlock().getBiome();
    }
    
    public World.Environment getDimension() {
        return Main.getOrDefault(location.getWorld(), Bukkit.getWorlds().get(0)).getEnvironment();
    }
    
    public PrivateState getPrivateState() {
        return privateState;
    }
    
    public void setPrivateState(PrivateState privateState) {
        this.privateState = privateState;
    }
    
    public WhitelistVisibility getWhitelistVisibility() {
        return whitelistVisibility;
    }
    
    public void setWhitelistVisibility(WhitelistVisibility whitelistVisibility) {
        this.whitelistVisibility = whitelistVisibility;
    }
    
    public Boolean hasAccess(Player player) {
        return hasAccess(player.getUniqueId());
    }
    
    public Boolean hasAccess(UUID uuid) {
        return this.getPrivateState().hasAccess(uuid, this);
    }
    
    public ArrayList<UUID> getWhitelist() {
        return whitelist;
    }
    
    public void setWhitelist(ArrayList<UUID> whitelist) {
        this.whitelist = whitelist;
    }
    
    public boolean addWhitelist(UUID uuid) {
        if (!whitelist.contains(uuid)) {
            return this.whitelist.add(uuid);
        }
        return false;
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
    
    public Message getDescription() {
        Message d = new Message();
        ArrayList<Message> listedDescription = getListedDescription();
        for (int i = 0; i < listedDescription.size() - 1; i++) {
            Message m = listedDescription.get(i);
            d.addMessage(m);
            d.addNewLine();
        }
        d.addMessage(listedDescription.get(listedDescription.size() - 1));
        return d;
    }
    public ArrayList<Message> getListedDescription() {
        return transformColoredTextToMessage("&9" + description, "#5555ff");
    }
    
    public String getRawDescription() {
        return description;
    }
    
    public String getTextDescription() {
        return getDescription().getText().stream().map(TextComponent::getText).collect(Collectors.joining());
    }
    
    public void setDescription(@Nullable String description) {
        if (description == null) {
            description = "";
        }
        this.description = description;
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
        if (this.hasRange()) DynmapHandler.updateTPort(this);
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
    
    public UUID getOfferedTo() {
        return offeredTo;
    }
    
    public void setOfferedTo(@Nullable UUID receiver) {
        this.offeredTo = receiver;
    }
    
    public void clearLogBook() {
        this.logBook = new ArrayList<>();
    }
    
    public ArrayList<LogEntry> getLogBook() {
        logBook.sort(Comparator.comparing(logEntry -> logEntry.timeOfTeleport().getTime()));
        return logBook;
    }
    
    public void setLogBook(ArrayList<LogEntry> log) {
        this.logBook = log;
    }
    
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isLogBookEmpty() {
        return logBook.isEmpty();
    }
    
    public void log(UUID consumer) {
        LogMode logMode = getLogMode(consumer);
        if (logMode.shouldLog(consumer, this)) {
            logBook.add(new LogEntry(consumer, Calendar.getInstance(), logMode, Bukkit.getPlayer(owner) != null));
            while (logBook.size() > LogSize.getLogSize()) {
                logBook.remove(logBook.size() - 1);
            }
        }
    }
    
    public List<UUID> getLogged() {
        return new ArrayList<>(logged.keySet());
    }
    
    public LogMode getLogMode(@Nullable UUID uuid) {
        if (uuid == null) return LogMode.NONE;
        if (uuid.equals(owner)) return logged.getOrDefault(owner, LogMode.NONE);
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
    
    /**
     * @return true if successfully changed
     */
    public boolean setPublicTPort(boolean publicTPort, @Nullable Player player) {
        boolean b = this.publicTPort != publicTPort;
        this.publicTPort = publicTPort;
        if (publicTPort) {
            if (!privateState.canGoPublic()) {
                this.setPrivateState(PrivateState.OPEN);
                sendInfoTranslation(player, "tport.tport.tport.setPublicTPort.incompatiblePrivateState", this, privateState);
            }
            if (!previewState.canGoPublic()) {
                this.setPreviewState(PreviewState.ON);
                sendInfoTranslation(player, "tport.tport.tport.setPublicTPort.incompatiblePreviewState", this, previewState);
            }
        }
        if (b) DynmapHandler.updateTPort(this);
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
    
    public NotifyMode getNotifyMode() {
        return notifyMode;
    }
    
    public void setNotifyMode(NotifyMode notifyMode) {
        this.notifyMode = notifyMode;
    }
    
    public void notifyOwner(Player teleporter) {
        if (shouldNotify(teleporter.getUniqueId())) {
            Player player = Bukkit.getPlayer(owner);
            if (player != null) {
                sendInfoTranslation(player, "tport.tport.tport.notifyOwner", teleporter, this);
            }
        }
    }
    
    public PreviewState getPreviewState() {
        return previewState;
    }
    public void setPreviewState(PreviewState previewState) {
        this.previewState = previewState;
    }
    
    public boolean addTag(String tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
            DynmapHandler.updateTPort(this);
            return true;
        }
        return false;
    }
    
    public boolean removeTag(String tag) {
        if (tags.contains(tag)) {
            tags.remove(tag);
            DynmapHandler.updateTPort(this);
            return true;
        }
        return false;
    }
    
    public ArrayList<String> getTags() {
        return new ArrayList<>(tags);
    }
    
    public boolean hasTags() {
        return !tags.isEmpty();
    }
    
    public boolean showOnDynmap() {
        return showOnDynmap;
    }
    
    public void showOnDynmap(boolean state) {
        showOnDynmap = state;
    }
    
    public String getDynmapIconID() {
        return dynmapIconID;
    }
    
    public void setDynmapIconID(String dynmapIconID) {
        this.dynmapIconID = dynmapIconID;
    }
    
    public boolean shouldReturnItem() {
        return shouldReturnItem;
    }
    
    public void setShouldReturnItem(boolean shouldReturnItem) {
        this.shouldReturnItem = shouldReturnItem;
    }
    
    //this is used when a TPort is being converted to a TextComponent in ColorTheme#formatTranslation(String color, String varColor, String id, Object... objects)
    private boolean parseAsPublic = false;
    
    public TPort parseAsPublic(boolean parseAsPublic) {
        this.parseAsPublic = parseAsPublic;
        return this;
    }
    
    public boolean parseAsPublic() {
        return parseAsPublic;
    }
    
    public void save() {
        TPortManager.saveTPort(this);
        DynmapHandler.updateTPort(this);
    }
    
    public void setInactiveWorldName(String inactiveWorldName) {
        this.inactiveWorldName = inactiveWorldName;
    }
    
    public boolean canTeleport(Player player, boolean sendError, boolean askConsent, boolean safetyCheck) {
        if (!this.isActive()) {
            if (sendError) sendErrorTranslation(player, "tport.tport.tport.canTeleport.notActive", this);
            return false;
        }
        
        if (this.getLocation() == null) {
            if (sendError) sendErrorTranslation(player, "tport.tport.tport.canTeleport.worldNotFound");
            return false;
        }
        
        if (safetyCheck) {
            if (!SafetyCheck.isSafe(this.getLocation())) {
                if (sendError) sendErrorTranslation(player, "tport.tport.tport.canTeleport.safetyCheck", this);
                return false;
            }
        }
        
        if (!owner.equals(player.getUniqueId())) {
            Boolean access = this.getPrivateState().hasAccess(player, this);
            if (access == null) {
                if (askConsent) {
                    
                    Player ownerPlayer = Bukkit.getPlayer(owner);
                    if (ownerPlayer == null) { //should not be null, otherwise access wouldn't be null
                        return false;
                    }
                    
                    if (TPRequest.hasRequest(player, true)) {
                        return false;
                    }
                    TPRequest.createTPortRequest(player.getUniqueId(), this);
                    
                    Message accept = formatTranslation(varInfoColor, varInfo2Color, "tport.command.requests.here");
                    accept.getText().forEach(t -> t
                            .addTextEvent(ClickEvent.runCommand("/tport requests accept " + player.getName()))
                            .addTextEvent(new HoverEvent(textComponent("/tport requests accept " + player.getName(), infoColor))));
                    Message reject = formatTranslation(varInfoColor, varInfo2Color, "tport.command.requests.here");
                    reject.getText().forEach(t -> t
                            .addTextEvent(ClickEvent.runCommand("/tport requests reject " + player.getName()))
                            .addTextEvent(new HoverEvent(textComponent("/tport requests reject " + player.getName(), infoColor))));
                    Message revoke = formatTranslation(varInfoColor, varInfo2Color, "tport.command.requests.here");
                    revoke.getText().forEach(t -> t
                            .addTextEvent(ClickEvent.runCommand("/tport requests revoke"))
                            .addTextEvent(new HoverEvent(textComponent("/tport requests revoke", infoColor))));
                    
                    sendInfoTranslation(ownerPlayer, "tport.tport.tport.consent.consent.askConsent", player, this, accept, reject);
                    sendInfoTranslation(player, "tport.tport.tport.consent.consent.consentAsked", ownerPlayer, this, this.getPrivateState(), revoke);
                    return false;
                }
            }
            if (access != null && !access) {
                if (sendError) this.getPrivateState().sendErrorMessage(player, this);
                return false;
            }
            
            if (this.hasRange()) {
                Player ownerPlayer = Bukkit.getPlayer(owner);
                if (ownerPlayer == null) {
                    if (sendError)
                        sendErrorTranslation(player, "tport.tport.tport.canTeleport.ownerOutOfRange", PlayerUUID.getPlayerName(owner), this);
                    return false;
                }
                Location location = ownerPlayer.getLocation();
                if (!Objects.equals(location.getWorld(), this.getLocation().getWorld()) ||
                        location.distance(this.getLocation()) > this.getRange()) {
                    if (sendError)
                        sendErrorTranslation(player, "tport.tport.tport.canTeleport.ownerOutOfRange", ownerPlayer, this);
                    return false;
                }
            }
        }
        
        return true;
    }
    
    public boolean teleport(Player player, boolean safetyCheck) {
        return teleport(player, safetyCheck, true, null, null);
    }
    
    public boolean teleport(Player player, boolean safetyCheck, boolean askConsent, @Nullable String successMessage, @Nullable String requestMessage) {
        if (!CooldownManager.TPortTP.hasCooled(player, true)) {
            return false;
        }
        
        if (!this.canTeleport(player, true, askConsent, safetyCheck)) {
            return false;
        }
        
        player.closeInventory();
        
        tpPlayerToTPort(player, this, () -> {
                    TPort lambdaTPort = TPortManager.getTPort(this.getOwner(), this.getTportID());
                    sendSuccessTranslation(player, successMessage == null ? "tport.tport.tport.teleport.succeeded" : successMessage, asTPort(lambdaTPort, this.getName()));
                    if (lambdaTPort != null) {
                        lambdaTPort.notifyOwner(player);
                        lambdaTPort.log(player.getUniqueId());
                        lambdaTPort.save();
                    }
                },
                (p, delay, tickMessage, seconds, secondMessage) -> sendSuccessTranslation(p, requestMessage == null ? "tport.tport.tport.teleport.tpRequested" : requestMessage,
                        this, delay, tickMessage, seconds, secondMessage));
        
        CooldownManager.TPortTP.update(player);
        return true;
    }
    
    public List<Message> getHoverData(boolean addOwner) {
        List<Message> hoverData = new ArrayList<>(15);
        
        if (addOwner) {
            hoverData.add(formatInfoTranslation("tport.tport.tport.hoverData.tportOwner", PlayerUUID.getPlayerName(this.getOwner())));
            hoverData.add(new Message());
        }
        
        if (this.hasDescription()) {
            hoverData.addAll(this.getListedDescription());
            hoverData.add(new Message());
        }
        
        hoverData.add(formatInfoTranslation("tport.tport.tport.hoverData.privateState", this.getPrivateState().getDisplayName()));
        hoverData.add(formatInfoTranslation("tport.tport.tport.hoverData.previewState", this.getPreviewState().getDisplayName()));
        
        if (this.getPrivateState().usesWhitelist() && this.getWhitelistVisibility() == WhitelistVisibility.ON) {
            Message whitelistMessage = new Message();
            for (int i = 0; i < this.getWhitelist().size(); i++) {
                if (i == 3) {
                    whitelistMessage.addMessage(formatTranslation(infoColor, infoColor, "tport.tport.tport.hoverData.whitelist.shorten"));
                    whitelistMessage.addWhiteSpace();
                    break;
                }
                UUID uuid = this.getWhitelist().get(i);
                whitelistMessage.addMessage(formatTranslation(varInfoColor, varInfoColor, "tport.tport.tport.hoverData.whitelist.element", PlayerUUID.getPlayerName(uuid)));
                whitelistMessage.addMessage(formatTranslation(infoColor, infoColor, "tport.tport.tport.hoverData.whitelist.delimiter"));
            }
            whitelistMessage.removeLast();
            if (whitelistMessage.getText().isEmpty())
                hoverData.add(formatInfoTranslation("tport.tport.tport.hoverData.whitelist.list",
                        formatTranslation(varInfoColor, varInfoColor, "tport.tport.tport.hoverData.whitelist.empty")));
            else hoverData.add(formatInfoTranslation("tport.tport.tport.hoverData.whitelist.list", whitelistMessage));
        }
        
        if (this.hasRange()) hoverData.add(formatInfoTranslation("tport.tport.tport.hoverData.range", this.getRange()));
        if (Features.Feature.PublicTP.isEnabled()) hoverData.add(formatInfoTranslation("tport.tport.tport.hoverData.publicTPort", this.isPublicTPort()));
        hoverData.add(formatInfoTranslation("tport.tport.tport.hoverData.defaultLogMode", this.getDefaultLogMode()));
        hoverData.add(formatInfoTranslation("tport.tport.tport.hoverData.notifyMode", this.getNotifyMode()));
        if (DynmapHandler.isEnabled()) {
            hoverData.add(formatInfoTranslation("tport.tport.tport.hoverData.dynmapShow", this.showOnDynmap()));
            hoverData.add(formatInfoTranslation("tport.tport.tport.hoverData.dynmapIcon", DynmapHandler.getTPortIconName(this)));
        }
        if (this.hasTags()) {
            Message tagsMessage = new Message();
            
            boolean color = true;
            for (String tag : this.getTags()) {
                tagsMessage.addText(textComponent(tag, (color ? varInfoColor : varInfo2Color)));
                tagsMessage.addText(textComponent(", ", infoColor));
                color = !color;
            }
            tagsMessage.removeLast();
            
            if (this.getTags().size() == 1) {
                hoverData.add(formatInfoTranslation("tport.tport.tport.hoverData.tag", tagsMessage));
            } else {
                hoverData.add(formatInfoTranslation("tport.tport.tport.hoverData.tags", tagsMessage));
            }
        }
        
        if (this.isOffered()) {
            hoverData.add(new Message());
            hoverData.add(formatInfoTranslation("tport.tport.tport.hoverData.isOffered", PlayerUUID.getPlayerName(this.getOfferedTo())));
        }
        
        return hoverData;
    }
    
    public enum PrivateState implements MessageUtils.MessageDescription {
        OPEN(new TextComponent("open", ChatColor.RED), true, false, quick_edit_private_open_model,
                (player, tport) -> true),
        PRIVATE(new TextComponent("private", ChatColor.GREEN), false, true, quick_edit_private_private_model,
                (player, tport) -> tport.getWhitelist().contains(player)),
        ONLINE(new TextComponent("online", ChatColor.YELLOW), true, true, quick_edit_private_online_model,
                (player, tport) -> Bukkit.getPlayer(tport.getOwner()) != null || PRIVATE.hasAccess(player, tport)),
        PRION(new TextComponent("private online", ChatColor.GOLD), false, true, quick_edit_private_prion_model,
                (player, tport) -> Bukkit.getPlayer(tport.getOwner()) != null && PRIVATE.hasAccess(player, tport)),
        CONSENT_PRIVATE(new TextComponent("consent private", ChatColor.DARK_AQUA), false, true, quick_edit_private_consent_private_model,
                (player, tport) -> Bukkit.getPlayer(tport.getOwner()) != null ? null : PRIVATE.hasAccess(player, tport)),
        CONSENT_CLOSE(new TextComponent("consent close", ChatColor.BLUE), false, true, quick_edit_private_consent_close_model,
                (player, tport) -> (Bukkit.getPlayer(tport.getOwner()) != null && tport.getWhitelist().contains(player)) ? null : false);
        
        /*
         * OPEN: always open
         * PRIVATE: only players in whitelist can teleport
         * ONLINE: when online OPEN, when offline PRIVATE
         * PRION: when online PRIVATE, when offline close
         * CONSENT_PRIVATE: when online ask consent, when offline PRIVATE
         * CONSENT_CLOSE: when online whitelist ask consent, when offline close
         * */
        
        private final AccessTester tester;
        private final TextComponent displayName;
        private final boolean canGoPublic;
        private final boolean usesWhitelist;
        private final InventoryModel inventoryModel;
        
        PrivateState(TextComponent displayName, boolean canGoPublic, boolean usesWhitelist, InventoryModel inventoryModel, AccessTester tester) {
            this.displayName = displayName;
            this.canGoPublic = canGoPublic;
            this.tester = tester;
            this.inventoryModel = inventoryModel;
            this.usesWhitelist = usesWhitelist;
        }
        
        @Nonnull
        public static PrivateState get(@Nullable String name, PrivateState def) {
            if (name == null) {
                return def;
            }
            try {
                return PrivateState.valueOf(name.toUpperCase());
            } catch (IllegalArgumentException | NullPointerException iae) {
                if (name.equalsIgnoreCase("on")) {
                    return PRIVATE;
                } else if (name.equalsIgnoreCase("off")) {
                    return OPEN;
                }
                return def;
            }
        }
        
        public InventoryModel getInventoryModel() {
            return inventoryModel;
        }
        
        @SuppressWarnings("BooleanMethodIsAlwaysInverted")
        public boolean canGoPublic() {
            return canGoPublic;
        }
        
        public boolean usesWhitelist() {
            return usesWhitelist;
        }
        
        public PrivateState getNext() {
            boolean next = false;
            for (PrivateState state : values()) {
                if (state.equals(this)) {
                    next = true;
                } else if (next) {
                    return state;
                }
            }
            return Arrays.asList(values()).get(0);
        }
        
        public void sendErrorMessage(Player player, TPort tport) {
            sendErrorTranslation(player, "tport.tport.tport.privateState." + this.name() + ".errorMessage",
                    asPlayer(tport.getOwner()),
                    this,
                    tport);
        }
        
        public TextComponent getDisplayName() {
            return new TextComponent(displayName.getText(), displayName.getColor());
        }
        
        @Override
        public Message getDescription() {
            return formatInfoTranslation("tport.tport.tport.privateState." + this.name() + ".description", getDisplayName());
        }
        
        @Override
        public TextComponent getName(String unused) {
            return getDisplayName();
        }
        
        @Override
        public String getInsertion() {
            return name();
        }
        
        public Boolean hasAccess(Player player, TPort tport) {
            return hasAccess(player.getUniqueId(), tport);
        }
        
        //return:
        // true: has access
        // false: has not access
        // null: ask consent
        public Boolean hasAccess(UUID uuid, TPort tport) {
            if (uuid.equals(tport.getOwner())) {
                return true;
            }
            return this.tester.hasAccess(uuid, tport);
        }
        
        @FunctionalInterface
        private interface AccessTester {
            Boolean hasAccess(UUID uuid, TPort tport);
        }
    }
    
    public enum WhitelistVisibility implements MessageUtils.MessageDescription {
        ON(new TextComponent("on", ChatColor.GREEN), quick_edit_whitelist_visibility_on_model),
        OFF(new TextComponent("off", ChatColor.RED), quick_edit_whitelist_visibility_off_model);
        
        private final TextComponent displayName;
        private final InventoryModel model;
        
        WhitelistVisibility(TextComponent displayName, InventoryModel model) {
            this.displayName = displayName;
            this.model = model;
        }
        
        public InventoryModel getModel() {
            return model;
        }
        
        public static WhitelistVisibility get(@Nullable String name) {
            try {
                return WhitelistVisibility.valueOf(name != null ? name.toUpperCase() : ON.name());
            } catch (IllegalArgumentException | NullPointerException iae) {
                return ON;
            }
        }
        
        public WhitelistVisibility getNext() {
            if (this == ON) {
                return OFF;
            } else {
                return ON;
            }
        }
        
        public TextComponent getDisplayName() {
            return new TextComponent(displayName.getText(), displayName.getColor());
        }
        
        @Override
        public Message getDescription() {
            return formatInfoTranslation("tport.tport.tport.whitelistVisibility." + this.name() + ".description", this.getDisplayName());
        }
        
        @Override
        public TextComponent getName(String ignore) {
            return getDisplayName();
        }
        
        @Override
        public String getInsertion() {
            return name();
        }
    }
    
    public enum LogMode implements MessageUtils.MessageDescription {
        ONLINE((uuid, tport) -> Bukkit.getPlayer(tport.getOwner()) != null, quick_edit_log_mode_online_model),
        OFFLINE((uuid, tport) -> Bukkit.getPlayer(tport.getOwner()) == null, quick_edit_log_mode_offline_model),
        ALL((uuid, tport) -> true, quick_edit_log_mode_all_model),
        NONE((uuid, tport) -> false, quick_edit_log_mode_none_model);
        
        private final LogTester tester;
        private final InventoryModel model;
        
        LogMode(LogTester tester, InventoryModel model) {
            this.tester = tester;
            this.model = model;
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
        
        public boolean shouldLog(UUID uuid, TPort tport) {
            return tester.shouldLog(uuid, tport);
        }
        
        public LogMode getNext() {
            boolean next = false;
            for (LogMode state : values()) {
                if (state.equals(this)) {
                    next = true;
                } else if (next) {
                    return state;
                }
            }
            return Arrays.asList(values()).get(0);
        }
        
        public InventoryModel getModel() {
            return model;
        }
        
        @Override
        public Message getDescription() {
            return formatInfoTranslation("tport.tport.tport.logMode." + this.name() + ".description", this.name());
        }
        
        @Override
        public TextComponent getName(String varColor) {
            return new TextComponent(name(), varColor);
        }
        
        @Override
        public String getInsertion() {
            return name();
        }
        
        @FunctionalInterface
        private interface LogTester {
            boolean shouldLog(UUID uuid, TPort tport);
        }
    }
    
    public enum NotifyMode implements MessageUtils.MessageDescription {
        ONLINE(new TextComponent("online", ChatColor.GREEN), quick_edit_notify_online_model, (uuid, tport) -> Bukkit.getPlayer(tport.getOwner()) != null),
        LOG(new TextComponent("log", ChatColor.YELLOW), quick_edit_notify_log_model, (uuid, tport) -> tport.getLogMode(uuid).shouldLog(uuid, tport)),
        NONE(new TextComponent("none", ChatColor.RED), quick_edit_notify_none_model, (uuid, tport) -> false);
        
        private final NotifyTester tester;
        private final TextComponent displayName;
        private final InventoryModel model;
        
        NotifyMode(TextComponent displayName, InventoryModel model, NotifyTester tester) {
            this.displayName = displayName;
            this.model = model;
            this.tester = tester;
        }
        
        public InventoryModel getModel() {
            return model;
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
        
        public boolean shouldNotify(UUID uuid, TPort tport) {
            return tester.shouldNotify(uuid, tport);
        }
        
        public TextComponent getDisplayName() {
            return new TextComponent(displayName.getText(), displayName.getColor());
        }
        
        @Override
        public Message getDescription() {
            return formatInfoTranslation("tport.tport.tport.notifyMode." + this.name() + ".description", this.getDisplayName());
        }
        
        @Override
        public TextComponent getName(String varColor) {
            return getDisplayName();
        }
        
        @Override
        public String getInsertion() {
            return name();
        }
        
        public NotifyMode getNext() {
            boolean next = false;
            for (NotifyMode mode : values()) {
                if (mode.equals(this)) {
                    next = true;
                } else if (next) {
                    return mode;
                }
            }
            return Arrays.asList(values()).get(0);
        }
        
        @FunctionalInterface
        private interface NotifyTester {
            boolean shouldNotify(UUID uuid, TPort tport);
        }
    }
    
    public enum PreviewState implements MessageUtils.MessageDescription {
        ON(new TextComponent("on", ChatColor.GREEN), quick_edit_preview_on_model, true),
        OFF(new TextComponent("off", ChatColor.RED), quick_edit_preview_off_model, false),
        NOTIFIED(new TextComponent("notified", ChatColor.YELLOW), quick_edit_preview_notified_model, true);
        
        private final boolean canGoPublic;
        private final TextComponent displayName;
        private final InventoryModel model;
        
        PreviewState(TextComponent displayName, InventoryModel model, boolean canGoPublic) {
            this.displayName = displayName;
            this.model = model;
            this.canGoPublic = canGoPublic;
        }
        
        public InventoryModel getModel() {
            if (Features.Feature.Preview.isDisabled()) {
                return quick_edit_preview_grayed_model;
            }
            return model;
        }
        
        public boolean canGoPublic() {
            return canGoPublic;
        }
        
        @Nullable
        public static PreviewState get(@Nullable String name, PreviewState def) {
            try {
                return PreviewState.valueOf(name != null ? name.toUpperCase() : ON.name());
            } catch (IllegalArgumentException | NullPointerException iae) {
                return def;
            }
        }
        
        public PreviewState getNext() {
            return switch (this) {
                case ON -> OFF;
                case OFF -> NOTIFIED;
                case NOTIFIED -> ON;
            };
        }
        
        public TextComponent getDisplayName() {
            return new TextComponent(displayName.getText(), displayName.getColor());
        }
        
        @Override
        public Message getDescription() {
            return formatInfoTranslation("tport.tport.tport.previewState." + this.name() + ".description", this.getDisplayName());
        }
        
        @Override
        public TextComponent getName(String ignore) {
            return getDisplayName();
        }
        
        @Override
        public String getInsertion() {
            return name();
        }
    }
    
    public record LogEntry(UUID teleportedUUID, Calendar timeOfTeleport, LogMode loggedMode,
                           Boolean ownerOnline) implements ConfigurationSerializable {
            
            public LogEntry(UUID teleportedUUID, Calendar timeOfTeleport, @Nullable LogMode loggedMode, @Nullable Boolean ownerOnline) {
                this.teleportedUUID = teleportedUUID;
                this.timeOfTeleport = timeOfTeleport;
                this.loggedMode = loggedMode;
                this.ownerOnline = ownerOnline;
            }
            
            @SuppressWarnings("unused")
            public static LogEntry deserialize(Map<String, Object> args) {
                UUID teleportedUUID = UUID.fromString((String) args.get("teleportedUUID"));
                Calendar timeOfTeleport = Calendar.getInstance();
                timeOfTeleport.setTime(new Date((Long) args.get("timeOfTeleport")));
                String loggedModeName = (String) args.getOrDefault("loggedMode", null);
                LogMode loggedMode = loggedModeName == null ? null : LogMode.get(loggedModeName);
                Boolean ownerOnline = (Boolean) args.getOrDefault("ownerOnline", null);
                
                return new LogEntry(teleportedUUID, timeOfTeleport, loggedMode, ownerOnline);
            }
            
            @Nonnull
            @Override
            public Map<String, Object> serialize() {
                return Main.asMap(
                        new Pair<>("teleportedUUID", teleportedUUID.toString()),
                        new Pair<>("timeOfTeleport", timeOfTeleport.getTime().getTime()),
                        loggedMode == null ? null : new Pair<>("loggedMode", loggedMode.name()),
                        ownerOnline == null ? null : new Pair<>("ownerOnline", ownerOnline)
                );
            }
        }
}
