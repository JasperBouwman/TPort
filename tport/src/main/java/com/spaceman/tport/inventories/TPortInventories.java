package com.spaceman.tport.inventories;

import com.google.gson.JsonObject;
import com.spaceman.tport.Glow;
import com.spaceman.tport.Main;
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
import com.spaceman.tport.fancyMessage.inventories.FancyClickEvent;
import com.spaceman.tport.fancyMessage.inventories.FancyInventory;
import com.spaceman.tport.fancyMessage.inventories.InventoryModel;
import com.spaceman.tport.fancyMessage.inventories.keyboard.KeyboardGUI;
import com.spaceman.tport.fancyMessage.language.Language;
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
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static com.spaceman.tport.commands.tport.Back.getPrevLocName;
import static com.spaceman.tport.commands.tport.SafetyCheck.SafetyCheckSource.TPORT_BACK;
import static com.spaceman.tport.commands.tport.SafetyCheck.SafetyCheckSource.TPORT_HOME;
import static com.spaceman.tport.fancyMessage.MessageUtils.setCustomItemData;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.inventories.FancyClickEvent.addCommand;
import static com.spaceman.tport.fancyMessage.inventories.FancyClickEvent.addFunction;
import static com.spaceman.tport.fancyMessage.inventories.FancyInventory.getDynamicScrollableInventory;
import static com.spaceman.tport.fancyMessage.inventories.FancyInventory.pageDataName;
import static com.spaceman.tport.fancyMessage.language.Language.getPlayerLang;
import static com.spaceman.tport.fileHander.Files.tportData;
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
    
    public static final InventoryModel back_model                              = new InventoryModel(Material.BARRIER, KeyboardGUI.last_model_id + 1, "navigation");
    public static final InventoryModel extra_tp_model                          = new InventoryModel(Material.OAK_BUTTON, back_model, "navigation");
    public static final InventoryModel extra_tp_grayed_model                   = new InventoryModel(Material.OAK_BUTTON, extra_tp_model, "navigation");
    public static final InventoryModel main_layout_model                       = new InventoryModel(Material.OAK_BUTTON, extra_tp_grayed_model, "main_layout");
    public static final InventoryModel main_layout_grayed_model                = new InventoryModel(Material.OAK_BUTTON, main_layout_model, "main_layout");
    public static final InventoryModel biome_tp_model                          = new InventoryModel(Material.OAK_BUTTON, main_layout_grayed_model, "biome_tp");
    public static final InventoryModel biome_tp_grayed_model                   = new InventoryModel(Material.OAK_BUTTON, biome_tp_model, "biome_tp");
    public static final InventoryModel biome_tp_clear_model                    = new InventoryModel(Material.OAK_BUTTON, biome_tp_grayed_model, "biome_tp");
    public static final InventoryModel biome_tp_clear_grayed_model             = new InventoryModel(Material.OAK_BUTTON, biome_tp_clear_model, "biome_tp");
    public static final InventoryModel biome_tp_run_model                      = new InventoryModel(Material.OAK_BUTTON, biome_tp_clear_grayed_model, "biome_tp");
    public static final InventoryModel biome_tp_run_grayed_model               = new InventoryModel(Material.OAK_BUTTON, biome_tp_run_model, "biome_tp");
    public static final InventoryModel biome_tp_presets_model                  = new InventoryModel(Material.OAK_BUTTON, biome_tp_run_grayed_model, "biome_tp");
    public static final InventoryModel biome_tp_presets_grayed_model           = new InventoryModel(Material.OAK_BUTTON, biome_tp_presets_model, "biome_tp");
    public static final InventoryModel biome_tp_random_tp_model                = new InventoryModel(Material.ELYTRA,     biome_tp_presets_grayed_model, "biome_tp");
    public static final InventoryModel biome_tp_random_tp_grayed_model         = new InventoryModel(Material.ELYTRA,     biome_tp_random_tp_model, "biome_tp");
    public static final InventoryModel feature_tp_model                        = new InventoryModel(Material.OAK_BUTTON, biome_tp_random_tp_grayed_model, "feature_tp");
    public static final InventoryModel feature_tp_grayed_model                 = new InventoryModel(Material.OAK_BUTTON, feature_tp_model, "feature_tp");
    public static final InventoryModel feature_tp_clear_model                  = new InventoryModel(Material.OAK_BUTTON, feature_tp_grayed_model, "feature_tp");
    public static final InventoryModel feature_tp_clear_grayed_model           = new InventoryModel(Material.OAK_BUTTON, feature_tp_clear_model, "feature_tp");
    public static final InventoryModel feature_tp_run_model                    = new InventoryModel(Material.OAK_BUTTON, feature_tp_clear_grayed_model, "feature_tp");
    public static final InventoryModel feature_tp_run_grayed_model             = new InventoryModel(Material.OAK_BUTTON, feature_tp_run_model, "feature_tp");
    public static final InventoryModel back_tp_model                           = new InventoryModel(Material.OAK_BUTTON, feature_tp_run_grayed_model, "back_tp");
    public static final InventoryModel back_tp_grayed_model                    = new InventoryModel(Material.OAK_BUTTON, back_tp_model, "back_tp");
    public static final InventoryModel public_tp_model                         = new InventoryModel(Material.OAK_BUTTON, back_tp_grayed_model, "public_tp");
    public static final InventoryModel public_tp_grayed_model                  = new InventoryModel(Material.OAK_BUTTON, public_tp_model, "public_tp");
    public static final InventoryModel public_tp_filter_own_model              = new InventoryModel(Material.OAK_BUTTON, public_tp_grayed_model, "public_tp");
    public static final InventoryModel public_tp_filter_all_model              = new InventoryModel(Material.OAK_BUTTON, public_tp_filter_own_model, "public_tp");
    public static final InventoryModel sorting_model                           = new InventoryModel(Material.OAK_BUTTON, public_tp_filter_all_model, "sorting");
    public static final InventoryModel sorting_grayed_model                    = new InventoryModel(Material.OAK_BUTTON, sorting_model, "sorting");
    public static final InventoryModel search_data_model                       = new InventoryModel(Material.OAK_BUTTON, sorting_grayed_model, "search");
    public static final InventoryModel home_model                              = new InventoryModel(Material.OAK_BUTTON, search_data_model, "home");
    public static final InventoryModel home_grayed_model                       = new InventoryModel(Material.OAK_BUTTON, home_model, "home");
    public static final InventoryModel world_tp_model                          = new InventoryModel(Material.OAK_BUTTON, home_grayed_model, "world_tp");
    public static final InventoryModel world_tp_grayed_model                   = new InventoryModel(Material.OAK_BUTTON, world_tp_model, "world_tp");
    public static final InventoryModel overworld_model                         = new InventoryModel(Material.STONE,      world_tp_grayed_model, "world_tp");
    public static final InventoryModel nether_model                            = new InventoryModel(Material.NETHERRACK, overworld_model, "world_tp");
    public static final InventoryModel the_end_model                           = new InventoryModel(Material.END_STONE,  nether_model, "world_tp");
    public static final InventoryModel other_environments_model                = new InventoryModel(Material.GLOWSTONE,  the_end_model, "world_tp");
    public static final int last_model_id = other_environments_model.getCustomModelData();
    
    public static void openMainTPortGUI(Player player) {
        openMainTPortGUI(player, 0, null);
    }
    public static void openMainTPortGUI(Player player, int page, FancyInventory prevWindow) {
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        ColorTheme theme = ColorTheme.getTheme(player);
        
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
        
        InventoryModel biomeTP_model = (Features.Feature.BiomeTP.isEnabled() ? biome_tp_model : biome_tp_grayed_model);
        ItemStack biomeTP = biomeTP_model.getItem(player);
        Message biomeTPTitle = formatTranslation(titleColor, titleColor, "tport.tportInventories.openMainGUI.biomeTP.title", "BiomeTP");
        Message biomeTPLeft = formatInfoTranslation("tport.tportInventories.openMainGUI.biomeTP.leftClick", LEFT);
        Message biomeTPRight = formatInfoTranslation("tport.tportInventories.openMainGUI.biomeTP.rightClick", RIGHT);
        Message biomeTPShiftRight = formatInfoTranslation("tport.tportInventories.openMainGUI.biomeTP.shiftRightClick", SHIFT_RIGHT);
        biomeTPTitle.translateMessage(playerLang);
        biomeTPLeft.translateMessage(playerLang);
        biomeTPRight.translateMessage(playerLang);
        biomeTPShiftRight.translateMessage(playerLang);
        MessageUtils.setCustomItemData(biomeTP, theme, biomeTPTitle, Arrays.asList(biomeTPLeft, biomeTPRight, biomeTPShiftRight));
        addCommand(biomeTP, LEFT, "tport biomeTP");
        addCommand(biomeTP, ClickType.RIGHT, "tport biomeTP preset");
        addCommand(biomeTP, ClickType.SHIFT_RIGHT, "tport biomeTP random");
        
        InventoryModel featureTP_model = Features.Feature.FeatureTP.isEnabled() ? feature_tp_model : feature_tp_grayed_model;
        ItemStack featureTP = featureTP_model.getItem(player);
        Message featureTPTitle = formatTranslation(titleColor, titleColor, "tport.tportInventories.openMainGUI.featureTP.title", "FeatureTP");
        featureTPTitle.translateMessage(playerLang);
        MessageUtils.setCustomItemData(featureTP, theme, featureTPTitle, null);
        addCommand(featureTP, LEFT, "tport featureTP");
        
        InventoryModel worldTP_model = Features.Feature.WorldTP.isEnabled() ? world_tp_model : world_tp_grayed_model;
        ItemStack worldTP = worldTP_model.getItem(player);
        Message worldTPTitle = formatTranslation(titleColor, titleColor, "tport.tportInventories.openMainGUI.worldTP.title", "WorldTP");
        worldTPTitle.translateMessage(playerLang);
        MessageUtils.setCustomItemData(worldTP, theme, worldTPTitle, null);
        addCommand(worldTP, LEFT, "tport world");
        
        InventoryModel backTP_model = Back.hasBack(player) ? back_tp_model : back_tp_grayed_model;
        ItemStack backTP = backTP_model.getItem(player);
        Message backTPTitle = formatTranslation(titleColor, titleColor, "tport.tportInventories.openMainGUI.backTP.title", "BackTP");
        Message backTPLore = Back.getPrevLocName(player);
        Message invertedBackTPLore = (backSafetyState == null ? null : formatInfoTranslation("tport.tportInventories.openMainGUI.backTP.inverted", SHIFT_LEFT, !backSafetyState));
        backTPTitle.translateMessage(playerLang);
        backTPLore.translateMessage(playerLang);
        invertedBackTPLore = MessageUtils.translateMessage(invertedBackTPLore, playerLang);
        MessageUtils.setCustomItemData(backTP, theme, backTPTitle, Arrays.asList(backTPLore, invertedBackTPLore));
        addCommand(backTP, LEFT, "tport back");
        if (backSafetyState != null) addCommand(backTP, ClickType.SHIFT_LEFT, "tport back " + !backSafetyState);
        
        InventoryModel home_modelData = Home.hasHome(player, true) ? home_model : home_grayed_model;
        ItemStack home = home_modelData.getItem(player);
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
        MessageUtils.setCustomItemData(home, theme, homeTitle, homeLore);
        addCommand(home, LEFT, "tport home");
        if (homeSafetyState != null) addCommand(home, ClickType.SHIFT_LEFT, "tport home " + !homeSafetyState);
        addFunction(home, RIGHT, ((whoClicked, clickType, pdc, fancyInventory) -> openHomeEditGUI(whoClicked)));
        
        InventoryModel publicTP_model = Features.Feature.PublicTP.isEnabled() ? public_tp_model : public_tp_grayed_model;
        ItemStack publicTP = publicTP_model.getItem(player);
        Message publicTPTitle = formatTranslation(titleColor, titleColor, "tport.tportInventories.openMainGUI.publicTP.title", "PublicTP");
        publicTPTitle.translateMessage(playerLang);
        FancyClickEvent.addCommand(publicTP, ClickType.LEFT, "tport public");
        MessageUtils.setCustomItemData(publicTP, theme, publicTPTitle, null);
        
        ItemStack settings = settings_model.getItem(player);
        Message settingsTitle = formatTranslation(titleColor, titleColor, "tport.tportInventories.openMainGUI.settings.title");
        settingsTitle.translateMessage(playerLang);
        MessageUtils.setCustomItemData(settings, theme, settingsTitle, null);
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
        MessageUtils.setCustomItemData(mainLayout, theme, mainLayoutTitle, layoutLore);
        addCommand(mainLayout, LEFT, "tport mainLayout tports " + !MainLayout.showTPorts(player), "tport");
        addCommand(mainLayout, ClickType.RIGHT, "tport mainLayout players " + !MainLayout.showPlayers(player), "tport");
        
        ItemStack sorting = ItemFactory.getSortingItem(player, playerLang, theme, ((whoClicked, clickType, pdc, fancyInventory) -> openMainTPortGUI(whoClicked)));
        
        inv.setItem(2, biomeTP);
        inv.setItem(4, featureTP);
        inv.setItem(6, worldTP);
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
        
        ColorTheme theme = ColorTheme.getTheme(player);
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
            if (backSafetyState != null) extraTPLore.add(formatInfoTranslation("tport.tportInventories.openTPortGUI.extraTP.format.shift_leftClick", ClickType.SHIFT_LEFT, !backSafetyState));
            extraTPLore.add(new Message());
        }
        if (Features.Feature.BiomeTP.isEnabled()) {
            extraTPLore.add(formatInfoTranslation("tport.tportInventories.openTPortGUI.extraTP.format.rightClick", ClickType.RIGHT));
            extraTPLore.add(new Message(textComponent("BiomeTP", varInfoColor)));
            extraTPLore.add(new Message());
        }
        if (Features.Feature.FeatureTP.isEnabled()) {
            extraTPLore.add(formatInfoTranslation("tport.tportInventories.openTPortGUI.extraTP.format.shiftRightClick", ClickType.SHIFT_RIGHT));
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
        MessageUtils.setCustomItemData(extraTP, theme, extraTPTitle, extraTPLore);
        addCommand(extraTP, LEFT, "tport back");
        if (backSafetyState != null) addCommand(extraTP, ClickType.SHIFT_LEFT, "tport back " + !backSafetyState);
        addCommand(extraTP, ClickType.RIGHT, "tport biomeTP");
        addCommand(extraTP, ClickType.SHIFT_RIGHT, "tport featureTP");
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
                    ClickType.RIGHT, !pltpConsent));
            warpLore.add(formatInfoTranslation("tport.tportInventories.openTPortGUI.playerHead.own.format.whenShiftLeftClick",
                    ClickType.SHIFT_LEFT, previewState.getNext().name()));
            warpLore.add(formatInfoTranslation("tport.tportInventories.openTPortGUI.playerHead.own.format.whenShiftRightClick",
                    ClickType.SHIFT_RIGHT, pltpOffset.getNext().name()));
            warpLore.add(formatInfoTranslation("tport.tportInventories.openTPortGUI.playerHead.own.format.whenDrop",
                    ClickType.DROP));
            
            warpTitle.translateMessage(playerLang);
            warpLore = MessageUtils.translateMessage(warpLore, playerLang);
            MessageUtils.setCustomItemData(warp, theme, warpTitle, warpLore);
            
            addCommand(warp, LEFT, "tport PLTP state " + !pltpState, "tport own");
            addCommand(warp, ClickType.RIGHT, "tport PLTP consent " + !pltpConsent, "tport own");
            addCommand(warp, ClickType.SHIFT_LEFT, "tport PLTP preview " + previewState.getNext(), "tport own");
            addCommand(warp, ClickType.SHIFT_RIGHT, "tport PLTP offset " + pltpOffset.getNext(), "tport own");
            FancyClickEvent.addFunction(warp, ClickType.DROP,
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
            
            MessageUtils.setCustomItemData(warp, theme, warpTitle, lore);
            addCommand(warp, LEFT, "tport PLTP tp " + newPlayerName);
            addCommand(warp, ClickType.RIGHT, "tport preview " + newPlayerName);
            
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
                        
                        TPort toMoveTPort = TPortManager.getTPort(ownerUUID, toMoveUUID);
                        Message emptyTitle = formatInfoTranslation("tport.tportInventories.openTPortGUI.quickEditReplacement", ClickType.RIGHT, toMoveTPort);
                        Message emptyLoreTPort = formatInfoTranslation("tport.tportInventories.openTPortGUI.quickEditReplacement.tport", ClickType.RIGHT);
                        Message emptyLoreCancel = formatInfoTranslation("tport.tportInventories.openTPortGUI.quickEditReplacement.cancel", ClickType.RIGHT, toMoveTPort);
                        emptyTitle.translateMessage(playerLang);
                        emptyLoreTPort.translateMessage(playerLang);
                        emptyLoreCancel.translateMessage(playerLang);
                        MessageUtils.setCustomItemData(empty, theme, emptyTitle, Arrays.asList(emptyLoreTPort, emptyLoreCancel));
                        
                        ItemMeta emptyMeta = empty.getItemMeta();
                        emptyMeta.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "TPortNameToMove"), PersistentDataType.STRING, toMoveTPort.getName());
                        emptyMeta.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "TPortSlot"), PersistentDataType.STRING, String.valueOf(i + 1));
                        
                        FancyClickEvent.addFunction(emptyMeta, ClickType.RIGHT, (whoClicked, clickType, pdc, fancyInventory) -> {
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
        ColorTheme theme = ColorTheme.getTheme(player);
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
            Message biomeRClick = formatInfoTranslation(playerLang, "tport.tportInventories.openBiomeTP.biome.RClick", ClickType.RIGHT);
            Message biomeSRClick = formatInfoTranslation(playerLang, "tport.tportInventories.openBiomeTP.biome.shift_RClick", ClickType.SHIFT_RIGHT);
            MessageUtils.setCustomItemData(item, theme, biomeTitle, Arrays.asList(biomeLClick, biomeRClick, biomeSRClick));
            
            ItemMeta im = item.getItemMeta();
            im.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "biome"), PersistentDataType.STRING, biome);
            if (selected) Glow.addGlow(im);
            
            FancyClickEvent.addFunction(im, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
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
            addCommand(im, ClickType.RIGHT, "tport biomeTP whitelist " + biome);
            addCommand(im, ClickType.SHIFT_RIGHT, "tport biomeTP blacklist " + biome);
            
            item.setItemMeta(im);
            
            if (selected) list.add(0, item);
            else list.add(item);
        }
        
        ItemStack random = (Random.getInstance().hasPermissionToRun(player, false) ? biome_tp_random_tp_model : biome_tp_random_tp_grayed_model).getItem(player);
        Message randomTitle = formatTranslation(titleColor, titleColor, "tport.tportInventories.openBiomeTP.randomTP.title");
        randomTitle.translateMessage(playerLang);
        MessageUtils.setCustomItemData(random, theme, randomTitle, null);
        addCommand(random, LEFT, "tport biomeTP random");
        list.add(0, random);
        
        ItemStack presets = (Preset.getInstance().hasPermissionToRun(player, false) ? biome_tp_presets_model : biome_tp_presets_grayed_model).getItem(player);
        Message presetsTitle = formatTranslation(titleColor, titleColor, "tport.tportInventories.openBiomeTP.presets.title");
        presetsTitle.translateMessage(playerLang);
        MessageUtils.setCustomItemData(presets, theme, presetsTitle, null);
        FancyClickEvent.addFunction(presets, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
            if (Preset.getInstance().hasPermissionToRun(whoClicked, true))
                openBiomeTPPreset(whoClicked, 0, fancyInventory);
        }));
        
        ItemStack clearSelected = (biomeSelection.isEmpty() ? biome_tp_clear_grayed_model : biome_tp_clear_model).getItem(player);
        Message modeTitle = formatInfoTranslation("tport.tportInventories.openBiomeTP.clearSelected.title", LEFT);
        modeTitle.translateMessage(playerLang);
        MessageUtils.setCustomItemData(clearSelected, theme, modeTitle, null);
        FancyClickEvent.addFunction(clearSelected, LEFT, (whoClicked, clickType, pdc, fancyInventory) -> {
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
        Message runBlacklist = formatInfoTranslation("tport.tportInventories.openBiomeTP.run.blacklist", ClickType.RIGHT);
        runBlacklist.translateMessage(playerLang);
        Message runCurrentMode = formatInfoTranslation("tport.tportInventories.openBiomeTP.run.currentMode", biomeTPMode.name());
        runCurrentMode.translateMessage(playerLang);
        Message runChangeMode = formatInfoTranslation("tport.tportInventories.openBiomeTP.run.changeMode", ClickType.SHIFT_RIGHT, biomeTPMode.getNext());
        runChangeMode.translateMessage(playerLang);
        MessageUtils.setCustomItemData(run, theme, runTitle, Arrays.asList(runWhitelist, runBlacklist, new Message(), runCurrentMode, runChangeMode));
        if (!biomeSelection.isEmpty()) {
            addCommand(run, LEFT, "tport biomeTP whitelist " + String.join(" ", biomeSelection));
            addCommand(run, ClickType.RIGHT, "tport biomeTP blacklist " + String.join(" ", biomeSelection));
        }
        addCommand(run, ClickType.SHIFT_RIGHT, "tport biomeTP mode " + biomeTPMode.getNext().name());
        addFunction(run, SHIFT_RIGHT, ((whoClicked, clickType, pdc, fancyInventory) -> openBiomeTP(whoClicked, fancyInventory.getData(pageDataName), fancyInventory)));
        
        FancyInventory inv = getDynamicScrollableInventory(player, page, TPortInventories::openBiomeTP, "tport.tportInventories.openBiomeTP.title", list, createBack(player, MAIN, OWN, FEATURE_TP, BIOME_TP), 45);
        inv.setData(biomeSelectionDataName, biomeSelection);
        
        inv.setItem(9, clearSelected);
        inv.setItem(27, presets);
        inv.setItem(18, run);
        
        inv.open(player);
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
        ColorTheme theme = ColorTheme.getTheme(player);
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
        Message runMode2 = formatInfoTranslation("tport.tportInventories.openFeatureTP.run.mode", ClickType.RIGHT, featureTPMode.getNext());
        runMode2.translateMessage(playerLang);
        Message runCurrentMode = formatInfoTranslation("tport.tportInventories.openFeatureTP.run.currentMode", featureTPMode);
        runCurrentMode.translateMessage(playerLang);
        Message runChangeMode = formatInfoTranslation("tport.tportInventories.openFeatureTP.run.changeMode", ClickType.SHIFT_RIGHT, featureTPMode.getNext());
        runChangeMode.translateMessage(playerLang);
        MessageUtils.setCustomItemData(run, theme, runTitle, Arrays.asList(runMode1, runMode2, new Message(), runCurrentMode, runChangeMode));
        if (!featureSelection.isEmpty()) {
            addCommand(run, LEFT, "tport featureTP search " + featureTPMode + " " + String.join(" ", featureSelection));
            addCommand(run, ClickType.RIGHT, "tport featureTP search " + featureTPMode.getNext() + " " + String.join(" ", featureSelection));
        }
        addCommand(run, ClickType.SHIFT_RIGHT, "tport featureTP mode " + featureTPMode.getNext().name(), "tport featureTP");
        
        ItemStack clearSelected = (featureSelection.isEmpty() ? feature_tp_clear_grayed_model : feature_tp_clear_model).getItem(player);
        Message clearSelectedTitle = formatInfoTranslation("tport.tportInventories.openFeatureTP.clearSelected.title", LEFT);
        clearSelectedTitle.translateMessage(playerLang);
        MessageUtils.setCustomItemData(clearSelected, theme, clearSelectedTitle, null);
        FancyClickEvent.addFunction(clearSelected, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
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
    }
    
    public static void openWorldTP(Player player) {
        openWorldTP(player, 0, null);
    }
    public static void openWorldTP(Player player, int page, @Nullable FancyInventory prevWindow) {
        ColorTheme theme = ColorTheme.getTheme(player);
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        
        List<ItemStack> worlds = new ArrayList<>();
        for (World world : Bukkit.getWorlds()) {
            ItemStack is = (switch (world.getEnvironment()) {
                case NORMAL -> overworld_model;
                case NETHER -> nether_model;
                case THE_END -> the_end_model;
                default -> other_environments_model;
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
            
            MessageUtils.setCustomItemData(is, theme, title, lore);
            addCommand(is, LEFT, "tport world " + world.getName());
            
            worlds.add(is);
        }
        
        FancyInventory inv = getDynamicScrollableInventory(player, page, TPortInventories::openWorldTP, "tport.tportInventories.openWorldTP.title",
                worlds, createBack(player, MAIN, OWN, PUBLIC, WORLD_TP));
        
        inv.open(player);
    }
    
    public static void openPublicTPortGUI(Player player) {
        openPublicTPortGUI(player, 0, null);
    }
    public static void openPublicTPortGUI(Player player, int page, @Nullable FancyInventory prevWindow) {
        //public.tports.<publicTPortSlot>.<TPortID>
        
        JsonObject playerLang = Language.getPlayerLang(player);
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        
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
    
    public static void openSearchGUI(Player player, int page, SearchMode searchMode, String searcherName, @Nonnull String query) {
        SearchType searcher = Search.Searchers.getSearcher(searcherName);
        if (searcher == null) {
            sendErrorTranslation(player, "tport.tportInventories.openSearchGUI.searchData.couldNotFindSearcher", searcherName);
            return;
        }
        if (!searcher.hasPermission(player, true)) {
            return;
        }
        List<ItemStack> searched = searcher.search(searchMode, query, player);
        CooldownManager.Search.update(player);
        
        openSearchGUI(player, page, searched, query, searcherName, searchMode, true);
    }
    private static void openSearchGUI(Player player, int page, FancyInventory fancyInventory) {
        openSearchGUI(player, page,
                fancyInventory.getData("content", List.class),
                fancyInventory.getData("query", String.class),
                fancyInventory.getData("searcher", String.class),
                fancyInventory.getData("searchMode", SearchMode.class),
                false);
    }
    private static void openSearchGUI(Player player, int page, List<ItemStack> searched, String query, String searcher, SearchMode searchMode, boolean updateCooldown) {
        FancyInventory inv = getDynamicScrollableInventory(player, page, TPortInventories::openSearchGUI, "tport.tportInventories.openSearchGUI.title",
                searched, createBack(player, SEARCH, OWN, MAIN));
        
        inv.setData("content", searched);
        inv.setData("query", query);
        inv.setData("searcher", searcher);
        inv.setData("searchMode", searchMode);
        
        ColorTheme theme = ColorTheme.getTheme(player);
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        
        ItemStack searchData = search_data_model.getItem(player);
        Message title = formatTranslation(titleColor, titleColor, "tport.tportInventories.openSearchGUI.searchData.title");
        title.translateMessage(playerLang);
        Message searchTypeLore = formatInfoTranslation("tport.tportInventories.openSearchGUI.searchData.type", searcher);
        searchTypeLore.translateMessage(playerLang);
        Message searchQueryLore = query.isEmpty() ? null : formatInfoTranslation("tport.tportInventories.openSearchGUI.searchData.query", query);
        if (!query.isEmpty()) searchQueryLore.translateMessage(playerLang);
        Message searchModeLore = searchMode == null ? null : formatInfoTranslation("tport.tportInventories.openSearchGUI.searchData.mode", searchMode);
        if (searchMode != null) searchModeLore.translateMessage(playerLang);
        MessageUtils.setCustomItemData(searchData, theme, title, Arrays.asList(searchTypeLore, searchQueryLore, searchModeLore));
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
        
        inv.setItem(inv.getSize() / 18 * 9, ItemFactory.getSortingItem(player, getPlayerLang(player.getUniqueId()), ColorTheme.getTheme(player), ((whoClicked, clickType, pdc, fancyInventory) -> openHomeEditGUI(whoClicked))));
        
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
    
}
