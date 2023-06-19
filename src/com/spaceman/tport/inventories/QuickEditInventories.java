package com.spaceman.tport.inventories;

import com.google.gson.JsonObject;
import com.spaceman.tport.Glow;
import com.spaceman.tport.Main;
import com.spaceman.tport.Pair;
import com.spaceman.tport.commands.TPortCommand;
import com.spaceman.tport.commands.tport.DynmapCommand;
import com.spaceman.tport.commands.tport.Features;
import com.spaceman.tport.commands.tport.Tag;
import com.spaceman.tport.commands.tport.log.Read;
import com.spaceman.tport.commands.tport.log.TimeFormat;
import com.spaceman.tport.dynmap.DynmapHandler;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.MessageUtils;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fancyMessage.inventories.FancyClickEvent;
import com.spaceman.tport.fancyMessage.inventories.FancyInventory;
import com.spaceman.tport.fancyMessage.inventories.InventoryModel;
import com.spaceman.tport.fancyMessage.inventories.KeyboardGUI;
import com.spaceman.tport.fancyMessage.language.Language;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.spaceman.tport.commands.TPortCommand.executeTPortCommand;
import static com.spaceman.tport.dynmap.DynmapHandler.tport_dynmap_icon;
import static com.spaceman.tport.fancyMessage.MessageUtils.setCustomItemData;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;
import static com.spaceman.tport.fancyMessage.encapsulation.TPortEncapsulation.asTPort;
import static com.spaceman.tport.fancyMessage.inventories.FancyClickEvent.addCommand;
import static com.spaceman.tport.fancyMessage.inventories.FancyClickEvent.addFunction;
import static com.spaceman.tport.fancyMessage.inventories.FancyInventory.getDynamicScrollableInventory;
import static com.spaceman.tport.fancyMessage.inventories.KeyboardGUI.getKeyboardOutput;
import static com.spaceman.tport.fancyMessage.language.Language.getPlayerLang;
import static com.spaceman.tport.fileHander.Files.tportData;
import static com.spaceman.tport.inventories.ItemFactory.BackType.*;
import static com.spaceman.tport.inventories.ItemFactory.*;
import static com.spaceman.tport.inventories.TPortInventories.openTPortGUI;
import static org.bukkit.event.inventory.ClickType.*;
import static org.bukkit.persistence.PersistentDataType.STRING;

public class QuickEditInventories {
    
