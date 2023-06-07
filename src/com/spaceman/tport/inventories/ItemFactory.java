package com.spaceman.tport.inventories;

import com.google.gson.JsonObject;
import com.spaceman.tport.Glow;
import com.spaceman.tport.Main;
import com.spaceman.tport.commands.TPortCommand;
import com.spaceman.tport.commands.tport.Features;
import com.spaceman.tport.commands.tport.MainLayout;
import com.spaceman.tport.commands.tport.Sort;
import com.spaceman.tport.commands.tport.publc.Move;
import com.spaceman.tport.dynmap.DynmapHandler;
import com.spaceman.tport.fancyMessage.Keybinds;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.MessageUtils;
import com.spaceman.tport.fancyMessage.TextType;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fancyMessage.inventories.FancyClickEvent;
import com.spaceman.tport.fancyMessage.inventories.FancyInventory;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;

import javax.annotation.Nullable;
import java.util.*;

import static com.spaceman.tport.commands.TPortCommand.getPlayerData;
import static com.spaceman.tport.commands.tport.SafetyCheck.SafetyCheckSource.*;
import static com.spaceman.tport.commands.tport.Sort.getSorter;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatInfoTranslation;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatTranslation;
import static com.spaceman.tport.fancyMessage.inventories.FancyClickEvent.*;
import static com.spaceman.tport.fancyMessage.language.Language.getPlayerLang;
import static com.spaceman.tport.fileHander.Files.tportData;
import static com.spaceman.tport.inventories.QuickEditInventories.openQuickEditSelection;
import static com.spaceman.tport.inventories.SettingsInventories.openRemovePlayerConfirmationGUI;
import static com.spaceman.tport.inventories.TPortInventories.*;
import static org.bukkit.event.inventory.ClickType.LEFT;
import static org.bukkit.event.inventory.ClickType.RIGHT;
import static org.bukkit.persistence.PersistentDataType.STRING;

public class ItemFactory {
    
