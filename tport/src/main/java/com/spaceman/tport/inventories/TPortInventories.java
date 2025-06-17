package com.spaceman.tport.inventories;

import com.google.gson.JsonObject;
import com.spaceman.tport.Glow;
import com.spaceman.tport.Main;
import com.spaceman.tport.advancements.TPortAdvancement;
import com.spaceman.tport.biomeTP.BiomePreset;
import com.spaceman.tport.commands.TPortCommand;
import com.spaceman.tport.commands.tport.*;
import com.spaceman.tport.commands.tport.biomeTP.Preset;
import com.spaceman.tport.commands.tport.biomeTP.Random;
import com.spaceman.tport.commands.tport.featureTP.Mode;
import com.spaceman.tport.commands.tport.mainLayout.Players;
import com.spaceman.tport.commands.tport.mainLayout.TPorts;
import com.spaceman.tport.commands.tport.pltp.Preview;
import com.spaceman.tport.commands.tport.pltp.*;
import com.spaceman.tport.cooldown.CooldownManager;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.MessageUtils;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fancyMessage.encapsulation.BiomeEncapsulation;
import com.spaceman.tport.fancyMessage.inventories.FancyInventory;
import com.spaceman.tport.fancyMessage.inventories.InventoryModel;
import com.spaceman.tport.fancyMessage.inventories.keyboard.KeyboardGUI;
import com.spaceman.tport.history.HistoryElement;
import com.spaceman.tport.history.HistoryFilter;
import com.spaceman.tport.history.TeleportHistory;
import com.spaceman.tport.history.locationSource.CraftLocationSource;
import com.spaceman.tport.history.locationSource.LocationSource;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.search.SearchMode;
import com.spaceman.tport.search.SearchType;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static com.spaceman.tport.advancements.TPortAdvancement.Advancement_LostAndFound;
import static com.spaceman.tport.commands.tport.Back.getPrevLocName;
import static com.spaceman.tport.commands.tport.SafetyCheck.SafetyCheckSource.TPORT_BACK;
import static com.spaceman.tport.commands.tport.SafetyCheck.SafetyCheckSource.TPORT_HOME;
import static com.spaceman.tport.fancyMessage.MessageUtils.setCustomItemData;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.inventories.FancyClickEvent.*;
import static com.spaceman.tport.fancyMessage.inventories.FancyInventory.getDynamicScrollableInventory;
import static com.spaceman.tport.fancyMessage.inventories.FancyInventory.pageDataName;
import static com.spaceman.tport.fancyMessage.language.Language.getPlayerLang;
import static com.spaceman.tport.fileHander.Files.tportData;
import static com.spaceman.tport.history.TeleportHistory.teleportHistory;
import static com.spaceman.tport.inventories.ItemFactory.BackType.*;
import static com.spaceman.tport.inventories.ItemFactory.HeadAttributes.*;
import static com.spaceman.tport.inventories.ItemFactory.TPortItemAttributes.*;
import static com.spaceman.tport.inventories.ItemFactory.createBack;
import static com.spaceman.tport.inventories.ItemFactory.toTPortItem;
import static com.spaceman.tport.inventories.QuickEditInventories.*;
import static com.spaceman.tport.inventories.SettingsInventories.openPLTPWhitelistSelectorGUI;
import static com.spaceman.tport.inventories.SettingsInventories.settings_model;
import static com.spaceman.tport.tport.TPortManager.getTPort;
import static org.bukkit.event.inventory.ClickType.*;

public class TPortInventories {
    
    public static final InventoryModel back_model                              = new InventoryModel(Material.BARRIER, KeyboardGUI.last_model_id + 1, "tport", "back", "navigation");
    public static final InventoryModel extra_tp_model                          = new InventoryModel(Material.OAK_BUTTON, back_model, "tport", "extra_tp", "navigation");
    public static final InventoryModel extra_tp_grayed_model                   = new InventoryModel(Material.OAK_BUTTON, extra_tp_model, "tport", "extra_tp_grayed", "navigation");
    public static final InventoryModel main_layout_model                       = new InventoryModel(Material.OAK_BUTTON, extra_tp_grayed_model, "tport", "main_layout", "main_layout");
    public static final InventoryModel main_layout_grayed_model                = new InventoryModel(Material.OAK_BUTTON, main_layout_model, "tport", "main_layout_grayed", "main_layout");
    public static final InventoryModel biome_tp_model                          = new InventoryModel(Material.OAK_BUTTON, main_layout_grayed_model, "tport", "biome_tp", "biome_tp");
    public static final InventoryModel biome_tp_grayed_model                   = new InventoryModel(Material.OAK_BUTTON, biome_tp_model, "tport", "biome_tp_grayed", "biome_tp");
    public static final InventoryModel biome_tp_clear_model                    = new InventoryModel(Material.OAK_BUTTON, biome_tp_grayed_model, "tport", "biome_tp_clear", "biome_tp");
    public static final InventoryModel biome_tp_clear_grayed_model             = new InventoryModel(Material.OAK_BUTTON, biome_tp_clear_model, "tport", "biome_tp_clear_grayed", "biome_tp");
    public static final InventoryModel biome_tp_run_model                      = new InventoryModel(Material.OAK_BUTTON, biome_tp_clear_grayed_model, "tport", "biome_tp_run", "biome_tp");
    public static final InventoryModel biome_tp_run_grayed_model               = new InventoryModel(Material.OAK_BUTTON, biome_tp_run_model, "tport", "biome_tp_run_grayed", "biome_tp");
    public static final InventoryModel biome_tp_presets_model                  = new InventoryModel(Material.OAK_BUTTON, biome_tp_run_grayed_model, "tport", "biome_tp_presets", "biome_tp");
    public static final InventoryModel biome_tp_presets_grayed_model           = new InventoryModel(Material.OAK_BUTTON, biome_tp_presets_model, "tport", "biome_tp_presets_grayed", "biome_tp");
    public static final InventoryModel biome_tp_random_tp_model                = new InventoryModel(Material.ELYTRA,     biome_tp_presets_grayed_model, "tport", "biome_tp_random_tp", "biome_tp");
    public static final InventoryModel biome_tp_random_tp_grayed_model         = new InventoryModel(Material.ELYTRA,     biome_tp_random_tp_model, "tport", "biome_tp_random_tp_grayed", "biome_tp");
    public static final InventoryModel feature_tp_model                        = new InventoryModel(Material.OAK_BUTTON, biome_tp_random_tp_grayed_model, "tport", "feature_tp", "feature_tp");
    public static final InventoryModel feature_tp_grayed_model                 = new InventoryModel(Material.OAK_BUTTON, feature_tp_model, "tport", "feature_tp_grayed", "feature_tp");
    public static final InventoryModel feature_tp_clear_model                  = new InventoryModel(Material.OAK_BUTTON, feature_tp_grayed_model, "tport", "feature_tp_clear", "feature_tp");
    public static final InventoryModel feature_tp_clear_grayed_model           = new InventoryModel(Material.OAK_BUTTON, feature_tp_clear_model, "tport", "feature_tp_clear_grayed", "feature_tp");
    public static final InventoryModel feature_tp_run_model                    = new InventoryModel(Material.OAK_BUTTON, feature_tp_clear_grayed_model, "tport", "feature_tp_run", "feature_tp");
    public static final InventoryModel feature_tp_run_grayed_model             = new InventoryModel(Material.OAK_BUTTON, feature_tp_run_model, "tport", "feature_tp_run_grayed", "feature_tp");
    public static final InventoryModel back_tp_model                           = new InventoryModel(Material.OAK_BUTTON, feature_tp_run_grayed_model, "tport", "back_tp", "back_tp");
    public static final InventoryModel back_tp_grayed_model                    = new InventoryModel(Material.OAK_BUTTON, back_tp_model, "tport", "back_tp_grayed", "back_tp");
    public static final InventoryModel public_tp_model                         = new InventoryModel(Material.OAK_BUTTON, back_tp_grayed_model, "tport", "public_tp", "public_tp");
    public static final InventoryModel public_tp_grayed_model                  = new InventoryModel(Material.OAK_BUTTON, public_tp_model, "tport", "public_tp_grayed", "public_tp");
    public static final InventoryModel public_tp_filter_own_model              = new InventoryModel(Material.OAK_BUTTON, public_tp_grayed_model, "tport", "public_tp_filter_own", "public_tp");
    public static final InventoryModel public_tp_filter_all_model              = new InventoryModel(Material.OAK_BUTTON, public_tp_filter_own_model, "tport", "public_tp_filter_all", "public_tp");
    public static final InventoryModel sorting_model                           = new InventoryModel(Material.OAK_BUTTON, public_tp_filter_all_model, "tport", "sorting", "sorting");
    public static final InventoryModel sorting_grayed_model                    = new InventoryModel(Material.OAK_BUTTON, sorting_model, "tport", "sorting_grayed", "sorting");
    public static final InventoryModel search_data_model                       = new InventoryModel(Material.OAK_BUTTON, sorting_grayed_model, "tport", "search_data", "search");
    public static final InventoryModel home_model                              = new InventoryModel(Material.OAK_BUTTON, search_data_model, "tport", "home", "home");
    public static final InventoryModel home_grayed_model                       = new InventoryModel(Material.OAK_BUTTON, home_model, "tport", "home_grayed", "home");
    