    public static final InventoryModel quick_edit_private_open_model = new InventoryModel(Material.OAK_BUTTON, TPortInventories.last_model_id + 1, "quick_edit");
    public static final InventoryModel quick_edit_private_private_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_private_open_model, "quick_edit");
    public static final InventoryModel quick_edit_private_online_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_private_private_model, "quick_edit");
    public static final InventoryModel quick_edit_private_prion_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_private_online_model, "quick_edit");
    public static final InventoryModel quick_edit_private_consent_private_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_private_prion_model, "quick_edit");
    public static final InventoryModel quick_edit_private_consent_close_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_private_consent_private_model, "quick_edit");
    public static final InventoryModel quick_edit_whitelist_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_private_consent_close_model, "quick_edit");
    public static final InventoryModel quick_edit_whitelist_visibility_on_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_whitelist_model, "quick_edit");
    public static final InventoryModel quick_edit_whitelist_visibility_off_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_whitelist_visibility_on_model, "quick_edit");
    public static final InventoryModel quick_edit_range_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_whitelist_visibility_off_model, "quick_edit");
    public static final InventoryModel quick_edit_range_add_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_range_model, "quick_edit");
    public static final InventoryModel quick_edit_range_remove_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_range_add_model, "quick_edit");
    public static final InventoryModel quick_edit_move_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_range_remove_model, "quick_edit");
    public static final InventoryModel quick_edit_move_empty_slot_model = new InventoryModel(Material.GRAY_STAINED_GLASS_PANE, quick_edit_move_model, "quick_edit");
    public static final InventoryModel quick_edit_log_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_move_empty_slot_model, "quick_edit");
    public static final InventoryModel quick_edit_log_edit_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_log_model, "quick_edit");
    public static final InventoryModel quick_edit_log_read_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_log_edit_model, "quick_edit");
    public static final InventoryModel quick_edit_log_clear_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_log_read_model, "quick_edit");
    public static final InventoryModel quick_edit_log_mode_online_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_log_clear_model, "quick_edit");
    public static final InventoryModel quick_edit_log_mode_offline_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_log_mode_online_model, "quick_edit");
    public static final InventoryModel quick_edit_log_mode_all_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_log_mode_offline_model, "quick_edit");
    public static final InventoryModel quick_edit_log_mode_none_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_log_mode_all_model, "quick_edit");
    public static final InventoryModel quick_edit_notify_online_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_log_mode_none_model, "quick_edit");
    public static final InventoryModel quick_edit_notify_log_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_notify_online_model, "quick_edit");
    public static final InventoryModel quick_edit_notify_none_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_notify_log_model, "quick_edit");
    public static final InventoryModel quick_edit_preview_on_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_notify_none_model, "quick_edit");
    public static final InventoryModel quick_edit_preview_off_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_preview_on_model, "quick_edit");
    public static final InventoryModel quick_edit_preview_notified_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_preview_off_model, "quick_edit");
    public static final InventoryModel quick_edit_preview_grayed_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_preview_notified_model, "quick_edit");
    public static final InventoryModel quick_edit_tag_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_preview_grayed_model, "quick_edit");
    public static final InventoryModel quick_edit_tag_selection_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_tag_model, "quick_edit");
    public static final InventoryModel quick_edit_remove_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_tag_selection_model, "quick_edit");
    public static final InventoryModel quick_edit_remove_confirm_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_remove_model, "quick_edit");
    public static final InventoryModel quick_edit_remove_cancel_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_remove_confirm_model, "quick_edit");
    public static final InventoryModel quick_edit_location_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_remove_cancel_model, "quick_edit");
    public static final InventoryModel quick_edit_location_confirm_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_location_model, "quick_edit");
    public static final InventoryModel quick_edit_location_cancel_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_location_confirm_model, "quick_edit");
    public static final InventoryModel quick_edit_name_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_location_cancel_model, "quick_edit");
    public static final InventoryModel quick_edit_description_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_name_model, "quick_edit");
    public static final InventoryModel quick_edit_public_tp_on_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_description_model, "quick_edit");
    public static final InventoryModel quick_edit_public_tp_off_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_public_tp_on_model, "quick_edit");
    public static final InventoryModel quick_edit_public_tp_grayed_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_public_tp_off_model, "quick_edit");
    public static final InventoryModel quick_edit_dynmap_show_on_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_public_tp_grayed_model, "quick_edit");
    public static final InventoryModel quick_edit_dynmap_show_off_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_dynmap_show_on_model, "quick_edit");
    public static final InventoryModel quick_edit_dynmap_show_grayed_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_dynmap_show_off_model, "quick_edit");
    public static final InventoryModel quick_edit_dynmap_icon_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_dynmap_show_grayed_model, "quick_edit");
    public static final InventoryModel quick_edit_dynmap_icon_tport_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_dynmap_icon_model, "quick_edit");
    public static final InventoryModel quick_edit_dynmap_icon_grayed_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_dynmap_icon_tport_model, "quick_edit");
    public static final int last_model_id = quick_edit_dynmap_icon_grayed_model.getCustomModelData();
    
    public static void openQuickEditSelection(Player player, int page, UUID tportUUID) {
        JsonObject playerLang = Language.getPlayerLang(player.getUniqueId());
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        
        QuickEditType currentType = QuickEditType.getForPlayer(player.getUniqueId());
        TPort tport = TPortManager.getTPort(player.getUniqueId(), tportUUID);
        
        ArrayList<ItemStack> quickEdits = new ArrayList<>();
        for (QuickEditType quickEdit : QuickEditType.values()) {
            ItemStack item = new ItemStack(quickEdit.getModel(tport).getItem(player));
            
            //todo add editor description
            Message itemTitle = formatInfoTranslation("tport.quickEditInventories.openQuickEditSelection.item.title", quickEdit.getDisplayName());
            itemTitle.translateMessage(playerLang);
            Message loreLeft = formatInfoTranslation("tport.quickEditInventories.openQuickEditSelection.item.click.left", LEFT);
            loreLeft.translateMessage(playerLang);
            Message loreRight = formatInfoTranslation("tport.quickEditInventories.openQuickEditSelection.item.click.right", RIGHT);
            loreRight.translateMessage(playerLang);
            setCustomItemData(item, colorTheme, itemTitle, Arrays.asList(loreLeft, loreRight));
            FancyClickEvent.setStringData(item, new NamespacedKey(Main.getInstance(), "quickEditType"), quickEdit.name());
            if (quickEdit == currentType) Glow.addGlow(item);
            
            FancyClickEvent.addFunction(item, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
                NamespacedKey quickEditKey = new NamespacedKey(Main.getInstance(), "quickEditType");
                if (pdc.has(quickEditKey, STRING)) {
                    String type = pdc.get(quickEditKey, STRING);
                    QuickEditType quickEditType = QuickEditType.get(type);
                    onQuickEdit(whoClicked, pdc, fancyInventory, quickEditType);
                }
            }));
            FancyClickEvent.addFunction(item, RIGHT, ((whoClicked, clickType, pdc, fancyInventory) -> {
                NamespacedKey quickEditKey = new NamespacedKey(Main.getInstance(), "quickEditType");
                
                if (pdc.has(quickEditKey, STRING)) {
                    String type = pdc.get(quickEditKey, STRING);
                    QuickEditType type_event = QuickEditType.get(type);
                    QuickEditType.setForPlayer(whoClicked.getUniqueId(), type_event);
                    openTPortGUI(whoClicked.getUniqueId(), whoClicked, null);
                }
            }));
            
            quickEdits.add(item);
        }
        
        ItemStack backItem = createBack(player, MAIN, OWN, null);
        Message invTitle = formatInfoTranslation("tport.quickEditInventories.openQuickEditSelection.title", asTPort(tport));
        FancyInventory inv = getDynamicScrollableInventory(player, page, QuickEditInventories::openQuickEditSelection, invTitle, quickEdits, backItem);
        inv.setData("tportUUID", tportUUID);
        inv.setData("fromQuickEdit", true);
        
        inv.open(player);
    }
    private static void openQuickEditSelection(Player player, int page, FancyInventory prevWindow) {
        openQuickEditSelection(player, page,
                UUID.fromString(prevWindow.getData("tportUUID", String.class)));
    }
    
    private static ItemStack getCornerTPortIcon(TPort tport, ColorTheme colorTheme, JsonObject playerLang) {
        ItemStack tportItem = tport.getItem();
        Message title = formatInfoTranslation(playerLang, "tport.tportInventories.tportName", asTPort(tport));
        List<Message> lore = tport.getHoverData(false);
        lore = MessageUtils.translateMessage(lore, playerLang);
        setCustomItemData(tportItem, colorTheme, title, lore);
        return tportItem;
    }
    
    public static void openTPortTagSelectorGUI(Player player, TPort tport, int page) {
        ArrayList<String> tags = Tag.getTags();
        ArrayList<ItemStack> tagItems = new ArrayList<>();
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        
        for (String tag : tags) {
            ItemStack is = quick_edit_tag_selection_model.getItem(player);
            ItemMeta im = is.getItemMeta();
            
            im.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "tagName"), PersistentDataType.STRING, tag);
            
            Message lore;
            
            if (tport.getTags().contains(tag)) {
                Glow.addGlow(im);
                FancyClickEvent.addFunction(im, LEFT, (whoClicked, clickType, pdc, fancyInventory) -> {
                    NamespacedKey tagNameKey = new NamespacedKey(Main.getInstance(), "tagName");
                    if (pdc.has(tagNameKey, PersistentDataType.STRING)) {
                        String tagName = pdc.get(tagNameKey, PersistentDataType.STRING);
                        String tportName = fancyInventory.getData("tport", TPort.class).getName();
                        executeTPortCommand(whoClicked, "edit " + tportName + " tag remove " + tagName);
                        openTPortTagSelectorGUI(whoClicked, fancyInventory.getData("page", Integer.class, 0), fancyInventory);
                    }
                });
                lore = formatInfoTranslation("tport.quickEditInventories.openTPortTagSelectionGUI.tag.unselect", LEFT);
            } else {
                FancyClickEvent.addFunction(im, LEFT, (whoClicked, clickType, pdc, fancyInventory) -> {
                    NamespacedKey tagNameKey = new NamespacedKey(Main.getInstance(), "tagName");
                    if (pdc.has(tagNameKey, PersistentDataType.STRING)) {
                        String tagName = pdc.get(tagNameKey, PersistentDataType.STRING);
                        String tportName = fancyInventory.getData("tport", TPort.class).getName();
                        executeTPortCommand(whoClicked, "edit " + tportName + " tag add " + tagName);
                        openTPortTagSelectorGUI(whoClicked, fancyInventory.getData("page", Integer.class, 0), fancyInventory);
                    }
                });
                lore = formatInfoTranslation("tport.quickEditInventories.openTPortTagSelectionGUI.tag.select", LEFT);
            }
            
            is.setItemMeta(im);
            
            Message title = formatInfoTranslation("tport.quickEditInventories.openTPortTagSelectionGUI.tag.name", tag);
            title.translateMessage(playerLang);
            lore.translateMessage(playerLang);
            setCustomItemData(is, colorTheme, title, Collections.singletonList(lore));
            
            tagItems.add(is);
        }
        
        FancyInventory inv = getDynamicScrollableInventory(player, page, QuickEditInventories::openTPortTagSelectorGUI,
                formatInfoTranslation("tport.quickEditInventories.openTPortTagSelectionGUI.title", asTPort(tport)), tagItems, createBack(player, MAIN, OWN, QUICK_EDIT));
        
        inv.setItem(0, getCornerTPortIcon(tport, colorTheme, playerLang));
        inv.setData("tportUUID", tport.getTportID());
        inv.setData("tport", tport);
        
        inv.open(player);
    }
    private static void openTPortTagSelectorGUI(Player player, int page, FancyInventory fancyInventory) {
        openTPortTagSelectorGUI(player, fancyInventory.getData("tport", TPort.class), page);
    }
    
    public static void openTPortWhitelistSelectorGUI(Player player, TPort tport, int page, @Nullable FancyInventory prevWindow) {
        List<ItemStack> rawHeadItems;
        if (prevWindow == null) {
            rawHeadItems = ItemFactory.getPlayerList(player, false, true, Collections.emptyList(), Collections.emptyList());
        } else {
            rawHeadItems = prevWindow.getData("content", List.class);
        }
        
        ArrayList<ItemStack> headItems = new ArrayList<>();
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        
        for (ItemStack playerHead : rawHeadItems) {
            ItemMeta im = playerHead.getItemMeta();
            UUID uuid = null;
            String playerName = "";
            
            PersistentDataContainer pdc = im.getPersistentDataContainer();
            NamespacedKey playerNameKey = new NamespacedKey(Main.getInstance(), "playerName");
            NamespacedKey playerUUIDKey = new NamespacedKey(Main.getInstance(), "playerUUID");
            if (pdc.has(playerNameKey, PersistentDataType.STRING)) {
                playerName = pdc.get(playerNameKey, PersistentDataType.STRING);
            }
            if (pdc.has(playerUUIDKey, PersistentDataType.STRING)) {
                uuid = UUID.fromString(pdc.get(playerUUIDKey, PersistentDataType.STRING));
            }
            
            Message lore;
            im.setLore(new ArrayList<>());
            
            if (tport.getWhitelist().contains(uuid)) {
                FancyClickEvent.addFunction(im, LEFT, (whoClicked, clickType, innerPDC, fancyInventory) -> {
                    NamespacedKey innerPlayerNameKey = new NamespacedKey(Main.getInstance(), "playerName");
                    if (innerPDC.has(innerPlayerNameKey, PersistentDataType.STRING)) {
                        String innerPlayerName = innerPDC.get(innerPlayerNameKey, PersistentDataType.STRING);
                        String tportName = fancyInventory.getData("tport", TPort.class).getName();
                        executeTPortCommand(whoClicked, "edit " + tportName + " whitelist remove " + innerPlayerName);
                        openTPortWhitelistSelectorGUI(whoClicked, fancyInventory.getData("page", Integer.class, 0), fancyInventory);
                    }
                });
                lore = formatInfoTranslation("tport.quickEditInventories.openTPortWhitelistSelectionGUI.player.unselect", LEFT, playerName);
            } else {
                FancyClickEvent.addFunction(im, LEFT, (whoClicked, clickType, innerPDC, fancyInventory) -> {
                    NamespacedKey innerPlayerNameKey = new NamespacedKey(Main.getInstance(), "playerName");
                    if (innerPDC.has(innerPlayerNameKey, PersistentDataType.STRING)) {
                        String innerPlayerName = innerPDC.get(innerPlayerNameKey, PersistentDataType.STRING);
                        String tportName = fancyInventory.getData("tport", TPort.class).getName();
                        executeTPortCommand(whoClicked, "edit " + tportName + " whitelist add " + innerPlayerName);
                        openTPortWhitelistSelectorGUI(whoClicked, fancyInventory.getData("page", Integer.class, 0), fancyInventory);
                    }
                });
                lore = formatInfoTranslation("tport.quickEditInventories.openTPortWhitelistSelectionGUI.player.select", LEFT, playerName);
            }
            
            playerHead.setItemMeta(im);
            
            Message title = formatInfoTranslation("tport.quickEditInventories.openTPortWhitelistSelectionGUI.player.name", playerName);
            title.translateMessage(playerLang);
            lore.translateMessage(playerLang);
            setCustomItemData(playerHead, colorTheme, title, Collections.singletonList(lore));
            
            headItems.add(playerHead);
        }
        
        FancyInventory inv = getDynamicScrollableInventory(player, page, QuickEditInventories::openTPortWhitelistSelectorGUI,
                formatInfoTranslation("tport.quickEditInventories.openTPortWhitelistSelectionGUI.title", asTPort(tport)), headItems, createBack(player, MAIN, OWN, QUICK_EDIT));
        inv.setData("content", headItems);
        
        inv.setItem(0, getCornerTPortIcon(tport, colorTheme, playerLang));
        inv.setData("tport", tport);
        inv.setItem(inv.getSize() / 18 * 9,
                ItemFactory.getSortingItem(player, getPlayerLang(player.getUniqueId()), ColorTheme.getTheme(player),
                        ((whoClicked, clickType, pdc, fancyInventory) -> openTPortWhitelistSelectorGUI(whoClicked, fancyInventory.getData("tport", TPort.class), 0, null))));
        inv.setData("tportUUID", tport.getTportID());
        
        inv.open(player);
    }
    private static void openTPortWhitelistSelectorGUI(Player player, int page, FancyInventory fancyInventory) {
        openTPortWhitelistSelectorGUI(player, fancyInventory.getData("tport", TPort.class), page, fancyInventory);
    }
    
    private static void openTPortRemoveGUI(Player player, TPort tport, FancyInventory prevWindow) {
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        
        Message title = formatInfoTranslation("tport.quickEditInventories.openTPortRemoveGUI.title", asTPort(tport));
        FancyInventory inv = new FancyInventory(27, title);
        
        inv.setItem(13, getCornerTPortIcon(tport, colorTheme, playerLang));
        
        ItemStack confirm = quick_edit_remove_confirm_model.getItem(player);
        addCommand(confirm, LEFT, "tport remove " + tport.getName(), "tport own");
        Message confirmTitle = formatInfoTranslation("tport.quickEditInventories.openTPortRemoveGUI.confirm.title", LEFT, asTPort(tport));
        confirmTitle.translateMessage(playerLang);
        setCustomItemData(confirm, colorTheme, confirmTitle, null);
        inv.setItem(11, confirm);
        
        ItemStack cancel = quick_edit_remove_cancel_model.getItem(player);
        ArrayList<Message> cancelLore = new ArrayList<>();
        ItemFactory.BackType leftBackType;
        ItemFactory.BackType rightBackType;
        ItemFactory.BackType shift_rightBackType = null;
        if (prevWindow.getData("fromQuickEdit", Boolean.class, false)) {
            leftBackType = QUICK_EDIT;
            rightBackType = MAIN;
            shift_rightBackType = OWN;
        } else {
            leftBackType = OWN;
            rightBackType = MAIN;
        }
        
        Message leftClickMessage = formatInfoTranslation("tport.quickEditInventories.openTPortLocationGUI.cancel.left", LEFT, leftBackType.getName());
        leftClickMessage.translateMessage(playerLang);
        addFunction(cancel, LEFT, leftBackType.getFunction());
        cancelLore.add(leftClickMessage);
        Message rightClickMessage = formatInfoTranslation("tport.quickEditInventories.openTPortLocationGUI.cancel.right", RIGHT, rightBackType.getName());
        rightClickMessage.translateMessage(playerLang);
        addFunction(cancel, RIGHT, rightBackType.getFunction());
        cancelLore.add(rightClickMessage);
        if (shift_rightBackType != null) {
            addFunction(cancel, SHIFT_RIGHT, shift_rightBackType.getFunction());
            Message shift_rightClickMessage = formatInfoTranslation("tport.quickEditInventories.openTPortLocationGUI.cancel.shiftRight", SHIFT_RIGHT, shift_rightBackType.getName());
            shift_rightClickMessage.translateMessage(playerLang);
            cancelLore.add(shift_rightClickMessage);
        }
        
        Message cancelTitle = formatInfoTranslation("tport.quickEditInventories.openTPortRemoveGUI.cancel.title", LEFT, asTPort(tport));
        cancelTitle.translateMessage(playerLang);
        setCustomItemData(cancel, colorTheme, cancelTitle, cancelLore);
        inv.setItem(15, cancel);
        inv.setData("tportUUID", tport.getTportID());
        
        inv.open(player);
    }
    
    private static void openTPortLocationGUI(Player player, TPort tport, FancyInventory prevWindow) {
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        
        Message title = formatInfoTranslation("tport.quickEditInventories.openTPortLocationGUI.title", asTPort(tport));
        FancyInventory inv = new FancyInventory(27, title);
        
        inv.setItem(13, getCornerTPortIcon(tport, colorTheme, playerLang));
        
        ItemStack confirm = quick_edit_location_confirm_model.getItem(player);
        addCommand(confirm, LEFT, "tport edit " + tport.getName() + " location", "tport own");
        Message confirmTitle = formatInfoTranslation("tport.quickEditInventories.openTPortLocationGUI.confirm.title", LEFT, asTPort(tport));
        confirmTitle.translateMessage(playerLang);
        setCustomItemData(confirm, colorTheme, confirmTitle, null);
        inv.setItem(11, confirm);
        
        
        ItemStack cancel = quick_edit_location_cancel_model.getItem(player);
        ArrayList<Message> cancelLore = new ArrayList<>();
        ItemFactory.BackType leftBackType;
        ItemFactory.BackType rightBackType;
        ItemFactory.BackType shift_rightBackType = null;
        if (prevWindow.getData("fromQuickEdit", Boolean.class, false)) {
            leftBackType = QUICK_EDIT;
            rightBackType = MAIN;
            shift_rightBackType = OWN;
        } else {
            leftBackType = OWN;
            rightBackType = MAIN;
        }
        
        Message leftClickMessage = formatInfoTranslation("tport.quickEditInventories.openTPortLocationGUI.cancel.left", LEFT, leftBackType.getName());
        leftClickMessage.translateMessage(playerLang);
        addFunction(cancel, LEFT, leftBackType.getFunction());
        cancelLore.add(leftClickMessage);
        Message rightClickMessage = formatInfoTranslation("tport.quickEditInventories.openTPortLocationGUI.cancel.right", RIGHT, rightBackType.getName());
        rightClickMessage.translateMessage(playerLang);
        addFunction(cancel, RIGHT, rightBackType.getFunction());
        cancelLore.add(rightClickMessage);
        if (shift_rightBackType != null) {
            addFunction(cancel, SHIFT_RIGHT, shift_rightBackType.getFunction());
            Message shift_rightClickMessage = formatInfoTranslation("tport.quickEditInventories.openTPortLocationGUI.cancel.shiftRight", SHIFT_RIGHT, shift_rightBackType.getName());
            shift_rightClickMessage.translateMessage(playerLang);
            cancelLore.add(shift_rightClickMessage);
        }
        
        Message cancelTitle = formatInfoTranslation("tport.quickEditInventories.openTPortLocationGUI.cancel.title", LEFT, asTPort(tport));
        cancelTitle.translateMessage(playerLang);
        setCustomItemData(cancel, colorTheme, cancelTitle, cancelLore);
        inv.setItem(15, cancel);
        inv.setData("tportUUID", tport.getTportID());
        
        inv.open(player);
    }
    
    private static void openTPortPrivateGUI(Player player, TPort tport, FancyInventory prevWindow) {
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        
        Message title = formatInfoTranslation(playerLang, "tport.quickEditInventories.openTPortPrivateGUI.title", asTPort(tport));
        FancyInventory inv = new FancyInventory(27, title);
        
        int itemIndex = 10;
        for (TPort.PrivateState privateState : TPort.PrivateState.values()) {
            ItemStack is = privateState.getInventoryModel().getItem(player);
            if (tport.getPrivateState().equals(privateState)) {
                Glow.addGlow(is);
            }
            
            Message leftClickMessage = formatInfoTranslation(playerLang, "tport.quickEditInventories.openTPortPrivateGUI.privateState.leftClick", LEFT);
            Message rightClickMessage = formatInfoTranslation(playerLang, "tport.quickEditInventories.openTPortPrivateGUI.privateState.rightClick", RIGHT);
            setCustomItemData(is, colorTheme, new Message(privateState.getName(null)), Arrays.asList(leftClickMessage, rightClickMessage));
            
            FancyClickEvent.setStringData(is, new NamespacedKey(Main.getInstance(), "privateState"), privateState.name());
            addCommand(is, LEFT, "tport edit " + tport.getName() + " private " + privateState.name(), "tport own");
            addFunction(is, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
                openTPortPrivateGUI(whoClicked, fancyInventory.getData("tport", TPort.class), fancyInventory);
            }));
            addFunction(is, RIGHT, ((whoClicked, clickType, pdc, fancyInventory) -> {
                String ps = pdc.get(new NamespacedKey(Main.getInstance(), "privateState"), STRING);
                TPort.PrivateState.get(ps, TPort.PrivateState.OPEN).getDescription().sendAndTranslateMessage(whoClicked);
            }));
            
            inv.setItem(itemIndex++, is);
        }
        
        inv.setItem(17, createBack(player, MAIN, OWN, QUICK_EDIT));
        inv.setData("tportUUID", tport.getTportID());
        inv.setData("tport", tport);
        
        inv.setItem(0, getCornerTPortIcon(tport, colorTheme, playerLang));
        
        inv.open(player);
    }
    
    private static int newRange(TPort tport, int add) {
        return Math.max(0, tport.getRange() + add);
    }
    private static void openTPortRangeGUI(Player player, TPort tport) {
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        
        ItemStack rangeDisplay = quick_edit_range_model.getItem(player);
        Message rangeMessage = tport.getRange() == 0 ? formatTranslation(ColorType.varInfoColor, ColorType.varInfoColor, "tport.command.edit.range.off") : formatTranslation(ColorType.varInfoColor, ColorType.varInfoColor, "%s", tport.getRange());
        Message rangeDisplayTitle = formatInfoTranslation(playerLang, "tport.quickEditInventories.openTPortRangeGUI.rangeDisplay", rangeMessage);
        setCustomItemData(rangeDisplay, colorTheme, rangeDisplayTitle, null);
        
        ItemStack rangeAdd = quick_edit_range_add_model.getItem(player);
        Message rangeAddTitle = formatInfoTranslation(playerLang, "tport.quickEditInventories.openTPortRangeGUI.rangeAdd.title");
        Message rangeAdd_1   = formatInfoTranslation(playerLang, "tport.quickEditInventories.openTPortRangeGUI.rangeAdd.value", LEFT, 1);
        Message rangeAdd_10  = formatInfoTranslation(playerLang, "tport.quickEditInventories.openTPortRangeGUI.rangeAdd.value", RIGHT, 10);
        Message rangeAdd_25  = formatInfoTranslation(playerLang, "tport.quickEditInventories.openTPortRangeGUI.rangeAdd.value", SHIFT_LEFT, 25);
        Message rangeAdd_100 = formatInfoTranslation(playerLang, "tport.quickEditInventories.openTPortRangeGUI.rangeAdd.value", SHIFT_RIGHT, 100);
        setCustomItemData(rangeAdd, colorTheme, rangeAddTitle, Arrays.asList(rangeAdd_1, rangeAdd_10, rangeAdd_25, rangeAdd_100));
        addCommand(rangeAdd, LEFT, "tport edit " + tport.getName() + " range " + newRange(tport, 1));
        addCommand(rangeAdd, RIGHT, "tport edit " + tport.getName() + " range " + newRange(tport, 10));
        addCommand(rangeAdd, SHIFT_LEFT, "tport edit " + tport.getName() + " range " + newRange(tport, 25));
        addCommand(rangeAdd, SHIFT_RIGHT, "tport edit " + tport.getName() + " range " + newRange(tport, 100));
        addFunction(rangeAdd, ((whoClicked, clickType, pdc, fancyInventory) -> {
            UUID tportUUID = fancyInventory.getData("tportUUID", UUID.class);
            openTPortRangeGUI(whoClicked, TPortManager.getTPort(whoClicked.getUniqueId(), tportUUID));
        }), LEFT, RIGHT, SHIFT_LEFT, SHIFT_RIGHT);
        
        ItemStack rangeRemove = quick_edit_range_remove_model.getItem(player);
        Message rangeRemoveTitle = formatInfoTranslation(playerLang, "tport.quickEditInventories.openTPortRangeGUI.rangeRemove.title");
        Message rangeRemove_1   = formatInfoTranslation(playerLang, "tport.quickEditInventories.openTPortRangeGUI.rangeRemove.value", LEFT, 1);
        Message rangeRemove_10  = formatInfoTranslation(playerLang, "tport.quickEditInventories.openTPortRangeGUI.rangeRemove.value", RIGHT, 10);
        Message rangeRemove_25  = formatInfoTranslation(playerLang, "tport.quickEditInventories.openTPortRangeGUI.rangeRemove.value", SHIFT_LEFT, 25);
        Message rangeRemove_100 = formatInfoTranslation(playerLang, "tport.quickEditInventories.openTPortRangeGUI.rangeRemove.value", SHIFT_RIGHT, 100);
        setCustomItemData(rangeRemove, colorTheme, rangeRemoveTitle, Arrays.asList(rangeRemove_1, rangeRemove_10, rangeRemove_25, rangeRemove_100));
        addCommand(rangeRemove, LEFT, "tport edit " + tport.getName() + " range " + newRange(tport, -1));
        addCommand(rangeRemove, RIGHT, "tport edit " + tport.getName() + " range " + newRange(tport, -10));
        addCommand(rangeRemove, SHIFT_LEFT, "tport edit " + tport.getName() + " range " + newRange(tport, -25));
        addCommand(rangeRemove, SHIFT_RIGHT, "tport edit " + tport.getName() + " range " + newRange(tport, -100));
        addFunction(rangeRemove, ((whoClicked, clickType, pdc, fancyInventory) -> {
            UUID tportUUID = fancyInventory.getData("tportUUID", UUID.class);
            openTPortRangeGUI(whoClicked, TPortManager.getTPort(whoClicked.getUniqueId(), tportUUID));
        }), LEFT, RIGHT, SHIFT_LEFT, SHIFT_RIGHT);
        
        FancyInventory inv = new FancyInventory(27, formatInfoTranslation("tport.quickEditInventories.openTPortRangeGUI.title", tport));
        inv.setData("tportUUID", tport.getTportID());
        
        inv.setItem(0, getCornerTPortIcon(tport, colorTheme, playerLang));
        inv.setItem(11, rangeRemove);
        inv.setItem(13, rangeDisplay);
        inv.setItem(15, rangeAdd);
        inv.setItem(17, createBack(player, MAIN, OWN, QUICK_EDIT));
        
        inv.open(player);
    }
    
    private static void openTPortDynmapIconGUI(Player player, TPort tport, int page) {
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        
        List<Pair<String, String>> markers = DynmapHandler.getIcons();
        ArrayList<ItemStack> items = new ArrayList<>();
        
        if (markers != null) {
            markers.sort(Comparator.comparing(Pair::getLeft));
            for (Pair<String, String> marker : markers) {
                ItemStack is = (marker.getLeft().equals(tport_dynmap_icon) ? quick_edit_dynmap_icon_tport_model : quick_edit_dynmap_icon_model).getItem(player);
                
                Message markerTitle = formatInfoTranslation(playerLang, "tport.quickEditInventories.openTPortDynmapIconGUI.icon.title", marker.getRight());
                Message markerLeft = formatInfoTranslation(playerLang,"tport.quickEditInventories.openTPortDynmapIconGUI.icon.leftClick", LEFT);
                setCustomItemData(is, colorTheme, markerTitle, List.of(markerLeft));
                
                addCommand(is, LEFT, "tport edit " + tport.getName() + " dynmap icon " + marker.getRight());
                
                if (marker.getLeft().equals(tport_dynmap_icon)) items.set(0, is);
                else items.add(is);
            }
        }
        
        FancyInventory inv = getDynamicScrollableInventory(player, page, QuickEditInventories::openTPortDynmapIconGUI,
                formatInfoTranslation("tport.quickEditInventories.openTPortDynmapIconGUI.title", tport), items, createBack(player, MAIN, OWN, QUICK_EDIT));
        inv.setData("tport", tport);
        inv.setData("tportUUID", tport.getTportID());
        inv.setItem(0, getCornerTPortIcon(tport, colorTheme, playerLang));
        
        inv.open(player);
    }
    private static void openTPortDynmapIconGUI(Player player, int page, FancyInventory prevWindow) {
        openTPortDynmapIconGUI(player, prevWindow.getData("tport", TPort.class), page);
    }
    
    public static void openTPortLogReadGUI(Player player, TPort tport) {
        openTPortLogReadGUI(player, tport, null, 0, null);
    }
    public static void openTPortLogReadGUI(Player player, TPort tport, @Nullable UUID filterUUID) {
        openTPortLogReadGUI(player, tport, filterUUID, 0, null);
    }
    private static void openTPortLogReadGUI(Player player, TPort tport, @Nullable UUID filterUUID, int page, @Nullable FancyInventory prevWindow) {
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        
        ArrayList<TPort.LogEntry> logbook = tport.getLogBook();
        ArrayList<ItemStack> items = new ArrayList<>(logbook.size());
        
        String format = TimeFormat.getTimeFormat(player);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(com.spaceman.tport.commands.tport.log.TimeZone.getTimeZone(player));
        
        for (TPort.LogEntry log : logbook) {
            if (filterUUID != null && !log.teleportedUUID().equals(filterUUID)) {
                continue;
            }
            ItemStack is = getHead(log.teleportedUUID(), player);
            
            Message title = formatInfoTranslation(playerLang, "tport.quickEditInventories.openTPortLogReadGUI.log.title", asPlayer(log.teleportedUUID()));
            Message timestamp = formatInfoTranslation(playerLang, "tport.quickEditInventories.openTPortLogReadGUI.log.time", tport, sdf.format(log.timeOfTeleport().getTime()));
            Message logMode = log.loggedMode() == null ? null : formatInfoTranslation(playerLang, "tport.quickEditInventories.openTPortLogReadGUI.log.logMode", log.loggedMode());
            Message ownerOnline = log.ownerOnline() == null ? null : formatInfoTranslation(playerLang, "tport.quickEditInventories.openTPortLogReadGUI.log.ownerOnline." + log.ownerOnline(), log.ownerOnline());
            setCustomItemData(is, colorTheme, title, Arrays.asList(timestamp, logMode, ownerOnline));
            
            items.add(is);
        }
        
        Message invTitle = filterUUID == null ?
                formatInfoTranslation("tport.quickEditInventories.openTPortLogReadGUI.all.title", tport) :
                formatInfoTranslation("tport.quickEditInventories.openTPortLogReadGUI.filtered.title", tport, asPlayer(filterUUID));
        
        FancyInventory inv = getDynamicScrollableInventory(player, page, QuickEditInventories::openTPortLogReadGUI,
                invTitle, items, createBack(player, MAIN, OWN, TPORT_LOG));
        inv.setData("tport", tport);
        if (filterUUID != null) inv.setData("filterUUID", filterUUID);
        
        ItemStack chatMode = new ItemStack(Material.OAK_BUTTON);
        addFunction(chatMode, LEFT, ((whoClicked, clickType, pdc, fancyInventory) ->
                Read.readLog_chat(
                        whoClicked,
                        fancyInventory.getData("tport", TPort.class),
                        fancyInventory.getData("filteredUUID", UUID.class, null)
                )));
        
        inv.open(player);
    }
    private static void openTPortLogReadGUI(Player player, int page, FancyInventory prevWindow) {
        openTPortLogReadGUI(player, prevWindow.getData("tport", TPort.class), prevWindow.getData("filterUUID", UUID.class), page, prevWindow);
    }
    private static void openTPortLogSelectorGUI(Player player, TPort tport, int page, @Nullable FancyInventory prevWindow) {
        List<ItemStack> rawHeadItems;
        if (prevWindow == null) {
            rawHeadItems = ItemFactory.getPlayerList(player, true, true, Collections.emptyList(), Collections.emptyList());
        } else {
            rawHeadItems = prevWindow.getData("content", List.class);
        }
        
        ArrayList<ItemStack> headItems = new ArrayList<>();
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        
        for (ItemStack playerHead : rawHeadItems) {
            ItemMeta im = playerHead.getItemMeta();
            UUID uuid = null;
            String playerName = "";
            
            PersistentDataContainer pdc = im.getPersistentDataContainer();
            NamespacedKey playerNameKey = new NamespacedKey(Main.getInstance(), "playerName");
            NamespacedKey playerUUIDKey = new NamespacedKey(Main.getInstance(), "playerUUID");
            if (pdc.has(playerNameKey, PersistentDataType.STRING)) {
                playerName = pdc.get(playerNameKey, PersistentDataType.STRING);
            }
            if (pdc.has(playerUUIDKey, PersistentDataType.STRING)) {
                uuid = UUID.fromString(pdc.get(playerUUIDKey, PersistentDataType.STRING));
            }
            boolean isPlayerLogged = tport.getLogged().contains(uuid);
            
            im.setLore(new ArrayList<>());
            
            FancyClickEvent.addFunction(im, LEFT, (whoClicked, clickType, innerPDC, fancyInventory) -> {
                NamespacedKey innerPlayerNameKey = new NamespacedKey(Main.getInstance(), "playerName");
                NamespacedKey innerPlayerUUIDKey = new NamespacedKey(Main.getInstance(), "playerUUID");
                if (innerPDC.has(innerPlayerNameKey, PersistentDataType.STRING)) {
                    String innerPlayerName = innerPDC.get(innerPlayerNameKey, PersistentDataType.STRING);
                    UUID innerPlayerUUID = UUID.fromString(innerPDC.get(innerPlayerUUIDKey, PersistentDataType.STRING));
                    TPort innerTPort = fancyInventory.getData("tport", TPort.class);
                    TPortCommand.executeTPortCommand(whoClicked, new String[]{"log", "add", innerTPort.getName(), innerPlayerName + ":" + innerTPort.getLogMode(innerPlayerUUID).getNext()});
                    openTPortLogSelectorGUI(whoClicked, fancyInventory.getData("page", Integer.class, 0), fancyInventory);
                }
            });
            if (isPlayerLogged) {
                FancyClickEvent.addFunction(im, RIGHT, (whoClicked, clickType, innerPDC, fancyInventory) -> {
                    NamespacedKey innerPlayerNameKey = new NamespacedKey(Main.getInstance(), "playerName");
                    if (innerPDC.has(innerPlayerNameKey, PersistentDataType.STRING)) {
                        String innerPlayerName = innerPDC.get(innerPlayerNameKey, PersistentDataType.STRING);
                        TPort innerTPort = fancyInventory.getData("tport", TPort.class);
                        TPortCommand.executeTPortCommand(whoClicked, new String[]{"log", "remove", innerTPort.getName(), innerPlayerName});
                        openTPortLogSelectorGUI(whoClicked, fancyInventory.getData("page", Integer.class, 0), fancyInventory);
                    }
                });
            }
            
            playerHead.setItemMeta(im);
            
            Message title = formatInfoTranslation(playerLang, "tport.quickEditInventories.openTPortLogSelectionGUI.player.name", playerName);
            Message currentState;
            if (isPlayerLogged) {
                currentState = formatInfoTranslation(playerLang, "tport.quickEditInventories.openTPortLogSelectionGUI.player.logState", tport.getLogMode(uuid));
            } else {
                Message defaultState = formatInfoTranslation(playerLang, "tport.quickEditInventories.openTPortLogSelectionGUI.player.default");
                currentState = formatInfoTranslation(playerLang, "tport.quickEditInventories.openTPortLogSelectionGUI.player.defaultState", tport.getLogMode(uuid), defaultState);
            }
            Message nextState = formatInfoTranslation(playerLang, "tport.quickEditInventories.openTPortLogSelectionGUI.player.nextLogState", LEFT, tport.getLogMode(uuid).getNext());
            Message delete = !isPlayerLogged ? null : formatInfoTranslation(playerLang, "tport.quickEditInventories.openTPortLogSelectionGUI.player.remove", RIGHT, playerName);
            setCustomItemData(playerHead, colorTheme, title, Arrays.asList(currentState, nextState, delete));
            
            headItems.add(playerHead);
        }
        
        FancyInventory inv = getDynamicScrollableInventory(
                player,
                page,
                QuickEditInventories::openTPortLogSelectorGUI,
                formatInfoTranslation("tport.quickEditInventories.openTPortLogSelectionGUI.title", asTPort(tport)),
                headItems,
                createBack(player, MAIN, OWN, TPORT_LOG));
        inv.setData("content", headItems);
        
        inv.setItem(0, getCornerTPortIcon(tport, colorTheme, playerLang));
        inv.setData("tport", tport);
        inv.setData("tportUUID", tport.getTportID());
        
        inv.setItem(inv.getSize() / 18 * 9,
                ItemFactory.getSortingItem(player, getPlayerLang(player.getUniqueId()), ColorTheme.getTheme(player),
                        ((whoClicked, clickType, pdc, fancyInventory) -> openTPortLogSelectorGUI(whoClicked, fancyInventory.getData("tport", TPort.class), 0, null))));
        
        inv.open(player);
    }
    private static void openTPortLogSelectorGUI(Player player, int page, FancyInventory prevWindow) {
        openTPortLogSelectorGUI(player, prevWindow.getData("tport", TPort.class), page, prevWindow);
    }
    public static void openTPortLogGUI(Player player, TPort tport) {
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        
        ItemStack editItem = quick_edit_log_edit_model.getItem(player);
        Message editTitle = formatInfoTranslation(playerLang, "tport.quickEditInventories.openTPortLogGUI.edit.title");
        setCustomItemData(editItem, colorTheme, editTitle, null);
        addFunction(editItem, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> openTPortLogSelectorGUI(whoClicked, fancyInventory.getData("tport", TPort.class), 0, null)));
        
        ItemStack readItem = quick_edit_log_read_model.getItem(player);
        Message readTitle = formatInfoTranslation(playerLang, "tport.quickEditInventories.openTPortLogGUI.read.title");
        Message readInventory = formatInfoTranslation(playerLang, "tport.quickEditInventories.openTPortLogGUI.read.inventory", LEFT);
        Message readChat = formatInfoTranslation(playerLang, "tport.quickEditInventories.openTPortLogGUI.read.chat", SHIFT_LEFT);
        setCustomItemData(readItem, colorTheme, readTitle, List.of(readInventory, readChat));
        addFunction(readItem, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> openTPortLogReadGUI(whoClicked, fancyInventory.getData("tport", TPort.class))));
        addFunction(readItem, SHIFT_LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> Read.readLog_chat(whoClicked, fancyInventory.getData("tport", TPort.class))));
        
        ItemStack clearItem = quick_edit_log_clear_model.getItem(player);
        Message clearTitle = formatInfoTranslation(playerLang, "tport.quickEditInventories.openTPortLogGUI.clear.title", LEFT);
        setCustomItemData(clearItem, colorTheme, clearTitle, null);
        addCommand(clearItem, LEFT, "tport log clear " + tport.getName());
        
        ItemStack defaultItem = tport.getDefaultLogMode().getModel().getItem(player);
        Message defaultTitle = formatInfoTranslation(playerLang, "tport.quickEditInventories.openTPortLogGUI.default.title", tport.getDefaultLogMode());
        Message defaultLore = formatInfoTranslation(playerLang, "tport.quickEditInventories.openTPortLogGUI.default.lore", LEFT, tport.getDefaultLogMode().getNext());
        setCustomItemData(defaultItem, colorTheme, defaultTitle, List.of(defaultLore));
        addCommand(defaultItem, LEFT, "tport log default " + tport.getName() + " " + tport.getDefaultLogMode().getNext());
        addFunction(defaultItem, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> openTPortLogGUI(whoClicked, fancyInventory.getData("tport", TPort.class))));
        
        FancyInventory inv = new FancyInventory(27, formatInfoTranslation("tport.quickEditInventories.openTPortLogGUI.title", tport));
        inv.setData("tportUUID", tport.getTportID());
        inv.setData("tport", tport);
        
        inv.setItem(0, getCornerTPortIcon(tport, colorTheme, playerLang));
        inv.setItem(10, editItem);
        inv.setItem(12, readItem);
        inv.setItem(13, clearItem);
        inv.setItem(15, defaultItem);
        inv.setItem(17, createBack(player, MAIN, OWN, QUICK_EDIT));
        
        inv.open(player);
    }
    
    public enum QuickEditType {//todo add item selection, offer TPort
        PRIVATE("Private State", tport -> tport.getPrivateState().getInventoryModel(), false, (tport, player, fancyInventory) -> openTPortPrivateGUI(player, tport, fancyInventory)),
        WHITELIST("Whitelist", quick_edit_whitelist_model, false, (tport, player, fancyInventory) -> {
            openTPortWhitelistSelectorGUI(player, tport, 0, null);
        }),
        WHITELIST_VISIBILITY("Whitelist Visibility", tport -> tport.getWhitelistVisibility().getModel(),
                (tport, player, fancyInventory) -> {
            TPortCommand.executeTPortCommand(player, new String[]{"edit", tport.getName(), "whitelist", "visibility", tport.getWhitelistVisibility().getNext().name()});
        }),
        RANGE("Range", quick_edit_range_model, false, (tport, player, fancyInventory) -> openTPortRangeGUI(player, tport)),
        MOVE("Move", quick_edit_move_model, false, (moveToTPort, player, fancyInventory) -> {
            UUID toMoveTPort = fancyInventory.getData("TPortToMove", UUID.class, null);
            if (toMoveTPort == null) {
                fancyInventory.setData("TPortToMove", moveToTPort.getTportID());
            } else {
                fancyInventory.setData("TPortToMove", null);
                if (!toMoveTPort.equals(moveToTPort.getTportID())) {
                    TPort tmpTPort = TPortManager.getTPort(toMoveTPort);
                    if (tmpTPort != null) {
                        TPortCommand.executeTPortCommand(player, new String[]{"edit", tmpTPort.getName() , "move", moveToTPort.getName()});
                    }
                }
            }
            TPortInventories.openTPortGUI(player.getUniqueId(), player, fancyInventory);
        }),
        LOG("Log", quick_edit_log_model, false, (tport, player, fancyInventory) -> openTPortLogGUI(player, tport)),
        NOTIFY("Notify", tport -> tport.getNotifyMode().getModel(), (tport, player, fancyInventory) -> TPortCommand.executeTPortCommand(player, new String[]{"log", "notify", tport.getName(), tport.getNotifyMode().getNext().name()})),
        PREVIEW("Preview State", tport -> tport.getPreviewState().getModel(), (tport, player, fancyInventory) -> {
            if (Features.Feature.Preview.isEnabled()) {
                TPortCommand.executeTPortCommand(player, new String[]{"edit", tport.getName(), "preview", tport.getPreviewState().getNext().name()});
            } else {
                Features.Feature.Preview.sendDisabledMessage(player);
            }
        }),
        TAG("Tag", quick_edit_tag_model, false, (tport, player, fancyInventory) -> openTPortTagSelectorGUI(player, tport, 0)),
        REMOVE("Remove TPort", quick_edit_remove_model, false, (tport, player, fancyInventory) -> openTPortRemoveGUI(player, tport, fancyInventory)),
        LOCATION("Location", quick_edit_location_model, false, (tport, player, fancyInventory) -> openTPortLocationGUI(player, tport, fancyInventory)),
        NAME("Name", quick_edit_name_model, false, (tport, player, fancyInventory) -> {
            FancyClickEvent.FancyClickRunnable onAccept = ((whoClicked, clickType, pdc, keyboardInventory) -> {
                String newTPortName = getKeyboardOutput(keyboardInventory);
                String tportName = keyboardInventory.getData("tportName", String.class, "");
                TPortCommand.executeTPortCommand(whoClicked, new String[] {"edit", tportName, "name", newTPortName});
                openTPortGUI(whoClicked.getUniqueId(), whoClicked, null);
            });
            FancyClickEvent.FancyClickRunnable onReject = ((whoClicked, clickType, pdc, keyboardInventory) -> {
                openTPortGUI(whoClicked.getUniqueId(), whoClicked, null);
            });
            FancyInventory inv = KeyboardGUI.openKeyboard(player, onAccept, onReject, KeyboardGUI.TEXT_ONLY);
            inv.setData("tportName", tport.getName());
        }),
        DESCRIPTION("Description", quick_edit_description_model, false, (tport, player, fancyInventory) -> {
            FancyClickEvent.FancyClickRunnable onAccept = ((whoClicked, clickType, pdc, keyboardInventory) -> {
                String description = getKeyboardOutput(keyboardInventory);
                description = description.replace("\n", "\\n");
                
                String tportName = keyboardInventory.getData("tportName", String.class, "");
                TPortCommand.executeTPortCommand(whoClicked, new String[] {"edit", tportName, "description", "set", description});
                openTPortGUI(whoClicked.getUniqueId(), whoClicked, null);
            });
            FancyClickEvent.FancyClickRunnable onReject = ((whoClicked, clickType, pdc, keyboardInventory) -> {
                openTPortGUI(whoClicked.getUniqueId(), whoClicked, null);
            });
            FancyInventory inv = KeyboardGUI.openKeyboard(player, onAccept, onReject, KeyboardGUI.SPACE | KeyboardGUI.NEWLINE | KeyboardGUI.COLOR);
            inv.setData("tportName", tport.getName());
            inv.setData("defColor", "#5555ff");
        }),
        PUBLIC_TP("PublicTP", QuickEditType::getPublicTPortModel, true, ((tport, player, fancyInventory) -> {
            if (Features.Feature.PublicTP.isEnabled()) {
                if (tport.isPublicTPort()) {
                    TPortCommand.executeTPortCommand(player, new String[]{"public", "remove", tport.getName()});
                } else {
                    TPortCommand.executeTPortCommand(player, new String[]{"public", "add", tport.getName()});
                }
            } else {
                Features.Feature.PublicTP.sendDisabledMessage(player);
            }
        })),
        DYNMAP_SHOW("Dynmap Show", QuickEditType::getDynmapShowModel, (tport, player, fancyInventory) -> {
            if (DynmapHandler.isEnabled()) {
                TPortCommand.executeTPortCommand(player, new String[]{"edit", tport.getName(), "dynmap", "show", String.valueOf(!tport.showOnDynmap())});
            } else {
                DynmapCommand.sendDisableError(player);
            }
        }),
        DYNMAP_ICON("Dynmap Icon", DynmapHandler.isEnabled() ? quick_edit_dynmap_icon_model : quick_edit_dynmap_icon_grayed_model, false,
                (tport, player, fancyInventory) -> {
            if (DynmapHandler.isEnabled()) {
                openTPortDynmapIconGUI(player, tport, 0);
            } else {
                DynmapCommand.sendDisableError(player);
            }
        });
        
        private final QuickEditor editor;
        private final String displayName;
        private final boolean reopen;
        private final ModelSelector modelSelector;
        
        public static HashMap<UUID, QuickEditType> map = new HashMap<>();
        
        QuickEditType(String displayName, ModelSelector modelSelector, QuickEditor run) {
            this.editor = run;
            this.displayName = displayName;
            this.modelSelector = modelSelector;
            reopen = true;
        }
        QuickEditType(String displayName, ModelSelector modelSelector, boolean reopen, QuickEditor run) {
            this.editor = run;
            this.displayName = displayName;
            this.modelSelector = modelSelector;
            this.reopen = reopen;
        }
        QuickEditType(String displayName, InventoryModel model, QuickEditor run) {
            this.editor = run;
            this.displayName = displayName;
            this.modelSelector = (TPort) -> model;
            reopen = true;
        }
        QuickEditType(String displayName, InventoryModel model, boolean reopen, QuickEditor run) {
            this.editor = run;
            this.displayName = displayName;
            this.modelSelector = (TPort) -> model;
            this.reopen = reopen;
        }
        
        public static QuickEditType getForPlayer(UUID uuid) {
            return get(tportData.getConfig().getString("tport." + uuid + ".editState", null));
        }
        public static void setForPlayer(UUID uuid, QuickEditType type) {
            tportData.getConfig().set("tport." + uuid + ".editState", type.name());
            tportData.saveConfig();
        }
        
        public static QuickEditType get(@Nullable String name) {
            if (name == null) return PRIVATE;
            try {
                return QuickEditType.valueOf(name.toUpperCase());
            } catch (IllegalArgumentException iae) {
                return PRIVATE;
            }
        }
        
        public boolean edit(TPort tport, Player player, FancyInventory fancyInventory) {
            this.editor.edit(tport, player, fancyInventory);
            return reopen;
        }
        
        public InventoryModel getModel(TPort tport) {
            return modelSelector.selectModel(tport);
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public QuickEditType getNext() {
            boolean next = false;
            boolean ignoreDynmap = !DynmapHandler.isEnabled();
            for (QuickEditType type : values()) {
                if (type.equals(this)) {
                    next = true;
                } else if (next) {
                    if (!Features.Feature.Preview.isEnabled()) {
                        if (type == PREVIEW) continue;
                    }
                    if (ignoreDynmap) {
                        if (type == DYNMAP_ICON || type == DYNMAP_SHOW) continue;
                    }
                    return type;
                }
            }
            return Arrays.asList(values()).get(0);
        }
        
        private static InventoryModel getDynmapShowModel(TPort tport) {
            if (!DynmapHandler.isEnabled()) {
                return quick_edit_dynmap_show_grayed_model;
            }
            return tport.showOnDynmap() ? quick_edit_dynmap_show_on_model : quick_edit_dynmap_show_off_model;
        }
        public static InventoryModel getPublicTPortModel(TPort tport) {
            if (Features.Feature.PublicTP.isDisabled()) {
                return quick_edit_public_tp_grayed_model;
            }
            return tport.isPublicTPort() ? quick_edit_public_tp_on_model : quick_edit_public_tp_off_model;
        }
        
        @FunctionalInterface
        private interface QuickEditor {
            void edit(TPort tport, Player player, FancyInventory fancyInventory);
        }
        @FunctionalInterface
        private interface ModelSelector {
            InventoryModel selectModel(TPort tport);
        }
    }
}
