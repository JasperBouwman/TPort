package com.spaceman.tport.inventories;

import com.google.common.collect.Iterables;
import com.google.gson.JsonObject;
import com.spaceman.tport.Glow;
import com.spaceman.tport.Main;
import com.spaceman.tport.Pair;
import com.spaceman.tport.commands.TPortCommand;
import com.spaceman.tport.commands.tport.*;
import com.spaceman.tport.commands.tport.backup.Auto;
import com.spaceman.tport.commands.tport.backup.Load;
import com.spaceman.tport.commands.tport.log.LogSize;
import com.spaceman.tport.commands.tport.log.TimeFormat;
import com.spaceman.tport.commands.tport.pltp.Consent;
import com.spaceman.tport.commands.tport.pltp.Offset;
import com.spaceman.tport.commands.tport.pltp.Preview;
import com.spaceman.tport.commands.tport.pltp.State;
import com.spaceman.tport.commands.tport.publc.ListSize;
import com.spaceman.tport.commands.tport.resourcePack.ResolutionCommand;
import com.spaceman.tport.cooldown.CooldownManager;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.MessageUtils;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fancyMessage.colorTheme.MultiColor;
import com.spaceman.tport.fancyMessage.encapsulation.BiomeEncapsulation;
import com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation;
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
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.List;
import java.util.*;

import static com.spaceman.tport.commands.tport.log.TimeFormat.defaultTimeFormat;
import static com.spaceman.tport.fancyMessage.MessageUtils.setCustomItemData;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.encapsulation.PlayerEncapsulation.asPlayer;
import static com.spaceman.tport.fancyMessage.encapsulation.TPortEncapsulation.asTPort;
import static com.spaceman.tport.fancyMessage.events.ClickEvent.openUrl;
import static com.spaceman.tport.fancyMessage.events.HoverEvent.hoverEvent;
import static com.spaceman.tport.fancyMessage.inventories.FancyClickEvent.*;
import static com.spaceman.tport.fancyMessage.inventories.FancyInventory.getDynamicScrollableInventory;
import static com.spaceman.tport.fancyMessage.inventories.FancyInventory.pageDataName;
import static com.spaceman.tport.fancyMessage.inventories.keyboard.KeyboardGUI.*;
import static com.spaceman.tport.fancyMessage.language.Language.getPlayerLang;
import static com.spaceman.tport.fileHander.Files.tportData;
import static com.spaceman.tport.inventories.ItemFactory.BackType.*;
import static com.spaceman.tport.inventories.ItemFactory.HeadAttributes.PLTP_WHITELIST;
import static com.spaceman.tport.inventories.ItemFactory.TPortItemAttributes.*;
import static com.spaceman.tport.inventories.ItemFactory.*;
import static com.spaceman.tport.inventories.QuickEditInventories.tportToMoveDataName;
import static com.spaceman.tport.inventories.TPortInventories.openHomeEditGUI;
import static com.spaceman.tport.tport.TPortManager.getTPort;
import static org.bukkit.event.inventory.ClickType.*;
import static org.bukkit.persistence.PersistentDataType.STRING;

public class SettingsInventories {
    