    public static final InventoryModel history_model                           = new InventoryModel(Material.OAK_BUTTON, home_grayed_model, "tport", "history", "history");
    public static final InventoryModel history_grayed_model                    = new InventoryModel(Material.OAK_BUTTON, history_model, "tport", "history_grayed", "history");
    public static final InventoryModel history_element_model                   = new InventoryModel(Material.OAK_BUTTON, history_grayed_model, "tport", "history_element", "history/models");
    public static final InventoryModel history_element_ender_pearl_model       = new InventoryModel(Material.OAK_BUTTON, history_element_model, "tport", "history_element_ender_pearl", "history/models");
    public static final InventoryModel history_element_command_model           = new InventoryModel(Material.OAK_BUTTON, history_element_ender_pearl_model, "tport", "history_element_command", "history/models");
    public static final InventoryModel history_element_plugin_model            = new InventoryModel(Material.OAK_BUTTON, history_element_command_model, "tport", "history_element_plugin", "history/models");
    public static final InventoryModel history_element_nether_portal_model     = new InventoryModel(Material.OAK_BUTTON, history_element_plugin_model, "tport", "history_element_nether_portal", "history/models");
    public static final InventoryModel history_element_end_portal_model        = new InventoryModel(Material.OAK_BUTTON, history_element_nether_portal_model, "tport", "history_element_end_portal", "history/models");
    public static final InventoryModel history_element_spectate_model          = new InventoryModel(Material.OAK_BUTTON, history_element_end_portal_model, "tport", "history_element_spectate", "history/models");
    public static final InventoryModel history_element_end_gateway_model       = new InventoryModel(Material.OAK_BUTTON, history_element_spectate_model, "tport", "history_element_end_gateway", "history/models");
    public static final InventoryModel history_element_chorus_fruit_model      = new InventoryModel(Material.OAK_BUTTON, history_element_end_gateway_model, "tport", "history_element_chorus_fruit", "history/models");
    public static final InventoryModel history_element_dismount_model          = new InventoryModel(Material.OAK_BUTTON, history_element_chorus_fruit_model, "tport", "history_element_dismount", "history/models");
    public static final InventoryModel history_element_exit_bed_model          = new InventoryModel(Material.OAK_BUTTON, history_element_dismount_model, "tport", "history_element_exit_bed", "history/models");
    public static final InventoryModel history_element_boat_model              = new InventoryModel(Material.OAK_BUTTON, history_element_exit_bed_model, "tport", "history_element_boat", "history/models");
    public static final InventoryModel history_element_minecart_model          = new InventoryModel(Material.OAK_BUTTON, history_element_boat_model, "tport", "history_element_minecart", "history/models");
    public static final InventoryModel history_element_world_tp_model          = new InventoryModel(Material.OAK_BUTTON, history_element_minecart_model, "tport", "history_element_world_tp", "history/models");
    public static final InventoryModel history_element_biome_tp_model          = new InventoryModel(Material.OAK_BUTTON, history_element_world_tp_model, "tport", "history_element_biome_tp", "history/models");
    public static final InventoryModel history_element_feature_tp_model        = new InventoryModel(Material.OAK_BUTTON, history_element_biome_tp_model, "tport", "history_element_feature_tp", "history/models");
    public static final InventoryModel history_element_tport_model             = new InventoryModel(Material.OAK_BUTTON, history_element_feature_tp_model, "tport", "history_element_tport", "history/models");
    public static final InventoryModel history_element_player_model            = new InventoryModel(Material.OAK_BUTTON, history_element_tport_model, "tport", "history_element_player", "history/models");
    public static final InventoryModel history_element_look_model              = new InventoryModel(Material.OAK_BUTTON, history_element_player_model, "tport", "history_element_look", "history/models");
    public static final InventoryModel history_element_death_model             = new InventoryModel(Material.OAK_BUTTON, history_element_look_model, "tport", "history_element_death", "history/models");
    public static final InventoryModel history_filter_model                    = new InventoryModel(Material.OAK_BUTTON, history_element_death_model, "tport", "history_filter", "history/filter");
    public static final InventoryModel history_filter_ender_pearl_model        = new InventoryModel(Material.OAK_BUTTON, history_filter_model, "tport", "history_filter_ender_pearl", "history/filter");
    public static final InventoryModel history_filter_command_model            = new InventoryModel(Material.OAK_BUTTON, history_filter_ender_pearl_model, "tport", "history_filter_command", "history/filter");
    public static final InventoryModel history_filter_plugin_model             = new InventoryModel(Material.OAK_BUTTON, history_filter_command_model, "tport", "history_filter_plugin", "history/filter");
    public static final InventoryModel history_filter_nether_portal_model      = new InventoryModel(Material.OAK_BUTTON, history_filter_plugin_model, "tport", "history_filter_nether_portal", "history/filter");
    public static final InventoryModel history_filter_end_portal_model         = new InventoryModel(Material.OAK_BUTTON, history_filter_nether_portal_model, "tport", "history_filter_end_portal", "history/filter");
    public static final InventoryModel history_filter_spectate_model           = new InventoryModel(Material.OAK_BUTTON, history_filter_end_portal_model, "tport", "history_filter_spectate", "history/filter");
    public static final InventoryModel history_filter_end_gateway_model        = new InventoryModel(Material.OAK_BUTTON, history_filter_spectate_model, "tport", "history_filter_end_gateway", "history/filter");
    public static final InventoryModel history_filter_chorus_fruit_model       = new InventoryModel(Material.OAK_BUTTON, history_filter_end_gateway_model, "tport", "history_filter_chorus_fruit", "history/filter");
    public static final InventoryModel history_filter_dismount_model           = new InventoryModel(Material.OAK_BUTTON, history_filter_chorus_fruit_model, "tport", "history_filter_dismount", "history/filter");
    public static final InventoryModel history_filter_exit_bed_model           = new InventoryModel(Material.OAK_BUTTON, history_filter_dismount_model, "tport", "history_filter_exit_bed", "history/filter");
    public static final InventoryModel history_filter_unknown_model            = new InventoryModel(Material.OAK_BUTTON, history_filter_exit_bed_model, "tport", "history_filter_unknown", "history/filter");
    public static final InventoryModel history_filter_none_model               = new InventoryModel(Material.OAK_BUTTON, history_filter_unknown_model, "tport", "history_filter_none", "history/filter");
    public static final InventoryModel history_filter_plugin_tport_model       = new InventoryModel(Material.OAK_BUTTON, history_filter_none_model, "tport", "history_filter_plugin_tport", "history/filter");
    public static final InventoryModel history_clear_model                     = new InventoryModel(Material.OAK_BUTTON, history_filter_plugin_tport_model, "tport", "history_clear", "history/filter");
    public static final InventoryModel world_tp_model                          = new InventoryModel(Material.OAK_BUTTON, history_clear_model, "tport", "world_tp", "world_tp");
    public static final InventoryModel world_tp_grayed_model                   = new InventoryModel(Material.OAK_BUTTON, world_tp_model, "tport", "world_tp_grayed", "world_tp");
    public static final InventoryModel world_tp_overworld_model                = new InventoryModel(Material.STONE,      world_tp_grayed_model, "tport", "world_tp_overworld", "world_tp");
    public static final InventoryModel world_tp_nether_model                   = new InventoryModel(Material.NETHERRACK, world_tp_overworld_model, "tport", "world_tp_nether", "world_tp");
    public static final InventoryModel world_tp_the_end_model                  = new InventoryModel(Material.END_STONE, world_tp_nether_model, "tport", "world_tp_the_end", "world_tp");
    public static final InventoryModel world_tp_other_environments_model       = new InventoryModel(Material.GLOWSTONE, world_tp_the_end_model, "tport", "world_tp_other_environments", "world_tp");
    public static final int last_model_id = world_tp_other_environments_model.getCustomModelData();
    
