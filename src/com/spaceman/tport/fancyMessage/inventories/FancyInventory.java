package com.spaceman.tport.fancyMessage.inventories;

import com.google.gson.JsonObject;
import com.spaceman.tport.commands.tport.ResourcePack;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.MessageUtils;
import com.spaceman.tport.fancyMessage.colorTheme.ColorTheme;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.network.protocol.game.PacketPlayOutOpenWindow;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.Containers;
import org.apache.http.annotation.Contract;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.titleColor;
import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.tport.fancyMessage.language.Language.getPlayerLang;

public class FancyInventory implements InventoryHolder {
    
    public static final int nextModelData = 5145462;
    public static final int prevModelData = 5145463;
    
    private final HashMap<String, Object> holderData;
    
    private Inventory inventory;
    private Message title;
    
    public FancyInventory(int size, Message title) {
        if (9 > size || size > 54 || size % 9 != 0) {
            size = 54;
        }
        this.inventory = Bukkit.createInventory(this, size, title.toString());
        this.title = title;
        holderData = new HashMap<>();
    }
    
    public void setData(String dataName, Object data) {
        holderData.put(dataName, data);
    }
    public Object getData(String dataName) {
        return holderData.getOrDefault(dataName,null);
    }
    public <T> T getData(String dataName, Class<T> clazz) {
        return getData(dataName, clazz, null);
    }
    public <T> T getData(String dataName, Class<T> clazz, T def) {
        if (holderData.containsKey(dataName)) {
            Object data = holderData.get(dataName);
            if (clazz.isInstance(data)) {
                return (T) data;
            }
            return null;
        } else {
            return def;
        }
    }
    
    public int getSize() {
        return inventory.getSize();
    }
    
    public void setItem(int index, ItemStack item) {
        this.inventory.setItem(index, item);
    }
    public ItemStack getItem(int index) {
        return this.inventory.getItem(index);
    }
    public HashMap<Integer, ItemStack> addItem(ItemStack... itemStack) {
        return this.inventory.addItem(itemStack);
    }
    
    public void ensureOriginalContent() {
        this.setData("originalContent", inventory.getContents());
    }
    
    public void open(Player player) {
        ensureOriginalContent();
        try {
            JsonObject playerLang = getPlayerLang(player.getUniqueId());
            if (playerLang != null) { //if player has no custom language, translate it
                title = MessageUtils.translateMessage(title, playerLang);
            }
            IChatMutableComponent chatSerializer = IChatBaseComponent.ChatSerializer.a(title.translateJSON(player));
            EntityPlayer entityPlayer = (EntityPlayer) player.getClass().getMethod("getHandle").invoke(player);
            
            String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
//            Container c = new CraftContainer(inventory, entityPlayer, entityPlayer.nextContainerCounter());
            Class<?> craftContainer = Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftContainer");
            Container container = (Container) craftContainer
                    .getConstructor(Class.forName("org.bukkit.inventory.Inventory"), entityPlayer.getClass().getSuperclass(), int.class)
                    .newInstance(inventory, entityPlayer, entityPlayer.nextContainerCounter());
            
//            Containers<?> windowType = CraftContainer.getNotchInventoryType(inventory);
            Containers<?> windowType = (Containers<?>) craftContainer.getMethod("getNotchInventoryType", Inventory.class).invoke(null, inventory);
            entityPlayer.b.a(new PacketPlayOutOpenWindow(container.j, windowType, chatSerializer));
            entityPlayer.bP = container;
            entityPlayer.a(container);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException | InstantiationException e) {
            e.printStackTrace();
            player.openInventory(inventory);
        }
    }
    
    public void setTitle(Message title) {
        this.title = title;
    }
    public Message getTitle() {
        return title;
    }
    
    public FancyInventory setInventory(Inventory inventory) {
        this.inventory = inventory;
        return this;
    }
    
    /**
     * It's not recommended to use this inventory to open for the player.
     * Use {@link FancyInventory#open(Player)}, this stores the original inventory contents in this holder.
     * The reason for this is that in the {@link FancyClickEvent#onFancyClick(InventoryClickEvent)} it checks if the clicked item comes from the opened inventory or is added by the player,
     * the player can add items that they can make themselves with potentially malicious code written in it.
     */
    @Nonnull
    @Override
    public Inventory getInventory() {
        return inventory;
    }
    
