package com.spaceman.tport.advancements;

import com.spaceman.tport.commands.tport.Features;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.MessageUtils;
import com.spaceman.tport.fancyMessage.inventories.InventoryModel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;

import static com.spaceman.tport.advancements.TPortAdvancementManager.AdvancementFrame.CHALLENGE;
import static com.spaceman.tport.advancements.TPortAdvancementManager.AdvancementFrame.TASK;
import static com.spaceman.tport.advancements.TPortAdvancementManager.AdvancementVisibility.*;
import static com.spaceman.tport.advancements.TPortAdvancementManager.getOrCreateManager;
import static com.spaceman.tport.advancements.TPortAdvancementsModels.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatTranslation;

public record TPortAdvancement(InventoryModel model, String translationID,
                               TPortAdvancementManager.AdvancementFrame frame, TPortAdvancementManager.AdvancementVisibility visibility,
                               float x, float y,
                               TPortAdvancement parent, String key, boolean register) implements MessageUtils.MessageDescription {
    
    public static ArrayList<TPortAdvancement> tportAdvancements = new ArrayList<>();
    
    public static boolean isActive() {
        if (Features.Feature.Advancements.isDisabled()) {
            return false;
        }
        
        return Bukkit.getPluginManager().getPlugin("CrazyAdvancementsAPI") != null;
    }
    
    public static void onStateChange(boolean newState) {
        if (newState) {
            if (TPortAdvancement.isActive())
                for (Player player : Bukkit.getOnlinePlayers())
                    getOrCreateManager(player);
        } else {
            if (TPortAdvancement.isActive())
                for (Player player : Bukkit.getOnlinePlayers())
                    TPortAdvancementManager.removeAdvancementManager(player);
        }
    }
    
    public TPortAdvancement(InventoryModel model,
                            String translationID,
                            TPortAdvancementManager.AdvancementFrame frame, TPortAdvancementManager.AdvancementVisibility visibility,
                            float x, float y,
                            TPortAdvancement parent, String key) {

        this(model, translationID, frame, visibility, x, y, parent, key, true);
    }
    
    public TPortAdvancement(InventoryModel model,
                             String translationID,
                            TPortAdvancementManager.AdvancementFrame frame, TPortAdvancementManager.AdvancementVisibility visibility,
                             float x, float y,
                             TPortAdvancement parent, String key, boolean register) {

        this.model = model;
        this.translationID = translationID;
        this.frame = frame;
        this.visibility = visibility;
        this.x = x;
        this.y = y;
        this.parent = parent;
        this.key = key;
        this.register = register;

        if (register) tportAdvancements.add(this);
    }
    
    public void grant(Player player) {
        if (!isActive()) return;
        
        TPortAdvancementManager.grantAdvancement(player, this);
    }
    
    @Override
    public Message getDescription() {
        return formatTranslation(ChatColor.GREEN, ChatColor.GREEN, "tport.advancements." + translationID + ".description");
    }
    
    @Override
    public Message getName(String color, String varColor) {
        return formatTranslation(varColor, varColor, "tport.advancements." + translationID + ".title");
    }
    
    @Override
    public String getInsertion() {
        return "";
    }
    
    public static TPortAdvancement Advancement_TPort = new TPortAdvancement(advancement_tport_model, "Advancement_tport",
            TASK, ALWAYS,
            0, 0,
            null,
            "tport",
            false);
    
    // Saved! (Create your first TPort)
    //    Familiar (Teleport to one of your own TPorts)
    //    Quick Edit (Open the Quick Editor)
    //       Not My Style (Change the title/description of a TPort)
    //       Safety First (Change a safety feature of a TPort)
    public static TPortAdvancement Advancement_saved = new TPortAdvancement(advancement_saved_model, "Advancement_saved",
            TASK, ALWAYS,
            1, 0,
            null,
            "saved");
    public static TPortAdvancement Advancement_familiar = new TPortAdvancement(advancement_familiar_model, "Advancement_familiar",
            TASK, ALWAYS,
            2, -1,
            Advancement_saved,
            "familiar");
    
    public static TPortAdvancement Advancement_quickEdit = new TPortAdvancement(advancement_quick_edit_model, "Advancement_quickEdit",
            TASK, ALWAYS,
            2, 0,
            Advancement_saved,
            "quickEdit");
    public static TPortAdvancement Advancement_notMyStyle = new TPortAdvancement(advancement_quick_edit_not_my_style_model, "Advancement_notMyStyle",
            TASK, ALWAYS,
            3, -1,
            Advancement_quickEdit,
            "notMyStyle");
    public static TPortAdvancement Advancement_safetyFirst = new TPortAdvancement(advancement_quick_edit_safety_first_model, "Advancement_safetyFirst",
            TASK, ALWAYS,
            3, 0,
            Advancement_quickEdit,
            "safetyFirst");
    
    
    // Moving Truck Coming Through (Set your home)
    //    Home Sweet Home (Teleport to your set home)
    public static TPortAdvancement Advancement_movingTruckComingThrough = new TPortAdvancement(advancement_moving_truck_coming_through_model, "Advancement_movingTruckComingThrough",
            TASK, ALWAYS,
            1, 1,
            null,
            "MovingTruckComingThrough");
    public static TPortAdvancement Advancement_homeSweetHome = new TPortAdvancement(advancement_home_sweet_home_model, "Advancement_homeSweetHome",
            TASK, ALWAYS,
            2, 1,
            Advancement_movingTruckComingThrough,
            "HomeSweetHome");
    
    
    // Back to the future (Use /tport back or /tport history)
    public static TPortAdvancement Advancement_backToTheFuture = new TPortAdvancement(advancement_back_to_the_future_model, "Advancement_backToTheFuture",
            TASK, ALWAYS,
            1, 2,
            null,
            "BackToTheFuture");
    
    
    // What's mine, is yours (Create a Public TPort)
    //   What's yours, is mine (Teleport to a Public TPort that is not one of your own)
    public static TPortAdvancement Advancement_whatsMineIsYours = new TPortAdvancement(advancement_what_is_mine_is_yours_model, "Advancement_whatsMineIsYours",
            TASK, ALWAYS,
            1, 3,
            null,
            "WhatsMineIsYours");
    public static TPortAdvancement Advancement_whatsYoursIsMine = new TPortAdvancement(advancement_what_is_yours_is_mine_model, "Advancement_whatsYoursIsMine",
            TASK, ALWAYS,
            2, 3,
            Advancement_whatsMineIsYours,
            "WhatsYoursIsMine");
    
    // I Don't Need You Anymore (Remove a TPort)
    //    Whoops (Restore an accidentally deleted TPort)
    public static TPortAdvancement Advancement_iDontNeedYouAnymore = new TPortAdvancement(advancement_i_dont_need_you_anymore_model, "Advancement_iDontNeedYouAnymore",
            TASK, ALWAYS,
            1, 4,
            null,
            "iDontNeedYouAnymore");
    public static TPortAdvancement Advancement_whoops = new TPortAdvancement(advancement_whoops_model, "Advancement_whoops",
            TASK, ALWAYS,
            2, 4,
            Advancement_iDontNeedYouAnymore,
            "Whoops");
    
    // Oh No, My Buttons (Use a Resource pack)
    // Pretty Colors (Change your Color Theme)
    // Que? (Change your language)
    public static TPortAdvancement Advancement_PrettyColors = new TPortAdvancement(advancement_pretty_colors_model, "Advancement_PrettyColors",
            TASK, ALWAYS,
            1, 5,
            null,
            "PrettyColors");
    public static TPortAdvancement Advancement_OhNoMyButtons = new TPortAdvancement(advancement_oh_no_my_buttons_model, "Advancement_OhNoMyButtons",
            TASK, ALWAYS,
            2, 5,
            Advancement_PrettyColors,
            "OhNoMyButtons");
    public static TPortAdvancement Advancement_Que = new TPortAdvancement(advancement_que_model, "Advancement_Que",
            TASK, ALWAYS,
            3, 5,
            Advancement_OhNoMyButtons,
            "Que");
    
    // Lost And Found (Use the search)
    public static TPortAdvancement Advancement_LostAndFound = new TPortAdvancement(advancement_lost_and_found_model, "Advancement_LostAndFound",
            TASK, ALWAYS,
            1, 6,
            null,
            "LostAndFound");
    
    // Tagged, you're it (Tag one of your TPorts)
    public static TPortAdvancement Advancement_TaggedYoureIt = new TPortAdvancement(advancement_tagged_you_are_it_model, "Advancement_TaggedYoureIt",
            TASK, ALWAYS,
            1, 7,
            null,
            "TaggedYoureIt");
    
    // I'm Watching You (Start logging a player)
    // I'm Watching Me (Start logging yourself)
    public static TPortAdvancement Advancement_ImWatchingYou = new TPortAdvancement(advancement_im_watching_you_model, "Advancement_ImWatchingYou",
            TASK, ALWAYS,
            1, 8,
            null,
            "ImWatchingYou");
    public static TPortAdvancement Advancement_ImWatchingMe = new TPortAdvancement(advancement_im_watching_you_model, "Advancement_ImWatchingMe",
            CHALLENGE, HIDDEN,
            2, 8,
            Advancement_ImWatchingYou,
            "ImWatchingMe");
    
    // I'm Helping (use the /tport help)
    public static TPortAdvancement Advancement_ImHelping = new TPortAdvancement(advancement_im_helping_model, "Advancement_ImHelping",
            TASK, ALWAYS,
            1, 9,
            null,
            "ImHelping");
    
    // I Can See It All (Dynmap / BlueMap)
    public static TPortAdvancement Advancement_ICanSeeItAll = new TPortAdvancement(advancement_i_can_see_it_all_model, "Advancement_ICanSeeItAll",
            TASK, ALWAYS,
            1, 10,
            null,
            "ICanSeeItAll");
    
    // Too Fast (Get your command rejected because of the cooldown)
    public static TPortAdvancement Advancement_TooFast = new TPortAdvancement(advancement_too_fast_model, "Advancement_TooFast",
            TASK, ALWAYS,
            1, 11,
            null,
            "TooFast");
    
    // WorldTP (Open the WorldTP window)
    //    A Whole New World (Use WorldTP)
    public static TPortAdvancement Advancement_WorldTP = new TPortAdvancement(advancement_world_tp_model, "Advancement_WorldTP",
            TASK, ALWAYS,
            1, 12,
            null,
            "WorldTP");
    public static TPortAdvancement Advancement_AWholeNewWorld = new TPortAdvancement(advancement_a_whole_new_world_model, "Advancement_AWholeNewWorld",
            TASK, ALWAYS,
            2, 12,
            Advancement_WorldTP,
            "AWholeNewWorld");
    
    // BiomeTP (Open the BiomeTP window)
    //    Certainty (Use BiomeTP with one biome)
    //    Surprise! (Use BiomeTP with more biomes selected)
    //    I Don't Care (Use BiomeTP random)
    //    One is not enough (Select a BiomeTP preset / tag list)
    public static TPortAdvancement Advancement_BiomeTP = new TPortAdvancement(advancement_biome_tp_model, "Advancement_BiomeTP",
            TASK, ALWAYS,
            1, 13,
            null,
            "BiomeTP");
    public static TPortAdvancement Advancement_BiomeTP_Certainty = new TPortAdvancement(advancement_biome_certainty_model, "Advancement_BiomeTP_Certainty",
            TASK, ALWAYS,
            2, 13,
            Advancement_BiomeTP,
            "BiomeTP_Certainty");
    public static TPortAdvancement Advancement_BiomeTP_Surprise = new TPortAdvancement(advancement_biome_surprise_model, "Advancement_BiomeTP_Surprise",
            TASK, ALWAYS,
            2, 14,
            Advancement_BiomeTP,
            "BiomeTP_Surprise");
    public static TPortAdvancement Advancement_BiomeTP_IDontCare = new TPortAdvancement(advancement_biome_i_dont_care_model, "Advancement_BiomeTP_IDontCare",
            TASK, ALWAYS,
            2, 15,
            Advancement_BiomeTP,
            "BiomeTP_IDontCare");
    public static TPortAdvancement Advancement_BiomeTP_OneIsNotEnough = new TPortAdvancement(advancement_biome_one_is_not_enough_model, "Advancement_BiomeTP_OneIsNotEnough",
            TASK, ALWAYS,
            2, 16,
            Advancement_BiomeTP,
            "BiomeTP_OneIsNotEnough");
    
    // FeatureTP (Open the FeatureTP window)
    //    Certainty (Use FeatureTP with one feature)
    //    Surprise! (Use FeatureTP with more features selected)
    //    One is not enough (Select a FeatureTP tag list)
    public static TPortAdvancement Advancement_FeatureTP = new TPortAdvancement(advancement_feature_tp_model, "Advancement_FeatureTP",
            TASK, ALWAYS,
            1, 17,
            null,
            "FeatureTP");
    public static TPortAdvancement Advancement_FeatureTP_Certainty = new TPortAdvancement(advancement_feature_certainty_model, "Advancement_FeatureTP_Certainty",
            TASK, ALWAYS,
            2, 17,
            Advancement_FeatureTP,
            "FeatureTP_Certainty");
    public static TPortAdvancement Advancement_FeatureTP_Surprise = new TPortAdvancement(advancement_feature_surprise_model, "Advancement_FeatureTP_Surprise",
            TASK, ALWAYS,
            2, 18,
            Advancement_FeatureTP,
            "FeatureTP_Surprise");
    public static TPortAdvancement Advancement_FeatureTP_OneIsNotEnough = new TPortAdvancement(advancement_feature_one_is_not_enough_model, "Advancement_FeatureTP_OneIsNotEnough",
            TASK, ALWAYS,
            2, 19,
            Advancement_FeatureTP,
            "FeatureTP_OneIsNotEnough");
    
    // Caves & Cliffs, part III (Teleport form the top of the world (build limit) to the bottom of the world)
    public static TPortAdvancement Advancement_CavesAndCliffsPartIII = new TPortAdvancement(advancement_caves_and_cliffs_part_iii_model, "Advancement_CavesAndCliffsPartIII",
            TASK, ALWAYS,
            1, 20,
            null,
            "CavesAndCliffsPartIII");
    
    // Look Me In The Eyes (Use LookTP to teleport to an entity)
    public static TPortAdvancement Advancement_LookMeInTheEyes = new TPortAdvancement(advancement_look_me_in_the_eyes_model, "Advancement_LookMeInTheEyes",
            TASK, ALWAYS,
            1, 21,
            null,
            "LookMeInTheEyes");
    
}
