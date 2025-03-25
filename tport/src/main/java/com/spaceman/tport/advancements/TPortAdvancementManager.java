package com.spaceman.tport.advancements;

import com.google.gson.JsonObject;
import com.spaceman.tport.Main;
import com.spaceman.tport.commands.tport.ResourcePack;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fancyMessage.inventories.InventoryModel;
import com.spaceman.tport.fancyMessage.language.Language;
import eu.endercentral.crazy_advancements.NameKey;
import eu.endercentral.crazy_advancements.advancement.Advancement;
import eu.endercentral.crazy_advancements.advancement.AdvancementDisplay;
import eu.endercentral.crazy_advancements.advancement.AdvancementVisibility;
import eu.endercentral.crazy_advancements.manager.AdvancementManager;
import eu.endercentral.crazy_advancements.packet.AdvancementsPacket;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static com.spaceman.tport.advancements.TPortAdvancement.Advancement_TPort;
import static com.spaceman.tport.advancements.TPortAdvancementsModels.advancement_background_model;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatInfoTranslation;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatTranslation;

public class TPortAdvancementManager {
    
    private static final HashMap<UUID, TPortAdvancementManager> advancements = new HashMap<>();
    
    public enum AdvancementFrame {
        TASK("TASK"),
        GOAL("GOAL"),
        CHALLENGE("CHALLENGE");
        
        private final String frame;
        
        AdvancementFrame(String frame) {
            this.frame = frame;
        }
        
        AdvancementDisplay.AdvancementFrame getFrame() {
            return AdvancementDisplay.AdvancementFrame.parse(frame);
        }
    }
    
    public enum AdvancementVisibility {
        ALWAYS("ALWAYS"),
        PARENT_GRANTED("PARENT_GRANTED"),
        VANILLA("VANILLA"),
        HIDDEN("HIDDEN");
        
        private final String visibility;
        
        AdvancementVisibility(String visibility) {
            this.visibility = visibility;
        }
        
        public eu.endercentral.crazy_advancements.advancement.AdvancementVisibility getVisibility() {
            return eu.endercentral.crazy_advancements.advancement.AdvancementVisibility.parseVisibility(visibility);
        }
    }
    
    private static TPortAdvancementManager createAdvancementManager(Player player) {
        TPortAdvancementManager tportManager = new TPortAdvancementManager(player);
        advancements.put(player.getUniqueId(), tportManager);
        return tportManager;
    }
    public static TPortAdvancementManager getOrCreateManager(Player player) {
        TPortAdvancementManager tportManager = advancements.get(player.getUniqueId());
        if (tportManager == null) {
            tportManager = createAdvancementManager(player);
        }
        
        return tportManager;
    }
    
    public static void removeAdvancementManager(Player player) {
        TPortAdvancementManager manager = getOrCreateManager(player);
        
        manager.advancementManager.resetAccessible();
        advancements.remove(player.getUniqueId());
    }
    
    static void grantAdvancement(@Nullable Player player, TPortAdvancement advancement) {
        if (player == null) return;
        
        TPortAdvancementManager tportManager = getOrCreateManager(player);
        Advancement advancement1 = tportManager.advancementManager.getAdvancement(toPersonafiedNameKey(advancement.key(), player));
        if (advancement1 == null) {
            return;
        }
        if (!advancement1.isGranted(player)) {
            tportManager.advancementManager.grantAdvancement(player, advancement1);
            advancement1.displayToast(player);
            
            String texID = switch (advancement1.getDisplay().getFrame()) {
                case TASK -> "tport.advancements.task";
                case GOAL -> "tport.advancements.goal";
                case CHALLENGE -> "tport.advancements.challenge";
            };
            
            Message chat = formatTranslation(ChatColor.WHITE, ChatColor.GREEN, texID, player, advancement);
            chat.broadcastTranslation();
            
            tportManager.advancementManager.saveProgress(player, advancement1);
            reInitAdvancements(player);
        }
        
    }
    
    public static void reInitAdvancements(Player player) {
        removeAdvancementManager(player);
        getOrCreateManager(player);
    }
    
