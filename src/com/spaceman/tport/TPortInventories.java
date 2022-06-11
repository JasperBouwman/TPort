package com.spaceman.tport;

import com.google.gson.JsonObject;
import com.spaceman.tport.commands.TPortCommand;
import com.spaceman.tport.commands.tport.*;
import com.spaceman.tport.commands.tport.featureTP.Mode;
import com.spaceman.tport.commands.tport.pltp.Offset;
import com.spaceman.tport.commands.tport.publc.Move;
import com.spaceman.tport.cooldown.CooldownManager;
import com.spaceman.tport.dynmap.DynmapHandler;
import com.spaceman.tport.fancyMessage.Keybinds;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.MessageUtils;
import com.spaceman.tport.fancyMessage.TextType;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.tport.fancyMessage.encapsulation.BiomeEncapsulation;
import com.spaceman.tport.fancyMessage.inventories.FancyClickEvent;
import com.spaceman.tport.fancyMessage.inventories.FancyInventory;
import com.spaceman.tport.playerUUID.PlayerUUID;
import com.spaceman.tport.tport.TPort;
import com.spaceman.tport.tport.TPortManager;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static com.spaceman.tport.commands.TPortCommand.executeInternal;
import static com.spaceman.tport.commands.tport.Back.getPrevLocName;
import static com.spaceman.tport.commands.tport.Sort.getSorter;
import static com.spaceman.tport.commands.tport.publc.Move.emptySlot;
import static com.spaceman.tport.fancyMessage.TextComponent.textComponent;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.*;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.inventories.FancyInventory.getDynamicScrollableInventory;
import static com.spaceman.tport.fancyMessage.language.Language.getPlayerLang;
import static com.spaceman.tport.fileHander.Files.tportData;
import static com.spaceman.tport.tport.TPortManager.getTPort;

public class TPortInventories {
    
    private static final HashMap<UUID, UUID> quickEditMoveList = new HashMap<>();
    
