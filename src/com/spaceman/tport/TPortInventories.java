package com.spaceman.tport;

import com.spaceman.tport.colorFormatter.ColorTheme;
import com.spaceman.tport.commands.TPortCommand;
import com.spaceman.tport.commands.tport.BiomeTP;
import com.spaceman.tport.commands.tport.FeatureTP;
import com.spaceman.tport.commands.tport.pltp.Offset;
import com.spaceman.tport.fileHander.Files;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.apache.commons.lang.Validate;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

import static com.spaceman.tport.Main.getOrDefault;
import static com.spaceman.tport.commands.TPortCommand.getHead;
import static com.spaceman.tport.commands.tport.Back.getPrevLocName;
import static com.spaceman.tport.events.InventoryClick.*;
import static com.spaceman.tport.fileHander.GettingFiles.getFile;
import static com.spaceman.tport.permissions.PermissionHandler.hasPermission;
import static com.spaceman.tport.tport.TPortManager.getTPort;

public class TPortInventories {
    
    public static HashMap<UUID, Integer> mainTPortGUIPage = new HashMap<>();
    public static HashMap<UUID, UUID> quickEditPublicMoveList = new HashMap<>();
    private static HashMap<UUID, UUID> quickEditMoveList = new HashMap<>();
    
    private static ItemStack toTPortItem(TPort tport, Player player) {
        Files tportData = getFile("TPortData");
        ItemStack is = tport.getItem();
        ItemMeta im = is.getItemMeta();
        
        ColorTheme theme = ColorTheme.getTheme(player);
        im.setDisplayName(theme.getVarInfoColor() + tport.getName());
        
        List<String> lore = new ArrayList<>();
        
        if (tport.hasDescription()) {
            for (String s : tport.getDescription().split("\\\\n")) {
                lore.add(ChatColor.BLUE + s);
            }
            lore.add("");
        }
        
        lore.add(theme.getInfoColor() + "Private Statement: " + tport.getPrivateStatement().getDisplayName());
        lore.add(theme.getInfoColor() + "Range: " + theme.getVarInfoColor() + tport.getRange());
        lore.add(theme.getInfoColor() + "Public TPort: " + theme.getVarInfoColor() + tport.isPublicTPort());
        lore.add(theme.getInfoColor() + "Default LogMode: " + theme.getVarInfoColor() + tport.getDefaultLogMode().name());
        lore.add(theme.getInfoColor() + "Notify mode: " + theme.getVarInfoColor() + tport.getNotifyMode().name());
        
        for (Enchantment e : im.getEnchants().keySet()) {
            im.removeEnchant(e);
        }
        
        if (tport.getOwner().equals(player.getUniqueId())) {
            if (tport.getTportID().equals(quickEditMoveList.get(player.getUniqueId()))) {
                Glow.addGlow(im);
            }
            if (tport.isOffered()) {
                lore.add("");
                lore.add(theme.getInfoColor() + "Offered to: " + theme.getVarInfoColor() + PlayerUUID.getPlayerName(tport.getOfferedTo()));
            }
            lore.add("");
            QuickEditType type = QuickEditType.get(tportData.getConfig().getString("tport." + player.getUniqueId() + ".editState"));
            lore.add(theme.getInfoColor() + "Editing: " + theme.getVarInfoColor() + type.getDisplayName());
            lore.add(theme.getInfoColor() + "Press middle-click to edit " + theme.getVarInfoColor() + type.getNext().getDisplayName());
        }
        
        im.setLore(lore);
        
        is.setItemMeta(im);
        return is;
    }
    