    @FunctionalInterface
    public interface InventoryCreator {
        void openInventory(Player player, int page, FancyInventory previousWindow);
    }
    public static void openDynamicScrollableInventory(Player player, int page, InventoryCreator invCreator, String titleID, List<ItemStack> items, @Nullable ItemStack backButton) {
        getDynamicScrollableInventory(player, page, invCreator, titleID, items, backButton).open(player);
    }
    public static FancyInventory getDynamicScrollableInventory(Player player, int page, InventoryCreator invCreator, String titleID, List<ItemStack> items, @Nullable ItemStack backButton) {
        return getDynamicScrollableInventory(player, page, invCreator, titleID, items, backButton, 27);
    }
    public static FancyInventory getDynamicScrollableInventory(Player player, int page, InventoryCreator invCreator, String titleID, List<ItemStack> items, @Nullable ItemStack backButton, int minSize) {
        int rows = 3; //amount of rows
        int width = 7; //amount of items in a row
        int skipPerPage = 7; //amount of items skipped per page ( width * (rows to skip) )
        
        int maxPages = 1;
        int size = items.size();
        //add max to GUI page
        if (size > rows * width) {
            maxPages = size / width - rows + (size % width == 0 ? 0 : 1);
            page = Math.min(maxPages, page);
        } else {
            page = 0;
        }
        page = Math.max(0, page); //set min to page
        
        size += (width - (size - 1) % width); //calculate square rectangle items (add remaining empties)
        size /= width; //get amount of rows
        if (size > rows) {
            size -= (size - rows); //set a max on 3 rows
        }
        size *= 9; //turn rows into slots
        size += 18; //add top and bottom row
        size = Math.max(size, minSize);
        
        Message title = formatInfoTranslation(titleID);
        if (maxPages != 1) {
            title.addMessage(formatInfoTranslation("tport.fancyMessage.inventories.fancyInventory.getDynamicScrollableInventory.page", page + 1, maxPages + 1));
        }
        FancyInventory inv = new FancyInventory(size, title);
        inv.setData("page", page);
        inv.setData("totalPages", items.size() / width - rows + (items.size() % width == 0 ? 0 : 1));
        inv.setData("invCreator", invCreator);
        
        int startIndex = page * skipPerPage; //amount to skip
        
        int slot = 9 + (9 - width) / 2;
        for (int index = startIndex; index < startIndex + width * rows && index < items.size(); index++) {
            if ((slot + 1) % 9 == 0) { //end of row, +2 to go to next row
                slot += (9 - width);
            }
            if (slot >= size - 9) { //end of items
                break;
            }
            inv.setItem(slot, items.get(index)); //add item
            slot++; //next slot
        }
        
        if (backButton != null) {
            inv.setItem(size / 18 * 9 + 8, backButton);
        }
        
        ColorTheme theme = getTheme(player);
        JsonObject playerLang = getPlayerLang(player.getUniqueId());
        
        if ((page + rows) * width < items.size()) { //if not all items could be displayed, add next 'button'
            ItemStack is = new ItemStack(Material.HOPPER);
            
            Message nextTitle = formatTranslation(titleColor, titleColor, "tport.fancyMessage.inventories.fancyInventory.getDynamicScrollableInventory.next.title");
            Message next1 = formatInfoTranslation("tport.fancyMessage.inventories.fancyInventory.getDynamicScrollableInventory.next.+1", ClickType.LEFT, "+1");
            Message nextRow = formatInfoTranslation("tport.fancyMessage.inventories.fancyInventory.getDynamicScrollableInventory.next.+row", ClickType.RIGHT, "+" + rows);
            Message nextEnd = formatInfoTranslation("tport.fancyMessage.inventories.fancyInventory.getDynamicScrollableInventory.next.end", ClickType.SHIFT_RIGHT);
            
            if (playerLang != null) {
                nextTitle = MessageUtils.translateMessage(nextTitle, playerLang);
                next1 = MessageUtils.translateMessage(next1, playerLang);
                nextRow = MessageUtils.translateMessage(nextRow, playerLang);
                nextEnd = MessageUtils.translateMessage(nextEnd, playerLang);
            }
            
            MessageUtils.setCustomItemData(is, theme, nextTitle, Arrays.asList(next1, nextRow, nextEnd));
            
            ItemMeta im = is.getItemMeta();
            FancyClickEvent.addFunction(im, ClickType.LEFT, null, ((whoClicked, clickType, pdc, fancyInventory) ->
                    fancyInventory.getData("invCreator", InventoryCreator.class).openInventory(whoClicked, fancyInventory.getData("page", Integer.class, 0) +1, fancyInventory)));
            FancyClickEvent.addFunction(im, ClickType.RIGHT, null, ((whoClicked, clickType, pdc, fancyInventory) ->
                    fancyInventory.getData("invCreator", InventoryCreator.class).openInventory(whoClicked, fancyInventory.getData("page", Integer.class, 0) + rows, fancyInventory)));
            FancyClickEvent.addFunction(im, ClickType.SHIFT_RIGHT, null, ((whoClicked, clickType, pdc, fancyInventory) ->
                    fancyInventory.getData("invCreator", InventoryCreator.class).openInventory(whoClicked, fancyInventory.getData("totalPages", Integer.class, 0), fancyInventory)));
            is.setItemMeta(im);
            ResourcePack.applyModelData(is, nextModelData, player.getUniqueId());
            
            inv.setItem(size - 1, is);
        }
        if (page != 0) { //if not at page 0 (1 as display) add previous 'button'
            ItemStack is = new ItemStack(Material.FERN);
            
            Message previousTitle = formatTranslation(titleColor, titleColor, "tport.fancyMessage.inventories.fancyInventory.getDynamicScrollableInventory.previous.title");
            Message previous1 = formatInfoTranslation("tport.fancyMessage.inventories.fancyInventory.getDynamicScrollableInventory.previous.-1", ClickType.LEFT, "-1");
            Message previousRow = formatInfoTranslation("tport.fancyMessage.inventories.fancyInventory.getDynamicScrollableInventory.previous.-row", ClickType.RIGHT, "-" + rows);
            Message previousBegin = formatInfoTranslation("tport.fancyMessage.inventories.fancyInventory.getDynamicScrollableInventory.previous.begin", ClickType.SHIFT_RIGHT);
            
            if (playerLang != null) {
                previousTitle = MessageUtils.translateMessage(previousTitle, playerLang);
                previous1 = MessageUtils.translateMessage(previous1, playerLang);
                previousRow = MessageUtils.translateMessage(previousRow, playerLang);
                previousBegin = MessageUtils.translateMessage(previousBegin, playerLang);
            }
            
            MessageUtils.setCustomItemData(is, theme, previousTitle, Arrays.asList(previous1, previousRow, previousBegin));
            
            ItemMeta im = is.getItemMeta();
            FancyClickEvent.addFunction(im, ClickType.LEFT, null, ((whoClicked, clickType, pdc, fancyInventory) ->
                    fancyInventory.getData("invCreator", InventoryCreator.class).openInventory(whoClicked, fancyInventory.getData("page", Integer.class, 1) -1, fancyInventory)));
            FancyClickEvent.addFunction(im, ClickType.RIGHT, null, ((whoClicked, clickType, pdc, fancyInventory) ->
                    fancyInventory.getData("invCreator", InventoryCreator.class).openInventory(whoClicked, fancyInventory.getData("page", Integer.class, 1) - rows, fancyInventory)));
            FancyClickEvent.addFunction(im, ClickType.SHIFT_RIGHT, null, ((whoClicked, clickType, pdc, fancyInventory) ->
                    fancyInventory.getData("invCreator", InventoryCreator.class).openInventory(whoClicked, 0, fancyInventory)));
            is.setItemMeta(im);
            ResourcePack.applyModelData(is, prevModelData, player.getUniqueId());
            
            inv.setItem(8, is);
        }
        return inv;
    }
}