    public static final InventoryModel settings_model                          = new InventoryModel(Material.OAK_BUTTON, QuickEditInventories.last_model_id + 1, "settings");
    public static final InventoryModel settings_version_model                  = new InventoryModel(Material.OAK_BUTTON, settings_model, "settings/version");
    public static final InventoryModel settings_reload_model                   = new InventoryModel(Material.OAK_BUTTON, settings_version_model, "settings/reload");
    public static final InventoryModel settings_log_model                           = new InventoryModel(Material.OAK_BUTTON, settings_reload_model, "settings/log");
    public static final InventoryModel settings_log_set_time_zone_model             = new InventoryModel(Material.OAK_BUTTON, settings_log_model, "settings/log");
    public static final InventoryModel settings_log_time_zone_id_model              = new InventoryModel(Material.OAK_BUTTON, settings_log_set_time_zone_model, "settings/log");
    public static final InventoryModel settings_log_set_time_format_model           = new InventoryModel(Material.OAK_BUTTON, settings_log_time_zone_id_model, "settings/log");
    public static final InventoryModel settings_log_format_accept_time_format_model = new InventoryModel(Material.OAK_BUTTON, settings_log_set_time_format_model, "settings/log");
    public static final InventoryModel settings_log_format_accept_time_format_grayed_model = new InventoryModel(Material.OAK_BUTTON, settings_log_format_accept_time_format_model, "settings/log");
    public static final InventoryModel settings_log_format_keyboard_model = new InventoryModel(Material.OAK_BUTTON, settings_log_format_accept_time_format_grayed_model, "settings/log");
    public static final InventoryModel settings_log_format_reset_model              = new InventoryModel(Material.OAK_BUTTON, settings_log_format_keyboard_model, "settings/log");
    public static final InventoryModel settings_log_format_eraDesignator_model      = new InventoryModel(Material.OAK_BUTTON, settings_log_format_reset_model, "settings/log");
    public static final InventoryModel settings_log_format_year_model               = new InventoryModel(Material.OAK_BUTTON, settings_log_format_eraDesignator_model, "settings/log");
    public static final InventoryModel settings_log_format_weekYear_model           = new InventoryModel(Material.OAK_BUTTON, settings_log_format_year_model, "settings/log");
    public static final InventoryModel settings_log_format_monthInYear_model        = new InventoryModel(Material.OAK_BUTTON, settings_log_format_weekYear_model, "settings/log");
    public static final InventoryModel settings_log_format_weekInYear_model         = new InventoryModel(Material.OAK_BUTTON, settings_log_format_monthInYear_model, "settings/log");
    public static final InventoryModel settings_log_format_weekInMonth_model        = new InventoryModel(Material.OAK_BUTTON, settings_log_format_weekInYear_model, "settings/log");
    public static final InventoryModel settings_log_format_dayInYear_model          = new InventoryModel(Material.OAK_BUTTON, settings_log_format_weekInMonth_model, "settings/log");
    public static final InventoryModel settings_log_format_dayInMonth_model         = new InventoryModel(Material.OAK_BUTTON, settings_log_format_dayInYear_model, "settings/log");
    public static final InventoryModel settings_log_format_dayOfWeekInMonth_model   = new InventoryModel(Material.OAK_BUTTON,settings_log_format_dayInMonth_model, "settings/log");
    public static final InventoryModel settings_log_format_dayNameInWeek_model      = new InventoryModel(Material.OAK_BUTTON, settings_log_format_dayOfWeekInMonth_model, "settings/log");
    public static final InventoryModel settings_log_format_dayNumberOfWeek_model    = new InventoryModel(Material.OAK_BUTTON, settings_log_format_dayNameInWeek_model, "settings/log");
    public static final InventoryModel settings_log_format_amPmMarker_model         = new InventoryModel(Material.OAK_BUTTON, settings_log_format_dayNumberOfWeek_model, "settings/log");
    public static final InventoryModel settings_log_format_hourInDay0_23_model      = new InventoryModel(Material.OAK_BUTTON, settings_log_format_amPmMarker_model, "settings/log");
    public static final InventoryModel settings_log_format_hourInDay1_24_model      = new InventoryModel(Material.OAK_BUTTON, settings_log_format_hourInDay0_23_model, "settings/log");
    public static final InventoryModel settings_log_format_hourInAmPm0_11_model     = new InventoryModel(Material.OAK_BUTTON, settings_log_format_hourInDay1_24_model, "settings/log");
    public static final InventoryModel settings_log_format_hourInAmPm1_12_model     = new InventoryModel(Material.OAK_BUTTON, settings_log_format_hourInAmPm0_11_model, "settings/log");
    public static final InventoryModel settings_log_format_minuteInHour_model       = new InventoryModel(Material.OAK_BUTTON, settings_log_format_hourInAmPm1_12_model, "settings/log");
    public static final InventoryModel settings_log_format_secondInMinute_model     = new InventoryModel(Material.OAK_BUTTON, settings_log_format_minuteInHour_model, "settings/log");
    public static final InventoryModel settings_log_format_millisecond_model        = new InventoryModel(Material.OAK_BUTTON, settings_log_format_secondInMinute_model, "settings/log");
    public static final InventoryModel settings_log_format_timeZone0_model          = new InventoryModel(Material.OAK_BUTTON, settings_log_format_millisecond_model, "settings/log");
    public static final InventoryModel settings_log_format_timeZone1_model          = new InventoryModel(Material.OAK_BUTTON, settings_log_format_timeZone0_model, "settings/log");
    public static final InventoryModel settings_log_format_timeZone2_model          = new InventoryModel(Material.OAK_BUTTON, settings_log_format_timeZone1_model, "settings/log");
    public static final InventoryModel settings_log_logdata_model                   = new InventoryModel(Material.OAK_BUTTON, settings_log_format_timeZone2_model, "settings/log");
    public static final InventoryModel settings_log_logdata_not_logged_model        = new InventoryModel(Material.BARRIER, settings_log_logdata_model, "settings/log");
    public static final InventoryModel settings_log_size_model                      = new InventoryModel(Material.OAK_BUTTON, settings_log_logdata_not_logged_model, "settings/log");
    public static final InventoryModel settings_backup_model                   = new InventoryModel(Material.OAK_BUTTON, settings_log_size_model, "settings/backup");
    public static final InventoryModel settings_backup_state_true_model        = new InventoryModel(Material.OAK_BUTTON, settings_backup_model, "settings/backup");
    public static final InventoryModel settings_backup_state_false_model       = new InventoryModel(Material.OAK_BUTTON, settings_backup_state_true_model, "settings/backup");
    public static final InventoryModel settings_backup_count_model             = new InventoryModel(Material.OAK_BUTTON, settings_backup_state_false_model, "settings/backup");
    public static final InventoryModel settings_backup_load_model              = new InventoryModel(Material.OAK_BUTTON, settings_backup_count_model, "settings/backup");
    public static final InventoryModel settings_backup_save_model              = new InventoryModel(Material.OAK_BUTTON, settings_backup_load_model, "settings/backup");
    public static final InventoryModel settings_biome_tp_model                 = new InventoryModel(Material.OAK_BUTTON, settings_backup_save_model, "settings/biome_tp");
    public static final InventoryModel settings_delay_model                    = new InventoryModel(Material.OAK_BUTTON, settings_biome_tp_model, "settings/delay");
    public static final InventoryModel settings_restriction_model              = new InventoryModel(Material.OAK_BUTTON, settings_delay_model, "settings/restriction");
    public static final InventoryModel settings_dynmap_model                   = new InventoryModel(Material.OAK_BUTTON, settings_restriction_model, "settings/dynmap");
    public static final InventoryModel settings_bluemap_model                  = new InventoryModel(Material.OAK_BUTTON, settings_dynmap_model, "settings/bluemap");
    public static final InventoryModel settings_particle_animation_model       = new InventoryModel(Material.OAK_BUTTON, settings_bluemap_model, "settings/particle_animation");
    public static final InventoryModel settings_pltp_model                     = new InventoryModel(Material.OAK_BUTTON, settings_particle_animation_model, "settings/pltp");
    public static final InventoryModel settings_pltp_state_on_model            = new InventoryModel(Material.OAK_BUTTON, settings_pltp_model, "settings/pltp");
    public static final InventoryModel settings_pltp_state_off_model           = new InventoryModel(Material.OAK_BUTTON, settings_pltp_state_on_model, "settings/pltp");
    public static final InventoryModel settings_pltp_state_grayed_model        = new InventoryModel(Material.OAK_BUTTON, settings_pltp_state_off_model, "settings/pltp");
    public static final InventoryModel settings_pltp_consent_on_model          = new InventoryModel(Material.OAK_BUTTON, settings_pltp_state_grayed_model, "settings/pltp");
    public static final InventoryModel settings_pltp_consent_off_model         = new InventoryModel(Material.OAK_BUTTON, settings_pltp_consent_on_model, "settings/pltp");
    public static final InventoryModel settings_pltp_consent_grayed_model      = new InventoryModel(Material.OAK_BUTTON, settings_pltp_consent_off_model, "settings/pltp");
    public static final InventoryModel settings_pltp_offset_in_model           = new InventoryModel(Material.OAK_BUTTON, settings_pltp_consent_grayed_model, "settings/pltp");
    public static final InventoryModel settings_pltp_offset_behind_model       = new InventoryModel(Material.OAK_BUTTON, settings_pltp_offset_in_model, "settings/pltp");
    public static final InventoryModel settings_pltp_offset_grayed_model       = new InventoryModel(Material.OAK_BUTTON, settings_pltp_offset_behind_model, "settings/pltp");
    public static final InventoryModel settings_pltp_preview_on_model          = new InventoryModel(Material.OAK_BUTTON, settings_pltp_offset_grayed_model, "settings/pltp");
    public static final InventoryModel settings_pltp_preview_off_model         = new InventoryModel(Material.OAK_BUTTON, settings_pltp_preview_on_model, "settings/pltp");
    public static final InventoryModel settings_pltp_preview_notified_model    = new InventoryModel(Material.OAK_BUTTON, settings_pltp_preview_off_model, "settings/pltp");
    public static final InventoryModel settings_pltp_preview_grayed_model      = new InventoryModel(Material.OAK_BUTTON, settings_pltp_preview_notified_model, "settings/pltp");
    public static final InventoryModel settings_pltp_whitelist_model           = new InventoryModel(Material.OAK_BUTTON, settings_pltp_preview_grayed_model, "settings/pltp");
    public static final InventoryModel settings_pltp_whitelist_grayed_model    = new InventoryModel(Material.OAK_BUTTON, settings_pltp_whitelist_model, "settings/pltp");
    public static final InventoryModel settings_public_model                   = new InventoryModel(Material.OAK_BUTTON, settings_pltp_whitelist_grayed_model, "settings/public_tp");
    public static final InventoryModel settings_public_size_model              = new InventoryModel(Material.OAK_BUTTON, settings_public_model, "settings/public_tp");
    public static final InventoryModel settings_public_fit_model               = new InventoryModel(Material.OAK_BUTTON, settings_public_size_model, "settings/public_tp");
    public static final InventoryModel settings_public_reset_model             = new InventoryModel(Material.OAK_BUTTON, settings_public_fit_model, "settings/public_tp");
    public static final InventoryModel settings_public_tports_model            = new InventoryModel(Material.OAK_BUTTON, settings_public_reset_model, "settings/public_tp");
    public static final InventoryModel settings_public_filter_own_model        = new InventoryModel(Material.OAK_BUTTON, settings_public_tports_model, "settings/public_tp");
    public static final InventoryModel settings_public_filter_all_model        = new InventoryModel(Material.OAK_BUTTON, settings_public_filter_own_model, "settings/public_tp");
    public static final InventoryModel settings_resource_pack_model                   = new InventoryModel(Material.OAK_BUTTON, settings_public_filter_all_model, "settings/resource_pack");
    public static final InventoryModel settings_resource_pack_state_enabled_model     = new InventoryModel(Material.OAK_BUTTON, settings_resource_pack_model, "settings/resource_pack");
    public static final InventoryModel settings_resource_pack_state_disabled_model    = new InventoryModel(Material.OAK_BUTTON, settings_resource_pack_state_enabled_model, "settings/resource_pack");
    public static final InventoryModel settings_resource_pack_resolution_model        = new InventoryModel(Material.OAK_BUTTON, settings_resource_pack_state_disabled_model, "settings/resource_pack");
    public static final InventoryModel settings_resource_pack_resolution_grayed_model = new InventoryModel(Material.OAK_BUTTON, settings_resource_pack_resolution_model, "settings/resource_pack");
    public static final InventoryModel settings_tag_model                      = new InventoryModel(Material.OAK_BUTTON, settings_resource_pack_resolution_grayed_model, "settings/tag");
    public static final InventoryModel settings_tag_selection_model            = new InventoryModel(Material.OAK_BUTTON, settings_tag_model, "settings/tag");
    public static final InventoryModel settings_tag_create_model               = new InventoryModel(Material.OAK_BUTTON, settings_tag_selection_model, "settings/tag");
    public static final InventoryModel settings_tag_reset_model                = new InventoryModel(Material.OAK_BUTTON, settings_tag_create_model, "settings/tag");
    public static final InventoryModel settings_transfer_model                 = new InventoryModel(Material.OAK_BUTTON, settings_tag_reset_model, "settings/transfer");
    public static final InventoryModel settings_transfer_switch_offered_model  = new InventoryModel(Material.OAK_BUTTON, settings_transfer_model, "settings/transfer");
    public static final InventoryModel settings_transfer_switch_offers_model   = new InventoryModel(Material.OAK_BUTTON, settings_transfer_switch_offered_model, "settings/transfer");
    public static final InventoryModel settings_features_model                 = new InventoryModel(Material.OAK_BUTTON, settings_transfer_switch_offers_model, "settings/features");
    public static final InventoryModel settings_features_biome_tp_model        = new InventoryModel(Material.OAK_BUTTON, settings_features_model, "settings/features");
    public static final InventoryModel settings_features_biome_tp_grayed_model = new InventoryModel(Material.OAK_BUTTON, settings_features_biome_tp_model, "settings/features");
    public static final InventoryModel settings_features_feature_tp_model         = new InventoryModel(Material.OAK_BUTTON, settings_features_biome_tp_grayed_model, "settings/features");
    public static final InventoryModel settings_features_feature_tp_grayed_model  = new InventoryModel(Material.OAK_BUTTON, settings_features_feature_tp_model, "settings/features");
    public static final InventoryModel settings_features_back_tp_model         = new InventoryModel(Material.OAK_BUTTON, settings_features_feature_tp_grayed_model, "settings/features");
    public static final InventoryModel settings_features_back_tp_grayed_model  = new InventoryModel(Material.OAK_BUTTON, settings_features_back_tp_model, "settings/features");
    public static final InventoryModel settings_features_public_tp_model         = new InventoryModel(Material.OAK_BUTTON, settings_features_back_tp_grayed_model, "settings/features");
    public static final InventoryModel settings_features_public_tp_grayed_model  = new InventoryModel(Material.OAK_BUTTON, settings_features_public_tp_model, "settings/features");
    public static final InventoryModel settings_features_pltp_model         = new InventoryModel(Material.OAK_BUTTON, settings_features_public_tp_grayed_model, "settings/features");
    public static final InventoryModel settings_features_pltp_grayed_model  = new InventoryModel(Material.OAK_BUTTON, settings_features_pltp_model, "settings/features");
    public static final InventoryModel settings_features_dynmap_model         = new InventoryModel(Material.OAK_BUTTON, settings_features_pltp_grayed_model, "settings/features");
    public static final InventoryModel settings_features_dynmap_grayed_model  = new InventoryModel(Material.OAK_BUTTON, settings_features_dynmap_model, "settings/features");
    public static final InventoryModel settings_features_bluemap_model         = new InventoryModel(Material.OAK_BUTTON, settings_features_dynmap_grayed_model, "settings/features");
    public static final InventoryModel settings_features_bluemap_grayed_model  = new InventoryModel(Material.OAK_BUTTON, settings_features_bluemap_model, "settings/features");
    public static final InventoryModel settings_features_metrics_model         = new InventoryModel(Material.OAK_BUTTON, settings_features_bluemap_grayed_model, "settings/features");
    public static final InventoryModel settings_features_metrics_grayed_model  = new InventoryModel(Material.OAK_BUTTON, settings_features_metrics_model, "settings/features");
    public static final InventoryModel settings_features_companion_tp_model         = new InventoryModel(Material.OAK_BUTTON, settings_features_metrics_grayed_model, "settings/features");
    public static final InventoryModel settings_features_companion_tp_grayed_model  = new InventoryModel(Material.OAK_BUTTON, settings_features_companion_tp_model, "settings/features");
    public static final InventoryModel settings_features_permissions_model         = new InventoryModel(Material.OAK_BUTTON, settings_features_companion_tp_grayed_model, "settings/features");
    public static final InventoryModel settings_features_permissions_grayed_model  = new InventoryModel(Material.OAK_BUTTON, settings_features_permissions_model, "settings/features");
    public static final InventoryModel settings_features_particle_animation_model         = new InventoryModel(Material.OAK_BUTTON, settings_features_permissions_grayed_model, "settings/features");
    public static final InventoryModel settings_features_particle_animation_grayed_model  = new InventoryModel(Material.OAK_BUTTON, settings_features_particle_animation_model, "settings/features");
    public static final InventoryModel settings_features_redirects_model         = new InventoryModel(Material.OAK_BUTTON, settings_features_particle_animation_grayed_model, "settings/features");
    public static final InventoryModel settings_features_redirects_grayed_model  = new InventoryModel(Material.OAK_BUTTON, settings_features_redirects_model, "settings/features");
    public static final InventoryModel settings_features_history_model         = new InventoryModel(Material.OAK_BUTTON, settings_features_redirects_grayed_model, "settings/features");
    public static final InventoryModel settings_features_history_grayed_model  = new InventoryModel(Material.OAK_BUTTON, settings_features_history_model, "settings/features");
    public static final InventoryModel settings_features_preview_model         = new InventoryModel(Material.OAK_BUTTON, settings_features_history_grayed_model, "settings/features");
    public static final InventoryModel settings_features_preview_grayed_model  = new InventoryModel(Material.OAK_BUTTON, settings_features_preview_model, "settings/features");
    public static final InventoryModel settings_features_world_tp_model         = new InventoryModel(Material.OAK_BUTTON, settings_features_preview_grayed_model, "settings/features");
    public static final InventoryModel settings_features_world_tp_grayed_model  = new InventoryModel(Material.OAK_BUTTON, settings_features_world_tp_model, "settings/features");
    public static final InventoryModel settings_features_tport_takes_item_model         = new InventoryModel(Material.OAK_BUTTON, settings_features_world_tp_grayed_model, "settings/features");
    public static final InventoryModel settings_features_tport_takes_item_grayed_model  = new InventoryModel(Material.OAK_BUTTON, settings_features_tport_takes_item_model, "settings/features");
    public static final InventoryModel settings_features_interdimensional_teleporting_model         = new InventoryModel(Material.OAK_BUTTON, settings_features_tport_takes_item_grayed_model, "settings/features");
    public static final InventoryModel settings_features_interdimensional_teleporting_grayed_model  = new InventoryModel(Material.OAK_BUTTON, settings_features_interdimensional_teleporting_model, "settings/features");
    public static final InventoryModel settings_features_death_tp_model         = new InventoryModel(Material.OAK_BUTTON, settings_features_interdimensional_teleporting_grayed_model, "settings/features");
    public static final InventoryModel settings_features_death_tp_grayed_model  = new InventoryModel(Material.OAK_BUTTON, settings_features_death_tp_model, "settings/features");
    public static final InventoryModel settings_features_look_tp_model         = new InventoryModel(Material.OAK_BUTTON, settings_features_death_tp_grayed_model, "settings/features");
    public static final InventoryModel settings_features_look_tp_grayed_model  = new InventoryModel(Material.OAK_BUTTON, settings_features_look_tp_model, "settings/features");
    public static final InventoryModel settings_features_ensure_unique_uuid_model         = new InventoryModel(Material.OAK_BUTTON, settings_features_look_tp_grayed_model, "settings/features");
    public static final InventoryModel settings_features_ensure_unique_uuid_grayed_model  = new InventoryModel(Material.OAK_BUTTON, settings_features_ensure_unique_uuid_model, "settings/features");
    public static final InventoryModel settings_features_print_errors_in_console_model         = new InventoryModel(Material.OAK_BUTTON, settings_features_ensure_unique_uuid_grayed_model, "settings/features");
    public static final InventoryModel settings_features_print_errors_in_console_grayed_model  = new InventoryModel(Material.OAK_BUTTON, settings_features_print_errors_in_console_model, "settings/features");
    public static final InventoryModel settings_features_feature_settings_model         = new InventoryModel(Material.OAK_BUTTON, settings_features_print_errors_in_console_grayed_model, "settings/features");
    public static final InventoryModel settings_features_feature_settings_grayed_model  = new InventoryModel(Material.OAK_BUTTON, settings_features_feature_settings_model, "settings/features");
    public static final InventoryModel settings_redirect_model                                 = new InventoryModel(Material.OAK_BUTTON, settings_features_feature_settings_grayed_model, "settings/redirect");
    public static final InventoryModel settings_redirect_console_feedback_model                = new InventoryModel(Material.OAK_BUTTON, settings_redirect_model, "settings/redirect");
    public static final InventoryModel settings_redirect_console_feedback_grayed_model         = new InventoryModel(Material.OAK_BUTTON, settings_redirect_console_feedback_model, "settings/redirect");
    public static final InventoryModel settings_redirect_tp_pltp_model                         = new InventoryModel(Material.OAK_BUTTON, settings_redirect_console_feedback_grayed_model, "settings/redirect");
    public static final InventoryModel settings_redirect_tp_pltp_grayed_model                  = new InventoryModel(Material.OAK_BUTTON, settings_redirect_tp_pltp_model, "settings/redirect");
    public static final InventoryModel settings_redirect_locate_feature_tp_model               = new InventoryModel(Material.OAK_BUTTON, settings_redirect_tp_pltp_grayed_model, "settings/redirect");
    public static final InventoryModel settings_redirect_locate_feature_tp_grayed_model        = new InventoryModel(Material.OAK_BUTTON, settings_redirect_locate_feature_tp_model, "settings/redirect");
    public static final InventoryModel settings_redirect_locate_biome_biome_tp_model           = new InventoryModel(Material.OAK_BUTTON, settings_redirect_locate_feature_tp_grayed_model, "settings/redirect");
    public static final InventoryModel settings_redirect_locate_biome_biome_tp_grayed_model    = new InventoryModel(Material.OAK_BUTTON, settings_redirect_locate_biome_biome_tp_model, "settings/redirect");
    public static final InventoryModel settings_redirect_home_tport_home_model                 = new InventoryModel(Material.OAK_BUTTON, settings_redirect_locate_biome_biome_tp_grayed_model, "settings/redirect");
    public static final InventoryModel settings_redirect_home_tport_home_grayed_model          = new InventoryModel(Material.OAK_BUTTON, settings_redirect_home_tport_home_model, "settings/redirect");
    public static final InventoryModel settings_redirect_back_tport_back_model                 = new InventoryModel(Material.OAK_BUTTON, settings_redirect_home_tport_home_grayed_model, "settings/redirect");
    public static final InventoryModel settings_redirect_back_tport_back_grayed_model          = new InventoryModel(Material.OAK_BUTTON, settings_redirect_back_tport_back_model, "settings/redirect");
    public static final InventoryModel settings_redirect_tpa_pltp_tp_model                     = new InventoryModel(Material.OAK_BUTTON, settings_redirect_back_tport_back_grayed_model, "settings/redirect");
    public static final InventoryModel settings_redirect_tpa_pltp_tp_grayed_model              = new InventoryModel(Material.OAK_BUTTON, settings_redirect_tpa_pltp_tp_model, "settings/redirect");
    public static final InventoryModel settings_redirect_tpaccept_requests_accept_model        = new InventoryModel(Material.OAK_BUTTON, settings_redirect_tpa_pltp_tp_grayed_model, "settings/redirect");
    public static final InventoryModel settings_redirect_tpaccept_requests_accept_grayed_model = new InventoryModel(Material.OAK_BUTTON, settings_redirect_tpaccept_requests_accept_model, "settings/redirect");
    public static final InventoryModel settings_redirect_tpdeny_requests_reject_model          = new InventoryModel(Material.OAK_BUTTON, settings_redirect_tpaccept_requests_accept_grayed_model, "settings/redirect");
    public static final InventoryModel settings_redirect_tpdeny_requests_reject_grayed_model   = new InventoryModel(Material.OAK_BUTTON, settings_redirect_tpdeny_requests_reject_model, "settings/redirect");
    public static final InventoryModel settings_redirect_tprevoke_requests_revoke_model        = new InventoryModel(Material.OAK_BUTTON, settings_redirect_tpdeny_requests_reject_grayed_model, "settings/redirect");
    public static final InventoryModel settings_redirect_tprevoke_requests_revoke_grayed_model = new InventoryModel(Material.OAK_BUTTON, settings_redirect_tprevoke_requests_revoke_model, "settings/redirect");
    public static final InventoryModel settings_redirect_tprandom_biome_tp_random_model        = new InventoryModel(Material.OAK_BUTTON, settings_redirect_tprevoke_requests_revoke_grayed_model, "settings/redirect");
    public static final InventoryModel settings_redirect_tprandom_biome_tp_random_grayed_model = new InventoryModel(Material.OAK_BUTTON, settings_redirect_tprandom_biome_tp_random_model, "settings/redirect");
    public static final InventoryModel settings_remove_player_model            = new InventoryModel(Material.OAK_BUTTON, settings_redirect_tprandom_biome_tp_random_grayed_model, "settings/remove_player");
    public static final InventoryModel settings_remove_player_confirm_model    = new InventoryModel(Material.OAK_BUTTON, settings_remove_player_model, "settings/remove_player");
    public static final InventoryModel settings_remove_player_cancel_model     = new InventoryModel(Material.OAK_BUTTON, settings_remove_player_confirm_model, "settings/remove_player");
    public static final InventoryModel settings_safety_check_model                     = new InventoryModel(Material.OAK_BUTTON, settings_remove_player_cancel_model, "settings/safety_check");
    public static final InventoryModel settings_safety_check_tport_open_model          = new InventoryModel(Material.OAK_BUTTON, settings_safety_check_model, "settings/safety_check");
    public static final InventoryModel settings_safety_check_tport_open_grayed_model   = new InventoryModel(Material.OAK_BUTTON, settings_safety_check_tport_open_model, "settings/safety_check");
    public static final InventoryModel settings_safety_check_tport_own_model           = new InventoryModel(Material.OAK_BUTTON, settings_safety_check_tport_open_grayed_model, "settings/safety_check");
    public static final InventoryModel settings_safety_check_tport_own_grayed_model    = new InventoryModel(Material.OAK_BUTTON, settings_safety_check_tport_own_model, "settings/safety_check");
    public static final InventoryModel settings_safety_check_tport_home_model          = new InventoryModel(Material.OAK_BUTTON, settings_safety_check_tport_own_grayed_model, "settings/safety_check");
    public static final InventoryModel settings_safety_check_tport_home_grayed_model   = new InventoryModel(Material.OAK_BUTTON, settings_safety_check_tport_home_model, "settings/safety_check");
    public static final InventoryModel settings_safety_check_tport_back_model          = new InventoryModel(Material.OAK_BUTTON, settings_safety_check_tport_home_grayed_model, "settings/safety_check");
    public static final InventoryModel settings_safety_check_tport_back_grayed_model   = new InventoryModel(Material.OAK_BUTTON, settings_safety_check_tport_back_model, "settings/safety_check");
    public static final InventoryModel settings_safety_check_pltp_model = new InventoryModel(Material.OAK_BUTTON, settings_safety_check_tport_back_grayed_model, "settings/safety_check");
    public static final InventoryModel settings_safety_check_pltp_grayed_model = new InventoryModel(Material.OAK_BUTTON, settings_safety_check_pltp_model, "settings/safety_check");
    public static final InventoryModel settings_safety_check_tport_public_model        = new InventoryModel(Material.OAK_BUTTON, settings_safety_check_pltp_grayed_model, "settings/safety_check");
    public static final InventoryModel settings_safety_check_tport_public_grayed_model = new InventoryModel(Material.OAK_BUTTON, settings_safety_check_tport_public_model, "settings/safety_check");
    public static final InventoryModel settings_language_model                 = new InventoryModel(Material.OAK_BUTTON, settings_safety_check_tport_public_grayed_model, "settings/language");
    public static final InventoryModel settings_cooldown_model                 = new InventoryModel(Material.OAK_BUTTON, settings_language_model, "settings/cooldown");
    public static final InventoryModel settings_cooldown_tport_tp_model        = new InventoryModel(Material.OAK_BUTTON, settings_cooldown_model, "settings/cooldown");
    public static final InventoryModel settings_cooldown_player_tp_model       = new InventoryModel(Material.OAK_BUTTON, settings_cooldown_tport_tp_model, "settings/cooldown");
    public static final InventoryModel settings_cooldown_feature_tp_model      = new InventoryModel(Material.OAK_BUTTON, settings_cooldown_player_tp_model, "settings/cooldown");
    public static final InventoryModel settings_cooldown_biome_tp_model        = new InventoryModel(Material.OAK_BUTTON, settings_cooldown_feature_tp_model, "settings/cooldown");
    public static final InventoryModel settings_cooldown_search_model          = new InventoryModel(Material.OAK_BUTTON, settings_cooldown_biome_tp_model, "settings/cooldown");
    public static final InventoryModel settings_cooldown_back_model            = new InventoryModel(Material.OAK_BUTTON, settings_cooldown_search_model, "settings/cooldown");
    public static final InventoryModel settings_cooldown_look_tp_model         = new InventoryModel(Material.OAK_BUTTON, settings_cooldown_back_model, "settings/cooldown");
    public static final InventoryModel settings_color_theme_model              = new InventoryModel(Material.OAK_BUTTON, settings_cooldown_look_tp_model, "settings/color_theme");
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
    public static final InventoryModel settings_restore_model                  = new InventoryModel(Material.OAK_BUTTON, settings_color_theme_blue_remove_model, "settings/restore");
    public static final InventoryModel settings_home_model                     = new InventoryModel(Material.OAK_BUTTON, settings_restore_model, "settings/home");
    public static final InventoryModel settings_search_model                              = new InventoryModel(Material.OAK_BUTTON, settings_home_model, "settings/search");
    public static final InventoryModel settings_search_mode_equal_model                   = new InventoryModel(Material.OAK_BUTTON, settings_search_model, "settings/search");
    public static final InventoryModel settings_search_mode_not_equal_model               = new InventoryModel(Material.OAK_BUTTON, settings_search_mode_equal_model, "settings/search");
    public static final InventoryModel settings_search_mode_contains_model                = new InventoryModel(Material.OAK_BUTTON, settings_search_mode_not_equal_model, "settings/search");
    public static final InventoryModel settings_search_mode_not_contains_model            = new InventoryModel(Material.OAK_BUTTON, settings_search_mode_contains_model, "settings/search");
    public static final InventoryModel settings_search_mode_starts_model                  = new InventoryModel(Material.OAK_BUTTON, settings_search_mode_not_contains_model, "settings/search");
    public static final InventoryModel settings_search_mode_ends_model                    = new InventoryModel(Material.OAK_BUTTON, settings_search_mode_starts_model, "settings/search");
    public static final InventoryModel settings_search_tport_model                        = new InventoryModel(Material.OAK_BUTTON, settings_search_mode_ends_model, "settings/search");
    public static final InventoryModel settings_search_description_model                  = new InventoryModel(Material.OAK_BUTTON, settings_search_tport_model, "settings/search");
    public static final InventoryModel settings_search_player_model                       = new InventoryModel(Material.OAK_BUTTON, settings_search_description_model, "settings/search");
    public static final InventoryModel settings_search_owned_tports_model           = new InventoryModel(Material.OAK_BUTTON, settings_search_player_model, "settings/search");
    public static final InventoryModel settings_search_can_tp_model                       = new InventoryModel(Material.OAK_BUTTON, settings_search_owned_tports_model, "settings/search");
    public static final InventoryModel settings_search_biome_model                        = new InventoryModel(Material.OAK_BUTTON, settings_search_can_tp_model, "settings/search");
    public static final InventoryModel settings_search_biome_preset_model                 = new InventoryModel(Material.OAK_BUTTON, settings_search_biome_model, "settings/search");
    public static final InventoryModel settings_search_dimension_model                    = new InventoryModel(Material.OAK_BUTTON, settings_search_biome_preset_model, "settings/search");
    public static final InventoryModel settings_search_dimension_overworld_model          = new InventoryModel(Material.OAK_BUTTON, settings_search_dimension_model, "settings/search");
    public static final InventoryModel settings_search_dimension_nether_model             = new InventoryModel(Material.OAK_BUTTON, settings_search_dimension_overworld_model, "settings/search");
    public static final InventoryModel settings_search_dimension_the_end_model            = new InventoryModel(Material.OAK_BUTTON, settings_search_dimension_nether_model, "settings/search");
    public static final InventoryModel settings_search_dimension_other_environments_model = new InventoryModel(Material.OAK_BUTTON, settings_search_dimension_the_end_model, "settings/search");
    public static final InventoryModel settings_search_tag_model                          = new InventoryModel(Material.OAK_BUTTON, settings_search_dimension_other_environments_model, "settings/search");
    public static final InventoryModel settings_search_tag_tags_model                     = new InventoryModel(Material.OAK_BUTTON, settings_search_tag_model, "settings/search");
    public static final InventoryModel settings_search_world_model                        = new InventoryModel(Material.OAK_BUTTON, settings_search_tag_tags_model, "settings/search");
    public static final InventoryModel settings_search_world_overworld_model              = new InventoryModel(Material.OAK_BUTTON, settings_search_world_model, "settings/search");
    public static final InventoryModel settings_search_world_nether_model                 = new InventoryModel(Material.OAK_BUTTON, settings_search_world_overworld_model, "settings/search");
    public static final InventoryModel settings_search_world_the_end_model                = new InventoryModel(Material.OAK_BUTTON, settings_search_world_nether_model, "settings/search");
    public static final InventoryModel settings_search_world_other_worlds_model           = new InventoryModel(Material.OAK_BUTTON, settings_search_world_the_end_model, "settings/search");
    public static final InventoryModel settings_adapter_model                   = new InventoryModel(Material.OAK_BUTTON, settings_search_world_other_worlds_model, "settings/adapter");
    public static final InventoryModel settings_adapter_adapter_model           = new InventoryModel(Material.OAK_BUTTON, settings_adapter_model, "settings/adapter");
    public static final int last_model_id = settings_adapter_adapter_model.getCustomModelData();
    