    private static ItemStack toPublicTPortItem(TPort tport, Player player) {
        ItemStack is = tport.getItem();
        ItemMeta im = is.getItemMeta();
        
        ColorTheme theme = ColorTheme.getTheme(player);
        im.setDisplayName(theme.getVarInfoColor() + tport.getName());
        
        List<String> lore = new ArrayList<>();
        
        lore.add(theme.getInfoColor() + "TPort Owner: " + theme.getVarInfoColor() + PlayerUUID.getPlayerName(tport.getOwner()));
        lore.add("");
        
        if (tport.hasDescription()) {
            for (String s : tport.getDescription().split("\\\\n")) {
                lore.add(ChatColor.BLUE + s);
            }
            lore.add("");
        }
        
        lore.add(theme.getInfoColor() + "Private Statement: " + tport.getPrivateStatement().getDisplayName());
        lore.add(theme.getInfoColor() + "Range: " + theme.getVarInfoColor() + tport.getRange());
        lore.add(theme.getInfoColor() + "Public TPort: " + theme.getVarInfoColor() + tport.isPublicTPort());
        lore.add(theme.getInfoColor() + "Default LogMode: " + theme.getVarInfoColor() + tport.getDefaultLogMode().name());
        lore.add(theme.getInfoColor() + "Notify mode: " + theme.getVarInfoColor() + tport.getNotifyMode().name());
        
        for (Enchantment e : im.getEnchants().keySet()) {
            im.removeEnchant(e);
        }
        
        if (hasPermission(player, false, "TPort.public.move", "TPort.admin.public")) {
            if (tport.getTportID().equals(quickEditPublicMoveList.get(player.getUniqueId()))) {
                Glow.addGlow(im);
            }
        }
        
        im.setLore(lore);
        
        is.setItemMeta(im);
        return is;
    }
    
    private static ItemStack createStack(String displayName, Material material) {
        ItemStack is = new ItemStack(material);
        
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(displayName);
        is.setItemMeta(im);
        
        return is;
    }
    
    public static void openMainTPortGUI(Player player, int page) {
        Files tportData = getFile("TPortData");
        //noinspection unchecked
        ArrayList<String> playerList = (ArrayList<String>) new ArrayList<>(tportData.getKeys("tport")).clone();
        
        List<ItemStack> list = new ArrayList<>();
        
        list.add(getOrDefault(getHead(player), new ItemStack(Material.AIR)));
        playerList.remove(player.getUniqueId().toString());
        for (String playerUUID : playerList) {
            list.add(getOrDefault(getHead(UUID.fromString(playerUUID)), new ItemStack(Material.AIR)));
        }
        openDynamicScrollableInventory(player, page, "Choose a player", list, null);
    }
    
    public static void openTPortGUI(UUID newPlayerUUID, Player player) {
        
        String newPlayerName = PlayerUUID.getPlayerName(newPlayerUUID);
        
        Validate.notNull(newPlayerName, "The newPlayerName can not be null");
        Validate.notNull(newPlayerUUID, "The newPlayerUUID can not be null");
        Validate.notNull(player, "The player can not be null");
        
        Files tportData = getFile("TPortData");
        Inventory inv = Bukkit.createInventory(null, 27, "TPort: " + newPlayerName);
        int slotOffset = 0;
        for (int i = 0; i <= TPortSize; i++) {
            
            if (i == 8 || i == 16/*16 because of the slot+slotOffset*/) {
                slotOffset++;
            }
            
            TPort tport = TPortManager.getTPort(newPlayerUUID, i);
            if (tport != null) {
                inv.setItem(i + slotOffset, toTPortItem(tport, player));
            }
            ColorTheme theme = ColorTheme.getTheme(player);
            ItemStack back = new ItemStack(Material.BARRIER);
            ItemMeta metaBack = back.getItemMeta();
            metaBack.setDisplayName(BACK);
            ArrayList<String> backLore = new ArrayList<>();
            backLore.add(theme.getInfoColor() + "Left-Click:");
            backLore.add(theme.getVarInfoColor() + "Main GUI");
            backLore.add("");
            backLore.add(theme.getInfoColor() + "Right-Click:");
            backLore.add(theme.getVarInfoColor() + "Public TPort GUI");
            backLore.add("");
            backLore.add(theme.getInfoColor() + "Middle-Click:");
            backLore.add(theme.getVarInfoColor() + "Own TPot GUI");
            metaBack.setLore(backLore);
            back.setItemMeta(metaBack);
            inv.setItem(26, back);
            
            ItemStack extraTP = new ItemStack(Material.ELYTRA);
            ItemMeta metaExtraTP = extraTP.getItemMeta();
            
            ArrayList<String> extraTPLore = new ArrayList<>();
            extraTPLore.add(theme.getInfoColor() + "Left-Click:");
            extraTPLore.add(getPrevLocName(player));
            extraTPLore.add("");
            extraTPLore.add(theme.getInfoColor() + "Right-Click:");
            extraTPLore.add(theme.getVarInfoColor() + "BiomeTP");
            extraTPLore.add("");
            extraTPLore.add(theme.getInfoColor() + "Middle-Click:");
            extraTPLore.add(theme.getVarInfoColor() + "FeatureTP");
            metaExtraTP.setLore(extraTPLore);
            metaExtraTP.setDisplayName(ChatColor.YELLOW + "Extra TP features");
            
            extraTP.setItemMeta(metaExtraTP);
            inv.setItem(17, extraTP);
    
            boolean pltpState = tportData.getConfig().getBoolean("tport." + newPlayerUUID.toString() + ".tp.statement", true);
            if (newPlayerUUID.equals(player.getUniqueId())) {
                ItemStack warp = new ItemStack(Material.PLAYER_HEAD);
    
                SkullMeta skin = (SkullMeta) warp.getItemMeta();
                skin.setOwningPlayer(Bukkit.getOfflinePlayer(newPlayerUUID));
                
                boolean pltpConsent = tportData.getConfig().getBoolean("tport." + newPlayerUUID.toString() + ".tp.consent", false);
                Offset.PLTPOffset pltpOffset = Offset.getPLTPOffset(player);
                
                skin.setDisplayName(theme.getInfoColor() + "Edit your PLTP settings");
                
                skin.setLore(Arrays.asList(
                        theme.getInfoColor() + "PLTP state is set to " + theme.getVarInfoColor() + pltpState,
                        theme.getInfoColor() + "PLTP consent is set to " + theme.getVarInfoColor() + pltpConsent,
                        theme.getInfoColor() + "PLTP offset is set to " + theme.getVarInfoColor() + pltpOffset.name(),
                        "",
                        theme.getInfoColor() + "When left clicking your PLTP state will be turned to " + theme.getVarInfoColor() + !pltpState,
                        theme.getInfoColor() + "When right clicking your PLTP consent will be turned to " + theme.getVarInfoColor() + !pltpConsent,
                        theme.getInfoColor() + "When middle clicking your PLTP offset will be turned to " + theme.getVarInfoColor() + pltpOffset.getNext()));
                
                warp.setItemMeta(skin);
                inv.setItem(8, warp);
            } else {
                ItemStack warp = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta skin = (SkullMeta) warp.getItemMeta();
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
                warp.setItemMeta(skin);
                inv.setItem(8, warp);
            }
        }
        player.openInventory(inv);
    }
    
