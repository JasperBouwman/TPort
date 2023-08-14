package com.spaceman.tport.inventories;

import com.google.gson.JsonObject;
import com.spaceman.tport.Main;
import com.spaceman.tport.commands.TPortCommand;
import com.spaceman.tport.commands.tport.RemovePlayer;
import com.spaceman.tport.commands.tport.backup.Auto;
import com.spaceman.tport.commands.tport.backup.Load;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.MessageUtils;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fancyMessage.colorTheme.MultiColor;
import com.spaceman.tport.fancyMessage.inventories.FancyClickEvent;
import com.spaceman.tport.fancyMessage.inventories.FancyInventory;
import com.spaceman.tport.fancyMessage.inventories.InventoryModel;
import com.spaceman.tport.fancyMessage.inventories.KeyboardGUI;
import com.spaceman.tport.playerUUID.PlayerUUID;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.*;
import java.util.List;

import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatInfoTranslation;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;
import static com.spaceman.tport.fancyMessage.inventories.FancyClickEvent.*;
import static com.spaceman.tport.fancyMessage.inventories.FancyInventory.getDynamicScrollableInventory;
import static com.spaceman.tport.fancyMessage.inventories.KeyboardGUI.getKeyboardOutput;
import static com.spaceman.tport.fancyMessage.language.Language.getPlayerLang;
import static com.spaceman.tport.inventories.ItemFactory.BackType.*;
import static com.spaceman.tport.inventories.ItemFactory.createBack;
import static com.spaceman.tport.inventories.ItemFactory.getPlayerList;
import static org.bukkit.event.inventory.ClickType.*;
import static org.bukkit.event.inventory.ClickType.LEFT;
import static org.bukkit.persistence.PersistentDataType.STRING;

public class SettingsInventories {
    
    public static final InventoryModel settings_model                          = new InventoryModel(Material.OAK_BUTTON, QuickEditInventories.last_model_id + 1, "settings");
    public static final InventoryModel settings_version_model                  = new InventoryModel(Material.OAK_BUTTON, settings_model, "settings/version");
    public static final InventoryModel settings_reload_model                   = new InventoryModel(Material.OAK_BUTTON, settings_version_model, "settings/reload");
    public static final InventoryModel settings_log_model                      = new InventoryModel(Material.OAK_BUTTON, settings_reload_model, "settings/log");
    public static final InventoryModel settings_backup_model                   = new InventoryModel(Material.OAK_BUTTON, settings_log_model, "settings/backup");
    public static final InventoryModel settings_biome_tp_model                 = new InventoryModel(Material.OAK_BUTTON, settings_backup_model, "settings/biome_tp");
    public static final InventoryModel settings_delay_model                    = new InventoryModel(Material.OAK_BUTTON, settings_biome_tp_model, "settings/delay");
    public static final InventoryModel settings_restriction_model              = new InventoryModel(Material.OAK_BUTTON, settings_delay_model, "settings/restriction");
    public static final InventoryModel settings_dynmap_model                   = new InventoryModel(Material.OAK_BUTTON, settings_restriction_model, "settings/dynmap");
    public static final InventoryModel settings_particle_animation_model       = new InventoryModel(Material.OAK_BUTTON, settings_dynmap_model, "settings/particle_animation");
    public static final InventoryModel settings_pltp_model                     = new InventoryModel(Material.OAK_BUTTON, settings_particle_animation_model, "settings/pltp");
    public static final InventoryModel settings_public_model                   = new InventoryModel(Material.OAK_BUTTON, settings_pltp_model, "settings/public_tp");
    public static final InventoryModel settings_resource_pack_model            = new InventoryModel(Material.OAK_BUTTON, settings_public_model, "settings/resource_pack");
    public static final InventoryModel settings_tag_model                      = new InventoryModel(Material.OAK_BUTTON, settings_resource_pack_model, "settings/tag");
    public static final InventoryModel settings_transfer_model                 = new InventoryModel(Material.OAK_BUTTON, settings_tag_model, "settings/transfer");
    public static final InventoryModel settings_features_model                 = new InventoryModel(Material.OAK_BUTTON, settings_transfer_model, "settings/features");
    public static final InventoryModel settings_metrics_model                  = new InventoryModel(Material.OAK_BUTTON, settings_features_model, "settings/metrics");
    public static final InventoryModel settings_redirect_model                 = new InventoryModel(Material.OAK_BUTTON, settings_metrics_model, "settings/redirect");
    public static final InventoryModel settings_remove_player_model            = new InventoryModel(Material.OAK_BUTTON, settings_redirect_model, "settings/remove_player");
    public static final InventoryModel settings_remove_player_confirm_model    = new InventoryModel(Material.OAK_BUTTON, settings_remove_player_model, "settings/remove_player");
    public static final InventoryModel settings_remove_player_cancel_model     = new InventoryModel(Material.OAK_BUTTON, settings_remove_player_confirm_model, "settings/remove_player");
    public static final InventoryModel settings_safety_check_model             = new InventoryModel(Material.OAK_BUTTON, settings_remove_player_cancel_model, "settings/safety_check");
    public static final InventoryModel settings_language_model                 = new InventoryModel(Material.OAK_BUTTON, settings_safety_check_model, "settings/language");
    public static final InventoryModel settings_cooldown_model                 = new InventoryModel(Material.OAK_BUTTON, settings_language_model, "settings/cooldown");
    public static final InventoryModel settings_backup_state_true_model        = new InventoryModel(Material.OAK_BUTTON, settings_cooldown_model, "settings/backup");
    public static final InventoryModel settings_backup_state_false_model       = new InventoryModel(Material.OAK_BUTTON, settings_backup_state_true_model, "settings/backup");
    public static final InventoryModel settings_backup_count_model             = new InventoryModel(Material.OAK_BUTTON, settings_backup_state_false_model, "settings/backup");
    public static final InventoryModel settings_backup_load_model              = new InventoryModel(Material.OAK_BUTTON, settings_backup_count_model, "settings/backup");
    public static final InventoryModel settings_backup_save_model              = new InventoryModel(Material.OAK_BUTTON, settings_backup_load_model, "settings/backup");
    public static final InventoryModel settings_color_theme_model              = new InventoryModel(Material.OAK_BUTTON, settings_backup_save_model, "settings/color_theme");
    public static final InventoryModel settings_color_theme_info_model         = new InventoryModel(Material.OAK_BUTTON, settings_color_theme_model, "settings/color_theme");
    public static final InventoryModel settings_color_theme_success_model      = new InventoryModel(Material.OAK_BUTTON, settings_color_theme_info_model, "settings/color_theme");
    public static final InventoryModel settings_color_theme_error_model        = new InventoryModel(Material.OAK_BUTTON, settings_color_theme_success_model, "settings/color_theme");
    public static final InventoryModel settings_color_theme_good_model         = new InventoryModel(Material.OAK_BUTTON, settings_color_theme_error_model, "settings/color_theme");
    public static final InventoryModel settings_color_theme_bad_model          = new InventoryModel(Material.OAK_BUTTON, settings_color_theme_good_model, "settings/color_theme");
    public static final InventoryModel settings_color_theme_title_model        = new InventoryModel(Material.OAK_BUTTON, settings_color_theme_bad_model, "settings/color_theme");
    public static final InventoryModel settings_color_theme_select_model       = new InventoryModel(Material.OAK_BUTTON, settings_color_theme_title_model, "settings/color_theme");
    public static final InventoryModel settings_color_theme_red_add_model      = new InventoryModel(Material.OAK_BUTTON, settings_color_theme_select_model, "settings/color_theme");
    public static final InventoryModel settings_color_theme_red_remove_model   = new InventoryModel(Material.OAK_BUTTON, settings_color_theme_red_add_model, "settings/color_theme");
    public static final InventoryModel settings_color_theme_green_add_model    = new InventoryModel(Material.OAK_BUTTON, settings_color_theme_red_remove_model, "settings/color_theme");
    public static final InventoryModel settings_color_theme_green_remove_model = new InventoryModel(Material.OAK_BUTTON, settings_color_theme_green_add_model, "settings/color_theme");
    public static final InventoryModel settings_color_theme_blue_add_model     = new InventoryModel(Material.OAK_BUTTON, settings_color_theme_green_remove_model, "settings/color_theme");
    public static final InventoryModel settings_color_theme_blue_remove_model  = new InventoryModel(Material.OAK_BUTTON, settings_color_theme_blue_add_model, "settings/color_theme");
    public static final int last_model_id = settings_color_theme_blue_remove_model.getCustomModelData();
    
