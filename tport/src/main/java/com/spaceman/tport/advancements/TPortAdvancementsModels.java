package com.spaceman.tport.advancements;

import com.spaceman.tport.fancyMessage.inventories.InventoryModel;
import com.spaceman.tport.inventories.SettingsInventories;
import org.bukkit.Material;

public class TPortAdvancementsModels {

    
    public static final InventoryModel advancement_background_model                  = new InventoryModel(Material.OAK_BUTTON, SettingsInventories.last_model_id + 1, "tport", "advancement_background", "advancement");
    public static final InventoryModel advancement_tport_model                       = new InventoryModel(Material.OAK_BUTTON, advancement_background_model, "tport", "advancement_tport", "advancement");
    public static final InventoryModel advancement_saved_model                       = new InventoryModel(Material.OAK_BUTTON, advancement_tport_model, "tport", "advancement_saved", "advancement");
    public static final InventoryModel advancement_familiar_model                    = new InventoryModel(Material.OAK_BUTTON, advancement_saved_model, "tport", "advancement_familiar", "advancement");
    public static final InventoryModel advancement_quick_edit_model                  = new InventoryModel(Material.OAK_BUTTON, advancement_familiar_model, "tport", "advancement_quick_edit", "advancement");
    public static final InventoryModel advancement_quick_edit_not_my_style_model     = new InventoryModel(Material.OAK_BUTTON, advancement_quick_edit_model, "tport", "advancement_quick_edit_not_my_style", "advancement");
    public static final InventoryModel advancement_quick_edit_safety_first_model     = new InventoryModel(Material.OAK_BUTTON, advancement_quick_edit_not_my_style_model, "tport", "advancement_quick_edit_safety", "advancement");
    public static final InventoryModel advancement_moving_truck_coming_through_model = new InventoryModel(Material.OAK_BUTTON, advancement_quick_edit_safety_first_model, "tport", "advancement_moving_truck_coming_through", "advancement");
    public static final InventoryModel advancement_home_sweet_home_model             = new InventoryModel(Material.OAK_BUTTON, advancement_moving_truck_coming_through_model, "tport", "advancement_home_sweet_home", "advancement");
    public static final InventoryModel advancement_back_to_the_future_model          = new InventoryModel(Material.OAK_BUTTON, advancement_home_sweet_home_model, "tport", "advancement_back_to_the_future", "advancement");
    public static final InventoryModel advancement_what_is_mine_is_yours_model       = new InventoryModel(Material.OAK_BUTTON, advancement_back_to_the_future_model, "tport", "advancement_what_is_mine_is_yours", "advancement");
    public static final InventoryModel advancement_what_is_yours_is_mine_model       = new InventoryModel(Material.OAK_BUTTON, advancement_what_is_mine_is_yours_model, "tport", "advancement_what_is_yours_is_mine", "advancement");
    public static final InventoryModel advancement_i_dont_need_you_anymore_model     = new InventoryModel(Material.OAK_BUTTON, advancement_what_is_yours_is_mine_model, "tport", "advancement_i_dont_need_you_anymore", "advancement");
    public static final InventoryModel advancement_whoops_model                      = new InventoryModel(Material.OAK_BUTTON, advancement_i_dont_need_you_anymore_model, "tport", "advancement_whoops", "advancement");
    public static final InventoryModel advancement_oh_no_my_buttons_model            = new InventoryModel(Material.OAK_BUTTON, advancement_whoops_model, "tport", "advancement_oh_no_my_buttons", "advancement");
    public static final InventoryModel advancement_pretty_colors_model               = new InventoryModel(Material.OAK_BUTTON, advancement_oh_no_my_buttons_model, "tport", "advancement_pretty_colors", "advancement");
    public static final InventoryModel advancement_que_model                         = new InventoryModel(Material.OAK_BUTTON, advancement_pretty_colors_model, "tport", "advancement_que", "advancement");
    public static final InventoryModel advancement_lost_and_found_model              = new InventoryModel(Material.OAK_BUTTON, advancement_que_model, "tport", "advancement_lost_and_found", "advancement");
    public static final InventoryModel advancement_tagged_you_are_it_model           = new InventoryModel(Material.OAK_BUTTON, advancement_lost_and_found_model, "tport", "advancement_tagged_you_are_it", "advancement");
    public static final InventoryModel advancement_im_watching_you_model             = new InventoryModel(Material.OAK_BUTTON, advancement_tagged_you_are_it_model, "tport", "advancement_im_watching_you", "advancement");
    public static final InventoryModel advancement_im_watching_me_model              = new InventoryModel(Material.OAK_BUTTON, advancement_im_watching_you_model, "tport", "advancement_im_watching_me", "advancement");
    public static final InventoryModel advancement_im_helping_model                  = new InventoryModel(Material.OAK_BUTTON, advancement_im_watching_me_model, "tport", "advancement_im_helping", "advancement");
    public static final InventoryModel advancement_i_can_see_it_all_model            = new InventoryModel(Material.OAK_BUTTON, advancement_im_helping_model, "tport", "advancement_i_can_see_it_all", "advancement");
    public static final InventoryModel advancement_too_fast_model                    = new InventoryModel(Material.OAK_BUTTON, advancement_i_can_see_it_all_model, "tport", "advancement_too_fast", "advancement");
    public static final InventoryModel advancement_world_tp_model                    = new InventoryModel(Material.OAK_BUTTON, advancement_too_fast_model, "tport", "advancement_world_tp", "advancement");
    public static final InventoryModel advancement_a_whole_new_world_model           = new InventoryModel(Material.OAK_BUTTON, advancement_world_tp_model, "tport", "advancement_a_whole_new_world", "advancement");
    public static final InventoryModel advancement_biome_tp_model                    = new InventoryModel(Material.OAK_BUTTON, advancement_a_whole_new_world_model, "tport", "advancement_biome_tp", "advancement");
    public static final InventoryModel advancement_biome_certainty_model             = new InventoryModel(Material.OAK_BUTTON, advancement_biome_tp_model, "tport", "advancement_biome_certainty", "advancement");
    public static final InventoryModel advancement_biome_surprise_model              = new InventoryModel(Material.OAK_BUTTON, advancement_biome_certainty_model, "tport", "advancement_biome_surprise", "advancement");
    public static final InventoryModel advancement_biome_i_dont_care_model           = new InventoryModel(Material.OAK_BUTTON, advancement_biome_surprise_model, "tport", "advancement_biome_i_dont_care", "advancement");
    public static final InventoryModel advancement_biome_one_is_not_enough_model     = new InventoryModel(Material.OAK_BUTTON, advancement_biome_i_dont_care_model, "tport", "advancement_biome_one_is_not_enough", "advancement");
    public static final InventoryModel advancement_feature_tp_model                  = new InventoryModel(Material.OAK_BUTTON, advancement_biome_one_is_not_enough_model, "tport", "advancement_feature_tp", "advancement");
    public static final InventoryModel advancement_feature_certainty_model           = new InventoryModel(Material.OAK_BUTTON, advancement_feature_tp_model, "tport", "advancement_feature_certainty", "advancement");
    public static final InventoryModel advancement_feature_surprise_model            = new InventoryModel(Material.OAK_BUTTON, advancement_feature_certainty_model, "tport", "advancement_feature_surprise", "advancement");
    public static final InventoryModel advancement_feature_one_is_not_enough_model   = new InventoryModel(Material.OAK_BUTTON, advancement_feature_surprise_model, "tport", "advancement_feature_one_is_not_enough", "advancement");
    public static final InventoryModel advancement_caves_and_cliffs_part_iii_model   = new InventoryModel(Material.OAK_BUTTON, advancement_feature_one_is_not_enough_model, "tport", "advancement_caves_and_cliffs_part_iii", "advancement");
    public static final InventoryModel advancement_look_me_in_the_eyes_model         = new InventoryModel(Material.OAK_BUTTON, advancement_caves_and_cliffs_part_iii_model, "tport", "advancement_look_me_in_the_eyes", "advancement");
    public static final int last_model_id = advancement_look_me_in_the_eyes_model.getCustomModelData();
    
}