    private static ItemStack toTPortItem(TPort tport, Player player) {
        return toTPortItem(tport, player, false);
    }
    public static ItemStack toTPortItem(TPort tport, Player player, boolean extended) {
        ItemStack is = tport.getItem();
        ItemMeta im = is.getItemMeta();
        
        if (im == null) {
            return is;
        }
        
        FancyClickEvent.removeAllFunctions(im);
        
        ColorTheme theme = ColorTheme.getTheme(player);
        
        FancyClickEvent.addCommand(im, ClickType.LEFT, "tport open " + PlayerUUID.getPlayerName(tport.getOwner()) + " " + tport.getName());
        FancyClickEvent.addCommand(im, ClickType.DROP, "tport preview " + PlayerUUID.getPlayerName(tport.getOwner()) + " " + tport.getName());
        FancyClickEvent.addFunction(im, ClickType.RIGHT, "tportGUI_quickEdit", (whoClicked, clickType, pdc, fancyInventory) -> {
            UUID ownerUUID = fancyInventory.getData("ownerUUID", UUID.class);
            //quick edit
            if (whoClicked.getUniqueId().equals(ownerUUID)) {
                
                NamespacedKey tportUUIDKey = new NamespacedKey(Main.getInstance(), "TPortUUID");
                if (pdc.has(tportUUIDKey, PersistentDataType.STRING)) {
                    //noinspection ConstantConditions
                    TPort quickEditTPort = TPortManager.getTPort(ownerUUID, UUID.fromString(pdc.get(tportUUIDKey, PersistentDataType.STRING)));
                    if (quickEditTPort != null) {
                        TPortInventories.QuickEditType.getForPlayer(whoClicked.getUniqueId()).edit(quickEditTPort, whoClicked);
                        openTPortGUI(whoClicked.getUniqueId(), whoClicked);
                    }
                }
            }
        });
        FancyClickEvent.addFunction(im, ClickType.SHIFT_RIGHT, "tportGUI_nextQuickEdit", (whoClicked, clickType, pdc, fancyInventory) -> {
            UUID ownerUUID = fancyInventory.getData("ownerUUID", UUID.class);
            //next quick edit
            if (whoClicked.getUniqueId().equals(ownerUUID)) {
                NamespacedKey tportUUIDKey = new NamespacedKey(Main.getInstance(), "TPortUUID");
                if (pdc.has(tportUUIDKey, PersistentDataType.STRING)) {
                    //noinspection ConstantConditions
                    TPort quickEditTPort = TPortManager.getTPort(ownerUUID, UUID.fromString(pdc.get(tportUUIDKey, PersistentDataType.STRING)));
                    if (quickEditTPort != null) {
                        QuickEditType type = QuickEditType.getForPlayer(whoClicked.getUniqueId()).getNext();
                        QuickEditType.clearData(whoClicked.getUniqueId());
                        QuickEditType.setForPlayer(whoClicked.getUniqueId(), type);
                        openTPortGUI(ownerUUID, whoClicked);
                    }
                }
            }
        });
        im.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "TPortUUID"), PersistentDataType.STRING, tport.getTportID().toString());
        
        for (Enchantment e : im.getEnchants().keySet()) {
            im.removeEnchant(e);
        }
        
        if (tport.getOwner().equals(player.getUniqueId()) && !extended) {
            if (tport.getTportID().equals(quickEditMoveList.get(player.getUniqueId()))) {
                Glow.addGlow(im);
            }
        }
        is.setItemMeta(im);
        
        Message title = formatTranslation(infoColor, varInfoColor, "tport.tportInventories.tportName", tport.getName());
        
        Collection<Message> lore = tport.getHoverData(extended);
        lore.add(new Message());
        if (tport.getOwner().equals(player.getUniqueId()) && !extended) {
            TPortInventories.QuickEditType type = TPortInventories.QuickEditType.getForPlayer(player.getUniqueId());
            lore.add(formatInfoTranslation("tport.tport.tport.hoverData.editing", type.getDisplayName()));
            lore.add(formatInfoTranslation("tport.tport.tport.hoverData.buttons",
                    ClickType.SHIFT_RIGHT, type.getNext().getDisplayName()));
        }
        if (!extended) {
            lore.add(formatInfoTranslation("tport.tport.tport.hoverData.preview",
                    textComponent(Keybinds.DROP, varInfoColor).setType(TextType.KEYBIND)));
        }
        
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        if (playerLang != null) { //if player has no custom language, translate it
            title = MessageUtils.translateMessage(title, playerLang);
            lore = MessageUtils.translateMessage(lore, playerLang);
        }
        
        MessageUtils.setCustomItemData(is, theme, title, lore);
        return is;
    }
    private static ItemStack toPublicTPortItem(TPort tport, Player player, @Nullable FancyInventory prevWindow) {
        ItemStack is = tport.getItem();
        ItemMeta im = is.getItemMeta();
        
        if (im == null) {
            return is;
        }
        
        FancyClickEvent.removeAllFunctions(im);
        
        ColorTheme theme = ColorTheme.getTheme(player);
        
        FancyClickEvent.addCommand(im, ClickType.LEFT, "tport public open " + tport.getName());
        im.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "TPortUUID"), PersistentDataType.STRING, tport.getTportID().toString());
        FancyClickEvent.addFunction(im, ClickType.RIGHT, "publicTP_move", ((whoClicked, clickType, pdc, fancyInventory) -> {
            NamespacedKey keyUUID = new NamespacedKey(Main.getInstance(), "TPortUUID");
            if (pdc.has(keyUUID, PersistentDataType.STRING)) {
                if (!emptySlot.hasPermissionToRun(whoClicked, false)) {
                    return;
                }
                
                //noinspection ConstantConditions
                TPort innerTPort = TPortManager.getTPort(UUID.fromString(pdc.get(keyUUID, PersistentDataType.STRING)));
                if (innerTPort != null) {
                    UUID otherTPortID = fancyInventory.getData("TPortToMove", UUID.class, null);
                    if (otherTPortID != null) {
                        fancyInventory.setData("TPortToMove", null);
                        if (!otherTPortID.equals(innerTPort.getTportID())) {
                            TPort tmpTPort = TPortManager.getTPort(otherTPortID);
                            if (tmpTPort != null) {
                                executeInternal(whoClicked, new String[]{"public", "move", tmpTPort.getName(), innerTPort.getName()});
                                openPublicTPortGUI(whoClicked, fancyInventory.getData("page", Integer.class, 0), fancyInventory);
                            }
                        }
                    } else {
                        if (com.spaceman.tport.commands.tport.edit.Move.emptySlot.hasPermissionToRun(whoClicked, false)) {
                            fancyInventory.setData("TPortToMove", innerTPort.getTportID());
                            openPublicTPortGUI(whoClicked, fancyInventory.getData("page", Integer.class, 0), fancyInventory);
                        }
                    }
                }
            }
        }));
        
        for (Enchantment e : im.getEnchants().keySet()) {
            im.removeEnchant(e);
        }
        
        if (Move.emptySlot.hasPermissionToRun(player, false) && prevWindow != null) {
            if (tport.getTportID().equals(prevWindow.getData("TPortToMove", UUID.class))) {
                Glow.addGlow(im);
            }
        }
        is.setItemMeta(im);
        
        Message title = formatTranslation(infoColor, varInfoColor, "tport.tportInventories.tportName", tport.getName());
        Collection<Message> lore = tport.getHoverData(true);
        
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        if (playerLang != null) { //if player has no custom language, translate it
            title = MessageUtils.translateMessage(title, playerLang);
            lore = MessageUtils.translateMessage(lore, playerLang);
        }
        
        MessageUtils.setCustomItemData(is, theme, title, lore);
        
        return is;
    }
    
    private static ItemStack createBack(Player player, @Nullable BackType left, @Nullable BackType right, @Nullable BackType shiftRight) {
        return createBack(player, left, right, shiftRight, null);
    }
    private static ItemStack createBack(Player player, @Nullable BackType left, @Nullable BackType right, @Nullable BackType shiftRight, @Nullable BackType notAllowed) {
        ItemStack is = new ItemStack(Material.BARRIER);
        
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
            if (Features.Feature.PublicTP.isEnabled()) {
                if (BackType.FEATURE_TP.equals(left)) left = BackType.PUBLIC;
                if (BackType.FEATURE_TP.equals(right)) right = BackType.PUBLIC;
                if (BackType.FEATURE_TP.equals(shiftRight)) shiftRight = BackType.PUBLIC;
            } else {
                if (BackType.FEATURE_TP.equals(left)) left = null;
                if (BackType.FEATURE_TP.equals(right)) right = null;
                if (BackType.FEATURE_TP.equals(shiftRight)) shiftRight = null;
            }
        }
        if (left != null && left.equals(notAllowed)) left = null;
        if (right != null && right.equals(notAllowed)) right = null;
        if (shiftRight != null && shiftRight.equals(notAllowed)) shiftRight = null;
        
        Message title = formatTranslation(titleColor, titleColor, "tport.tportInventories.backButton.name");
        ArrayList<Message> lore = new ArrayList<>();
        if (left != null) {
            lore.add(formatInfoTranslation("tport.tportInventories.backButton.format.leftClick", ClickType.LEFT));
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
        if (playerLang != null) { //if player has no custom language, translate it
            title = MessageUtils.translateMessage(title, playerLang);
            lore = (ArrayList<Message>) MessageUtils.translateMessage(lore, playerLang);
        }
        
        ColorTheme theme = ColorTheme.getTheme(player);
        MessageUtils.setCustomItemData(is, theme, title, lore);
        
        if (left != null) FancyClickEvent.addCommand(is, ClickType.LEFT, left.getCommand());
        if (right != null) FancyClickEvent.addCommand(is, ClickType.RIGHT, right.getCommand());
        if (shiftRight != null) FancyClickEvent.addCommand(is, ClickType.SHIFT_RIGHT, shiftRight.getCommand());
        return is;
    }
    
    public static void openMainTPortGUI(Player player) {
        openMainTPortGUI(player, 0, null);
    }
    public static void openMainTPortGUI(Player player, int page, FancyInventory prevWindow) {
        List<ItemStack> list;
        
        if (prevWindow == null) {
            list = getSorter(player).sort(player);
            
            for (ItemStack is : list) { //remove player and set him first
                if (is.getItemMeta() instanceof SkullMeta skullMeta) {
                    if (skullMeta.getOwningPlayer() != null) {
                        if (skullMeta.getOwningPlayer().getUniqueId().equals(player.getUniqueId())) {
                            list.remove(is);
                            list.add(0, is);
                            break;
                        }
                    }
                }
            }
            
            ArrayList<ItemStack> newList = new ArrayList<>();
            for (ItemStack is : list) {
                if (MainLayout.showPlayers(player)) {
                    newList.add(is);
                }
                if (MainLayout.showTPorts(player)) {
                    if (is.getItemMeta() instanceof SkullMeta sm) {
                        if (sm.getOwningPlayer() != null) {
                            TPortManager.getSortedTPortList(tportData, sm.getOwningPlayer().getUniqueId()).stream()
                                    .filter(Objects::nonNull).map(tport -> toTPortItem(tport, player, true)).forEach(newList::add);
                        }
                    }
                }
            }
            list = newList;
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
        
        FancyInventory inv = getDynamicScrollableInventory(player, page, TPortInventories::openMainTPortGUI, title, list, null);
        inv.setData("content", list);
        
        ItemStack biomeTP = new ItemStack(Material.OAK_BUTTON);
        ItemStack featureTP = new ItemStack(Material.OAK_BUTTON);
        ItemStack backTP = new ItemStack(Material.OAK_BUTTON);
        ItemStack publicTP = new ItemStack(Material.OAK_BUTTON);
        ItemStack mainLayout = new ItemStack(Material.OAK_BUTTON);
        ItemStack sorting = new ItemStack(Material.OAK_BUTTON);
        Message biomeTPTitle = formatTranslation(titleColor, titleColor, "tport.tportInventories.openMainGUI.biomeTP.title", "BiomeTP");
        Message biomeTPLeft = formatInfoTranslation("tport.tportInventories.openMainGUI.biomeTP.leftClick", ClickType.LEFT);
        Message biomeTPRight = formatInfoTranslation("tport.tportInventories.openMainGUI.biomeTP.rightClick", ClickType.RIGHT);
        Message biomeTPShiftRight = formatInfoTranslation("tport.tportInventories.openMainGUI.biomeTP.shiftRightClick", ClickType.SHIFT_RIGHT);
        Message featureTPTitle = formatTranslation(titleColor, titleColor, "tport.tportInventories.openMainGUI.featureTP.title", "FeatureTP");
        Message backTPTitle = formatTranslation(titleColor, titleColor, "tport.tportInventories.openMainGUI.backTP.title", "BackTP");
        Message backTPLore = Back.getPrevLocName(player);
        Message publicTPTitle = formatTranslation(titleColor, titleColor, "tport.tportInventories.openMainGUI.publicTP.title", "PublicTP");
        Message mainLayoutTitle = formatTranslation(titleColor, titleColor, "tport.tportInventories.openMainGUI.mainLayout.title");
        String layoutState = "tport.tportInventories.openMainGUI.mainLayout.";
        Message mainLayoutTPortState = formatInfoTranslation("tport.tportInventories.openMainGUI.mainLayout.tportState", formatTranslation(varInfoColor, varInfo2Color, layoutState + (MainLayout.showTPorts(player) ? "showing" : "hiding")));
        Message mainLayoutPlayerState = formatInfoTranslation("tport.tportInventories.openMainGUI.mainLayout.playerState", formatTranslation(varInfoColor, varInfo2Color, layoutState + (MainLayout.showPlayers(player) ? "showing" : "hiding")));
        layoutState = "tport.tportInventories.openMainGUI.mainLayout.";
        Message mainLayoutTPorts = formatInfoTranslation("tport.tportInventories.openMainGUI.mainLayout.editTPorts", ClickType.LEFT, formatTranslation(varInfoColor, varInfo2Color, layoutState + (!MainLayout.showTPorts(player) ? "show" : "hide")));
        Message mainLayoutPlayers = formatInfoTranslation("tport.tportInventories.openMainGUI.mainLayout.editPlayers", ClickType.RIGHT, formatTranslation(varInfoColor, varInfo2Color, layoutState + (!MainLayout.showPlayers(player) ? "show" : "hide")));
        Message sortingTitle = formatTranslation(titleColor, titleColor, "tport.tportInventories.openMainGUI.sorting.title");
        String sort = Sort.getSorterName(player);
        Message sortingCurrent = formatInfoTranslation("tport.tportInventories.openMainGUI.sorting.current", sort);
        String nextSort = Sort.getNextSorterName(sort);
        String previousSort = Sort.getPreviousSorterName(sort);
        Message sortingNext = formatInfoTranslation("tport.tportInventories.openMainGUI.sorting.next", ClickType.LEFT, nextSort);
        Message sortingPrevious = formatInfoTranslation("tport.tportInventories.openMainGUI.sorting.previous", ClickType.RIGHT, previousSort);
        
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        if (playerLang != null) { //if player has no custom language, translate it
            biomeTPTitle = MessageUtils.translateMessage(biomeTPTitle, playerLang);
            biomeTPLeft = MessageUtils.translateMessage(biomeTPLeft, playerLang);
            biomeTPRight = MessageUtils.translateMessage(biomeTPRight, playerLang);
            biomeTPShiftRight = MessageUtils.translateMessage(biomeTPShiftRight, playerLang);
            featureTPTitle = MessageUtils.translateMessage(featureTPTitle, playerLang);
            backTPTitle = MessageUtils.translateMessage(backTPTitle, playerLang);
            backTPLore = MessageUtils.translateMessage(backTPLore, playerLang);
            publicTPTitle = MessageUtils.translateMessage(publicTPTitle, playerLang);
            mainLayoutTitle = MessageUtils.translateMessage(mainLayoutTitle, playerLang);
            mainLayoutTPortState = MessageUtils.translateMessage(mainLayoutTPortState, playerLang);
            mainLayoutPlayerState = MessageUtils.translateMessage(mainLayoutPlayerState, playerLang);
            mainLayoutTPorts = MessageUtils.translateMessage(mainLayoutTPorts, playerLang);
            mainLayoutPlayers = MessageUtils.translateMessage(mainLayoutPlayers, playerLang);
            sortingTitle = MessageUtils.translateMessage(sortingTitle, playerLang);
            sortingCurrent = MessageUtils.translateMessage(sortingCurrent, playerLang);
            sortingNext = MessageUtils.translateMessage(sortingNext, playerLang);
            sortingPrevious = MessageUtils.translateMessage(sortingPrevious, playerLang);
        }
        ColorTheme theme = ColorTheme.getTheme(player);
        MessageUtils.setCustomItemData(biomeTP, theme, biomeTPTitle, Arrays.asList(biomeTPLeft, biomeTPRight, biomeTPShiftRight));
        MessageUtils.setCustomItemData(featureTP, theme, featureTPTitle, null);
        MessageUtils.setCustomItemData(backTP, theme, backTPTitle, Collections.singleton(backTPLore));
        MessageUtils.setCustomItemData(publicTP, theme, publicTPTitle, null);
        MessageUtils.setCustomItemData(mainLayout, theme, mainLayoutTitle, Arrays.asList(mainLayoutTPortState, mainLayoutPlayerState, new Message(), mainLayoutTPorts, mainLayoutPlayers));
        MessageUtils.setCustomItemData(sorting, theme, sortingTitle, Arrays.asList(sortingCurrent, new Message(), sortingNext, sortingPrevious));
        FancyClickEvent.addCommand(biomeTP, ClickType.LEFT, "tport biomeTP");
        FancyClickEvent.addCommand(biomeTP, ClickType.RIGHT, "tport biomeTP preset");
        FancyClickEvent.addCommand(biomeTP, ClickType.SHIFT_RIGHT, "tport biomeTP random");
        FancyClickEvent.addCommand(featureTP, ClickType.LEFT, "tport featureTP");
        FancyClickEvent.addCommand(backTP, ClickType.LEFT, "tport back");
        FancyClickEvent.addCommand(publicTP, ClickType.LEFT, "tport public");
        FancyClickEvent.addCommand(mainLayout, ClickType.LEFT, "tport mainLayout tports " + !MainLayout.showTPorts(player), "tport");
        FancyClickEvent.addCommand(mainLayout, ClickType.RIGHT, "tport mainLayout players " + !MainLayout.showPlayers(player), "tport");
        FancyClickEvent.addCommand(sorting, ClickType.LEFT, "tport sort " + nextSort);
        FancyClickEvent.addCommand(sorting, ClickType.RIGHT, "tport sort " + previousSort);
        
        ArrayList<ItemStack> naviButtons = new ArrayList<>(4);
        if (Features.Feature.BiomeTP.isEnabled()) naviButtons.add(biomeTP);
        if (Features.Feature.FeatureTP.isEnabled()) naviButtons.add(featureTP);
        if (Features.Feature.BackTP.isEnabled()) naviButtons.add(backTP);
        if (Features.Feature.PublicTP.isEnabled()) naviButtons.add(publicTP);
        int naviSize = naviButtons.size();
        if (naviSize > 0) {
            int buttonOffset = inv.getSize() - 4 - naviSize;
            for (int i = 0; i < naviSize; i++) {
                inv.setItem(buttonOffset + 2*i, naviButtons.get(i));
            }
        }
        
        inv.setItem(inv.getSize() / 18 * 9, mainLayout);
        inv.setItem(inv.getSize() / 18 * 9 + 8, sorting);
        
        inv.open(player);
    }
    
    public static void openTPortGUI(UUID ownerUUID, Player player) {
        String newPlayerName = PlayerUUID.getPlayerName(ownerUUID);
        
        Validate.notNull(newPlayerName, "The newPlayerName can not be null");
        Validate.notNull(ownerUUID, "The ownerUUID can not be null");
        Validate.notNull(player, "The player can not be null");
        
        FancyInventory inv = new FancyInventory(27, formatInfoTranslation("tport.tportInventories.TPORT.title", newPlayerName));
        inv.setData("ownerUUID", ownerUUID);
        
        ColorTheme theme = ColorTheme.getTheme(player);
        
        ItemStack extraTP = new ItemStack(Material.OAK_BUTTON);
        Message extraTPTitle = formatTranslation(titleColor, titleColor, "tport.tportInventories.openTPortGUI.extraTP.title");
        Collection<Message> extraTPLore = new ArrayList<>();
        extraTPLore.add(formatInfoTranslation("tport.tportInventories.openTPortGUI.extraTP.format.leftClick", ClickType.LEFT));
        extraTPLore.add(getPrevLocName(player));
        extraTPLore.add(new Message());
        extraTPLore.add(formatInfoTranslation("tport.tportInventories.openTPortGUI.extraTP.format.rightClick", ClickType.RIGHT));
        extraTPLore.add(new Message(textComponent("BiomeTP", varInfoColor)));
        extraTPLore.add(new Message());
        extraTPLore.add(formatInfoTranslation("tport.tportInventories.openTPortGUI.extraTP.format.shiftRightClick", ClickType.SHIFT_RIGHT));
        extraTPLore.add(new Message(textComponent("FeatureTP", varInfoColor)));

        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        if (playerLang != null) { //if player has no custom language, translate it
            extraTPTitle = MessageUtils.translateMessage(extraTPTitle, playerLang);
            extraTPLore = MessageUtils.translateMessage(extraTPLore, playerLang);
        }
        MessageUtils.setCustomItemData(extraTP, theme, extraTPTitle, extraTPLore);
        FancyClickEvent.addCommand(extraTP, ClickType.LEFT, "tport back");
        FancyClickEvent.addCommand(extraTP, ClickType.RIGHT, "tport biomeTP");
        FancyClickEvent.addCommand(extraTP, ClickType.SHIFT_RIGHT, "tport featureTP");
        
        inv.setItem(17, extraTP);
        
        inv.setItem(26, createBack(player, BackType.MAIN, BackType.OWN, BackType.PUBLIC));
        
        boolean pltpState = tportData.getConfig().getBoolean("tport." + ownerUUID + ".tp.statement", true);
        if (ownerUUID.equals(player.getUniqueId())) {
            ItemStack warp = new ItemStack(Material.PLAYER_HEAD);
            
            boolean pltpConsent = tportData.getConfig().getBoolean("tport." + ownerUUID + ".tp.consent", false);
            Offset.PLTPOffset pltpOffset = Offset.getPLTPOffset(player);
            
            Message warpTitle = formatTranslation(titleColor, titleColor, "tport.tportInventories.openTPortGUI.playerHead.own.format.title");
            Collection<Message> warpLore = new ArrayList<>();
            warpLore.add(formatInfoTranslation("tport.tportInventories.openTPortGUI.playerHead.own.format.PLTPState", pltpState));
            warpLore.add(formatInfoTranslation("tport.tportInventories.openTPortGUI.playerHead.own.format.PLTPConsent", pltpConsent));
            warpLore.add(formatInfoTranslation("tport.tportInventories.openTPortGUI.playerHead.own.format.PLTPOffset", pltpOffset.name()));
            warpLore.add(new Message());
            warpLore.add(formatInfoTranslation("tport.tportInventories.openTPortGUI.playerHead.own.format.whenLeftClick",
                    ClickType.LEFT, !pltpState));
            warpLore.add(formatInfoTranslation("tport.tportInventories.openTPortGUI.playerHead.own.format.whenRightClick",
                    ClickType.RIGHT, !pltpConsent));
            warpLore.add(formatInfoTranslation("tport.tportInventories.openTPortGUI.playerHead.own.format.whenShiftRightClick",
                    ClickType.SHIFT_RIGHT, pltpOffset.getNext().name()));
            
            if (playerLang != null) { //if player has no custom language, translate it
                warpTitle = MessageUtils.translateMessage(warpTitle, playerLang);
                warpLore = MessageUtils.translateMessage(warpLore, playerLang);
            }
            MessageUtils.setCustomItemData(warp, theme, warpTitle, warpLore);
            
            FancyClickEvent.addCommand(warp, ClickType.LEFT, "tport PLTP state " + !pltpState, "tport own");
            FancyClickEvent.addCommand(warp, ClickType.RIGHT, "tport PLTP consent " + !pltpConsent, "tport own");
            FancyClickEvent.addCommand(warp, ClickType.SHIFT_RIGHT, "tport PLTP offset " + pltpOffset.getNext(), "tport own");
            
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
            if (!pltpState) {
                ArrayList<String> list = (ArrayList<String>) tportData.getConfig()
                        .getStringList("tport." + ownerUUID + "tp.players");
                
                if (list.contains(player.getUniqueId().toString())) {
                    warpTitleSuffix = "warp";
                } else {
                    warpTitleSuffix = "off";
                }
            } else if (Bukkit.getPlayer(ownerUUID) != null) {
                warpTitleSuffix = "warp";
            } else {
                warpTitleSuffix = "offline";
            }
            
            Message warpTitle = formatTranslation(titleColor, titleColor, "tport.tportInventories.openTPortGUI.playerHead.other.format." + warpTitleSuffix,
                    PlayerUUID.getPlayerName(ownerUUID));
            if (playerLang != null) { //if player has no custom language, translate it
                warpTitle = MessageUtils.translateMessage(warpTitle, playerLang);
            }
            MessageUtils.setCustomItemData(warp, theme, warpTitle, null);
            FancyClickEvent.addCommand(warp, ClickType.LEFT, "tport PLTP tp " + newPlayerName);
            
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
                inv.setItem(i + slotOffset, toTPortItem(tport, player));
            } else {
                if (ownerUUID.equals(player.getUniqueId())) {
                    UUID toMoveUUID = quickEditMoveList.get(player.getUniqueId());
                    if (toMoveUUID != null) {
                        
                        ItemStack empty = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
                        
                        TPort toMoveTPort = TPortManager.getTPort(ownerUUID, toMoveUUID);
                        Message emptyTitle = formatInfoTranslation("tport.tportInventories.openTPortGUI.quickEditReplacement",
                                ClickType.RIGHT, toMoveTPort);
                        if (playerLang != null) { //if player has no custom language, translate it
                            emptyTitle = MessageUtils.translateMessage(emptyTitle, playerLang);
                        }
                        MessageUtils.setCustomItemData(empty, theme, emptyTitle, null);
                        
                        ItemMeta emptyMeta = empty.getItemMeta();
                        emptyMeta.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "TPortNameToMove"), PersistentDataType.STRING, toMoveTPort.getName());
                        emptyMeta.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "TPortSlot"), PersistentDataType.STRING, String.valueOf(i + 1));
                        
                        FancyClickEvent.addFunction(emptyMeta, ClickType.RIGHT, "tportGUI_moveToEmpty", (whoClicked, clickType, pdc, fancyInventory) -> {
                            String tportNameToMove = pdc.get(new NamespacedKey(Main.getInstance(), "TPortNameToMove"), PersistentDataType.STRING);
                            String toSlot = pdc.get(new NamespacedKey(Main.getInstance(), "TPortSlot"), PersistentDataType.STRING);
                            TPortCommand.executeInternal(whoClicked, new String[]{"edit", tportNameToMove, "move", toSlot});
                            quickEditMoveList.remove(whoClicked.getUniqueId());
                            openTPortGUI(whoClicked.getUniqueId(), whoClicked);
                        });
                        
                        empty.setItemMeta(emptyMeta);
                        inv.setItem(i + slotOffset, empty);
                    }
                }
            }
        }
        inv.open(player);
    }
    
    public static void openBiomeTP(Player player) {
        openBiomeTP(player, 0, null);
    }
    public static void openBiomeTP(Player player, int page, @Nullable FancyInventory prevWindow) {
        List<ItemStack> list = new ArrayList<>();
        ColorTheme theme = ColorTheme.getTheme(player);
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        
        ArrayList<String> biomeSelection;
        if (prevWindow != null) {
            biomeSelection = prevWindow.getData("biomeSelection", ArrayList.class, new ArrayList<String>());
        } else {
            biomeSelection = new ArrayList<>();
        }
        
        for (String biome : BiomeTP.availableBiomes(player.getWorld())) {
            if (biome.equals("custom")) continue;
            Material material = switch (biome.toUpperCase()) {
                case "OCEAN", "RIVER", "DEEP_OCEAN", "LUKEWARM_OCEAN", "COLD_OCEAN", "DEEP_LUKEWARM_OCEAN", "DEEP_COLD_OCEAN" -> Material.WATER_BUCKET;
                case "PLAINS", "WINDSWEPT_HILLS", "MEADOW" -> Material.GRASS_BLOCK;
                case "DESERT", "BEACH" -> Material.SAND;
                case "FOREST", "WINDSWEPT_FOREST" -> Material.OAK_LOG;
                case "TAIGA", "OLD_GROWTH_PINE_TAIGA", "OLD_GROWTH_SPRUCE_TAIGA" -> Material.SPRUCE_LOG;
                case "SWAMP" -> Material.LILY_PAD;
                case "NETHER_WASTES" -> Material.NETHERRACK;
                case "THE_END", "SMALL_END_ISLANDS", "END_MIDLANDS", "END_HIGHLANDS", "END_BARRENS" -> Material.END_STONE;
                case "FROZEN_OCEAN", "FROZEN_RIVER", "DEEP_FROZEN_OCEAN" -> Material.ICE;
                case "ICE_SPIKES", "FROZEN_PEAKS" -> Material.PACKED_ICE;
                case "SNOWY_PLAINS", "SNOWY_BEACH", "SNOWY_TAIGA" -> Material.SNOW;
                case "MUSHROOM_FIELDS" -> Material.RED_MUSHROOM_BLOCK;
                case "JUNGLE", "SPARSE_JUNGLE" -> Material.JUNGLE_LOG;
                case "BAMBOO_JUNGLE" -> Material.BAMBOO;
                case "STONY_SHORE", "STONY_PEAKS" -> Material.STONE;
                case "BIRCH_FOREST", "OLD_GROWTH_BIRCH_FOREST" -> Material.BIRCH_LOG;
                case "DARK_FOREST" -> Material.DARK_OAK_LOG;
                case "SAVANNA", "SAVANNA_PLATEAU", "WINDSWEPT_SAVANNA" -> Material.ACACIA_LOG;
                case "BADLANDS", "WOODED_BADLANDS", "ERODED_BADLANDS" -> Material.TERRACOTTA;
                case "WARM_OCEAN" -> Material.BRAIN_CORAL_BLOCK;
                case "THE_VOID" -> Material.BARRIER;
                case "SUNFLOWER_PLAINS" -> Material.SUNFLOWER;
                case "WINDSWEPT_GRAVELLY_HILLS" -> Material.GRAVEL;
                case "FLOWER_FOREST" -> Material.ROSE_BUSH;
                case "SOUL_SAND_VALLEY" -> Material.SOUL_SAND;
                case "CRIMSON_FOREST" -> Material.CRIMSON_NYLIUM;
                case "WARPED_FOREST" -> Material.WARPED_NYLIUM;
                case "BASALT_DELTAS" -> Material.BASALT;
                case "DRIPSTONE_CAVES" -> Material.POINTED_DRIPSTONE;
                case "LUSH_CAVES" -> Material.GLOW_BERRIES;
                case "GROVE", "JAGGED_PEAKS" -> Material.SNOW_BLOCK;
                case "SNOWY_SLOPES" -> Material.POWDER_SNOW_BUCKET;
                case "MANGROVE_SWAMP" -> Material.MANGROVE_LOG;
                case "DEEP_DARK" -> Material.SCULK;
                default -> Material.DIAMOND_BLOCK;
            };
            
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
            Message biomeLClick = formatInfoTranslation("tport.tportInventories.openBiomeTP.biome.LClick", ClickType.LEFT, selectedMessage);
            Message biomeRClick = formatInfoTranslation("tport.tportInventories.openBiomeTP.biome.RClick", ClickType.RIGHT);
            Message biomeSRClick = formatInfoTranslation("tport.tportInventories.openBiomeTP.biome.shift_RClick", ClickType.SHIFT_RIGHT);
            if (playerLang != null) { //if player has no custom language, translate it
                biomeTitle = MessageUtils.translateMessage(biomeTitle, playerLang);
                biomeLClick = MessageUtils.translateMessage(biomeLClick, playerLang);
                biomeRClick = MessageUtils.translateMessage(biomeRClick, playerLang);
                biomeSRClick = MessageUtils.translateMessage(biomeSRClick, playerLang);
            }
            MessageUtils.setCustomItemData(item, theme, biomeTitle, Arrays.asList(biomeLClick, biomeRClick, biomeSRClick));
            
            ItemMeta im = item.getItemMeta();
            im.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "biome"), PersistentDataType.STRING, biome);
            if (selected) Glow.addGlow(im);
            
            FancyClickEvent.addFunction(im, ClickType.LEFT, "biomeTP_select", ((whoClicked, clickType, pdc, fancyInventory) -> {
                NamespacedKey biomeKey = new NamespacedKey(Main.getInstance(), "biome");
                if (pdc.has(biomeKey, PersistentDataType.STRING)) {
                    ArrayList<String> innerBiomeSelection = fancyInventory.getData("biomeSelection", ArrayList.class, new ArrayList<String>());
                    String innerBiome = pdc.get(biomeKey, PersistentDataType.STRING);
                    if (innerBiomeSelection.contains(innerBiome)) {
                        innerBiomeSelection.remove(innerBiome);
                    } else {
                        innerBiomeSelection.add(innerBiome);
                    }
                    fancyInventory.setData("biomeSelection", innerBiomeSelection);
                    openBiomeTP(whoClicked, fancyInventory.getData("page", Integer.class, 0), fancyInventory);
                }
            }));
            FancyClickEvent.addCommand(im, ClickType.RIGHT, "tport biomeTP whitelist " + biome);
            FancyClickEvent.addCommand(im, ClickType.SHIFT_RIGHT, "tport biomeTP blacklist " + biome);
            
            item.setItemMeta(im);
            
            if (selected) list.add(0, item);
            else list.add(item);
        }
        
        ItemStack random = new ItemStack(Material.ELYTRA);
        Message randomTitle = formatTranslation(titleColor, titleColor, "tport.tportInventories.openBiomeTP.randomTP.title");
        
        ItemStack presets = new ItemStack(Material.OAK_BUTTON);
        Message presetsTitle = formatTranslation(titleColor, titleColor, "tport.tportInventories.openBiomeTP.presets.title");
        
        ItemStack clearSelected = new ItemStack(Material.OAK_BUTTON);
        Message modeTitle = formatInfoTranslation("tport.tportInventories.openBiomeTP.clearSelected.title", ClickType.LEFT);
        
        ItemStack run = new ItemStack(Material.OAK_BUTTON);
        Mode.WorldSearchMode biomeTPMode = com.spaceman.tport.commands.tport.biomeTP.Mode.getDefMode(player.getUniqueId());
        Message runTitle = formatTranslation(titleColor, titleColor, "tport.tportInventories.openBiomeTP.run.title");
        Message runWhitelist = formatInfoTranslation("tport.tportInventories.openBiomeTP.run.whitelist", ClickType.LEFT);
        Message runBlacklist = formatInfoTranslation("tport.tportInventories.openBiomeTP.run.blacklist", ClickType.RIGHT);
        Message runCurrentMode = formatInfoTranslation("tport.tportInventories.openBiomeTP.run.currentMode", biomeTPMode.name());
        Message runChangeMode = formatInfoTranslation("tport.tportInventories.openBiomeTP.run.changeMode", ClickType.SHIFT_RIGHT, biomeTPMode.getNext());
        
        if (playerLang != null) { //if player has no custom language, translate it
            randomTitle = MessageUtils.translateMessage(randomTitle, playerLang);
            presetsTitle = MessageUtils.translateMessage(presetsTitle, playerLang);
            modeTitle = MessageUtils.translateMessage(modeTitle, playerLang);
            runTitle = MessageUtils.translateMessage(runTitle, playerLang);
            runWhitelist = MessageUtils.translateMessage(runWhitelist, playerLang);
            runBlacklist = MessageUtils.translateMessage(runBlacklist, playerLang);
            runCurrentMode = MessageUtils.translateMessage(runCurrentMode, playerLang);
            runChangeMode = MessageUtils.translateMessage(runChangeMode, playerLang);
        }
        MessageUtils.setCustomItemData(random, theme, randomTitle, null);
        MessageUtils.setCustomItemData(presets, theme, presetsTitle, null);
        MessageUtils.setCustomItemData(run, theme, runTitle, Arrays.asList(runWhitelist, runBlacklist, new Message(), runCurrentMode, runChangeMode));
        MessageUtils.setCustomItemData(clearSelected, theme, modeTitle, null);
        FancyClickEvent.addCommand(random, ClickType.LEFT, "tport biomeTP random");
        FancyClickEvent.addFunction(presets, ClickType.LEFT,
                "biomeTP_openPresets", ((whoClicked, clickType, pdc, fancyInventory) -> openBiomeTPPreset(whoClicked, 0, fancyInventory)));
        
        if (!biomeSelection.isEmpty()) {
            FancyClickEvent.addCommand(run, ClickType.LEFT, "tport biomeTP whitelist " + String.join(" ", biomeSelection));
            FancyClickEvent.addCommand(run, ClickType.RIGHT, "tport biomeTP blacklist " + String.join(" ", biomeSelection));
        }
        FancyClickEvent.addCommand(run, ClickType.SHIFT_RIGHT, "tport biomeTP mode " + biomeTPMode.getNext().name(), "tport biomeTP");
        
        FancyClickEvent.addFunction(clearSelected, ClickType.LEFT, "biomeTP_clearSelected", (whoClicked, clickType, pdc, fancyInventory) -> {
            sendSuccessTranslation(whoClicked, "tport.events.inventoryClick.onInventoryClick.clearSelectedBiomes");
            openBiomeTP(whoClicked, fancyInventory.getData("page", Integer.class, 0), null);
        });
        
        list.add(0, random);
        
        FancyInventory inv = getDynamicScrollableInventory(player, page, TPortInventories::openBiomeTP, "tport.tportInventories.BIOME_TP.title", list, createBack(player, BackType.MAIN, BackType.OWN, BackType.FEATURE_TP, BackType.BIOME_TP), 45);
        inv.setData("biomeSelection", biomeSelection);
        inv.setItem(9, clearSelected);
        inv.setItem(27, presets);
        inv.setItem(18, run);
        
        inv.open(player);
    }
    public static void openBiomeTPPreset(Player player, int page, @Nullable FancyInventory prevWindow) {
        FancyInventory inv = getDynamicScrollableInventory(player, page, TPortInventories::openBiomeTPPreset, "tport.tportInventories.BIOME_TP_PRESETS.title",
                BiomeTP.BiomeTPPresets.getItems(player), createBack(player, BackType.BIOME_TP, BackType.OWN, BackType.MAIN));
        if (prevWindow != null) inv.setData("biomeSelection", prevWindow.getData("biomeSelection", ArrayList.class, new ArrayList<String>()));
        inv.open(player);
    }
    
    public static void openFeatureTP(Player player) {
        openFeatureTP(player, 0, null);
    }
    public static void openFeatureTP(Player player, int page, @Nullable FancyInventory prevWindow) {
        ColorTheme theme = ColorTheme.getTheme(player);
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        
        ArrayList<String> featureSelection;
        if (prevWindow != null) {
            featureSelection = prevWindow.getData("featureSelection", ArrayList.class, new ArrayList<String>());
        } else {
            featureSelection = new ArrayList<>();
        }
        
        FancyInventory inv = getDynamicScrollableInventory(player, page, TPortInventories::openFeatureTP, "tport.tportInventories.FEATURE_TP.title",
                FeatureTP.getItems(player, featureSelection), createBack(player, BackType.MAIN, BackType.OWN, BackType.PUBLIC, BackType.FEATURE_TP));
        inv.setData("featureSelection", featureSelection);
        
        ItemStack run = new ItemStack(Material.OAK_BUTTON);
        Mode.WorldSearchMode featureTPMode = Mode.getDefMode(player.getUniqueId());
        Message runTitle = formatTranslation(titleColor, titleColor, "tport.tportInventories.openFeatureTP.run.title");
        Message runMode1 = formatInfoTranslation("tport.tportInventories.openFeatureTP.run.mode", ClickType.LEFT, featureTPMode);
        Message runMode2 = formatInfoTranslation("tport.tportInventories.openFeatureTP.run.mode", ClickType.RIGHT, featureTPMode.getNext());
        Message runCurrentMode = formatInfoTranslation("tport.tportInventories.openFeatureTP.run.currentMode", featureTPMode);
        Message runChangeMode = formatInfoTranslation("tport.tportInventories.openFeatureTP.run.changeMode", ClickType.SHIFT_RIGHT, featureTPMode.getNext());
        
        ItemStack clearSelected = new ItemStack(Material.OAK_BUTTON);
        Message clearSelectedTitle = formatInfoTranslation("tport.tportInventories.openFeatureTP.clearSelected.title", ClickType.LEFT);
        
        if (playerLang != null) { //if player has no custom language, translate it
            runTitle = MessageUtils.translateMessage(runTitle, playerLang);
            clearSelectedTitle = MessageUtils.translateMessage(clearSelectedTitle, playerLang);
            runMode1 = MessageUtils.translateMessage(runMode1, playerLang);
            runMode2 = MessageUtils.translateMessage(runMode2, playerLang);
            runCurrentMode = MessageUtils.translateMessage(runCurrentMode, playerLang);
            runChangeMode = MessageUtils.translateMessage(runChangeMode, playerLang);
        }
        MessageUtils.setCustomItemData(run, theme, runTitle, Arrays.asList(runMode1, runMode2, new Message(), runCurrentMode, runChangeMode));
        MessageUtils.setCustomItemData(clearSelected, theme, clearSelectedTitle, null);
        
        if (!featureSelection.isEmpty()) {
            FancyClickEvent.addCommand(run, ClickType.LEFT, "tport featureTP search " + featureTPMode + " " + String.join(" ", featureSelection));
            FancyClickEvent.addCommand(run, ClickType.RIGHT, "tport featureTP search " + featureTPMode.getNext() + " " + String.join(" ", featureSelection));
        }
        FancyClickEvent.addCommand(run, ClickType.SHIFT_RIGHT, "tport featureTP mode " + featureTPMode.getNext().name(), "tport featureTP");
        
        FancyClickEvent.addFunction(clearSelected, ClickType.LEFT, "featureTP_clearSelected", ((whoClicked, clickType, pdc, fancyInventory) -> {
            sendSuccessTranslation(whoClicked, "tport.tportInventories.openFeatureTP.clearSelected.succeeded");
            openFeatureTP(whoClicked, fancyInventory.getData("page", Integer.class, 0), null);
        }));
        
        inv.setItem(9, clearSelected);
        inv.setItem(18, run);
        inv.open(player);
    }
    
    public static void openPublicTPortGUI(Player player) {
        openPublicTPortGUI(player, 0, null);
    }
    public static void openPublicTPortGUI(Player player, int page, @Nullable FancyInventory prevWindow) {
        //public.tports.<publicTPortSlot>.<TPortID>
        
        List<ItemStack> list = new ArrayList<>();
        
        for (String publicTPortSlot : tportData.getKeys("public.tports")) {
            String tportID = tportData.getConfig().getString("public.tports." + publicTPortSlot, TPortManager.defUUID.toString());
            
            TPort tport = getTPort(UUID.fromString(tportID));
            if (tport != null) {
                list.add(toPublicTPortItem(tport, player, prevWindow));
                if (tport.setPublicTPort(true)) {
                    tport.save();
                }
            } else {
                int publicSlotTmp = Integer.parseInt(publicTPortSlot) + 1;
                String tportID2 = tportData.getConfig().getString("public.tports." + publicSlotTmp, TPortManager.defUUID.toString());
                
                TPort tport2 = getTPort(UUID.fromString(tportID2));
                if (tport2 != null) {
                    list.add(toPublicTPortItem(tport2, player, prevWindow));
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
        
        FancyInventory inv = getDynamicScrollableInventory(player, page, TPortInventories::openPublicTPortGUI, "tport.tportInventories.PUBLIC.title", list, createBack(player, BackType.MAIN, BackType.OWN, BackType.BIOME_TP, BackType.PUBLIC));
        if (prevWindow != null) inv.setData("TPortToMove", prevWindow.getData("TPortToMove", UUID.class));
        inv.open(player);
    }
    
    public static void openSearchGUI(Player player, int page, FancyInventory fancyInventory) {
        openSearchGUI(player, page,
                fancyInventory.getData("content", List.class),
                fancyInventory.getData("query", String.class),
                fancyInventory.getData("searcher", String.class),
                fancyInventory.getData("searchMode", Search.SearchMode.class),
                false);
    }
    public static void openSearchGUI(Player player, int page, Search.SearchMode searchMode, String searcherName, @Nonnull String query) {
        Search.Searchers.Searcher searcher = Search.Searchers.getSearcher(searcherName);
        if (searcher == null) {
            sendErrorTranslation(player, "tport.tportInventories.openSearchGUI.searchData.couldNotFindSearcher", searcherName);
            return;
        }
        
        openSearchGUI(player, page, searcher.search(searchMode, query, player), query, searcherName, searchMode, true);
    }
    private static void openSearchGUI(Player player, int page, List<ItemStack> searched, String query, String searcher, Search.SearchMode searchMode, boolean updateCooldown) {
        FancyInventory inv = getDynamicScrollableInventory(player, page, TPortInventories::openSearchGUI, "tport.tportInventories.SEARCH.title",
                searched, createBack(player, BackType.MAIN, BackType.OWN, BackType.PUBLIC));
        
        inv.setData("content", searched);
        inv.setData("query", query);
        inv.setData("searcher", searcher);
        inv.setData("searchMode", searchMode);
        
        ColorTheme theme = ColorTheme.getTheme(player);
        
        ItemStack searchData = new ItemStack(Material.OAK_BUTTON);
        
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        Message title = formatTranslation(titleColor, titleColor, "tport.tportInventories.openSearchGUI.searchData.title");
        Message searchTypeLore = formatInfoTranslation("tport.tportInventories.openSearchGUI.searchData.type", searcher);
        Message searchQueryLore = query.isEmpty() ? null : formatInfoTranslation("tport.tportInventories.openSearchGUI.searchData.query", query);
        Message searchModeLore = searchMode == null ? null : formatInfoTranslation("tport.tportInventories.openSearchGUI.searchData.mode", searchMode);
        if (playerLang != null) { //if player has no custom language, translate it
            title = MessageUtils.translateMessage(title, playerLang);
            searchTypeLore = MessageUtils.translateMessage(searchTypeLore, playerLang);
            if (!query.isEmpty()) searchQueryLore = MessageUtils.translateMessage(searchQueryLore, playerLang);
            if (searchMode != null) searchModeLore = MessageUtils.translateMessage(searchModeLore, playerLang);
        }
        MessageUtils.setCustomItemData(searchData, theme, title, Arrays.asList(searchTypeLore, searchQueryLore, searchModeLore));
        
        inv.setItem(0, searchData);
        
        inv.open(player);
        if (updateCooldown) CooldownManager.Search.update(player);
    }
    
    private enum BackType {
        MAIN("tport"),
        OWN("tport own"),
        PUBLIC("tport public"),
        BIOME_TP("tport biomeTP"),
        FEATURE_TP("tport featureTP");
        
        private final String command;
        
        BackType(String command) {
            this.command = command;
        }
        
        public Message getName() {
            return formatTranslation(varInfoColor, varInfoColor, "tport.tportInventories.backType." + name() + ".description");
        }
        
        public String getCommand() {
            return command;
        }
    }
    
    public enum QuickEditType {
        PRIVATE("Private State", (tport, player) -> {
            TPortCommand.executeInternal(player, new String[]{"edit", tport.getName(), "private", tport.getPrivateState().getNext().name()});
        }),
        WHITELIST_VISIBILITY("Whitelist Visibility", (tport, player) -> {
            TPortCommand.executeInternal(player, new String[]{"edit", tport.getName(), "whitelist", "visibility", tport.getWhitelistVisibility().getNext().name()});
        }),
        RANGE("Range", (tport, player) -> {
            int range = tport.getRange();
            for (int r : Arrays.asList(50, 100, 250, 500)) {
                if (r > range) {
                    TPortCommand.executeInternal(player, new String[]{"edit", tport.getName(), "range", String.valueOf(r)});
                    return;
                }
            }
            TPortCommand.executeInternal(player, new String[]{"edit", tport.getName(), "range", "0"});
        }),
        MOVE("Move", (tport, player) -> {
            UUID otherTPortID = quickEditMoveList.get(player.getUniqueId());
            if (otherTPortID == null) {
                quickEditMoveList.put(player.getUniqueId(), tport.getTportID());
            } else {
                quickEditMoveList.remove(player.getUniqueId());
                if (!otherTPortID.equals(tport.getTportID())) {
                    TPortCommand.executeInternal(player, new String[]{"edit", TPortManager.getTPort(player.getUniqueId(), otherTPortID).getName(), "move", tport.getName()});
                }
            }
        }),
        LOG("Log", (tport, player) -> {
            TPortCommand.executeInternal(player, new String[]{"log", "default", tport.getName(), tport.getDefaultLogMode().getNext().name()});
        }),
        NOTIFY("Notify", (tport, player) -> {
            TPortCommand.executeInternal(player, new String[]{"log", "notify", tport.getName(), tport.getNotifyMode().getNext().name()});
        }),
        PREVIEW("Preview State", (tport, player) -> {
            if (Features.Feature.Preview.isEnabled()) {
                TPortCommand.executeInternal(player, new String[]{"edit", tport.getName(), "preview", tport.getPreviewState().getNext().name()});
            }
        }),
        DYNMAP_SHOW("Dynmap Show", (tport, player) -> {
            if (DynmapHandler.isEnabled()) {
                TPortCommand.executeInternal(player, new String[]{"edit", tport.getName(), "dynmap", "show", String.valueOf(!tport.showOnDynmap())});
            } else {
                DynmapCommand.sendDisableError(player);
            }
        }),
        DYNMAP_ICON("Dynmap Icon", (tport, player) -> {
            if (DynmapHandler.isEnabled()) {
                String iconName = DynmapHandler.getTPortIconName(tport);
                List<Pair<String, String>> list = DynmapHandler.getIcons();
                if (list != null) {
                    for (Iterator<Pair<String, String>> iterator = list.iterator(); iterator.hasNext(); ) {
                        Pair<String, String> icon = iterator.next();
                        if (icon.getRight().equalsIgnoreCase(iconName)) {
                            if (iterator.hasNext()) {
                                TPortCommand.executeInternal(player, new String[]{"edit", tport.getName(), "dynmap", "icon", iterator.next().getRight()});
                            } else {
                                TPortCommand.executeInternal(player, new String[]{"edit", tport.getName(), "dynmap", "icon", list.get(0).getRight()});
                            }
                        }
                    }
                }
            } else {
                DynmapCommand.sendDisableError(player);
            }
        });
        
        private final Run editor;
        private final String displayName;
        
        public static HashMap<UUID, QuickEditType> map = new HashMap<>();
        
        QuickEditType(String displayName, Run run) {
            this.editor = run;
            this.displayName = displayName;
        }
        
        public static void clearData(UUID player) {
            quickEditMoveList.remove(player);
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
                QuickEditType type = QuickEditType.valueOf(name.toUpperCase());
                if (type == PREVIEW && !Features.Feature.Preview.isEnabled()) {
                    type = type.getNext();
                }
                if ((type == DYNMAP_ICON || type == DYNMAP_SHOW) && !DynmapHandler.isEnabled()) {
                    type = type.getNext();
                }
                return type;
            } catch (IllegalArgumentException iae) {
                return PRIVATE;
            }
        }
        
        public void edit(TPort tport, Player player) {
            this.editor.edit(tport, player);
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
        
        @FunctionalInterface
        private interface Run {
            void edit(TPort tport, Player player);
        }
    }
}