    public static void openBackup_loadBackupGUI(Player player, int page, @Nullable FancyInventory prevWindow) {
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        
        ArrayList<ItemStack> items = new ArrayList<>();
        for (String backup : Load.getBackups(true)) {
            ItemStack item = settings_backup_load_model.getItem(player);
            Message title = formatInfoTranslation("tport.settingsInventories.openTPortBackup_load_backupGUI.backup", backup);
            title.translateMessage(playerLang);
            setCustomItemData(item, colorTheme, title, null);
            addCommand(item, LEFT, "tport backup load " + backup);
            
            items.add(item);
        }
        
        FancyInventory inv = getDynamicScrollableInventory(player, page, SettingsInventories::openBackup_loadBackupGUI,
                "tport.settingsInventories.openTPortBackup_load_backupGUI.title", items, createBack(player, BACKUP, OWN, MAIN));
        
        inv.open(player);
    }
    public static void openBackupGUI(Player player) {
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
        setCustomItemData(autoCount, colorTheme, autoCountTitle, Arrays.asList(new Message(), autoCountLC, autoCountRC));
        addFunction(autoCount, LEFT, (whoClicked, clickType, pdc, fancyInventory) -> {
            TPortCommand.executeTPortCommand(whoClicked, "backup auto " + (Auto.getBackupCount() + 1));
            openBackupGUI(whoClicked);
        });
        addFunction(autoCount, ClickType.RIGHT, (whoClicked, clickType, pdc, fancyInventory) -> {
            TPortCommand.executeTPortCommand(whoClicked, "backup auto " + (Auto.getBackupCount() - 1));
            openBackupGUI(whoClicked);
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
        setCustomItemData(backupStateItem, colorTheme, stateTitle, Arrays.asList(new Message(), stateLore));
        
        if (Auto.getBackupState()) {
            addFunction(backupStateItem, LEFT, (whoClicked, clickType, pdc, fancyInventory) -> {
                TPortCommand.executeTPortCommand(whoClicked, "backup auto false");
                openBackupGUI(whoClicked);
            });
        } else {
            addFunction(backupStateItem, LEFT, (whoClicked, clickType, pdc, fancyInventory) -> {
                TPortCommand.executeTPortCommand(whoClicked, "backup auto true");
                openBackupGUI(whoClicked);
            });
        }
        
        ItemStack save = settings_backup_save_model.getItem(player);
        addFunction(save, LEFT, (whoClicked, clickType, pdc, fancyInventory) -> {
            TPortCommand.executeTPortCommand(whoClicked, "backup save");
            openBackupGUI(whoClicked);
        });
        addFunction(save, RIGHT, (whoClicked, clickType, pdc, fancyInventory) -> {
            FancyClickEvent.FancyClickRunnable onAccept = (whoClicked1, clickType1, pdc1, keyboardInventory) -> {
                String keyboardOutput = getKeyboardOutput(keyboardInventory);
                TPortCommand.executeTPortCommand(whoClicked1, "backup save " + keyboardOutput);
                openBackupGUI(whoClicked1);
            };
            FancyClickEvent.FancyClickRunnable onReject = (whoClicked1, clickType1, pdc1, keyboardInventory) -> openBackupGUI(whoClicked1);
            KeyboardGUI.openKeyboard(whoClicked, onAccept, onReject, KeyboardGUI.NUMBERS | KeyboardGUI.CHARS | KeyboardGUI.LINES);
        });
        Message saveTitle = formatInfoTranslation("tport.settingsInventories.openTPortBackupGUI.save");
        saveTitle.translateMessage(playerLang);
        Message saveLeft = formatInfoTranslation("tport.settingsInventories.openTPortBackupGUI.save.left", LEFT);
        saveLeft.translateMessage(playerLang);
        Message saveRight = formatInfoTranslation("tport.settingsInventories.openTPortBackupGUI.save.right", RIGHT);
        saveRight.translateMessage(playerLang);
        setCustomItemData(save, colorTheme, saveTitle, Arrays.asList(new Message(), saveLeft, saveRight));
        
        ItemStack load = settings_backup_load_model.getItem(player);
        Message loadTitle = formatInfoTranslation("tport.settingsInventories.openTPortBackupGUI.load");
        loadTitle.translateMessage(playerLang);
        Message loadLeft = formatInfoTranslation("tport.settingsInventories.openTPortBackupGUI.load.left", LEFT);
        loadLeft.translateMessage(playerLang);
        setCustomItemData(load, colorTheme, loadTitle, Arrays.asList(new Message(), loadLeft));
        addFunction(load, LEFT, (whoClicked, clickType, pdc, fancyInventory) -> openBackup_loadBackupGUI(whoClicked, 0, null));
        
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
            setCustomItemData(redAdd, colorTheme, redAddTitle, Arrays.asList(valueHEXMessage, valueRGBMessage, new Message(), valueAdd1, valueAdd10, valueAdd25, valueAdd100));
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
            setCustomItemData(redRem, colorTheme, redRemTitle, Arrays.asList(valueHEXMessage, valueRGBMessage, new Message(), valueRemove1, valueRemove10, valueRemove25, valueRemove100));
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
            setCustomItemData(greenAdd, colorTheme, greenAddTitle, Arrays.asList(valueHEXMessage, valueRGBMessage, new Message(), valueAdd1, valueAdd10, valueAdd25, valueAdd100));
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
            setCustomItemData(greenRem, colorTheme, greenRemTitle, Arrays.asList(valueHEXMessage, valueRGBMessage, new Message(), valueRemove1, valueRemove10, valueRemove25, valueRemove100));
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
            setCustomItemData(blueAdd, colorTheme, blueAddTitle, Arrays.asList(valueHEXMessage, valueRGBMessage, new Message(), valueAdd1, valueAdd10, valueAdd25, valueAdd100));
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
            setCustomItemData(blueRem, colorTheme, blueRemTitle, Arrays.asList(valueHEXMessage, valueRGBMessage, new Message(), valueRemove1, valueRemove10, valueRemove25, valueRemove100));
            FancyClickEvent.setStringData(blueRem, new NamespacedKey(Main.getInstance(), "colorType_name"), colorType.name());
            addFunction(blueRem, LEFT, ((whoClicked, clickType, pdc, fancyInventory) ->
                    changeColorRunnable(whoClicked, pdc, fancyInventory, (color) -> new Color(color.getRed(), color.getGreen(), Math.max(color.getBlue() - 1, 0)))));
            addFunction(blueRem, RIGHT, ((whoClicked, clickType, pdc, fancyInventory) ->
                    changeColorRunnable(whoClicked, pdc, fancyInventory, (color) -> new Color(color.getRed(), color.getGreen(), Math.max(color.getBlue() - 10, 0)))));
            addFunction(blueRem, SHIFT_LEFT, ((whoClicked, clickType, pdc, fancyInventory) ->
                    changeColorRunnable(whoClicked, pdc, fancyInventory, (color) -> new Color(color.getRed(), color.getGreen(), Math.max(color.getBlue() - 25, 0)))));
            addFunction(blueRem, SHIFT_RIGHT, ((whoClicked, clickType, pdc, fancyInventory) ->
                    changeColorRunnable(whoClicked, pdc, fancyInventory, (color) -> new Color(color.getRed(), color.getGreen(), Math.max(color.getBlue() - 100, 0)))));
            
            ItemStack builtInColorSelector = keyboard_chat_color_model.getItem(player);
            Message builtInColorSelectorTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openTPortColorTheme_editTypeGUI.colorTypes.openBuiltInColorSelector");
            setCustomItemData(builtInColorSelector, colorTheme, builtInColorSelectorTitle,  null);
            FancyClickEvent.setStringData(builtInColorSelector, new NamespacedKey(Main.getInstance(), "colorType_name"), colorType.name());
            addFunction(builtInColorSelector, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
                NamespacedKey key = new NamespacedKey(Main.getInstance(), "colorType_name");
                if (pdc.has(key, STRING)) {
                    ColorTheme.ColorType type = ColorTheme.ColorType.valueOf(pdc.get(key, STRING));
                    openBuiltInColorSelector(whoClicked, type, 0, fancyInventory);
                }
            }));
            
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
            setCustomItemData(masterColor, colorTheme, masterColorTitle, Arrays.asList(valueHEXMessage, valueRGBMessage));
            
            inv.setItem(10 + indexOffset, masterColor);
            inv.setItem(11 + indexOffset, redAdd);
            inv.setItem(12 + indexOffset, redRem);
            inv.setItem(13 + indexOffset, greenAdd);
            inv.setItem(14 + indexOffset, greenRem);
            inv.setItem(15 + indexOffset, blueAdd);
            inv.setItem(16 + indexOffset, blueRem);
            inv.setItem(17 + indexOffset, builtInColorSelector);
            
            indexOffset += 9;
        }
        
        ItemStack backItem = createBack(player, COLOR_THEME, OWN, MAIN);
        inv.setItem(inv.getSize() - 1, backItem);
        