    public static void openTPortBackup_loadBackupGUI(Player player, int page, @Nullable FancyInventory prevWindow) {
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        
        ArrayList<ItemStack> items = new ArrayList<>();
        for (String backup : Load.getBackups(true)) {
            ItemStack item = settings_backup_load_model.getItem(player);
            Message title = formatInfoTranslation("tport.settingsInventories.openTPortBackup_load_backupGUI.backup", backup);
            title.translateMessage(playerLang);
            MessageUtils.setCustomItemData(item, colorTheme, title, null);
            addCommand(item, LEFT, "tport backup load " + backup);
            
            items.add(item);
        }
        
        FancyInventory inv = getDynamicScrollableInventory(player, page, SettingsInventories::openTPortBackup_loadBackupGUI,
                "tport.settingsInventories.openTPortBackup_load_backupGUI.title", items, createBack(player, BACKUP, OWN, MAIN));
        
        inv.open(player);
    }
    public static void openTPortBackupGUI(Player player) {
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        
        FancyInventory inv = new FancyInventory(27, formatInfoTranslation("tport.settingsInventories.openTPortBackupGUI.title"));
        
        int backupCount = Auto.getBackupCount();
        ItemStack autoCount = settings_backup_count_model.getItem(player);
        Message autoCountTitle = formatInfoTranslation("tport.settingsInventories.openTPortBackupGUI.autoCount.title", backupCount);
        Message autoCountLC = formatInfoTranslation("tport.settingsInventories.openTPortBackupGUI.autoCount.leftClick", LEFT);
        Message autoCountRC = formatInfoTranslation("tport.settingsInventories.openTPortBackupGUI.autoCount.rightClick", ClickType.RIGHT);
        autoCountTitle.translateMessage(playerLang);
        autoCountLC.translateMessage(playerLang);
        autoCountRC.translateMessage(playerLang);
        MessageUtils.setCustomItemData(autoCount, colorTheme, autoCountTitle, Arrays.asList(new Message(), autoCountLC, autoCountRC));
        addFunction(autoCount, LEFT, (whoClicked, clickType, pdc, fancyInventory) -> {
            TPortCommand.executeTPortCommand(whoClicked, "backup auto " + (Auto.getBackupCount() + 1));
            openTPortBackupGUI(whoClicked);
        });
        addFunction(autoCount, ClickType.RIGHT, (whoClicked, clickType, pdc, fancyInventory) -> {
            TPortCommand.executeTPortCommand(whoClicked, "backup auto " + (Auto.getBackupCount() - 1));
            openTPortBackupGUI(whoClicked);
        });
        
        boolean backupState = Auto.getBackupState();
        ItemStack backupStateItem = (backupState ? settings_backup_state_true_model : settings_backup_state_false_model).getItem(player);
        Message stateMessage;
        Message changeStateMessage;
        if (backupState) {
            stateMessage = formatTranslation(varInfoColor, varInfoColor, "tport.settingsInventories.openTPortBackupGUI.backupState.enabled");
            changeStateMessage = formatTranslation(varInfoColor, varInfoColor, "tport.settingsInventories.openTPortBackupGUI.backupState.disable");
        } else {
            stateMessage = formatTranslation(varInfoColor, varInfoColor, "tport.settingsInventories.openTPortBackupGUI.backupState.disabled");
            changeStateMessage = formatTranslation(varInfoColor, varInfoColor, "tport.settingsInventories.openTPortBackupGUI.backupState.enable");
        }
        stateMessage.translateMessage(playerLang);
        changeStateMessage.translateMessage(playerLang);
        Message stateTitle = formatInfoTranslation("tport.settingsInventories.openTPortBackupGUI.backupState.title", stateMessage);
        Message stateLore = formatInfoTranslation("tport.settingsInventories.openTPortBackupGUI.backupState.changeState", LEFT, changeStateMessage);
        stateTitle.translateMessage(playerLang);
        stateLore.translateMessage(playerLang);
        MessageUtils.setCustomItemData(backupStateItem, colorTheme, stateTitle, Arrays.asList(new Message(), stateLore));
        
        if (Auto.getBackupState()) {
            addFunction(backupStateItem, LEFT, (whoClicked, clickType, pdc, fancyInventory) -> {
                TPortCommand.executeTPortCommand(whoClicked, "backup auto false");
                openTPortBackupGUI(whoClicked);
            });
        } else {
            addFunction(backupStateItem, LEFT, (whoClicked, clickType, pdc, fancyInventory) -> {
                TPortCommand.executeTPortCommand(whoClicked, "backup auto true");
                openTPortBackupGUI(whoClicked);
            });
        }
        
        ItemStack save = settings_backup_save_model.getItem(player);
        addFunction(save, LEFT, (whoClicked, clickType, pdc, fancyInventory) -> {
            TPortCommand.executeTPortCommand(whoClicked, "backup save");
            openTPortBackupGUI(whoClicked);
        });
        addFunction(save, RIGHT, (whoClicked, clickType, pdc, fancyInventory) -> {
            FancyClickEvent.FancyClickRunnable onAccept = (whoClicked1, clickType1, pdc1, keyboardInventory) -> {
                String keyboardOutput = getKeyboardOutput(keyboardInventory);
                TPortCommand.executeTPortCommand(whoClicked1, "backup save " + keyboardOutput);
                openTPortBackupGUI(whoClicked1);
            };
            FancyClickEvent.FancyClickRunnable onReject = (whoClicked1, clickType1, pdc1, keyboardInventory) -> openTPortBackupGUI(whoClicked1);
            KeyboardGUI.openKeyboard(whoClicked, onAccept, onReject, KeyboardGUI.TEXT_ONLY);
        });
        Message saveTitle = formatInfoTranslation("tport.settingsInventories.openTPortBackupGUI.save");
        saveTitle.translateMessage(playerLang);
        Message saveLeft = formatInfoTranslation("tport.settingsInventories.openTPortBackupGUI.save.left", LEFT);
        saveLeft.translateMessage(playerLang);
        Message saveRight = formatInfoTranslation("tport.settingsInventories.openTPortBackupGUI.save.right", RIGHT);
        saveRight.translateMessage(playerLang);
        MessageUtils.setCustomItemData(save, colorTheme, saveTitle, Arrays.asList(new Message(), saveLeft, saveRight));
        
        ItemStack load = settings_backup_load_model.getItem(player);
        Message loadTitle = formatInfoTranslation("tport.settingsInventories.openTPortBackupGUI.load");
        loadTitle.translateMessage(playerLang);
        Message loadLeft = formatInfoTranslation("tport.settingsInventories.openTPortBackupGUI.load.left", LEFT);
        loadLeft.translateMessage(playerLang);
        MessageUtils.setCustomItemData(load, colorTheme, loadTitle, Arrays.asList(new Message(), loadLeft));
        addFunction(load, LEFT, (whoClicked, clickType, pdc, fancyInventory) -> openTPortBackup_loadBackupGUI(whoClicked, 0, null));
        
        ItemStack back = createBack(player, SETTINGS, MAIN, OWN);
        
        inv.setItem(10, save);
        inv.setItem(11, load);
        inv.setItem(13, backupStateItem);
        inv.setItem(14, autoCount);
        
        inv.setItem(17, back);
        
        inv.open(player);
    }
    