    public static void openMainTPortGUI(Player player) {
        openMainTPortGUI(player, 0, null);
    }
    public static void openMainTPortGUI(Player player, int page, FancyInventory prevWindow) {
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        ColorTheme theme = getTheme(player);
        
        List<ItemStack> list;
        if (prevWindow == null) {
            list = ItemFactory.getPlayerList(player, true, false, List.of(TPORT_AMOUNT, CLICK_EVENTS), List.of(ADD_OWNER, CLICK_TO_OPEN), null);
        } else {
            list = prevWindow.getData("content", List.class);
        }
        
        boolean noneSelected = true;
        String title = "tport.tportInventories.openMainGUI.title.selectNone";
        if (MainLayout.showPlayers(player)) {
            title = "tport.tportInventories.openMainGUI.title.selectPlayer";
            noneSelected = false;
        }
        if (MainLayout.showTPorts(player)) {
            if (noneSelected) {
                title = "tport.tportInventories.openMainGUI.title.selectTPort";
            } else {
                title = "tport.tportInventories.openMainGUI.title.selectBoth";
            }
        }
        
        Boolean backSafetyState = null; // false when player has no permission
        if (TPORT_BACK.hasPermission(player, false)) {
            backSafetyState = TPORT_BACK.getState(player);
        }
        Boolean homeSafetyState = null; // false when player has no permission
        if (TPORT_HOME.hasPermission(player, false)) {
            homeSafetyState = TPORT_HOME.getState(player);
        }
        
        FancyInventory inv = getDynamicScrollableInventory(player, page, TPortInventories::openMainTPortGUI, title, list, null);
        inv.setData("content", list);
        
        ItemStack biomeTP = (Features.Feature.BiomeTP.isEnabled() ? biome_tp_model : biome_tp_grayed_model).getItem(player);
        Message biomeTPTitle = formatTranslation(titleColor, titleColor, "tport.tportInventories.openMainGUI.biomeTP.title", "BiomeTP");
        Message biomeTPLeft = formatInfoTranslation("tport.tportInventories.openMainGUI.biomeTP.leftClick", LEFT);
        Message biomeTPRight = formatInfoTranslation("tport.tportInventories.openMainGUI.biomeTP.rightClick", RIGHT);
        Message biomeTPShiftRight = formatInfoTranslation("tport.tportInventories.openMainGUI.biomeTP.shiftRightClick", SHIFT_RIGHT);
        biomeTPTitle.translateMessage(playerLang);
        biomeTPLeft.translateMessage(playerLang);
        biomeTPRight.translateMessage(playerLang);
        biomeTPShiftRight.translateMessage(playerLang);
        setCustomItemData(biomeTP, theme, biomeTPTitle, Arrays.asList(biomeTPLeft, biomeTPRight, biomeTPShiftRight));
        addCommand(biomeTP, LEFT, "tport biomeTP");
        addCommand(biomeTP, RIGHT, "tport biomeTP preset");
        addCommand(biomeTP, SHIFT_RIGHT, "tport biomeTP random");
        
        ItemStack featureTP = (Features.Feature.FeatureTP.isEnabled() ? feature_tp_model : feature_tp_grayed_model).getItem(player);
        Message featureTPTitle = formatTranslation(titleColor, titleColor, "tport.tportInventories.openMainGUI.featureTP.title", "FeatureTP");
        featureTPTitle.translateMessage(playerLang);
        setCustomItemData(featureTP, theme, featureTPTitle, null);
        addCommand(featureTP, LEFT, "tport featureTP");
        
        ItemStack worldTP = (Features.Feature.WorldTP.isEnabled() ? world_tp_model : world_tp_grayed_model).getItem(player);
        Message worldTPTitle = formatTranslation(titleColor, titleColor, "tport.tportInventories.openMainGUI.worldTP.title", "WorldTP");
        worldTPTitle.translateMessage(playerLang);
        setCustomItemData(worldTP, theme, worldTPTitle, null);
        addCommand(worldTP, LEFT, "tport world");
        
        ItemStack history = (Features.Feature.History.isEnabled() ? history_model : history_grayed_model).getItem(player);
        Message historyTitle = formatTranslation(titleColor, titleColor, "tport.tportInventories.openMainGUI.history.title", "History");
        historyTitle.translateMessage(playerLang);
        setCustomItemData(history, theme, historyTitle, null);
        addCommand(history, LEFT, "tport history");
        
        ItemStack backTP = (Back.hasBack(player) ? back_tp_model : back_tp_grayed_model).getItem(player);
        Message backTPTitle = formatTranslation(titleColor, titleColor, "tport.tportInventories.openMainGUI.backTP.title", "BackTP");
        Message backTPLore = getPrevLocName(player);
        Message invertedBackTPLore = (backSafetyState == null ? null : formatInfoTranslation(playerLang, "tport.tportInventories.openMainGUI.backTP.inverted", SHIFT_LEFT, !backSafetyState));
        backTPTitle.translateMessage(playerLang);
        backTPLore.translateMessage(playerLang);
        Message backTPDeprecated = formatErrorTranslation(playerLang, "tport.tportInventories.openMainGUI.backTP.deprecated");
        setCustomItemData(backTP, theme, backTPTitle, Arrays.asList(backTPLore, invertedBackTPLore, new Message(), backTPDeprecated));
        addCommand(backTP, LEFT, "tport back");
        if (backSafetyState != null) addCommand(backTP, SHIFT_LEFT, "tport back " + !backSafetyState);
        
        ItemStack home = (Home.hasHome(player, true) ? home_model : home_grayed_model).getItem(player);
        Message homeTitle = formatTranslation(titleColor, titleColor, "tport.tportInventories.openMainGUI.home.title", "Home");
        TPort homeTPortObject = Home.getHome(player);
        List<Message> homeLore = new ArrayList<>();
        if (homeTPortObject == null) {
            homeLore.add(formatTranslation(varInfoColor, varInfo2Color, "tport.tportInventories.openMainGUI.home.unknown"));
            homeLore.add(new Message());
        } else {
            homeLore.add(formatInfoTranslation("tport.tportInventories.openMainGUI.home.tportName", homeTPortObject.getName()));
            homeLore.addAll(homeTPortObject.getHoverData(true));
            if (homeSafetyState != null) {
                homeLore.add(new Message());
                homeLore.add(formatInfoTranslation("tport.tportInventories.openMainGUI.home.inverted", SHIFT_LEFT, !homeSafetyState));
            }
        }
        homeLore.add(formatInfoTranslation("tport.tportInventories.openMainGUI.home.setHome", RIGHT));
        homeTitle.translateMessage(playerLang);
        homeLore = MessageUtils.translateMessage(homeLore, playerLang);
        setCustomItemData(home, theme, homeTitle, homeLore);
        addCommand(home, LEFT, "tport home");
        if (homeSafetyState != null) addCommand(home, SHIFT_LEFT, "tport home " + !homeSafetyState);
        addFunction(home, RIGHT, ((whoClicked, clickType, pdc, fancyInventory) -> openHomeEditGUI(whoClicked)));
        
        ItemStack publicTP = (Features.Feature.PublicTP.isEnabled() ? public_tp_model : public_tp_grayed_model).getItem(player);
        Message publicTPTitle = formatTranslation(titleColor, titleColor, "tport.tportInventories.openMainGUI.publicTP.title", "PublicTP");
        publicTPTitle.translateMessage(playerLang);
        addCommand(publicTP, LEFT, "tport public");
        setCustomItemData(publicTP, theme, publicTPTitle, null);
        
        ItemStack settings = settings_model.getItem(player);
        Message settingsTitle = formatTranslation(titleColor, titleColor, "tport.tportInventories.openMainGUI.settings.title");
        settingsTitle.translateMessage(playerLang);
        setCustomItemData(settings, theme, settingsTitle, null);
        addCommand(settings, LEFT, "tport settings");
        
        boolean pPermission = Players.getInstance().emptyPlayersState.hasPermissionToRun(player, false);
        boolean tPermission = TPorts.getInstance().emptyTPortsState.hasPermissionToRun(player, false);
        InventoryModel mainLayout_model = (!pPermission && !tPermission ? main_layout_grayed_model : main_layout_model);
        ItemStack mainLayout = mainLayout_model.getItem(player);
        Message mainLayoutTitle = formatTranslation(titleColor, titleColor, "tport.tportInventories.openMainGUI.mainLayout.title");
        String layoutState = "tport.tportInventories.openMainGUI.mainLayout.";
        List<Message> layoutLore = new ArrayList<>();
        if (pPermission || tPermission) {
            if (pPermission) layoutLore.add(formatInfoTranslation("tport.tportInventories.openMainGUI.mainLayout.tportState", formatTranslation(varInfoColor, varInfo2Color, layoutState + (MainLayout.showTPorts(player) ? "showing" : "hiding"))));
            if (tPermission) layoutLore.add(formatInfoTranslation("tport.tportInventories.openMainGUI.mainLayout.playerState", formatTranslation(varInfoColor, varInfo2Color, layoutState + (MainLayout.showPlayers(player) ? "showing" : "hiding"))));
            layoutLore.add(new Message());
            if (pPermission) layoutLore.add(formatInfoTranslation("tport.tportInventories.openMainGUI.mainLayout.editTPorts", LEFT, formatTranslation(varInfoColor, varInfo2Color, layoutState + (!MainLayout.showTPorts(player) ? "show" : "hide"))));
            if (tPermission) layoutLore.add(formatInfoTranslation("tport.tportInventories.openMainGUI.mainLayout.editPlayers", RIGHT, formatTranslation(varInfoColor, varInfo2Color, layoutState + (!MainLayout.showPlayers(player) ? "show" : "hide"))));
        } else {
            layoutLore.add(formatInfoTranslation("tport.tportInventories.openMainGUI.mainLayout.noPermissions.error"));
            layoutLore.add(formatInfoTranslation("tport.tportInventories.openMainGUI.mainLayout.noPermissions.permissions",
                    Players.getInstance().emptyPlayersState.getPermissions().get(0), TPorts.getInstance().emptyTPortsState.getPermissions().get(0)));
        }
        mainLayoutTitle.translateMessage(playerLang);
        layoutLore = MessageUtils.translateMessage(layoutLore, playerLang);
        setCustomItemData(mainLayout, theme, mainLayoutTitle, layoutLore);
        addCommand(mainLayout, LEFT, "tport mainLayout tports " + !MainLayout.showTPorts(player), "tport");
        addCommand(mainLayout, RIGHT, "tport mainLayout players " + !MainLayout.showPlayers(player), "tport");
        
        ItemStack sorting = ItemFactory.getSortingItem(player, playerLang, theme, ((whoClicked, clickType, pdc, fancyInventory) -> openMainTPortGUI(whoClicked)));
        
        inv.setItem(1, biomeTP);
        inv.setItem(3, featureTP);
        inv.setItem(5, worldTP);
        inv.setItem(7, history);
        inv.setItem(inv.getSize() - 8, home);
        inv.setItem(inv.getSize() - 6, backTP);
        inv.setItem(inv.getSize() - 4, publicTP);
        inv.setItem(inv.getSize() - 2, settings);
        
        inv.setItem(inv.getSize() / 18 * 9, sorting);
        inv.setItem(inv.getSize() / 18 * 9 + 8, mainLayout);
        
        inv.open(player);
    }
    