    public static void openBiomeTP(Player player, int page) {
        List<ItemStack> list = new ArrayList<>();
        for (Biome biome : Biome.values()) {
            
            String b = biome.toString();
            if (b.contains("SNOWY")) {
                list.add(createStack(b, Material.SNOW));
            } else if (b.contains("BAMBOO_JUNGLE") || b.equalsIgnoreCase("BAMBOO_JUNGLE")) {
                list.add(createStack(b, Material.BAMBOO));
            } else if (b.contains("FROZEN")) {
                list.add(createStack(b, Material.ICE));
            } //override biome
            else if (b.contains("BADLANDS") || b.equalsIgnoreCase("BADLANDS")) {
                list.add(createStack(b, Material.TERRACOTTA));
            } else if (b.equalsIgnoreCase("BEACH") || b.contains("DESERT") || b.equalsIgnoreCase("DESERT")) {
                list.add(createStack(b, Material.SAND));
            } else if (b.contains("BIRCH")) {
                list.add(createStack(b, Material.BIRCH_LOG));
            } else if (b.contains("OCEAN") || b.equalsIgnoreCase("OCEAN") || b.equalsIgnoreCase("RIVER")) { // WARM!LUKEWARM
                if (b.contains("WARM") && !b.contains("LUKEWARM")) {
                    list.add(createStack(b, Material.BRAIN_CORAL));
                } else {
                    list.add(createStack(b, Material.WATER_BUCKET));
                }
            } else if (b.contains("DARK_FOREST") || b.equalsIgnoreCase("DARK_FOREST")) {
                list.add(createStack(b, Material.DARK_OAK_LOG));
            } else if (b.contains("END")) {
                list.add(createStack(b, Material.END_STONE));
            } else if (b.equalsIgnoreCase("SUNFLOWER_PLAINS")) {
                list.add(createStack(b, Material.SUNFLOWER));
            } else if (b.equalsIgnoreCase("FLOWER_FOREST")) {
                list.add(createStack(b, Material.ROSE_BUSH));
            } else if (b.equalsIgnoreCase("FOREST") || b.equalsIgnoreCase("WOODED_HILLS")) {
                list.add(createStack(b, Material.OAK_LOG));
            } else if (b.contains("TAIGA") || b.equalsIgnoreCase("TAIGA")) {
                list.add(createStack(b, Material.SPRUCE_LOG));
            } else if (b.contains("GRAVELLY")) {
                list.add(createStack(b, Material.GRAVEL));
            } else if (b.contains("ICE")) {
                list.add(createStack(b, Material.PACKED_ICE));
            } else if (b.contains("JUNGLE") || b.equalsIgnoreCase("JUNGLE")) {
                list.add(createStack(b, Material.JUNGLE_LOG));
            } else if (b.equalsIgnoreCase("MOUNTAIN_EDGE") || b.equalsIgnoreCase("PLAINS")) {
                list.add(createStack(b, Material.GRASS_BLOCK));
            } else if (b.contains("MUSHROOM")) {
                list.add(createStack(b, Material.RED_MUSHROOM_BLOCK));
            } else if (b.contains("NETHER") || b.equalsIgnoreCase("NETHER")) {
                list.add(createStack(b, Material.NETHERRACK));
            } else if (b.contains("SAVANNA") || b.equalsIgnoreCase("SAVANNA")) {
                list.add(createStack(b, Material.ACACIA_LOG));
            } else if (b.contains("STONE") || b.equalsIgnoreCase("WOODED_MOUNTAINS") || b.equalsIgnoreCase("MOUNTAINS")) {
                list.add(createStack(b, Material.STONE));
            } else if (b.contains("SWAMP") || b.equalsIgnoreCase("SWAMP")) {
                list.add(createStack(b, Material.VINE));
            } else if (b.equalsIgnoreCase("THE_VOID")) {
                list.add(createStack(b, Material.BARRIER));
            } else {
                list.add(createStack(b, Material.DIAMOND_BLOCK));
            }
        }
        
        ColorTheme theme = ColorTheme.getTheme(player);
        
        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta metaBack = back.getItemMeta();
        metaBack.setDisplayName(BACK);
        ArrayList<String> backLore = new ArrayList<>();
        backLore.add(theme.getInfoColor() + "Left-Click:");
        backLore.add(theme.getVarInfoColor() + "Main GUI");
        backLore.add("");
        backLore.add(theme.getInfoColor() + "Right-Click:");
        backLore.add(theme.getVarInfoColor() + "Own TPort GUI");
        backLore.add("");
        backLore.add(theme.getInfoColor() + "Middle-Click:");
        backLore.add(theme.getVarInfoColor() + "Public TPort GUI");
        metaBack.setLore(backLore);
        back.setItemMeta(metaBack);
        
        Inventory inv = getDynamicScrollableInventory(page, "Select a Biome", list, back);
        inv.setItem(9, createStack(ChatColor.YELLOW + "Random", Material.ELYTRA));
        inv.setItem(27, createStack(ChatColor.YELLOW + "Presets", Material.ELYTRA));
        player.openInventory(inv);
    }
    
