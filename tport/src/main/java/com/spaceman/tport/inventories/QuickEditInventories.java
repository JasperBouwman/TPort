package com.spaceman.tport.inventories;

import com.google.gson.JsonObject;
import com.spaceman.tport.Glow;
import com.spaceman.tport.Main;
import com.spaceman.tport.Pair;
import com.spaceman.tport.advancements.TPortAdvancement;
import com.spaceman.tport.commands.TPortCommand;
import com.spaceman.tport.commands.tport.BlueMapCommand;
import com.spaceman.tport.commands.tport.DynmapCommand;
import com.spaceman.tport.commands.tport.Features;
import com.spaceman.tport.commands.tport.Tag;
import com.spaceman.tport.commands.tport.edit.Item;
import com.spaceman.tport.commands.tport.log.Read;
import com.spaceman.tport.commands.tport.log.TimeFormat;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fancyMessage.colorTheme.MultiColor;
import com.spaceman.tport.fancyMessage.inventories.FancyClickEvent;
import com.spaceman.tport.fancyMessage.inventories.FancyInventory;
import com.spaceman.tport.fancyMessage.inventories.InventoryModel;
import com.spaceman.tport.fancyMessage.inventories.WaypointModel;
import com.spaceman.tport.fancyMessage.inventories.keyboard.KeyboardGUI;
import com.spaceman.tport.fancyMessage.language.Language;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import com.spaceman.tport.waypoint.WaypointModels;
import com.spaceman.tport.webMaps.BlueMapHandler;
import com.spaceman.tport.webMaps.DynmapHandler;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import static com.spaceman.tport.commands.TPortCommand.executeTPortCommand;
import static com.spaceman.tport.fancyMessage.MessageUtils.setCustomItemData;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;
import static com.spaceman.tport.fancyMessage.encapsulation.TPortEncapsulation.asTPort;
import static com.spaceman.tport.fancyMessage.inventories.FancyClickEvent.*;
import static com.spaceman.tport.fancyMessage.inventories.FancyInventory.getDynamicScrollableInventory;
import static com.spaceman.tport.fancyMessage.inventories.FancyInventory.pageDataName;
import static com.spaceman.tport.fancyMessage.inventories.keyboard.KeyboardGUI.ALL;
import static com.spaceman.tport.fancyMessage.inventories.keyboard.KeyboardGUI.getKeyboardOutput;
import static com.spaceman.tport.fancyMessage.language.Language.getPlayerLang;
import static com.spaceman.tport.fileHander.Files.tportData;
import static com.spaceman.tport.inventories.ItemFactory.BackType.*;
import static com.spaceman.tport.inventories.ItemFactory.HeadAttributes.*;
import static com.spaceman.tport.inventories.ItemFactory.*;
import static com.spaceman.tport.inventories.TPortInventories.openTPortGUI;
import static com.spaceman.tport.tport.TPort.*;
import static com.spaceman.tport.webMaps.DynmapHandler.tport_dynmap_icon;
import static org.bukkit.event.inventory.ClickType.*;
import static org.bukkit.persistence.PersistentDataType.STRING;

public class QuickEditInventories {
    
