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
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;
import static com.spaceman.tport.fancyMessage.encapsulation.TPortEncapsulation.asTPort;
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
    private ArrayList<Pair<Calendar, UUID>> logBook = new ArrayList<>();
    private HashMap<UUID, LogMode> logged = new HashMap<>();
    private LogMode defaultLogMode = LogMode.NONE;
    private NotifyMode notifyMode = NotifyMode.NONE;
    private PreviewState previewState = PreviewState.ON;
    private ArrayList<String> tags = new ArrayList<>();
    private boolean showOnDynmap = true;
    private String dynmapIconID = "";
    
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
            ArrayList<Pair<Calendar, UUID>> log = new ArrayList<>();
            //noinspection unchecked
            for (Pair<Long, String> pair : ((ArrayList<Pair<Long, String>>) args.get("logBook"))) {
                Calendar c = Calendar.getInstance();
                c.setTime(new Date(pair.getLeft()));
                log.add(new Pair<>(c, UUID.fromString(pair.getRight())));
            }
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
        if (!logBook.isEmpty())
            map.put("logBook", logBook.stream().map(p -> new Pair<>(p.getLeft().getTime().getTime(), p.getRight().toString())).collect(Collectors.toList()));
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
        DynmapHandler.updateTPort(this);
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
        DynmapHandler.updateTPort(this);
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
        
        Pattern hexPattern = Pattern.compile("#[0-9a-fA-F]{6}");
        Matcher hexMatcher = hexPattern.matcher(description);
        while (hexMatcher.find()) {
            String hexColor = description.substring(hexMatcher.start(), hexMatcher.start() + 7);
            description = description.replace(hexColor, "&x" + hexColor.substring(1).replaceAll("(.)", "&$1"));
            hexMatcher = hexPattern.matcher(description);
        }
        
        Pattern rgbPattern = Pattern.compile("(\\$(([01][0-9]{2})|(2[0-4][0-9])|(25[0-5]))){3}");
        Matcher rgbMatcher = rgbPattern.matcher(description);
        while (rgbMatcher.find()) {
            String rgbColor = description.substring(rgbMatcher.start(), rgbMatcher.start() + 12);
            
            //this used bit shift to move the correct color values to their places (rgb).
            //this is done in a lambda, but this used only final objects.
            //this is a problem because I needed an int that stored how far the bits should be shifted.
            //the final int array of size 1 got around that. The array is final, but the int inside that array
            //is not final, this stores how far the bits should shift
            final int[] shift = {16};
            int color = Arrays.stream(rgbColor.substring(1).split("\\$")).mapToInt(Integer::parseInt).map(i -> {
                i <<= shift[0];
                shift[0] -= 8;
                return i;
            }).sum();
            color = ((255) << 24) | color; //this is only to make sure that the leading zeroes are not forgotten (set alpha to max)
            String hexColor = Integer.toHexString(color).substring(2);
            
            description = description.replace(rgbColor, "&x" + hexColor.replaceAll("(.)", "&$1"));
            rgbMatcher = rgbPattern.matcher(description);
        }
        
        this.description = ChatColor.translateAlternateColorCodes('&', description);
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
        DynmapHandler.updateTPort(this);
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
    
    public UUID getOfferedTo() {
        return offeredTo;
    }
    
    public void setOfferedTo(@Nullable UUID receiver) {
        this.offeredTo = receiver;
    }
    
    public void clearLogBook() {
        this.logBook = new ArrayList<>();
    }
    
    public ArrayList<Pair<Calendar, UUID>> getLogBook() {
        logBook.sort(Comparator.comparing(pair -> pair.getLeft().getTime()));
        return logBook;
    }
    
    public void setLogBook(ArrayList<Pair<Calendar, UUID>> log) {
        this.logBook = log;
    }
    
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isLogBookEmpty() {
        return logBook.isEmpty();
    }
    
    public void log(UUID consumer) {
        if (getLogMode(consumer).shouldLog(consumer, this)) {
            logBook.add(new Pair<>(Calendar.getInstance(), consumer));
            while (logBook.size() > LogSize.getLogSize()) {
                logBook.remove(logBook.size() - 1);
            }
        }
    }
    
    public List<UUID> getLogged() {
        return new ArrayList<>(logged.keySet());
    }
    
    public LogMode getLogMode(UUID uuid) {
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
                privateState = PrivateState.OPEN;
                sendInfoTranslation(player, "tport.tport.tport.setPublicTPort.incompatiblePrivateState", this, privateState);
            }
            if (!previewState.canGoPublic()) {
                previewState = PreviewState.ON;
                sendInfoTranslation(player, "tport.tport.tport.setPublicTPort.incompatiblePreviewState", this, previewState);
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
            return true;
        }
        return false;
    }
    
    public boolean removeTag(String tag) {
        return tags.remove(tag);
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
        DynmapHandler.updateTPort(this);
    }
    
    public String getDynmapIconID() {
        return dynmapIconID;
    }
    
    public void setDynmapIconID(String dynmapIconID) {
        this.dynmapIconID = dynmapIconID;
        DynmapHandler.updateTPort(this);
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
    
    public List<Message> getHoverData(boolean extended) {
        List<Message> hoverData = new ArrayList<>(15);
        
        if (extended) {
            hoverData.add(formatInfoTranslation("tport.tport.tport.hoverData.tportOwner", PlayerUUID.getPlayerName(this.getOwner())));
            hoverData.add(new Message());
        }
        
        if (this.hasDescription()) {
            for (String s : this.getDescription().split("\\\\n")) {
                hoverData.add(new Message(textComponent(ChatColor.BLUE + s)));
            }
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
        
        hoverData.add(formatInfoTranslation("tport.tport.tport.hoverData.range", this.getRange()));
        if (Features.Feature.PublicTP.isEnabled()) hoverData.add(formatInfoTranslation("tport.tport.tport.hoverData.publicTPort", this.isPublicTPort()));
        hoverData.add(formatInfoTranslation("tport.tport.tport.hoverData.defaultLogMode", this.getDefaultLogMode().name()));
        hoverData.add(formatInfoTranslation("tport.tport.tport.hoverData.notifyMode", this.getNotifyMode().name()));
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
            
            hoverData.add(formatInfoTranslation("tport.tport.tport.hoverData.tags", tagsMessage));
        }
        
        if (this.isOffered()) {
            hoverData.add(new Message());
            hoverData.add(formatInfoTranslation("tport.tport.tport.hoverData.isOffered", PlayerUUID.getPlayerName(this.getOfferedTo())));
        }
        
        return hoverData;
    }
    
    public enum PrivateState implements MessageUtils.MessageDescription {
        OPEN(ChatColor.RED + "open", true, false,
                (player, tport) -> true),
        PRIVATE(ChatColor.GREEN + "private", false, true,
                (player, tport) -> tport.getWhitelist().contains(player)),
        ONLINE(ChatColor.YELLOW + "online", true, true,
                (player, tport) -> Bukkit.getPlayer(tport.getOwner()) != null || PRIVATE.hasAccess(player, tport)),
        PRION(ChatColor.GOLD + "private online", false, true,
                (player, tport) -> Bukkit.getPlayer(tport.getOwner()) != null && PRIVATE.hasAccess(player, tport)),
        CONSENT_PRIVATE(ChatColor.DARK_AQUA + "consent private", false, true,
                (player, tport) -> Bukkit.getPlayer(tport.getOwner()) != null ? null : PRIVATE.hasAccess(player, tport)),
        CONSENT_CLOSE(ChatColor.BLUE + "consent close", false, true,
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
        private final String displayName;
        private final boolean canGoPublic;
        private final boolean usesWhitelist;
        
        PrivateState(String displayName, boolean canGoPublic, boolean usesWhitelist, AccessTester tester) {
            this.displayName = displayName;
            this.canGoPublic = canGoPublic;
            this.tester = tester;
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
        
        public String getDisplayName() {
            return displayName;
        }
        
        @Override
        public Message getDescription() {
            return formatInfoTranslation("tport.tport.tport.privateState." + this.name() + ".description", this.getDisplayName());
        }
        
        @Override
        public String getName() {
            return name();
        }
        
        @Override
        public String getInsertion() {
            return getName();
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
        ON,
        OFF;
        
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
        
        @Override
        public Message getDescription() {
            return formatInfoTranslation("tport.tport.tport.whitelistVisibility." + this.name() + ".description", this.name());
        }
        
        @Override
        public String getName() {
            return name();
        }
        
        @Override
        public String getInsertion() {
            return getName();
        }
    }
    
    public enum LogMode implements MessageUtils.MessageDescription {
        ONLINE((uuid, tport) -> Bukkit.getPlayer(tport.getOwner()) != null),
        OFFLINE((uuid, tport) -> Bukkit.getPlayer(tport.getOwner()) == null),
        ALL((uuid, tport) -> true),
        NONE((uuid, tport) -> false);
        
        private final LogTester tester;
        
        LogMode(LogTester tester) {
            this.tester = tester;
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
        
        @Override
        public Message getDescription() {
            return formatInfoTranslation("tport.tport.tport.logMode." + this.name() + ".description", this.name());
        }
        
        @Override
        public String getName() {
            return name();
        }
        
        @Override
        public String getInsertion() {
            return getName();
        }
        
        @FunctionalInterface
        private interface LogTester {
            boolean shouldLog(UUID uuid, TPort tport);
        }
    }
    
    public enum NotifyMode implements MessageUtils.MessageDescription {
        ONLINE((uuid, tport) -> Bukkit.getPlayer(tport.getOwner()) != null),
        LOG((uuid, tport) -> tport.getLogMode(uuid).shouldLog(uuid, tport)),
        NONE((uuid, tport) -> false);
        
        private final NotifyTester tester;
        
        NotifyMode(NotifyTester tester) {
            this.tester = tester;
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
        
        @Override
        public Message getDescription() {
            return formatInfoTranslation("tport.tport.tport.notifyMode." + this.name() + ".description", this.name());
        }
        
        @Override
        public String getName() {
            return name();
        }
        
        @Override
        public String getInsertion() {
            return getName();
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
        ON(ChatColor.RED + "on", true),
        OFF(ChatColor.GREEN + "off", false),
        NOTIFIED(ChatColor.YELLOW + "notified",true);
        
        private final boolean canGoPublic;
        private final String displayName;
        
        PreviewState(String displayName, boolean canGoPublic) {
            this.displayName = displayName;
            this.canGoPublic = canGoPublic;
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
        
        public String getDisplayName() {
            return displayName;
        }
        
        @Override
        public Message getDescription() {
            return formatInfoTranslation("tport.tport.tport.previewState." + this.name() + ".description", this.getDisplayName());
        }
        
        @Override
        public String getName() {
            return name();
        }
        
        @Override
        public String getInsertion() {
            return getName();
        }
    }
}
