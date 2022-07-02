package com.spaceman.tport.fancyMessage.inventories;

import com.spaceman.tport.Main;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class FancyClickEvent implements Listener {
    
    public static ItemStack addCommand(ItemStack is, ClickType clickType, String command) {
        ItemMeta im = is.getItemMeta();
        if (im != null) {
            is.setItemMeta(addCommand(im, clickType, command, null));
        }
        return is;
    }
    public static ItemMeta addCommand(@Nonnull ItemMeta im, ClickType clickType, String command) {
        return addCommand(im, clickType, command, null);
    }
    public static ItemStack addCommand(ItemStack is, ClickType clickType, String command, @Nullable String secondary) {
        ItemMeta im = is.getItemMeta();
        if (im != null) {
            is.setItemMeta(addCommand(im, clickType, command, secondary));
        }
        return is;
    }
    public static ItemMeta addCommand(@Nonnull ItemMeta im, ClickType clickType, String command, @Nullable String secondary) {
        im.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "runCommand_" + clickType.name()), PersistentDataType.STRING, command);
        if (!StringUtils.isEmpty(secondary))
            im.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "runCommand_" + clickType.name() + "_secondary"), PersistentDataType.STRING, secondary);
        return im;
    }
    
    private static final HashMap<String, FancyClickRunnable> functionsMap = new HashMap<>();
    public static ItemStack addFunction(ItemStack is, ClickType clickType, @Nullable String funcName, FancyClickRunnable runnable) {
        ItemMeta im = is.getItemMeta();
        if (im != null) {
            is.setItemMeta(addFunction(im, clickType, funcName, runnable));
        }
        return is;
    }
    public static ItemMeta addFunction(ItemMeta im, ClickType clickType, @Nullable String funcName, FancyClickRunnable runnable) {
        if (im == null) return null;
        if (funcName == null) funcName = runnable.toString();
        
        im.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "runFunc_" + clickType.name()), PersistentDataType.STRING, funcName);
        functionsMap.putIfAbsent(funcName, runnable);
        
        return im;
    }
    public static ItemMeta removeFunction(ItemMeta im, ClickType clickType) {
        if (im == null) return null;
        im.getPersistentDataContainer().remove(new NamespacedKey(Main.getInstance(), "runFuncSer_" + clickType.name()));
        return im;
    }
    public static ItemMeta removeAllFunctions(ItemMeta im) {
        if (im == null) return null;
        Arrays.stream(ClickType.values()).forEach(clickType -> removeFunction(im, clickType));
        return im;
    }
    @FunctionalInterface
    public interface FancyClickRunnable {
        void run(Player whoClicked, ClickType clickType, PersistentDataContainer pdc, FancyInventory fancyInventory);
    }
    
    @EventHandler
    @SuppressWarnings("unused")
    public void onFancyClose(InventoryCloseEvent e) {
        if (!(e.getInventory().getHolder() instanceof FancyInventory fancyInventory)) {
            return;
        }
    
        List<ItemStack> originalContent = Arrays.asList(fancyInventory.getData("originalContent", ItemStack[].class, new ItemStack[0]));
        for (ItemStack item : e.getInventory().getContents()) {
            if (!originalContent.contains(item)) {
                Main.giveItems((Player) e.getPlayer(), item);
            }
        }
    }
    
    @EventHandler
    @SuppressWarnings("unused")
    public void onFancyClick(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof FancyInventory fancyInventory)) {
            return;
        }
        
        Player player = (Player) e.getWhoClicked();
        ClickType clickType = e.getClick();
        
        if (e.getRawSlot() > e.getInventory().getSize()) {
            return;
        }
        
        ItemStack item = e.getCurrentItem();
        if (item == null) {
            return;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        
        e.setCancelled(true);
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        
        NamespacedKey runCommandKey = new NamespacedKey(Main.getInstance(), "runCommand_" + clickType.name());
        if (pdc.has(runCommandKey, PersistentDataType.STRING)) {
            Bukkit.dispatchCommand(player, pdc.getOrDefault(runCommandKey, PersistentDataType.STRING, ""));
            
            runCommandKey = new NamespacedKey(Main.getInstance(), "runCommand_" + clickType.name() + "_secondary");
            if (pdc.has(runCommandKey, PersistentDataType.STRING)) {
                Bukkit.dispatchCommand(player, pdc.getOrDefault(runCommandKey, PersistentDataType.STRING, ""));
            }
        }
        
        NamespacedKey runFuncKey = new NamespacedKey(Main.getInstance(), "runFunc_" + clickType.name());
        if (pdc.has(runFuncKey, PersistentDataType.STRING)) {
            functionsMap.getOrDefault(
                    pdc.getOrDefault(runFuncKey, PersistentDataType.STRING, "null"),
                    (whoClicked, clickType1, pdc1, fancyInventory1) -> {}
            ).run(player, clickType, pdc, fancyInventory);
        }
    }
}