    public static final InventoryModel quick_edit_private_open_model = new InventoryModel(Material.OAK_BUTTON, TPortInventories.last_model_id + 1, "tport", "quick_edit_private_open", "quick_edit");
    public static final InventoryModel quick_edit_private_private_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_private_open_model, "tport", "quick_edit_private_private", "quick_edit");
    public static final InventoryModel quick_edit_private_online_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_private_private_model, "tport", "quick_edit_private_online", "quick_edit");
    public static final InventoryModel quick_edit_private_private_online_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_private_online_model, "tport", "quick_edit_private_private_online", "quick_edit");
    public static final InventoryModel quick_edit_private_consent_private_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_private_private_online_model, "tport", "quick_edit_private_consent_private", "quick_edit");
    public static final InventoryModel quick_edit_private_consent_close_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_private_consent_private_model, "tport", "quick_edit_private_consent_close", "quick_edit");
    public static final InventoryModel quick_edit_whitelist_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_private_consent_close_model, "tport", "quick_edit_whitelist", "quick_edit");
    public static final InventoryModel quick_edit_whitelist_visibility_on_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_whitelist_model, "tport", "quick_edit_whitelist_visibility_on", "quick_edit");
    public static final InventoryModel quick_edit_whitelist_visibility_off_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_whitelist_visibility_on_model, "tport", "quick_edit_whitelist_visibility_off", "quick_edit");
    public static final InventoryModel quick_edit_whitelist_clone_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_whitelist_visibility_off_model, "tport", "quick_edit_whitelist_clone", "quick_edit");
    public static final InventoryModel quick_edit_range_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_whitelist_clone_model, "tport", "quick_edit_range", "quick_edit");
    public static final InventoryModel quick_edit_range_add_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_range_model, "tport", "quick_edit_range_add", "quick_edit");
    public static final InventoryModel quick_edit_range_remove_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_range_add_model, "tport", "quick_edit_range_remove", "quick_edit");
    public static final InventoryModel quick_edit_move_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_range_remove_model, "tport", "quick_edit_move", "quick_edit");
    public static final InventoryModel quick_edit_move_empty_slot_model = new InventoryModel(Material.GRAY_STAINED_GLASS_PANE, quick_edit_move_model, "tport", "quick_edit_move_empty_slot", "quick_edit");
    public static final InventoryModel quick_edit_log_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_move_empty_slot_model, "tport", "quick_edit_log", "quick_edit");
    public static final InventoryModel quick_edit_log_edit_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_log_model, "tport", "quick_edit_log_edit", "quick_edit");
    public static final InventoryModel quick_edit_log_delete_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_log_edit_model, "tport", "quick_edit_log_delete", "quick_edit");
    public static final InventoryModel quick_edit_log_read_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_log_delete_model, "tport", "quick_edit_log_read", "quick_edit");
    public static final InventoryModel quick_edit_log_read_filter_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_log_read_model, "tport", "quick_edit_log_read_filter", "quick_edit");
    public static final InventoryModel quick_edit_log_read_chat_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_log_read_filter_model, "tport", "quick_edit_log_read_chat", "quick_edit");
    public static final InventoryModel quick_edit_log_clear_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_log_read_chat_model, "tport", "quick_edit_log_clear", "quick_edit");
    public static final InventoryModel quick_edit_log_mode_online_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_log_clear_model, "tport", "quick_edit_log_mode_online", "quick_edit");
    public static final InventoryModel quick_edit_log_mode_offline_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_log_mode_online_model, "tport", "quick_edit_log_mode_offline", "quick_edit");
    public static final InventoryModel quick_edit_log_mode_all_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_log_mode_offline_model, "tport", "quick_edit_log_mode_all", "quick_edit");
    public static final InventoryModel quick_edit_log_mode_none_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_log_mode_all_model, "tport", "quick_edit_log_mode_none", "quick_edit");
    public static final InventoryModel quick_edit_notify_online_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_log_mode_none_model, "tport", "quick_edit_notify_online", "quick_edit");
    public static final InventoryModel quick_edit_notify_log_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_notify_online_model, "tport", "quick_edit_notify_log", "quick_edit");
    public static final InventoryModel quick_edit_notify_none_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_notify_log_model, "tport", "quick_edit_notify_none", "quick_edit");
    public static final InventoryModel quick_edit_preview_on_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_notify_none_model, "tport", "quick_edit_preview_on", "quick_edit");
    public static final InventoryModel quick_edit_preview_off_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_preview_on_model, "tport", "quick_edit_preview_off", "quick_edit");
    public static final InventoryModel quick_edit_preview_notified_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_preview_off_model, "tport", "quick_edit_preview_notified", "quick_edit");
    public static final InventoryModel quick_edit_preview_grayed_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_preview_notified_model, "tport", "quick_edit_preview_grayed", "quick_edit");
    public static final InventoryModel quick_edit_tag_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_preview_grayed_model, "tport", "quick_edit_tag", "quick_edit");
    public static final InventoryModel quick_edit_tag_selection_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_tag_model, "tport", "quick_edit_tag_selection", "quick_edit");
    public static final InventoryModel quick_edit_remove_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_tag_selection_model, "tport", "quick_edit_remove", "quick_edit");
    public static final InventoryModel quick_edit_remove_confirm_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_remove_model, "tport", "quick_edit_remove_confirm", "quick_edit");
    public static final InventoryModel quick_edit_remove_cancel_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_remove_confirm_model, "tport", "quick_edit_remove_cancel", "quick_edit");
    public static final InventoryModel quick_edit_location_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_remove_cancel_model, "tport", "quick_edit_location", "quick_edit");
    public static final InventoryModel quick_edit_location_confirm_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_location_model, "tport", "quick_edit_location_confirm", "quick_edit");
    public static final InventoryModel quick_edit_location_cancel_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_location_confirm_model, "tport", "quick_edit_location_cancel", "quick_edit");
    public static final InventoryModel quick_edit_name_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_location_cancel_model, "tport", "quick_edit_name", "quick_edit");
    public static final InventoryModel quick_edit_description_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_name_model, "tport", "quick_edit_description", "quick_edit");
    public static final InventoryModel quick_edit_public_tp_on_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_description_model, "tport", "quick_edit_public_tp_on", "quick_edit");
    public static final InventoryModel quick_edit_public_tp_off_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_public_tp_on_model, "tport", "quick_edit_public_tp_off", "quick_edit");
    public static final InventoryModel quick_edit_public_tp_grayed_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_public_tp_off_model, "tport", "quick_edit_public_tp_grayed", "quick_edit");
    public static final InventoryModel quick_edit_dynmap_show_on_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_public_tp_grayed_model, "tport", "quick_edit_dynmap_show_on", "quick_edit");
    public static final InventoryModel quick_edit_dynmap_show_off_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_dynmap_show_on_model, "tport", "quick_edit_dynmap_show_off", "quick_edit");
    public static final InventoryModel quick_edit_dynmap_show_grayed_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_dynmap_show_off_model, "tport", "quick_edit_dynmap_show_grayed", "quick_edit");
    public static final InventoryModel quick_edit_dynmap_icon_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_dynmap_show_grayed_model, "tport", "quick_edit_dynmap_icon", "quick_edit");
    public static final InventoryModel quick_edit_dynmap_icon_tport_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_dynmap_icon_model, "tport", "quick_edit_dynmap_icon_tport", "quick_edit");
    public static final InventoryModel quick_edit_dynmap_icon_grayed_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_dynmap_icon_tport_model, "tport", "quick_edit_dynmap_icon_grayed", "quick_edit");
    public static final InventoryModel quick_edit_bluemap_show_on_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_dynmap_icon_grayed_model, "tport", "quick_edit_bluemap_show_on", "quick_edit");
    public static final InventoryModel quick_edit_bluemap_show_off_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_bluemap_show_on_model, "tport", "quick_edit_bluemap_show_off", "quick_edit");
    public static final InventoryModel quick_edit_bluemap_show_grayed_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_bluemap_show_off_model, "tport", "quick_edit_bluemap_show_grayed", "quick_edit");
    public static final InventoryModel quick_edit_bluemap_icon_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_bluemap_show_grayed_model, "tport", "quick_edit_bluemap_icon", "quick_edit");
    public static final InventoryModel quick_edit_bluemap_icon_tport_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_bluemap_icon_model, "tport", "quick_edit_bluemap_icon_tport", "quick_edit");
    public static final InventoryModel quick_edit_bluemap_icon_grayed_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_bluemap_icon_tport_model, "tport", "quick_edit_bluemap_icon_grayed", "quick_edit");
    public static final InventoryModel quick_edit_offer_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_bluemap_icon_grayed_model, "tport", "quick_edit_offer", "quick_edit");
    public static final InventoryModel quick_edit_revoke_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_offer_model, "tport", "quick_edit_revoke", "quick_edit");
    public static final InventoryModel quick_edit_item_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_revoke_model, "tport", "quick_edit_item", "quick_edit");
    public static final InventoryModel quick_edit_item_reload_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_item_model, "tport", "quick_edit_item_reload", "quick_edit");
    
    public static final InventoryModel quick_edit_waypoint_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_item_reload_model, "tport", "quick_edit_waypoint", "quick_edit");
    public static final InventoryModel quick_edit_waypoint_grayed_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_waypoint_model, "tport", "quick_edit_waypoint_grayed", "quick_edit");
    public static final InventoryModel quick_edit_waypoint_show_show_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_waypoint_grayed_model, "tport", "quick_edit_waypoint_show_show", "quick_edit");
    public static final InventoryModel quick_edit_waypoint_show_hide_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_waypoint_show_show_model, "tport", "quick_edit_waypoint_show_hide", "quick_edit");
    public static final InventoryModel quick_edit_waypoint_show_whitelist_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_waypoint_show_hide_model, "tport", "quick_edit_waypoint_show_whitelist", "quick_edit");
    
    public static final InventoryModel quick_edit_waypoint_icon_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_waypoint_show_whitelist_model, "tport", "quick_edit_waypoint_icon", "quick_edit");
    
    public static final InventoryModel quick_edit_waypoint_color_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_waypoint_icon_model, "tport", "quick_edit_waypoint_color", "quick_edit");
    public static final InventoryModel quick_edit_waypoint_color_accept_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_waypoint_color_model, "tport", "quick_edit_waypoint_color_accept", "quick_edit");
    public static final InventoryModel quick_edit_waypoint_color_reject_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_waypoint_color_accept_model, "tport", "quick_edit_waypoint_color_reject", "quick_edit");
    public static final InventoryModel quick_edit_waypoint_color_red_add_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_waypoint_color_reject_model, "tport", "quick_edit_waypoint_color_red_add", "quick_edit");
    public static final InventoryModel quick_edit_waypoint_color_red_remove_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_waypoint_color_red_add_model, "tport", "quick_edit_waypoint_color_red_remove", "quick_edit");
    public static final InventoryModel quick_edit_waypoint_color_green_add_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_waypoint_color_red_remove_model, "tport", "quick_edit_waypoint_color_green_add", "quick_edit");
    public static final InventoryModel quick_edit_waypoint_color_green_remove_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_waypoint_color_green_add_model, "tport", "quick_edit_waypoint_color_green_remove", "quick_edit");
    public static final InventoryModel quick_edit_waypoint_color_blue_add_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_waypoint_color_green_remove_model, "tport", "quick_edit_waypoint_color_blue_add", "quick_edit");
    public static final InventoryModel quick_edit_waypoint_color_blue_remove_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_waypoint_color_blue_add_model, "tport", "quick_edit_waypoint_color_blue_remove", "quick_edit");
    public static final InventoryModel quick_edit_waypoint_color_chat_color_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_waypoint_color_blue_remove_model, "tport", "quick_edit_waypoint_color_chat_color", "quick_edit");
    public static final InventoryModel quick_edit_waypoint_color_chat_color_reject_model = new InventoryModel(Material.OAK_BUTTON, quick_edit_waypoint_color_chat_color_model, "tport", "quick_edit_waypoint_color_chat_color_reject", "quick_edit");
    
    
    public static final int last_model_id = quick_edit_waypoint_color_chat_color_reject_model.getCustomModelData();
    
    private static final FancyInventory.DataName<Boolean> fromQuickEditDataName = new FancyInventory.DataName<>("fromQuickEdit", Boolean.class, false);
    
    public static void openQuickEditSelection(Player player, int page, UUID tportUUID) {
        JsonObject playerLang = Language.getPlayerLang(player.getUniqueId());
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        
        TPortAdvancement.Advancement_quickEdit.grant(player);
        
        QuickEditType currentType = QuickEditType.getForPlayer(player.getUniqueId());
        TPort tport = TPortManager.getTPort(player.getUniqueId(), tportUUID);
        
        ArrayList<ItemStack> quickEdits = new ArrayList<>();
        for (QuickEditType quickEdit : QuickEditType.values()) {
            ItemStack item = new ItemStack(quickEdit.getModel(tport).getItem(player));
            
            Message itemTitle = formatInfoTranslation(playerLang, "tport.quickEditInventories.openQuickEditSelection.item.title", quickEdit.getDisplayName());
            Message loreLeft = formatInfoTranslation(playerLang, "tport.quickEditInventories.openQuickEditSelection.item.click.left", LEFT);
            Message loreRight = formatInfoTranslation(playerLang, "tport.quickEditInventories.openQuickEditSelection.item.click.right", RIGHT);
            Message description = quickEdit.getDescription();
            description.translateMessage(playerLang);
            setCustomItemData(item, colorTheme, itemTitle, Arrays.asList(description, new Message(), loreLeft, loreRight));
            
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
                    openTPortGUI(whoClicked.getUniqueId(), whoClicked);
                }
            }));
            
            quickEdits.add(item);
        }
        
        ItemStack backItem = createBack(player, OWN, MAIN, null);
        Message invTitle = formatInfoTranslation("tport.quickEditInventories.openQuickEditSelection.title", asTPort(tport));
        FancyInventory inv = getDynamicScrollableInventory(player, page, QuickEditInventories::openQuickEditSelection, invTitle, quickEdits, backItem);
        inv.setData(tportUUIDDataName, tportUUID);
        inv.setData(fromQuickEditDataName, true);
        
        inv.open(player);
    }
    private static void openQuickEditSelection(Player player, int page, FancyInventory prevWindow) {
        openQuickEditSelection(player, page, prevWindow.getData(tportUUIDDataName));
    }
    
    private static ItemStack getCornerTPortIcon(TPort tport, Player player) {
        return toTPortItem(tport, player, List.of(), null);
    }
    
    public static boolean openTPortTagSelectorGUI(Player player, TPort tport, int page) {
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
                        String tportName = fancyInventory.getData(tportDataName).getName();
                        executeTPortCommand(whoClicked, "edit " + tportName + " tag remove " + tagName);
                        openTPortTagSelectorGUI(whoClicked, fancyInventory.getData(pageDataName), fancyInventory);
                    }
                });
                lore = formatInfoTranslation("tport.quickEditInventories.openTPortTagSelectionGUI.tag.unselect", LEFT);
            } else {
                FancyClickEvent.addFunction(im, LEFT, (whoClicked, clickType, pdc, fancyInventory) -> {
                    NamespacedKey tagNameKey = new NamespacedKey(Main.getInstance(), "tagName");
                    if (pdc.has(tagNameKey, PersistentDataType.STRING)) {
                        String tagName = pdc.get(tagNameKey, PersistentDataType.STRING);
                        String tportName = fancyInventory.getData(tportDataName).getName();
                        executeTPortCommand(whoClicked, "edit " + tportName + " tag add " + tagName);
                        openTPortTagSelectorGUI(whoClicked, fancyInventory.getData(pageDataName), fancyInventory);
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
                formatInfoTranslation("tport.quickEditInventories.openTPortTagSelectionGUI.title", asTPort(tport)), tagItems, createBack(player, QUICK_EDIT, OWN, MAIN));
        
        inv.setItem(0, getCornerTPortIcon(tport, player));
        inv.setData(tportUUIDDataName, tport.getTportID());
        inv.setData(tportDataName, tport);
        
        inv.open(player);
        return false;
    }
    private static void openTPortTagSelectorGUI(Player player, int page, FancyInventory fancyInventory) {
        openTPortTagSelectorGUI(player, fancyInventory.getData(tportDataName), page);
    }
    
    public static boolean openTPortWhitelistSelectorGUI(Player player, TPort tport, int page, @Nullable FancyInventory prevWindow) {
        List<ItemStack> headItems = ItemFactory.getPlayerList(player, false, true, List.of(TPORT_WHITELIST), List.of(), tport);
        
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        
        FancyInventory inv = getDynamicScrollableInventory(player, page, QuickEditInventories::openTPortWhitelistSelectorGUI,
                formatInfoTranslation("tport.quickEditInventories.openTPortWhitelistSelectionGUI.title", asTPort(tport)), headItems, createBack(player, QUICK_EDIT, OWN, MAIN));
        inv.setData("content", headItems);
        
        inv.setItem(0, getCornerTPortIcon(tport, player));
        inv.setData(tportDataName, tport);
        inv.setItem(inv.getSize() / 18 * 9,
                ItemFactory.getSortingItem(player, playerLang, colorTheme,
                        ((whoClicked, clickType, pdc, fancyInventory) -> openTPortWhitelistSelectorGUI(whoClicked, fancyInventory.getData(tportDataName), 0, null))));
        inv.setData(tportUUIDDataName, tport.getTportID());
        
        inv.open(player);
        return false;
    }
    public static void openTPortWhitelistSelectorGUI(Player player, int page, FancyInventory fancyInventory) {
        openTPortWhitelistSelectorGUI(player, fancyInventory.getData(tportDataName), page, fancyInventory);
    }
    
    public static final FancyInventory.DataName<UUID> whitelistCloneToDataName = new FancyInventory.DataName<>("whitelistCloneTo", UUID.class, null);
    private static boolean onWhitelistClone(TPort toCloneTPort, Player player, FancyInventory fancyInventory) {
        UUID cloneToTPortUUID = fancyInventory.getData(whitelistCloneToDataName);
        if (cloneToTPortUUID == null) {
            QuickEditType.setForPlayer(player.getUniqueId(), QuickEditType.WHITELIST_CLONE);
            fancyInventory.setData(whitelistCloneToDataName, toCloneTPort.getTportID());
            sendInfoTranslation(player, "tport.quickEditInventories.onWhitelistClone.firstSelection", toCloneTPort);
        } else {
            fancyInventory.setData(whitelistCloneToDataName, null);
            if (!cloneToTPortUUID.equals(toCloneTPort.getTportID())) {
                TPort cloneTo = TPortManager.getTPort(cloneToTPortUUID);
                if (cloneTo != null) {
                    TPortCommand.executeTPortCommand(player, new String[]{"edit", cloneTo.getName() , "whitelist", "clone", toCloneTPort.getName()});
                }
            }
        }
        TPortInventories.openTPortGUI(player.getUniqueId(), player, fancyInventory);
        return false;
    }
    
    private static boolean openTPortRemoveGUI(Player player, TPort tport, FancyInventory prevWindow) {
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        
        Message title = formatInfoTranslation("tport.quickEditInventories.openTPortRemoveGUI.title", asTPort(tport));
        FancyInventory inv = new FancyInventory(27, title);
        
        inv.setItem(13, getCornerTPortIcon(tport, player));
        
        ItemStack confirm = quick_edit_remove_confirm_model.getItem(player);
        addCommand(confirm, LEFT, "tport remove " + tport.getName(), "tport own");
        Message confirmTitle = formatInfoTranslation("tport.quickEditInventories.openTPortRemoveGUI.confirm.title", LEFT, asTPort(tport));
        confirmTitle.translateMessage(playerLang);
        setCustomItemData(confirm, colorTheme, confirmTitle, null);
        inv.setItem(11, confirm);
        
        ItemStack cancel = quick_edit_remove_cancel_model.getItem(player);
        ArrayList<Message> cancelLore = new ArrayList<>();
        cancelLore.add(new Message());
        ItemFactory.BackType leftBackType;
        ItemFactory.BackType rightBackType;
        ItemFactory.BackType shift_rightBackType = null;
        if (prevWindow.getData(fromQuickEditDataName)) {
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
        
        Message cancelTitle = formatInfoTranslation("tport.quickEditInventories.openTPortRemoveGUI.cancel.title", asTPort(tport));
        cancelTitle.translateMessage(playerLang);
        setCustomItemData(cancel, colorTheme, cancelTitle, cancelLore);
        inv.setItem(15, cancel);
        inv.setData(tportUUIDDataName, tport.getTportID());
        
        inv.open(player);
        return false;
    }
    
    private static boolean openTPortLocationGUI(Player player, TPort tport, FancyInventory prevWindow) {
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        
        Message title = formatInfoTranslation("tport.quickEditInventories.openTPortLocationGUI.title", asTPort(tport));
        FancyInventory inv = new FancyInventory(27, title);
        
        inv.setItem(13, getCornerTPortIcon(tport, player));
        
        ItemStack confirm = quick_edit_location_confirm_model.getItem(player);
        addCommand(confirm, LEFT, "tport edit " + tport.getName() + " location", "tport own");
        Message confirmTitle = formatInfoTranslation("tport.quickEditInventories.openTPortLocationGUI.confirm.title", LEFT, asTPort(tport));
        confirmTitle.translateMessage(playerLang);
        setCustomItemData(confirm, colorTheme, confirmTitle, null);
        inv.setItem(11, confirm);
        
        
        ItemStack cancel = quick_edit_location_cancel_model.getItem(player);
        ArrayList<Message> cancelLore = new ArrayList<>();
        cancelLore.add(new Message());
        ItemFactory.BackType leftBackType;
        ItemFactory.BackType rightBackType;
        ItemFactory.BackType shift_rightBackType = null;
        if (prevWindow.getData(fromQuickEditDataName)) {
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
        
        Message cancelTitle = formatInfoTranslation("tport.quickEditInventories.openTPortLocationGUI.cancel.title", asTPort(tport));
        cancelTitle.translateMessage(playerLang);
        setCustomItemData(cancel, colorTheme, cancelTitle, cancelLore);
        inv.setItem(15, cancel);
        inv.setData(tportUUIDDataName, tport.getTportID());
        
        inv.open(player);
        return false;
    }
    
    private static boolean openTPortPrivateGUI(Player player, TPort tport, FancyInventory prevWindow) {
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
            setCustomItemData(is, colorTheme, privateState.getName(null, null), Arrays.asList(leftClickMessage, rightClickMessage));
            
            FancyClickEvent.setStringData(is, new NamespacedKey(Main.getInstance(), "privateState"), privateState.name());
            addCommand(is, LEFT, "tport edit " + tport.getName() + " private " + privateState.name());
            addFunction(is, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
                openTPortPrivateGUI(whoClicked, fancyInventory.getData(tportDataName), fancyInventory);
            }));
            addFunction(is, RIGHT, ((whoClicked, clickType, pdc, fancyInventory) -> {
                String ps = pdc.get(new NamespacedKey(Main.getInstance(), "privateState"), STRING);
                TPort.PrivateState.get(ps, TPort.PrivateState.OPEN).getDescription().sendAndTranslateMessage(whoClicked);
            }));
            
            inv.setItem(itemIndex++, is);
        }
        
        inv.setItem(17, createBack(player, QUICK_EDIT, OWN, MAIN));
        inv.setData(tportUUIDDataName, tport.getTportID());
        inv.setData(tportDataName, tport);
        
        inv.setItem(0, getCornerTPortIcon(tport, player));
        
        inv.open(player);
        return false;
    }
    
    private static int newRange(TPort tport, int add) {
        return Math.max(0, tport.getRange() + add);
    }
    private static boolean openTPortRangeGUI(Player player, TPort tport) {
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
            UUID tportUUID = fancyInventory.getData(tportUUIDDataName);
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
            UUID tportUUID = fancyInventory.getData(tportUUIDDataName);
            openTPortRangeGUI(whoClicked, TPortManager.getTPort(whoClicked.getUniqueId(), tportUUID));
        }), LEFT, RIGHT, SHIFT_LEFT, SHIFT_RIGHT);
        
        FancyInventory inv = new FancyInventory(27, formatInfoTranslation("tport.quickEditInventories.openTPortRangeGUI.title", tport));
        inv.setData(tportUUIDDataName, tport.getTportID());
        
        inv.setItem(0, getCornerTPortIcon(tport, player));
        inv.setItem(11, rangeRemove);
        inv.setItem(13, rangeDisplay);
        inv.setItem(15, rangeAdd);
        inv.setItem(17, createBack(player, QUICK_EDIT, OWN, MAIN));
        
        inv.open(player);
        return false;
    }
    
    private static void openTPortDynmapIconGUI(Player player, TPort tport, int page) {
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        
        List<Pair<String, String>> icons = DynmapHandler.getIcons();
        ArrayList<ItemStack> items = new ArrayList<>();
        
        if (icons != null) {
            icons.sort(Comparator.comparing(Pair::getLeft));
            for (Pair<String, String> icon : icons) {
                ItemStack is = (icon.getLeft().equals(tport_dynmap_icon) ? quick_edit_dynmap_icon_tport_model : quick_edit_dynmap_icon_model).getItem(player);
                
                Message markerTitle = formatInfoTranslation(playerLang, "tport.quickEditInventories.openTPortDynmapIconGUI.icon.title", icon.getRight());
                Message markerLeft = formatInfoTranslation(playerLang,"tport.quickEditInventories.openTPortDynmapIconGUI.icon.leftClick", LEFT);
                setCustomItemData(is, colorTheme, markerTitle, List.of(markerLeft));
                
                addCommand(is, LEFT, "tport edit " + tport.getName() + " dynmap icon " + icon.getRight());
                
                if (icon.getLeft().equals(tport_dynmap_icon)) items.add(0, is);
                else items.add(is);
            }
        }
        
        FancyInventory inv = getDynamicScrollableInventory(player, page, QuickEditInventories::openTPortDynmapIconGUI,
                formatInfoTranslation("tport.quickEditInventories.openTPortDynmapIconGUI.title", tport), items, createBack(player, QUICK_EDIT, OWN, MAIN));
        inv.setData(tportDataName, tport);
        inv.setData(tportUUIDDataName, tport.getTportID());
        inv.setItem(0, getCornerTPortIcon(tport, player));
        
        inv.open(player);
    }
    private static void openTPortDynmapIconGUI(Player player, int page, FancyInventory prevWindow) {
        openTPortDynmapIconGUI(player, prevWindow.getData(tportDataName), page);
    }
    
    private static void openTPortBlueMapIconGUI(Player player, TPort tport, int page) {
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        
        ArrayList<ItemStack> items = new ArrayList<>();
        
        try {
            ArrayList<String> icons = BlueMapHandler.getBlueMapImages();
            if (icons != null) {
                for (String icon : icons) {
                    ItemStack is = (icon.equals(BlueMapHandler.defaultIcon) ? quick_edit_bluemap_icon_tport_model : quick_edit_bluemap_icon_model).getItem(player);
                    
                    Message markerTitle = formatInfoTranslation(playerLang, "tport.quickEditInventories.openTPortBlueMapIconGUI.icon.title", icon);
                    Message markerLeft = formatInfoTranslation(playerLang,"tport.quickEditInventories.openTPortBlueMapIconGUI.icon.leftClick", LEFT);
                    setCustomItemData(is, colorTheme, markerTitle, List.of(markerLeft));
                    
                    addCommand(is, LEFT, "tport edit " + tport.getName() + " blueMap icon " + icon);
                    
                    if (icon.equals(BlueMapHandler.defaultIcon)) items.add(0, is);
                    else items.add(is);
                }
            }
        } catch (Throwable ignore) { }
        
        FancyInventory inv = getDynamicScrollableInventory(player, page, QuickEditInventories::openTPortBlueMapIconGUI,
                formatInfoTranslation("tport.quickEditInventories.openTPortBlueMapIconGUI.title", tport), items, createBack(player, QUICK_EDIT, OWN, MAIN));
        inv.setData(tportDataName, tport);
        inv.setData(tportUUIDDataName, tport.getTportID());
        inv.setItem(0, getCornerTPortIcon(tport, player));
        
        inv.open(player);
    }
    private static void openTPortBlueMapIconGUI(Player player, int page, FancyInventory prevWindow) {
        openTPortBlueMapIconGUI(player, prevWindow.getData(tportDataName), page);
    }
    
    private static void openTPortLogRead_filterGUI(Player player, int page, FancyInventory prevWindow) {
        openTPortLogRead_filterGUI(player, prevWindow.getData(tportDataName), prevWindow.getData("filterUUID", UUID.class), page, prevWindow);
    }
    private static void openTPortLogRead_filterGUI(Player player, TPort tport, @Nullable UUID oldFilterUUID, int page, @Nullable FancyInventory prevWindow) {
        List<ItemStack> rawHeadItems;
        if (prevWindow == null) {
            rawHeadItems = getPlayerList(player, true, true, List.of(TPORT_LOG_READ_FILTER), List.of(), null);
        } else {
            rawHeadItems = prevWindow.getData("content", List.class);
        }
        
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        JsonObject playerLang = Language.getPlayerLang(player.getUniqueId());
        
        Message title = formatInfoTranslation("tport.quickEditInventories.openTPortLogRead_filterGUI.title", tport);
        
        FancyInventory inv = getDynamicScrollableInventory(player, page, QuickEditInventories::openTPortLogRead_filterGUI, title, rawHeadItems, createBack(player, LOG_SETTINGS_READ, OWN, MAIN));
        inv.setData("content", rawHeadItems);
        inv.setData(tportDataName, tport);
        inv.setData(tportUUIDDataName, tport.getTportID());
        inv.setData("filterUUID", oldFilterUUID);
        
        inv.setItem(0, getCornerTPortIcon(tport, player));
        inv.setItem(inv.getSize() / 18 * 9,
                ItemFactory.getSortingItem(player, playerLang, colorTheme,
                        ((whoClicked, clickType, pdc, fancyInventory) -> openTPortLogRead_filterGUI(
                                whoClicked, fancyInventory.getData(tportDataName), fancyInventory.getData("filterUUID", UUID.class), 0, null))));
        
        inv.open(player);
    }
    public static void openTPortLogReadGUI(Player player, TPort tport) {
        openTPortLogReadGUI(player, tport, null, 0);
    }
    public static void openTPortLogReadGUI(Player player, TPort tport, @Nullable UUID filterUUID) {
        openTPortLogReadGUI(player, tport, filterUUID, 0);
    }
    private static void openTPortLogReadGUI(Player player, TPort tport, @Nullable UUID filterUUID, int page) {
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
            ItemStack is = getHead(log.teleportedUUID(), player, List.of(TPORT_LOG_READ), new ImmutableTriple<>(tport, log, sdf));
            items.add(is);
        }
        
        Message invTitle = filterUUID == null ?
                formatInfoTranslation("tport.quickEditInventories.openTPortLogReadGUI.all.title", tport) :
                formatInfoTranslation("tport.quickEditInventories.openTPortLogReadGUI.filtered.title", tport, asPlayer(filterUUID));
        
        FancyInventory inv = getDynamicScrollableInventory(player, page, QuickEditInventories::openTPortLogReadGUI,
                invTitle, items, createBack(player, TPORT_LOG, OWN, MAIN));
        inv.setData(tportDataName, tport);
        if (filterUUID != null) inv.setData("filterUUID", filterUUID);
        
        ItemStack chatMode = quick_edit_log_read_chat_model.getItem(player);
        Message chatModeTitle = formatInfoTranslation(playerLang, "tport.quickEditInventories.openTPortLogReadGUI.chatMode", LEFT);
        setCustomItemData(chatMode, colorTheme, chatModeTitle, null);
        addFunction(chatMode, LEFT, ((whoClicked, clickType, pdc, fancyInventory) ->
                Read.readLog_chat(
                        whoClicked,
                        fancyInventory.getData(tportDataName),
                        fancyInventory.getData("filterUUID", UUID.class, null)
                )));
        inv.setItem(18, chatMode);
        
        ItemStack filterSelection = quick_edit_log_read_filter_model.getItem(player);
        Message filterSelectionTitle = formatInfoTranslation(playerLang, "tport.quickEditInventories.openTPortLogReadGUI.filterSelection.title");
        Message filterSelect = formatInfoTranslation(playerLang, "tport.quickEditInventories.openTPortLogReadGUI.filterSelection.select", LEFT);
        Message filterReset = formatInfoTranslation(playerLang, "tport.quickEditInventories.openTPortLogReadGUI.filterSelection.reset", RIGHT);
        setCustomItemData(filterSelection, colorTheme, filterSelectionTitle, List.of(new Message(), filterSelect, filterReset));
        addFunction(filterSelection, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> openTPortLogRead_filterGUI(whoClicked, fancyInventory.getData(tportDataName), fancyInventory.getData("filterUUID", UUID.class), 0, null)));
        addFunction(filterSelection, RIGHT, ((whoClicked, clickType, pdc, fancyInventory) -> openTPortLogReadGUI(whoClicked, fancyInventory.getData(tportDataName))));
        inv.setItem(9, filterSelection);
        
        inv.setItem(0, getCornerTPortIcon(tport, player));
        
        inv.open(player);
    }
    private static void openTPortLogReadGUI(Player player, int page, FancyInventory prevWindow) {
        openTPortLogReadGUI(player, prevWindow.getData(tportDataName), prevWindow.getData("filterUUID", UUID.class), page);
    }
    private static void openTPortLogSelectorGUI(Player player, TPort tport, int page, @Nullable FancyInventory prevWindow) {
        List<ItemStack> headItems = ItemFactory.getPlayerList(player, true, true, List.of(TPORT_LOG_SELECTION), List.of(), tport);
        
        FancyInventory inv = getDynamicScrollableInventory(
                player,
                page,
                QuickEditInventories::openTPortLogSelectorGUI,
                formatInfoTranslation("tport.quickEditInventories.openTPortLogSelectionGUI.title", asTPort(tport)),
                headItems,
                createBack(player, TPORT_LOG, OWN, MAIN));
        inv.setData("content", headItems);
        
        inv.setItem(0, getCornerTPortIcon(tport, player));
        inv.setData(tportDataName, tport);
        inv.setData(tportUUIDDataName, tport.getTportID());
        
        inv.setItem(inv.getSize() / 18 * 9,
                ItemFactory.getSortingItem(player, getPlayerLang(player.getUniqueId()), ColorTheme.getTheme(player),
                        ((whoClicked, clickType, pdc, fancyInventory) -> openTPortLogSelectorGUI(whoClicked, fancyInventory.getData(tportDataName), 0, null))));
        
        inv.open(player);
    }
    static void openTPortLogSelectorGUI(Player player, int page, FancyInventory prevWindow) {
        openTPortLogSelectorGUI(player, prevWindow.getData(tportDataName), page, prevWindow);
    }
    //todo remember if opened from QuickEditor or Settings/Log
    public static boolean openTPortLogGUI(Player player, TPort tport) {
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        
        ItemStack editItem = quick_edit_log_edit_model.getItem(player);
        Message editTitle = formatInfoTranslation(playerLang, "tport.quickEditInventories.openTPortLogGUI.edit.title");
        setCustomItemData(editItem, colorTheme, editTitle, null);
        addFunction(editItem, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> openTPortLogSelectorGUI(whoClicked, fancyInventory.getData(tportDataName), 0, null)));
        
        ItemStack deleteItem = quick_edit_log_delete_model.getItem(player);
        Message deleteTitle = formatInfoTranslation(playerLang, "tport.quickEditInventories.openTPortLogGUI.delete.title", SHIFT_LEFT);
        setCustomItemData(deleteItem, colorTheme, deleteTitle, null);
        addCommand(deleteItem, SHIFT_LEFT, "tport log delete " + tport.getName());
        addFunction(deleteItem, SHIFT_LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> openTPortLogGUI(whoClicked, fancyInventory.getData(tportDataName))));
        
        ItemStack readItem = quick_edit_log_read_model.getItem(player);
        Message readTitle = formatInfoTranslation(playerLang, "tport.quickEditInventories.openTPortLogGUI.read.title");
        Message readInventory = formatInfoTranslation(playerLang, "tport.quickEditInventories.openTPortLogGUI.read.inventory", LEFT);
        Message readChat = formatInfoTranslation(playerLang, "tport.quickEditInventories.openTPortLogGUI.read.chat", SHIFT_LEFT);
        setCustomItemData(readItem, colorTheme, readTitle, List.of(readInventory, readChat));
        addFunction(readItem, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> openTPortLogReadGUI(whoClicked, fancyInventory.getData(tportDataName))));
        addFunction(readItem, SHIFT_LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> Read.readLog_chat(whoClicked, fancyInventory.getData(tportDataName))));
        
        ItemStack clearItem = quick_edit_log_clear_model.getItem(player);
        Message clearTitle = formatInfoTranslation(playerLang, "tport.quickEditInventories.openTPortLogGUI.clear.title", SHIFT_RIGHT);
        setCustomItemData(clearItem, colorTheme, clearTitle, null);
        addCommand(clearItem, SHIFT_RIGHT, "tport log clear " + tport.getName());
        
        ItemStack defaultItem = tport.getDefaultLogMode().getModel().getItem(player);
        Message defaultTitle = formatInfoTranslation(playerLang, "tport.quickEditInventories.openTPortLogGUI.default.title", tport.getDefaultLogMode());
        Message defaultLore = formatInfoTranslation(playerLang, "tport.quickEditInventories.openTPortLogGUI.default.lore", LEFT, tport.getDefaultLogMode().getNext());
        setCustomItemData(defaultItem, colorTheme, defaultTitle, List.of(defaultLore));
        addCommand(defaultItem, LEFT, "tport log default " + tport.getName() + " " + tport.getDefaultLogMode().getNext());
        addFunction(defaultItem, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> openTPortLogGUI(whoClicked, fancyInventory.getData(tportDataName))));
        
        FancyInventory inv = new FancyInventory(27, formatInfoTranslation("tport.quickEditInventories.openTPortLogGUI.title", tport));
        inv.setData(tportUUIDDataName, tport.getTportID());
        inv.setData(tportDataName, tport);
        
        inv.setItem(0, getCornerTPortIcon(tport, player));
        inv.setItem(10, defaultItem);
        inv.setItem(11, editItem);
        inv.setItem(13, deleteItem);
        inv.setItem(15, readItem);
        inv.setItem(16, clearItem);
        inv.setItem(17, createBack(player, QUICK_EDIT, OWN, MAIN));
        
        inv.open(player);
        return false;
    }
    
    private static void openTPortOfferGUI(Player player, TPort tport, boolean fromQuickEdit) {
        openTPortOfferGUI(player, tport, 0, fromQuickEdit, null);
    }
    private static void openTPortOfferGUI(Player player, TPort tport, int page, boolean fromQuickEdit, @Nullable FancyInventory prevWindow) {
        //tport transfer offer <TPort> <player>
        
        List<ItemStack> rawHeadItems;
        if (prevWindow == null) {
            rawHeadItems = getPlayerList(player, false, true, List.of(OFFER_TO_PLAYER), List.of(), new Pair<>(tport, fromQuickEdit));
        } else {
            rawHeadItems = prevWindow.getData("offerContent", List.class);
        }
        
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        JsonObject playerLang = Language.getPlayerLang(player.getUniqueId());
        
        Message title = formatInfoTranslation("tport.quickEditInventories.openTPortOfferGUI.title", tport);
        
        FancyInventory inv = getDynamicScrollableInventory(player, page, QuickEditInventories::openTPortOfferGUI, title, rawHeadItems, createBack(player, QUICK_EDIT, OWN, MAIN));
        inv.setData("offerContent", rawHeadItems);
        inv.setItem(0, getCornerTPortIcon(tport, player));
        inv.setData(tportDataName, tport);
        inv.setItem(inv.getSize() / 18 * 9,
                ItemFactory.getSortingItem(player, playerLang, colorTheme,
                        ((whoClicked, clickType, pdc, fancyInventory) -> openTPortOfferGUI(whoClicked, fancyInventory.getData(tportDataName), fancyInventory.getData(fromQuickEditDataName)))));
        inv.setData(tportUUIDDataName, tport.getTportID());
        inv.setData(fromQuickEditDataName, fromQuickEdit);
        
        inv.open(player);
    }
    private static void openTPortOfferGUI(Player player, int page, FancyInventory prevWindow) {
        openTPortOfferGUI(player, prevWindow.getData(tportDataName), page, prevWindow.getData(fromQuickEditDataName), prevWindow);
    }
    
    private static boolean openTPortItemGUI(Player player, TPort tport) {
        openTPortItemGUI(player, tport, 0, null);
        return false;
    }
    private static void openTPortItemGUI(Player player, TPort tport, int page, @Nullable FancyInventory prevWindow) {
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        JsonObject playerLang = Language.getPlayerLang(player);
        
        List<ItemStack> items;
        if (prevWindow == null) {
            items = new ArrayList<>();
            for (ItemStack dontToutchItemStack : player.getInventory().getContents()) {
                if (dontToutchItemStack == null) continue;
                ItemStack is = dontToutchItemStack.clone();
                setStringData(is, new NamespacedKey(Main.getInstance(), "itemData"), is.toString());
                
                Message itemTitle = formatInfoTranslation(playerLang, "tport.quickEditInventories.openTPortItemGUI.item.title");
                Message all = formatInfoTranslation(playerLang, "tport.quickEditInventories.openTPortItemGUI.item.select.all", LEFT);
                Message one = formatInfoTranslation(playerLang, "tport.quickEditInventories.openTPortItemGUI.item.select.one", RIGHT);
                Message tportTakesItem = Features.Feature.TPortTakesItem.isEnabled() ?
                        formatInfoTranslation(playerLang, "tport.quickEditInventories.openTPortItemGUI.item.tportTakesItem.enabled") :
                        formatInfoTranslation(playerLang, "tport.quickEditInventories.openTPortItemGUI.item.tportTakesItem.disabled");
                Message returnItem = tport.shouldReturnItem() ?
                        formatInfoTranslation(playerLang, "tport.quickEditInventories.openTPortItemGUI.item.returnItem.true", tport.getItem(), tport.getItem().getAmount()) :
                        formatInfoTranslation(playerLang, "tport.quickEditInventories.openTPortItemGUI.item.returnItem.false");
                
                setCustomItemData(is, colorTheme, itemTitle, List.of(new Message(), all, one, new Message(), tportTakesItem, returnItem));
                
                ItemMeta im = is.getItemMeta();
                if (im == null) continue;
                FancyClickEvent.removeAllFunctions(im);
                im.getEnchants().keySet().forEach(im::removeEnchant);
                Arrays.stream(ItemFlag.values()).forEach(im::addItemFlags);
                try {
                    im.removeItemFlags(ItemFlag.HIDE_LORE);
                } catch (NoSuchFieldError ignore) {}
                is.setItemMeta(im);
                
                addFunction(is, ((whoClicked, clickType, pdc, fancyInventory) -> {
                    String innerItemData = pdc.get(new NamespacedKey(Main.getInstance(), "itemData"), STRING);
                    for (ItemStack innerDontToutchItemStack : whoClicked.getInventory().getContents()) {
                        if (innerDontToutchItemStack == null) continue;
                        ItemStack innerItem = innerDontToutchItemStack.clone();
                        if (innerItem.toString().equals(innerItemData)) {
                            Item.setTPortDisplayItem(whoClicked, fancyInventory.getData(tportDataName), innerItem, clickType == RIGHT);
                            openQuickEditSelection(whoClicked, 0, fancyInventory.getData(tportDataName).getTportID());
                            return;
                        }
                    }
                    sendErrorTranslation(whoClicked, "tport.quickEditInventories.openTPortItemGUI.itemNotFound");
                    openTPortItemGUI(whoClicked, fancyInventory.getData(tportDataName));
                }), LEFT, RIGHT);
                
                items.add(is);
            }
        } else {
            items = prevWindow.getData("content", List.class);
        }
        
        Message title = formatInfoTranslation("tport.quickEditInventories.openTPortItemGUI.title");
        FancyInventory inv = getDynamicScrollableInventory(player, page, QuickEditInventories::openTPortItemGUI, title, items, createBack(player, QUICK_EDIT, OWN, MAIN));
        inv.setData("content", items);
        inv.setData(tportUUIDDataName, tport.getTportID());
        inv.setData(tportDataName, tport);
        
        ItemStack reload = quick_edit_item_reload_model.getItem(player);
        Message reloadTitle = formatInfoTranslation(playerLang, "tport.quickEditInventories.openTPortItemGUI.reload.title", LEFT);
        setCustomItemData(reload, colorTheme, reloadTitle, null);
        addFunction(reload, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
            openTPortItemGUI(whoClicked, fancyInventory.getData(tportDataName));
        }));
        inv.setItem(inv.getSize() - 9, reload);
        
        inv.open(player);
    }
    private static void openTPortItemGUI(Player player, int page, FancyInventory prevWindow) {
        openTPortItemGUI(player, prevWindow.getData(tportDataName), page, prevWindow);
    }
    
    private static int valueToEdit(ClickType clickType) {
        return switch (clickType) {
            case LEFT -> 1;
            case RIGHT -> 10;
            case SHIFT_LEFT -> 25;
            case SHIFT_RIGHT -> 100;
            default -> 0;
        };
    }
    private static int constrain(int value, int max, int min) {
        return Math.max(Math.min(value, max), min);
    }
    private static final FancyInventory.DataName<Color> colorDataType = new FancyInventory.DataName<>("color", Color.class, new Color(255, 255, 255));
    private static void openWaypointColorGUI(Player player, TPort tport, FancyInventory prevWindow) {
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        
        Color color = prevWindow.getData(colorDataType);
        MultiColor multiColor = new MultiColor(color);
        
        Message colorTitle = formatTranslation(multiColor, multiColor, "tport.quickEditInventories.openWaypointColorGUI.title.color");
        FancyInventory inv = new FancyInventory(3 * 9, formatInfoTranslation(playerLang, "tport.quickEditInventories.openWaypointColorGUI.title", colorTitle));
        
        inv.setItem(0, getCornerTPortIcon(tport, player));
        inv.setData(tportDataName, tport);
        inv.setData(tportUUIDDataName, tport.getTportID());
        inv.setData(colorDataType, color);
        
        ItemStack acceptColor = quick_edit_waypoint_color_accept_model.getItem(player);
        setCustomItemData(acceptColor, colorTheme, formatInfoTranslation(playerLang, "tport.quickEditInventories.openWaypointColorGUI.acceptColor"), null);
        addFunction(acceptColor, LEFT, ((whoClicked, clickType, pdc, fancyColorInventory) -> {
            TPort innerTPort = fancyColorInventory.getData(tportDataName);
            innerTPort.setWaypointColor(new MultiColor(fancyColorInventory.getData(colorDataType)));
            innerTPort.save();
            openWaypointGUI(whoClicked, innerTPort);
        }));
        inv.setItem(inv.getSize() - 1, acceptColor);
        
        ItemStack rejectColor = quick_edit_waypoint_color_reject_model.getItem(player);
        setCustomItemData(rejectColor, colorTheme, formatInfoTranslation(playerLang, "tport.quickEditInventories.openWaypointColorGUI.cancel"), null);
        addFunction(rejectColor, LEFT, ((whoClicked, clickType, pdc, fancyColorInventory) -> {
            openWaypointGUI(whoClicked, fancyColorInventory.getData(tportDataName));
        }));
        inv.setItem(inv.getSize() - 9, rejectColor);
        
        
        Message valueHEXMessage = formatInfoTranslation(playerLang, "tport.quickEditInventories.openWaypointColorGUI.value.hex", multiColor.getColorAsValue());
        Message valueRGBMessage = formatInfoTranslation(playerLang, "tport.quickEditInventories.openWaypointColorGUI.value.rgb", color.getRed(), color.getGreen(), color.getBlue());
        Message valueAdd1      = formatInfoTranslation(playerLang, "tport.quickEditInventories.openWaypointColorGUI.value.add",    LEFT, valueToEdit(LEFT));
        Message valueAdd10     = formatInfoTranslation(playerLang, "tport.quickEditInventories.openWaypointColorGUI.value.add",    RIGHT, valueToEdit(RIGHT));
        Message valueAdd25     = formatInfoTranslation(playerLang, "tport.quickEditInventories.openWaypointColorGUI.value.add",    SHIFT_LEFT, valueToEdit(SHIFT_LEFT));
        Message valueAdd100    = formatInfoTranslation(playerLang, "tport.quickEditInventories.openWaypointColorGUI.value.add",    SHIFT_RIGHT, valueToEdit(SHIFT_RIGHT));
        Message valueRemove1   = formatInfoTranslation(playerLang, "tport.quickEditInventories.openWaypointColorGUI.value.remove", LEFT, valueToEdit(LEFT));
        Message valueRemove10  = formatInfoTranslation(playerLang, "tport.quickEditInventories.openWaypointColorGUI.value.remove", RIGHT, valueToEdit(RIGHT));
        Message valueRemove25  = formatInfoTranslation(playerLang, "tport.quickEditInventories.openWaypointColorGUI.value.remove", SHIFT_LEFT, valueToEdit(SHIFT_LEFT));
        Message valueRemove100 = formatInfoTranslation(playerLang, "tport.quickEditInventories.openWaypointColorGUI.value.remove", SHIFT_RIGHT, valueToEdit(SHIFT_RIGHT));
        
        FancyClickRunnable onColorClick = (whoClicked, clickType, pdc, fancyColorInventory) -> {
            int valueToAdd = valueToEdit(clickType);
            if (pdc.has(new NamespacedKey(Main.getInstance(), "keyboard_color_remove"))) valueToAdd *= -1;
            Color c = fancyColorInventory.getData(colorDataType);
            
            Color newColor = null;
            if (pdc.has(new NamespacedKey(Main.getInstance(), "keyboard_color_red"))) {
                newColor = new Color(constrain(c.getRed() + valueToAdd, 255, 0), c.getGreen(), c.getBlue());
            } else if (pdc.has(new NamespacedKey(Main.getInstance(), "keyboard_color_green"))) {
                newColor = new Color(c.getRed(), constrain(c.getGreen() + valueToAdd, 255, 0), c.getBlue());
            } else if (pdc.has(new NamespacedKey(Main.getInstance(), "keyboard_color_blue"))) {
                newColor = new Color(c.getRed(), c.getGreen(), constrain(c.getBlue() + valueToAdd, 255, 0));
            }
            fancyColorInventory.setData(colorDataType, newColor);
            openWaypointColorGUI(whoClicked, fancyColorInventory.getData(tportDataName), fancyColorInventory);
        };
        
        ItemStack redAdd = quick_edit_waypoint_color_red_add_model.getItem(player);
        Message redAddTitle = formatTranslation(multiColor, multiColor, "tport.quickEditInventories.openWaypointColorGUI.red.add").translateMessage(playerLang);
        setCustomItemData(redAdd, colorTheme, redAddTitle, Arrays.asList(valueHEXMessage, valueRGBMessage, new Message(), valueAdd1, valueAdd10, valueAdd25, valueAdd100));
        setBooleanData(redAdd, new NamespacedKey(Main.getInstance(), "keyboard_color_red"), true);
        addFunction(redAdd, onColorClick, LEFT, RIGHT, SHIFT_LEFT, SHIFT_RIGHT);
        inv.setItem(10, redAdd);
        
        ItemStack redRem = quick_edit_waypoint_color_red_remove_model.getItem(player);
        Message redRemTitle = formatTranslation(multiColor, multiColor, "tport.quickEditInventories.openWaypointColorGUI.red.remove").translateMessage(playerLang);
        setCustomItemData(redRem, colorTheme, redRemTitle, Arrays.asList(valueHEXMessage, valueRGBMessage, new Message(), valueRemove1, valueRemove10, valueRemove25, valueRemove100));
        setBooleanData(redRem, new NamespacedKey(Main.getInstance(), "keyboard_color_red"), true);
        setBooleanData(redRem, new NamespacedKey(Main.getInstance(), "keyboard_color_remove"), true);
        addFunction(redRem, onColorClick, LEFT, RIGHT, SHIFT_LEFT, SHIFT_RIGHT);
        inv.setItem(11, redRem);
        
        ItemStack greenAdd = quick_edit_waypoint_color_green_add_model.getItem(player);
        Message greenAddTitle = formatTranslation(multiColor, multiColor, "tport.quickEditInventories.openWaypointColorGUI.green.add").translateMessage(playerLang);
        setCustomItemData(greenAdd, colorTheme, greenAddTitle, Arrays.asList(valueHEXMessage, valueRGBMessage, new Message(), valueAdd1, valueAdd10, valueAdd25, valueAdd100));
        setBooleanData(greenAdd, new NamespacedKey(Main.getInstance(), "keyboard_color_green"), true);
        addFunction(greenAdd, onColorClick, LEFT, RIGHT, SHIFT_LEFT, SHIFT_RIGHT);
        inv.setItem(12, greenAdd);
        
        ItemStack greenRem = quick_edit_waypoint_color_green_remove_model.getItem(player);
        Message greenRemTitle = formatTranslation(multiColor, multiColor, "tport.quickEditInventories.openWaypointColorGUI.green.remove").translateMessage(playerLang);
        setCustomItemData(greenRem, colorTheme, greenRemTitle, Arrays.asList(valueHEXMessage, valueRGBMessage, new Message(), valueRemove1, valueRemove10, valueRemove25, valueRemove100));
        setBooleanData(greenRem, new NamespacedKey(Main.getInstance(), "keyboard_color_green"), true);
        setBooleanData(greenRem, new NamespacedKey(Main.getInstance(), "keyboard_color_remove"), true);
        addFunction(greenRem, onColorClick, LEFT, RIGHT, SHIFT_LEFT, SHIFT_RIGHT);
        inv.setItem(13, greenRem);
        
        ItemStack blueAdd = quick_edit_waypoint_color_blue_add_model.getItem(player);
        Message blueAddTitle = formatTranslation(multiColor, multiColor, "tport.quickEditInventories.openWaypointColorGUI.blue.add").translateMessage(playerLang);
        setCustomItemData(blueAdd, colorTheme, blueAddTitle, Arrays.asList(valueHEXMessage, valueRGBMessage, new Message(), valueAdd1, valueAdd10, valueAdd25, valueAdd100));
        setBooleanData(blueAdd, new NamespacedKey(Main.getInstance(), "keyboard_color_blue"), true);
        addFunction(blueAdd, onColorClick, LEFT, RIGHT, SHIFT_LEFT, SHIFT_RIGHT);
        inv.setItem(14, blueAdd);
        
        ItemStack blueRem = quick_edit_waypoint_color_blue_remove_model.getItem(player);
        Message blueRemTitle = formatTranslation(multiColor, multiColor, "tport.quickEditInventories.openWaypointColorGUI.blue.remove").translateMessage(playerLang);
        setCustomItemData(blueRem, colorTheme, blueRemTitle, Arrays.asList(valueHEXMessage, valueRGBMessage, new Message(), valueRemove1, valueRemove10, valueRemove25, valueRemove100));
        setBooleanData(blueRem, new NamespacedKey(Main.getInstance(), "keyboard_color_blue"), true);
        setBooleanData(blueRem, new NamespacedKey(Main.getInstance(), "keyboard_color_remove"), true);
        addFunction(blueRem, onColorClick, LEFT, RIGHT, SHIFT_LEFT, SHIFT_RIGHT);
        inv.setItem(15, blueRem);
        
        ItemStack builtInColorSelector = quick_edit_waypoint_color_chat_color_model.getItem(player);
        Message builtInColorSelectorTitle = formatInfoTranslation(playerLang, "tport.quickEditInventories.openWaypointColorGUI.openBuiltInColorSelector");
        setCustomItemData(builtInColorSelector, colorTheme, builtInColorSelectorTitle,  null);
        addFunction(builtInColorSelector, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> openWaypointColor_builtInColorSelector(whoClicked, 0, fancyInventory)));
        inv.setItem(16, builtInColorSelector);
        
        inv.open(player);
    }
    private static void openWaypointColor_builtInColorSelector(Player player, int page, FancyInventory prevWindow) {
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        
        ArrayList<ItemStack> items = new ArrayList<>();
        for (ChatColor chatColor : ChatColor.values()) {
            if (!chatColor.isColor()) continue;
            ItemStack is = KeyboardGUI.getChatColorStack(chatColor, player);
            MultiColor chatMultiColor = new MultiColor(chatColor);
            Message title = formatTranslation(colorTheme.getInfoColor(), chatMultiColor,
                    "tport.quickEditInventories.openWaypointColor_builtInColorSelector.chatColor", chatColor.name(), "&", chatColor.getChar()).translateMessage(playerLang);
            
            Message valueHEXMessage = formatInfoTranslation(playerLang, "tport.quickEditInventories.openWaypointColor_builtInColorSelector.chatColor.hex", chatMultiColor.getColorAsValue());
            Message valueRGBMessage = formatInfoTranslation(playerLang, "tport.quickEditInventories.openWaypointColor_builtInColorSelector.chatColor.rgb", chatMultiColor.getRed(), chatMultiColor.getGreen(), chatMultiColor.getBlue());
            Message clickToSelect = formatInfoTranslation(playerLang, "tport.quickEditInventories.openWaypointColor_builtInColorSelector.clickToSelect", LEFT);
            
            setCustomItemData(is, colorTheme, title, List.of(new Message(), valueHEXMessage, valueRGBMessage, new Message(), clickToSelect));
            setStringData(is, new NamespacedKey(Main.getInstance(), "keyboard_chat_color"), chatColor.name());
            addFunction(is, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
                ChatColor c = ChatColor.valueOf(pdc.get(new NamespacedKey(Main.getInstance(), "keyboard_chat_color"), STRING));
                fancyInventory.setData(colorDataType, new MultiColor(c).getColor());
                openWaypointColorGUI(whoClicked, fancyInventory.getData(tportDataName), fancyInventory);
            }));
            
            items.add(is);
        }
        for (DyeColor dyeColor : DyeColor.values()) {
            ItemStack is = KeyboardGUI.getDyeStack(dyeColor);
            MultiColor chatMultiColor = new MultiColor(dyeColor);
            Message title = formatTranslation(colorTheme.getInfoColor(), chatMultiColor,
                    "tport.quickEditInventories.openWaypointColor_builtInColorSelector.dyeColor", dyeColor.name()).translateMessage(playerLang);
            
            Message valueHEXMessage = formatInfoTranslation(playerLang, "tport.quickEditInventories.openWaypointColor_builtInColorSelector.dyeColor.hex", chatMultiColor.getColorAsValue());
            Message valueRGBMessage = formatInfoTranslation(playerLang, "tport.quickEditInventories.openWaypointColor_builtInColorSelector.dyeColor.rgb", chatMultiColor.getRed(), chatMultiColor.getGreen(), chatMultiColor.getBlue());
            Message clickToSelect = formatInfoTranslation(playerLang, "tport.quickEditInventories.openWaypointColor_builtInColorSelector.clickToSelect", LEFT);
            
            setCustomItemData(is, colorTheme, title, List.of(new Message(), valueHEXMessage, valueRGBMessage, new Message(), clickToSelect));
            setStringData(is, new NamespacedKey(Main.getInstance(), "keyboard_dye_color"), dyeColor.name());
            addFunction(is, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
                DyeColor c = DyeColor.valueOf(pdc.get(new NamespacedKey(Main.getInstance(), "keyboard_dye_color"), STRING));
                fancyInventory.setData(colorDataType, new MultiColor(c).getColor());
                openWaypointColorGUI(whoClicked, fancyInventory.getData(tportDataName), fancyInventory);
            }));
            
            items.add(is);
        }
        
        ItemStack backButton = quick_edit_waypoint_color_chat_color_reject_model.getItem(player);
        Message backTitle = formatInfoTranslation(playerLang, "tport.quickEditInventories.openWaypointColor_builtInColorSelector.cancel");
        setCustomItemData(backButton, colorTheme, backTitle, null);
        addFunction(backButton, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> openWaypointColorGUI(whoClicked, fancyInventory.getData(tportDataName), fancyInventory)));
        
        Message title = formatInfoTranslation(playerLang, "tport.quickEditInventories.openWaypointColor_builtInColorSelector.title");
        FancyInventory builtInColorSelectorKeyboard = getDynamicScrollableInventory(player, page, QuickEditInventories::openWaypointColor_builtInColorSelector, title, items, backButton);
        builtInColorSelectorKeyboard.transferData(prevWindow, false);
        
        builtInColorSelectorKeyboard.open(player);
    
    }
    private static boolean openWaypointGUI(Player player, TPort tport) {
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        
        Message title = formatInfoTranslation("tport.quickEditInventories.openWaypointGUI.title", asTPort(tport));
        FancyInventory inv = new FancyInventory(27, title);
        inv.setData(tportUUIDDataName, tport.getTportID());
        inv.setData(tportDataName, tport);
        
        inv.setItem(0, getCornerTPortIcon(tport, player));
        
        // current state: STATE
        // click left to set state to NEXT STATE
        ItemStack showButton = tport.getShowWaypoint().getInventoryModel().getItem(player);
        addCommand(showButton, LEFT, "tport edit " + tport.getName() + " waypoint show " + tport.getShowWaypoint().getNext().name());
        addFunction(showButton, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> openWaypointGUI(whoClicked, fancyInventory.getData(tportDataName))));
        Message showState = formatInfoTranslation(playerLang, "tport.quickEditInventories.openWaypointGUI.show.state", asTPort(tport), tport.getShowWaypoint());
        Message showNextState = formatInfoTranslation(playerLang, "tport.quickEditInventories.openWaypointGUI.show.nextState", LEFT, asTPort(tport), tport.getShowWaypoint().getNext());
        setCustomItemData(showButton, colorTheme, showState, List.of(showNextState));
        inv.setItem(11, showButton);
        
        
        
        // button icon
        ItemStack iconButton;
        Message iconTypeTitle;
        if (tport.getWaypointIcon() == null) {
            iconButton = quick_edit_waypoint_icon_model.getItem(player);
            addFunction(iconButton, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
                TPort innerTPort = fancyInventory.getData(tportDataName);
                innerTPort.setWaypointIcon(WaypointModels.tport_waypoint_model);
                innerTPort.save();
                openWaypointGUI(whoClicked, innerTPort);
            }));
            iconTypeTitle = formatTranslation(ColorType.varInfoColor, ColorType.varInfoColor, "tport.quickEditInventories.openWaypointGUI.icon.default");
        } else {
            iconButton = tport.getWaypointIcon().getItem(player);
            addFunction(iconButton, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
                TPort innerTPort = fancyInventory.getData(tportDataName);
                innerTPort.setWaypointIcon((WaypointModel) null);
                innerTPort.save();
                openWaypointGUI(whoClicked, innerTPort);
            }));
            iconTypeTitle = formatTranslation(ColorType.varInfoColor, ColorType.varInfoColor, tport.getWaypointIcon().getName());
        }
        Message iconTitle = formatInfoTranslation(playerLang, "tport.quickEditInventories.openWaypointGUI.icon.title", iconTypeTitle);
        setCustomItemData(iconButton, colorTheme, iconTitle, null);
        inv.setItem(13, iconButton);
        
        
        // button color
        MultiColor randomColor = MultiColor.fromRandom();
        inv.setData("randomColor", randomColor);
        ItemStack colorButton = quick_edit_waypoint_color_model.getItem(player);
        addFunction(colorButton, LEFT, (whoClicked, clickType, pdc, fancyInventory) -> {
            TPort innerTPort = fancyInventory.getData(tportDataName);
            fancyInventory.setData(colorDataType, innerTPort.getWaypointColor().getColor());
            openWaypointColorGUI(whoClicked, innerTPort, fancyInventory);
        });
        addFunction(colorButton, RIGHT, (whoClicked, clickType, pdc, fancyInventory) -> {
            TPort innerTPort = fancyInventory.getData(tportDataName);
            innerTPort.setWaypointColor(new MultiColor(ChatColor.WHITE));
            innerTPort.save();
            openWaypointGUI(whoClicked, innerTPort);
        });
        addFunction(colorButton, SHIFT_RIGHT, (whoClicked, clickType, pdc, fancyInventory) -> {
            TPort innerTPort = fancyInventory.getData(tportDataName);
            innerTPort.setWaypointColor(fancyInventory.getData("randomColor", MultiColor.class));
            innerTPort.save();
            openWaypointGUI(whoClicked, innerTPort);
        });
        Message colorEdit = formatInfoTranslation(playerLang, "tport.quickEditInventories.openWaypointGUI.color.title", LEFT, asTPort(tport));
        Message colorThis = formatTranslation(tport.getWaypointColor(), tport.getWaypointColor(), "tport.quickEditInventories.openWaypointGUI.color.this");
        Message colorColor = formatInfoTranslation(playerLang, "tport.quickEditInventories.openWaypointGUI.color.color", colorThis);
        Message colorReset = formatInfoTranslation(playerLang, "tport.quickEditInventories.openWaypointGUI.color.reset", RIGHT, asTPort(tport));
        Message colorRandomize = formatTranslation(randomColor, randomColor, "tport.quickEditInventories.openWaypointGUI.color.randomize");
        Message colorRandom = formatInfoTranslation(playerLang, "tport.quickEditInventories.openWaypointGUI.color.random", SHIFT_RIGHT, colorRandomize, asTPort(tport));
        setCustomItemData(colorButton, colorTheme, colorColor, Arrays.asList(new Message(), colorEdit, colorReset, colorRandom));
        inv.setItem(15, colorButton);
        
        inv.setItem(26, createBack(player, QUICK_EDIT, OWN, MAIN));
        
        inv.open(player);
        
        return false;
    }
    
    public static final FancyInventory.DataName<UUID> tportToMoveDataName = new FancyInventory.DataName<>("TPortToMove", UUID.class, null);
    private static boolean onMove(TPort moveToTPort, Player player, FancyInventory fancyInventory) {
        UUID toMoveTPort = fancyInventory.getData(tportToMoveDataName);
        if (toMoveTPort == null) {
            QuickEditType.setForPlayer(player.getUniqueId(), QuickEditType.MOVE);
            fancyInventory.setData(tportToMoveDataName, moveToTPort.getTportID());
        } else {
            fancyInventory.setData(tportToMoveDataName, null);
            if (!toMoveTPort.equals(moveToTPort.getTportID())) {
                TPort tmpTPort = TPortManager.getTPort(toMoveTPort);
                if (tmpTPort != null) {
                    TPortCommand.executeTPortCommand(player, new String[]{"edit", tmpTPort.getName() , "move", moveToTPort.getName()});
                }
            }
        }
        TPortInventories.openTPortGUI(player.getUniqueId(), player, fancyInventory);
        return false;
    }
    
    public enum QuickEditType {
        ITEM(quick_edit_item_model, (tport, player, fancyInventory) -> openTPortItemGUI(player, tport)),
        PRIVATE(tport -> tport.getPrivateState().getInventoryModel(), (tport, player, fancyInventory) -> openTPortPrivateGUI(player, tport, fancyInventory)),
        WHITELIST(quick_edit_whitelist_model, (tport, player, fancyInventory) -> openTPortWhitelistSelectorGUI(player, tport, 0, null)),
        WHITELIST_VISIBILITY(tport -> tport.getWhitelistVisibility().getModel(),
                (tport, player, fancyInventory) -> {
                    TPortCommand.executeTPortCommand(player, new String[]{"edit", tport.getName(), "whitelist", "visibility", tport.getWhitelistVisibility().getNext().name()});
                    return true;
                }),
        WHITELIST_CLONE(quick_edit_whitelist_clone_model, QuickEditInventories::onWhitelistClone),
        RANGE(quick_edit_range_model, (tport, player, fancyInventory) -> openTPortRangeGUI(player, tport)),
        MOVE(quick_edit_move_model, QuickEditInventories::onMove),
        LOG(quick_edit_log_model, (tport, player, fancyInventory) -> openTPortLogGUI(player, tport)),
        NOTIFY(tport -> tport.getNotifyMode().getModel(), (tport, player, fancyInventory) -> {
            TPortCommand.executeTPortCommand(player, new String[]{"log", "notify", tport.getName(), tport.getNotifyMode().getNext().name()});
            return true;
        }),
        PREVIEW(tport -> tport.getPreviewState().getModel(), (tport, player, fancyInventory) -> {
            if (Features.Feature.Preview.isEnabled()) {
                TPortCommand.executeTPortCommand(player, new String[]{"edit", tport.getName(), "preview", tport.getPreviewState().getNext().name()});
                return true;
            } else {
                Features.Feature.Preview.sendDisabledMessage(player);
                return false;
            }
        }),
        TAG(quick_edit_tag_model, (tport, player, fancyInventory) -> openTPortTagSelectorGUI(player, tport, 0)),
        REMOVE(quick_edit_remove_model, (tport, player, fancyInventory) -> openTPortRemoveGUI(player, tport, fancyInventory)),
        LOCATION(quick_edit_location_model, (tport, player, fancyInventory) -> openTPortLocationGUI(player, tport, fancyInventory)),
        NAME(quick_edit_name_model, (tport, player, fancyInventory) -> {
            FancyClickEvent.FancyClickRunnable onAccept = ((whoClicked, clickType, pdc, keyboardInventory) -> {
                String newTPortName = getKeyboardOutput(keyboardInventory);
                String tportName = keyboardInventory.getData(tportNameDataName);
                TPortCommand.executeTPortCommand(whoClicked, new String[] {"edit", tportName, "name", newTPortName});
                if (keyboardInventory.getData(fromQuickEditDataName)) {
                    openQuickEditSelection(whoClicked, 0, keyboardInventory.getData(tportUUIDDataName));
                } else {
                    openTPortGUI(whoClicked.getUniqueId(), whoClicked);
                }
            });
            FancyClickEvent.FancyClickRunnable onReject = ((whoClicked, clickType, pdc, keyboardInventory) -> {
                if (keyboardInventory.getData(fromQuickEditDataName)) {
                    openQuickEditSelection(whoClicked, 0, keyboardInventory.getData(tportUUIDDataName));
                } else {
                    openTPortGUI(whoClicked.getUniqueId(), whoClicked);
                }
            });
            FancyInventory inv = KeyboardGUI.openKeyboard(player, onAccept, onReject, KeyboardGUI.NUMBERS | KeyboardGUI.CHARS | KeyboardGUI.LINES);
            inv.setData(tportNameDataName, tport.getName());
            inv.setData(tportUUIDDataName, tport.getTportID());
            inv.setData(fromQuickEditDataName, fancyInventory.getData(fromQuickEditDataName));
            return false;
        }),
        DESCRIPTION(quick_edit_description_model, (tport, player, fancyInventory) -> {
            FancyClickEvent.FancyClickRunnable onAccept = ((whoClicked, clickType, pdc, keyboardInventory) -> {
                String description = getKeyboardOutput(keyboardInventory);
                description = description.replace("\n", "\\n");
                
                String tportName = keyboardInventory.getData(tportNameDataName);
                TPortCommand.executeTPortCommand(whoClicked, new String[] {"edit", tportName, "description", "set", description});
                if (keyboardInventory.getData(fromQuickEditDataName)) {
                    openQuickEditSelection(whoClicked, 0, keyboardInventory.getData(tportUUIDDataName));
                } else {
                    openTPortGUI(whoClicked.getUniqueId(), whoClicked);
                }
            });
            FancyClickEvent.FancyClickRunnable onReject = ((whoClicked, clickType, pdc, keyboardInventory) -> {
                if (keyboardInventory.getData(fromQuickEditDataName)) {
                    openQuickEditSelection(whoClicked, 0, keyboardInventory.getData(tportUUIDDataName));
                } else {
                    openTPortGUI(whoClicked.getUniqueId(), whoClicked);
                }
            });
            FancyInventory inv = KeyboardGUI.openKeyboard(player, onAccept, onReject, tport.getRawDescription(), "#5555ff", ALL);
            inv.setData(tportNameDataName, tport.getName());
            inv.setData(tportUUIDDataName, tport.getTportID());
            inv.setData(fromQuickEditDataName, fancyInventory.getData(fromQuickEditDataName));
            return false;
        }),
        PUBLIC_TP(QuickEditType::getPublicTPortModel, ((tport, player, fancyInventory) -> {
            if (Features.Feature.PublicTP.isEnabled()) {
                if (tport.isPublicTPort()) {
                    TPortCommand.executeTPortCommand(player, new String[]{"public", "remove", tport.getName()});
                } else {
                    TPortCommand.executeTPortCommand(player, new String[]{"public", "add", tport.getName()});
                }
                return true;
            } else {
                Features.Feature.PublicTP.sendDisabledMessage(player);
                return false;
            }
        })),
        WAYPOINT(QuickEditType::getWaypointModel, (tport, player, fancyInventory) -> {
            if (Features.Feature.Waypoints.isEnabled()) {
                return openWaypointGUI(player, tport);
            }
            return false;
        }),
        DYNMAP_SHOW(QuickEditType::getDynmapShowModel, (tport, player, fancyInventory) -> {
            if (DynmapCommand.checkDynmapState(player)) {
                TPortCommand.executeTPortCommand(player, new String[]{"edit", tport.getName(), "dynmap", "show", String.valueOf(!tport.showOnDynmap())});
                return true;
            } else {
                return false;
            }
        }),
        DYNMAP_ICON(DynmapHandler.isEnabled() ? quick_edit_dynmap_icon_model : quick_edit_dynmap_icon_grayed_model, (tport, player, fancyInventory) -> {
            if (DynmapCommand.checkDynmapState(player)) {
                openTPortDynmapIconGUI(player, tport, 0);
            }
            return false;
        }),
        BLUEMAP_SHOW(QuickEditType::getBlueMapShowModel, (tport, player, fancyInventory) -> {
            if (BlueMapCommand.checkBlueMapState(player)) {
                TPortCommand.executeTPortCommand(player, new String[]{"edit", tport.getName(), "blueMap", "show", String.valueOf(!tport.showOnBlueMap())});
                return true;
            } else {
                return false;
            }
        }),
        BLUEMAP_ICON(QuickEditType::getBlueMapIconModel, (tport, player, fancyInventory) -> {
            if (BlueMapCommand.checkBlueMapState(player)) {
                openTPortBlueMapIconGUI(player, tport, 0);
            }
            return false;
        }),
        OFFER(tport -> tport.isOffered() ? quick_edit_revoke_model : quick_edit_offer_model, ((tport, player, fancyInventory) -> {
            if (tport.isOffered()) {
                TPortCommand.executeTPortCommand(player, new String[]{"transfer", "revoke", tport.getName()});
                return true;
            } else {
                openTPortOfferGUI(player, tport, fancyInventory.getData(fromQuickEditDataName));
                return false;
            }
        }));
        
        private final QuickEditor editor;
        private final ModelSelector modelSelector;
        
        public static HashMap<UUID, QuickEditType> map = new HashMap<>();
        
        QuickEditType(ModelSelector modelSelector, QuickEditor run) {
            this.editor = run;
            this.modelSelector = modelSelector;
        }
        QuickEditType(InventoryModel model, QuickEditor run) {
            this.editor = run;
            this.modelSelector = (TPort) -> model;
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
            return this.editor.edit(tport, player, fancyInventory);
        }
        
        public InventoryModel getModel(TPort tport) {
            return modelSelector.selectModel(tport);
        }
        
        public Message getDisplayName() {
            return formatTranslation(ColorType.varInfoColor, ColorType.varInfoColor, "tport.quickEditInventories.quickEditType." + this.name() + ".displayName");
        }
        
        public Message getDescription() {
            return formatInfoTranslation("tport.quickEditInventories.quickEditType." + this.name() + ".description");
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
        private static InventoryModel getBlueMapShowModel(TPort tport) {
            boolean blueMapState = false;
            try { blueMapState = BlueMapHandler.isEnabled(); } catch (Throwable ignored) { }
            
            if (!blueMapState) {
                return quick_edit_bluemap_show_grayed_model;
            }
            return tport.showOnBlueMap() ? quick_edit_bluemap_show_on_model : quick_edit_bluemap_show_off_model;
        }
        private static InventoryModel getBlueMapIconModel(TPort ignored) {
            boolean blueMapState = false;
            try { blueMapState = BlueMapHandler.isEnabled(); } catch (Throwable ignoredError) { }
            
            return blueMapState ? quick_edit_bluemap_icon_model : quick_edit_bluemap_icon_grayed_model;
        }
        public static InventoryModel getPublicTPortModel(TPort tport) {
            if (Features.Feature.PublicTP.isDisabled()) {
                return quick_edit_public_tp_grayed_model;
            }
            return tport.isPublicTPort() ? quick_edit_public_tp_on_model : quick_edit_public_tp_off_model;
        }
        public static InventoryModel getWaypointModel(TPort tport) {
            if (Features.Feature.Waypoints.isDisabled()) {
                return quick_edit_waypoint_grayed_model;
            }
            return quick_edit_waypoint_model;
        }
        
        @FunctionalInterface
        private interface QuickEditor {
            boolean edit(TPort tport, Player player, FancyInventory fancyInventory);
        }
        @FunctionalInterface
        private interface ModelSelector {
            InventoryModel selectModel(TPort tport);
        }
    }
}
