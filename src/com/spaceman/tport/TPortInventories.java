package com.spaceman.tport;

import com.spaceman.tport.commands.TPortCommand;
import com.spaceman.tport.commands.tport.*;
import com.spaceman.tport.commands.tport.pltp.Offset;
import com.spaceman.tport.commands.tport.publc.Move;
import com.spaceman.tport.cooldown.CooldownManager;
import com.spaceman.tport.dynmap.DynmapHandler;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.fileHander.GettingFiles;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.StringUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

import static com.spaceman.tport.commands.tport.Back.getPrevLocName;
import static com.spaceman.tport.commands.tport.Sort.getSorter;
import static com.spaceman.tport.events.InventoryClick.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;
import static com.spaceman.tport.tport.TPortManager.getTPort;

public class TPortInventories implements InventoryHolder {
    
    public static HashMap<UUID, UUID> quickEditPublicMoveList = new HashMap<>();
    private static final HashMap<UUID, UUID> quickEditMoveList = new HashMap<>();
    
    private final int page;
    private final InventoryType type;
    private String addendum = null;
    private List<ItemStack> content = null;
    private Inventory inv = null;
    private Search.SearchMode searchMode = null;
    private String searcher = null;
    private String searchedQuery = null;
    
    public TPortInventories(InventoryType type, int page) {
        this.type = type;
        this.page = page;
    }
    
    private static ItemStack toTPortItem(TPort tport, Player player) {
        return toTPortItem(tport, player, false);
    }
    