    public static void openBiomeTPPreset(Player player, int page) {
        ColorTheme theme = ColorTheme.getTheme(player);
        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta metaBack = back.getItemMeta();
        metaBack.setDisplayName(BACK);
        ArrayList<String> backLore = new ArrayList<>();
        backLore.add(theme.getInfoColor() + "Left-Click:");
        backLore.add(theme.getVarInfoColor() + "BiomeTP");
        backLore.add("");
        backLore.add(theme.getInfoColor() + "Right-Click:");
        backLore.add(theme.getVarInfoColor() + "Own TPort GUI");
        backLore.add("");
        backLore.add(theme.getInfoColor() + "Middle-Click:");
        backLore.add(theme.getVarInfoColor() + "Main GUI");
        metaBack.setLore(backLore);
        back.setItemMeta(metaBack);
        openDynamicScrollableInventory(player, page, "Select a BiomeTP preset", BiomeTP.BiomeTPPresets.getItems(player), back);
    }
    
    public static void openFeatureTP(Player player, int page) {
        ColorTheme theme = ColorTheme.getTheme(player);
        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta metaBack = back.getItemMeta();
        metaBack.setDisplayName(BACK);
        ArrayList<String> backLore = new ArrayList<>();
        backLore.add(theme.getInfoColor() + "Left-Click:");
        backLore.add(theme.getVarInfoColor() + "Main GUI");
        backLore.add("");
        backLore.add(theme.getInfoColor() + "Right-Click:");
        backLore.add(theme.getVarInfoColor() + "Own TPort GUI");
        backLore.add("");
        backLore.add(theme.getInfoColor() + "Middle-Click:");
        backLore.add(theme.getVarInfoColor() + "Public TPort GUI");
        metaBack.setLore(backLore);
        back.setItemMeta(metaBack);
        
        Inventory inv = getDynamicScrollableInventory(page, "Select a Feature", Arrays.stream(FeatureTP.FeatureType.values())
                .map((f) -> createStack(theme.getInfoColor() + f.name(), f.getItemStack().getType())).collect(Collectors.toList()), back);
    
        ItemStack is = new ItemStack(Material.ELYTRA);
        ItemMeta im = is.getItemMeta();
        ColorTheme ct = ColorTheme.getTheme(player);
        FeatureTP.FeatureTPMode mode = FeatureTP.getDefMode(player.getUniqueId());
        im.setDisplayName(ct.getInfoColor() + "Current mode: " + ct.getVarInfoColor() + mode.name());
        im.setLore(Arrays.asList(ct.getInfoColor() + "Click to change to " + ct.getVarInfoColor() + mode.getNext().name()));
        is.setItemMeta(im);
        inv.setItem(18, is);
        player.openInventory(inv);
    }
    
