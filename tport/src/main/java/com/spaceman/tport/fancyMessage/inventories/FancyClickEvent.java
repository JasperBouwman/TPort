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

import static org.bukkit.persistence.PersistentDataType.STRING;

public class FancyClickEvent implements Listener {
    
    public static void setStringData(ItemStack item, NamespacedKey key, String value) {
        ItemMeta im = item.getItemMeta();
        im.getPersistentDataContainer().set(key, STRING, value);
        item.setItemMeta(im);
    }
    
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
    public static ItemStack addFunction(ItemStack is, FancyClickRunnable runnable, ClickType... clickTypes) {
        for (ClickType clickType : clickTypes) {
            addFunction(is, clickType, runnable);
        }
        return is;
    }
    public static ItemStack addFunction(ItemStack is, ClickType clickType, FancyClickRunnable runnable) {
        ItemMeta im = is.getItemMeta();
        if (im != null) {
            is.setItemMeta(addFunction(im, clickType, runnable));
        }
        return is;
    }
    public static ItemMeta addFunction(ItemMeta im, ClickType clickType, FancyClickRunnable runnable) {
        if (im == null) return null;
        String funcName = runnable.toString();
        
        im.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), "runFunc_" + clickType.name()), PersistentDataType.STRING, funcName);
        functionsMap.putIfAbsent(funcName, runnable);
        return im;
    }
    public static ItemMeta removeFunction(ItemMeta im, ClickType clickType) {
        if (im == null) return null;
        im.getPersistentDataContainer().remove(new NamespacedKey(Main.getInstance(), "runFunc_" + clickType.name()));
        return im;
    }
    public static ItemMeta removeCommand(ItemMeta im, ClickType clickType) {
        if (im == null) return null;
        im.getPersistentDataContainer().remove(new NamespacedKey(Main.getInstance(), "runCommand_" + clickType.name()));
        return im;
    }
    public static String getFunctionName(ItemStack is, ClickType clickType) {
        ItemMeta im = is.getItemMeta();
        if (im != null) {
            return getFunctionName(im, clickType);
        }
        return null;
    }
    public static String getFunctionName(ItemMeta im, ClickType clickType) {
        return im.getPersistentDataContainer().get(new NamespacedKey(Main.getInstance(), "runFunc_" + clickType.name()), PersistentDataType.STRING);
    }
    @Nullable
    public static FancyClickRunnable getFunction(String funcName) {
        return functionsMap.get(funcName);
    }
    public static ItemMeta removeAllFunctions(ItemMeta im) {
        if (im == null) return null;
        Arrays.stream(ClickType.values()).forEach(clickType -> removeFunction(im, clickType));
        return im;
    }
    public static ItemMeta removeAllCommands(ItemMeta im) {
        if (im == null) return null;
        Arrays.stream(ClickType.values()).forEach(clickType -> removeCommand(im, clickType));
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
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        
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