    public static ItemStack toTPortItem(TPort tport, Player player, boolean extern) {
        Files tportData = getFile("TPortData");
        ItemStack is = tport.getItem();
        ItemMeta im = is.getItemMeta();
        
        if (im == null) {
            return is;
        }
        
        ColorTheme theme = ColorTheme.getTheme(player);
        im.setDisplayName(theme.getVarInfoColor() + tport.getName());
        
        addCommand(im, Action.LEFT_CLICK, "open " + PlayerUUID.getPlayerName(tport.getOwner()) + " " + tport.getName());
        im.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "TPortUUID"), PersistentDataType.STRING, tport.getTportID().toString());
        
        List<String> lore = new ArrayList<>();
        
        if (extern) {
            lore.add("§r" + theme.getInfoColor() + "TPort owner: " + theme.getVarInfoColor() + PlayerUUID.getPlayerName(tport.getOwner()));
            lore.add("");
        }
        
        if (tport.hasDescription()) {
            for (String s : tport.getDescription().split("\\\\n")) {
                lore.add(ChatColor.BLUE + s);
            }
            lore.add("");
        }
        
        lore.add("§r" + theme.getInfoColor() + "Private Statement: " + tport.getPrivateStatement().getDisplayName());
        lore.add("§r" + theme.getInfoColor() + "Range: " + theme.getVarInfoColor() + tport.getRange());
        lore.add("§r" + theme.getInfoColor() + "Public TPort: " + theme.getVarInfoColor() + tport.isPublicTPort());
        lore.add("§r" + theme.getInfoColor() + "Default LogMode: " + theme.getVarInfoColor() + tport.getDefaultLogMode().name());
        lore.add("§r" + theme.getInfoColor() + "Notify mode: " + theme.getVarInfoColor() + tport.getNotifyMode().name());
        if (DynmapHandler.isEnabled()) {
            lore.add("§r" + theme.getInfoColor() + "Dynmap Show: " + theme.getVarInfoColor() + tport.showOnDynmap());
            lore.add("§r" + theme.getInfoColor() + "Dynmap Icon: " + theme.getVarInfoColor() + DynmapHandler.getTPortIconName(tport));
        }
        if (tport.hasTags()) {
            boolean color = false;
            ArrayList<String> tportTags = tport.getTags();
            StringBuilder tags = new StringBuilder(theme.getVarInfoColor() + tportTags.get(0));
            for (int i = 1; i < tportTags.size(); i++) {
                String tag = tportTags.get(i);
                if (color) tags.append(theme.getInfoColor()).append(", ").append(theme.getVarInfoColor()).append(tag);
                if (!color) tags.append(theme.getInfoColor()).append(", ").append(theme.getVarInfo2Color()).append(tag);
                color = !color;
            }
            lore.add("§r" + theme.getInfoColor() + "Tag" + (tport.getTags().size() == 1 ? "" : "s") + ": " + tags);
        }
        
        for (Enchantment e : im.getEnchants().keySet()) {
            im.removeEnchant(e);
        }
        
        if (tport.getOwner().equals(player.getUniqueId()) && !extern) {
            if (tport.getTportID().equals(quickEditMoveList.get(player.getUniqueId()))) {
                Glow.addGlow(im);
            }
            if (tport.isOffered()) {
                lore.add("");
                lore.add("§r" + theme.getInfoColor() + "Offered to: " + theme.getVarInfoColor() + PlayerUUID.getPlayerName(tport.getOfferedTo()));
            }
            lore.add("");
            QuickEditType type = QuickEditType.get(tportData.getConfig().getString("tport." + player.getUniqueId() + ".editState"));
            lore.add("§r" + theme.getInfoColor() + "Editing: " + theme.getVarInfoColor() + type.getDisplayName());
            lore.add("§r" + theme.getInfoColor() + "Press middle-click to edit " + theme.getVarInfoColor() + type.getNext().getDisplayName());
        }
        
        im.setLore(lore);
        
        is.setItemMeta(im);
        return is;
    }
    
    private static ItemStack toPublicTPortItem(TPort tport, Player player) {
        ItemStack is = tport.getItem();
        ItemMeta im = is.getItemMeta();
        
        if (im == null) {
            return is;
        }
        
        ColorTheme theme = ColorTheme.getTheme(player);
        im.setDisplayName(theme.getVarInfoColor() + tport.getName());
        
        addCommand(im, Action.LEFT_CLICK, "public open " + tport.getName());
        im.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "TPortUUID"), PersistentDataType.STRING, tport.getTportID().toString());
        
        List<String> lore = new ArrayList<>();
        
        lore.add("§r" + theme.getInfoColor() + "TPort owner: " + theme.getVarInfoColor() + PlayerUUID.getPlayerName(tport.getOwner()));
        lore.add("");
        
        if (tport.hasDescription()) {
            for (String s : tport.getDescription().split("\\\\n")) {
                lore.add(ChatColor.BLUE + s);
            }
            lore.add("");
        }
        
        lore.add("§r" + theme.getInfoColor() + "Private Statement: " + tport.getPrivateStatement().getDisplayName());
        lore.add("§r" + theme.getInfoColor() + "Range: " + theme.getVarInfoColor() + tport.getRange());
        lore.add("§r" + theme.getInfoColor() + "Public TPort: " + theme.getVarInfoColor() + tport.isPublicTPort());
        lore.add("§r" + theme.getInfoColor() + "Default LogMode: " + theme.getVarInfoColor() + tport.getDefaultLogMode().name());
        lore.add("§r" + theme.getInfoColor() + "Notify mode: " + theme.getVarInfoColor() + tport.getNotifyMode().name());
        if (DynmapHandler.isEnabled())
            lore.add("§r" + theme.getInfoColor() + "Dynmap Show: " + theme.getVarInfoColor() + tport.showOnDynmap());
        if (DynmapHandler.isEnabled())
            lore.add("§r" + theme.getInfoColor() + "Dynmap Icon: " + theme.getVarInfoColor() + DynmapHandler.getTPortIconName(tport));
        if (tport.hasTags()) {
            boolean color = false;
            ArrayList<String> tportTags = tport.getTags();
            StringBuilder tags = new StringBuilder(theme.getVarInfoColor() + tportTags.get(0));
            for (int i = 1; i < tportTags.size(); i++) {
                String tag = tportTags.get(i);
                if (color) tags.append(theme.getInfoColor()).append(", ").append(theme.getVarInfoColor()).append(tag);
                if (!color) tags.append(theme.getInfoColor()).append(", ").append(theme.getVarInfo2Color()).append(tag);
                color = !color;
            }
            lore.add("§r" + theme.getInfoColor() + "Tag" + (tport.getTags().size() == 1 ? "" : "s") + ": " + tags);
        }
        
        for (Enchantment e : im.getEnchants().keySet()) {
            im.removeEnchant(e);
        }
        
        if (Move.emptySlot.hasPermissionToRun(player, false)) {
            if (tport.getTportID().equals(quickEditPublicMoveList.get(player.getUniqueId()))) {
                Glow.addGlow(im);
            }
        }
        
        im.setLore(lore);
        
        is.setItemMeta(im);
        return is;
    }
    
    public enum Action {
        LEFT_CLICK("LClickAction"),
        RIGHT_CLICK("RClickAction"),
        SECONDARY_CLICK("SecondaryClickAction"),
        MIDDLE_CLICK("MClickAction");
        
        private final String code;
        
        Action(String code) {
            this.code = code;
        }
        
        public String getCode() {
            return code;
        }
    }
    
    public static ItemStack addCommand(ItemStack is, Action action, String command) {
        if (is.hasItemMeta()) {
            //noinspection ConstantConditions
            is.setItemMeta(addCommand(is.getItemMeta(), action, command, null));
        }
        return is;
    }
    
    public static ItemMeta addCommand(@Nonnull ItemMeta im, Action action, String command) {
        return addCommand(im, action, command, null);
    }
    
    public static ItemStack addCommand(ItemStack is, Action action, String command, @Nullable String secondary) {
        ItemMeta im = is.getItemMeta();
        if (im != null) {
            is.setItemMeta(addCommand(im, action, command, secondary));
        }
        return is;
    }
    
    public static ItemMeta addCommand(@Nonnull ItemMeta im, Action action, String command, @Nullable String secondary) {
        im.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), action.getCode()), PersistentDataType.STRING, command);
        if (!StringUtils.isEmpty(secondary))
            im.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), Action.SECONDARY_CLICK.getCode()), PersistentDataType.STRING, secondary);
        return im;
    }
    
    private static ItemStack createStack(String displayName, Material material) {
        return createStack(displayName, material, Collections.emptyList());
    }
    
    private static ItemStack createStack(String displayName, Material material, String... lore) {
        return createStack(displayName, material, Arrays.asList(lore));
    }
    
    private static ItemStack createStack(String displayName, Material material, List<String> lore) {
        ItemStack is = new ItemStack(material);
        ItemMeta im = is.getItemMeta();
        if (im != null) {
            im.setDisplayName(displayName);
            if (lore != null && !lore.isEmpty()) im.setLore(lore);
            is.setItemMeta(im);
        }
        return is;
    }
    
    private static ItemStack createBack(ColorTheme theme, BackType left, BackType right, BackType middle) {
        ItemStack is = createStack(BACK, Material.BARRIER, Arrays.asList(
        "§r" + theme.getInfoColor() + "Left-Click:",
        "§r" + theme.getVarInfoColor() + left.getName(),
        "§r" + "",
        "§r" + theme.getInfoColor() + "Right-Click:",
        "§r" + theme.getVarInfoColor() + right.getName(),
        "§r" + "",
        "§r" + theme.getInfoColor() + "Middle-Click:",
        "§r" + theme.getVarInfoColor() + middle.getName()));
        addCommand(is, Action.LEFT_CLICK, left.getCommand());
        addCommand(is, Action.RIGHT_CLICK, right.getCommand());
        addCommand(is, Action.MIDDLE_CLICK, middle.getCommand());
        return is;
    }
    
    public static void openMainTPortGUI(Player player, int page) {
        TPortInventories.openMainTPortGUI(player, page, getSorter(player).sort(player), true);
    }
    
    public static void openMainTPortGUI(Player player, int page, List<ItemStack> list, boolean modifyLayout) {
        
        if (modifyLayout) {
            for (ItemStack is : list) { //remove player and set him first
                if (is.getItemMeta() instanceof SkullMeta) {
                    if (((SkullMeta) is.getItemMeta()).getOwningPlayer().getUniqueId().equals(player.getUniqueId())) {
                        list.remove(is);
                        list.add(0, is);
                        break;
                    }
                }
            }
            list.indexOf(null);
            
            ArrayList<ItemStack> newList = new ArrayList<>();
            Files tportData = GettingFiles.getFile("TPortData");
            for (ItemStack is : list) {
                if (MainLayout.showPlayers(player)) {
                    newList.add(is);
                }
                if (MainLayout.showTPorts(player)) {
                    if (is.getItemMeta() instanceof SkullMeta) {
                        SkullMeta sm = (SkullMeta) is.getItemMeta();
                        if (sm.getOwningPlayer() != null) {
                            TPortManager.getTPortList(tportData, sm.getOwningPlayer().getUniqueId())
                                    .stream().sorted((a, b) -> a.getSlot() < b.getSlot() ? -1 : 1
                            ).map(tport -> toTPortItem(tport, player, true)).forEach(newList::add);
                        }
                    }
                }
            }
            list = newList;
        }
        
        String title = "Select a ";
        if (MainLayout.showPlayers(player)) {
            title += "Player";
        }
        if (MainLayout.showTPorts(player)) {
            if (title.length() > 9) {
                title += "/";
            }
            title += "TPort";
        }
        if (title.length() == 9) {
            title = "Please show Players or TPorts";
        }
        InventoryType inventoryType = InventoryType.MAIN;
        inventoryType.setTitle(title);
        
        Inventory inv = getDynamicScrollableInventory(ColorTheme.getTheme(player), page, inventoryType, list, null);
        inv.setItem(inv.getSize() - 8, addCommand(createStack(ChatColor.YELLOW + "Open BiomeTP", Material.OAK_BUTTON), Action.LEFT_CLICK, "biomeTP"));
        inv.setItem(inv.getSize() - 6, addCommand(createStack(ChatColor.YELLOW + "Open FeatureTP", Material.OAK_BUTTON), Action.LEFT_CLICK, "featureTP"));
        inv.setItem(inv.getSize() - 4, addCommand(createStack(ChatColor.YELLOW + "Go to previous location", Material.OAK_BUTTON), Action.LEFT_CLICK, "back"));
        if (Public.isEnabled())
            inv.setItem(inv.getSize() - 2, addCommand(createStack(ChatColor.YELLOW + "Open Public TPorts", Material.OAK_BUTTON), Action.LEFT_CLICK, "public"));
        
        ColorTheme ct = ColorTheme.getTheme(player);
        ItemStack layoutButton = createStack(ChatColor.YELLOW + "Main Layout", Material.OAK_BUTTON,
                "§r" + ct.getVarInfoColor() + "Right" + ct.getInfoColor() + " Click to " + ct.getVarInfoColor() + (MainLayout.showPlayers(player) ? "Hide" : "Show") + ct.getInfoColor() + " Players",
                "§r" + ct.getVarInfoColor() + "Left" + ct.getInfoColor() + " Click to " + ct.getVarInfoColor() + (MainLayout.showTPorts(player) ? "Hide" : "Show") + ct.getInfoColor() + " TPorts");
        addCommand(layoutButton, Action.RIGHT_CLICK, "mainLayout players " + !MainLayout.showPlayers(player), " ");
        addCommand(layoutButton, Action.LEFT_CLICK, "mainLayout tports " + !MainLayout.showTPorts(player), " ");
        inv.setItem(inv.getSize() / 18 * 9, layoutButton);
        
        ItemStack sortButton = createStack(ChatColor.YELLOW + "Sorting", Material.OAK_BUTTON,
                "§r" + ct.getInfoColor() + "Click to change to sorter " + ct.getVarInfoColor() + Sort.getNextSorterName(Sort.getSorterName(player)));
        addCommand(sortButton, Action.LEFT_CLICK, "sort " + Sort.getNextSorterName(Sort.getSorterName(player)));
        inv.setItem(inv.getSize() / 18 * 9 + 8, sortButton);
        
        
        TPortInventories tportInventories = (TPortInventories) inv.getHolder();
        tportInventories.content = list;
        
        player.openInventory(inv);
    }
    
    public static void openTPortGUI(UUID newPlayerUUID, Player player) {
        
        String newPlayerName = PlayerUUID.getPlayerName(newPlayerUUID);
        
        Validate.notNull(newPlayerName, "The newPlayerName can not be null");
        Validate.notNull(newPlayerUUID, "The newPlayerUUID can not be null");
        Validate.notNull(player, "The player can not be null");
        
        Files tportData = getFile("TPortData");
        TPortInventories tportInventories = new TPortInventories(InventoryType.TPORT, 0);
        tportInventories.addendum = newPlayerName;
        Inventory inv = Bukkit.createInventory(tportInventories, 27, "TPort: " + newPlayerName);
        
        ColorTheme theme = ColorTheme.getTheme(player);
        
        ItemStack extraTP = new ItemStack(Material.OAK_BUTTON);
        ItemMeta metaExtraTP = extraTP.getItemMeta();
        if (metaExtraTP != null) {
            ArrayList<String> extraTPLore = new ArrayList<>();
            extraTPLore.add("§r" + theme.getInfoColor() + "Left-Click:");
            extraTPLore.add("§r" + getPrevLocName(player));
            extraTPLore.add("§r" + "");
            extraTPLore.add("§r" + theme.getInfoColor() + "Right-Click:");
            extraTPLore.add("§r" + theme.getVarInfoColor() + "BiomeTP");
            extraTPLore.add("§r" + "");
            extraTPLore.add("§r" + theme.getInfoColor() + "Middle-Click:");
            extraTPLore.add("§r" + theme.getVarInfoColor() + "FeatureTP");
            metaExtraTP.setLore(extraTPLore);
            metaExtraTP.setDisplayName(ChatColor.YELLOW + "Extra TP features");
            addCommand(metaExtraTP, Action.LEFT_CLICK, "back");
            addCommand(metaExtraTP, Action.RIGHT_CLICK, "biomeTP");
            addCommand(metaExtraTP, Action.MIDDLE_CLICK, "featureTP");
            extraTP.setItemMeta(metaExtraTP);
        }
        inv.setItem(17, extraTP);
        
        inv.setItem(26, createBack(theme, BackType.MAIN, BackType.PUBLIC, BackType.OWN));
        
        boolean pltpState = tportData.getConfig().getBoolean("tport." + newPlayerUUID.toString() + ".tp.statement", true);
        if (newPlayerUUID.equals(player.getUniqueId())) {
            ItemStack warp = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta skin = (SkullMeta) warp.getItemMeta();
            if (skin != null) {
                skin.setOwningPlayer(Bukkit.getOfflinePlayer(newPlayerUUID));
    
                boolean pltpConsent = tportData.getConfig().getBoolean("tport." + newPlayerUUID.toString() + ".tp.consent", false);
                Offset.PLTPOffset pltpOffset = Offset.getPLTPOffset(player);
    
                skin.setDisplayName("§r" + theme.getInfoColor() + "Edit your PLTP settings");
    
                skin.setLore(Arrays.asList(
                        "§r" + theme.getInfoColor() + "PLTP state is set to " + theme.getVarInfoColor() + pltpState,
                        "§r" + theme.getInfoColor() + "PLTP consent is set to " + theme.getVarInfoColor() + pltpConsent,
                        "§r" + theme.getInfoColor() + "PLTP offset is set to " + theme.getVarInfoColor() + pltpOffset.name(),
                        "",
                        "§r" + theme.getInfoColor() + "When left clicking your PLTP state will be turned to " + theme.getVarInfoColor() + !pltpState,
                        "§r" + theme.getInfoColor() + "When right clicking your PLTP consent will be turned to " + theme.getVarInfoColor() + !pltpConsent,
                        "§r" + theme.getInfoColor() + "When middle clicking your PLTP offset will be turned to " + theme.getVarInfoColor() + pltpOffset.getNext()));
    
                addCommand(skin, Action.LEFT_CLICK, "PLTP state " + !pltpState, "own");
                addCommand(skin, Action.RIGHT_CLICK, "PLTP consent " + !pltpConsent, "own");
                addCommand(skin, Action.MIDDLE_CLICK, "PLTP offset " + pltpOffset.getNext(), "own");
    
                warp.setItemMeta(skin);
            }
            inv.setItem(8, warp);
        } else {
            ItemStack warp = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta skin = (SkullMeta) warp.getItemMeta();
            if (skin != null) {
                skin.setOwningPlayer(Bukkit.getOfflinePlayer(newPlayerUUID));
    
                if (!pltpState) {
        
                    ArrayList<String> list = (ArrayList<String>) tportData.getConfig()
                            .getStringList("tport." + newPlayerUUID + "tp.players");
        
                    if (list.contains(player.getUniqueId().toString())) {
                        skin.setDisplayName(WARP + PlayerUUID.getPlayerName(newPlayerUUID));
                    } else {
                        skin.setDisplayName(TPOFF);
                    }
                } else if (Bukkit.getPlayer(newPlayerUUID) != null) {
                    skin.setDisplayName(WARP + PlayerUUID.getPlayerName(newPlayerUUID));
                } else {
                    skin.setDisplayName(OFFLINE);
                }
                addCommand(skin, Action.LEFT_CLICK, "PLTP tp " + newPlayerName);
                warp.setItemMeta(skin);
            }
            inv.setItem(8, warp);
        }
        
        int slotOffset = 0;
        for (int i = 0; i <= TPortSize; i++) {
            
            if (i == 8 || i == 16/*16 because of the slot+slotOffset*/) {
                slotOffset++;
            }
            
            TPort tport = TPortManager.getTPort(newPlayerUUID, i);
            if (tport != null) {
                inv.setItem(i + slotOffset, toTPortItem(tport, player));
            }
        }
        player.openInventory(inv);
    }
    
    public static void openBiomeTP(Player player, int page) {
        List<ItemStack> list = new ArrayList<>();
        ColorTheme theme = ColorTheme.getTheme(player);
        for (Biome biome : Biome.values()) {
            String command = "biomeTP whitelist " + biome.name();
            
            String b = biome.toString();
            String name = "§r" + theme.getInfoColor().getStringColor() + b;
            
            if (b.contains("SNOWY")) {
                list.add(addCommand(createStack(name, Material.SNOW), Action.LEFT_CLICK, command));
            } else if (b.contains("BAMBOO_JUNGLE") || b.equalsIgnoreCase("BAMBOO_JUNGLE")) {
                list.add(addCommand(createStack(name, Material.BAMBOO), Action.LEFT_CLICK, command));
            } else if (b.contains("FROZEN")) {
                list.add(addCommand(createStack(name, Material.ICE), Action.LEFT_CLICK, command));
            } //override biome
            else if (b.contains("BADLANDS") || b.equalsIgnoreCase("BADLANDS")) {
                list.add(addCommand(createStack(name, Material.TERRACOTTA), Action.LEFT_CLICK, command));
            } else if (b.equalsIgnoreCase("BEACH") || b.contains("DESERT") || b.equalsIgnoreCase("DESERT")) {
                list.add(addCommand(createStack(name, Material.SAND), Action.LEFT_CLICK, command));
            } else if (b.contains("BIRCH")) {
                list.add(addCommand(createStack(name, Material.BIRCH_LOG), Action.LEFT_CLICK, command));
            } else if (b.contains("OCEAN") || b.equalsIgnoreCase("OCEAN") || b.equalsIgnoreCase("RIVER")) { // WARM!LUKEWARM
                if (b.contains("WARM") && !b.contains("LUKEWARM")) {
                    list.add(addCommand(createStack(name, Material.BRAIN_CORAL), Action.LEFT_CLICK, command));
                } else {
                    list.add(addCommand(createStack(name, Material.WATER_BUCKET), Action.LEFT_CLICK, command));
                }
            } else if (b.contains("DARK_FOREST") || b.equalsIgnoreCase("DARK_FOREST")) {
                list.add(addCommand(createStack(name, Material.DARK_OAK_LOG), Action.LEFT_CLICK, command));
            } else if (b.contains("END")) {
                list.add(addCommand(createStack(name, Material.END_STONE), Action.LEFT_CLICK, command));
            } else if (b.equalsIgnoreCase("SUNFLOWER_PLAINS")) {
                list.add(addCommand(createStack(name, Material.SUNFLOWER), Action.LEFT_CLICK, command));
            } else if (b.equalsIgnoreCase("FLOWER_FOREST")) {
                list.add(addCommand(createStack(name, Material.ROSE_BUSH), Action.LEFT_CLICK, command));
            } else if (b.equalsIgnoreCase("FOREST") || b.equalsIgnoreCase("WOODED_HILLS")) {
                list.add(addCommand(createStack(name, Material.OAK_LOG), Action.LEFT_CLICK, command));
            } else if (b.contains("TAIGA") || b.equalsIgnoreCase("TAIGA")) {
                list.add(addCommand(createStack(name, Material.SPRUCE_LOG), Action.LEFT_CLICK, command));
            } else if (b.contains("GRAVELLY")) {
                list.add(addCommand(createStack(name, Material.GRAVEL), Action.LEFT_CLICK, command));
            } else if (b.contains("ICE")) {
                list.add(addCommand(createStack(name, Material.PACKED_ICE), Action.LEFT_CLICK, command));
            } else if (b.contains("JUNGLE") || b.equalsIgnoreCase("JUNGLE")) {
                list.add(addCommand(createStack(name, Material.JUNGLE_LOG), Action.LEFT_CLICK, command));
            } else if (b.equalsIgnoreCase("MOUNTAIN_EDGE") || b.equalsIgnoreCase("PLAINS")) {
                list.add(addCommand(createStack(name, Material.GRASS_BLOCK), Action.LEFT_CLICK, command));
            } else if (b.contains("MUSHROOM")) {
                list.add(addCommand(createStack(name, Material.RED_MUSHROOM_BLOCK), Action.LEFT_CLICK, command));
            } else if (b.contains("NETHER") || b.equalsIgnoreCase("NETHER")) {
                list.add(addCommand(createStack(name, Material.NETHERRACK), Action.LEFT_CLICK, command));
            } else if (b.contains("SAVANNA") || b.equalsIgnoreCase("SAVANNA")) {
                list.add(addCommand(createStack(name, Material.ACACIA_LOG), Action.LEFT_CLICK, command));
            } else if (b.contains("STONE") || b.equalsIgnoreCase("WOODED_MOUNTAINS") || b.equalsIgnoreCase("MOUNTAINS")) {
                list.add(addCommand(createStack(name, Material.STONE), Action.LEFT_CLICK, command));
            } else if (b.contains("SWAMP") || b.equalsIgnoreCase("SWAMP")) {
                list.add(addCommand(createStack(name, Material.VINE), Action.LEFT_CLICK, command));
            } else if (b.equalsIgnoreCase("THE_VOID")) {
                list.add(addCommand(createStack(name, Material.BARRIER), Action.LEFT_CLICK, command));
            } else if (b.equalsIgnoreCase("SOUL_SAND_VALLEY")) {
                list.add(addCommand(createStack(name, Material.SOUL_SAND), Action.LEFT_CLICK, command));
            } else if (b.contains("CRIMSON")) {
                list.add(addCommand(createStack(name, Material.CRIMSON_NYLIUM), Action.LEFT_CLICK, command));
            } else if (b.contains("WARPED")) {
                list.add(addCommand(createStack(name, Material.WARPED_NYLIUM), Action.LEFT_CLICK, command));
            } else if (b.equalsIgnoreCase("BASALT_DELTAS")) {
                list.add(addCommand(createStack(name, Material.BASALT), Action.LEFT_CLICK, command));
            } else {
                list.add(addCommand(createStack(name, Material.DIAMOND_BLOCK), Action.LEFT_CLICK, command));
            }
        }
        
        Inventory inv = getDynamicScrollableInventory(ColorTheme.getTheme(player), page, InventoryType.BIOME_TP, list, createBack(theme, BackType.MAIN, BackType.OWN, BackType.PUBLIC));
        inv.setItem(9, addCommand(createStack(ChatColor.YELLOW + "Random", Material.ELYTRA), Action.LEFT_CLICK, "biomeTP random"));
        inv.setItem(27, addCommand(createStack(ChatColor.YELLOW + "Presets", Material.OAK_BUTTON), Action.LEFT_CLICK, "biomeTP preset"));
        player.openInventory(inv);
    }
    
    public static void openBiomeTPPreset(Player player, int page) {
        ColorTheme theme = ColorTheme.getTheme(player);
        
        openDynamicScrollableInventory(player, page, InventoryType.BIOME_TP_PRESETS,
                BiomeTP.BiomeTPPresets.getItems(player), createBack(theme, BackType.BIOME_TP, BackType.OWN, BackType.MAIN));
    }
    
    public static void openFeatureTP(Player player, int page) {
        ColorTheme theme = ColorTheme.getTheme(player);
        
        Inventory inv = getDynamicScrollableInventory(ColorTheme.getTheme(player), page, InventoryType.FEATURE_TP, Arrays.stream(FeatureTP.FeatureType.values())
                .map((f) -> addCommand(createStack("§r" + theme.getInfoColor() + f.name(), f.getItemStack().getType()), Action.LEFT_CLICK, "featureTP search " + f.name()))
                .collect(Collectors.toList()), createBack(theme, BackType.MAIN, BackType.OWN, BackType.PUBLIC));
        
        ItemStack is = new ItemStack(Material.OAK_BUTTON);
        ColorTheme ct = ColorTheme.getTheme(player);
        FeatureTP.FeatureTPMode mode = FeatureTP.getDefMode(player.getUniqueId());
        ItemMeta im = is.getItemMeta();
        if (im != null) {
            im.setDisplayName(ct.getInfoColor() + "Current mode: " + ct.getVarInfoColor() + mode.name());
            im.setLore(Collections.singletonList("§r" + ct.getInfoColor() + "Click to change to " + ct.getVarInfoColor() + mode.getNext().name()));
            
            addCommand(im, Action.LEFT_CLICK, "featureTP mode " + mode.getNext().name(), "featureTP");
            
            is.setItemMeta(im);
        }
        inv.setItem(18, is);
        player.openInventory(inv);
    }
    
    public static void openPublicTPortGUI(Player player, int page) {
        Files tportData = getFile("TPortData");
        //public.tports.<publicTPortSlot>.<TPortID>
        
        List<ItemStack> list = new ArrayList<>();
        
        for (String publicTPortSlot : tportData.getKeys("public.tports")) {
            String tportID = tportData.getConfig().getString("public.tports." + publicTPortSlot, TPortManager.defUUID.toString());
            
            TPort tport = getTPort(UUID.fromString(tportID));
            if (tport != null) {
                list.add(toPublicTPortItem(tport, player));
                if (tport.setPublicTPort(true)) {
                    tport.save();
                }
            } else {
                int publicSlotTmp = Integer.parseInt(publicTPortSlot) + 1;
                String tportID2 = tportData.getConfig().getString("public.tports." + publicSlotTmp, TPortManager.defUUID.toString());
                
                TPort tport2 = getTPort(UUID.fromString(tportID2));
                if (tport2 != null) {
                    list.add(toPublicTPortItem(tport2, player));
                    if (tport2.setPublicTPort(true)) {
                        tport2.save();
                    }
                }
                
                while (true) {
                    if (tportData.getConfig().contains("public.tports." + publicSlotTmp)) {
                        String publicTPort = tportData.getConfig().getString("public.tports." + publicSlotTmp, TPortManager.defUUID.toString());
                        tportData.getConfig().set("public.tports." + (publicSlotTmp - 1), publicTPort);
                        tportData.getConfig().set("public.tports." + (publicSlotTmp), null);
                        publicSlotTmp++;
                    } else {
                        break;
                    }
                }
                tportData.saveConfig();
            }
        }
        
        ColorTheme theme = ColorTheme.getTheme(player);
        openDynamicScrollableInventory(player, page, InventoryType.PUBLIC, list, createBack(theme, BackType.MAIN, BackType.OWN, BackType.PUBLIC));
    }
    
    public static void openSearchGUI(Player player, int page, TPortInventories tportInventories) {
        openSearchGUI(player, page, tportInventories.getContent(), tportInventories.getSearchedQuery(), tportInventories.searcher, tportInventories.searchMode, false);
    }
    
    public static void openSearchGUI(Player player, int page, Search.SearchMode searchMode, String searcherName, @Nonnull String query) {
        Search.Searchers.Searcher searcher = Search.Searchers.getSearcher(searcherName);
        if (searcher == null) {
            sendErrorTheme(player, "Could not find the Search type %s", searcherName);
            return;
        }
        
        openSearchGUI(player, page, searcher.search(searchMode, query, player), query, searcherName, searchMode, true);
    }
    
    private static void openSearchGUI(Player player, int page, List<ItemStack> searched, String query, String searcher, Search.SearchMode searchMode, boolean updateCooldown) {
        Inventory inv = getDynamicScrollableInventory(ColorTheme.getTheme(player), page, InventoryType.SEARCH,
                searched, createBack(ColorTheme.getTheme(player), BackType.MAIN, BackType.OWN, BackType.PUBLIC));
        
        TPortInventories tportInventories = (TPortInventories) inv.getHolder();
        tportInventories.content = searched;
        tportInventories.searchedQuery = query;
        tportInventories.searcher = null;
        tportInventories.searchMode = null;
        
        ColorTheme theme = ColorTheme.getTheme(player);
        
        ItemStack searchData = new ItemStack(Material.OAK_BUTTON);
        ItemMeta im = searchData.getItemMeta();
        if (im != null) {
            im.setDisplayName(theme.getVarInfoColor() + "Search Data");
    
            ArrayList<String> lore = new ArrayList<>();
            lore.add("§r" + theme.getInfoColor() + "Search type: " + theme.getVarInfoColor() + searcher);
            if (!query.isEmpty())
                lore.add("§r" + theme.getInfoColor() + "Search query: " + theme.getVarInfoColor() + query);
            if (searchMode != null)
                lore.add("§r" + theme.getInfoColor() + "Search mode: " + theme.getVarInfoColor() + searchMode);
            im.setLore(lore);
            searchData.setItemMeta(im);
        }
        inv.setItem(0, searchData);
        
        player.openInventory(inv);
        if (updateCooldown) CooldownManager.Search.update(player);
    }
    
    public static void openDynamicScrollableInventory(Player player, int page, InventoryType type, List<ItemStack> items, @Nullable ItemStack backButton) {
        player.openInventory(getDynamicScrollableInventory(ColorTheme.getTheme(player), page, type, items, backButton));
    }
    
    public static Inventory getDynamicScrollableInventory(ColorTheme theme, int page, InventoryType type, List<ItemStack> items, @Nullable ItemStack backButton) {
        int size = items.size();
        
        int rows = 3; //amount of rows
        int width = 7; //amount of items in a row
        int skipPerPage = 7; //amount of items skipped per page ( width * (rows to skip) )
        
        //add max to GUI page
        if (size > rows * width) {
            page = Math.min(size / width - rows + (size % width == 0 ? 0 : 1), page);
        } else {
            page = 0;
        }
        page = Math.max(0, page); //set min to page
        
        size += (width - (size - 1) % width); //calculate square rectangle items
        size /= width; //get amount of rows
        if (size > rows) {
            size -= (size - rows); //set a max on 3 rows
        }
        size *= 9; //turn rows into slots
        size += 18; //add top and bottom row
        
        TPortInventories tportInventories = new TPortInventories(type, page);
        Inventory inv = Bukkit.createInventory(tportInventories, size, type.getTitle() + " (" + (page + 1) + ")");
        tportInventories.inv = inv;
        
        int a = page * skipPerPage; //amount to skip
        
        int slot = 9 + (9 - width) / 2;
        for (int index = a; index < a + width * rows && index < items.size(); index++) {
            if ((slot + 1) % 9 == 0) { //end of row, +2 to go to next row
                slot += (9 - width);
            }
            if (slot >= size - 9) { //end of items
                break;
            }
            inv.setItem(slot, items.get(index)); //add item
            slot++; //next slot
        }
        
        if (backButton != null) {
            inv.setItem(size / 18 * 9 + 8, backButton);
        }
        
        if ((page + rows) * width < items.size()) { //if not all items could be displayed, add next 'button'
            ItemStack is = new ItemStack(Material.HOPPER);
            
            ItemMeta im = is.getItemMeta();
            if (im != null) {
                im.setDisplayName(NEXT);
                
                List<String> lore = new ArrayList<>();
                lore.add("§r" + theme.getInfoColor() + "Left-Click:" + theme.getVarInfoColor() + " +1");
                lore.add("§r" + theme.getInfoColor() + "Right-Click:" + theme.getVarInfoColor() + " +" + rows);
                lore.add("§r" + theme.getInfoColor() + "Middle-Click:" + theme.getVarInfoColor() + " go to end");
                im.setLore(lore);
                PersistentDataContainer pdc = im.getPersistentDataContainer();
                pdc.set(new NamespacedKey(Main.getInstance(), "pageNumber"), PersistentDataType.INTEGER, page + 1);
                pdc.set(new NamespacedKey(Main.getInstance(), "pageNumberSkip"), PersistentDataType.INTEGER, page + rows);
                pdc.set(new NamespacedKey(Main.getInstance(), "pageNumberEnd"), PersistentDataType.INTEGER,
                        items.size() / width - rows + (items.size() % width == 0 ? 0 : 1));
                is.setItemMeta(im);
            }
            
            inv.setItem(size - 1, is);
        }
        if (page != 0) { //if not at page 0 (1 as display) add previous 'button'
            ItemStack is = new ItemStack(Material.FERN);
            
            ItemMeta im = is.getItemMeta();
            if (im != null) {
                im.setDisplayName(PREVIOUS);
                
                List<String> lore = new ArrayList<>();
                lore.add("§r" + theme.getInfoColor() + "Left-Click:" + theme.getVarInfoColor() + " -1");
                lore.add("§r" + theme.getInfoColor() + "Right-Click:" + theme.getVarInfoColor() + " -" + rows);
                lore.add("§r" + theme.getInfoColor() + "Middle-Click:" + theme.getVarInfoColor() + " go to begin");
                im.setLore(lore);
                
                PersistentDataContainer pdc = im.getPersistentDataContainer();
                pdc.set(new NamespacedKey(Main.getInstance(), "pageNumber"), PersistentDataType.INTEGER, page - 1);
                pdc.set(new NamespacedKey(Main.getInstance(), "pageNumberSkip"), PersistentDataType.INTEGER, page - rows);
                pdc.set(new NamespacedKey(Main.getInstance(), "pageNumberEnd"), PersistentDataType.INTEGER, 0);
                
                is.setItemMeta(im);
            }
            
            inv.setItem(8, is);
        }
        return inv;
    }
    
    public int getPage() {
        return page;
    }
    
    public InventoryType getType() {
        return type;
    }
    
    public List<ItemStack> getContent() {
        return content;
    }
    
    public String getSearchedQuery() {
        return searchedQuery;
    }
    
    @Override
    @Nonnull
    public Inventory getInventory() {
        return inv;
    }
    
    public String getAddendum() {
        return addendum;
    }
    
    public static class InventoryType {
        public static final InventoryType MAIN = new InventoryType("Choose a player", 1);
        public static final InventoryType TPORT = new InventoryType("TPorts: ", 2);
        public static final InventoryType BIOME_TP = new InventoryType("Select a Biome", 3);
        public static final InventoryType BIOME_TP_PRESETS = new InventoryType("Select a BiomeTP preset", 4);
        public static final InventoryType FEATURE_TP = new InventoryType("Select a Feature", 5);
        public static final InventoryType PUBLIC = new InventoryType("Select a Public TPort", 6);
        public static final InventoryType SEARCH = new InventoryType("TPort Search", 7);
        
        private String title;
        private final int id;
        
        public InventoryType(String title, int id) {
            this.title = title;
            this.id = id;
        }
        
        public String getTitle() {
            return title;
        }
        
        public void setTitle(String title) {
            this.title = title;
        }
        
        public int getId() {
            return id;
        }
        
        @Override
        public boolean equals(Object o) {
            if (o instanceof InventoryType) {
                return ((InventoryType) o).id == this.id;
            } else {
                return false;
            }
        }
    }
    
    private enum BackType {
        MAIN("Main GUI", " "),
        OWN("Own TPort GUI", "own"),
        PUBLIC("Public TPort GUI", "public"),
        BIOME_TP("BiomeTP", "biomeTP");
        
        private final String name;
        private final String command;
        
        BackType(String name, String command) {
            this.name = name;
            this.command = command;
        }
        
        public String getName() {
            return name;
        }
        
        public String getCommand() {
            return command;
        }
    }
    
    public enum QuickEditType {
        PRIVATE("Private Statement", (tport, player) -> {
            TPortCommand.executeInternal(player, new String[]{"edit", tport.getName(), "private", tport.getPrivateStatement().getNext().name()});
        }),
        RANGE("Range", (tport, player) -> {
            int range = tport.getRange();
            for (int r : Arrays.asList(50, 100, 250, 500)) {
                if (r > range) {
                    TPortCommand.executeInternal(player, new String[]{"edit", tport.getName(), "range", String.valueOf(r)});
                    return;
                }
            }
            TPortCommand.executeInternal(player, new String[]{"edit", tport.getName(), "range", "0"});
        }),
        MOVE("Swap", (tport, player) -> {
            UUID otherTPortID = quickEditMoveList.get(player.getUniqueId());
            if (otherTPortID == null) {
                quickEditMoveList.put(player.getUniqueId(), tport.getTportID());
            } else {
                quickEditMoveList.remove(player.getUniqueId());
                if (!otherTPortID.equals(tport.getTportID())) {
                    TPortCommand.executeInternal(player, new String[]{"edit", TPortManager.getTPort(player.getUniqueId(), otherTPortID).getName(), "move", tport.getName()});
                }
            }
        }),
        LOG("Log", (tport, player) -> {
            TPortCommand.executeInternal(player, new String[]{"log", "default", tport.getName(), tport.getDefaultLogMode().getNext().name()});
        }),
        NOTIFY("Notify", (tport, player) -> {
            TPortCommand.executeInternal(player, new String[]{"log", "notify", tport.getName(), tport.getNotifyMode().getNext().name()});
        }),
        DYNMAP_SHOW("Dynmap Show", (tport, player) -> {
            if (DynmapHandler.isEnabled()) {
                TPortCommand.executeInternal(player, new String[]{"edit", tport.getName(), "dynmap", "show", String.valueOf(!tport.showOnDynmap())});
            } else {
                DynmapHandler.sendDisableError(player);
            }
        }),
        DYNMAP_ICON("Dynmap Icon", (tport, player) -> {
            if (DynmapHandler.isEnabled()) {
                String iconName = DynmapHandler.getTPortIconName(tport);
                List<Pair<String, String>> list = DynmapHandler.getIcons();
                if (list != null) {
                    for (Iterator<Pair<String, String>> iterator = list.iterator(); iterator.hasNext(); ) {
                        Pair<String, String> icon = iterator.next();
                        if (icon.getRight().equalsIgnoreCase(iconName)) {
                            if (iterator.hasNext()) {
                                TPortCommand.executeInternal(player, new String[]{"edit", tport.getName(), "dynmap", "icon", iterator.next().getRight()});
                            } else {
                                TPortCommand.executeInternal(player, new String[]{"edit", tport.getName(), "dynmap", "icon", list.get(0).getRight()});
                            }
                        }
                    }
                }
            } else {
                DynmapHandler.sendDisableError(player);
            }
        });
        
        private final Run editor;
        private final String displayName;
        
        QuickEditType(String displayName, Run run) {
            this.editor = run;
            this.displayName = displayName;
        }
        
        public static void clearData(UUID player) {
            quickEditMoveList.remove(player);
        }
        
        public static QuickEditType get(@Nullable String name) {
            try {
                return QuickEditType.valueOf(name != null ? name.toUpperCase() : PRIVATE.name());
            } catch (IllegalArgumentException | NullPointerException iae) {
                return PRIVATE;
            }
        }
        
        public void edit(TPort tport, Player player) {
            this.editor.edit(tport, player);
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public QuickEditType getNext() {
            boolean next = false;
            for (QuickEditType type : values()) {
                if (type.equals(this)) {
                    next = true;
                } else if (next) {
                    return type;
                }
            }
            return Arrays.asList(values()).get(0);
        }
        
        @FunctionalInterface
        private interface Run {
            void edit(TPort tport, Player player);
        }
    }
}
