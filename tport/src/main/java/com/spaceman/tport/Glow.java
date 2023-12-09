package com.spaceman.tport;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Glow {
    
    public static ItemStack addGlow(ItemStack item) {
        ItemMeta im = item.getItemMeta();
        if (im != null) {
            addGlow(im);
            item.setItemMeta(im);
        }
        return item;
    }
    
    public static void addGlow(ItemMeta meta) {
        if (!meta.hasEnchants()) {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addEnchant(Enchantment.MENDING, 1, true);
        }
    }
}
