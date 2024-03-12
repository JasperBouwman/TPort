package com.spaceman.tport.inventories;

import com.google.gson.JsonObject;
import com.spaceman.tport.Glow;
import com.spaceman.tport.Main;
import com.spaceman.tport.Pair;
import com.spaceman.tport.commands.TPortCommand;
import com.spaceman.tport.commands.tport.Features;
import com.spaceman.tport.commands.tport.MainLayout;
import com.spaceman.tport.commands.tport.Sort;
import com.spaceman.tport.commands.tport.pltp.Whitelist;
import com.spaceman.tport.commands.tport.publc.Move;
import com.spaceman.tport.commands.tport.publc.Remove;
import com.spaceman.tport.dynmap.DynmapHandler;
import com.spaceman.tport.fancyMessage.Keybinds;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.MessageUtils;
import com.spaceman.tport.fancyMessage.TextType;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation;
import com.spaceman.tport.fancyMessage.inventories.FancyClickEvent;
import com.spaceman.tport.fancyMessage.inventories.FancyInventory;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.spaceman.tport.commands.TPortCommand.executeTPortCommand;
import static com.spaceman.tport.commands.TPortCommand.getPlayerData;
import static com.spaceman.tport.commands.tport.SafetyCheck.SafetyCheckSource.*;
import static com.spaceman.tport.commands.tport.Sort.getSorter;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatInfoTranslation;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatTranslation;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;
import static com.spaceman.tport.fancyMessage.encapsulation.TPortEncapsulation.asTPort;
import static com.spaceman.tport.fancyMessage.inventories.FancyClickEvent.*;
import static com.spaceman.tport.fancyMessage.inventories.FancyInventory.pageDataName;
import static com.spaceman.tport.fancyMessage.language.Language.getPlayerLang;
import static com.spaceman.tport.fileHander.Files.tportData;
import static com.spaceman.tport.inventories.QuickEditInventories.*;
import static com.spaceman.tport.inventories.SettingsInventories.*;
import static com.spaceman.tport.inventories.TPortInventories.*;
import static com.spaceman.tport.tport.TPort.tportDataName;
import static com.spaceman.tport.tport.TPort.tportUUIDDataName;
import static org.bukkit.event.inventory.ClickType.*;
import static org.bukkit.persistence.PersistentDataType.STRING;

public class ItemFactory {
    