        inv.open(player);
    }
    private static void openBuiltInColorSelector(Player player, int page, @Nonnull FancyInventory editTypeInventory) {
        openBuiltInColorSelector(player, editTypeInventory.getData("color_type", ColorType.class), page, editTypeInventory);
    }
    private static void openBuiltInColorSelector(Player player, ColorType colorType, int page, @Nonnull FancyInventory editTypeInventory) {
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        
        ArrayList<ItemStack> items = new ArrayList<>();
        for (ChatColor chatColor : ChatColor.values()) {
            if (!chatColor.isColor()) continue;
            ItemStack is = KeyboardGUI.getChatColorStack(chatColor, player);
            MultiColor chatMultiColor = new MultiColor(chatColor);
            Message title = formatTranslation(colorTheme.getInfoColor(), chatMultiColor,
                    "tport.settingsInventories.openBuiltInColorSelector.chatColor", chatColor.name(), "&", chatColor.getChar()).translateMessage(playerLang);
            
            Message valueHEXMessage = formatInfoTranslation(playerLang, "tport.settingsInventories.openBuiltInColorSelector.chatColor.hex", chatMultiColor.getColorAsValue());
            Message valueRGBMessage = formatInfoTranslation(playerLang, "tport.settingsInventories.openBuiltInColorSelector.chatColor.rgb", chatMultiColor.getRed(), chatMultiColor.getGreen(), chatMultiColor.getBlue());
            Message clickToSelect = formatInfoTranslation(playerLang, "tport.settingsInventories.openBuiltInColorSelector.clickToSelect", LEFT);
            
            setCustomItemData(is, colorTheme, title, List.of(new Message(), valueHEXMessage, valueRGBMessage, new Message(), clickToSelect));
            setStringData(is, new NamespacedKey(Main.getInstance(), "keyboard_chat_color"), chatColor.name());
            addFunction(is, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
                ChatColor c = ChatColor.valueOf(pdc.get(new NamespacedKey(Main.getInstance(), "keyboard_chat_color"), STRING));
                ColorType type = fancyInventory.getData("color_type", ColorType.class);
                
                ColorTheme theme = fancyInventory.getData("colorTheme", ColorTheme.class);
                type.setColor(theme, new MultiColor(c));
                openTPortColorTheme_editTypeGUI(whoClicked, fancyInventory.getData("colorTypes", java.util.List.class));
            }));
            
            items.add(is);
        }
        for (DyeColor dyeColor : DyeColor.values()) {
            ItemStack is = KeyboardGUI.getDyeStack(dyeColor);
            MultiColor chatMultiColor = new MultiColor(dyeColor);
            Message title = formatTranslation(colorTheme.getInfoColor(), chatMultiColor,
                    "tport.settingsInventories.openBuiltInColorSelector.dyeColor", dyeColor.name()).translateMessage(playerLang);
            
            Message valueHEXMessage = formatInfoTranslation(playerLang, "tport.settingsInventories.openBuiltInColorSelector.dyeColor.hex", chatMultiColor.getColorAsValue());
            Message valueRGBMessage = formatInfoTranslation(playerLang, "tport.settingsInventories.openBuiltInColorSelector.dyeColor.rgb", chatMultiColor.getRed(), chatMultiColor.getGreen(), chatMultiColor.getBlue());
            Message clickToSelect = formatInfoTranslation(playerLang, "tport.settingsInventories.openBuiltInColorSelector.clickToSelect", LEFT);
            
            setCustomItemData(is, colorTheme, title, List.of(new Message(), valueHEXMessage, valueRGBMessage, new Message(), clickToSelect));
            setStringData(is, new NamespacedKey(Main.getInstance(), "keyboard_dye_color"), dyeColor.name());
            addFunction(is, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
                DyeColor c = DyeColor.valueOf(pdc.get(new NamespacedKey(Main.getInstance(), "keyboard_dye_color"), STRING));
                ColorType type = fancyInventory.getData("color_type", ColorType.class);
                
                ColorTheme theme = fancyInventory.getData("colorTheme", ColorTheme.class);
                type.setColor(theme, new MultiColor(c));
                
                openTPortColorTheme_editTypeGUI(whoClicked, fancyInventory.getData("colorTypes", java.util.List.class));
            }));
            
            items.add(is);
        }
        
        ItemStack backButton = keyboard_chat_color_reject_model.getItem(player);
        Message backTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openBuiltInColorSelector.cancel");
        setCustomItemData(backButton, colorTheme, backTitle, null);
        addFunction(backButton, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> openTPortColorTheme_editTypeGUI(whoClicked, fancyInventory.getData("colorTypes", java.util.List.class))));
        
        Message title = formatInfoTranslation(playerLang, "tport.settingsInventories.openBuiltInColorSelector.title");
        FancyInventory builtInColorSelectorKeyboard = getDynamicScrollableInventory(player, page, SettingsInventories::openBuiltInColorSelector, title, items, backButton);
        builtInColorSelectorKeyboard.transferData(editTypeInventory, false);
        builtInColorSelectorKeyboard.setData("color_type", colorType);
        
        builtInColorSelectorKeyboard.open(player);
    }
    public static void openTPortColorTheme_selectTheme(Player player, int page, FancyInventory prevWindow) {
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        
        ArrayList<ItemStack> items = new ArrayList<>();
        
        for (Map.Entry<String, ColorTheme> themeEntry : ColorTheme.getDefaultThemesMap().entrySet()) {
            ItemStack themeItem = settings_color_theme_model.getItem(player);
            
            addCommand(themeItem, LEFT, "tport colorTheme set " + themeEntry.getKey());
            addFunction(themeItem, LEFT, ((whoClicked, clickType, pdc, fancyInventory) ->
                    openTPortColorTheme_selectTheme(whoClicked, fancyInventory.getData(pageDataName), fancyInventory)));
            
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
            
            setCustomItemData(themeItem, themeEntry.getValue(), itemTitle,
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
        setCustomItemData(info, colorTheme, infoTheme, Arrays.asList(infoList, infoArray, new Message(), clickToEditInfo));
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
        setCustomItemData(success, colorTheme, successTheme, Arrays.asList(successList, successArray, new Message(), clickToEditSuccess));
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
        setCustomItemData(error, colorTheme, errorTheme, Arrays.asList(errorList, errorArray, new Message(), clickToEditError));
        addFunction(error, LEFT, (whoClicked, clickType, pdc, fancyInventory) -> openTPortColorTheme_editTypeGUI(whoClicked, Arrays.asList(errorColor, varErrorColor, varError2Color)));
        inv.setItem(12, error);
        
        ItemStack good = settings_color_theme_good_model.getItem(player);
        Message goodMessage = formatTranslation(varInfoColor, varInfoColor, "tport.settingsInventories.openTPortColorThemeGUI.good");
        goodMessage.translateMessage(playerLang);
        Message goodTheme = formatTranslation(goodColor, goodColor, "tport.settingsInventories.openTPortColorThemeGUI.good.theme", goodMessage);
        goodTheme.translateMessage(playerLang);
        Message clickToEditGood = formatTranslation(goodColor, varInfoColor, "tport.settingsInventories.openTPortColorThemeGUI.good.clickToEdit", LEFT);
        clickToEditGood.translateMessage(playerLang);
        setCustomItemData(good, colorTheme, goodTheme, Arrays.asList(new Message(), clickToEditGood));
        addFunction(good, LEFT, (whoClicked, clickType, pdc, fancyInventory) -> openTPortColorTheme_editTypeGUI(whoClicked, Arrays.asList(goodColor, badColor, titleColor)));
        inv.setItem(13, good);
        
        ItemStack bad = settings_color_theme_bad_model.getItem(player);
        Message badMessage = formatTranslation(varInfoColor, varInfoColor, "tport.settingsInventories.openTPortColorThemeGUI.bad");
        badMessage.translateMessage(playerLang);
        Message badTheme = formatTranslation(badColor, badColor, "tport.settingsInventories.openTPortColorThemeGUI.bad.theme", badMessage);
        badTheme.translateMessage(playerLang);
        Message clickToEditBad = formatTranslation(badColor, varInfoColor, "tport.settingsInventories.openTPortColorThemeGUI.bad.clickToEdit", LEFT);
        clickToEditBad.translateMessage(playerLang);
        setCustomItemData(bad, colorTheme, badTheme, Arrays.asList(new Message(), clickToEditBad));
        addFunction(bad, LEFT, (whoClicked, clickType, pdc, fancyInventory) -> openTPortColorTheme_editTypeGUI(whoClicked, Arrays.asList(goodColor, badColor, titleColor)));
        inv.setItem(14, bad);
        
        ItemStack title = settings_color_theme_title_model.getItem(player);
        Message titleMessage = formatTranslation(varInfoColor, varInfoColor, "tport.settingsInventories.openTPortColorThemeGUI.title.title");
        titleMessage.translateMessage(playerLang);
        Message titleTheme = formatTranslation(titleColor, varInfoColor, "tport.settingsInventories.openTPortColorThemeGUI.title.theme", titleMessage);
        titleTheme.translateMessage(playerLang);
        Message clickToEditTitle = formatTranslation(titleColor, varInfoColor, "tport.settingsInventories.openTPortColorThemeGUI.title.clickToEdit", LEFT);
        clickToEditTitle.translateMessage(playerLang);
        setCustomItemData(title, colorTheme, titleTheme, Arrays.asList(new Message(), clickToEditTitle));
        addFunction(title, LEFT, (whoClicked, clickType, pdc, fancyInventory) -> openTPortColorTheme_editTypeGUI(whoClicked, Arrays.asList(goodColor, badColor, titleColor)));
        inv.setItem(15, title);
        
        ItemStack selectFromList = settings_color_theme_select_model.getItem(player);
        Message selectFromListTitle = formatInfoTranslation("tport.settingsInventories.openTPortColorThemeGUI.selectFromList.title", LEFT);
        selectFromListTitle.translateMessage(playerLang);
        setCustomItemData(selectFromList, colorTheme, selectFromListTitle, null);
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
                getPlayerList(player, false, true, List.of(ItemFactory.HeadAttributes.TPORT_AMOUNT, ItemFactory.HeadAttributes.REMOVE_PLAYER), List.of(), null),
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
        setCustomItemData(cancel, colorTheme, cancelTitle, null);
        inv.setItem(11, cancel);
        
        ItemStack head = ItemFactory.getHead(toRemove, player, List.of(ItemFactory.HeadAttributes.TPORT_AMOUNT), null);
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
        setCustomItemData(confirm, colorTheme, confirmTitle, null);
        inv.setItem(15, confirm);
        
        inv.open(player);
    }
    
    public static void openTransferGUI(Player player, int page, @Nullable FancyInventory prevWindow) {
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        
        boolean offers = false;
        if (prevWindow != null) {
            //noinspection ConstantValue
            offers = prevWindow.getData("offers", Boolean.class, offers);
        }
        
        ArrayList<ItemStack> items = new ArrayList<>();
        String titleID;
        String switchID;
        ItemStack offerSwitch;
        if (offers) {
            titleID = "tport.settingsInventories.openTPortTransferGUI.title.offers";
            switchID = "tport.settingsInventories.openTPortTransferGUI.switch.toOffered";
            offerSwitch = settings_transfer_switch_offers_model.getItem(player);
            
            for (String uuidString : tportData.getKeys("tport")) {
                UUID uuid = UUID.fromString(uuidString);
                for (TPort tport : TPortManager.getTPortList(uuid)) {
                    if (player.getUniqueId().equals(tport.getOfferedTo())) {
                        PlayerEncapsulation otherPlayer = asPlayer(tport.getOwner());
                        ItemStack is = ItemFactory.toTPortItem(tport, player, List.of(ItemFactory.TPortItemAttributes.TRANSFER_OFFERS), otherPlayer);
                        addFunction(is, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> openTransferGUI(whoClicked, fancyInventory.getData(pageDataName), fancyInventory)));
                        items.add(is);
                    }
                }
            }
        } else {
            titleID = "tport.settingsInventories.openTPortTransferGUI.title.offered";
            switchID = "tport.settingsInventories.openTPortTransferGUI.switch.toOffers";
            offerSwitch = settings_transfer_switch_offered_model.getItem(player);
            
            for (TPort tport : TPortManager.getTPortList(player.getUniqueId())) {
                if (tport.isOffered()) {
                    PlayerEncapsulation otherPlayer = asPlayer(tport.getOfferedTo());
                    ItemStack is = ItemFactory.toTPortItem(tport, player, List.of(ItemFactory.TPortItemAttributes.TRANSFER_OFFERED), otherPlayer);
                    addFunction(is, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> openTransferGUI(whoClicked, fancyInventory.getData(pageDataName), fancyInventory)));
                    items.add(is);
                }
            }
        }
        
        Message title = formatInfoTranslation(playerLang, titleID);
        ItemStack backButton = createBack(player, SETTINGS, OWN, MAIN);
        
        FancyInventory inv = getDynamicScrollableInventory(player, page, SettingsInventories::openTransferGUI, title, items, backButton);
        inv.setData("offers", offers);
        
        Message switchTitle = formatInfoTranslation(playerLang, switchID);
        setCustomItemData(offerSwitch, colorTheme, switchTitle, null);
        addFunction(offerSwitch, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
            int p = fancyInventory.getData(pageDataName);
            fancyInventory.setData("offers", !fancyInventory.getData("offers", Boolean.class));
            openTransferGUI(whoClicked, p, fancyInventory);
        }));
        inv.setItem(inv.getSize() - 9, offerSwitch);
        
        inv.open(player);
    }
    
    private static ItemStack getFeaturesItem(Features.Feature feature, Player player, @Nullable ColorTheme colorTheme, @Nullable JsonObject playerLang) {
        if (colorTheme == null) colorTheme = ColorTheme.getTheme(player);
        if (playerLang == null) playerLang = getPlayerLang(player.getUniqueId());
        
        ItemStack is = feature.getModel().getItem(player);
        
        Message title = formatInfoTranslation(playerLang, "tport.settingsInventories.openTPortFeaturesGUI.feature.title", feature.name());
        Message stateMessage;
        Message nextStateMessage;
        String newState;
        if (feature.isEnabled()) {
            stateMessage = formatTranslation(varInfoColor, varInfoColor, "tport.settingsInventories.openTPortFeaturesGUI.feature.enabled");
            nextStateMessage = formatTranslation(varInfoColor, varInfoColor, "tport.settingsInventories.openTPortFeaturesGUI.feature.disable");
            newState = "false";
        } else {
            stateMessage = formatTranslation(varInfoColor, varInfoColor, "tport.settingsInventories.openTPortFeaturesGUI.feature.disabled");
            nextStateMessage = formatTranslation(varInfoColor, varInfoColor, "tport.settingsInventories.openTPortFeaturesGUI.feature.enable");
            newState = "true";
        }
        Message currentStateMessage = formatInfoTranslation(playerLang, "tport.settingsInventories.openTPortFeaturesGUI.feature.state", stateMessage);
        Message clickMessage = formatInfoTranslation(playerLang, "tport.settingsInventories.openTPortFeaturesGUI.feature.nextState", LEFT, nextStateMessage);
        
        ArrayList<Message> lore = new ArrayList<>();
        lore.add(new Message());
        lore.add(currentStateMessage);
        if (Features.Feature.FeatureSettings.isEnabled()) {
            lore.add(formatInfoTranslation(playerLang, "tport.settingsInventories.openTPortFeaturesGUI.feature.moreInfo", RIGHT));
            lore.add(new Message());
            lore.add(clickMessage);
        }
        
        setCustomItemData(is, colorTheme, title, lore);
        
        addCommand(is, LEFT, "tport features " + feature.name() + " state " + newState);
        addFunction(is, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> openFeaturesGUI(whoClicked, fancyInventory.getData(pageDataName), fancyInventory)));
        addCommand(is, RIGHT, "tport features " + feature.name());
        
        return is;
    }
    private static void openFeaturesGUI(Player player, int page, @Nullable FancyInventory prevWindow) {
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        ArrayList<ItemStack> items = new ArrayList<>();
        
        for (Features.Feature feature : Features.Feature.values()) {
            items.add(getFeaturesItem(feature, player, colorTheme, playerLang));
        }
        
        FancyInventory inv = getDynamicScrollableInventory(player, page, SettingsInventories::openFeaturesGUI, "tport.settingsInventories.openTPortFeaturesGUI.title", items, createBack(player, SETTINGS, OWN, MAIN));
        
        inv.open(player);
    }
    
    private static void openRedirectsGUI(Player player, int page, @Nullable FancyInventory prevWindow) {
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        ArrayList<ItemStack> items = new ArrayList<>();
        
        for (Redirect.Redirects redirect : Redirect.Redirects.values()) {
            ItemStack is = redirect.getModel().getItem(player);
            
            Message title = formatInfoTranslation(playerLang, "tport.settingsInventories.openTPortRedirectsGUI.redirect.title", redirect.name());
            Message stateMessage;
            Message nextStateMessage;
            String newState;
            if (redirect.isEnabled()) {
                stateMessage = formatTranslation(varInfoColor, varInfoColor, "tport.settingsInventories.openTPortRedirectsGUI.redirect.enabled");
                nextStateMessage = formatTranslation(varInfoColor, varInfoColor, "tport.settingsInventories.openTPortRedirectsGUI.redirect.disable");
                newState = "false";
            } else {
                stateMessage = formatTranslation(varInfoColor, varInfoColor, "tport.settingsInventories.openTPortRedirectsGUI.redirect.disabled");
                nextStateMessage = formatTranslation(varInfoColor, varInfoColor, "tport.settingsInventories.openTPortRedirectsGUI.redirect.enable");
                newState = "true";
            }
            Message currentStateMessage = formatInfoTranslation(playerLang, "tport.settingsInventories.openTPortRedirectsGUI.redirect.state", stateMessage);
            Message clickMessage = formatInfoTranslation(playerLang, "tport.settingsInventories.openTPortRedirectsGUI.redirect.nextState", LEFT, nextStateMessage);
            
            ArrayList<Message> lore = new ArrayList<>();
            lore.add(new Message());
            lore.add(currentStateMessage);
            lore.add(formatInfoTranslation(playerLang, "tport.settingsInventories.openTPortRedirectsGUI.redirect.moreInfo", RIGHT));
            lore.add(new Message());
            lore.add(clickMessage);
            
            setCustomItemData(is, colorTheme, title, lore);
            
            addCommand(is, LEFT, "tport redirect " + redirect.name() + " " + newState);
            addFunction(is, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> openRedirectsGUI(whoClicked, fancyInventory.getData(pageDataName), fancyInventory)));
            addCommand(is, RIGHT, "tport redirect");
            
            items.add(is);
        }
        
        FancyInventory inv = getDynamicScrollableInventory(player, page, SettingsInventories::openRedirectsGUI, "tport.settingsInventories.openTPortRedirectsGUI.title", items, createBack(player, SETTINGS, OWN, MAIN));
        
        inv.open(player);
    }
    
    private static void openSafetyCheckGUI(Player player, int page, @Nullable FancyInventory prevWindow) {
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        ArrayList<ItemStack> items = new ArrayList<>();
        
        for (SafetyCheck.SafetyCheckSource source : SafetyCheck.SafetyCheckSource.values()) {
            ItemStack is = source.getModel(player).getItem(player);
            
            Message title = formatInfoTranslation(playerLang, "tport.settingsInventories.openTPortSafetyCheckGUI.source.title", source.name());
            Message stateMessage;
            Message nextStateMessage;
            String newState;
            if (source.getState(player)) {
                stateMessage = formatTranslation(varInfoColor, varInfoColor, "tport.settingsInventories.openTPortSafetyCheckGUI.source.enabled");
                nextStateMessage = formatTranslation(varInfoColor, varInfoColor, "tport.settingsInventories.openTPortSafetyCheckGUI.source.disable");
                newState = "false";
            } else {
                stateMessage = formatTranslation(varInfoColor, varInfoColor, "tport.settingsInventories.openTPortSafetyCheckGUI.source.disabled");
                nextStateMessage = formatTranslation(varInfoColor, varInfoColor, "tport.settingsInventories.openTPortSafetyCheckGUI.source.enable");
                newState = "true";
            }
            Message currentStateMessage = formatInfoTranslation(playerLang, "tport.settingsInventories.openTPortSafetyCheckGUI.source.state", stateMessage);
            Message clickMessage = formatInfoTranslation(playerLang, "tport.settingsInventories.openTPortSafetyCheckGUI.source.nextState", LEFT, nextStateMessage);
            
            ArrayList<Message> lore = new ArrayList<>();
            lore.add(new Message());
            lore.add(currentStateMessage);
            lore.add(source.getDescription().translateMessage(playerLang));
            lore.add(new Message());
            lore.add(clickMessage);
            
            setCustomItemData(is, colorTheme, title, lore);
            
            addCommand(is, LEFT, "tport safetyCheck " + source.name() + " " + newState);
            addFunction(is, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> openSafetyCheckGUI(whoClicked, fancyInventory.getData(pageDataName), fancyInventory)));
            
            items.add(is);
        }
        
        FancyInventory inv = getDynamicScrollableInventory(player, page, SettingsInventories::openSafetyCheckGUI, "tport.settingsInventories.openTPortSafetyCheckGUI.title", items, createBack(player, SETTINGS, OWN, MAIN));
        
        inv.open(player);
    }
    
    private static void openTagGUI(Player player, int page, @Nullable FancyInventory prevWindow) {
        ColorTheme colorTheme = getTheme(player);
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        ArrayList<ItemStack> items = new ArrayList<>();
        
        for (String tag : Tag.getTags()) {
            ItemStack item = settings_tag_selection_model.getItem(player);
            
            Message tagTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openTPortTagGUI.tag.title", tag);
            Message tagLore = formatInfoTranslation(playerLang, "tport.settingsInventories.openTPortTagGUI.tag.delete", LEFT);
            setCustomItemData(item, colorTheme, tagTitle, List.of(new Message(), tagLore));
            addCommand(item, LEFT, "tport tag delete " + tag);
            addFunction(item, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
                int p = fancyInventory.getData(pageDataName);
                openTagGUI(whoClicked, p, null);
            }));
            
            items.add(item);
        }
        
        FancyInventory inv = getDynamicScrollableInventory(player, page, SettingsInventories::openTagGUI,
                "tport.settingsInventories.openTPortTagGUI.title",
                items, createBack(player, SETTINGS, OWN, MAIN));
        
        ItemStack createTag = settings_tag_create_model.getItem(player);
        setCustomItemData(createTag, colorTheme, formatInfoTranslation(playerLang, "tport.settingsInventories.openTPortTagGUI.tag.create", LEFT), null);
        addFunction(createTag, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
            FancyClickEvent.FancyClickRunnable onAccept = ((whoClicked1, clickType1, pdc1, keyboardInventory) -> {
                String tagName = getKeyboardOutput(keyboardInventory);
                TPortCommand.executeTPortCommand(whoClicked1, new String[] {"tag", "create", tagName});
                openTagGUI(whoClicked1, 0, null);
            });
            FancyClickEvent.FancyClickRunnable onReject = ((whoClicked1, clickType1, pdc1, keyboardInventory) -> openTagGUI(whoClicked1, 0, null));
            KeyboardGUI.openKeyboard(whoClicked, onAccept, onReject, KeyboardGUI.TEXT_ONLY);
        }));
        inv.setItem(inv.getSize() - 6, createTag);
        
        ItemStack resetTags = settings_tag_reset_model.getItem(player);
        setCustomItemData(resetTags, colorTheme, formatInfoTranslation(playerLang, "tport.settingsInventories.openTPortTagGUI.reset", SHIFT_LEFT), null);
        addCommand(resetTags, SHIFT_LEFT, "tport tag reset");
        addFunction(resetTags, SHIFT_RIGHT, ((whoClicked, clickType, pdc, fancyInventory) -> {
            openTagGUI(whoClicked, 0, null);
        }));
        inv.setItem(inv.getSize() - 4, resetTags);
        
        inv.open(player);
    }
    
    static void openPLTPGUI(Player player) {
        ColorTheme colorTheme = getTheme(player);
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        boolean pltpEnabled = Features.Feature.PLTP.isEnabled();
        
        FancyInventory inv = new FancyInventory(27, formatInfoTranslation("tport.settingsInventories.openTPortPLTPGUI.title"));
        
        boolean pltpState = State.getPLTPState(player);
        boolean pltpConsent = Consent.shouldAskConsent(player);
        Offset.PLTPOffset pltpOffset = Offset.getPLTPOffset(player);
        Preview.PreviewState previewState = Preview.getPreviewState(player.getUniqueId());
        
        ItemStack stateItem = pltpEnabled ?
                (pltpState ?
                        settings_pltp_state_on_model.getItem(player) :
                        settings_pltp_state_off_model.getItem(player)) :
                settings_pltp_state_grayed_model.getItem(player);
        if (pltpEnabled) {
            addCommand(stateItem, LEFT, "tport PLTP state " + !pltpState);
            addFunction(stateItem, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> openPLTPGUI(whoClicked)));
            Message stateTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openTPortPLTPGUI.state.title", pltpState);
            Message stateLore = formatInfoTranslation(playerLang, "tport.settingsInventories.openTPortPLTPGUI.state.click", LEFT, !pltpState);
            setCustomItemData(stateItem, colorTheme, stateTitle, List.of(new Message(), stateLore));
        } else {
            Message stateMessage = formatTranslation(varInfoColor, varInfoColor, "tport.settingsInventories.openTPortPLTPGUI.disabled");
            Message stateTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openTPortPLTPGUI.pltpDisabled", stateMessage);
            setCustomItemData(stateItem, colorTheme, stateTitle, null);
        }
        inv.setItem(10, stateItem);
        
        ItemStack consentItem = pltpEnabled ?
                (pltpConsent ?
                        settings_pltp_consent_on_model.getItem(player) :
                        settings_pltp_consent_off_model.getItem(player)) :
                settings_pltp_consent_grayed_model.getItem(player);
        if (pltpEnabled) {
            addCommand(consentItem, LEFT, "tport PLTP consent " + !pltpConsent);
            addFunction(consentItem, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> openPLTPGUI(whoClicked)));
            Message consentTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openTPortPLTPGUI.consent.title", pltpConsent);
            Message consentLore = formatInfoTranslation(playerLang, "tport.settingsInventories.openTPortPLTPGUI.consent.click", LEFT, !pltpConsent);
            setCustomItemData(consentItem, colorTheme, consentTitle, List.of(new Message(), consentLore));
        } else {
            Message stateMessage = formatTranslation(varInfoColor, varInfoColor, "tport.settingsInventories.openTPortPLTPGUI.disabled");
            Message stateTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openTPortPLTPGUI.pltpDisabled", stateMessage);
            setCustomItemData(consentItem, colorTheme, stateTitle, null);
        }
        inv.setItem(11, consentItem);
        
        ItemStack offsetItem = pltpOffset.getModel().getItem(player);
        if (pltpEnabled) {
            addCommand(offsetItem, LEFT, "tport PLTP offset " + pltpOffset.getNext());
            addFunction(offsetItem, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> openPLTPGUI(whoClicked)));
            Message offsetTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openTPortPLTPGUI.offset.title", pltpOffset);
            Message offsetLore = formatInfoTranslation(playerLang, "tport.settingsInventories.openTPortPLTPGUI.offset.click", LEFT, pltpOffset.getNext());
            setCustomItemData(offsetItem, colorTheme, offsetTitle, List.of(new Message(), offsetLore));
        } else {
            Message stateMessage = formatTranslation(varInfoColor, varInfoColor, "tport.settingsInventories.openTPortPLTPGUI.disabled");
            Message stateTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openTPortPLTPGUI.pltpDisabled", stateMessage);
            setCustomItemData(offsetItem, colorTheme, stateTitle, null);
        }
        inv.setItem(12, offsetItem);
        
        ItemStack previewItem = previewState.getModel().getItem(player);
        if (pltpEnabled && Features.Feature.Preview.isEnabled()) {
            addCommand(previewItem, LEFT, "tport PLTP preview " + previewState.getNext());
            addFunction(previewItem, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> openPLTPGUI(whoClicked)));
            Message previewTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openTPortPLTPGUI.preview.title", previewState);
            Message previewLore = formatInfoTranslation(playerLang, "tport.settingsInventories.openTPortPLTPGUI.preview.click", LEFT, previewState.getNext());
            setCustomItemData(previewItem, colorTheme, previewTitle, List.of(new Message(), previewLore));
        } else {
            if (!pltpEnabled && Features.Feature.Preview.isEnabled()) {
                Message stateMessage = formatTranslation(varInfoColor, varInfoColor, "tport.settingsInventories.openTPortPLTPGUI.disabled");
                Message stateTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openTPortPLTPGUI.pltpDisabled", stateMessage);
                setCustomItemData(previewItem, colorTheme, stateTitle, null);
            } else if (Features.Feature.Preview.isDisabled() && pltpEnabled) {
                Message stateMessage = formatTranslation(varInfoColor, varInfoColor, "tport.settingsInventories.openTPortPLTPGUI.disabled");
                Message stateTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openTPortPLTPGUI.previewDisabled", stateMessage);
                setCustomItemData(previewItem, colorTheme, stateTitle, null);
            } else {
                Message stateMessage = formatTranslation(varInfoColor, varInfoColor, "tport.settingsInventories.openTPortPLTPGUI.disabled");
                Message stateTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openTPortPLTPGUI.bothDisabled", stateMessage);
                setCustomItemData(previewItem, colorTheme, stateTitle, null);
            }
        }
        inv.setItem(13, previewItem);
        
        ItemStack whitelistItem = (pltpEnabled ? settings_pltp_whitelist_model : settings_pltp_whitelist_grayed_model).getItem(player);
        if (pltpEnabled) {
            addFunction(whitelistItem, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> openPLTPWhitelistSelectorGUI(whoClicked, true)));
            Message whitelistTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openTPortPLTPGUI.whitelist.title", previewState);
            Message whitelistLore = formatInfoTranslation(playerLang, "tport.settingsInventories.openTPortPLTPGUI.whitelist.click", LEFT);
            setCustomItemData(whitelistItem, colorTheme, whitelistTitle, List.of(new Message(), whitelistLore));
        } else {
            Message stateMessage = formatTranslation(varInfoColor, varInfoColor, "tport.settingsInventories.openTPortPLTPGUI.disabled");
            Message stateTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openTPortPLTPGUI.pltpDisabled", stateMessage);
            setCustomItemData(whitelistItem, colorTheme, stateTitle, null);
        }
        inv.setItem(14, whitelistItem);
        
        ItemStack pltpFeatureStateItem;
        Message stateMessage;
        if (pltpEnabled) {
            pltpFeatureStateItem = settings_features_pltp_model.getItem(player);
            addCommand(pltpFeatureStateItem, LEFT, "tport features PLTP state false");
            stateMessage = formatTranslation(varInfoColor, varInfoColor, "tport.settingsInventories.openTPortPLTPGUI.changeState.disable");
        } else {
            pltpFeatureStateItem = settings_features_pltp_grayed_model.getItem(player);
            addCommand(pltpFeatureStateItem, LEFT, "tport features PLTP state true");
            stateMessage = formatTranslation(varInfoColor, varInfoColor, "tport.settingsInventories.openTPortPLTPGUI.changeState.enable");
        }
        Message pltpChangeState = formatInfoTranslation(playerLang, "tport.settingsInventories.openTPortPLTPGUI.changeState", LEFT, stateMessage);
        setCustomItemData(pltpFeatureStateItem, colorTheme, pltpChangeState, null);
        addFunction(pltpFeatureStateItem, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> openPLTPGUI(whoClicked)));
        inv.setItem(16, pltpFeatureStateItem);
        
        inv.setItem(17, createBack(player, SETTINGS, OWN, MAIN));
        
        inv.open(player);
    }
    private static void openPLTPWhitelistSelectorGUI(Player player, boolean fromSettings, int page) {
        List<ItemStack> headItems = ItemFactory.getPlayerList(player, false, true, List.of(PLTP_WHITELIST), List.of(), null);
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        JsonObject playerLang = Language.getPlayerLang(player.getUniqueId());
        
        ItemStack backItem = fromSettings ? createBack(player, PLTP, OWN, MAIN) : createBack(player, MAIN, OWN, null);
        
        FancyInventory inv = getDynamicScrollableInventory(player, page, SettingsInventories::openPLTPWhitelistSelectorGUI,
                "tport.tportInventories.openPLTPWhitelistSelectionGUI.title", headItems, backItem);
        
        inv.setData("content", headItems);
        inv.setItem(inv.getSize() / 18 * 9,
                ItemFactory.getSortingItem(player, playerLang, colorTheme,
                        ((whoClicked, clickType, pdc, fancyInventory) -> openPLTPWhitelistSelectorGUI(whoClicked, fancyInventory.getData("fromSettings", Boolean.class, true), 0))));
        inv.setData("fromSettings", fromSettings);
        
        inv.open(player);
    }
    public static void openPLTPWhitelistSelectorGUI(Player player, boolean fromSettings) {
        openPLTPWhitelistSelectorGUI(player, fromSettings, 0);
    }
    public static void openPLTPWhitelistSelectorGUI(Player player, int page, FancyInventory prevWindow) {
        openPLTPWhitelistSelectorGUI(player, prevWindow.getData("fromSettings", Boolean.class, true), page);
    }
    
    private static void openResourcePackGUI(Player player) {
        openResourcePackGUI(player, 0, null);
    }
    private static void openResourcePackGUI(Player player, int page, @Nullable FancyInventory prevWindow) {
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        boolean packState = ResourcePack.getResourcePackState(player.getUniqueId());
        
        ArrayList<ItemStack> items = new ArrayList<>();
        for (ResolutionCommand.Resolution resolution : ResolutionCommand.Resolution.getResolutions()) {
            ItemStack resItem = (packState ? settings_resource_pack_resolution_model : settings_resource_pack_resolution_grayed_model).getItem(player);
            Message resTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openResourcePackGUI.resolution.title", resolution.getName());
            Message resDes = formatInfoTranslation(playerLang, "tport.settingsInventories.openResourcePackGUI.resolution.description", resolution.getDescription());
            Message urlSpacer = null;
            Message resURL = null;
            Message resURLValue = null;
            
            if (resolution.getUrl() != null) {
                urlSpacer = new Message();
                resURL = formatInfoTranslation(playerLang, "tport.settingsInventories.openResourcePackGUI.resolution.url", RIGHT, resolution.getUrl());
                resURLValue = formatInfoTranslation(playerLang, "tport.settingsInventories.openResourcePackGUI.resolution.url.value", resolution.getUrl());
            }
            
            setCustomItemData(resItem, colorTheme, resTitle, Arrays.asList(resDes, urlSpacer, resURL, resURLValue));
            
            if (ResourcePack.getResourcePackResolution(player.getUniqueId()).equals(resolution)) {
                Glow.addGlow(resItem);
            }
            
            addCommand(resItem, LEFT, "tport resourcePack resolution " + resolution.getName());
            addFunction(resItem, LEFT,
                    ((whoClicked, clickType, pdc, fancyInventory) -> openResourcePackGUI(whoClicked, fancyInventory.getData(pageDataName), fancyInventory)));
            
            if (resolution.getUrl() != null) {
                setStringData(resItem, new NamespacedKey(Main.getInstance(), "packName"), resolution.getName());
                addFunction(resItem, RIGHT, ((whoClicked, clickType, pdc, fancyInventory) -> {
                    String packName = pdc.get(new NamespacedKey(Main.getInstance(), "packName"), STRING);
                    ResolutionCommand.Resolution res = ResolutionCommand.Resolution.getResolution(packName);
                    if (res == null) return;
                    
                    String packURL = res.getUrl();
                    Message hereMessage = formatTranslation(varInfoColor, varInfoColor, "tport.settingsInventories.openResourcePackGUI.resolution.openURLMessage.here");
                    hereMessage.getText().forEach(t -> t
                            .setInsertion(packURL)
                            .addTextEvent(hoverEvent(packURL, infoColor))
                            .addTextEvent(openUrl(packURL)));
                    sendInfoTranslation(whoClicked, "tport.settingsInventories.openResourcePackGUI.resolution.openURLMessage", hereMessage, res);
            }));
            }
            items.add(resItem);
        }
        
        Message invTitle = formatInfoTranslation("tport.settingsInventories.openResourcePackGUI.title");
        FancyInventory inv = getDynamicScrollableInventory(player, page, SettingsInventories::openResourcePackGUI, invTitle, items, createBack(player, SETTINGS, OWN, MAIN));
        
        ItemStack changeStateButton = (packState ? settings_resource_pack_state_enabled_model : settings_resource_pack_state_disabled_model).getItem(player);
        addCommand(changeStateButton, LEFT, "tport resourcePack state " + !packState);
        addFunction(changeStateButton, LEFT,
                ((whoClicked, clickType, pdc, fancyInventory) -> openResourcePackGUI(whoClicked, fancyInventory.getData(pageDataName), fancyInventory)));
        Message stateMessage = formatTranslation(varInfoColor, varInfoColor, "tport.settingsInventories.openResourcePackGUI.state." + (packState ? "enabled" : "disabled"));
        stateMessage.translateMessage(playerLang);
        Message changeStateTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openResourcePackGUI.state.title", stateMessage);
        
        Message newStateMessage = formatTranslation(varInfoColor, varInfoColor, "tport.settingsInventories.openResourcePackGUI.state." + (!packState ? "enable" : "disable"));
        stateMessage.translateMessage(playerLang);
        Message changeStateClickTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openResourcePackGUI.state.change", LEFT, newStateMessage);
        setCustomItemData(changeStateButton, colorTheme, changeStateTitle, List.of(new Message(), changeStateClickTitle));
        inv.setItem(inv.getSize() - 9, changeStateButton);
        
        inv.open(player);
    }
    
    static void openPublicTPTPortsSettings(Player player, int page, @Nullable FancyInventory prevWindow) {
        //public.tports.<publicTPortSlot>.<TPortID>
        
        JsonObject playerLang = Language.getPlayerLang(player.getUniqueId());
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        
        boolean filter = false;
        FancyInventory.DataName<Boolean> filterDataName = new FancyInventory.DataName<>("filter", Boolean.class, false);
        if (prevWindow != null) filter = prevWindow.getData(filterDataName);
        
        List<ItemStack> list = new ArrayList<>();
        
        for (String publicTPortSlot : tportData.getKeys("public.tports")) {
            String tportID = tportData.getConfig().getString("public.tports." + publicTPortSlot, TPortManager.defUUID.toString());
            
            TPort tport = getTPort(UUID.fromString(tportID));
            if (tport != null) {
                if (filter && !tport.getOwner().equals(player.getUniqueId())) continue;
                list.add(toTPortItem(tport, player, List.of(ADD_OWNER, PUBLIC_MOVE_DELETE), prevWindow));
                if (tport.setPublicTPort(true)) {
                    tport.save();
                }
            } else {
                int publicSlotTmp = Integer.parseInt(publicTPortSlot) + 1;
                String tportID2 = tportData.getConfig().getString("public.tports." + publicSlotTmp, TPortManager.defUUID.toString());
                
                TPort tport2 = getTPort(UUID.fromString(tportID2));
                if (tport2 != null) {
                    if (filter && !tport.getOwner().equals(player.getUniqueId())) continue;
                    list.add(toTPortItem(tport2, player, List.of(ADD_OWNER, PUBLIC_MOVE_DELETE), prevWindow));
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
        
        FancyInventory inv = getDynamicScrollableInventory(player, page, SettingsInventories::openPublicTPTPortsSettings, "tport.settingsInventories.openPublicTPTPortsSettings.title", list, createBack(player, PUBLIC_TP_SETTINGS, OWN, MAIN));
        if (prevWindow != null) inv.setData(tportToMoveDataName, prevWindow.getData(tportToMoveDataName));
        inv.setData(filterDataName, filter);
        
        ItemStack publicFilter = (filter ? settings_public_filter_own_model : settings_public_filter_all_model).getItem(player);
        addFunction(publicFilter, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
            fancyInventory.setData("filter", !fancyInventory.getData("filter", Boolean.class, false));
            openPublicTPTPortsSettings(whoClicked, fancyInventory.getData(pageDataName), fancyInventory);
        }));
        Message publicFilterTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openPublicTPTPortsSettings.filter." + (filter ? "own" : "all") + ".title");
        Message publicFilterClick = formatInfoTranslation(playerLang, "tport.settingsInventories.openPublicTPTPortsSettings.filter." + (filter ? "own" : "all") + ".click", LEFT);
        setCustomItemData(publicFilter, colorTheme, publicFilterTitle, List.of(new Message(), publicFilterClick));
        inv.setItem(inv.getSize() - 9, publicFilter);
        
        inv.open(player);
    }
    static void openPublicTPSettings(Player player) {
        ColorTheme colorTheme = getTheme(player);
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        
        FancyInventory inv = new FancyInventory(3, formatInfoTranslation(playerLang, "tport.settingsInventories.openPublicTPSettings.title"));
        
        int listSize = ListSize.getPublicTPortSize();
        ItemStack listSize_resize = settings_public_size_model.getItem(player);
        Message listSizeTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openPublicTPSettings.listSize.title", listSize);
        Message listSizeAdd = formatInfoTranslation(playerLang, "tport.settingsInventories.openPublicTPSettings.listSize.left", LEFT, 1);
        Message listSizeRem = null;
        if (listSize > 0) listSizeRem = formatInfoTranslation(playerLang, "tport.settingsInventories.openPublicTPSettings.listSize.right", RIGHT, 1);
        setCustomItemData(listSize_resize, colorTheme, listSizeTitle, Arrays.asList(new Message(), listSizeAdd, listSizeRem));
        addFunction(listSize_resize, LEFT,  ((whoClicked, clickType, pdc, fancyInventory) -> {
            TPortCommand.executeTPortCommand(whoClicked, new String[]{"public", "listSize", String.valueOf(ListSize.getPublicTPortSize() + 1)});
            openPublicTPSettings(whoClicked);
        }));
        if (listSize > 0) addFunction(listSize_resize, RIGHT, ((whoClicked, clickType, pdc, fancyInventory) -> {
            TPortCommand.executeTPortCommand(whoClicked, new String[]{"public", "listSize", String.valueOf(ListSize.getPublicTPortSize() - 1)});
            openPublicTPSettings(whoClicked);
        }));
        inv.setItem(10, listSize_resize);
        
        ItemStack listSize_fit = settings_public_fit_model.getItem(player);
        Message listSizeFit_title = formatInfoTranslation(playerLang, "tport.settingsInventories.openPublicTPSettings.listSize.amount", ListSize.getTPortAmount());
        Message listSizeFit_fit = formatInfoTranslation(playerLang, "tport.settingsInventories.openPublicTPSettings.listSize.fit", LEFT);
        setCustomItemData(listSize_fit, colorTheme, listSizeFit_title, List.of(new Message(), listSizeFit_fit));
        addFunction(listSize_fit, LEFT,  ((whoClicked, clickType, pdc, fancyInventory) -> {
            TPortCommand.executeTPortCommand(whoClicked, new String[]{"public", "listSize", String.valueOf(ListSize.getTPortAmount())});
            openPublicTPSettings(whoClicked);
        }));
        inv.setItem(11, listSize_fit);
        
        ItemStack resetButton = settings_public_reset_model.getItem(player);
        Message resetTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openPublicTPSettings.reset", SHIFT_LEFT);
        setCustomItemData(resetButton, colorTheme, resetTitle, null);
        addFunction(resetButton, SHIFT_LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
            TPortCommand.executeTPortCommand(whoClicked, new String[]{"public", "reset"});
            openPublicTPSettings(whoClicked);
        }));
        inv.setItem(13, resetButton);
        
        ItemStack moveRemoveButton = settings_public_tports_model.getItem(player);
        Message moveRemoveTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openPublicTPSettings.tports", LEFT);
        setCustomItemData(moveRemoveButton, colorTheme, moveRemoveTitle, null);
        addFunction(moveRemoveButton, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> openPublicTPTPortsSettings(whoClicked, 0, null)));
        inv.setItem(15, moveRemoveButton);
        
        ItemStack featureSwitch = getFeaturesItem(Features.Feature.PublicTP, player, null, null);
        addFunction(featureSwitch, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> openPublicTPSettings(whoClicked)));
        inv.setItem(16, featureSwitch);
        
        inv.setItem(17, createBack(player, SETTINGS, OWN, MAIN));
        
        inv.open(player);
    }
    
    private static ArrayList<Pair<InventoryModel, String>> collectModels(Class<?> clazz) {
        ArrayList<Pair<InventoryModel, String>> models = new ArrayList<>();
        final String modelSuffix = "_model";
        
        for (Field modelField : clazz.getFields()) {
            if (modelField.getType() == InventoryModel.class) {
                if (modelField.getName().endsWith(modelSuffix)) {
                    try {
                        InventoryModel inventoryModel = (InventoryModel) modelField.get(null);
                        String modelName = modelField.getName();
                        modelName = modelName.substring(0, modelName.length() - modelSuffix.length());
                        models.add(new Pair<>(inventoryModel, modelName));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        
        return models;
    }
    private static void openItemDebug(Player player) {
        ArrayList<Pair<InventoryModel, String>> models = collectModels(FancyInventory.class);
        models.addAll( collectModels(KeyboardGUI.class) );
        models.addAll( collectModels(TPortInventories.class) );
        models.addAll( collectModels(QuickEditInventories.class) );
        models.addAll( collectModels(SettingsInventories.class) );
        
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        JsonObject playerLang = Language.getPlayerLang(player.getUniqueId());
        
        ArrayList<ItemStack> items = new ArrayList<>();
        for (Pair<InventoryModel, String> model : models) {
            ItemStack is = model.getLeft().getItem(player);
            Message title = formatInfoTranslation(playerLang, "tport.settingsInventories.openItemDebug.modelName", model.getRight());
            Message customModelData = formatInfoTranslation(playerLang, "tport.settingsInventories.openItemDebug.customModelData", model.getLeft().getCustomModelData());
            Message subDir = formatInfoTranslation(playerLang, "tport.settingsInventories.openItemDebug.subDir", model.getLeft().getSubDir());
            setCustomItemData(is, colorTheme, title, List.of(new Message(), customModelData, subDir));
            items.add(is);
        }
        
        FancyInventory inv = getDynamicScrollableInventory(player, 0, SettingsInventories::openItemDebug, "tport.settingsInventories.openItemDebug.title", items, createBack(player, SETTINGS, OWN, MAIN));
        inv.setData("items", items);
        inv.open(player);
    }
    public static void openItemDebug(Player player, int page, @Nonnull FancyInventory prevWindow) {
        ArrayList<ItemStack> items = prevWindow.getData("items", ArrayList.class, new ArrayList<>());
        FancyInventory inv = getDynamicScrollableInventory(player, page, SettingsInventories::openItemDebug, "tport.settingsInventories.openItemDebug.title", items, createBack(player, SETTINGS, OWN, MAIN));
        inv.setData("items", items);
        inv.open(player);
    }
    
    private static void openTPortCooldownGUI(Player player) {
        openTPortCooldownGUI(player, 0, null);
    }
    private static void openTPortCooldownGUI(Player player, int page, @Nullable FancyInventory prevWindow) {
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        JsonObject playerLang = Language.getPlayerLang(player);
        ArrayList<ItemStack> items = new ArrayList<>();
        
        String selectedCooldown = null;
        if (prevWindow != null) {
            selectedCooldown = prevWindow.getData("selectedCooldown", String.class);
        }
        
        for (CooldownManager cooldown : CooldownManager.values()) {
            ItemStack item = cooldown.getInventoryModel().getItem(player);
            setStringData(item, new NamespacedKey(Main.getInstance(), "value"), cooldown.value());
            setStringData(item, new NamespacedKey(Main.getInstance(), "name"), cooldown.name());
            
            Message title = formatInfoTranslation(playerLang, "tport.settingsInventories.openTPortCooldownGUI.cooldown.title", cooldown.name(), cooldown.value());
            
            if (selectedCooldown == null) {
                Message clickLeft = formatInfoTranslation(playerLang, "tport.settingsInventories.openTPortCooldownGUI.cooldown.left", LEFT);
                Message clickRight = formatInfoTranslation(playerLang, "tport.settingsInventories.openTPortCooldownGUI.cooldown.right.start", RIGHT);
                Message clickShiftRight = formatInfoTranslation(playerLang, "tport.settingsInventories.openTPortCooldownGUI.cooldown.shift_right", SHIFT_RIGHT);
                setCustomItemData(item, colorTheme, title, List.of(new Message(), clickLeft, clickRight, clickShiftRight));
                
                addFunction(item, RIGHT, ((whoClicked, clickType, pdc, fancyInventory) -> {
                    fancyInventory.setData("selectedCooldown", pdc.get(new NamespacedKey(Main.getInstance(), "name"), STRING));
                    openTPortCooldownGUI(whoClicked, fancyInventory.getData(pageDataName), fancyInventory);
                }));
                
                addFunction(item, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
                    FancyClickRunnable onAccept = (whoClicked1, clickType1, pdc1, keyboardGUI) -> {
                        String newCooldownValue = KeyboardGUI.getKeyboardOutput(keyboardGUI);
                        String cooldownName = keyboardGUI.getData("cooldownName", String.class);
                        TPortCommand.executeTPortCommand(whoClicked1, new String[]{"cooldown", cooldownName, newCooldownValue});
                        openTPortCooldownGUI(whoClicked1);
                    };
                    FancyClickRunnable onReject = (whoClicked1, clickType1, pdc1, keyboardGUI) -> openTPortCooldownGUI(whoClicked1);
                    
                    String value = pdc.get(new NamespacedKey(Main.getInstance(), "value"), STRING);
                    try {
                        if (value != null) Long.parseLong(value);
                    } catch (NumberFormatException nfe) {
                        value = "3000";
                    }
                    FancyInventory keyboard = KeyboardGUI.openKeyboard(whoClicked, onAccept, onReject, value, null, NUMBERS);
                    keyboard.setData("cooldownName", pdc.get(new NamespacedKey(Main.getInstance(), "name"), STRING));
                }));
                
                addFunction(item, SHIFT_RIGHT, ((whoClicked, clickType, pdc, fancyInventory) -> {
                    String name = pdc.get(new NamespacedKey(Main.getInstance(), "name"), STRING);
                    TPortCommand.executeTPortCommand(whoClicked, new String[]{"cooldown", name, "permission"});
                    
                    openTPortCooldownGUI(whoClicked, fancyInventory.getData(pageDataName), fancyInventory);
                }));
            }
            else {
                if (cooldown.name().equals(selectedCooldown)) {
                    Message clickRight = formatInfoTranslation(playerLang, "tport.settingsInventories.openTPortCooldownGUI.cooldown.right.cancel", RIGHT);
                    setCustomItemData(item, colorTheme, title, List.of(new Message(), clickRight));
                    
                    Glow.addGlow(item);
                    addFunction(item, RIGHT, ((whoClicked, clickType, pdc, fancyInventory) -> {
                        fancyInventory.setData("selectedCooldown", null);
                        openTPortCooldownGUI(whoClicked, fancyInventory.getData(pageDataName), fancyInventory);
                    }));
                } else {
                    Message clickRight = formatInfoTranslation(playerLang, "tport.settingsInventories.openTPortCooldownGUI.cooldown.right.set", RIGHT, selectedCooldown, cooldown.name());
                    setCustomItemData(item, colorTheme, title, List.of(new Message(), clickRight));
                    
                    addFunction(item, RIGHT, ((whoClicked, clickType, pdc, fancyInventory) -> {
                        String selected = fancyInventory.getData("selectedCooldown", String.class);
                        String name = pdc.get(new NamespacedKey(Main.getInstance(), "name"), STRING);
                        TPortCommand.executeTPortCommand(whoClicked, new String[]{"cooldown", selected, name});
                        
                        fancyInventory.setData("selectedCooldown", null);
                        openTPortCooldownGUI(whoClicked, fancyInventory.getData(pageDataName), fancyInventory);
                    }));
                }
            }
            
            items.add(item);
        }
        
        FancyInventory inv = getDynamicScrollableInventory(player, page, SettingsInventories::openTPortCooldownGUI,
                "tport.settingsInventories.openTPortCooldownGUI.title", items, createBack(player, SETTINGS, OWN, MAIN));
        inv.setData("selectedCooldown", selectedCooldown);
        
        inv.open(player);
    }
    
    private static void openLogTimeZoneEditor(Player player, int page, @Nullable FancyInventory prevWindow) {
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        JsonObject playerLang = Language.getPlayerLang(player);
        
        ArrayList<ItemStack> items = new ArrayList<>();
        TimeZone playerTimeZone = com.spaceman.tport.commands.tport.log.TimeZone.getTimeZone(player);
        
        for (String timeZoneID : java.util.TimeZone.getAvailableIDs()) {
            ItemStack item = settings_log_time_zone_id_model.getItem(player);
            
            Message timeZoneTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openLogTimeZoneEditor.timeZone", timeZoneID);
            setCustomItemData(item, colorTheme, timeZoneTitle, null);
            
            setStringData(item, new NamespacedKey(Main.getInstance(), "timeZone"), timeZoneID);
            
            if (playerTimeZone.equals(TimeZone.getTimeZone(timeZoneID))) {
                Glow.addGlow(item);
                items.add(0, item);
            } else {
                addFunction(item, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
                    String innerTimeZoneID = pdc.get(new NamespacedKey(Main.getInstance(), "timeZone"), STRING);
                    TPortCommand.executeTPortCommand(whoClicked, new String[]{"log", "timeZone", innerTimeZoneID});
                    openLogTimeZoneEditor(whoClicked, 0, fancyInventory);
                }));
                
                items.add(item);
            }
        }
        
        Message title = formatInfoTranslation("tport.settingsInventories.openLogTimeZoneEditor.title");
        FancyInventory inv = getDynamicScrollableInventory(player, page, SettingsInventories::openLogTimeZoneEditor, title, items, createBack(player, LOG_SETTINGS, OWN, MAIN));
        
        inv.open(player);
    }
    private record TimeFormatRecord(String letter, String languageKey, int repeat, InventoryModel inventoryModel) {}
    private static void openLogTimeFormatEditor(Player player) {
        openLogTimeFormatEditor(player, "");
    }
    private static void openLogTimeFormatEditor(Player player, String timeFormat) {
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        JsonObject playerLang = Language.getPlayerLang(player);
        
        Message title = formatInfoTranslation("tport.settingsInventories.openLogTimeFormatEditor.title");
        FancyInventory inv = new FancyInventory(4, title);
        inv.setData("timeFormat", timeFormat);
        
        for (TimeFormatRecord timeFormatRecord : List.of(
                new TimeFormatRecord("G", "eraDesignator",    1, settings_log_format_eraDesignator_model),
                new TimeFormatRecord("y", "year",             4, settings_log_format_year_model),
                new TimeFormatRecord("Y", "weekYear",         4, settings_log_format_weekYear_model),
                new TimeFormatRecord("M", "monthInYear",      4, settings_log_format_monthInYear_model),
                new TimeFormatRecord("w", "weekInYear",       2, settings_log_format_weekInYear_model),
                new TimeFormatRecord("W", "weekInMonth",      1, settings_log_format_weekInMonth_model),
                new TimeFormatRecord("D", "dayInYear",        3, settings_log_format_dayInYear_model),
                new TimeFormatRecord("d", "dayInMonth",       2, settings_log_format_dayInMonth_model),
                new TimeFormatRecord("F", "dayOfWeekInMonth", 1, settings_log_format_dayOfWeekInMonth_model),
                new TimeFormatRecord("E", "dayNameInWeek",    4, settings_log_format_dayNameInWeek_model),
                new TimeFormatRecord("u", "dayNumberOfWeek",  1, settings_log_format_dayNumberOfWeek_model),
                new TimeFormatRecord("a", "amPmMarker",       1, settings_log_format_amPmMarker_model),
                new TimeFormatRecord("H", "hourInDay0-23",    2, settings_log_format_hourInDay0_23_model),
                new TimeFormatRecord("k", "hourInDay1-24",    2, settings_log_format_hourInDay1_24_model),
                new TimeFormatRecord("K", "hourInAmPm0-11",   2, settings_log_format_hourInAmPm0_11_model),
                new TimeFormatRecord("h", "hourInAmPm1-12",   2, settings_log_format_hourInAmPm1_12_model),
                new TimeFormatRecord("m", "minuteInHour",     2, settings_log_format_minuteInHour_model),
                new TimeFormatRecord("s", "secondInMinute",   2, settings_log_format_secondInMinute_model),
                new TimeFormatRecord("S", "millisecond",      3, settings_log_format_millisecond_model),
                new TimeFormatRecord("z", "timeZone0",        4, settings_log_format_timeZone0_model),
                new TimeFormatRecord("Z", "timeZone1",        1, settings_log_format_timeZone1_model),
                new TimeFormatRecord("X", "timeZone2",        3, settings_log_format_timeZone2_model))) {
            
            ItemStack item = timeFormatRecord.inventoryModel().getItem(player);
            Message itemTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openLogTimeFormatEditor.type." + timeFormatRecord.languageKey() + ".title");
            
            ArrayList<Message> lore = new ArrayList<>();
            lore.add(new Message());
            Message itemLetter = formatInfoTranslation(playerLang, "tport.settingsInventories.openLogTimeFormatEditor.type." + timeFormatRecord.languageKey() + ".letter", timeFormatRecord.letter());
            lore.add(itemLetter);
            
            for (int i = 0; i < timeFormatRecord.repeat(); i++) {
                String letters = timeFormatRecord.letter().repeat(i + 1);
                Message itemExample = formatInfoTranslation(playerLang, "tport.settingsInventories.openLogTimeFormatEditor.type." + timeFormatRecord.languageKey() + ".example",
                        letters, TimeFormat.getFormatExample(player, letters));
                lore.add(itemExample);
            }
            setCustomItemData(item, colorTheme, itemTitle, lore);
            
            setStringData(item, new NamespacedKey(Main.getInstance(), "letter"), timeFormatRecord.letter());
            addFunction(item, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
                String letter = pdc.get(new NamespacedKey(Main.getInstance(), "letter"), STRING);
                openLogTimeFormatEditor(whoClicked, fancyInventory.getData("timeFormat", String.class) + letter);
            }));
            
            inv.addItem(item);
        }
        
        ItemStack acceptItem;
        if (timeFormat.isBlank()) {
            acceptItem = settings_log_format_accept_time_format_grayed_model.getItem(player);
            Message acceptTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openLogTimeFormatEditor.accept.title");
            setCustomItemData(acceptItem, colorTheme, acceptTitle, List.of(new Message()));
        } else {
            acceptItem = settings_log_format_accept_time_format_model.getItem(player);
            Message acceptTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openLogTimeFormatEditor.accept.title");
            Message acceptRaw = formatInfoTranslation(playerLang, "tport.settingsInventories.openLogTimeFormatEditor.accept.raw", timeFormat);
            Message acceptFormat = formatInfoTranslation(playerLang, "tport.settingsInventories.openLogTimeFormatEditor.accept.format", TimeFormat.getFormatExample(player, timeFormat));
            setCustomItemData(acceptItem, colorTheme, acceptTitle, List.of(new Message(), acceptRaw, acceptFormat));
            addFunction(acceptItem, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
                TPortCommand.executeTPortCommand(whoClicked, new String[]{"log", "timeFormat", fancyInventory.getData("timeFormat", String.class)});
                openLogGUI(whoClicked);
            }));
        }
        inv.setItem(27, acceptItem);
        
        ItemStack keyboardItem = settings_log_format_keyboard_model.getItem(player);
        Message keyboardTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openLogTimeFormatEditor.keyboard.title");
        setCustomItemData(keyboardItem, colorTheme, keyboardTitle, null);
        addFunction(keyboardItem, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
            String startInput = fancyInventory.getData("timeFormat", String.class);
            FancyClickRunnable onAccept = (whoClicked1, clickType1, pdc1, fancyKeyboard) ->
                    openLogTimeFormatEditor(whoClicked1, getKeyboardOutput(fancyKeyboard));
            FancyClickRunnable onReject = (whoClicked1, clickType1, pdc1, fancyKeyboard) ->
                    openLogTimeFormatEditor(whoClicked1, fancyKeyboard.getData("oldTimeFormat", String.class));
            
            FancyInventory keyboard = openKeyboard(whoClicked, onAccept, onReject, startInput, null, SPACE | NUMBERS | SPECIAL);
            keyboard.setData("oldTimeFormat", startInput);
        }));
        inv.setItem(28, keyboardItem);
        
        ItemStack reset = settings_log_format_reset_model.getItem(player);
        Message resetTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openLogTimeFormatEditor.reset.title", SHIFT_LEFT);
        Message resetValue = formatInfoTranslation(playerLang, "tport.settingsInventories.openLogTimeFormatEditor.reset.default", defaultTimeFormat);
        setCustomItemData(reset, colorTheme, resetTitle, List.of(new Message(), resetValue));
        addFunction(reset, SHIFT_LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
            TPortCommand.executeTPortCommand(whoClicked, new String[]{"log", "timeFormat", defaultTimeFormat});
            openLogGUI(whoClicked);
        }));
        inv.setItem(34, reset);
        
        inv.setItem(35, createBack(player, LOG_SETTINGS, OWN, MAIN));
        
        inv.open(player);
    }
    private static void openLogDataGUI(Player player) {
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        JsonObject playerLang = Language.getPlayerLang(player);
        
        Message title = formatInfoTranslation("tport.settingsInventories.openLogDataGUI.title");
        FancyInventory inv = new FancyInventory(3, title);
        
        int slotOffset = 0;
        List<TPort> sortedTPortList = TPortManager.getSortedTPortList(tportData, player.getUniqueId());
        for (int i = 0; i < sortedTPortList.size(); i++) {
            
            if (i == 8 || i == 16/*16 because of the slot+slotOffset*/) {
                slotOffset++;
            }
            
            TPort tport = sortedTPortList.get(i);
            if (tport != null) {
                if (tport.isLogged()) {
                    ItemStack is = ItemFactory.toTPortItem(tport, player, List.of(LOG_DATA));
                    inv.setItem(i + slotOffset, is);
                } else {
                    ItemStack is = settings_log_logdata_not_logged_model.getItem(player);
                    
                    Message tportTitle = formatTranslation(infoColor, varInfoColor, "tport.inventories.itemFactory.toTPortItem.title", tport.getName());
                    tportTitle.translateMessage(playerLang);
                    
                    List<Message> lore = new ArrayList<>(tport.getHoverData(false));
                    lore.add(new Message());
                    lore.add(formatInfoTranslation("tport.inventories.itemFactory.toTPortItem.logData.notLogged"));
                    lore = MessageUtils.translateMessage(lore, playerLang);
                    
                    MessageUtils.setCustomItemData(is, colorTheme, tportTitle, lore);
                    
                    inv.setItem(i + slotOffset, is);
                }
            }
        }
        
        inv.setItem(26, createBack(player, LOG_SETTINGS, OWN, MAIN));
        
        inv.open(player);
    }
    public static void openLogGUI(Player player) {
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        JsonObject playerLang = Language.getPlayerLang(player);
        
        ItemStack setTimeZone = settings_log_set_time_zone_model.getItem(player);
        Message timeZoneTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openLogGUI.setTimeZone.title");
        setCustomItemData(setTimeZone, colorTheme, timeZoneTitle, null);
        addFunction(setTimeZone, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> openLogTimeZoneEditor(whoClicked, 0, null)));
        
        ItemStack setTimeFormat = settings_log_set_time_format_model.getItem(player);
        Message timeFormatTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openLogGUI.setTimeFormat.title");
        setCustomItemData(setTimeFormat, colorTheme, timeFormatTitle, null);
        addFunction(setTimeFormat, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> openLogTimeFormatEditor(whoClicked)));
        
        ItemStack logData = settings_log_logdata_model.getItem(player);
        Message logDataTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openLogGUI.logData.title");
        setCustomItemData(logData, colorTheme, logDataTitle, null);
        addFunction(logData, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> openLogDataGUI(whoClicked)));
        
        ItemStack logSize = settings_log_size_model.getItem(player);
        Message logSizeTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openLogGUI.logSize.title");
        Message current = formatInfoTranslation(playerLang, "tport.settingsInventories.openLogGUI.logSize.size", LogSize.getLogSize());
        Message add1 = formatInfoTranslation(playerLang, "tport.settingsInventories.openLogGUI.logSize.add.1", LEFT, "1");
        Message rem1 = formatInfoTranslation(playerLang, "tport.settingsInventories.openLogGUI.logSize.remove.1", RIGHT, "1");
        setCustomItemData(logSize, colorTheme, logSizeTitle, List.of(new Message(), current, new Message(), add1, rem1));
        addCommand(logSize, LEFT, "tport log logSize " + (LogSize.getLogSize() + 1));
        addCommand(logSize, RIGHT, "tport log logSize " + (LogSize.getLogSize() - 1));
        addFunction(logSize, ((whoClicked, clickType, pdc, fancyInventory) -> {
            openLogGUI(whoClicked);
        }), LEFT, RIGHT);
        
        Message invTitle = formatInfoTranslation("tport.settingsInventories.openLogGUI.title");
        FancyInventory inv = new FancyInventory(3, invTitle);
        inv.setItem(10, setTimeZone);
        inv.setItem(11, setTimeFormat);
        inv.setItem(13, logData);
        inv.setItem(15, logSize);
        inv.setItem(17, createBack(player, SETTINGS, OWN, MAIN));
        
        inv.open(player);
    }
    
    private static void openMainSearchGUI(Player player, int page, FancyInventory prevWindow) {
        openMainSearchGUI(player, page);
    }
    public static void openMainSearchGUI(Player player, int page) {
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        JsonObject playerLang = Language.getPlayerLang(player);
        
        ArrayList<ItemStack> items = new ArrayList<>();
        
        for (SearchType searchType : Search.Searchers.getSearchers()) {
            ItemStack is = searchType.getDisplayItem(player);
            
            setStringData(is, new NamespacedKey(Main.getInstance(), "searchType"), searchType.getSearchTypeName());
            
            if (searchType.hasPermission(player, false)) {
                addFunction(is, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
                    if (!CooldownManager.Search.hasCooled(whoClicked, true)) {
                        return;
                    }
                    
                    String innerSearchTypeName = pdc.get(new NamespacedKey(Main.getInstance(), "searchType"), STRING);
                    SearchType innerSearchType = Search.Searchers.getSearcher(innerSearchTypeName);
                    if (innerSearchType.hasSearchMode()) {
                        //open search mode selection -> open query -> open search result
                        openSearchModeGUI(whoClicked, innerSearchType);
                    } else if (innerSearchType.hasQuery()) {
                        //open query -> open search result
                        if (innerSearchType.keyboardQuery()) {
                            openSearchKeyboardQuery(whoClicked, innerSearchType, null, null);
                        } else {
                            openSearchCustomQuery(whoClicked, innerSearchType, null);
                        }
                    } else {
                        //open search result
                        TPortInventories.openSearchGUI(whoClicked, 0, null, innerSearchTypeName, "");
                    }
                }));
            }
            
            Message searchTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openMainSearchGUI.type.title", searchType);
            Message searchDescription = searchType.getDescription().translateMessage(playerLang);
            
            Message searchPermissionState = (searchType.hasPermission(player, false) ?
                    formatTranslation(goodColor, goodColor, "tport.settingsInventories.openMainSearchGUI.permission.do") :
                    formatTranslation(badColor, badColor, "tport.settingsInventories.openMainSearchGUI.permission.dont")
            );
            Message searchPermission = formatInfoTranslation(playerLang, "tport.settingsInventories.openMainSearchGUI.permission", searchPermissionState);
            setCustomItemData(is, colorTheme, searchTitle, Arrays.asList(new Message(), searchDescription, searchPermission));
            
            items.add(is);
        }
        
        Message title = formatInfoTranslation("tport.settingsInventories.openMainSearchGUI.title");
        FancyInventory inv = getDynamicScrollableInventory(player, page, SettingsInventories::openMainSearchGUI,
                title, items, createBack(player, SETTINGS, OWN, MAIN));
        
        inv.open(player);
    }
    private final static FancyInventory.DataName<SearchType> searchTypeDataName = new FancyInventory.DataName<>("searchType", SearchType.class, null);
    private final static FancyInventory.DataName<SearchMode> searchModeDataName = new FancyInventory.DataName<>("searchMode", SearchMode.class, null);
    private final static FancyInventory.DataName<ArrayList> loopedQueryDataName = new FancyInventory.DataName<>("loopedQuery", ArrayList.class, new ArrayList<>());
    private static void openSearchModeGUI(Player player, SearchType searchType) {
        openSearchModeGUI(player, searchType, null);
    }
    private static void openSearchModeGUI(Player player, SearchType searchType, @Nullable String keyboardOutput) {
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        JsonObject playerLang = Language.getPlayerLang(player);
        
        Message title = formatInfoTranslation("tport.settingsInventories.openSearchModeGUI.title");
        FancyInventory inv = new FancyInventory(3, title);
        inv.setData(searchTypeDataName, searchType);
        inv.setData("oldKeyboardOutput", keyboardOutput);
        
        FancyClickRunnable onMode = (whoClicked, clickType, pdc, fancyInventory) -> {
            String searchModeName = pdc.get(new NamespacedKey(Main.getInstance(), "searchMode"), STRING);
            SearchMode searchMode = SearchMode.get(searchModeName);
            SearchType innerSearchType = fancyInventory.getData(searchTypeDataName);
            
            if (innerSearchType.keyboardQuery()) {
                openSearchKeyboardQuery(whoClicked, innerSearchType, searchMode, fancyInventory.getData("oldKeyboardOutput", String.class));
            } else {
                openSearchCustomQuery(whoClicked, innerSearchType, searchMode);
            }
        };
        
        int index = 10;
        for (SearchMode mode : SearchMode.values()) {
            if (searchType.isIntSearch() && !mode.hasIntegerFitter()) continue;
            
            ItemStack modeItem = mode.getInventoryModel().getItem(player);
            setStringData(modeItem, new NamespacedKey(Main.getInstance(), "searchMode"), mode.name());
            Message modeTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openSearchModeGUI.mode.title", mode);
            setCustomItemData(modeItem, colorTheme, modeTitle, Arrays.asList(new Message(), mode.getDescription().translateMessage(playerLang)));
            addFunction(modeItem, LEFT, onMode);
            inv.setItem(index++, modeItem);
        }
        
        inv.setItem(17, createBack(player, SEARCH, OWN, MAIN));
        
        inv.open(player);
    }
    private static void openSearchKeyboardQuery(Player player, SearchType searchType, @Nullable SearchMode searchMode, @Nullable String keyboardOutput) {
        FancyClickRunnable onAccept = (whoClicked, clickType, pdc, keyboard) -> {
            SearchType innerSearchType = keyboard.getData(searchTypeDataName);
            SearchMode innerSearchMode = keyboard.getData(searchModeDataName);
            TPortInventories.openSearchGUI(whoClicked, 0, innerSearchMode, innerSearchType.getSearchTypeName(), getKeyboardOutput(keyboard));
        };
        FancyClickRunnable onReject = (whoClicked, clickType, pdc, keyboard) -> {
            SearchType innerSearchType = keyboard.getData(searchTypeDataName);
            SearchMode innerSearchMode = keyboard.getData(searchModeDataName);
            if (innerSearchMode == null) {
                openMainSearchGUI(whoClicked, 0);
            } else {
                openSearchModeGUI(whoClicked, innerSearchType, getKeyboardOutput(keyboard));
            }
        };
        
        int keyboardSettings = TEXT_ONLY;
        if (searchType.isLoopedQuery()) keyboardSettings += SPACE;
        if (searchType.isIntSearch()) keyboardSettings = NUMBERS;
        
        FancyInventory keyboard = openKeyboard(player, onAccept, onReject, keyboardOutput, null, keyboardSettings);
        keyboard.setData(searchTypeDataName, searchType);
        keyboard.setData(searchModeDataName, searchMode);
    }
    private static void openSearchCustomQuery(Player player, int page, FancyInventory prevWindow) {
        openSearchCustomQuery(player, page, prevWindow.getData(searchTypeDataName), prevWindow.getData(searchModeDataName), prevWindow.getData(loopedQueryDataName));
    }
    private static void openSearchCustomQuery(Player player, SearchType searchType, @Nullable SearchMode searchMode) {
        openSearchCustomQuery(player, 0, searchType, searchMode, new ArrayList<>());
    }
    private static void openSearchCustomQuery(Player player, int page, SearchType searchType, @Nullable SearchMode searchMode, ArrayList<String> looped) { //todo remove 'query' naming
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        JsonObject playerLang = Language.getPlayerLang(player);
        
        ArrayList<ItemStack> items = searchType.queryItems(player);
        
        for (ItemStack is : items) {
            String query = is.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Main.getInstance(), "query"), STRING);
            Message queryTitle = null;
            if (!MessageUtils.hasCustomName(is)) {
                queryTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openSearchCustomQuery.query.title", query);
            }
            ArrayList<Message> lore = new ArrayList<>();
            lore.add(new Message());
            lore.add(formatInfoTranslation(playerLang, "tport.settingsInventories.openSearchCustomQuery.query.click", LEFT, query));
            
            ItemMeta im = is.getItemMeta();
            removeAllFunctions(im);
            removeAllCommands(im);
            
            if (searchType.isLoopedQuery()) {
                if (looped.contains(query)) {
                    Glow.addGlow(im);
                    lore.add(formatInfoTranslation(playerLang, "tport.settingsInventories.openSearchCustomQuery.query.remove", RIGHT, query));
                } else {
                    lore.add(formatInfoTranslation(playerLang, "tport.settingsInventories.openSearchCustomQuery.query.add", RIGHT, query));
                }
                addFunction(im, RIGHT, (whoClicked, clickType, pdc, fancyInventory) -> {
                    SearchType innerSearchType = fancyInventory.getData(searchTypeDataName);
                    SearchMode innerSearchMode = fancyInventory.getData(searchModeDataName);
                    ArrayList<String> innerLooped = fancyInventory.getData(loopedQueryDataName);
                    String innerQuery = pdc.get(new NamespacedKey(Main.getInstance(), "query"), STRING);
                    if (innerLooped.contains(innerQuery)) innerLooped.remove(innerQuery);
                    else innerLooped.add(innerQuery);
                    openSearchCustomQuery(whoClicked, fancyInventory.getData(pageDataName), innerSearchType, innerSearchMode, innerLooped);
                });
            }
            addFunction(im, LEFT, (whoClicked, clickType, pdc, fancyInventory) -> {
                SearchType innerSearchType = fancyInventory.getData(searchTypeDataName);
                SearchMode innerSearchMode = fancyInventory.getData(searchModeDataName);
                ArrayList<String> innerLooped = fancyInventory.getData(loopedQueryDataName);
                String innerQuery = pdc.get(new NamespacedKey(Main.getInstance(), "query"), STRING);
                innerLooped.add(innerQuery);
                TPortInventories.openSearchGUI(whoClicked, 0, innerSearchMode, innerSearchType.getSearchTypeName(), String.join(" ", innerLooped));
            });
            
            is.setItemMeta(im);
            
            setCustomItemData(is, colorTheme, queryTitle, lore);
        }
        
        Message title = formatInfoTranslation("tport.settingsInventories.openSearchCustomQuery.title");
        FancyInventory inv = getDynamicScrollableInventory(player, page, SettingsInventories::openSearchCustomQuery, title, items, createBack(player, SEARCH, OWN, MAIN));
        inv.setData(searchTypeDataName, searchType);
        inv.setData(searchModeDataName, searchMode);
        inv.setData(loopedQueryDataName, looped);
        
        inv.open(player);
    }
    
    private static void openAdapterGUI(Player player, int page, @Nullable FancyInventory prevWindow) {
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        JsonObject playerLang = Language.getPlayerLang(player);
        
        String selectedAdapterName = Adapter.getSelectedAdapter();
        String loadedAdapterName = Adapter.getLoadedAdapter();
        
        ArrayList<ItemStack> items = new ArrayList<>();
        for (String adapter : Iterables.concat(List.of(Adapter.automatic), Adapter.adapters.keySet())) {
            ItemStack is = settings_adapter_adapter_model.getItem(player);
            
            addCommand(is, LEFT, "tport adapter " + adapter);
            addFunction(is, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> openAdapterGUI(whoClicked, fancyInventory.getData(pageDataName), fancyInventory)));
            
            Message adapterTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openAdapterGUI.adapter.title", adapter);
            ArrayList<Message> lore = new ArrayList<>();
            lore.add(new Message());
            lore.add(formatInfoTranslation(playerLang, "tport.settingsInventories.openAdapterGUI.adapter.select", LEFT));
            
            int index = items.size();
            if (adapter.equals(selectedAdapterName)) {
                Glow.addGlow(is);
                lore.add(formatInfoTranslation(playerLang, "tport.settingsInventories.openAdapterGUI.adapter.selected"));
                index = 0;
            }
            if (adapter.equals(loadedAdapterName)) {
                Glow.addGlow(is);
                lore.add(formatInfoTranslation(playerLang, "tport.settingsInventories.openAdapterGUI.adapter.loaded"));
                index = 0;
            }
            
            setCustomItemData(is, colorTheme, adapterTitle, lore);
            items.add(index, is);
        }
        
        Message title = formatInfoTranslation("tport.settingsInventories.openAdapterGUI.title");
        FancyInventory inv = getDynamicScrollableInventory(player, page, SettingsInventories::openAdapterGUI, title, items, createBack(player, SETTINGS, OWN, MAIN));
        
        inv.open(player);
    }
    
    
    
    private static void openBiomeTPSettingsGUI(Player player) {
        openBiomeTPSettingsGUI(player, 0, null);
    }
    private static void openBiomeTPSettingsGUI(Player player, int page, @Nullable FancyInventory prevWindow) {
        ColorTheme colorTheme = ColorTheme.getTheme(player);
        JsonObject playerLang = Language.getPlayerLang(player.getUniqueId());
        
        ArrayList<ItemStack> items = new ArrayList<>();
        
        ArrayList<String> biomeSelection = new ArrayList<>();
        
        for (String biome : BiomeTP.availableBiomes()) {
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
            MessageUtils.setCustomItemData(item, colorTheme, biomeTitle, List.of(biomeLClick));
            
            ItemMeta im = item.getItemMeta();
            im.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "biome"), PersistentDataType.STRING, biome);
            if (selected) Glow.addGlow(im);
            
            FancyClickEvent.addFunction(im, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> {
            
            }));
            
            item.setItemMeta(im);
            
            items.add(item);
        }
        
        
        FancyInventory inv = getDynamicScrollableInventory(player, page, SettingsInventories::openBiomeTPSettingsGUI,
                "", items, createBack(player, SETTINGS, OWN, MAIN));
        
        inv.open(player);
    }
    
    private static void openLanguageGUI(Player player) {
        //todo create language setting
        /*
         * /tport language server [server]
         * /tport language get
         * /tport language set <custom|server|language>
         * /tport language repair <language> [repair with]
         */
        
    }
    
    public static void openSettingsGUI(Player player, int page, @Nullable FancyInventory prevWindow) {
        ColorTheme colorTheme = getTheme(player);
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        
        ItemStack versionItem = settings_version_model.getItem(player);
        addCommand(versionItem, LEFT, "tport version");
        Message versionTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openSettingsGUI.version.title");
        Message versionLore = formatInfoTranslation(playerLang, "tport.settingsInventories.openSettingsGUI.version.currentVersion", Version.getCurrentVersion());
        Message infoLore = formatInfoTranslation(playerLang, "tport.settingsInventories.openSettingsGUI.version.moreInfo", LEFT);
        setCustomItemData(versionItem, colorTheme, versionTitle, List.of(new Message(), versionLore, infoLore));
        
        ItemStack reloadItem = settings_reload_model.getItem(player);
        addCommand(reloadItem, LEFT, "tport reload");
        Message reloadTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openSettingsGUI.reload.title");
        setCustomItemData(reloadItem, colorTheme, reloadTitle, null);
        
        ItemStack logItem = settings_log_model.getItem(player);
        Message logTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openSettingsGUI.log.title");
        setCustomItemData(logItem, colorTheme, logTitle, null);
        addFunction(logItem, LEFT, (whoClicked, clickType, pdc, fancyInventory) -> openLogGUI(whoClicked));
        
        ItemStack adapterItem = settings_adapter_model.getItem(player);
        Message adapterTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openSettingsGUI.adapter.title");
        setCustomItemData(adapterItem, colorTheme, adapterTitle, null);
        addFunction(adapterItem, LEFT, (whoClicked, clickType, pdc, fancyInventory) -> openAdapterGUI(whoClicked, 0, null));
        
        ItemStack backupItem = settings_backup_model.getItem(player);
        addFunction(backupItem, LEFT, (whoClicked, clickType, pdc, fancyInventory) -> openBackupGUI(whoClicked));
        Message backupTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openSettingsGUI.backup.title");
        setCustomItemData(backupItem, colorTheme, backupTitle, null);
        
        ItemStack biomeTPItem = settings_biome_tp_model.getItem(player);
        Message biomeTPTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openSettingsGUI.biomeTP.title");
        setCustomItemData(biomeTPItem, colorTheme, biomeTPTitle, List.of(new Message(), formatInfoTranslation(playerLang, "tport.comingSoon")));
//        addFunction(biomeTPItem, LEFT, (whoClicked, clickType, pdc, fancyInventory) -> openBiomeTPSettingsGUI(whoClicked));
        //todo add legacy BiomeTP state
        //todo make use of BiomeTP legacy icon
        //todo create biomeTP setting
        /*
        * /tport biomeTP accuracy [size] //todo maybe set per player
        *
        * /tport features biomeTP state [state]
        * */
        
        ItemStack delayItem = settings_delay_model.getItem(player);
        Message delayTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openSettingsGUI.delay.title");
        setCustomItemData(delayItem, colorTheme, delayTitle, List.of(new Message(), formatInfoTranslation(playerLang, "tport.comingSoon")));
        //todo create delay setting
        /*
        * /tport delay handler [state]
        * /tport delay set <player> <delay>
        * /tport delay get [player]
        *
        * dynamic scroll with players
        * add toggle button for permissions <-> commands
        * */
        
        ItemStack restrictionItem = settings_restriction_model.getItem(player);
        Message restrictionTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openSettingsGUI.restriction.title");
        setCustomItemData(restrictionItem, colorTheme, restrictionTitle, List.of(new Message(), formatInfoTranslation(playerLang, "tport.comingSoon")));
        //todo create restriction setting
        /*
         * /tport restriction handler [state]
         * /tport restriction set <player> <delay>
         * /tport restriction get [player]
         *
         * dynamic scroll with players
         * add toggle button for permissions <-> commands
         * */
        
        ItemStack dynmapItem = settings_dynmap_model.getItem(player);
        Message dynmapTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openSettingsGUI.dynmap.title");
        setCustomItemData(dynmapItem, colorTheme, dynmapTitle, List.of(new Message(), formatInfoTranslation(playerLang, "tport.comingSoon")));
        //todo create dynmap setting
        /*
        * /tport dynmap ip [IP]
        * /tport dynmap colors
        *
        * /tport features dynmap state [state]
        * */
        
        ItemStack blueMapItem = settings_bluemap_model.getItem(player);
        Message blueMapTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openSettingsGUI.blueMap.title");
        setCustomItemData(blueMapItem, colorTheme, blueMapTitle, List.of(new Message(), formatInfoTranslation(playerLang, "tport.comingSoon")));
        //todo create blueMap setting
        /*
        * /tport blueMap ip [IP]
        * /tport blueMap colors
        *
        * /tport features blueMap state [state]
        * */
        
        ItemStack particleAnimationItem = settings_particle_animation_model.getItem(player);
        Message particleAnimationTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openSettingsGUI.particleAnimation.title");
        setCustomItemData(particleAnimationItem, colorTheme, particleAnimationTitle, List.of(new Message(), formatInfoTranslation(playerLang, "tport.comingSoon")));
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
        Message pltpTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openSettingsGUI.PLTP.title");
        setCustomItemData(pltpItem, colorTheme, pltpTitle, null);
        addFunction(pltpItem, LEFT, (whoClicked, clickType, pdc, fancyInventory) -> openPLTPGUI(whoClicked));
        
        ItemStack publicItem = settings_public_model.getItem(player);
        Message publicTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openSettingsGUI.public.title");
        setCustomItemData(publicItem, colorTheme, publicTitle, null);
        addFunction(publicItem, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> openPublicTPSettings(whoClicked)));
        
        ItemStack resourcePackItem = settings_resource_pack_model.getItem(player);
        Message resourcePackTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openSettingsGUI.resourcePack.title");
        setCustomItemData(resourcePackItem, colorTheme, resourcePackTitle, null);
        addFunction(resourcePackItem, LEFT, (whoClicked, clickType, pdc, fancyInventory) -> openResourcePackGUI(whoClicked));
        
        ItemStack tagItem = settings_tag_model.getItem(player);
        Message tagTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openSettingsGUI.tag.title");
        setCustomItemData(tagItem, colorTheme, tagTitle, null);
        addFunction(tagItem, LEFT, (whoClicked, clickType, pdc, fancyInventory) -> openTagGUI(whoClicked, 0, null));
        
        ItemStack transferItem = settings_transfer_model.getItem(player);
        Message transferTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openSettingsGUI.transfer.title");
        setCustomItemData(transferItem, colorTheme, transferTitle, null);
        addFunction(transferItem, LEFT, (whoClicked, clickType, pdc, fancyInventory) -> openTransferGUI(whoClicked, 0, null));
        
        ItemStack featuresItem = settings_features_model.getItem(player);
        Message featuresTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openSettingsGUI.features.title");
        setCustomItemData(featuresItem, colorTheme, featuresTitle, null);
        addFunction(featuresItem, LEFT, (whoClicked, clickType, pdc, fancyInventory) -> openFeaturesGUI(whoClicked, 0, null));
        
        ItemStack metricsItem;
        Message metricsViewStats = null;
        Message stateMessage;
        if (Features.Feature.Metrics.isEnabled()) {
            metricsItem = settings_features_metrics_model.getItem(player);
            addCommand(metricsItem, LEFT, "tport features metrics state false");
            addCommand(metricsItem, RIGHT, "tport metrics viewStats");
            addFunction(metricsItem, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> openSettingsGUI(whoClicked, fancyInventory.getData(pageDataName), null)));
            stateMessage = formatTranslation(varInfoColor, varInfoColor, "tport.settingsInventories.openSettingsGUI.metrics.changeState.disable");
            metricsViewStats = formatInfoTranslation(playerLang, "tport.settingsInventories.openSettingsGUI.metrics.viewStats", RIGHT);
        } else {
            metricsItem = settings_features_metrics_grayed_model.getItem(player);
            addCommand(metricsItem, LEFT, "tport features metrics state true");
            addFunction(metricsItem, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> openSettingsGUI(whoClicked, fancyInventory.getData(pageDataName), null)));
            
            stateMessage = formatTranslation(varInfoColor, varInfoColor, "tport.settingsInventories.openSettingsGUI.metrics.changeState.enable");
        }
        Message metricsChangeState = formatInfoTranslation(playerLang, "tport.settingsInventories.openSettingsGUI.metrics.changeState", LEFT, stateMessage);
        Message metricsTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openSettingsGUI.metrics.title");
        setCustomItemData(metricsItem, colorTheme, metricsTitle, Arrays.asList(new Message(), metricsChangeState, metricsViewStats));
        
        ItemStack redirectItem = settings_redirect_model.getItem(player);
        Message redirectTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openSettingsGUI.redirect.title");
        setCustomItemData(redirectItem, colorTheme, redirectTitle, null);
        addFunction(redirectItem, LEFT, (whoClicked, clickType, pdc, fancyInventory) -> openRedirectsGUI(whoClicked, 0, null));
        
        ItemStack removePlayerItem = settings_remove_player_model.getItem(player);
        Message removePlayerTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openSettingsGUI.removePlayer.title");
        setCustomItemData(removePlayerItem, colorTheme, removePlayerTitle, null);
        addFunction(removePlayerItem, LEFT, (whoClicked, clickType, pdc, fancyInventory) -> openRemovePlayerGUI(whoClicked, 0, null));
        
        ItemStack safetyCheckItem = settings_safety_check_model.getItem(player);
        Message safetyCheckTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openSettingsGUI.safetyCheck.title");
        setCustomItemData(safetyCheckItem, colorTheme, safetyCheckTitle, null);
        addFunction(safetyCheckItem, LEFT, (whoClicked, clickType, pdc, fancyInventory) -> openSafetyCheckGUI(whoClicked, 0, null));
        
        ItemStack languageItem = settings_language_model.getItem(player);
        Message languageTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openSettingsGUI.language.title");
        setCustomItemData(languageItem, colorTheme, languageTitle, List.of(new Message(), formatInfoTranslation(playerLang, "tport.comingSoon")));
        addFunction(languageItem, LEFT, (whoClicked, clickType, pdc, fancyInventory) -> openLanguageGUI(whoClicked));
        
        ItemStack colorThemeItem = settings_color_theme_model.getItem(player);
        Message colorThemeTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openSettingsGUI.colorTheme.title");
        addFunction(colorThemeItem, LEFT, (whoClicked, clickType, pdc, fancyInventory) -> openTPortColorThemeGUI(whoClicked));
        setCustomItemData(colorThemeItem, colorTheme, colorThemeTitle, null);
        
        ItemStack cooldownItem = settings_cooldown_model.getItem(player);
        Message cooldownTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openSettingsGUI.cooldown.title");
        addFunction(cooldownItem, LEFT, (whoClicked, clickType, pdc, fancyInventory) -> openTPortCooldownGUI(whoClicked));
        setCustomItemData(cooldownItem, colorTheme, cooldownTitle, null);
        
        ItemStack restoreItem = settings_restore_model.getItem(player);
        Message restoreTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openSettingsGUI.restore.title");
        TPort restoreTPort = Restore.getRestoreTPort(player.getUniqueId());
        Message restoreLore;
        Message displayItemMessage = null;
        if (restoreTPort == null) {
            restoreLore = formatInfoTranslation(playerLang, "tport.settingsInventories.openSettingsGUI.restore.hasNoRestore");
        } else {
            restoreLore = formatInfoTranslation(playerLang, "tport.settingsInventories.openSettingsGUI.restore.clickToRestore", LEFT, asTPort(restoreTPort));
            displayItemMessage = formatInfoTranslation(playerLang, "tport.settingsInventories.openSettingsGUI.restore.displayItemMessage");
            addCommand(restoreItem, LEFT, "tport restore");
        }
        setCustomItemData(restoreItem, colorTheme, restoreTitle, Arrays.asList(new Message(), restoreLore, displayItemMessage));
        
        ItemStack homeItem = settings_home_model.getItem(player);
        Message homeTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openSettingsGUI.home.title");
        addFunction(homeItem, LEFT, (whoClicked, clickType, pdc, fancyInventory) -> openHomeEditGUI(whoClicked));
        TPort homeTPortObject = Home.getHome(player);
        List<Message> homeLore = new ArrayList<>();
        if (homeTPortObject == null) {
            homeLore.add(formatTranslation(varInfoColor, varInfo2Color, "tport.settingsInventories.openSettingsGUI.home.unknown"));
        } else {
            homeLore.add(formatInfoTranslation("tport.settingsInventories.openSettingsGUI.home.tportName", homeTPortObject.getName()));
            homeLore.addAll(homeTPortObject.getHoverData(true));
        }
        homeLore.add(new Message());
        homeLore.add(formatInfoTranslation("tport.settingsInventories.openSettingsGUI.home.setHome", LEFT));
        homeLore = MessageUtils.translateMessage(homeLore, playerLang);
        setCustomItemData(homeItem, colorTheme, homeTitle, homeLore);
        
        ItemStack searchItem = settings_search_model.getItem(player);
        Message searchTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openSettingsGUI.search.title");
        setCustomItemData(searchItem, colorTheme, searchTitle, null);
        addFunction(searchItem, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> openMainSearchGUI(whoClicked, 0)));
        
        ItemStack itemDebugItem = new ItemStack(Material.DIAMOND_BLOCK);
        Message itemDebugTitle = formatInfoTranslation(playerLang, "tport.settingsInventories.openSettingsGUI.itemDebug.title");
        setCustomItemData(itemDebugItem, colorTheme, itemDebugTitle, null);
        addFunction(itemDebugItem, LEFT, ((whoClicked, clickType, pdc, fancyInventory) -> openItemDebug(whoClicked)));
        
        ArrayList<ItemStack> items = new ArrayList<>();
        //useful for users
        items.add(versionItem);         //DONE
        items.add(restoreItem);         //DONE
        items.add(resourcePackItem);    //DONE
        items.add(colorThemeItem);      //DONE
        items.add(transferItem);        //DONE (todo test)
        items.add(pltpItem);            //DONE
        items.add(logItem);             //DONE
        items.add(particleAnimationItem);
        items.add(safetyCheckItem);     //DONE
        items.add(homeItem);            //DONE
        items.add(languageItem);
        items.add(searchItem);          //DONE
//        items.add(requests);          //todo
        
        //useful for admins
        items.add(reloadItem);          //DONE
        items.add(featuresItem);        //DONE
        items.add(biomeTPItem);
        items.add(tagItem);             //DONE
        items.add(backupItem);          //DONE
        items.add(removePlayerItem);    //DONE
        items.add(redirectItem);        //DONE
        items.add(metricsItem);         //DONE
        items.add(cooldownItem);        //DONE
        items.add(dynmapItem);
        items.add(blueMapItem);
        items.add(publicItem);          //DONE
        items.add(adapterItem);         //DONE
        items.add(delayItem);
        items.add(restrictionItem);
        items.add(itemDebugItem);       //DONE
        
        Message title = formatInfoTranslation("tport.settingsInventories.openSettingsGUI.title");
        FancyInventory inv = getDynamicScrollableInventory(player, page,
                SettingsInventories::openSettingsGUI,
                title,
                items, createBack(player, MAIN, OWN, null));
        
        inv.open(player);
        
        //tport teleporter                          - add in settings, configuration now with inventories
        //tport cancel
    }
}