    public static void openTPortGUI(UUID ownerUUID, Player player) {
        openTPortGUI(ownerUUID, player, null);
    }
    public static void openTPortGUI(UUID ownerUUID, Player player, @Nullable FancyInventory prevWindow) {
        String newPlayerName = PlayerUUID.getPlayerName(ownerUUID);
        
        Validate.notNull(newPlayerName, "The newPlayerName can not be null");
        Validate.notNull(ownerUUID, "The ownerUUID can not be null");
        Validate.notNull(player, "The player can not be null");
        
        FancyInventory inv = new FancyInventory(27, formatInfoTranslation("tport.tportInventories.openTPortGUI.title", newPlayerName));
        inv.setData("ownerUUID", ownerUUID);
        if (prevWindow != null) inv.setData(tportToMoveDataName, prevWindow.getData(tportToMoveDataName));
        if (prevWindow != null) inv.setData(whitelistCloneToDataName, prevWindow.getData(whitelistCloneToDataName));
        
        ColorTheme theme = getTheme(player);
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        
        Message extraTPTitle = formatTranslation(titleColor, titleColor, "tport.tportInventories.openTPortGUI.extraTP.title");
        List<Message> extraTPLore = new ArrayList<>();
        
        Boolean backSafetyState = null; // false when player has no permission
        if (TPORT_BACK.hasPermission(player, false)) {
            backSafetyState = TPORT_BACK.getState(player);
        }
        
        boolean extraTPEmptyFlag = false;
        if (Features.Feature.BackTP.isEnabled()) {
            extraTPLore.add(formatInfoTranslation("tport.tportInventories.openTPortGUI.extraTP.format.leftClick", LEFT));
            extraTPLore.add(getPrevLocName(player));
            if (backSafetyState != null) extraTPLore.add(formatInfoTranslation("tport.tportInventories.openTPortGUI.extraTP.format.shift_leftClick", SHIFT_LEFT, !backSafetyState));
            extraTPLore.add(new Message());
        }
        if (Features.Feature.BiomeTP.isEnabled()) {
            extraTPLore.add(formatInfoTranslation("tport.tportInventories.openTPortGUI.extraTP.format.rightClick", RIGHT));
            extraTPLore.add(new Message(textComponent("BiomeTP", varInfoColor)));
            extraTPLore.add(new Message());
        }
        if (Features.Feature.FeatureTP.isEnabled()) {
            extraTPLore.add(formatInfoTranslation("tport.tportInventories.openTPortGUI.extraTP.format.shiftRightClick", SHIFT_RIGHT));
            extraTPLore.add(new Message(textComponent("FeatureTP", varInfoColor)));
            extraTPLore.add(new Message());
        }
        if (!extraTPLore.isEmpty()) {
            extraTPLore.remove(extraTPLore.size() - 1);
        } else {
            extraTPLore = new ArrayList<>(List.of(formatInfoTranslation("tport.tportInventories.openTPortGUI.extraTP.format.none")));
            extraTPEmptyFlag = true;
        }
        
        ItemStack extraTP = (extraTPEmptyFlag ? extra_tp_grayed_model : extra_tp_model).getItem(player);
        extraTPTitle.translateMessage(playerLang);
        extraTPLore = MessageUtils.translateMessage(extraTPLore, playerLang);
        setCustomItemData(extraTP, theme, extraTPTitle, extraTPLore);
        addCommand(extraTP, LEFT, "tport back");
        if (backSafetyState != null) addCommand(extraTP, SHIFT_LEFT, "tport back " + !backSafetyState);
        addCommand(extraTP, RIGHT, "tport biomeTP");
        addCommand(extraTP, SHIFT_RIGHT, "tport featureTP");
        inv.setItem(17, extraTP);
        
        inv.setItem(26, createBack(player, MAIN, OWN, PUBLIC));
        
        boolean pltpState = State.getPLTPState(ownerUUID);
        if (ownerUUID.equals(player.getUniqueId())) {
            ItemStack warp = new ItemStack(Material.PLAYER_HEAD);
            
            boolean pltpConsent = Consent.shouldAskConsent(ownerUUID);
            Offset.PLTPOffset pltpOffset = Offset.getPLTPOffset(player);
            Preview.PreviewState previewState = Preview.getPreviewState(ownerUUID);
            
            Message warpTitle = formatTranslation(titleColor, titleColor, "tport.tportInventories.openTPortGUI.playerHead.own.format.title");
            List<Message> warpLore = new ArrayList<>();
            warpLore.add(formatInfoTranslation("tport.tportInventories.openTPortGUI.playerHead.own.format.PLTPState", pltpState));
            warpLore.add(formatInfoTranslation("tport.tportInventories.openTPortGUI.playerHead.own.format.PLTPConsent", pltpConsent));
            warpLore.add(formatInfoTranslation("tport.tportInventories.openTPortGUI.playerHead.own.format.PLTPPreview", previewState));
            warpLore.add(formatInfoTranslation("tport.tportInventories.openTPortGUI.playerHead.own.format.PLTPOffset", pltpOffset));
            warpLore.add(new Message());
            warpLore.add(formatInfoTranslation("tport.tportInventories.openTPortGUI.playerHead.own.format.whenLeftClick",
                    LEFT, !pltpState));
            warpLore.add(formatInfoTranslation("tport.tportInventories.openTPortGUI.playerHead.own.format.whenRightClick",
                    RIGHT, !pltpConsent));
            warpLore.add(formatInfoTranslation("tport.tportInventories.openTPortGUI.playerHead.own.format.whenShiftLeftClick",
                    SHIFT_LEFT, previewState.getNext().name()));
            warpLore.add(formatInfoTranslation("tport.tportInventories.openTPortGUI.playerHead.own.format.whenShiftRightClick",
                    SHIFT_RIGHT, pltpOffset.getNext().name()));
            warpLore.add(formatInfoTranslation("tport.tportInventories.openTPortGUI.playerHead.own.format.whenDrop",
                    DROP));
            
            warpTitle.translateMessage(playerLang);
            warpLore = MessageUtils.translateMessage(warpLore, playerLang);
            setCustomItemData(warp, theme, warpTitle, warpLore);
            
            addCommand(warp, LEFT, "tport PLTP state " + !pltpState, "tport own");
            addCommand(warp, RIGHT, "tport PLTP consent " + !pltpConsent, "tport own");
            addCommand(warp, SHIFT_LEFT, "tport PLTP preview " + previewState.getNext(), "tport own");
            addCommand(warp, SHIFT_RIGHT, "tport PLTP offset " + pltpOffset.getNext(), "tport own");
            addFunction(warp, DROP,
                    (whoClicked, clickType, innerPDC, fancyInventory) -> openPLTPWhitelistSelectorGUI(whoClicked, false));
            
            SkullMeta skin = (SkullMeta) warp.getItemMeta();
            if (skin != null) {
                skin.setOwningPlayer(Bukkit.getOfflinePlayer(ownerUUID));
                warp.setItemMeta(skin);
            }
            inv.setItem(8, warp);
        }
        else {
            ItemStack warp = new ItemStack(Material.PLAYER_HEAD);
            String warpTitleSuffix;
            boolean showPreview = false;
            if (!pltpState) {
                ArrayList<String> list = Whitelist.getPLTPWhitelist(ownerUUID);
                
                if (list.contains(player.getUniqueId().toString())) {
                    warpTitleSuffix = "warp";
                    showPreview = true;
                } else {
                    warpTitleSuffix = "off";
                }
            } else if (Bukkit.getPlayer(ownerUUID) != null) {
                warpTitleSuffix = "warp";
                showPreview = true;
            } else {
                warpTitleSuffix = "offline";
            }
            
            String otherPlayerName = PlayerUUID.getPlayerName(ownerUUID);
            Message warpTitle = formatTranslation(titleColor, titleColor, "tport.tportInventories.openTPortGUI.playerHead.other.format." + warpTitleSuffix, otherPlayerName);
            warpTitle.translateMessage(playerLang);
            
            List<Message> lore = null;
            if (showPreview) {
                Message warpPreview = formatInfoTranslation("tport.tportInventories.openTPortGUI.playerHead.other.format.preview", RIGHT, otherPlayerName);
                warpPreview.translateMessage(playerLang);
                lore = Arrays.asList(new Message(), warpPreview);
            }
            
            setCustomItemData(warp, theme, warpTitle, lore);
            addCommand(warp, LEFT, "tport PLTP tp " + newPlayerName);
            addCommand(warp, RIGHT, "tport preview " + newPlayerName);
            
            SkullMeta skin = (SkullMeta) warp.getItemMeta();
            if (skin != null) {
                skin.setOwningPlayer(Bukkit.getOfflinePlayer(ownerUUID));
                warp.setItemMeta(skin);
            }
            inv.setItem(8, warp);
        }
        
        int slotOffset = 0;
        List<TPort> sortedTPortList = TPortManager.getSortedTPortList(tportData, ownerUUID);
        for (int i = 0; i < sortedTPortList.size(); i++) {
            
            if (i == 8 || i == 16/*16 because of the slot+slotOffset*/) {
                slotOffset++;
            }
            
            TPort tport = sortedTPortList.get(i);
            if (tport != null) {
                inv.setItem(i + slotOffset, toTPortItem(tport, player, List.of(QUICK_EDITOR, CLICK_TO_OPEN), prevWindow));
            } else {
                if (ownerUUID.equals(player.getUniqueId())) {
                    UUID toMoveUUID = null;
                    if (prevWindow != null) toMoveUUID = prevWindow.getData(tportToMoveDataName);
                    if (toMoveUUID != null) {
                        ItemStack empty = quick_edit_move_empty_slot_model.getItem(player);
                        
                        TPort toMoveTPort = getTPort(ownerUUID, toMoveUUID);
                        Message emptyTitle = formatInfoTranslation("tport.tportInventories.openTPortGUI.quickEditReplacement", RIGHT, toMoveTPort);
                        Message emptyLoreTPort = formatInfoTranslation("tport.tportInventories.openTPortGUI.quickEditReplacement.tport", RIGHT);
                        Message emptyLoreCancel = formatInfoTranslation("tport.tportInventories.openTPortGUI.quickEditReplacement.cancel", RIGHT, toMoveTPort);
                        emptyTitle.translateMessage(playerLang);
                        emptyLoreTPort.translateMessage(playerLang);
                        emptyLoreCancel.translateMessage(playerLang);
                        setCustomItemData(empty, theme, emptyTitle, Arrays.asList(emptyLoreTPort, emptyLoreCancel));
                        
                        ItemMeta emptyMeta = empty.getItemMeta();
                        emptyMeta.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "TPortNameToMove"), PersistentDataType.STRING, toMoveTPort.getName());
                        emptyMeta.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "TPortSlot"), PersistentDataType.STRING, String.valueOf(i + 1));
                        
                        addFunction(emptyMeta, RIGHT, (whoClicked, clickType, pdc, fancyInventory) -> {
                            String tportNameToMove = pdc.get(new NamespacedKey(Main.getInstance(), "TPortNameToMove"), PersistentDataType.STRING);
                            String toSlot = pdc.get(new NamespacedKey(Main.getInstance(), "TPortSlot"), PersistentDataType.STRING);
                            TPortCommand.executeTPortCommand(whoClicked, new String[]{"edit", tportNameToMove, "move", toSlot});
                            openTPortGUI(whoClicked.getUniqueId(), whoClicked, null);
                        });
                        
                        empty.setItemMeta(emptyMeta);
                        inv.setItem(i + slotOffset, empty);
                    }
                }
            }
        }
        inv.open(player);
    }
    
    public static final FancyInventory.DataName<Set> biomeSelectionDataName = new FancyInventory.DataName<>("biomeSelection", Set.class, new HashSet<String>());
    public static void openBiomeTP(Player player) {
        openBiomeTP(player, 0, null);
    }
    public static void openBiomeTP(Player player, int page, @Nullable FancyInventory prevWindow) {
        List<ItemStack> list = new ArrayList<>();
        ColorTheme theme = getTheme(player);
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        
        Set<String> biomeSelection;
        if (prevWindow != null) {
            biomeSelection = prevWindow.getData(biomeSelectionDataName);
        } else {
            biomeSelection = new HashSet<>();
        }
        
        for (String biome : BiomeTP.availableBiomes(player.getWorld())) {
            if (biome.equals("custom")) continue;
            Material material = BiomeTP.getMaterial(biome);
            
            boolean selected = false;
            Message selectedMessage;
            if (biomeSelection.contains(biome)) {
                selected = true;
                selectedMessage = formatTranslation(varInfoColor, varInfoColor, "tport.tportInventories.openBiomeTP.biome.unselect");
            } else {
                selectedMessage = formatTranslation(varInfoColor, varInfoColor, "tport.tportInventories.openBiomeTP.biome.select");
            }
            
            ItemStack item = new ItemStack(material);
            Message biomeTitle = formatTranslation(varInfoColor, varInfoColor, "%s", new BiomeEncapsulation(biome));
            biomeTitle.translateMessage(playerLang);
            Message biomeLClick = formatInfoTranslation(playerLang, "tport.tportInventories.openBiomeTP.biome.LClick", LEFT, selectedMessage);
            Message biomeRClick = formatInfoTranslation(playerLang, "tport.tportInventories.openBiomeTP.biome.RClick", RIGHT);
            Message biomeSRClick = formatInfoTranslation(playerLang, "tport.tportInventories.openBiomeTP.biome.shift_RClick", SHIFT_RIGHT);
            setCustomItemData(item, theme, biomeTitle, Arrays.asList(biomeLClick, biomeRClick, biomeSRClick));
            
            ItemMeta im = item.getItemMeta();
            im.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "biome"), PersistentDataType.STRING, biome);
            if (selected) Glow.addGlow(im);
            
            addFunction(im, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
                NamespacedKey biomeKey = new NamespacedKey(Main.getInstance(), "biome");
                if (pdc.has(biomeKey, PersistentDataType.STRING)) {
                    Set<String> innerBiomeSelection = fancyInventory.getData(biomeSelectionDataName);
                    String innerBiome = pdc.get(biomeKey, PersistentDataType.STRING);
                    if (innerBiomeSelection.contains(innerBiome)) {
                        innerBiomeSelection.remove(innerBiome);
                    } else {
                        innerBiomeSelection.add(innerBiome);
                    }
                    fancyInventory.setData(biomeSelectionDataName, innerBiomeSelection);
                    openBiomeTP(whoClicked, fancyInventory.getData(pageDataName), fancyInventory);
                }
            }));
            addCommand(im, RIGHT, "tport biomeTP whitelist " + biome);
            addCommand(im, SHIFT_RIGHT, "tport biomeTP blacklist " + biome);
            
            item.setItemMeta(im);
            
            if (selected) list.add(0, item);
            else list.add(item);
        }
        
        ItemStack random = (Random.getInstance().hasPermissionToRun(player, false) ? biome_tp_random_tp_model : biome_tp_random_tp_grayed_model).getItem(player);
        Message randomTitle = formatTranslation(titleColor, titleColor, "tport.tportInventories.openBiomeTP.randomTP.title");
        randomTitle.translateMessage(playerLang);
        setCustomItemData(random, theme, randomTitle, null);
        addCommand(random, LEFT, "tport biomeTP random");
        list.add(0, random);
        
        ItemStack presets = (Preset.getInstance().hasPermissionToRun(player, false) ? biome_tp_presets_model : biome_tp_presets_grayed_model).getItem(player);
        Message presetsTitle = formatTranslation(titleColor, titleColor, "tport.tportInventories.openBiomeTP.presets.title");
        presetsTitle.translateMessage(playerLang);
        setCustomItemData(presets, theme, presetsTitle, null);
        addFunction(presets, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
            if (Preset.getInstance().hasPermissionToRun(whoClicked, true))
                openBiomeTPPreset(whoClicked, 0, fancyInventory);
        }));
        
        ItemStack clearSelected = (biomeSelection.isEmpty() ? biome_tp_clear_grayed_model : biome_tp_clear_model).getItem(player);
        Message modeTitle = formatInfoTranslation("tport.tportInventories.openBiomeTP.clearSelected.title", LEFT);
        modeTitle.translateMessage(playerLang);
        setCustomItemData(clearSelected, theme, modeTitle, null);
        addFunction(clearSelected, LEFT, (whoClicked, clickType, pdc, fancyInventory) -> {
            if (fancyInventory.getData(biomeSelectionDataName).isEmpty()) {
                sendErrorTranslation(whoClicked, "tport.tportInventories.openBiomeTP.clearSelected.noSelection");
            } else {
                sendSuccessTranslation(whoClicked, "tport.tportInventories.openBiomeTP.clearSelected.succeeded");
            }
            openBiomeTP(whoClicked, fancyInventory.getData(pageDataName), null);
        });
        
        ItemStack run = (biomeSelection.isEmpty() ? biome_tp_run_grayed_model : biome_tp_run_model).getItem(player);
        Mode.WorldSearchMode biomeTPMode = com.spaceman.tport.commands.tport.biomeTP.Mode.getDefMode(player.getUniqueId());
        Message runTitle = formatTranslation(titleColor, titleColor, "tport.tportInventories.openBiomeTP.run.title");
        runTitle.translateMessage(playerLang);
        Message runWhitelist = formatInfoTranslation("tport.tportInventories.openBiomeTP.run.whitelist", LEFT);
        runWhitelist.translateMessage(playerLang);
        Message runBlacklist = formatInfoTranslation("tport.tportInventories.openBiomeTP.run.blacklist", RIGHT);
        runBlacklist.translateMessage(playerLang);
        Message runCurrentMode = formatInfoTranslation("tport.tportInventories.openBiomeTP.run.currentMode", biomeTPMode.name());
        runCurrentMode.translateMessage(playerLang);
        Message runChangeMode = formatInfoTranslation("tport.tportInventories.openBiomeTP.run.changeMode", SHIFT_RIGHT, biomeTPMode.getNext());
        runChangeMode.translateMessage(playerLang);
        setCustomItemData(run, theme, runTitle, Arrays.asList(runWhitelist, runBlacklist, new Message(), runCurrentMode, runChangeMode));
        if (!biomeSelection.isEmpty()) {
            addCommand(run, LEFT, "tport biomeTP whitelist " + String.join(" ", biomeSelection));
            addCommand(run, RIGHT, "tport biomeTP blacklist " + String.join(" ", biomeSelection));
        }
        addCommand(run, SHIFT_RIGHT, "tport biomeTP mode " + biomeTPMode.getNext().name());
        addFunction(run, SHIFT_RIGHT, ((whoClicked, clickType, pdc, fancyInventory) -> openBiomeTP(whoClicked, fancyInventory.getData(pageDataName), fancyInventory)));
        
        FancyInventory inv = getDynamicScrollableInventory(player, page, TPortInventories::openBiomeTP, "tport.tportInventories.openBiomeTP.title", list, createBack(player, MAIN, OWN, FEATURE_TP, BIOME_TP), 45);
        inv.setData(biomeSelectionDataName, biomeSelection);
        
        inv.setItem(9, clearSelected);
        inv.setItem(27, presets);
        inv.setItem(18, run);
        
        inv.open(player);
        TPortAdvancement.Advancement_BiomeTP.grant(player);
    }
    public static void openBiomeTPPreset(Player player, int page, @Nullable FancyInventory prevWindow) {
        FancyInventory inv = getDynamicScrollableInventory(player, page, TPortInventories::openBiomeTPPreset, "tport.tportInventories.openBiomeTPPreset.title",
                BiomePreset.getItems(player), createBack(player, BIOME_TP, OWN, MAIN));
        if (prevWindow != null) inv.setData(biomeSelectionDataName, prevWindow.getData(biomeSelectionDataName));
        inv.open(player);
    }
    
    public static final FancyInventory.DataName<Set> featureSelectionDataName = new FancyInventory.DataName<>("featureSelection", Set.class, new HashSet<String>());
    public static void openFeatureTP(Player player) {
        openFeatureTP(player, 0, null);
    }
    public static void openFeatureTP(Player player, int page, @Nullable FancyInventory prevWindow) {
        ColorTheme theme = getTheme(player);
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        
        Set<String> featureSelection;
        if (prevWindow != null) {
            featureSelection = prevWindow.getData(featureSelectionDataName);
        } else {
            featureSelection = new HashSet<>();
        }
        
        FancyInventory inv = getDynamicScrollableInventory(player, page, TPortInventories::openFeatureTP, "tport.tportInventories.openFeatureTP.title",
                FeatureTP.getItems(player, featureSelection), createBack(player, MAIN, OWN, WORLD_TP, FEATURE_TP));
        inv.setData(featureSelectionDataName, featureSelection);
        
        ItemStack run = (featureSelection.isEmpty() ? feature_tp_run_grayed_model : feature_tp_run_model).getItem(player);
        Mode.WorldSearchMode featureTPMode = Mode.getDefMode(player.getUniqueId());
        Message runTitle = formatTranslation(titleColor, titleColor, "tport.tportInventories.openFeatureTP.run.title");
        runTitle.translateMessage(playerLang);
        Message runMode1 = formatInfoTranslation("tport.tportInventories.openFeatureTP.run.mode", LEFT, featureTPMode);
        runMode1.translateMessage(playerLang);
        Message runMode2 = formatInfoTranslation("tport.tportInventories.openFeatureTP.run.mode", RIGHT, featureTPMode.getNext());
        runMode2.translateMessage(playerLang);
        Message runCurrentMode = formatInfoTranslation("tport.tportInventories.openFeatureTP.run.currentMode", featureTPMode);
        runCurrentMode.translateMessage(playerLang);
        Message runChangeMode = formatInfoTranslation("tport.tportInventories.openFeatureTP.run.changeMode", SHIFT_RIGHT, featureTPMode.getNext());
        runChangeMode.translateMessage(playerLang);
        setCustomItemData(run, theme, runTitle, Arrays.asList(runMode1, runMode2, new Message(), runCurrentMode, runChangeMode));
        if (!featureSelection.isEmpty()) {
            addCommand(run, LEFT, "tport featureTP search " + featureTPMode + " " + String.join(" ", featureSelection));
            addCommand(run, RIGHT, "tport featureTP search " + featureTPMode.getNext() + " " + String.join(" ", featureSelection));
        }
        addCommand(run, SHIFT_RIGHT, "tport featureTP mode " + featureTPMode.getNext().name(), "tport featureTP");
        
        ItemStack clearSelected = (featureSelection.isEmpty() ? feature_tp_clear_grayed_model : feature_tp_clear_model).getItem(player);
        Message clearSelectedTitle = formatInfoTranslation("tport.tportInventories.openFeatureTP.clearSelected.title", LEFT);
        clearSelectedTitle.translateMessage(playerLang);
        setCustomItemData(clearSelected, theme, clearSelectedTitle, null);
        addFunction(clearSelected, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
            if (fancyInventory.getData(featureSelectionDataName).isEmpty()) {
                sendErrorTranslation(whoClicked, "tport.tportInventories.openFeatureTP.clearSelected.noSelection");
            } else {
                sendSuccessTranslation(whoClicked, "tport.tportInventories.openFeatureTP.clearSelected.succeeded");
            }
            openFeatureTP(whoClicked, fancyInventory.getData(pageDataName), null);
        }));
        
        inv.setItem(9, clearSelected);
        inv.setItem(18, run);
        inv.open(player);
        TPortAdvancement.Advancement_FeatureTP.grant(player);
    }
    
    public static void openWorldTP(Player player) {
        openWorldTP(player, 0, null);
    }
    public static void openWorldTP(Player player, int page, @Nullable FancyInventory prevWindow) {
        ColorTheme theme = getTheme(player);
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        
        List<ItemStack> worlds = new ArrayList<>();
        for (World world : Bukkit.getWorlds()) {
            ItemStack is = (switch (world.getEnvironment()) {
                case NORMAL -> world_tp_overworld_model;
                case NETHER -> world_tp_nether_model;
                case THE_END -> world_tp_the_end_model;
                default -> world_tp_other_environments_model;
            }).getItem(player);
            
            Message title = formatTranslation(titleColor, titleColor, "tport.tportInventories.openWorldTP.world.name", world.getName());
            
            List<Message> lore = new ArrayList<>();
            lore.add(formatInfoTranslation("tport.tportInventories.openWorldTP.world.run", LEFT));
            lore.add(new Message());
            lore.add(formatInfoTranslation("tport.tportInventories.openWorldTP.world.environment", world.getEnvironment()));
            lore.add(formatInfoTranslation("tport.tportInventories.openWorldTP.world.amountPlayers", world.getPlayers().size()));
            lore.add(formatInfoTranslation("tport.tportInventories.openWorldTP.world.difficulty", world.getDifficulty()));
            lore.add(formatInfoTranslation("tport.tportInventories.openWorldTP.world.pvp", world.getPVP()));
            
            title.translateMessage(playerLang);
            lore = MessageUtils.translateMessage(lore, playerLang);
            
            setCustomItemData(is, theme, title, lore);
            addCommand(is, LEFT, "tport world " + world.getName());
            
            worlds.add(is);
        }
        
        FancyInventory inv = getDynamicScrollableInventory(player, page, TPortInventories::openWorldTP, "tport.tportInventories.openWorldTP.title",
                worlds, createBack(player, MAIN, OWN, PUBLIC, WORLD_TP));
        
        inv.open(player);
        TPortAdvancement.Advancement_WorldTP.grant(player);
    }
    
    public static void openPublicTPortGUI(Player player) {
        openPublicTPortGUI(player, 0, null);
    }
    public static void openPublicTPortGUI(Player player, int page, @Nullable FancyInventory prevWindow) {
        //public.tports.<publicTPortSlot>.<TPortID>
        
        JsonObject playerLang = getPlayerLang(player);
        ColorTheme colorTheme = getTheme(player);
        
        boolean filter = false;
        FancyInventory.DataName<Boolean> filterDataName = new FancyInventory.DataName<>("filter", Boolean.class, false);
        if (prevWindow != null) filter = prevWindow.getData("filter", Boolean.class, false);
        
        List<ItemStack> list = new ArrayList<>();
        
        for (String publicTPortSlot : tportData.getKeys("public.tports")) {
            String tportID = tportData.getConfig().getString("public.tports." + publicTPortSlot, TPortManager.defUUID.toString());
            
            TPort tport = getTPort(UUID.fromString(tportID));
            if (tport != null) {
                if (filter && !tport.getOwner().equals(player.getUniqueId())) continue;
                list.add(toTPortItem(tport, player, List.of(ADD_OWNER, CLICK_TO_OPEN_PUBLIC), prevWindow));
                if (tport.setPublicTPort(true)) {
                    tport.save();
                }
            } else {
                int publicSlotTmp = Integer.parseInt(publicTPortSlot) + 1;
                String tportID2 = tportData.getConfig().getString("public.tports." + publicSlotTmp, TPortManager.defUUID.toString());
                
                TPort tport2 = getTPort(UUID.fromString(tportID2));
                if (tport2 != null) {
                    if (filter && !tport.getOwner().equals(player.getUniqueId())) continue;
                    list.add(toTPortItem(tport2, player, List.of(ADD_OWNER, CLICK_TO_OPEN_PUBLIC), prevWindow));
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
        
        FancyInventory inv = getDynamicScrollableInventory(player, page, TPortInventories::openPublicTPortGUI, "tport.tportInventories.openPublicTP.title", list, createBack(player, MAIN, OWN, BIOME_TP, PUBLIC));
        if (prevWindow != null) inv.setData(tportToMoveDataName, prevWindow.getData(tportToMoveDataName));
        inv.setData(filterDataName, filter);
        
        ItemStack publicFilter = (filter ? public_tp_filter_own_model : public_tp_filter_all_model).getItem(player);
        addFunction(publicFilter, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
            fancyInventory.setData("filter", !fancyInventory.getData("filter", Boolean.class, false));
            openPublicTPortGUI(whoClicked, fancyInventory.getData(pageDataName), fancyInventory);
        }));
        Message publicFilterTitle = formatInfoTranslation(playerLang, "tport.tportInventories.openPublicTP.filter." + (filter ? "own" : "all") + ".title");
        Message publicFilterClick = formatInfoTranslation(playerLang, "tport.tportInventories.openPublicTP.filter." + (filter ? "own" : "all") + ".click", LEFT);
        setCustomItemData(publicFilter, colorTheme, publicFilterTitle, List.of(new Message(), publicFilterClick));
        inv.setItem(inv.getSize() - 9, publicFilter);
        
        inv.open(player);
    }
    
    private static final FancyInventory.DataName<List> searchResultDataName = new FancyInventory.DataName<>("searchResult", List.class, new ArrayList<>());
    private static final FancyInventory.DataName<String> queryDataName = new FancyInventory.DataName<>("query", String.class, "");
    private static final FancyInventory.DataName<String> searcherNameDataName = new FancyInventory.DataName<>("searcherName", String.class, "");
    private static final FancyInventory.DataName<SearchMode> searchModeDataName = new FancyInventory.DataName<>("searchMode", SearchMode.class, null);
    public static void openSearchGUI(Player player, int page, SearchMode searchMode, String searcherName, @Nonnull String query) {
        SearchType searcher = Search.Searchers.getSearcher(searcherName);
        if (searcher == null) {
            sendErrorTranslation(player, "tport.tportInventories.openSearchGUI.searchData.couldNotFindSearcher", searcherName);
            return;
        }
        if (!searcher.hasPermission(player, true)) {
            return;
        }
        List<ItemStack> searchResult = searcher.search(searchMode, query, player);
        CooldownManager.Search.update(player);
        Advancement_LostAndFound.grant(player);
        
        openSearchGUI(player, page, searchResult, query, searcherName, searchMode);
    }
    private static void openSearchGUI(Player player, int page, FancyInventory fancyInventory) {
        openSearchGUI(player, page,
                fancyInventory.getData(searchResultDataName),
                fancyInventory.getData(queryDataName),
                fancyInventory.getData(searcherNameDataName),
                fancyInventory.getData(searchModeDataName));
    }
    private static void openSearchGUI(Player player, int page, List<ItemStack> searchResult, String query, String searcherName, SearchMode searchMode) {
        FancyInventory inv = getDynamicScrollableInventory(player, page, TPortInventories::openSearchGUI, "tport.tportInventories.openSearchGUI.title",
                searchResult, createBack(player, SEARCH, OWN, MAIN));
        
        inv.setData(searchResultDataName, searchResult);
        inv.setData(queryDataName, query);
        inv.setData(searcherNameDataName, searcherName);
        inv.setData(searchModeDataName, searchMode);
        
        ColorTheme theme = getTheme(player);
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        
        ItemStack searchData = search_data_model.getItem(player);
        Message title = formatTranslation(titleColor, titleColor, "tport.tportInventories.openSearchGUI.searchData.title");
        title.translateMessage(playerLang);
        Message searchTypeLore = formatInfoTranslation("tport.tportInventories.openSearchGUI.searchData.type", searcherName);
        searchTypeLore.translateMessage(playerLang);
        Message searchQueryLore = query.isEmpty() ? null : formatInfoTranslation("tport.tportInventories.openSearchGUI.searchData.query", query);
        if (!query.isEmpty()) searchQueryLore.translateMessage(playerLang);
        Message searchModeLore = searchMode == null ? null : formatInfoTranslation("tport.tportInventories.openSearchGUI.searchData.mode", searchMode);
        if (searchMode != null) searchModeLore.translateMessage(playerLang);
        setCustomItemData(searchData, theme, title, Arrays.asList(searchTypeLore, searchQueryLore, searchModeLore));
        inv.setItem(0, searchData);
        
        inv.open(player);
    }
    
    public static void openHomeEditGUI(Player player) {
        openHomeEditGUI(player, 0, null);
    }
    public static void openHomeEditGUI(Player player, int page, @Nullable FancyInventory prevWindow) {
        List<ItemStack> list;
        
        if (prevWindow == null) {
            list = ItemFactory.getPlayerList(player, true, false, List.of(TPORT_AMOUNT, HOME_PLAYER_SELECTION), List.of(ADD_OWNER), null);
        } else {
            list = prevWindow.getData("content", List.class);
        }
        
        FancyInventory inv = getDynamicScrollableInventory(player, page, TPortInventories::openHomeEditGUI, "tport.tportInventories.openHomeEditGUI.title", list, createBack(player, MAIN, OWN, null, null));
        inv.setData("content", list);
        
        inv.setItem(inv.getSize() / 18 * 9, ItemFactory.getSortingItem(player, getPlayerLang(player.getUniqueId()), getTheme(player), ((whoClicked, clickType, pdc, fancyInventory) -> openHomeEditGUI(whoClicked))));
        
        inv.open(player);
    }
    public static void openHomeEdit_SelectTPortGUI(UUID ownerUUID, Player player) {
        FancyInventory inv = new FancyInventory(27, formatInfoTranslation("tport.tportInventories.openHomeEdit_selectTPortGUI.title"));
        
        int slotOffset = 0;
        List<TPort> sortedTPortList = TPortManager.getSortedTPortList(tportData, ownerUUID);
        for (int i = 0; i < sortedTPortList.size(); i++) {
            
            if (i == 8 || i == 16/*16 because of the slot+slotOffset*/) {
                slotOffset++;
            }
            
            TPort tport = sortedTPortList.get(i);
            if (tport != null) {
                inv.setItem(i + slotOffset, toTPortItem(tport, player, List.of(SELECT_HOME)));
            }
        }
        
        inv.setItem(26, createBack(player, HOME_SET, OWN, MAIN));
        inv.open(player);
    }
    
    public static final FancyInventory.DataName<String> historyFilterDataName = new FancyInventory.DataName<>("historyFilter", String.class, null);
    public static void openHistory(Player player) {
        openHistory(player, null);
    }
    public static void openHistory(Player player, @Nullable String filter) {
        openHistory(player, filter, 0, null);
    }
    private static void openHistory(Player player, @Nullable String filter, int page, @Nullable FancyInventory prevWindow) {
        ColorTheme colorTheme = getTheme(player);
        JsonObject playerLang = getPlayerLang(player);
        
        if (filter != null) {
            String filter2 = HistoryFilter.exist(filter);
            if (filter2 == null) {
                sendErrorTranslation(player, "tport.tportInventories.openHistory.filterNotFound", filter);
                return;
            }
            filter = filter2;
        }
        
        List<ItemStack> items;
        if (prevWindow == null) {
            ArrayList<HistoryElement> history = teleportHistory.getOrDefault(player.getUniqueId(), new ArrayList<>());
            
            items = new ArrayList<>(history.size());
            int index = 0;
            
            for (HistoryElement element : history) {
                index++;
                
                if (!HistoryFilter.fits(element, filter)) {
                    continue;
                }
                
                ItemStack is = Main.getOrDefault(element.inventoryModel(), history_element_model).getItem(player);
                
                LocationSource newLocationSource = element.newLocation();
                LocationSource newLocationLoc = null;
                if (!(newLocationSource instanceof CraftLocationSource)) {
                    newLocationLoc = new CraftLocationSource();
                    newLocationLoc.setLocation(newLocationSource.getLocation(player));
                }
                LocationSource oldLocation = new CraftLocationSource(element.oldLocation());
                Message elementTitle = formatInfoTranslation(playerLang, "tport.tportInventories.openHistory.element.title", index, oldLocation, newLocationSource, element.cause(), element.application());
                Message from = formatInfoTranslation(playerLang, "tport.tportInventories.openHistory.element.line.from", oldLocation);
                Message to = formatInfoTranslation(playerLang, "tport.tportInventories.openHistory.element.line.to", newLocationSource);
                Message to2 = null;
                if (newLocationLoc != null) {
                    to2 = formatInfoTranslation(playerLang, "tport.tportInventories.openHistory.element.line.to", newLocationLoc);
                }
                Message cause = formatInfoTranslation(playerLang, "tport.tportInventories.openHistory.element.line.cause", element.cause());
                Message plugin = null;
                if (element.application() != null) plugin = formatInfoTranslation(playerLang, "tport.tportInventories.openHistory.element.line.plugin", element.application());
                Message type = null;
                if (newLocationSource.getType() != null) type = formatInfoTranslation(playerLang, "tport.tportInventories.openHistory.element.line.type", newLocationSource.getType());
                
                Message teleportNew = formatInfoTranslation(playerLang, "tport.tportInventories.openHistory.element.line.new", LEFT);
                Message teleportNewInverted = formatInfoTranslation(playerLang, "tport.tportInventories.openHistory.element.line.new.inverted", SHIFT_LEFT, newLocationSource.getSafetyCheckState(player));
                Message teleportOld = formatInfoTranslation(playerLang, "tport.tportInventories.openHistory.element.line.old", RIGHT);
                Message teleportOldInverted = formatInfoTranslation(playerLang, "tport.tportInventories.openHistory.element.line.old.inverted", SHIFT_RIGHT, TPORT_BACK.getState(player));
                
                setCustomItemData(is, colorTheme, elementTitle, Arrays.asList(from, to, to2, cause, plugin, type, new Message(), teleportNew, teleportNewInverted, teleportOld, teleportOldInverted));
                
                ItemMeta im = is.getItemMeta();
                im.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "historyIndex"), PersistentDataType.INTEGER, index -1);
                
                addFunction(im, ((whoClicked, clickType, pdc, fancyInventory) -> {
                    ArrayList<HistoryElement> innerHistory = teleportHistory.getOrDefault(whoClicked.getUniqueId(), new ArrayList<>());
                    int innerIndex = pdc.get(new NamespacedKey(Main.getInstance(), "historyIndex"), PersistentDataType.INTEGER);
                    HistoryElement historyElement = innerHistory.get(innerIndex);
                    
                    com.spaceman.tport.commands.tport.history.Back.run(historyElement, whoClicked, clickType.isShiftClick());
                }), RIGHT, SHIFT_RIGHT);
                
                addFunction(im, ((whoClicked, clickType, pdc, fancyInventory) -> {
                    ArrayList<HistoryElement> innerHistory = teleportHistory.getOrDefault(whoClicked.getUniqueId(), new ArrayList<>());
                    int innerIndex = pdc.get(new NamespacedKey(Main.getInstance(), "historyIndex"), PersistentDataType.INTEGER);
                    HistoryElement historyElement = innerHistory.get(innerIndex);
                    
                    com.spaceman.tport.commands.tport.history.TmpName.run(historyElement, whoClicked, clickType.isShiftClick());
                }), LEFT, SHIFT_LEFT);
                is.setItemMeta(im);
                
                // todo on drop preview to old
                // todo on control+drop preview to new
                
                items.add(is);
            }
            
            items = items.reversed();
        } else {
            items = prevWindow.getData("historyItems", List.class);
        }
        
        Message title = formatInfoTranslation("tport.tportInventories.openHistory.title");
        FancyInventory inv = getDynamicScrollableInventory(player, page, TPortInventories::openHistory, title, items, createBack(player, MAIN, OWN, null));
        inv.setData("historyItems", items);
        inv.setData(historyFilterDataName, filter);
        
        ItemStack filterSelection = history_filter_model.getItem(player);
        Message filterTitle = filter == null ? formatInfoTranslation(playerLang, "tport.tportInventories.openHistory.filter.title") : formatInfoTranslation(playerLang, "tport.tportInventories.openHistory.filter.selection", filter);
        Message filterClick = formatInfoTranslation(playerLang, "tport.tportInventories.openHistory.filter.click", LEFT);
        setCustomItemData(filterSelection, colorTheme, filterTitle, List.of(new Message(), filterClick));
        addFunction(filterSelection, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
            openHistoryFilter(whoClicked, 0, fancyInventory);
        }));
        inv.setItem(inv.getSize() - 9, filterSelection);
        
        ItemStack clearHistory = history_clear_model.getItem(player);
        Message clearTitle = formatInfoTranslation(playerLang, "tport.tportInventories.openHistory.clearHistory.title");
        setCustomItemData(clearHistory, colorTheme, clearTitle, null);
        addCommand(clearHistory, LEFT, "tport history clear");
        addFunction(clearHistory, LEFT, (whoClicked, clickType, pdc, fancyInventory) -> openHistory(whoClicked));
        inv.setItem(0, clearHistory);
        
        inv.open(player);
    }
    public static void openHistory(Player player, int page, FancyInventory prevWindow) {
        openHistory(player, prevWindow.getData(historyFilterDataName), page, prevWindow);
    }
    
    private static void openHistoryFilter(Player player, int page, FancyInventory prevWindow) {
        ColorTheme colorTheme = getTheme(player);
        JsonObject playerLang = getPlayerLang(player);
        
        List<ItemStack> items = new ArrayList<>();
        
        ItemStack noFilter = history_filter_none_model.getItem(player);
        Message NoFilterTitle = formatInfoTranslation(playerLang, "tport.tportInventories.openHistory.filter.none.title");
        Message clickToSelect = formatInfoTranslation(playerLang, "tport.tportInventories.openHistoryFilter.filter.select", LEFT);
        setCustomItemData(noFilter, colorTheme, NoFilterTitle, List.of(new Message(), clickToSelect));
        addFunction(noFilter, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
            openHistory(whoClicked, null);
        }));
        items.add(noFilter);
        
        for (String filter : HistoryFilter.getFilters()) {
            ItemStack is;
            if (filter.startsWith(HistoryFilter.PLUGIN_PREFIX)) {
                String plugin = filter.substring(HistoryFilter.PLUGIN_PREFIX.length());
                is = TeleportHistory.pluginFilterModels.getOrDefault(plugin, history_filter_unknown_model).getItem(player);
            } else {
                is = (switch (filter) {
                    case "ENDER_PEARL" -> history_filter_ender_pearl_model;
                    case "COMMAND" -> history_filter_command_model;
                    case "PLUGIN" -> history_filter_plugin_model;
                    case "NETHER_PORTAL" -> history_filter_nether_portal_model;
                    case "END_PORTAL" -> history_filter_end_portal_model;
                    case "SPECTATE" -> history_filter_spectate_model;
                    case "END_GATEWAY" -> history_filter_end_gateway_model;
                    case "CHORUS_FRUIT" -> history_filter_chorus_fruit_model;
                    case "DISMOUNT" -> history_filter_dismount_model;
                    case "EXIT_BED" -> history_filter_exit_bed_model;
                    case "UNKNOWN" -> history_filter_unknown_model;
                    // in case a new cause gets made
                    default -> history_filter_unknown_model;
                }).getItem(player);
            }
            
            Message filterTitle = formatInfoTranslation(playerLang, "tport.tportInventories.openHistoryFilter.filter.title", filter);
            setCustomItemData(is, colorTheme, filterTitle, List.of(new Message(), clickToSelect));
            
            setStringData(is, new NamespacedKey(Main.getInstance(), "filter"), filter);
            addFunction(is, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
                String innerFilter = pdc.get(new NamespacedKey(Main.getInstance(), "filter"), PersistentDataType.STRING);
                openHistory(whoClicked, innerFilter);
            }));
            
            items.add(is);
        }
        
        Message title = formatInfoTranslation("tport.tportInventories.openHistoryFilter.title");
        FancyInventory inv = getDynamicScrollableInventory(player, page, TPortInventories::openHistoryFilter, title, items, createBack(player, HISTORY, OWN, MAIN));
        inv.setData(historyFilterDataName, prevWindow.getData(historyFilterDataName));
        
        inv.open(player);
    }
}