    public enum HeadAttributes {
        TPORT_AMOUNT,  //shows the amount of TPorts owned by the player
        CLICK_EVENTS,  //shows the standard click event messages (open TPort GUI, PLTP, preview, ect)
        HOME_PLAYER_SELECTION,  //shows the message to select a TPort (used for home selection)
        OFFER_TO_PLAYER, //used for offering a TPort to player
        TPORT_WHITELIST, //used for TPort whitelist selection
        PLTP_WHITELIST, //used for PLTP whitelist selection
        TPORT_LOG_SELECTION, //used for TPort logging selection
        TPORT_LOG_READ, //used for TPort read log
        TPORT_LOG_READ_FILTER, //used for TPort read log, filter selection
        REMOVE_PLAYER; //shows the message to remove this Player (used for Remove Player)
    }
    public static ItemStack getHead(UUID head, Player player, List<HeadAttributes> attributes, @Nullable Object headData) {
        return getHead(Bukkit.getOfflinePlayer(head), player, attributes, headData);
    }
    public static ItemStack getHead(OfflinePlayer headOwner, Player player, List<HeadAttributes> attributes, @Nullable Object headData) {
        if (headOwner == null) {
            return null;
        }
        
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        
        ColorTheme theme = ColorTheme.getTheme(player);
        
        String displayTitle = headOwner.getName() == null ? headOwner.getUniqueId().toString() : headOwner.getName();
        Message title = formatTranslation(ColorTheme.ColorType.titleColor, ColorTheme.ColorType.titleColor, "tport.inventories.itemFactory.getHead.title", displayTitle);
        
        List<Message> lore;
        if (attributes.contains(HeadAttributes.TPORT_AMOUNT)) {
            lore = getPlayerData(headOwner.getUniqueId());
        } else {
            lore = new ArrayList<>();
        }
        
        if (attributes.contains(HeadAttributes.HOME_PLAYER_SELECTION)) {
            FancyClickEvent.addCommand(item, ClickType.LEFT, "tport open " + headOwner.getName());
            lore.add(new Message());
            lore.add(formatInfoTranslation("tport.inventories.itemFactory.getHead.home_player_selection", ClickType.LEFT));
            FancyClickEvent.addCommand(item, ClickType.LEFT, "tport home set " + headOwner.getName());
        }
        
        if (attributes.contains(HeadAttributes.CLICK_EVENTS)) {
            lore.add(new Message());
            lore.add(formatInfoTranslation("tport.inventories.itemFactory.getHead.click_events.clickToOpen", ClickType.LEFT));
            FancyClickEvent.addCommand(item, ClickType.LEFT, "tport open " + headOwner.getName());
            
            if (headOwner.isOnline()) {
                lore.add(formatInfoTranslation("tport.inventories.itemFactory.getHead.click_events.PLTP", ClickType.RIGHT, headOwner.getName()));
                FancyClickEvent.addCommand(item, ClickType.RIGHT, "tport pltp tp " + headOwner.getName());
                
                lore.add(formatInfoTranslation("tport.inventories.itemFactory.getHead.click_events.preview",
                        textComponent(Keybinds.DROP, varInfoColor).setType(TextType.KEYBIND), headOwner.getName()));
                FancyClickEvent.addCommand(item, ClickType.DROP, "tport preview " + headOwner.getName());
                
                if (DynmapHandler.isEnabled()) { //todo add BlueMap
                    lore.add(formatInfoTranslation("tport.inventories.itemFactory.getHead.click_events.dynmapSearch", ClickType.CONTROL_DROP, headOwner.getName()));
                    FancyClickEvent.addCommand(item, ClickType.CONTROL_DROP, "tport dynmap search " + headOwner.getName());
                }
            }
        }
        
        if (attributes.contains(HeadAttributes.OFFER_TO_PLAYER) && headData instanceof Pair) {
            Pair<TPort, Boolean> pair = (Pair<TPort, Boolean>) headData;
            TPort tport = pair.getLeft();
            Boolean fromQuickEdit = pair.getRight();
            lore.add(new Message());
            lore.add(formatInfoTranslation("tport.inventories.itemFactory.getHead.offer_to_player", LEFT, asTPort(tport), asPlayer(headOwner)));
            addCommand(item, LEFT, "tport transfer offer " + headOwner.getName() + " " + tport.getName());
            if (fromQuickEdit) {
                addFunction(item, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
                    openQuickEditSelection(whoClicked, 0, fancyInventory.getData(tportUUIDDataName));
                }));
            } else {
                addFunction(item, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
                    openTPortGUI(whoClicked.getUniqueId(), whoClicked);
                }));
            }
        }
        
        if (attributes.contains(HeadAttributes.REMOVE_PLAYER)) {
            lore.add(new Message());
            lore.add(formatInfoTranslation("tport.inventories.itemFactory.getHead.remove_player", LEFT));
            setStringData(item, new NamespacedKey(Main.getInstance(), "toRemoveUUID"), headOwner.getUniqueId().toString());
            addFunction(item, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
                NamespacedKey toRemoveUUIDKey = new NamespacedKey(Main.getInstance(), "toRemoveUUID");
                if (pdc.has(toRemoveUUIDKey, STRING)) {
                    UUID toRemoveUUID = UUID.fromString(pdc.get(toRemoveUUIDKey, STRING));
                    openRemovePlayerConfirmationGUI(whoClicked, toRemoveUUID);
                }
            }));
        }
        
        if (attributes.contains(HeadAttributes.TPORT_WHITELIST) && headData instanceof TPort tport) {
            lore.add(new Message());
            
            if (tport.getWhitelist().contains(headOwner.getUniqueId())) {
                FancyClickEvent.addFunction(item, LEFT, (whoClicked, clickType, innerPDC, fancyInventory) -> {
                    NamespacedKey innerPlayerNameKey = new NamespacedKey(Main.getInstance(), "playerName");
                    if (innerPDC.has(innerPlayerNameKey, PersistentDataType.STRING)) {
                        String innerPlayerName = innerPDC.get(innerPlayerNameKey, PersistentDataType.STRING);
                        String tportName = fancyInventory.getData(tportDataName).getName();
                        executeTPortCommand(whoClicked, "edit " + tportName + " whitelist remove " + innerPlayerName);
                        openTPortWhitelistSelectorGUI(whoClicked, fancyInventory.getData(pageDataName), fancyInventory);
                    }
                });
                lore.add(formatInfoTranslation("tport.quickEditInventories.openTPortWhitelistSelectionGUI.player.unselect", LEFT, headOwner.getName()));
            } else {
                FancyClickEvent.addFunction(item, LEFT, (whoClicked, clickType, innerPDC, fancyInventory) -> {
                    NamespacedKey innerPlayerNameKey = new NamespacedKey(Main.getInstance(), "playerName");
                    if (innerPDC.has(innerPlayerNameKey, PersistentDataType.STRING)) {
                        String innerPlayerName = innerPDC.get(innerPlayerNameKey, PersistentDataType.STRING);
                        String tportName = fancyInventory.getData(tportDataName).getName();
                        executeTPortCommand(whoClicked, "edit " + tportName + " whitelist add " + innerPlayerName);
                        openTPortWhitelistSelectorGUI(whoClicked, fancyInventory.getData(pageDataName), fancyInventory);
                    }
                });
                lore.add(formatInfoTranslation("tport.quickEditInventories.openTPortWhitelistSelectionGUI.player.select", LEFT, headOwner.getName()));
            }
        }
        
        if (attributes.contains(HeadAttributes.TPORT_LOG_SELECTION) && headData instanceof TPort tport) {
            boolean isPlayerLogged = tport.getLogged().contains(headOwner.getUniqueId());
            
            lore.add(new Message());
            
            Message currentState;
            if (isPlayerLogged) {
                currentState = formatInfoTranslation("tport.quickEditInventories.openTPortLogSelectionGUI.player.logState", tport.getLogMode(headOwner.getUniqueId()));
            } else {
                Message defaultState = formatInfoTranslation("tport.quickEditInventories.openTPortLogSelectionGUI.player.default");
                currentState = formatInfoTranslation("tport.quickEditInventories.openTPortLogSelectionGUI.player.defaultState", tport.getLogMode(headOwner.getUniqueId()), defaultState);
            }
            Message nextState = formatInfoTranslation("tport.quickEditInventories.openTPortLogSelectionGUI.player.nextLogState", LEFT, tport.getLogMode(headOwner.getUniqueId()).getNext());
            Message delete = !isPlayerLogged ? null : formatInfoTranslation("tport.quickEditInventories.openTPortLogSelectionGUI.player.remove", RIGHT, headOwner.getName());
            
            lore.add(currentState);
            lore.add(nextState);
            lore.add(delete);
            
            FancyClickEvent.addFunction(item, LEFT, (whoClicked, clickType, innerPDC, fancyInventory) -> {
                NamespacedKey innerPlayerNameKey = new NamespacedKey(Main.getInstance(), "playerName");
                NamespacedKey innerPlayerUUIDKey = new NamespacedKey(Main.getInstance(), "playerUUID");
                if (innerPDC.has(innerPlayerNameKey, PersistentDataType.STRING)) {
                    String innerPlayerName = innerPDC.get(innerPlayerNameKey, PersistentDataType.STRING);
                    UUID innerPlayerUUID = UUID.fromString(innerPDC.get(innerPlayerUUIDKey, PersistentDataType.STRING));
                    TPort innerTPort = fancyInventory.getData(tportDataName);
                    TPortCommand.executeTPortCommand(whoClicked, new String[]{"log", "add", innerTPort.getName(), innerPlayerName + ":" + innerTPort.getLogMode(innerPlayerUUID).getNext()});
                    openTPortLogSelectorGUI(whoClicked, fancyInventory.getData(pageDataName), fancyInventory);
                }
            });
            if (isPlayerLogged) {
                FancyClickEvent.addFunction(item, RIGHT, (whoClicked, clickType, innerPDC, fancyInventory) -> {
                    NamespacedKey innerPlayerNameKey = new NamespacedKey(Main.getInstance(), "playerName");
                    if (innerPDC.has(innerPlayerNameKey, PersistentDataType.STRING)) {
                        String innerPlayerName = innerPDC.get(innerPlayerNameKey, PersistentDataType.STRING);
                        TPort innerTPort = fancyInventory.getData(tportDataName);
                        TPortCommand.executeTPortCommand(whoClicked, new String[]{"log", "remove", innerTPort.getName(), innerPlayerName});
                        openTPortLogSelectorGUI(whoClicked, fancyInventory.getData(pageDataName), fancyInventory);
                    }
                });
            }
        }
        
        if (attributes.contains(HeadAttributes.TPORT_LOG_READ) && headData instanceof ImmutableTriple) {
            lore.add(new Message());
            
            ImmutableTriple<TPort, TPort.LogEntry, SimpleDateFormat> triple = (ImmutableTriple<TPort, TPort.LogEntry, SimpleDateFormat>) headData;
            
            Message timestamp = formatInfoTranslation("tport.quickEditInventories.openTPortLogReadGUI.log.time", triple.left, triple.right.format(triple.middle.timeOfTeleport().getTime()));
            Message logMode = triple.middle.loggedMode() == null ? null : formatInfoTranslation("tport.quickEditInventories.openTPortLogReadGUI.log.logMode", triple.middle.loggedMode());
            Message ownerOnline = triple.middle.ownerOnline() == null ? null : formatInfoTranslation("tport.quickEditInventories.openTPortLogReadGUI.log.ownerOnline." + triple.middle.ownerOnline(), triple.middle.ownerOnline());
            
            lore.add(timestamp);
            lore.add(logMode);
            lore.add(ownerOnline);
        }
        
        if (attributes.contains(HeadAttributes.TPORT_LOG_READ_FILTER)) {
            lore.add(new Message());
            lore.add(formatInfoTranslation("tport.quickEditInventories.openTPortLogRead_filterGUI.selectForFiltering", LEFT));
            
            addFunction(item, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
                openTPortLogReadGUI(whoClicked, fancyInventory.getData(tportDataName), headOwner.getUniqueId());
            }));
        }
        
        if (attributes.contains(HeadAttributes.PLTP_WHITELIST)) {
            ArrayList<String> pltpWhitelist = Whitelist.getPLTPWhitelist(player);
            setStringData(item, new NamespacedKey(Main.getInstance(), "playerName"), headOwner.getName());
            lore.add(new Message());
            
            if (pltpWhitelist.contains(headOwner.getUniqueId().toString())) {
                FancyClickEvent.addFunction(item, LEFT, (whoClicked, clickType, innerPDC, fancyInventory) -> {
                    NamespacedKey innerPlayerNameKey = new NamespacedKey(Main.getInstance(), "playerName");
                    if (innerPDC.has(innerPlayerNameKey, PersistentDataType.STRING) ) {
                        String innerPlayerName = innerPDC.get(innerPlayerNameKey, PersistentDataType.STRING);
                        executeTPortCommand(whoClicked, "pltp whitelist remove " + innerPlayerName);
                        openPLTPWhitelistSelectorGUI(whoClicked, fancyInventory.getData(pageDataName), fancyInventory);
                    }
                });
                lore.add(formatInfoTranslation("tport.tportInventories.openPLTPWhitelistSelectionGUI.player.unselect", LEFT, headOwner.getName()));
            } else {
                FancyClickEvent.addFunction(item, LEFT, (whoClicked, clickType, innerPDC, fancyInventory) -> {
                    NamespacedKey innerPlayerNameKey = new NamespacedKey(Main.getInstance(), "playerName");
                    if (innerPDC.has(innerPlayerNameKey, PersistentDataType.STRING) ) {
                        String innerPlayerName = innerPDC.get(innerPlayerNameKey, PersistentDataType.STRING);
                        executeTPortCommand(whoClicked, "pltp whitelist add " + innerPlayerName);
                        openPLTPWhitelistSelectorGUI(whoClicked, fancyInventory.getData(pageDataName), fancyInventory);
                    }
                });
                lore.add(formatInfoTranslation("tport.tportInventories.openPLTPWhitelistSelectionGUI.player.select", LEFT, headOwner.getName()));
            }
        }
        
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        title = MessageUtils.translateMessage(title, playerLang);
        lore = MessageUtils.translateMessage(lore, playerLang);
        MessageUtils.setCustomItemData(item, theme, title, lore);
        
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        if (meta != null) {
            meta.setOwningPlayer(headOwner);
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
        if (fancyInventory.hasData(tportUUIDDataName)) {
            tportUUID = fancyInventory.getData(tportUUIDDataName);
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
        QUICK_EDITOR, //add the quick edit (needs the FancyInventory for move)
        CLICK_TO_OPEN,  //add the teleport, inverted safetyCheck and preview events
        CLICK_TO_OPEN_PUBLIC,  //add the teleport, inverted safetyCheck and preview events for the public GUI (needs the FancyInventory for move)
        TRANSFER_OFFERS, //TPorts offered to the player
        TRANSFER_OFFERED, //own TPorts offered to other players
        PUBLIC_MOVE_DELETE, //add move and remove from Public TP settings
        LOG_DATA, //add the 'is logged' and link to the Quick Edit
        SELECT_HOME
    }
    public static ItemStack toTPortItem(TPort tport, Player player, List<TPortItemAttributes> attributes) {
        return toTPortItem(tport, player, attributes, null);
    }
    public static ItemStack toTPortItem(TPort tport, Player player, List<TPortItemAttributes> attributes, @Nullable Object extraData) {
        ItemStack is = tport.getItem();
        ItemMeta im = is.getItemMeta();
        
        if (im == null) {
            return is;
        }
        
        FancyClickEvent.removeAllFunctions(im);
        
        ColorTheme theme = ColorTheme.getTheme(player);
        
        im.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "tportUUID"), STRING, tport.getTportID().toString());
        im.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "tportName"), STRING, tport.getName());
        for (Enchantment e : im.getEnchants().keySet()) {
            im.removeEnchant(e);
        }
        for (ItemFlag itemFlag : ItemFlag.values()) {
            im.addItemFlags(itemFlag);
        }
        
        Message title = formatTranslation(infoColor, varInfoColor, "tport.inventories.itemFactory.toTPortItem.title", tport.getName());
        List<Message> lore = tport.getHoverData(attributes.contains(TPortItemAttributes.ADD_OWNER));
        
        if (tport.getOwner().equals(player.getUniqueId()) && attributes.contains(TPortItemAttributes.QUICK_EDITOR)) {
            FancyInventory prevWindow = null;
            if (extraData instanceof FancyInventory) {
                prevWindow = (FancyInventory) extraData;
            }
            
            lore.add(new Message());
            QuickEditInventories.QuickEditType type = QuickEditInventories.QuickEditType.getForPlayer(player.getUniqueId());
            lore.add(formatInfoTranslation("tport.inventories.itemFactory.toTPortItem.editing", ClickType.RIGHT, type.getDisplayName()));
            lore.add(formatInfoTranslation("tport.inventories.itemFactory.toTPortItem.buttons", ClickType.SHIFT_RIGHT));
            
            if (prevWindow != null) {
                if (tport.getTportID().equals(prevWindow.getData(tportToMoveDataName)) ||
                    tport.getTportID().equals(prevWindow.getData(whitelistCloneToDataName))) {
                    Glow.addGlow(im);
                }
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
        
        if (attributes.contains(TPortItemAttributes.CLICK_TO_OPEN)) {
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
            lore.add(formatInfoTranslation("tport.inventories.itemFactory.toTPortItem.teleportSelected", LEFT));
            addCommand(im, LEFT, "tport open " + PlayerUUID.getPlayerName(tport.getOwner()) + " " + tport.getName());
            
            if (safetyState != null) {
                lore.add(formatInfoTranslation("tport.inventories.itemFactory.toTPortItem.invertSafetyCheck", ClickType.SHIFT_LEFT, !safetyState));
                addCommand(im, ClickType.SHIFT_LEFT,
                        "tport open " + PlayerUUID.getPlayerName(tport.getOwner()) + " " + tport.getName() + " " + !safetyState);
            }
            
            lore.add(formatInfoTranslation("tport.inventories.itemFactory.toTPortItem.preview", ClickType.DROP));
            addCommand(im, ClickType.DROP, "tport preview " + PlayerUUID.getPlayerName(tport.getOwner()) + " " + tport.getName());
            
            if (DynmapHandler.isEnabled()) { //todo add BlueMap
                lore.add(formatInfoTranslation("tport.inventories.itemFactory.toTPortItem.dynmapSearch", ClickType.CONTROL_DROP));
                addCommand(im, ClickType.CONTROL_DROP, "tport dynmap search " + PlayerUUID.getPlayerName(tport.getOwner()) + " " + tport.getName());
            }
        }
        
        if (attributes.contains(TPortItemAttributes.CLICK_TO_OPEN_PUBLIC)) {
            Boolean safetyState = null;
            if (TPORT_PUBLIC.hasPermission(player, false)) {
                safetyState = TPORT_PUBLIC.getState(player);
            }
            
            addCommand(im, LEFT, "tport public open " + tport.getName());
            addCommand(im, ClickType.DROP, "tport preview " + PlayerUUID.getPlayerName(tport.getOwner()) + " " + tport.getName());
            if (safetyState != null) addCommand(im, ClickType.SHIFT_LEFT, "tport public open " + tport.getName() + " " + !safetyState);
            
            lore.add(new Message());
            lore.add(formatInfoTranslation("tport.inventories.itemFactory.toTPortItem.teleportSelected", LEFT));
            if (safetyState != null) lore.add(formatInfoTranslation("tport.inventories.itemFactory.toTPortItem.invertSafetyCheck", ClickType.SHIFT_LEFT, !safetyState));
            lore.add(formatInfoTranslation("tport.inventories.itemFactory.toTPortItem.preview", ClickType.DROP));
        }
        
        if (attributes.contains(TPortItemAttributes.PUBLIC_MOVE_DELETE)) {
            FancyInventory prevWindow = null;
            if (extraData instanceof FancyInventory) {
                prevWindow = (FancyInventory) extraData;
            }
            
            FancyClickEvent.addFunction(im, ClickType.LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
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
                    UUID moveToTPort = fancyInventory.getData(tportToMoveDataName);
                    if (moveToTPort == null) {
                        fancyInventory.setData(tportToMoveDataName, toMoveTPort.getTportID());
                        openPublicTPTPortsSettings(whoClicked, fancyInventory.getData(pageDataName), fancyInventory);
                    } else {
                        fancyInventory.setData(tportToMoveDataName, null);
                        if (!moveToTPort.equals(toMoveTPort.getTportID())) {
                            TPort tmpTPort = TPortManager.getTPort(moveToTPort);
                            if (tmpTPort != null) {
                                TPortCommand.executeTPortCommand(whoClicked, new String[]{"public", "move", tmpTPort.getName(), toMoveTPort.getName()});
                            }
                        }
                        openPublicTPTPortsSettings(whoClicked, fancyInventory.getData(pageDataName), fancyInventory);
                    }
                }
            }));
            
            lore.add(new Message());
            
            if (Move.getInstance().emptySlot.hasPermissionToRun(player, false)) {
                lore.add(formatInfoTranslation("tport.inventories.itemFactory.toTPortItem.publicMove", LEFT, tport));
                
                if (prevWindow != null && tport.getTportID().equals(prevWindow.getData(tportToMoveDataName))) {
                    Glow.addGlow(im);
                }
            }
            
            if (Remove.getInstance().emptyAll.hasPermissionToRun(player, false)) {
                lore.add(formatInfoTranslation("tport.inventories.itemFactory.toTPortItem.publicRemove", SHIFT_RIGHT, tport));
                addCommand(im, SHIFT_RIGHT, "tport public remove " + tport.getName());
                addFunction(im, SHIFT_RIGHT, ((whoClicked, clickType, pdc, fancyInventory) ->
                        openPublicTPTPortsSettings(whoClicked, fancyInventory.getData(pageDataName), fancyInventory)));
            }
        }
        
        if (attributes.contains(TPortItemAttributes.SELECT_HOME)) {
            lore.add(new Message());
            lore.add(formatInfoTranslation("tport.inventories.itemFactory.toTPortItem.selectAsHome", LEFT));
            addCommand(im, LEFT, "tport home set " + PlayerUUID.getPlayerName(tport.getOwner()) + " " + tport.getName(), "tport");
        }
        
        if (attributes.contains(TPortItemAttributes.TRANSFER_OFFERS) && extraData instanceof PlayerEncapsulation pe) {
            lore.add(new Message());
            
            lore.add(formatInfoTranslation("tport.inventories.itemFactory.toTPortItem.transfer.accept", LEFT, tport));
            lore.add(formatInfoTranslation("tport.inventories.itemFactory.toTPortItem.transfer.reject", RIGHT, tport));
            
            addCommand(im, LEFT, "tport transfer accept " + pe.getName() + " " + tport.getName());
            addCommand(im, RIGHT, "tport transfer reject " + pe.getName() + " " + tport.getName());
        }
        
        if (attributes.contains(TPortItemAttributes.TRANSFER_OFFERED) && extraData instanceof PlayerEncapsulation pe) {
            lore.add(new Message());
            lore.add(formatInfoTranslation("tport.inventories.itemFactory.toTPortItem.transfer.revoke", LEFT, tport));
            addCommand(im, LEFT, "tport transfer revoke " + tport.getName());
        }
        
        if (attributes.contains(TPortItemAttributes.LOG_DATA)) {
            lore.add(new Message());
            
            lore.add(formatInfoTranslation("tport.inventories.itemFactory.toTPortItem.logData.isLogged"));
            lore.add(formatInfoTranslation("tport.inventories.itemFactory.toTPortItem.logData.quickEdit", LEFT));
            
            addFunction(im, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
                String innerTPortName = pdc.get(new NamespacedKey(Main.getInstance(), "tportName"), STRING);
                TPort innerTPort = TPortManager.getTPort(whoClicked.getUniqueId(), innerTPortName);
                if (innerTPort != null) QuickEditInventories.openTPortLogGUI(whoClicked, innerTPort);
            }));
        }
        
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        title.translateMessage(playerLang);
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
        BIOME_TP(((whoClicked, clickType, pdc, fancyInventory) -> openBiomeTP(whoClicked, 0, fancyInventory))), //TPort.biomeTP.open
        FEATURE_TP(((whoClicked, clickType, pdc, fancyInventory) -> TPortCommand.executeTPortCommand(whoClicked, "featureTP"))), //TPort.featureTP.open
        WORLD_TP(((whoClicked, clickType, pdc, fancyInventory) -> TPortCommand.executeTPortCommand(whoClicked, "world"))), //TPort.world.tp
        SETTINGS(((whoClicked, clickType, pdc, fancyInventory) -> TPortCommand.executeTPortCommand(whoClicked, "settings"))),
        COLOR_THEME(((whoClicked, clickType, pdc, fancyInventory) -> TPortCommand.executeTPortCommand(whoClicked, "colorTheme"))),
        HOME_SET(((whoClicked, clickType, pdc, fancyInventory) -> TPortCommand.executeTPortCommand(whoClicked, "home set"))), //TPort.home.set OR TPort.basic
        BACKUP(((whoClicked, clickType, pdc, fancyInventory) -> TPortCommand.executeTPortCommand(whoClicked, "backup"))),
        QUICK_EDIT(((whoClicked, clickType, pdc, fancyInventory) -> QuickEditInventories.openQuickEditSelection(whoClicked, 0, fancyInventory.getData(tportUUIDDataName)))),
        TPORT_LOG(((whoClicked, clickType, pdc, fancyInventory) -> QuickEditInventories.openTPortLogGUI(whoClicked, fancyInventory.getData(tportDataName)))),
        PUBLIC_TP_SETTINGS(((whoClicked, clickType, pdc, fancyInventory) -> SettingsInventories.openPublicTPSettings(whoClicked))),
        LOG_SETTINGS(((whoClicked, clickType, pdc, fancyInventory) -> SettingsInventories.openLogGUI(whoClicked))),
        LOG_SETTINGS_READ(((whoClicked, clickType, pdc, fancyInventory) -> QuickEditInventories.openTPortLogReadGUI(whoClicked, fancyInventory.getData(tportDataName), fancyInventory.getData("filterUUID", UUID.class)))),
        SEARCH(((whoClicked, clickType, pdc, fancyInventory) -> SettingsInventories.openMainSearchGUI(whoClicked, 0))),
        PLTP(((whoClicked, clickType, pdc, fancyInventory) -> SettingsInventories.openPLTPGUI(whoClicked)));
        
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
    
    public static List<ItemStack> getPlayerList(Player player, boolean hasOwn, boolean forceHeadsOnly, List<HeadAttributes> headAttributes, List<TPortItemAttributes> tportItemAttributes, @Nullable Object headData) {
        List<ItemStack> list = getSorter(player).sort(player, headAttributes, headData);
        
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
            if (!forceHeadsOnly && MainLayout.showTPorts(player)) {
                if (!(is.getItemMeta() instanceof SkullMeta sm)) {
                    continue;
                }
                if (sm.getOwningPlayer() != null) {
                    TPortManager.getSortedTPortList(tportData, sm.getOwningPlayer().getUniqueId()).stream()
                            .filter(Objects::nonNull).map(tport -> toTPortItem(tport, player, tportItemAttributes)).forEach(newList::add);
                }
            }
        }
        list = newList;
        
        return list;
    }
    
    //todo fix content stored in prevWindow
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