    @FunctionalInterface
    private interface ColorChanger {
        Color editColor(Color color);
    }
    private static void changeColorRunnable(Player whoClicked, PersistentDataContainer pdc, FancyInventory fancyInventory, ColorChanger colorChanger) {
        NamespacedKey key = new NamespacedKey(Main.getInstance(), "colorType_name");
        if (pdc.has(key, STRING)) {
            ColorTheme theme = fancyInventory.getData("colorTheme", ColorTheme.class);
            
            ColorTheme.ColorType type = ColorTheme.ColorType.valueOf(pdc.get(key, STRING));
            Color color = type.getColor(theme).getColor();
            color = colorChanger.editColor(color);
            type.setColor(theme, new MultiColor(color));
            
            openTPortColorTheme_editTypeGUI(whoClicked, fancyInventory.getData("colorTypes", java.util.List.class));
        }
    }
    public static void openTPortColorTheme_editTypeGUI(Player player, List<ColorTheme.ColorType> colorTypes) {
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        
        int windowSize = (colorTypes.size() + 2) * 9;
        
        FancyInventory inv = new FancyInventory(windowSize, formatInfoTranslation("tport.settingsInventories.openTPortColorTheme_editTypeGUI.title"));
        inv.setData("colorTypes", colorTypes);
        inv.setData("colorTheme", colorTheme);
        
        int indexOffset = 0;
        for (ColorTheme.ColorType colorType : colorTypes) {
            Message valueHEXMessage = formatInfoTranslation(playerLang, "tport.settingsInventories.openTPortColorTheme_editTypeGUI.colorTypes.value.hex", colorType.getColor(colorTheme).getColorAsValue());
            Color c = colorType.getColor(colorTheme).getColor();
            Message valueRGBMessage = formatInfoTranslation(playerLang, "tport.settingsInventories.openTPortColorTheme_editTypeGUI.colorTypes.value.rgb", c.getRed(), c.getGreen(), c.getBlue());
            Message valueAdd1      = formatInfoTranslation(playerLang, "tport.settingsInventories.openTPortColorTheme_editTypeGUI.value.add",    LEFT, 1);
            Message valueAdd10     = formatInfoTranslation(playerLang, "tport.settingsInventories.openTPortColorTheme_editTypeGUI.value.add",    RIGHT, 10);
            Message valueAdd25     = formatInfoTranslation(playerLang, "tport.settingsInventories.openTPortColorTheme_editTypeGUI.value.add",    SHIFT_LEFT, 25);
            Message valueAdd100    = formatInfoTranslation(playerLang, "tport.settingsInventories.openTPortColorTheme_editTypeGUI.value.add",    SHIFT_RIGHT, 100);
            Message valueRemove1   = formatInfoTranslation(playerLang, "tport.settingsInventories.openTPortColorTheme_editTypeGUI.value.remove", LEFT, 1);
            Message valueRemove10  = formatInfoTranslation(playerLang, "tport.settingsInventories.openTPortColorTheme_editTypeGUI.value.remove", RIGHT, 10);
            Message valueRemove25  = formatInfoTranslation(playerLang, "tport.settingsInventories.openTPortColorTheme_editTypeGUI.value.remove", SHIFT_LEFT, 25);
            Message valueRemove100 = formatInfoTranslation(playerLang, "tport.settingsInventories.openTPortColorTheme_editTypeGUI.value.remove", SHIFT_RIGHT, 100);
            
            ItemStack redAdd = settings_color_theme_red_add_model.getItem(player);
            Message redAddTitle = formatTranslation(colorType, colorType, "tport.settingsInventories.openTPortColorTheme_editTypeGUI.red.add");
            redAddTitle.translateMessage(playerLang);
            MessageUtils.setCustomItemData(redAdd, colorTheme, redAddTitle, Arrays.asList(valueHEXMessage, valueRGBMessage, new Message(), valueAdd1, valueAdd10, valueAdd25, valueAdd100));
            FancyClickEvent.setStringData(redAdd, new NamespacedKey(Main.getInstance(), "colorType_name"), colorType.name());
            addFunction(redAdd, LEFT, ((whoClicked, clickType, pdc, fancyInventory) ->
                    changeColorRunnable(whoClicked, pdc, fancyInventory, (color) -> new Color(Math.min(color.getRed() + 1, 255), color.getGreen(), color.getBlue()))));
            addFunction(redAdd, RIGHT, ((whoClicked, clickType, pdc, fancyInventory) ->
                    changeColorRunnable(whoClicked, pdc, fancyInventory, (color) -> new Color(Math.min(color.getRed() + 10, 255), color.getGreen(), color.getBlue()))));
            addFunction(redAdd, SHIFT_LEFT, ((whoClicked, clickType, pdc, fancyInventory) ->
                    changeColorRunnable(whoClicked, pdc, fancyInventory, (color) -> new Color(Math.min(color.getRed() + 25, 255), color.getGreen(), color.getBlue()))));
            addFunction(redAdd, SHIFT_RIGHT, ((whoClicked, clickType, pdc, fancyInventory) ->
                    changeColorRunnable(whoClicked, pdc, fancyInventory, (color) -> new Color(Math.min(color.getRed() + 100, 255), color.getGreen(), color.getBlue()))));
            
            ItemStack redRem = settings_color_theme_red_remove_model.getItem(player);
            Message redRemTitle = formatTranslation(colorType, colorType, "tport.settingsInventories.openTPortColorTheme_editTypeGUI.red.remove");
            redRemTitle.translateMessage(playerLang);
            MessageUtils.setCustomItemData(redRem, colorTheme, redRemTitle, Arrays.asList(valueHEXMessage, valueRGBMessage, new Message(), valueRemove1, valueRemove10, valueRemove25, valueRemove100));
            FancyClickEvent.setStringData(redRem, new NamespacedKey(Main.getInstance(), "colorType_name"), colorType.name());
            addFunction(redRem, LEFT, ((whoClicked, clickType, pdc, fancyInventory) ->
                    changeColorRunnable(whoClicked, pdc, fancyInventory, (color) -> new Color(Math.max(color.getRed() - 1, 0), color.getGreen(), color.getBlue()))));
            addFunction(redRem, RIGHT, ((whoClicked, clickType, pdc, fancyInventory) ->
                    changeColorRunnable(whoClicked, pdc, fancyInventory, (color) -> new Color(Math.max(color.getRed() - 10, 0), color.getGreen(), color.getBlue()))));
            addFunction(redRem, SHIFT_LEFT, ((whoClicked, clickType, pdc, fancyInventory) ->
                    changeColorRunnable(whoClicked, pdc, fancyInventory, (color) -> new Color(Math.max(color.getRed() - 25, 0), color.getGreen(), color.getBlue()))));
            addFunction(redRem, SHIFT_RIGHT, ((whoClicked, clickType, pdc, fancyInventory) ->
                    changeColorRunnable(whoClicked, pdc, fancyInventory, (color) -> new Color(Math.max(color.getRed() - 100, 0), color.getGreen(), color.getBlue()))));
            
            ItemStack greenAdd = settings_color_theme_green_add_model.getItem(player);
            Message greenAddTitle = formatTranslation(colorType, colorType, "tport.settingsInventories.openTPortColorTheme_editTypeGUI.green.add");
            greenAddTitle.translateMessage(playerLang);
            MessageUtils.setCustomItemData(greenAdd, colorTheme, greenAddTitle, Arrays.asList(valueHEXMessage, valueRGBMessage, new Message(), valueAdd1, valueAdd10, valueAdd25, valueAdd100));
            FancyClickEvent.setStringData(greenAdd, new NamespacedKey(Main.getInstance(), "colorType_name"), colorType.name());
            addFunction(greenAdd, LEFT, ((whoClicked, clickType, pdc, fancyInventory) ->
                    changeColorRunnable(whoClicked, pdc, fancyInventory, (color) -> new Color(color.getRed(), Math.min(color.getGreen() + 1, 255), color.getBlue()))));
            addFunction(greenAdd, RIGHT, ((whoClicked, clickType, pdc, fancyInventory) ->
                    changeColorRunnable(whoClicked, pdc, fancyInventory, (color) -> new Color(color.getRed(), Math.min(color.getGreen() + 10, 255), color.getBlue()))));
            addFunction(greenAdd, SHIFT_LEFT, ((whoClicked, clickType, pdc, fancyInventory) ->
                    changeColorRunnable(whoClicked, pdc, fancyInventory, (color) -> new Color(color.getRed(), Math.min(color.getGreen() + 25, 255), color.getBlue()))));
            addFunction(greenAdd, SHIFT_RIGHT, ((whoClicked, clickType, pdc, fancyInventory) ->
                    changeColorRunnable(whoClicked, pdc, fancyInventory, (color) -> new Color(color.getRed(), Math.min(color.getGreen() + 100, 255), color.getBlue()))));
            
            ItemStack greenRem = settings_color_theme_green_remove_model.getItem(player);
            Message greenRemTitle = formatTranslation(colorType, colorType, "tport.settingsInventories.openTPortColorTheme_editTypeGUI.green.remove");
            greenRemTitle.translateMessage(playerLang);
            MessageUtils.setCustomItemData(greenRem, colorTheme, greenRemTitle, Arrays.asList(valueHEXMessage, valueRGBMessage, new Message(), valueRemove1, valueRemove10, valueRemove25, valueRemove100));
            FancyClickEvent.setStringData(greenRem, new NamespacedKey(Main.getInstance(), "colorType_name"), colorType.name());
            addFunction(greenRem, LEFT, ((whoClicked, clickType, pdc, fancyInventory) ->
                    changeColorRunnable(whoClicked, pdc, fancyInventory, (color) -> new Color(color.getRed(), Math.max(color.getGreen() - 1, 0), color.getBlue()))));
            addFunction(greenRem, RIGHT, ((whoClicked, clickType, pdc, fancyInventory) ->
                    changeColorRunnable(whoClicked, pdc, fancyInventory, (color) -> new Color(color.getRed(), Math.max(color.getGreen() - 10, 0), color.getBlue()))));
            addFunction(greenRem, SHIFT_LEFT, ((whoClicked, clickType, pdc, fancyInventory) ->
                    changeColorRunnable(whoClicked, pdc, fancyInventory, (color) -> new Color(color.getRed(), Math.max(color.getGreen() - 25, 0), color.getBlue()))));
            addFunction(greenRem, SHIFT_RIGHT, ((whoClicked, clickType, pdc, fancyInventory) ->
                    changeColorRunnable(whoClicked, pdc, fancyInventory, (color) -> new Color(color.getRed(), Math.max(color.getGreen() - 100, 0), color.getBlue()))));
            
            ItemStack blueAdd = settings_color_theme_blue_add_model.getItem(player);
            Message blueAddTitle = formatTranslation(colorType, colorType, "tport.settingsInventories.openTPortColorTheme_editTypeGUI.blue.add");
            blueAddTitle.translateMessage(playerLang);
            MessageUtils.setCustomItemData(blueAdd, colorTheme, blueAddTitle, Arrays.asList(valueHEXMessage, valueRGBMessage, new Message(), valueAdd1, valueAdd10, valueAdd25, valueAdd100));
            FancyClickEvent.setStringData(blueAdd, new NamespacedKey(Main.getInstance(), "colorType_name"), colorType.name());
            addFunction(blueAdd, LEFT, ((whoClicked, clickType, pdc, fancyInventory) ->
                    changeColorRunnable(whoClicked, pdc, fancyInventory, (color) -> new Color(color.getRed(), color.getGreen(), Math.min(color.getBlue() + 1, 255)))));
            addFunction(blueAdd, RIGHT, ((whoClicked, clickType, pdc, fancyInventory) ->
                    changeColorRunnable(whoClicked, pdc, fancyInventory, (color) -> new Color(color.getRed(), color.getGreen(), Math.min(color.getBlue() + 10, 255)))));
            addFunction(blueAdd, SHIFT_LEFT, ((whoClicked, clickType, pdc, fancyInventory) ->
                    changeColorRunnable(whoClicked, pdc, fancyInventory, (color) -> new Color(color.getRed(), color.getGreen(), Math.min(color.getBlue() + 25, 255)))));
            addFunction(blueAdd, SHIFT_RIGHT, ((whoClicked, clickType, pdc, fancyInventory) ->
                    changeColorRunnable(whoClicked, pdc, fancyInventory, (color) -> new Color(color.getRed(), color.getGreen(), Math.min(color.getBlue() + 100, 255)))));
            
            ItemStack blueRem = settings_color_theme_blue_remove_model.getItem(player);
            Message blueRemTitle = formatTranslation(colorType, colorType, "tport.settingsInventories.openTPortColorTheme_editTypeGUI.blue.remove");
            blueRemTitle.translateMessage(playerLang);
            MessageUtils.setCustomItemData(blueRem, colorTheme, blueRemTitle, Arrays.asList(valueHEXMessage, valueRGBMessage, new Message(), valueRemove1, valueRemove10, valueRemove25, valueRemove100));
            FancyClickEvent.setStringData(blueRem, new NamespacedKey(Main.getInstance(), "colorType_name"), colorType.name());
            addFunction(blueRem, LEFT, ((whoClicked, clickType, pdc, fancyInventory) ->
                    changeColorRunnable(whoClicked, pdc, fancyInventory, (color) -> new Color(color.getRed(), color.getGreen(), Math.max(color.getBlue() - 1, 0)))));
            addFunction(blueRem, RIGHT, ((whoClicked, clickType, pdc, fancyInventory) ->
                    changeColorRunnable(whoClicked, pdc, fancyInventory, (color) -> new Color(color.getRed(), color.getGreen(), Math.max(color.getBlue() - 10, 0)))));
            addFunction(blueRem, SHIFT_LEFT, ((whoClicked, clickType, pdc, fancyInventory) ->
                    changeColorRunnable(whoClicked, pdc, fancyInventory, (color) -> new Color(color.getRed(), color.getGreen(), Math.max(color.getBlue() - 25, 0)))));
            addFunction(blueRem, SHIFT_RIGHT, ((whoClicked, clickType, pdc, fancyInventory) ->
                    changeColorRunnable(whoClicked, pdc, fancyInventory, (color) -> new Color(color.getRed(), color.getGreen(), Math.max(color.getBlue() - 100, 0)))));
            
            
            ItemStack masterColor = (switch (colorType) {
                case infoColor, successColor, errorColor ->             settings_color_theme_info_model;
                case varInfoColor, varSuccessColor, varErrorColor ->    settings_color_theme_success_model;
                case varInfo2Color, varSuccess2Color, varError2Color -> settings_color_theme_error_model;
                case goodColor ->                                       settings_color_theme_good_model;
                case badColor ->                                        settings_color_theme_bad_model;
                case titleColor ->                                      settings_color_theme_title_model;
            }).getItem(player);
            Message masterColorTitle = formatTranslation(colorType, colorType, "tport.settingsInventories.openTPortColorTheme_editTypeGUI.colorTypes." + colorType);
            masterColorTitle.translateMessage(playerLang);
            MessageUtils.setCustomItemData(masterColor, colorTheme, masterColorTitle, Arrays.asList(valueHEXMessage, valueRGBMessage));
            
            inv.setItem(10 + indexOffset, masterColor);
            inv.setItem(11 + indexOffset, redAdd);
            inv.setItem(12 + indexOffset, redRem);
            inv.setItem(13 + indexOffset, greenAdd);
            inv.setItem(14 + indexOffset, greenRem);
            inv.setItem(15 + indexOffset, blueAdd);
            inv.setItem(16 + indexOffset, blueRem);
            
            indexOffset += 9;
        }
        
        ItemStack backItem = createBack(player, COLOR_THEME, OWN, MAIN);
        inv.setItem(inv.getSize() - 1, backItem);
        
        inv.open(player);
    }
    public static void openTPortColorTheme_selectTheme(Player player, int page, FancyInventory prevWindow) {
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        
        ArrayList<ItemStack> items = new ArrayList<>();
        
        for (Map.Entry<String, ColorTheme> themeEntry : ColorTheme.getDefaultThemesMap().entrySet()) {
            ItemStack themeItem = settings_color_theme_model.getItem(player);
            
            addCommand(themeItem, LEFT, "tport colorTheme set " + themeEntry.getKey());
            addFunction(themeItem, LEFT, ((whoClicked, clickType, pdc, fancyInventory) ->
                    openTPortColorTheme_selectTheme(whoClicked, fancyInventory.getData("page", Integer.class), fancyInventory)));
            
            Message itemTitle = formatInfoTranslation("tport.settingsInventories.openTPortColorTheme_selectTheme.theme.name", themeEntry.getKey());
            itemTitle.translateMessage(playerLang);
            
            Message infoMessage = formatTranslation(varInfoColor, varInfoColor, "tport.settingsInventories.openTPortColorTheme_selectTheme.theme.info");
            infoMessage.translateMessage(playerLang);
            Message infoList = formatInfoTranslation("tport.settingsInventories.openTPortColorTheme_selectTheme.theme.info.list", infoMessage);
            infoList.translateMessage(playerLang);
            Message infoTheme = formatInfoTranslation("tport.settingsInventories.openTPortColorTheme_selectTheme.theme.info.theme", infoMessage);
            infoTheme.translateMessage(playerLang);
            Message infoArray = new Message();
            boolean color = true;
            for (String s : Arrays.asList("TPort", "BiomeTP", "FeatureTP", player.getName())) {
                infoArray.addMessage(formatTranslation(infoColor, (color ? varInfoColor : varInfo2Color), "%s", s));
                infoArray.addText(textComponent(", ", infoColor));
                color = !color;
            }
            infoArray.removeLast();
            
            Message successMessage = formatTranslation(varSuccessColor, varSuccessColor, "tport.settingsInventories.openTPortColorTheme_selectTheme.theme.success");
            successMessage.translateMessage(playerLang);
            Message successList = formatSuccessTranslation("tport.settingsInventories.openTPortColorTheme_selectTheme.theme.success.list", successMessage);
            successList.translateMessage(playerLang);
            Message successTheme = formatSuccessTranslation("tport.settingsInventories.openTPortColorTheme_selectTheme.theme.success.theme", successMessage);
            successTheme.translateMessage(playerLang);
            Message successArray = new Message();
            color = true;
            for (String s : Arrays.asList("TPort", "BiomeTP", "FeatureTP", player.getName())) {
                successArray.addMessage(formatTranslation(successColor, (color ? varSuccessColor : varSuccess2Color), "%s", s));
                successArray.addText(textComponent(", ", successColor));
                color = !color;
            }
            successArray.removeLast();
            
            Message errorMessage = formatTranslation(varErrorColor, varErrorColor, "tport.settingsInventories.openTPortColorTheme_selectTheme.theme.error");
            errorMessage.translateMessage(playerLang);
            Message errorList = formatErrorTranslation("tport.settingsInventories.openTPortColorTheme_selectTheme.theme.error.list", errorMessage);
            errorList.translateMessage(playerLang);
            Message errorTheme = formatErrorTranslation("tport.settingsInventories.openTPortColorTheme_selectTheme.theme.error.theme", errorMessage);
            errorTheme.translateMessage(playerLang);
            Message errorArray = new Message();
            color = true;
            for (String s : Arrays.asList("TPort", "BiomeTP", "FeatureTP", player.getName())) {
                errorArray.addMessage(formatTranslation(errorColor, (color ? varErrorColor : varError2Color), "%s", s));
                errorArray.addText(textComponent(", ", errorColor));
                color = !color;
            }
            errorArray.removeLast();
            
            MessageUtils.setCustomItemData(themeItem, themeEntry.getValue(), itemTitle,
                    Arrays.asList(
                            new Message(), infoTheme, infoList, infoArray,
                            new Message(), successTheme, successList, successArray,
                            new Message(), errorTheme, errorList, errorArray)
            );
            
            
            items.add(themeItem);
        }
        
        FancyInventory inv = getDynamicScrollableInventory(player, page, SettingsInventories::openTPortColorTheme_selectTheme,
                "tport.settingsInventories.openTPortColorTheme_selectTheme.title", items,
                createBack(player, COLOR_THEME, OWN, MAIN));
        inv.open(player);
    }
    public static void openTPortColorThemeGUI(Player player) {
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        
        FancyInventory inv = new FancyInventory(27, formatInfoTranslation("tport.settingsInventories.openTPortColorThemeGUI.title"));
        
        ItemStack info = settings_color_theme_info_model.getItem(player);
        Message infoMessage = formatTranslation(varInfoColor, varInfoColor, "tport.settingsInventories.openTPortColorThemeGUI.info");
        infoMessage.translateMessage(playerLang);
        Message infoList = formatInfoTranslation("tport.settingsInventories.openTPortColorThemeGUI.info.list", infoMessage);
        infoList.translateMessage(playerLang);
        Message infoTheme = formatInfoTranslation("tport.settingsInventories.openTPortColorThemeGUI.info.theme", infoMessage);
        infoTheme.translateMessage(playerLang);
        Message infoArray = new Message();
        boolean color = true;
        for (String s : Arrays.asList("TPort", "BiomeTP", "FeatureTP", player.getName())) {
            infoArray.addMessage(formatTranslation(infoColor, (color ? varInfoColor : varInfo2Color), "%s", s));
            infoArray.addText(textComponent(", ", infoColor));
            color = !color;
        }
        infoArray.removeLast();
        Message clickToEditInfo = formatInfoTranslation("tport.settingsInventories.openTPortColorThemeGUI.info.clickToEdit", LEFT);
        clickToEditInfo.translateMessage(playerLang);
        MessageUtils.setCustomItemData(info, colorTheme, infoTheme, Arrays.asList(infoList, infoArray, new Message(), clickToEditInfo));
        addFunction(info, LEFT, (whoClicked, clickType, pdc, fancyInventory) -> openTPortColorTheme_editTypeGUI(whoClicked, Arrays.asList(infoColor, varInfoColor, varInfo2Color)));
        inv.setItem(10, info);
        
        ItemStack success = settings_color_theme_success_model.getItem(player);
        Message successMessage = formatTranslation(varSuccessColor, varSuccessColor, "tport.settingsInventories.openTPortColorThemeGUI.success");
        successMessage.translateMessage(playerLang);
        Message successList = formatSuccessTranslation("tport.settingsInventories.openTPortColorThemeGUI.success.list", successMessage);
        successList.translateMessage(playerLang);
        Message successTheme = formatSuccessTranslation("tport.settingsInventories.openTPortColorThemeGUI.success.theme", successMessage);
        successTheme.translateMessage(playerLang);
        Message successArray = new Message();
        color = true;
        for (String s : Arrays.asList("TPort", "BiomeTP", "FeatureTP", player.getName())) {
            successArray.addMessage(formatTranslation(successColor, (color ? varSuccessColor : varSuccess2Color), "%s", s));
            successArray.addText(textComponent(", ", successColor));
            color = !color;
        }
        successArray.removeLast();
        Message clickToEditSuccess = formatSuccessTranslation("tport.settingsInventories.openTPortColorThemeGUI.success.clickToEdit", LEFT);
        clickToEditSuccess.translateMessage(playerLang);
        MessageUtils.setCustomItemData(success, colorTheme, successTheme, Arrays.asList(successList, successArray, new Message(), clickToEditSuccess));
        addFunction(success, LEFT, (whoClicked, clickType, pdc, fancyInventory) -> openTPortColorTheme_editTypeGUI(whoClicked, Arrays.asList(successColor, varSuccessColor, varSuccess2Color)));
        inv.setItem(11, success);
        
        ItemStack error = settings_color_theme_error_model.getItem(player);
        Message errorMessage = formatTranslation(varErrorColor, varErrorColor, "tport.settingsInventories.openTPortColorThemeGUI.error");
        errorMessage.translateMessage(playerLang);
        Message errorList = formatErrorTranslation("tport.settingsInventories.openTPortColorThemeGUI.error.list", errorMessage);
        errorList.translateMessage(playerLang);
        Message errorTheme = formatErrorTranslation("tport.settingsInventories.openTPortColorThemeGUI.error.theme", errorMessage);
        errorTheme.translateMessage(playerLang);
        Message errorArray = new Message();
        color = true;
        for (String s : Arrays.asList("TPort", "BiomeTP", "FeatureTP", player.getName())) {
            errorArray.addMessage(formatTranslation(errorColor, (color ? varErrorColor : varError2Color), "%s", s));
            errorArray.addText(textComponent(", ", errorColor));
            color = !color;
        }
        errorArray.removeLast();
        Message clickToEditError = formatErrorTranslation("tport.settingsInventories.openTPortColorThemeGUI.error.clickToEdit", LEFT);
        clickToEditError.translateMessage(playerLang);
        MessageUtils.setCustomItemData(error, colorTheme, errorTheme, Arrays.asList(errorList, errorArray, new Message(), clickToEditError));
        addFunction(error, LEFT, (whoClicked, clickType, pdc, fancyInventory) -> openTPortColorTheme_editTypeGUI(whoClicked, Arrays.asList(errorColor, varErrorColor, varError2Color)));
        inv.setItem(12, error);
        
        ItemStack good = settings_color_theme_good_model.getItem(player);
        Message goodMessage = formatTranslation(varInfoColor, varInfoColor, "tport.settingsInventories.openTPortColorThemeGUI.good");
        goodMessage.translateMessage(playerLang);
        Message goodTheme = formatTranslation(goodColor, goodColor, "tport.settingsInventories.openTPortColorThemeGUI.good.theme", goodMessage);
        goodTheme.translateMessage(playerLang);
        Message clickToEditGood = formatTranslation(goodColor, varInfoColor, "tport.settingsInventories.openTPortColorThemeGUI.good.clickToEdit", LEFT);
        clickToEditGood.translateMessage(playerLang);
        MessageUtils.setCustomItemData(good, colorTheme, goodTheme, Arrays.asList(new Message(), clickToEditGood));
        addFunction(good, LEFT, (whoClicked, clickType, pdc, fancyInventory) -> openTPortColorTheme_editTypeGUI(whoClicked, Arrays.asList(goodColor, badColor, titleColor)));
        inv.setItem(13, good);
        
        ItemStack bad = settings_color_theme_bad_model.getItem(player);
        Message badMessage = formatTranslation(varInfoColor, varInfoColor, "tport.settingsInventories.openTPortColorThemeGUI.bad");
        badMessage.translateMessage(playerLang);
        Message badTheme = formatTranslation(badColor, badColor, "tport.settingsInventories.openTPortColorThemeGUI.bad.theme", badMessage);
        badTheme.translateMessage(playerLang);
        Message clickToEditBad = formatTranslation(badColor, varInfoColor, "tport.settingsInventories.openTPortColorThemeGUI.bad.clickToEdit", LEFT);
        clickToEditBad.translateMessage(playerLang);
        MessageUtils.setCustomItemData(bad, colorTheme, badTheme, Arrays.asList(new Message(), clickToEditBad));
        addFunction(bad, LEFT, (whoClicked, clickType, pdc, fancyInventory) -> openTPortColorTheme_editTypeGUI(whoClicked, Arrays.asList(goodColor, badColor, titleColor)));
        inv.setItem(14, bad);
        
        ItemStack title = settings_color_theme_title_model.getItem(player);
        Message titleMessage = formatTranslation(varInfoColor, varInfoColor, "tport.settingsInventories.openTPortColorThemeGUI.title.title");
        titleMessage.translateMessage(playerLang);
        Message titleTheme = formatTranslation(titleColor, varInfoColor, "tport.settingsInventories.openTPortColorThemeGUI.title.theme", titleMessage);
        titleTheme.translateMessage(playerLang);
        Message clickToEditTitle = formatTranslation(titleColor, varInfoColor, "tport.settingsInventories.openTPortColorThemeGUI.title.clickToEdit", LEFT);
        clickToEditTitle.translateMessage(playerLang);
        MessageUtils.setCustomItemData(title, colorTheme, titleTheme, Arrays.asList(new Message(), clickToEditTitle));
        addFunction(title, LEFT, (whoClicked, clickType, pdc, fancyInventory) -> openTPortColorTheme_editTypeGUI(whoClicked, Arrays.asList(goodColor, badColor, titleColor)));
        inv.setItem(15, title);
        
        ItemStack selectFromList = settings_color_theme_select_model.getItem(player);
        Message selectFromListTitle = formatInfoTranslation("tport.settingsInventories.openTPortColorThemeGUI.selectFromList.title", LEFT);
        selectFromListTitle.translateMessage(playerLang);
        MessageUtils.setCustomItemData(selectFromList, colorTheme, selectFromListTitle, null);
        addFunction(selectFromList, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> openTPortColorTheme_selectTheme(whoClicked, 0, null)));
        inv.setItem(16, selectFromList);
        