    public enum HeadAttributes {
        TPORT_AMOUNT,  //shows the amount of TPorts owned by the player
        CLICK_EVENTS,  //shows the standard click event messages (open TPort GUI, PLTP, preview, ect)
        SELECT_TPORT,  //shows the message to select a TPort (used for home selection)
        REMOVE_PLAYER, //shows the message to remove this Player (used for Remove Player)
        CONFIRM_REMOVE_PLAYER //shows the message to confirm remove this Player (used for Remove Player)
    }
    public static ItemStack getHead(UUID head, Player player, HeadAttributes... attributes) {
        return getHead(Bukkit.getOfflinePlayer(head), player, attributes);
    }
    public static ItemStack getHead(OfflinePlayer head, Player player, HeadAttributes... attributes) {
        if (head == null) {
            return null;
        }
        List<HeadAttributes> attributesList = List.of(attributes);
        
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        
        ColorTheme theme = ColorTheme.getTheme(player.getUniqueId());
        
        String displayTitle = head.getName() == null ? head.getUniqueId().toString() : head.getName();
        Message title = formatTranslation(ColorTheme.ColorType.titleColor, ColorTheme.ColorType.titleColor, "tport.command.headDisplay.title", displayTitle);
        
        List<Message> lore;
        if (attributesList.contains(HeadAttributes.TPORT_AMOUNT)) {
            lore = getPlayerData(head.getUniqueId());
        } else {
            lore = new ArrayList<>();
        }
        
        if (attributesList.contains(HeadAttributes.SELECT_TPORT)) {
            FancyClickEvent.addCommand(item, ClickType.LEFT, "tport open " + head.getName());
            lore.add(new Message());
            lore.add(formatInfoTranslation("tport.command.headDisplay.selectTPort_forHome", ClickType.LEFT));
            FancyClickEvent.addCommand(item, ClickType.LEFT, "tport home set " + head.getName());
        }
        
        if (attributesList.contains(HeadAttributes.CLICK_EVENTS)) {
            lore.add(new Message());
            lore.add(formatInfoTranslation("tport.command.headDisplay.leftClick", ClickType.LEFT));
            FancyClickEvent.addCommand(item, ClickType.LEFT, "tport open " + head.getName());
            
            if (head.isOnline()) {
                lore.add(formatInfoTranslation("tport.command.headDisplay.whenOnline.PLTP", ClickType.RIGHT, head.getName()));
                FancyClickEvent.addCommand(item, ClickType.RIGHT, "tport pltp tp " + head.getName());
                
                lore.add(formatInfoTranslation("tport.command.headDisplay.whenOnline.preview",
                        textComponent(Keybinds.DROP, varInfoColor).setType(TextType.KEYBIND), head.getName()));
                FancyClickEvent.addCommand(item, ClickType.DROP, "tport preview " + head.getName());
                
                if (DynmapHandler.isEnabled()) {
                    lore.add(formatInfoTranslation("tport.command.headDisplay.whenOnline.dynmapSearch", ClickType.CONTROL_DROP, head.getName()));
                    FancyClickEvent.addCommand(item, ClickType.CONTROL_DROP, "tport dynmap search " + head.getName());
                }
            }
        }
        
        if (attributesList.contains(HeadAttributes.REMOVE_PLAYER)) {
            lore.add(new Message());
            setStringData(item, new NamespacedKey(Main.getInstance(), "toRemoveUUID"), head.getUniqueId().toString());;
            addFunction(item, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
                NamespacedKey toRemoveUUIDKey = new NamespacedKey(Main.getInstance(), "toRemoveUUID");
                if (pdc.has(toRemoveUUIDKey, STRING)) {
                    UUID toRemoveUUID = UUID.fromString(pdc.get(toRemoveUUIDKey, STRING));
                    openRemovePlayerConfirmationGUI(whoClicked, toRemoveUUID);
                }
            }));
        }
        
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        title = MessageUtils.translateMessage(title, playerLang);
        lore = MessageUtils.translateMessage(lore, playerLang);
        MessageUtils.setCustomItemData(item, theme, title, lore);
        
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        if (meta != null) {
            meta.setOwningPlayer(head);
            item.setItemMeta(meta);
        }
        return item;
    }
    
    public static void onQuickEdit(Player whoClicked, PersistentDataContainer pdc, FancyInventory fancyInventory, QuickEditInventories.QuickEditType quickEditType) {
        //quick edit
        UUID tportUUID = null;
        boolean fromQuickEditSelection = false;
        
        //from item
        NamespacedKey tportUUIDKey = new NamespacedKey(Main.getInstance(), "tportUUID");
        if (pdc.has(tportUUIDKey, STRING)) {
            //noinspection ConstantConditions
            tportUUID = UUID.fromString(pdc.get(tportUUIDKey, STRING));
        }
        
        //from inventory
        if (fancyInventory.hasData("tportUUID")) {
            tportUUID = fancyInventory.getData("tportUUID", UUID.class);
            fromQuickEditSelection = true;
        }
        
        if (tportUUID == null) {
            return;
        }
        
        TPort quickEditTPort = TPortManager.getTPort(whoClicked.getUniqueId(), tportUUID);
        if (quickEditTPort != null) {
            boolean reopen = quickEditType.edit(quickEditTPort, whoClicked, fancyInventory);
            if (reopen) {
                if (fromQuickEditSelection) openQuickEditSelection(whoClicked, 0, tportUUID);
                else openTPortGUI(whoClicked.getUniqueId(), whoClicked, fancyInventory);
            }
        }
    }
    
    public enum TPortItemAttributes {
        ADD_OWNER,  //add the owner name at the top
        QUICK_EDITOR, //add the quick edit
        CLICK_TO_OPEN,  //add the teleport, inverted safetyCheck and preview events
        CLICK_TO_OPEN_PUBLIC,  //add the move, teleport, inverted safetyCheck and preview events for the public GUI
        SELECT_HOME
    }
    public static ItemStack toTPortItem(TPort tport, Player player, TPortItemAttributes... attributes) {
        return toTPortItem(tport, player, null, attributes);
    }
    public static ItemStack toTPortItem(TPort tport, Player player, @Nullable FancyInventory prevWindow, TPortItemAttributes... attributes) {
        ItemStack is = tport.getItem();
        ItemMeta im = is.getItemMeta();
        
        if (im == null) {
            return is;
        }
        
        FancyClickEvent.removeAllFunctions(im);
        List<TPortItemAttributes> attributesList = List.of(attributes);
        
        ColorTheme theme = ColorTheme.getTheme(player);
        
        im.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "tportUUID"), STRING, tport.getTportID().toString());
        for (Enchantment e : im.getEnchants().keySet()) {
            im.removeEnchant(e);
        }
        
        Message title = formatTranslation(infoColor, varInfoColor, "tport.tportInventories.tportName", tport.getName());
        
        List<Message> lore = tport.getHoverData(attributesList.contains(TPortItemAttributes.ADD_OWNER));
        if (tport.getOwner().equals(player.getUniqueId()) && attributesList.contains(TPortItemAttributes.QUICK_EDITOR)) {
            lore.add(new Message());
            QuickEditInventories.QuickEditType type = QuickEditInventories.QuickEditType.getForPlayer(player.getUniqueId());
            lore.add(formatInfoTranslation("tport.tport.tport.hoverData.editing", ClickType.RIGHT, type.getDisplayName()));
            lore.add(formatInfoTranslation("tport.tport.tport.hoverData.buttons", ClickType.SHIFT_RIGHT));
            
            if (prevWindow != null && tport.getTportID().equals(prevWindow.getData("TPortToMove", UUID.class))) {
                Glow.addGlow(im);
            }
            
            FancyClickEvent.addFunction(im, ClickType.RIGHT, ((whoClicked, clickType, pdc, fancyInventory) -> {
                QuickEditInventories.QuickEditType quickEditType = QuickEditInventories.QuickEditType.getForPlayer(whoClicked.getUniqueId());
                onQuickEdit(whoClicked, pdc, fancyInventory, quickEditType);
            }));
            FancyClickEvent.addFunction(im, ClickType.SHIFT_RIGHT, (whoClicked, clickType, pdc, fancyInventory) -> {
                NamespacedKey tportUUIDKey = new NamespacedKey(Main.getInstance(), "tportUUID");
                if (pdc.has(tportUUIDKey, STRING)) {
                    //noinspection ConstantConditions
                    TPort quickEditTPort = TPortManager.getTPort(whoClicked.getUniqueId(), UUID.fromString(pdc.get(tportUUIDKey, STRING)));
                    if (quickEditTPort != null) {
                        openQuickEditSelection(whoClicked, 0, quickEditTPort.getTportID());
                    }
                }
            });
        }
        
        if (attributesList.contains(TPortItemAttributes.CLICK_TO_OPEN)) {
            Boolean safetyState = null; // null when player has no permission
            if (tport.getOwner().equals(player.getUniqueId())) {
                if (TPORT_OWN.hasPermission(player, false)) {
                    safetyState = TPORT_OWN.getState(player);
                }
            } else {
                if (TPORT_OPEN.hasPermission(player, false)) {
                    safetyState = TPORT_OPEN.getState(player);
                }
            }
            
            lore.add(new Message());
            lore.add(formatInfoTranslation("tport.tport.tport.hoverData.teleportSelected", LEFT));
            addCommand(im, LEFT, "tport open " + PlayerUUID.getPlayerName(tport.getOwner()) + " " + tport.getName());
            
            if (safetyState != null) {
                lore.add(formatInfoTranslation("tport.tport.tport.hoverData.invertSafetyCheck", ClickType.SHIFT_LEFT, !safetyState));
                addCommand(im, ClickType.SHIFT_LEFT,
                        "tport open " + PlayerUUID.getPlayerName(tport.getOwner()) + " " + tport.getName() + " " + !safetyState);
            }
            
            lore.add(formatInfoTranslation("tport.tport.tport.hoverData.preview", ClickType.DROP));
            addCommand(im, ClickType.DROP, "tport preview " + PlayerUUID.getPlayerName(tport.getOwner()) + " " + tport.getName());
            
            if (DynmapHandler.isEnabled()) {
                lore.add(formatInfoTranslation("tport.tport.tport.hoverData.dynmapSearch", ClickType.CONTROL_DROP));
                addCommand(im, ClickType.CONTROL_DROP, "tport dynmap search " + PlayerUUID.getPlayerName(tport.getOwner()) + " " + tport.getName());
            }
        }
        
        if (attributesList.contains(TPortItemAttributes.CLICK_TO_OPEN_PUBLIC)) {
            Boolean safetyState = null;
            if (TPORT_PUBLIC.hasPermission(player, false)) {
                safetyState = TPORT_PUBLIC.getState(player);
            }
            
            addCommand(im, LEFT, "tport public open " + tport.getName());
            addCommand(im, ClickType.DROP, "tport preview " + PlayerUUID.getPlayerName(tport.getOwner()) + " " + tport.getName());
            if (safetyState != null) addCommand(im, ClickType.SHIFT_LEFT, "tport public open " + tport.getName() + " " + !safetyState);
            FancyClickEvent.addFunction(im, ClickType.RIGHT, ((whoClicked, clickType, pdc, fancyInventory) -> {
                NamespacedKey keyUUID = new NamespacedKey(Main.getInstance(), "tportUUID");
                if (pdc.has(keyUUID, STRING)) {
                    if (!Move.getInstance().emptySlot.hasPermissionToRun(whoClicked, false)) {
                        return;
                    }
                    
                    //noinspection ConstantConditions
                    TPort toMoveTPort = TPortManager.getTPort(UUID.fromString(pdc.get(keyUUID, STRING)));
                    if (toMoveTPort == null) {
                        return;
                    }
                    UUID moveToTPort = fancyInventory.getData("TPortToMove", UUID.class, null);
                    if (moveToTPort == null) {
                        fancyInventory.setData("TPortToMove", toMoveTPort.getTportID());
                        openPublicTPortGUI(whoClicked, fancyInventory.getData("page", Integer.class, 0), fancyInventory);
                    } else {
                        fancyInventory.setData("TPortToMove", null);
                        if (!moveToTPort.equals(toMoveTPort.getTportID())) {
                            TPort tmpTPort = TPortManager.getTPort(moveToTPort);
                            if (tmpTPort != null) {
                                TPortCommand.executeTPortCommand(whoClicked, new String[]{"public", "move", tmpTPort.getName(), toMoveTPort.getName()});
                            }
                        }
                        openPublicTPortGUI(whoClicked, fancyInventory.getData("page", Integer.class, 0), fancyInventory);
                    }
                }
            }));
            
            lore.add(new Message());
            lore.add(formatInfoTranslation("tport.tport.tport.hoverData.teleportSelected", LEFT));
            if (safetyState != null) lore.add(formatInfoTranslation("tport.tport.tport.hoverData.invertSafetyCheck", ClickType.SHIFT_LEFT, !safetyState));
            lore.add(formatInfoTranslation("tport.tport.tport.hoverData.preview", ClickType.DROP));
    
            if (Move.getInstance().emptySlot.hasPermissionToRun(player, false) && prevWindow != null) {
                if (tport.getTportID().equals(prevWindow.getData("TPortToMove", UUID.class))) {
                    Glow.addGlow(im);
                }
                lore.add(formatInfoTranslation("tport.tport.tport.hoverData.publicMove", RIGHT, tport));
            }
        }
        
        if (attributesList.contains(TPortItemAttributes.SELECT_HOME)) {
            lore.add(new Message());
            lore.add(formatInfoTranslation("tport.tport.tport.hoverData.selectAsHome", LEFT));
            addCommand(im, LEFT, "tport home set " + PlayerUUID.getPlayerName(tport.getOwner()) + " " + tport.getName(), "tport");
        }
        
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        title = MessageUtils.translateMessage(title, playerLang);
        lore = MessageUtils.translateMessage(lore, playerLang);
    
        is.setItemMeta(im);
        MessageUtils.setCustomItemData(is, theme, title, lore);
        return is;
    }
    
    protected static ItemStack createBack(Player player, @Nullable BackType left, @Nullable BackType right, @Nullable BackType shiftRight) {
        return createBack(player, left, right, shiftRight, null);
    }
    protected static ItemStack createBack(Player player, @Nullable BackType left, @Nullable BackType right, @Nullable BackType shiftRight, @Nullable BackType notAllowed) {
        ItemStack is = back_model.getItem(player);
        
        if (!Features.Feature.PublicTP.isEnabled()) {
            if (BackType.PUBLIC.equals(left)) left = BackType.BIOME_TP;
            if (BackType.PUBLIC.equals(right)) right = BackType.BIOME_TP;
            if (BackType.PUBLIC.equals(shiftRight)) shiftRight = BackType.BIOME_TP;
        }
        if (!Features.Feature.BiomeTP.isEnabled()) {
            if (BackType.BIOME_TP.equals(left)) left = BackType.FEATURE_TP;
            if (BackType.BIOME_TP.equals(right)) right = BackType.FEATURE_TP;
            if (BackType.BIOME_TP.equals(shiftRight)) shiftRight = BackType.FEATURE_TP;
        }
        if (!Features.Feature.FeatureTP.isEnabled()) {
            if (BackType.FEATURE_TP.equals(left)) left = BackType.WORLD_TP;
            if (BackType.FEATURE_TP.equals(right)) right = BackType.WORLD_TP;
            if (BackType.FEATURE_TP.equals(shiftRight)) shiftRight = BackType.WORLD_TP;
        }
        if (!Features.Feature.WorldTP.isEnabled()) {
            if (Features.Feature.PublicTP.isEnabled()) {
                if (BackType.WORLD_TP.equals(left)) left = BackType.PUBLIC;
                if (BackType.WORLD_TP.equals(right)) right = BackType.PUBLIC;
                if (BackType.WORLD_TP.equals(shiftRight)) shiftRight = BackType.PUBLIC;
            } else {
                if (BackType.WORLD_TP.equals(left)) left = null;
                if (BackType.WORLD_TP.equals(right)) right = null;
                if (BackType.WORLD_TP.equals(shiftRight)) shiftRight = null;
            }
        }
        if (left != null && left.equals(notAllowed)) left = null;
        if (right != null && right.equals(notAllowed)) right = null;
        if (shiftRight != null && shiftRight.equals(notAllowed)) shiftRight = null;
        
        Message title = formatTranslation(titleColor, titleColor, "tport.tportInventories.backButton.name");
        ArrayList<Message> lore = new ArrayList<>();
        if (left != null) {
            lore.add(formatInfoTranslation("tport.tportInventories.backButton.format.leftClick", LEFT));
            lore.add(left.getName());
            lore.add(new Message());
        }
        if (right != null) {
            lore.add(formatInfoTranslation("tport.tportInventories.backButton.format.rightClick", ClickType.RIGHT));
            lore.add(right.getName());
            lore.add(new Message());
        }
        if (shiftRight != null) {
            lore.add(formatInfoTranslation("tport.tportInventories.backButton.format.shiftRightClick", ClickType.SHIFT_RIGHT));
            lore.add(shiftRight.getName());
            lore.add(new Message());
        }
        if (!lore.isEmpty()) lore.remove(lore.size() -1);
        
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        title = MessageUtils.translateMessage(title, playerLang);
        lore = (ArrayList<Message>) MessageUtils.translateMessage(lore, playerLang);
        
        ColorTheme theme = ColorTheme.getTheme(player);
        MessageUtils.setCustomItemData(is, theme, title, lore);
        
        if (left != null) addFunction(is, LEFT, left.getFunction());
        if (right != null) addFunction(is, ClickType.RIGHT, right.getFunction());
        if (shiftRight != null) addFunction(is, ClickType.SHIFT_RIGHT, shiftRight.getFunction());
        return is;
    }
    protected enum BackType {
        MAIN(((whoClicked, clickType, pdc, fancyInventory) -> TPortCommand.executeTPortCommand(whoClicked, ""))),
        OWN(((whoClicked, clickType, pdc, fancyInventory) -> TPortCommand.executeTPortCommand(whoClicked, "own"))),
        PUBLIC(((whoClicked, clickType, pdc, fancyInventory) -> TPortCommand.executeTPortCommand(whoClicked, "public"))), //TPort.public.open OR TPort.basic
        BIOME_TP(((whoClicked, clickType, pdc, fancyInventory) -> TPortCommand.executeTPortCommand(whoClicked, "biomeTP"))), //TPort.biomeTP.open
        FEATURE_TP(((whoClicked, clickType, pdc, fancyInventory) -> TPortCommand.executeTPortCommand(whoClicked, "featureTP"))), //TPort.featureTP.open
        WORLD_TP(((whoClicked, clickType, pdc, fancyInventory) -> TPortCommand.executeTPortCommand(whoClicked, "world"))), //TPort.world.tp
        SETTINGS(((whoClicked, clickType, pdc, fancyInventory) -> TPortCommand.executeTPortCommand(whoClicked, "settings"))),
        COLOR_THEME(((whoClicked, clickType, pdc, fancyInventory) -> TPortCommand.executeTPortCommand(whoClicked, "colorTheme"))),
        HOME_SET(((whoClicked, clickType, pdc, fancyInventory) -> TPortCommand.executeTPortCommand(whoClicked, "home set"))), //TPort.home.set OR TPort.basic
        BACKUP(((whoClicked, clickType, pdc, fancyInventory) -> TPortCommand.executeTPortCommand(whoClicked, "backup"))),
        QUICK_EDIT(((whoClicked, clickType, pdc, fancyInventory) -> QuickEditInventories.openQuickEditSelection(whoClicked, 0, fancyInventory.getData("tportUUID", UUID.class)))),
        TPORT_LOG(((whoClicked, clickType, pdc, fancyInventory) -> QuickEditInventories.openTPortLogGUI(whoClicked, fancyInventory.getData("tport", TPort.class))));
        
        private final FancyClickEvent.FancyClickRunnable onClick;
        
        BackType(FancyClickEvent.FancyClickRunnable onClick) {
            this.onClick = onClick;
        }
        
        public Message getName() {
            return formatTranslation(varInfoColor, varInfoColor, "tport.tportInventories.backType." + name() + ".description");
        }
        
        public FancyClickEvent.FancyClickRunnable getFunction() {
            return onClick;
        }
    }
    
    public static List<ItemStack> getPlayerList(Player player, boolean hasOwn, boolean overrideShowTPorts_toFalse, List<HeadAttributes> headAttributes, List<TPortItemAttributes> tportItemAttributes) {
        List<ItemStack> list = getSorter(player).sort(player, headAttributes.toArray(new HeadAttributes[]{}));
        
        ItemStack ownHead = null;
        for (ItemStack is : list) {
            if (!(is.getItemMeta() instanceof SkullMeta skullMeta)) {
                continue;
            }
            OfflinePlayer offlinePlayer = skullMeta.getOwningPlayer();
            if (offlinePlayer == null) {
                continue;
            }
            skullMeta.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "playerName"), STRING, offlinePlayer.getName());
            skullMeta.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "playerUUID"), STRING, offlinePlayer.getUniqueId().toString());
            is.setItemMeta(skullMeta);
            
            if (skullMeta.getOwningPlayer().getUniqueId().equals(player.getUniqueId())) {
                ownHead = is;
            }
        }
        
        if (ownHead != null) { //remove player and set him first
            list.remove(ownHead);
            if (hasOwn) list.add(0, ownHead);
        }
        
        ArrayList<ItemStack> newList = new ArrayList<>();
        for (ItemStack is : list) {
            if (MainLayout.showPlayers(player)) {
                newList.add(is);
            }
            if (!overrideShowTPorts_toFalse && MainLayout.showTPorts(player)) {
                if (!(is.getItemMeta() instanceof SkullMeta sm)) {
                    continue;
                }
                if (sm.getOwningPlayer() != null) {
                    TPortManager.getSortedTPortList(tportData, sm.getOwningPlayer().getUniqueId()).stream()
                            .filter(Objects::nonNull).map(tport -> toTPortItem(tport, player, tportItemAttributes.toArray(new TPortItemAttributes[]{}))).forEach(newList::add);
                }
            }
        }
        list = newList;
        
        return list;
    }
    
    public static ItemStack getSortingItem(Player player, JsonObject playerLang, ColorTheme theme, FancyClickEvent.FancyClickRunnable invCreator) {
        String nextSort = Sort.getNextSorterName(player);
        
        ItemStack sorting = (nextSort == null ? sorting_grayed_model : sorting_model).getItem(player);
        
        Message sortingTitle = formatTranslation(titleColor, titleColor, "tport.tportInventories.openMainGUI.sorting.title");
        sortingTitle.translateMessage(playerLang);
        
        Message sortingCurrent = formatInfoTranslation(playerLang, "tport.tportInventories.openMainGUI.sorting.current", Sort.getSorterForPlayer(player));
        
        String previousSort = (nextSort == null ? null : Sort.getPreviousSorterName(player));
        Message sortingNext = (nextSort == null ? null : formatInfoTranslation(playerLang, "tport.tportInventories.openMainGUI.sorting.next", LEFT, nextSort));
        Message sortingPrevious = (previousSort == null ? null : formatInfoTranslation(playerLang, "tport.tportInventories.openMainGUI.sorting.previous", ClickType.RIGHT, previousSort));
        
        MessageUtils.setCustomItemData(sorting, theme, sortingTitle, Arrays.asList(sortingCurrent, new Message(), sortingNext, sortingPrevious));
        
        if (nextSort != null) {
            addCommand(sorting, LEFT, "tport sort " + nextSort);
            addFunction(sorting, LEFT, invCreator);
        }
        if (previousSort != null) {
            addCommand(sorting, ClickType.RIGHT, "tport sort " + previousSort);
            addFunction(sorting, RIGHT, invCreator);
        }
        
        return sorting;
    }
}