    private static NameKey toPersonafiedNameKey(String key, Player player) {
        return new NameKey(Main.getInstance().getName(), key + "_" + player.getName());
    }
    private static NameKey toPersonafiedNameKey(NameKey nameKey, Player player) {
        return new NameKey(Main.getInstance().getName(), nameKey.getKey() + "_" + player.getName());
    }
    
    private final Player player;
    private AdvancementManager advancementManager = null;
    
    public TPortAdvancementManager(Player player) {
        this.player = player;
        
        createAdvancements();
        
        this.advancementManager.loadProgress(player);
        AdvancementsPacket removeAdvancements = new AdvancementsPacket(player, false, null, advancementManager.getAdvancements().stream().map(Advancement::getName).toList());
        removeAdvancements.send();
        
        ArrayList<Advancement> list = new ArrayList<>();
        for (Advancement a : advancementManager.getAdvancements()) {
            if (a.getDisplay().getVisibility().isVisible(player, a)) {
                list.add(a);
            }
        }
        
        AdvancementsPacket addAdvancements = new AdvancementsPacket(player, false, list, null);
        addAdvancements.send();
    }
    
    private void createAdvancements() {
        try {

            ColorTheme colorTheme = ColorTheme.getTheme(player);
            JsonObject playerLang = Language.getPlayerLang(player);
            
            TPortAdvancement mainAdvancement = Advancement_TPort;
            AdvancementDisplay display = new AdvancementDisplay(mainAdvancement.model().getItem(player), mainAdvancement.translationID(), mainAdvancement.translationID(),
                    mainAdvancement.frame().getFrame(), mainAdvancement.visibility().getVisibility());
            
            display.setTitle(new CustomJSONMessage(formatInfoTranslation("tport.advancements." + mainAdvancement.translationID() + ".title").translateMessage(playerLang), colorTheme));
            display.setDescription(new CustomJSONMessage(formatTranslation(ColorTheme.ColorType.varInfoColor, ColorTheme.ColorType.varInfo2Color, "tport.advancements." + mainAdvancement.translationID() + ".description").translateMessage(playerLang), colorTheme));
            
            if (ResourcePack.getResourcePackState(player.getUniqueId())) {
                InventoryModel background = advancement_background_model;
                String namespace = background.getNamespacedKey().getNamespace();
                String textureName = background.getNamespacedKey().getKey();
                if (background.hasSubDir()) {
                    textureName = background.getSubDir() + "/" + textureName;
                }
                
                display.setBackgroundTexture(namespace + ":textures/item/" + textureName + ".png");
            } else {
                display.setBackgroundTexture("textures/block/dirt.png");
            }
            
            Advancement advancement = new Advancement(null, toPersonafiedNameKey(mainAdvancement.key(), player), display);
            
            advancementManager = new AdvancementManager(toPersonafiedNameKey("manager", player));
            advancementManager.makeAccessible();
            
            advancementManager.addAdvancement(advancement);
            advancementManager.addPlayer(player);
            
            for (TPortAdvancement tportAdvancement : TPortAdvancement.tportAdvancements) {
                TPortAdvancement parent = tportAdvancement.parent();
                Advancement p = parent == null ? advancement : advancementManager.getAdvancement(toPersonafiedNameKey(parent.key(), player));
                
                AdvancementDisplay saved_display = new AdvancementDisplay(tportAdvancement.model().getItem(player), tportAdvancement.translationID(), tportAdvancement.translationID(),
                        tportAdvancement.frame().getFrame(), tportAdvancement.visibility().getVisibility());
                
                saved_display.setTitle(new CustomJSONMessage(formatInfoTranslation("tport.advancements." + tportAdvancement.translationID() + ".title").translateMessage(playerLang), colorTheme));
                saved_display.setDescription(new CustomJSONMessage(formatTranslation(ColorTheme.ColorType.varInfoColor, ColorTheme.ColorType.varInfo2Color, "tport.advancements." + tportAdvancement.translationID() + ".description").translateMessage(playerLang), colorTheme));
                
                saved_display.setCoordinates(tportAdvancement.x(), tportAdvancement.y());
                
                Advancement a = new Advancement(p, toPersonafiedNameKey(tportAdvancement.key(), player), saved_display);
                advancementManager.addAdvancement(a);
                
            }
            
        } catch (Exception | Error ignore) {
            ignore.printStackTrace();
        }
        
    }
    
}