    public static void openPublicTPortGUI(Player player, int page) {
        Files tportData = getFile("TPortData");
        //public.tports.<publicTPortSlot>.<TPortID;ownerUUID>
        
        List<ItemStack> list = new ArrayList<>();
        
        Collection<String> publicTPortsSlots = tportData.getKeys("public.tports");
        for (String publicTPortSlot : publicTPortsSlots) {
            String tportID = tportData.getConfig().getString("public.tports." + publicTPortSlot, TPortManager.defUUID.toString());
            
            TPort tport = getTPort(UUID.fromString(tportID));
            if (tport != null) {
                list.add(toPublicTPortItem(tport, player));
                if (tport.setPublicTPort(true)) {
                    tport.save();
                }
            }
        }
        ColorTheme theme = ColorTheme.getTheme(player);
        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta metaBack = back.getItemMeta();
        metaBack.setDisplayName(BACK);
        ArrayList<String> backLore = new ArrayList<>();
        backLore.add(theme.getInfoColor() + "Left-Click:");
        backLore.add(theme.getVarInfoColor() + "Main GUI");
        backLore.add("");
        backLore.add(theme.getInfoColor() + "Right-Click:");
        backLore.add(theme.getVarInfoColor() + "Own TPort GUI");
        backLore.add("");
        backLore.add(theme.getInfoColor() + "Middle-Click:");
        backLore.add(theme.getVarInfoColor() + "Public TPort GUI (page 1)");
        metaBack.setLore(backLore);
        back.setItemMeta(metaBack);
        openDynamicScrollableInventory(player, page, "Select a Public TPort", list, back);
    }
    
    public static void openDynamicScrollableInventory(Player player, int page, String name, List<ItemStack> items, @Nullable ItemStack backButton) {
        player.openInventory(getDynamicScrollableInventory(page, name, items, backButton));
    }
    
    public static Inventory getDynamicScrollableInventory(int page, String name, List<ItemStack> items, @Nullable ItemStack backButton) {
        int size = items.size();
        
        int rows = 3; //amount of rows
        int width = 7; //amount of items in a row
        int skipPerPage = 7; //amount of items skipped per page (width * rows to skip)
        
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
        
        Inventory inv = Bukkit.createInventory(null, size, name + " (" + (page + 1) + ")");
        
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
            inv.setItem(size - 1, createStack(NEXT, Material.HOPPER));
        }
        if (page != 0) { //if not at page 0 (1 as display) add previous 'button'
            inv.setItem(8, createStack(PREVIOUS, Material.FERN));
        }
        return inv;
    }
    
    public enum QuickEditType {
        PRIVATE("Private Statement", (tport, player) -> {
            TPortCommand.executeInternal(player, new String[]{"edit", tport.getName(), "private",
                    tport.getPrivateStatement().getNext().name()
            });
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
        });
        
        private Run editor;
        private String displayName;
        
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