        ItemStack back = createBack(player, SETTINGS, OWN, MAIN);
        inv.setItem(26, back);
        
        inv.open(player);
    }
    
    private static void openRemovePlayerGUI(Player player, int page, @Nullable FancyInventory prevWindow) {
        FancyInventory inv = getDynamicScrollableInventory(player,
                page,
                SettingsInventories::openRemovePlayerGUI,
                "tport.settingsInventories.openRemovePlayerGUI.title",
                getPlayerList(player, false, true, List.of(ItemFactory.HeadAttributes.REMOVE_PLAYER), List.of()),
                createBack(player, SETTINGS, OWN, MAIN));
        
        inv.open(player);
    }
    public static void openRemovePlayerConfirmationGUI(Player player, UUID toRemove) {
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        
        FancyInventory inv = new FancyInventory(27, formatInfoTranslation(playerLang, "tport.settingsInventories.openRemovePlayerConfirmationGUI.title", asPlayer(toRemove)));
        inv.setData("toRemoveUUID", toRemove);
        
        ItemStack cancel = settings_remove_player_cancel_model.getItem(player);
        addFunction(cancel, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> openRemovePlayerGUI(whoClicked, 0, null)));
        Message cancelTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openRemovePlayerConfirmationGUI.cancel.title", LEFT, asPlayer((toRemove)));
        MessageUtils.setCustomItemData(cancel, colorTheme, cancelTitle, null);
        inv.setItem(11, cancel);
        
        ItemStack head = ItemFactory.getHead(toRemove, player, ItemFactory.HeadAttributes.CONFIRM_REMOVE_PLAYER);
        inv.setItem(13, head);
        
        ItemStack confirm = settings_remove_player_confirm_model.getItem(player);
        addFunction(confirm, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
            UUID uuid = fancyInventory.getData("toRemoveUUID", UUID.class);
            String name = PlayerUUID.getPlayerName(uuid);
            if (name == null) name = uuid.toString();
            RemovePlayer.removePlayer(whoClicked, name);
            whoClicked.closeInventory();
        }));
        Message confirmTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openRemovePlayerConfirmationGUI.confirm.title", LEFT, asPlayer((toRemove)));
        MessageUtils.setCustomItemData(confirm, colorTheme, confirmTitle, null);
        inv.setItem(15, confirm);
        
        inv.open(player);
    }
    
    public static void openSettingsGUI(Player player, int page, @Nullable FancyInventory prevWindow) {
        ColorTheme colorTheme = getTheme(player);
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        
        ArrayList<ItemStack> items = new ArrayList<>();
        
        ItemStack versionItem = settings_version_model.getItem(player);
        addCommand(versionItem, LEFT, "tport version");
        Message versionTitle = formatInfoTranslation("tport.settingsInventories.openSettingsGUI.version.title");
        versionTitle.translateMessage(playerLang);
        MessageUtils.setCustomItemData(versionItem, colorTheme, versionTitle, null);
        items.add(versionItem);
        
        ItemStack reloadItem = settings_reload_model.getItem(player);
        addCommand(reloadItem, LEFT, "tport reload");
        Message reloadTitle = formatInfoTranslation("tport.settingsInventories.openSettingsGUI.reload.title");
        reloadTitle.translateMessage(playerLang);
        MessageUtils.setCustomItemData(reloadItem, colorTheme, reloadTitle, null);
        items.add(reloadItem);
        
        ItemStack logItem = settings_log_model.getItem(player);
        Message logTitle = formatInfoTranslation("tport.settingsInventories.openSettingsGUI.log.title");
        logTitle.translateMessage(playerLang);
        MessageUtils.setCustomItemData(logItem, colorTheme, logTitle, null);
        items.add(logItem);
        //todo create log setting
        /*
        * /tport log timeZone [timeZone]
        * /tport log timeFormat [timeFormat]
        * /tport log logData
        * /tport log logData <TPort name> //todo check
        * /tport log logSize [size]
        * */
        
        ItemStack backupItem = settings_backup_model.getItem(player);
        addFunction(backupItem, LEFT, (whoClicked, clickType, pdc, fancyInventory) -> openTPortBackupGUI(whoClicked));
        Message backupTitle = formatInfoTranslation("tport.settingsInventories.openSettingsGUI.backup.title");
        backupTitle.translateMessage(playerLang);
        MessageUtils.setCustomItemData(backupItem, colorTheme, backupTitle, null);
        items.add(backupItem);
        
        ItemStack biomeTPItem = settings_biome_tp_model.getItem(player);
        Message biomeTPTitle = formatInfoTranslation("tport.settingsInventories.openSettingsGUI.biomeTP.title");
        biomeTPTitle.translateMessage(playerLang);
        MessageUtils.setCustomItemData(biomeTPItem, colorTheme, biomeTPTitle, null);
        items.add(biomeTPItem);
        //todo create biomeTP setting
        /*
        * /tport biomeTP accuracy [size] //todo maybe set per player
        *
        * /tport features biomeTP state [state]
        * */
        
        ItemStack delayItem = settings_delay_model.getItem(player);
        Message delayTitle = formatInfoTranslation("tport.settingsInventories.openSettingsGUI.delay.title");
        delayTitle.translateMessage(playerLang);
        MessageUtils.setCustomItemData(delayItem, colorTheme, delayTitle, null);
        items.add(delayItem);
        //todo create delay setting
        /*
        * /tport delay handler [state]
        * /tport delay set <player> <delay>
        * /tport delay get [player]
        * */
        
        ItemStack restrictionItem = settings_restriction_model.getItem(player);
        Message restrictionTitle = formatInfoTranslation("tport.settingsInventories.openSettingsGUI.restriction.title");
        restrictionTitle.translateMessage(playerLang);
        MessageUtils.setCustomItemData(restrictionItem, colorTheme, restrictionTitle, null);
        items.add(restrictionItem);
        /*
         * /tport restriction handler [state]
         * /tport restriction set <player> <delay>
         * /tport restriction get [player]
         * */
        
        ItemStack dynmapItem = settings_dynmap_model.getItem(player);
        Message dynmapTitle = formatInfoTranslation("tport.settingsInventories.openSettingsGUI.dynmap.title");
        dynmapTitle.translateMessage(playerLang);
        MessageUtils.setCustomItemData(dynmapItem, colorTheme, dynmapTitle, null);
        items.add(dynmapItem);
        //todo create dynmap setting
        /*
        * /tport dynmap [IP]
        *
        * /tport features dynmap state [state]
        * */
        
        ItemStack particleAnimationItem = settings_particle_animation_model.getItem(player);
        Message particleAnimationTitle = formatInfoTranslation("tport.settingsInventories.openSettingsGUI.particleAnimation.title");
        particleAnimationTitle.translateMessage(playerLang);
        MessageUtils.setCustomItemData(particleAnimationItem, colorTheme, particleAnimationTitle, null);
        items.add(particleAnimationItem);
        //todo create particle animation setting
        /*
        * /tport particleAnimation new set <particleAnimation> [data...]
        * /tport particleAnimation new edit <data...>
        * /tport particleAnimation new test
        * /tport particleAnimation new enable [state]
        * /tport particleAnimation old set <particleAnimation> [data...]
        * /tport particleAnimation old edit <data...>
        * /tport particleAnimation old test
        * /tport particleAnimation old enable [state]
        * /tport particleAnimation list
        *
        * /tport features particleAnimation state [state]
        * */
        
        ItemStack pltpItem = settings_pltp_model.getItem(player);
        Message pltpTitle = formatInfoTranslation("tport.settingsInventories.openSettingsGUI.PLTP.title");
        pltpTitle.translateMessage(playerLang);
        MessageUtils.setCustomItemData(pltpItem, colorTheme, pltpTitle, null);
        items.add(pltpItem);
        //todo create PLTP setting
        /*
        * /tport PLTP state [state]
        * /tport PLTP consent [state]
        * /tport PLTP whitelist add <player...>
        * /tport PLTP whitelist remove <player...>
        * /tport PLTP whitelist list
        * /tport PLTP offset <offset>
        * /tport PLTP preview <state>
        *
        * /tport features PLTP state [state]
        * */
        
        ItemStack publicItem = settings_public_model.getItem(player);
        Message publicTitle = formatInfoTranslation("tport.settingsInventories.openSettingsGUI.public.title");
        publicTitle.translateMessage(playerLang);
        MessageUtils.setCustomItemData(publicItem, colorTheme, publicTitle, null);
        items.add(publicItem);
        //todo create publicTP setting
        /*
        * /tport public listSize [size]
        * /tport public reset
        * /tport public move -> open publicTP GUI with added message on how to move TPorts
        *
        * /tport features PublicTP state [state]
        * */
        
        ItemStack resourcePackItem = settings_resource_pack_model.getItem(player);
        Message resourcePackTitle = formatInfoTranslation("tport.settingsInventories.openSettingsGUI.resourcePack.title");
        resourcePackTitle.translateMessage(playerLang);
        MessageUtils.setCustomItemData(resourcePackItem, colorTheme, resourcePackTitle, null);
        items.add(resourcePackItem);
        //todo create resource pack setting
        /*
        * /tport resourcePack state [state]
        * /tport resourcePack resolution [resolution]
        * */
        
        ItemStack tagItem = settings_tag_model.getItem(player);
        Message tagTitle = formatInfoTranslation("tport.settingsInventories.openSettingsGUI.tag.title");
        tagTitle.translateMessage(playerLang);
        MessageUtils.setCustomItemData(tagItem, colorTheme, tagTitle, null);
        items.add(tagItem);
        //todo create tag setting
        /*
        * /tport tag create <tag>
        * /tport tag delete <tag>
        * /tport tag list
        * /tport tag reset
        * */
        
        ItemStack transferItem = settings_transfer_model.getItem(player);
        Message transferTitle = formatInfoTranslation("tport.settingsInventories.openSettingsGUI.transfer.title");
        transferTitle.translateMessage(playerLang);
        MessageUtils.setCustomItemData(transferItem, colorTheme, transferTitle, null);
        items.add(transferItem);
        //todo create transfer setting
        /*
         * /tport transfer offer -> open Own TPort GUI with added message on how to do (add in QuickEdit)
         * /tport transfer accept
         * /tport transfer reject
         * /tport transfer revoke
         * /tport transfer list
         * */
        
        ItemStack featuresItem = settings_features_model.getItem(player);
        Message featuresTitle = formatInfoTranslation("tport.settingsInventories.openSettingsGUI.features.title");
        featuresTitle.translateMessage(playerLang);
        MessageUtils.setCustomItemData(featuresItem, colorTheme, featuresTitle, null);
        items.add(featuresItem);
        //todo create features setting
        /*
        * /tport features
        * */
        
        ItemStack metricsItem = settings_metrics_model.getItem(player);
        Message metricsTitle = formatInfoTranslation("tport.settingsInventories.openSettingsGUI.metrics.title");
        metricsTitle.translateMessage(playerLang);
        MessageUtils.setCustomItemData(metricsItem, colorTheme, metricsTitle, null);
        items.add(metricsItem);
        //todo create metrics setting
        /*
        * /tport metrics viewStats
        * /tport features metrics state [state]
        * */
        
        ItemStack redirectItem = settings_redirect_model.getItem(player);
        Message redirectTitle = formatInfoTranslation("tport.settingsInventories.openSettingsGUI.redirect.title");
        redirectTitle.translateMessage(playerLang);
        MessageUtils.setCustomItemData(redirectItem, colorTheme, redirectTitle, null);
        items.add(redirectItem);
        //todo create redirect setting
        /*
        * /tport redirect (use dynamic scroll GUI with all options)
        * /tport features metrics state [state]
        * */
        
        ItemStack removePlayerItem = settings_remove_player_model.getItem(player);
        Message removePlayerTitle = formatInfoTranslation("tport.settingsInventories.openSettingsGUI.removePlayer.title");
        removePlayerTitle.translateMessage(playerLang);
        MessageUtils.setCustomItemData(removePlayerItem, colorTheme, removePlayerTitle, null);
        addFunction(removePlayerItem, LEFT, (whoClicked, clickType, pdc, fancyInventory) -> openRemovePlayerGUI(whoClicked, 0, null));
        items.add(removePlayerItem);
        //todo create remove player setting
        /*
        * /tport removePlayer <player>
        * */
        
        ItemStack safetyCheckItem = settings_safety_check_model.getItem(player);
        Message safetyCheckTitle = formatInfoTranslation("tport.settingsInventories.openSettingsGUI.safetyCheck.title");
        safetyCheckTitle.translateMessage(playerLang);
        MessageUtils.setCustomItemData(safetyCheckItem, colorTheme, safetyCheckTitle, null);
        items.add(safetyCheckItem);
        //todo create safety check setting
        /*
        * /tport safetyCheck check
        * use dynamic scroll GUI with all sources
        * */
        
        ItemStack languageItem = settings_language_model.getItem(player);
        Message languageTitle = formatInfoTranslation("tport.settingsInventories.openSettingsGUI.language.title");
        languageTitle.translateMessage(playerLang);
        MessageUtils.setCustomItemData(languageItem, colorTheme, languageTitle, null);
        items.add(languageItem);
        //todo create language setting
        /*
        * /tport language server [server]
        * /tport language get
        * /tport language set <custom|server|language>
        * /tport language repair <language> [repair with]
        * */
        
        ItemStack colorThemeItem = settings_color_theme_model.getItem(player);
        Message colorThemeTitle = formatInfoTranslation("tport.settingsInventories.openSettingsGUI.colorTheme.title");
        addFunction(colorThemeItem, LEFT, (whoClicked, clickType, pdc, fancyInventory) -> openTPortColorThemeGUI(whoClicked));
        colorThemeTitle.translateMessage(playerLang);
        MessageUtils.setCustomItemData(colorThemeItem, colorTheme, colorThemeTitle, null);
        items.add(colorThemeItem);
        
        ItemStack cooldownItem = settings_cooldown_model.getItem(player);
        Message cooldownTitle = formatInfoTranslation("tport.settingsInventories.openSettingsGUI.cooldown.title");
        cooldownTitle.translateMessage(playerLang);
        MessageUtils.setCustomItemData(cooldownItem, colorTheme, cooldownTitle, null);
        items.add(cooldownItem);
        //todo create cooldown setting
        /*
        * use dynamic scroll GUI with cooldown
        * */
        
        FancyInventory inv = getDynamicScrollableInventory(player, page,
                SettingsInventories::openSettingsGUI,
                "tport.settingsInventories.openSettingsGUI.title",
                items, createBack(player, MAIN, OWN, null));
        
        inv.open(player);
        
        //tport version                                                     DONE
        //tport reload                                                      DONE
        //tport log timeZone
        //tport log timeFormat
        //tport log logSize
        //tport backup
        //tport biomeTP accuracy [accuracy]
        //tport delay
        //tport restriction
        //tport dynmap ip
        //tport particleAnimation
        //tport PLTP consent
        //tport PLTP offset
        //tport PLTP preview
        //tport PLTP state
        //tport PLTP whitelist
        //tport public listSize
        //tport resourcePack resolution
        //tport resourcePack state
        //tport tag create
        //tport tag delete
        //tport tag reset
        //tport transfer accept
        //tport transfer offer
        //tport transfer reject
        //tport transfer revoke
        //tport features
        //tport metrics
        //tport redirect
        //tport removePlayer
        //tport safetyCheck
        //tport language
        //tport colorTheme                                                  DONE
        //tport cooldown
        
        
        //tport dynmap search <player>              - add to PLTP                                           DONE
        //tport dynmap search <player> <TPort>      - add ???                                               DONE
        //tport public                              - add quickEdit (add, remove toggle)                    DONE
        //tport requests accept
        //tport requests reject
        //tport requests revoke
        //tport teleporter                          - add ???
        //tport cancel
        //tport restore
        //tport search
        
        //quick edit:
        // - when typing is available:
        //    tport edit <TPort> description           - add to quick edit                              DONE
        //    tport edit <TPort> name                  - add to quick edit                              DONE
        // - tport edit <TPort> item                   - add to quick edit with player inv to select
        // - tport log                                 - add with new GUI to quick edit
        // - tport edit <TPort> dynmap icon            - add GUI with icon selection                    DONE
        
        inv.open(player);
    }
}
